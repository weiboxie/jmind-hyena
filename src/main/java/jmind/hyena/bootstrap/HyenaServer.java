package jmind.hyena.bootstrap;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.util.concurrent.DefaultThreadFactory;
import jmind.base.util.DataUtil;
import jmind.base.util.GlobalConstants;
import jmind.core.cache.support.ConcurrentLinkedHashMap;
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

        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyServerBoss", true));
        EventLoopGroup workerGroup = new NioEventLoopGroup(GlobalConstants.NCPU+1,
                new DefaultThreadFactory("NettyServerWorker", true));



        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)

                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new HyenaServerPipelineFactory());




        try {
//            // bind
           ChannelFuture channelFuture = bootstrap.bind(port);
           channelFuture.syncUninterruptibly();

 //           ChannelFuture channelFuture = bootstrap.bind(port).sync();
//
//            // 等待服务端监听端口关闭
//            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            // 优雅退出，释放线程池资源
         //   bossGroup.shutdownGracefully();
         //   workerGroup.shutdownGracefully();
        }


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
