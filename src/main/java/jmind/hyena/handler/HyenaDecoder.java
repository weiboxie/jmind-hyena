package jmind.hyena.handler;


import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import jmind.core.log.LogUtil;
import jmind.hyena.frame.HyenaMsg;
import jmind.hyena.util.HyenaConst;
import jmind.hyena.util.HyenaUtil;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * decoder
 Created by xieweibo on 2016/11/28.
 */
public class HyenaDecoder extends LengthFieldBasedFrameDecoder {

	public HyenaDecoder(){

		super(1024,0,2);
		LogUtil.info("init HyenaDecoder");
	}


//	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
//		Object o = decode(channelHandlerContext, byteBuf);
//		list.add(o);
//	}


	//private List<Byte> bytes = new LinkedList<Byte>();
	//private Command command = new Command();


	protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
		Channel channel = ctx.channel();

		List<Byte> bytes = new LinkedList<Byte>();
		 Command command = new Command();

		while(buffer.readableBytes() > 0) {
			if(command.stat == Stat.head) {
				if(buffer.readableBytes() < 4) return null;
				// http://www.cnblogs.com/smark/p/3247620.html
				byte magic = buffer.readByte();  // *
				// * 号后面接具体行数
				int number = Integer.parseInt(String.valueOf((char) buffer.readByte())); //param num must <= 127
				byte cr = buffer.readByte();
				byte lf = buffer.readByte();
				if(magic != '*' || number <= 0 || cr != HyenaConst.CR || lf != HyenaConst.LF) {
					return invalidateProtocal(channel);
				}
				command.stat = Stat.paramHead;
				command.paramNum = number;
			}
		
			if(command.stat == Stat.paramHead) {
				while(buffer.readableBytes() > 0) {
					bytes.add(buffer.readByte());
					if(bytes.size() > 12) { //too long body
						return invalidateProtocal(channel);
					}
					if(bytes.get(bytes.size() - 1) == HyenaConst.LF && bytes.get(bytes.size() - 2) == HyenaConst.CR) {
						bytes.remove(0); bytes.remove(bytes.size() - 1); bytes.remove(bytes.size() - 1);
						
						command.currentBodySize = HyenaUtil.bytesToInteger(bytes);
						command.stat = Stat.paramBody;
						bytes.clear();
						break;
					}
				}
			}
			
			if(command.stat == Stat.paramBody) {
				int needLength = command.currentBodySize + 2;
				if(buffer.readableBytes() < needLength) return null;
				
				byte[] dst = new byte[command.currentBodySize];
				buffer.readBytes(dst, 0, command.currentBodySize);
				command.params.add(dst);
				
				byte cr = buffer.readByte();
				byte lf = buffer.readByte();
				if(cr != HyenaConst.CR || lf != HyenaConst.LF) {
					return invalidateProtocal(channel);
				}
				
				command.currentParamNum++;
				if(command.currentParamNum < command.paramNum) {
					command.stat = Stat.paramHead;
				}
				else if(command.currentParamNum == command.paramNum) {
					List<byte[]> temp = new ArrayList<>();
					temp.addAll(command.params);
					HyenaCommand zcmd = new HyenaCommand(temp);
					command.clear();
					
					return zcmd;
				}
			}
			
		}
		
		return null;
	}
	
	private Object invalidateProtocal(Channel channel) {
		channel.write(new HyenaMsg(HyenaConst.PUNCTUATION_MINUS, "invalidate protocal"));
		channel.close();
		return null;
	}
	

	
	private enum Stat {
		head, 
		paramHead,
		paramBody,
	}
	private class Command {
		Stat stat = Stat.head;
		int paramNum;
		int currentParamNum;
		int currentBodySize;
		
		List<byte[]> params = new ArrayList<>();
		
		void clear() {
			stat = Stat.head;
			paramNum = 0;
			currentParamNum = 0;
			params.clear();

		}
	}
	
}

