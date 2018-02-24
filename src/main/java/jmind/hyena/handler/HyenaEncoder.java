package jmind.hyena.handler;


import jmind.base.util.DataUtil;
import jmind.hyena.frame.HyenaMsg;
import jmind.hyena.util.HyenaConst;
import jmind.hyena.util.HyenaUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * Created by xieweibo on 2016/11/28.
 */
public class HyenaEncoder extends OneToOneEncoder {

    protected Object encode(ChannelHandlerContext ctx, Channel channel,
                            Object msg) throws Exception {
        HyenaMsg rctn = (HyenaMsg) msg;
        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

        buf.writeByte(rctn.getPunctuation());
        //($) 表示下一行数据长度，不包括换行符长度\r\n,$后面则是对应的长度的数据。

        if(rctn.getPunctuation() == HyenaConst.PUNCTUATION_DOLLAR) {
            if(!DataUtil.isEmpty(rctn.getResult())) {
                byte[] results=rctn.getResultBytes();
                buf.writeBytes(HyenaUtil.toBytes(results.length));
                buf.writeByte('\r');
                buf.writeByte('\n');
                buf.writeBytes(results);
                buf.writeByte('\r');
                buf.writeByte('\n');
            } else {
                buf.writeBytes(HyenaUtil.toBytes(-1));
                buf.writeByte('\r'); //官方协议未说明确要CRLN，但需要加
                buf.writeByte('\n');
            }
        } else if(rctn.getPunctuation() == HyenaConst.PUNCTUATION_PLUS || rctn.getPunctuation() == HyenaConst.PUNCTUATION_MINUS
                || rctn.getPunctuation() == HyenaConst.PUNCTUATION_COLON) {
            if(!DataUtil.isEmpty(rctn.getResult())) {
                buf.writeBytes(rctn.getResultBytes());
            }
            buf.writeByte('\r');
            buf.writeByte('\n');
        }

        return buf;
    }


}
