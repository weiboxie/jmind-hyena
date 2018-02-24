package jmind.hyena.container.web.servlet;

import com.alibaba.fastjson.JSON;
import jmind.base.util.DataUtil;
import jmind.core.redis.NioRedis;
import jmind.core.redis.Redis;

import jmind.hyena.util.HyenaConst;
import jmind.hyena.util.HyenaUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * command test servlet
 * @author xieweibo
 */
public class ServiceServlet extends HttpServlet {
	
	private static Redis redis = new NioRedis(HyenaConst.server_hosts,3000);

	@Override
	public void destroy() {
		if(redis != null) redis.releaseResource();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		
		resp.setContentType("text/html;charset=utf-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		
		resp.getWriter().println("<style>body { font-size: 14px; }</style>");
        
        String command = req.getParameter("command");
        String key = req.getParameter("key");
        String value = req.getParameter("value");
        String prettyPrint = req.getParameter("prettyPrint");
		
        //default message
		if(command == null) {
			resp.getWriter().println("result would be showed here!");
			return;
		}
		
		try {
			//deal command for very post
			//get
			if("get".equals(command)) {
				if(DataUtil.isEmpty(key)) {
					resp.getWriter().println("key can not be empty!");
					return;
				}
				String result = redis.get(key);
				printResult(resp, result, prettyPrint);
			}
			//set
			else if("set".equals(command)) {
				if(DataUtil.isEmpty(key,value)) {
					resp.getWriter().println("key and value can not be empty!");
					return;
				}
				String result = redis.set(key, value);
				printResult(resp, result, prettyPrint);
			}
			//exists
			else if("exists".equals(command)) {
				if(DataUtil.isEmpty(key)) {
					resp.getWriter().println("key can not be empty!");
					return;
				}
				String realKey = key;
				int idx = key.indexOf(HyenaConst.QUESTION_MARK);
				if(idx != -1) {
					realKey = realKey.substring(0, idx);
				}
				boolean result=redis.exists(realKey);
				printResult(resp, result, prettyPrint);
			}
			//ping
			else if(command.equals("ping")) {
			//	boolean result = redis.p
			//	resp.getWriter().println(result);
			}
			//keys *
			else if(command.equals("keys *")) {
				Collection<String> result = HyenaUtil.getServiceFactory().getServices();

				printResult(resp, result, prettyPrint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void printResult(HttpServletResponse resp, Object result, String prettyPrint) throws IOException {

		if(result == null) {
			resp.getWriter().println("<div style='color: gray; margin-bottom: 5px;'>" +
					"service('s according method) may not exists!</div>null");
			return;
		}
		if("on".equals(prettyPrint)) {
			result = "<pre font-size: 14px;>" + JSON.toJSONString(result, true) + "</pre>";

		}
		resp.getWriter().println(result);
	}
	

}
