package jmind.hyena.server;

import jmind.hyena.handler.HyenaCommand;

/**
 * Created by xieweibo on 2016/11/28.
 */
public interface Service {
    String get(HyenaCommand cmd);

    String set(HyenaCommand cmd);

    String del(HyenaCommand cmd);
}
