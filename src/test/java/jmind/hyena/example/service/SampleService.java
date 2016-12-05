package jmind.hyena.example.service;

import com.alibaba.fastjson.JSON;
import jmind.hyena.handler.HyenaCommand;
import jmind.hyena.server.Service;

/**
 * Created by xieweibo on 2016/11/30.
 */
public class SampleService implements Service {
    @Override
    public String get(HyenaCommand key) {
        System.out.println(key.getCommand()+key.getServiceName());
        return key.getParams();
    }

    @Override
    public String set(HyenaCommand cmd) {
        return JSON.toJSONString(cmd);
    }

    @Override
    public String del(HyenaCommand key) {

        return "1";
    }
}
