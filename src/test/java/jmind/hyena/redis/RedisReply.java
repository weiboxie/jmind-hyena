package jmind.hyena.redis;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * Created by weibo.xwb on 2018/2/28.
 */
public interface RedisReply<T> {

    byte[] CRLF = new byte[] { '\r', '\n' };

    T data();

    void write(ByteBuf out) throws IOException;

}
