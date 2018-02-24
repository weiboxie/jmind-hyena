package jmind.hyena.container;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import jmind.base.util.DataUtil;
import jmind.base.util.reflect.ClassUtil;
import jmind.hyena.server.Daemon;
import jmind.hyena.server.Filter;
import jmind.hyena.server.Service;
import jmind.hyena.server.ServiceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于自定义配置的实现。配置采用json格式
 * Created by xieweibo on 2016/11/30.
 */
public class JSONServiceFactory implements ServiceFactory{
    private final Map<String,Service> services=new ConcurrentHashMap<>();
    private List<Daemon> daemons;
    private Map<String,List<Filter>> filters;
    public JSONServiceFactory(){
        InputStream stream = getClass().getClassLoader().getResourceAsStream("service.jjs");
        try {
            JSONObject json = JSON.parseObject(stream, JSONObject.class);
            if(json.containsKey("daemons")){
                daemons=new ArrayList<>();
                JSONArray daemons = json.getJSONArray("daemons");
                for(int i=0;i<daemons.size();i++){
                    Daemon daemon= (Daemon) ClassUtil.newInstance(daemons.getString(i));
                    if(daemon!=null)
                     daemons.add(daemon);
                }
            }

            if(json.containsKey("services")){
                JSONObject serviceNames = json.getJSONObject("services");
                for(String key:serviceNames.keySet()){
                    Service s=(Service)ClassUtil.newInstance(serviceNames.getString(key));
                    if(s!=null)
                    services.put(key,s);
                }
            }
            if(json.containsKey("filters")){
                filters=new ConcurrentHashMap<>();
                JSONObject filterNames = json.getJSONObject("filters");
                 for (String key:filterNames.keySet()){
                     JSONArray ja = filterNames.getJSONArray(key);
                      List<Filter> ff=new ArrayList<>();
                      for(int i=0;i<ja.size();i++){
                          Filter o = (Filter) ClassUtil.newInstance(ja.getString(i));
                          if(o!=null)
                          ff.add(o);

                      }
                      filters.put(key,ff);
                 }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Service getService(String name) {
        return services.get(name);
    }

    @Override
    public Collection<Filter> getFilters(String name) {
        if(DataUtil.isEmpty(filters))
            return Collections.emptyList();
        if(filters.containsKey("*")){
            List<Filter> f=new ArrayList<>(filters.get("*"));
            List<Filter> list = this.filters.get(name);
            if(!DataUtil.isEmpty(list))
            f.addAll(list);
            return f ;
        }else{
            return filters.get(name);
        }
    }

    @Override
    public Collection<Daemon> getDaemons() {
        return daemons;
    }

    @Override
    public Collection<String> getServices() {
        return services.keySet();
    }
}
