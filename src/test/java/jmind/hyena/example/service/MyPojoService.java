package jmind.hyena.example.service;

import jmind.hyena.example.pojo.MyPojo;
import jmind.hyena.handler.HyenaCommand;
import jmind.hyena.server.AbstractService;

/**
 * Created by xieweibo on 2016/12/5.
 */
public class MyPojoService extends AbstractService<MyPojo> {
    @Override
    public String del(HyenaCommand cmd) {
        return null;
    }

    @Override
    public String get(MyPojo param) {
        return param.toString();
    }

    @Override
    public String set(MyPojo param, MyPojo value) {
        System.out.println(param);
        return value.toString();
    }
}
