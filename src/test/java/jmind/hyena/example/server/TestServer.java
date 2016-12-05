package jmind.hyena.example.server;

import jmind.hyena.bootstrap.HyenaServer;
import jmind.hyena.util.HyenaUtil;

/**
 * Created by xieweibo on 2016/12/1.
 */
public class TestServer {

    public static void main(String[] args) {
        // HyenaConst.multithreading=true;
        new HyenaServer().run(9527);
    }
}
