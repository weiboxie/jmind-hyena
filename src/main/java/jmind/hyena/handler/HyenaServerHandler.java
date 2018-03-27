package jmind.hyena.handler;

import io.netty.channel.*;
import jmind.base.util.AddrUtil;
import jmind.hyena.bootstrap.HyenaServer;
import jmind.hyena.frame.Dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by xieweibo on 2016/11/28.
 */
public class HyenaServerHandler extends SimpleChannelInboundHandler<HyenaCommand> {

    static final Logger logger = LoggerFactory.getLogger(HyenaServerHandler.class);





    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HyenaCommand msg) throws Exception {
        Dispatcher.getInstance().dispatch(msg, ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        HyenaServer.allChannels.put(AddrUtil.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()), ctx.channel());


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        ctx.close();
        HyenaServer.allChannels.remove(AddrUtil.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.fireExceptionCaught(cause);
        logger.error("exception:" + ctx.channel() + " " + cause.getMessage(),cause);

    }
}
