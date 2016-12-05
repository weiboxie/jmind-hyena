package jmind.hyena.handler;

import jmind.hyena.frame.CommandType;
import jmind.hyena.util.HyenaConst;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * client command
 * @author Created by xieweibo on 2016/11/28.
 */
public class HyenaCommand implements Serializable {
	// 命令
	private CommandType command;
	// 服务名
	private String serviceName;
	// 参数
	private String params ;
	// set 的值
	private String value;

	public HyenaCommand(List<byte[]> commands) {
		try{
		if(commands.size()>0){
			command=CommandType.valueOf(new String(commands.get(0)).toUpperCase());
		}
		if(commands.size()>1){
			serviceName=new String(commands.get(1));
			int index=serviceName.indexOf(HyenaConst.QUESTION_MARK);
			if(index>0) {
				params=serviceName.substring(index+1);
				serviceName = serviceName.substring(0, index);
			}
		}
		if(commands.size()>2){
			value=new String(commands.get(2));
		}
		}catch (Exception e){
			value=Arrays.toString(commands.toArray());
		}

	}

	public CommandType getCommand() {
		return command;
	}


	public String getServiceName() {
		return serviceName;
	}


	public String getParams() {
		return params;
	}


	public String getValue() {
		return value;
	}

}
