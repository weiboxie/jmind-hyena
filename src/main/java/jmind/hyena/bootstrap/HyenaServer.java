package jmind.hyena.bootstrap;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import jmind.base.util.DataUtil;
import jmind.base.util.GlobalConstants;
import jmind.core.cache.support.ConcurrentLinkedHashMap;
import jmind.hyena.handler.HyenaDecoder;
import jmind.hyena.handler.HyenaEncoder;
import jmind.hyena.handler.HyenaServerHandler;
import jmind.hyena.handler.HyenaServerPipelineFactory;
import jmind.hyena.server.Daemon;
import jmind.hyena.util.HyenaUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by xieweibo on 2016/11/29.
 */
public class HyenaServer {

    public final static Map<String, Channel> allChannels=new ConcurrentHashMap<>(); // <ip:port, channel>

  final Logger logger= LoggerFactory.getLogger(getClass());


    /** start server */
    public void run(int port) {
        //1.start  server
        ServerBootstrap   bootstrap = new ServerBootstrap();
        final DefaultEventExecutorGroup group = new DefaultEventExecutorGroup(1);
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyServerBoss", true));
        EventLoopGroup workerGroup = new NioEventLoopGroup(GlobalConstants.NCPU+1,
                new DefaultThreadFactory("NettyServerWorker", true));

        try {
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                    .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        public void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
//             p.addLast(new ByteLoggingHandler(LogLevel.INFO));
                            p.addLast(new HyenaDecoder());
                            p.addLast(new HyenaEncoder());
                            p.addLast(group, new HyenaServerHandler());
                        }
                    });

            // Start the server.
            ChannelFuture f = bootstrap.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down all event loops to terminate all threads.
            group.shutdownGracefully();
        }


        //run daemon
       //  daemon();



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
