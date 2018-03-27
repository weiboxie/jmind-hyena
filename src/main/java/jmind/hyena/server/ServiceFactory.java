package jmind.hyena.server;

import java.util.Collection;
import java.util.List;

/**
 * Created by xieweibo on 2016/11/29.
 */
public interface ServiceFactory {
    /**
     * 根据一个名字获取一个服务
     * @param name
     * @return
     */
    Service  getService(String name);

    /**
     * 根据名字获取过滤器
     * @param name
     * @return
     */
    List<Filter> getFilters(String name);

    Collection<Daemon> getDaemons();

    /**
     * 获取所有服务
     * @return
     */
    Collection<String> getServices();




}
