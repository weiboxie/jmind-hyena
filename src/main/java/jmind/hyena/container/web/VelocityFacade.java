package jmind.hyena.container.web;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/** 
 * Velocity Facade
 * @author xieweibo
 */
public class VelocityFacade {

	static {
		try {
			//1.通用设置转义html敏感字符串
			Properties p = new Properties();
			//得到 class path 路径下的配置文件来初始化
			p.load(VelocityFacade.class.getClassLoader().getResourceAsStream("velocity.properties"));
			Velocity.init(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	/** 
	 * 融合模版和数据
	 * @param vmPath
	 * @param context
	 * @return
	 */
	public static String merge(String vmPath, VelocityContext context) {
		String result = null;
		Template template = Velocity.getTemplate(vmPath);
		StringWriter writer = new StringWriter();
		try {
			template.merge(context, writer);
			result = writer.toString();
		} finally {
			if(writer != null) try { writer.close(); } catch(IOException ioe) {}
		}
		
		return result;
	}
	
}