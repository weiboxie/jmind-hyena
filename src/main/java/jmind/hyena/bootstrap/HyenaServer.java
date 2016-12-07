package jmind.hyena.bootstrap;

import jmind.core.util.DataUtil;
import jmind.hyena.handler.HyenaServerPipelineFactory;
import jmind.hyena.server.Daemon;
import jmind.hyena.util.HyenaUtil;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.Executors;

/**
 * Created by xieweibo on 2016/11/29.
 */
public class HyenaServer {

    public static final ChannelGroup allChannels = new DefaultChannelGroup("hyena-server");

    Logger logger= LoggerFactory.getLogger(getClass());


    /** start server */
    public void run(int port) {
        //1.start  server
        final ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2),
                        Executors.newCachedThreadPool()));

        final ChannelPipelineFactory channelPipelineFactory = new HyenaServerPipelineFactory();
        bootstrap.setPipelineFactory(channelPipelineFactory);

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        Channel channel = bootstrap.bind(new InetSocketAddress(port));
        logger.info("Hynea server started, listenning at " + port);

        //when shutdown release all resource
        allChannels.add(channel);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("Hynea release channel resource before exit");
                ChannelGroupFuture future = allChannels.close();
                future.awaitUninterruptibly(10000);// 10 sec for time out
                bootstrap.releaseExternalResources();
            }
        });

        //run daemon
         daemon();



    }

    /** run daemon */
    private void daemon()  {
        final Collection<Daemon> daemons = HyenaUtil.getServiceFactory().getDaemons();
        if(DataUtil.isEmpty(daemons))
            return ;
        for(Daemon daemon : daemons) {
            //start daemon
            daemon.init();
        }
        Thread t = new Thread() {
            @Override
            public void run() {
                for(Daemon inst : daemons) {
                    inst.destroy();
                }
            }
        };
        t.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(t);
    }
}
