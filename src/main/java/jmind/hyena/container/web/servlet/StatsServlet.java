package jmind.hyena.container.web.servlet;

import jmind.hyena.container.web.VelocityFacade;
import jmind.hyena.frame.StatMonitor;
import org.apache.velocity.VelocityContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xieweibo on 2016/12/6.
 */
// @WebServlet("/stats")
public class StatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        VelocityContext ctx = new VelocityContext();
        // 清空统计信息
        if("clear".equals(req.getParameter("type"))){
            StatMonitor.getStats().clear();
        }
        ctx.put("statList", StatMonitor.getStats().stat());
        response.getWriter().println(VelocityFacade.merge( "vm/statList.vm", ctx));
    }
}
