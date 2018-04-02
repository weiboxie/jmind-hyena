package jmind.hyena.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * Created by xieweibo on 2016/11/28.
 */
public class HyenaServerPipelineFactory extends ChannelInitializer<NioSocketChannel> {



    @Override
    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
        ChannelPipeline pipeline = nioSocketChannel.pipeline();
        pipeline.addLast("decoder", new HyenaDecoder());
        pipeline.addLast("encoder", new HyenaEncoder());

        pipeline.addLast("handler", new HyenaServerHandler());
    }
}

