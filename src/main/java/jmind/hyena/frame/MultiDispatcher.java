package jmind.hyena.frame;


import io.netty.channel.Channel;
import jmind.base.util.TaskExecutor;
import jmind.hyena.handler.HyenaCommand;


/**
 * 采用多线程形式分发,nio redis有问题，而且性能未必好。
 * Created by xieweibo on 2016/12/2.
 */
public class MultiDispatcher extends Dispatcher {

    public void dispatch(final HyenaCommand cmd, final Channel cs) {
        TaskExecutor.getInstance().runTask(()->super.dispatch(cmd, cs));
    }
}
