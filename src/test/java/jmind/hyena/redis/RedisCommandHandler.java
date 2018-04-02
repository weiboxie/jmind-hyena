package jmind.hyena.redis;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.HashMap;

@ChannelHandler.Sharable
public class RedisCommandHandler extends SimpleChannelInboundHandler<RedisCommand> {

    private HashMap<String, byte[]> database = new HashMap<String, byte[]>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RedisCommand msg) throws Exception {
        System.err.println("RedisCommandHandler: " + msg);

        if (msg.getName().equalsIgnoreCase("set")) {
            if (database.put(new String(msg.getArg1()), msg.getArg2()) == null) {
                ctx.writeAndFlush(new IntegerReply(1));
            } else {
                ctx.writeAndFlush(new IntegerReply(0));
            }
        }
        else if (msg.getName().equalsIgnoreCase("get")) {
            byte[] value = database.get(new String(msg.getArg1()));
            if (value != null && value.length > 0) {
                ctx.writeAndFlush(new BulkReply(value));
            } else {
                ctx.writeAndFlush(BulkReply.NIL_REPLY);
            }
        }
    }

}
