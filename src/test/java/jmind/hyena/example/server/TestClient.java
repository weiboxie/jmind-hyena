package jmind.hyena.example.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jmind.core.lang.SourceProperties;
import jmind.core.lang.shard.LoadBalance;
import jmind.core.lang.shard.RoundRobinLoadBalance;
import jmind.core.redis.*;
import jmind.core.util.RandUtil;
import jmind.core.util.TaskExecutor;
import jmind.hyena.example.pojo.MyPojo;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by xieweibo on 2016/11/30.
 */
public class TestClient {

   static Redis jedis=getBalanceRedis( "127.0.0.1:9527");

    public static void main(String[] args)  {
     while (true){
         testPojo();
         try {
             TimeUnit.SECONDS.sleep(1);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
     }
    }

    public static void testPojo(){
        String key="mypojo";
String myPojo="name=x&id=101&email=weibo@126.com";
        String get = jedis.get(key + "?" + myPojo);
        System.out.println(get);

        String set = jedis.set(key+"?name=x", myPojo.toString());
        System.out.println(set);


    }

    public static void testSample() throws InterruptedException {
        String key="sample";

        while (true){
            String value=RandUtil.randomCode(4,true);
            System.out.println(jedis.get(key+"?"+value));
            System.out.println(jedis.get(key+"?"+value));
            System.out.println(jedis.set(key,value));
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public static void testAll(String[] args) {
       long start=System.currentTimeMillis();
        int k=100;
        int j=100;
        CountDownLatch  c=new CountDownLatch(k);
       for(int i=0;i<k;i++){
           TaskExecutor.getInstance().runTask(new Runnable() {
               @Override
               public void run() {
                   test(j);

                   c.countDown();
               }
           });
       }

        try {
            c.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.print("总用时：");
        System.out.println(System.currentTimeMillis()-start);

    }

    static void test(int count){
         String key="sample";
         long start=System.currentTimeMillis();
         for(int i=0;i<count;i++){
             String abc=RandUtil.randomCode(8,true);
             String s = jedis.get(key + "?" + abc);
             if(!s.equals(abc)){
                 System.err.println(s+"=get=="+abc);
            }

             String j="";
             try{
                  j=jedis.set(key,abc);
                 JSONObject jsonObject = JSON.parseObject(j);
                if(!jsonObject.getString("value").equals(abc)){
                    System.err.println(j+"=set=="+abc);
                 }
             }catch (Exception e){
                 System.err.println(j+"---"+abc);
             }
         }

        //System.out.println(System.currentTimeMillis()-start);
     }

    static Redis getRedis(String hosts){

       return new NioRedis(hosts,1000, LoadBalance.Balance.RoundRobin);
     //  return new SingleJedis(hosts,100,100,100,null);
    }

    static Redis getSharedRedis(String hosts){
        return new ShardedRedis(hosts,1000,10,10);
    }

    static Redis getBalanceRedis(String hosts){

        return new HyneaLoadBalanceRedis(hosts,1000, LoadBalance.Balance.RoundRobin);
    }




}
