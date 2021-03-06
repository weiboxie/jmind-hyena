package jmind.hyena.container.web.netty.http;


import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import jmind.base.util.DataUtil;
import jmind.base.util.RequestUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.Cookie;
import java.io.*;
import java.net.InetAddress;
import java.security.Principal;
import java.util.*;

/**
 * @Author: xieweibo
 */
public class HttpRequest implements HttpServletRequest {

    private io.netty.handler.codec.http.HttpRequest request;

    private Map<String, String> heads = new HashMap<>();

    private String uri;

    private Hashtable attributes = new Hashtable();

    private String characterEncoding;

    private String contentType;

    //path parameter
    private Map<String, String> parameters=new HashMap<>();

    private boolean inputStreamUsed = false;

    private boolean readerUsed = false;

    //The content of the HTTP POST.
    private String postData;

    public HttpRequest(FullHttpRequest request) {
        this.request = request;
        String url = request.uri();
        int index=url.indexOf("?");
        if (index>0) {
            uri= url.substring(0, uri.indexOf('?'));
        }else{
            uri=url;
        }

        HttpHeaders headers = request.headers();

        for (Map.Entry<String, String> entry : headers.entries()) {
            heads.put(entry.getKey(), entry.getValue());
        }
       // parameters = RequestUtil.getParameterMap(url);

        // 是GET请求
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(url);
        queryStringDecoder.parameters().forEach( (k,v) -> {
            // entry.getValue()是一个List, 只取第一个元素
            parameters.put(k, v.get(0));
        });


        if (HttpMethod.POST .equals(request.method())){
            // 是POST请求
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
            decoder.offer(request);

            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

            for (InterfaceHttpData parm : parmList) {

                Attribute data = (Attribute) parm;
                try {
                    parameters.put(data.getName(), data.getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        contentType = headers.get("CONTENT-TYPE");
        if (contentType == null) {
            contentType = "application/x-www-form-urlencoded";
        }
        postData = request.content().toString();
        parameters.putAll(RequestUtil.getParameterMap(postData));
    }


    @Override
    public String getAuthType() {
        return "";
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String s) {
        return System.currentTimeMillis();
    }

    @Override
    public String getHeader(String s) {
        return request.headers().get(s);
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        throw new UnsupportedOperationException("getHeaders(String s)");
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        throw new UnsupportedOperationException("getHeaderNames");
    }

    @Override
    public int getIntHeader(String s) {
        String value = getHeader(s);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public String getMethod() {
        return request.method().name();
    }

    @Override
    public String getPathInfo() {
        return request.uri();
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return "";
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestURI() {
       return uri;
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(uri);
    }

    @Override
    public String getServletPath() {
        return "";
    }

    @Override
    public HttpSession getSession(boolean b) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return true;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    /**
     * @deprecated
     */
    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String s, String s1) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return attributes.keys();
    }

    @Override
    public String getCharacterEncoding() {
        if (characterEncoding != null) {
            return characterEncoding;
        } else if (contentType == null) {
            return null;
        } else {
            int charsetPos = contentType.indexOf("charset=");
            if (charsetPos == -1) {
                return "UTF-8";
            } else {
                return contentType.substring(charsetPos + 8);
            }
        }
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        this.characterEncoding = s;
    }

    @Override
    public int getContentLength() {
        return getIntHeader("Content-Length");
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (readerUsed) {
            throw new IllegalStateException("The method getReader() has already been called on this request.");
        }
        inputStreamUsed = true;
        return new InputStream(postData);
    }

    @Override
    public String getParameter(String s) {
        String[] values = getParameterValues(s);
        return (values == null) ? null : values[0];
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String s) {
        Object values = parameters.get(s);
        if (values == null) {
            return null;
        } else if (values instanceof String) {
            return new String[]{(String) values};
        } else {
            ArrayList list = (ArrayList) values;
            return (String[]) list.toArray(new String[list.size()]);
        }
    }

    @Override
    public Map getParameterMap() {
        return parameters;
    }

    @Override
    public String getProtocol() {
        return "file://";
    }

    @Override
    public String getScheme() {
        int separator = uri.indexOf("://");
        if (separator != -1) {
            return uri.substring(0, separator + 3);
        }
        return "file://";
    }

    @Override
    public String getServerName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception ioe) {
            return "localhost";
        }
    }

    @Override
    public int getServerPort() {

        int i = uri.indexOf(":", 7);
        if (i>0){
          return DataUtil.toInt( uri.substring(i,uri.indexOf("/",i)));
        }
        return 80;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (inputStreamUsed) {
            throw new IllegalStateException("The method getInputStream() has already been called on this request.");
        }
        readerUsed = true;
        return new BufferedReader(new StringReader(postData));
    }

    @Override
    public String getRemoteAddr() {
        return "localhost";
    }

    @Override
    public String getRemoteHost() {
        return "localhost";
    }

    @Override
    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        attributes.remove(s);
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param s
     * @deprecated
     */
    @Override
    public String getRealPath(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRemotePort() {
        return -1;
    }

    @Override
    public String getLocalName() {
        return "Servlet Server";
    }

    @Override
    public String getLocalAddr() {
        return "localhost";
    }

    @Override
    public int getLocalPort() {
        return getServerPort();
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }


    private static class InputStream extends ServletInputStream {
        /**
         * The data. Is <code>null</code> if there is no data.
         */
        private final ByteArrayInputStream _stream;

        /**
         * Constructs a new <code>InputStream</code> instance for the specified
         * data.
         *
         * @param data the data, as a string, can be <code>null</code>.
         */
        private InputStream(String data) {
            String encoding = "ISO-8859-1";
            try {
                byte[] dataAsByte = data.getBytes(encoding);
                _stream = new ByteArrayInputStream(dataAsByte);
            } catch (UnsupportedEncodingException exception) {
                throw new RuntimeException("Failed to convert characters to bytes using encoding \"" + encoding + "\".");
            }
        }

        public int read() throws IOException {
            return _stream.read();
        }

        public int read(byte[] b) throws IOException {
            return _stream.read(b);
        }

        public int read(byte[] b, int off, int len) throws IOException {
            return _stream.read(b, off, len);
        }

        public boolean markSupported() {
            return _stream.markSupported();
        }

        public void mark(int readlimit) {
            _stream.mark(readlimit);
        }

        public long skip(long n) throws IOException {
            return _stream.skip(n);
        }

        public void reset() throws IOException {
            _stream.reset();
        }

        public void close() throws IOException {
            _stream.close();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }
}
