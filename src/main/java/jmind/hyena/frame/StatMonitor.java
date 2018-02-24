package jmind.hyena.frame;



import jmind.base.algo.atomic.StatsCounterMap;

import java.util.List;

/**
 * 方法性能统计
 * Created by xieweibo on 2016/12/6.
 */
public class StatMonitor {

    private static  final StatMonitor statMonitor=new StatMonitor();

    private  final StatsCounterMap<String> statsCounterMap=new StatsCounterMap<String>();

    public static StatsCounterMap<String> getStats(){
        return statMonitor.statsCounterMap;
    }

}
