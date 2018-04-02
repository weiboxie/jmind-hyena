package jmind.hyena.container.web.netty.http;

import jmind.hyena.container.web.servlet.ChannelsServlet;
import jmind.hyena.container.web.servlet.ManageServlet;
import jmind.hyena.container.web.servlet.ServiceServlet;
import jmind.hyena.container.web.servlet.StatsServlet;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;

public class ServletHelper {

    private static Map<String, HttpServlet> servletMap = new HashMap<>();

    static {
        servletMap.put("manage", new ManageServlet());
        servletMap.put("service", new ServiceServlet());
        servletMap.put("channels", new ChannelsServlet());

        servletMap.put("stats", new StatsServlet());

    }

    public static Servlet getServlet(String uri) {
            int n = uri.indexOf("?");
            String str =n>0?uri.substring(uri.lastIndexOf("/")+1,n):uri.substring(uri.lastIndexOf("/") + 1);
            HttpServlet servlet = servletMap.get(str.toLowerCase());
            return servlet;

    }

    public static void destroy(Servlet s) {
        s.destroy();
    }
}