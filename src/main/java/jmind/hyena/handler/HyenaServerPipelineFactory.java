package jmind.hyena.handler;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * Created by xieweibo on 2016/11/28.
 */
public class HyenaServerPipelineFactory implements ChannelPipelineFactory {

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("decoder", new HyenaDecoder());
        pipeline.addLast("encoder", new HyenaEncoder());

        pipeline.addLast("handler", new HyenaServerHandler());

        return pipeline;
    }

}

