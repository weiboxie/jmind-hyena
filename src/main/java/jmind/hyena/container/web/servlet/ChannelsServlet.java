package jmind.hyena.container.web.servlet;

import io.netty.channel.Channel;
import jmind.hyena.bootstrap.HyenaServer;
import jmind.hyena.container.web.VelocityFacade;
import jmind.hyena.frame.StatMonitor;
import org.apache.commons.collections.map.HashedMap;
import org.apache.velocity.VelocityContext;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xieweibo on 2016/12/7.
 */
public class ChannelsServlet extends HttpServlet {

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
        List<Map<String,Object>> list=new ArrayList<>();
        for(Channel channel:HyenaServer.allChannels.values()){
            SocketAddress client = channel.remoteAddress();
            if(client!=null){
                Map<String,Object> map=new HashMap();
                map.put("isConnected",channel.isActive());
                map.put("client",client);
         //       map.put("id",channel.id());
                list.add(map);
            }


        }
        ctx.put("channels",list);
        response.getWriter().println(VelocityFacade.merge( "vm/channels.vm", ctx));
    }
}
