package jmind.hyena.server;

import jmind.hyena.handler.HyenaCommand;
import jmind.hyena.util.HyenaUtil;

import java.lang.reflect.ParameterizedType;

/**
 * 可直接操作具体对象的服务
 * Created by xieweibo on 2016/12/5.
 */
public  abstract  class AbstractService<T> implements Service  {

    public abstract String get(T param);

    public abstract String set(T param,T value);

    private Class<T> type;

    protected Class<T> getType() {
        if(type==null){
            ParameterizedType p = (ParameterizedType) getClass().getGenericSuperclass();
            type= (Class<T>) p.getActualTypeArguments()[0];
        }
        return type;
    }

    @Override
    public String get(HyenaCommand cmd) {
        return get(HyenaUtil.parseParam(cmd.getParams(),getType()));
    }

    @Override
    public String set(HyenaCommand cmd) {
        return set(HyenaUtil.parseParam(cmd.getParams(),getType()),HyenaUtil.parseParam(cmd.getValue(),getType()));
    }


}
