package jmind.hyena.handler;

import jmind.hyena.bootstrap.HyenaServer;
import jmind.hyena.frame.Dispatcher;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xieweibo on 2016/11/28.
 */
public class HyenaServerHandler extends SimpleChannelUpstreamHandler {

    static final Logger logger = LoggerFactory.getLogger(HyenaServerHandler.class);



    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        Dispatcher.getInstance().dispatch((HyenaCommand) e.getMessage(), e.getChannel());
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        logger.debug(ctx.getChannel().getId()+"channel added:" + e.getChannel().getId());
        HyenaServer.allChannels.add(e.getChannel());
       // ClientSession.createClientSession(ctx.getChannel());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx,
                                    ChannelStateEvent e) throws Exception {
        logger.debug(ctx.getChannel().getId() + " disconnected");
        HyenaServer.allChannels.remove(e.getChannel());
      //  ClientSession.destroy(ctx.getChannel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        logger.error("exception:" + e.getChannel() + " " + e.getCause().getMessage());

    }
}
