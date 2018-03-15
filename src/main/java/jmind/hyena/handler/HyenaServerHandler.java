package jmind.hyena.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jmind.base.util.AddrUtil;
import jmind.hyena.bootstrap.HyenaServer;
import jmind.hyena.frame.Dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by xieweibo on 2016/11/28.
 */
public class HyenaServerHandler extends ChannelInboundHandlerAdapter {

    static final Logger logger = LoggerFactory.getLogger(HyenaServerHandler.class);



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Dispatcher.getInstance().dispatch((HyenaCommand) msg, ctx.channel());
    }




    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        HyenaServer.allChannels.put(AddrUtil.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()), ctx.channel());


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        HyenaServer.allChannels.remove(AddrUtil.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        logger.error("exception:" + ctx.channel() + " " + cause.getMessage(),cause);

    }
}
