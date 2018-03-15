package jmind.hyena.example.service;

import jmind.hyena.frame.HyenaMsg;
import jmind.hyena.handler.HyenaCommand;
import jmind.hyena.server.Filter;


/**
 * Created by xieweibo on 2016/11/30.
 */
public class SampleFilter implements Filter {
    @Override
    public boolean filter(HyenaCommand command, HyenaMsg result) {
        result.setResult("filter error");
        return true;
    }

    @Override
    public void afterService(HyenaCommand command, HyenaMsg result) {

    }
}
