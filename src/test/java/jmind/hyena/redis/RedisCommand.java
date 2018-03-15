package jmind.hyena.redis;

import java.util.Arrays;

/**
 * Created by weibo.xwb on 2018/2/28.
 */
public class RedisCommand {
    /** Command name */
    private final String name;

    /** Optional arguments */
    private byte[] arg1;
    private byte[] arg2;

    public RedisCommand(String name, byte[] arg1) {
        this.name = name;
        this.arg1 = arg1;
    }

    public RedisCommand(String name, byte[] arg1, byte[] arg2) {
        this.name = name;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public String getName() {
        return name;
    }

    public byte[] getArg1() {
        return arg1;
    }

    public byte[] getArg2() {
        return arg2;
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", arg1=" + Arrays.toString(arg1) +
                ", arg2=" + Arrays.toString(arg2) +
                '}';
    }
}
