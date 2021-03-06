package jmind.hyena.frame;


import io.netty.channel.Channel;
import jmind.base.util.DataUtil;
import jmind.hyena.handler.HyenaCommand;
import jmind.hyena.server.Filter;
import jmind.hyena.server.Service;
import jmind.hyena.server.ServiceFactory;
import jmind.hyena.util.HyenaConst;
import jmind.hyena.util.HyenaUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * 事件分发
 * Created by xieweibo on 2016/11/28.
 */
public class Dispatcher {
    private static volatile Dispatcher instance =null;

    public static final Dispatcher getInstance() {
        if(instance==null){
            synchronized (Dispatcher.class){
                if(instance==null){
                    instance=HyenaConst.multithreading? new MultiDispatcher():new Dispatcher();
                }
            }
        }

        return instance;
    }

    private final ServiceFactory serviceFactory = HyenaUtil.getServiceFactory();
    private final Logger logger= LoggerFactory.getLogger(getClass());




    public void dispatch(final HyenaCommand cmd, final Channel cs) {
        //1.valid cmd
        CommandType commandType = cmd.getCommand();
        if (commandType == null) {
            String errMsg="ERR unknown command '" + cmd.getValue() + "'";
            cs.write(new HyenaMsg(HyenaConst.PUNCTUATION_MINUS, errMsg));
            logger.warn("remoteIp={},{}",cs.remoteAddress(),errMsg);
            return;
        }

        switch (commandType) {
            //2.ping command
            case PING: {
                cs.write(HyenaConst.PONG_RESULT);
                return;
            }
            //3.quit command
            case QUIT: {
                logger.info("remoteIp={} quit",cs.remoteAddress());
                cs.close();
                return;
            }
            //4.exists command
            case EXISTS: {
                Service service = serviceFactory.getService(cmd.getServiceName());
                if (service == null) {
                    cs.write(new HyenaMsg(HyenaConst.PUNCTUATION_COLON, "1"));
                } else {
                    cs.write(new HyenaMsg(HyenaConst.PUNCTUATION_COLON, "0"));

                }
                return;
            }
            default:{
                break;
            }

        }

        if (DataUtil.isEmpty(cmd.getServiceName())) {
            String errMsg= "ERR wrong number of arguments for '" + cmd.getCommand() + "' command";
            cs.write(new HyenaMsg(HyenaConst.PUNCTUATION_MINUS,errMsg));
            logger.warn("remoteIp={},{}",cs.remoteAddress(),errMsg);
            return;
        }

        Service service = serviceFactory.getService(cmd.getServiceName());
        if(service==null){
           String errMsg="ERR the service  '" + cmd.getServiceName() + "' doesn't exists";
            cs.write(new HyenaMsg(HyenaConst.PUNCTUATION_MINUS,  errMsg));
            logger.warn("remoteIp={},{}",cs.remoteAddress(),errMsg);
            return;
        }
        long start=System.currentTimeMillis();
        try{
            List<Filter> filters = serviceFactory.getFilters(cmd.getServiceName());
            HyenaMsg hyenaMsg = new HyenaMsg();
            if(!DataUtil.isEmpty(filters)){
                for (Filter filter : filters) {
                    if (!filter.filter(cmd, hyenaMsg)) {
                        cs.write(hyenaMsg);
                        return;
                    }
                }
            }

            switch (commandType) {
                case GET: {
                    hyenaMsg.setResult(service.get(cmd));
                    break;
                }
                case SET: {
                    if(DataUtil.isEmpty(cmd.getValue())) {
                        cs.write(HyenaConst.ERROR_NO_BODY_SET_RESULT);
                        return ;
                    } else {
                        hyenaMsg.setPunctuation(HyenaConst.PUNCTUATION_PLUS);
                        hyenaMsg.setResult(service.set(cmd));
                        break;
                    }
                }
                case DEL: {
                    hyenaMsg.setPunctuation(HyenaConst.PUNCTUATION_COLON);
                    hyenaMsg.setResult( service.del(cmd));
                    break;
                }
                default:{
                    String errMsg="ERR unknown command '" + cmd.getCommand() + "'";
                    cs.write(new HyenaMsg(HyenaConst.PUNCTUATION_MINUS, errMsg));
                    return ;
                }
            }
            if(!DataUtil.isEmpty(filters)){
                for (int i=filters.size()-1;i>=0;i--) {
                    filters.get(i).afterService(cmd, hyenaMsg);
                }
            }
            cs.write(hyenaMsg);
            StatMonitor.getStats().mustGet(cmd.getFullName()).recordSuccess(start,300);

        }catch (Exception e){
            cs.write(new HyenaMsg(HyenaConst.PUNCTUATION_MINUS,e.getMessage()));
            StatMonitor.getStats().mustGet(cmd.getFullName()).recordException(start);
            logger.error(cmd.getServiceName(),e);
        }
    }


}