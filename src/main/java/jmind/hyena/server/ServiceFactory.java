package jmind.hyena.server;

import java.util.Collection;

/**
 * Created by xieweibo on 2016/11/29.
 */
public interface ServiceFactory {

    Service  getService(String name);
    Collection<Filter> getFilters(String name);

    Collection<Daemon> getDaemons();




}
