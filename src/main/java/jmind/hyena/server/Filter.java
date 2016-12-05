package jmind.hyena.server;

import jmind.hyena.frame.HyenaMsg;
import jmind.hyena.handler.HyenaCommand;
import org.jboss.netty.channel.Channel;

/**
 * Created by xieweibo on 2016/11/28.
 */
public interface Filter {

    /**
     * filter before service
     * @param command
     *     only need if set command request
     * @param result
     *     only need when this method return false
     *     this is a success result with message the implementor set

     * @return
     *     true for forward and false for stop and write the result to the client
     */
    public boolean filter(HyenaCommand command, HyenaMsg result);



    /**
     * filter after service
     * <pre>
     * this helper function can do something according the parameters(especially the result parameter)
     * (can also do a asynchronous business request according the key,value,and result)
     * this doesn't do any interruption effect
     * </pre>
     @param command

     *     only need if set command request
     * @param result
     *     returned by the service
     */
    public void afterService(HyenaCommand command, HyenaMsg result);
}
