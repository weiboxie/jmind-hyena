package jmind.hyena.handler;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import jmind.base.util.DataUtil;
import jmind.hyena.frame.HyenaMsg;
import jmind.hyena.util.HyenaConst;
import jmind.hyena.util.HyenaUtil;


/**
 * Created by xieweibo on 2016/11/28.
 */
public class HyenaEncoder extends MessageToByteEncoder<HyenaMsg> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, HyenaMsg rctn, ByteBuf buf) throws Exception {
      //  Channel channel = channelHandlerContext.channel();

        // 创建动态bytebuf
//        Unpooled.directBuffer();
//        Unpooled.buffer();




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


    }





}
