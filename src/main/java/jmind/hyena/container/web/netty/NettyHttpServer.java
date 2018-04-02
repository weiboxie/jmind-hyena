package jmind.hyena.container.web.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class NettyHttpServer {
    public static void main(String[] args) {
        start(8087);
    }
    public static  void start(final int port)  {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch)
                                throws Exception {

                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());

                            //将多个消息转换为单一的FullHttpRequest或FullHttpResponse对象
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));

                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());

                            /**
                             * 压缩
                             * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
                             * while respecting the "Accept-Encoding" header.
                             * If there is no matching encoding, no compression is done.
                             */
                            ch.pipeline().addLast("deflater", new HttpContentCompressor());

                            ch.pipeline().addLast("ServletHandler", new ServletHandler());
                        }
                    });
            ChannelFuture future = b.bind("localhost", port).sync();
            System.err.println("Servlet Server startup.");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
