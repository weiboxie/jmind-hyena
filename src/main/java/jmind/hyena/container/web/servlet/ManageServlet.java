package jmind.hyena.container.web.servlet;



import jmind.hyena.container.web.VelocityFacade;
import org.apache.velocity.VelocityContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * manage servlet for zedis
 * @author xieweibo
 */
public class ManageServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
		VelocityContext ctx = new VelocityContext();
        response.getWriter().println(VelocityFacade.merge( "vm/manage.vm", ctx));
	}

}
