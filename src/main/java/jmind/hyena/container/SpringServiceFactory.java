package jmind.hyena.container;

import jmind.hyena.server.Daemon;
import jmind.hyena.server.Filter;
import jmind.hyena.server.Service;
import jmind.hyena.server.ServiceFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于spring ClassPathXmlApplicationContext 加载配置的实现
 * 可完美兼容spring
 * Created by xieweibo on 2016/11/29.
 */
public class SpringServiceFactory implements ServiceFactory{

    private final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("hyena-beans.xml");
    @Override
    public Service getService(String name) {
        return context.getBean(name+"Service",Service.class);
    }

    @Override
    public List<Filter> getFilters(String name) {
        Map<String, Filter> beansOfType = context.getBeansOfType(Filter.class);
        // return  new ArrayList<>(beansOfType.values());
        return  beansOfType.values().stream().collect(Collectors.toList());
    }

    @Override
    public Collection<Daemon> getDaemons() {
        return context.getBeansOfType(Daemon.class).values();
    }

    @Override
    public Collection<String> getServices() {
        return Arrays.asList(context.getBeanNamesForType(Service.class));
    }
}
