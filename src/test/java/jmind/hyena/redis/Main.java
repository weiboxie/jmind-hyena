package jmind.hyena.redis;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * Created by weibo.xwb on 2018/2/28.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        new Main().start(9527);
    }

    public void start(int port) throws Exception {
      //  EventLoopGroup group = new NioEventLoopGroup();
        final DefaultEventExecutorGroup group = new DefaultEventExecutorGroup(1);
        try {
            ServerBootstrap b = new ServerBootstrap()
                    .group(new NioEventLoopGroup(),new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new RedisCommandDecoder())
                                    .addLast(new RedisReplyEncoder())
                                    .addLast(group,new RedisCommandHandler());
                        }
                    });

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shutdown the EventLoopGroup, which releases all resources.
            group.shutdownGracefully();
        }
    }


}
