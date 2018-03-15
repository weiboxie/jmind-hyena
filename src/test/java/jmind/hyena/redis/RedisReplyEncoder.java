package jmind.hyena.redis;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * Created by weibo.xwb on 2018/2/28.
 */
public class RedisReplyEncoder extends MessageToByteEncoder<RedisReply> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RedisReply msg, ByteBuf out) throws Exception {
        System.out.println("RedisReplyEncoder: " + msg);
        msg.write(out);
    }

}
