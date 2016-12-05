package jmind.hyena.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jmind.core.util.CollectionsUtil;
import jmind.core.util.DataUtil;
import jmind.core.util.GlobalConstants;
import jmind.core.util.JmindLoader;
import jmind.hyena.server.ServiceFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by xieweibo on 2016/11/28.
 */
public class HyenaUtil {

    public static final byte[] toBytes(Object obj) {
        try {
            return String.valueOf(obj).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static final int bytesToInteger(List<Byte> bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) b);
        }
        return DataUtil.toInt(sb.toString());
    }

    public static final ServiceFactory getServiceFactory() {
        return JmindLoader.getJmindLoader(ServiceFactory.class).getExtension("serviceFactory");
    }

    /**
     * 解析参数形式的String
     * @param param
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T>  T parseParam(String param,Class<T> clazz){
        if (DataUtil.isEmpty(param))
            return null;
        JSONObject jo=new JSONObject();
        StringTokenizer token = new StringTokenizer(param, GlobalConstants.DELIM);
        while (token.hasMoreTokens()) {
            String[] ss=token.nextToken().split("=");
            if (ss.length == 2) {
                jo.put(ss[0], ss[1]);
            }
        }
        // 直接转换，性能高于转成String 在转换
        return JSON.toJavaObject(jo,clazz);
    }
}
