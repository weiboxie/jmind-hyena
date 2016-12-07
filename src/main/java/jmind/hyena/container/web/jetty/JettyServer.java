package jmind.hyena.container.web.jetty;

import jmind.hyena.container.web.servlet.ChannelsServlet;
import jmind.hyena.container.web.servlet.ServiceServlet;
import jmind.hyena.container.web.servlet.ManageServlet;
import jmind.hyena.container.web.servlet.StatsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http web server for config and management
 * @author xieweibi
 */
public class JettyServer {
	
	private static Logger logger = LoggerFactory.getLogger(JettyServer.class);

	private static Server server;

	/**
	 * web 启动端口
	 * @param port
     */
	public static void start(int port) {
		server = new Server(port);
		 
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
 
        context.addServlet(new ServletHolder(new ManageServlet()),"/manage");
        context.addServlet(new ServletHolder(new ServiceServlet()),"/service");
		context.addServlet(ChannelsServlet.class,"/channels");
		context.addServlet(StatsServlet.class,"/stats");
 
        try {
			server.start();
		} catch (Exception e) {
			logger.error("web server for hynea start failed");
		}
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
            	logger.info("shutdown servlet server");
            	try { stopServer(); } catch (Exception e) {}
            }
        });
        
	}

	public static void stopServer() throws Exception {
		if (server != null) {
			server.stop();
		}
	}
}
