package jmind.hyena.container.web.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import jmind.hyena.container.web.netty.servlet.HttpRequest;
import jmind.hyena.container.web.netty.servlet.HttpResponse;
import jmind.hyena.container.web.netty.servlet.ServletHelper;

import javax.servlet.Servlet;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * https://www.cnblogs.com/luangeng/p/7979328.html
 */
public class ServletHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext context, FullHttpRequest httpRequest) throws Exception {
        System.err.println(httpRequest instanceof DefaultFullHttpRequest);
        System.err.println(httpRequest.getClass());

        String uri = httpRequest.uri();

        Servlet servlet = ServletHelper.getServlet(uri);
        if (servlet == null) {
            sendError(context, HttpResponseStatus.NOT_FOUND);
            return;
        }

//        ServletConfig config = new ServletConfig() {
//            @Override
//            public String getServletName() {
//                return null;
//            }
//
//            @Override
//            public ServletContext getServletContext() {
//                return null;
//            }
//
//            @Override
//            public String getInitParameter(String s) {
//                return null;
//            }
//
//            @Override
//            public Enumeration<String> getInitParameterNames() {
//                return null;
//            }
//        };
//        servlet.init(config);

        HttpRequest request = new HttpRequest(httpRequest);
        HttpResponse response = new HttpResponse();
        servlet.service(request, response);


        FullHttpResponse nettyResponse = getHttpResponse(response);
        nettyResponse.setProtocolVersion(httpRequest.protocolVersion());

        context.writeAndFlush(nettyResponse);
        context.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.content().writeBytes(Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        ctx.channel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

    private FullHttpResponse getHttpResponse(HttpResponse response) {
        int statusCode = response.getStatus();
        FullHttpResponse nettyResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(statusCode));

        HttpUtil.setContentLength(nettyResponse, response.getContentLength());

        Map<String, String> responseHeaders = response.getHeaders();
        for (Map.Entry<String, String> header : responseHeaders.entrySet()) {
            nettyResponse.headers().add(header.getKey(), header.getValue());
        }
        String responseString = response.getResult();
        if (responseString != null) {
            nettyResponse.content().writeBytes(Unpooled.copiedBuffer(responseString, CharsetUtil.UTF_8));
        }
        return nettyResponse;
    }
}