package jmind.hyena.util;

import jmind.hyena.frame.HyenaMsg;

/**
 * 常量定义，大写不可变，小写可变
 * Created by xieweibo on 2016/11/25.
 */
public abstract class HyenaConst {

    // 是否启动多线程
    public static boolean multithreading=false ;
    public static String server_hosts ="127.0.0.1:9527" ;

    public static  final String UTF8="UTF-8" ;

    public static final char CR = '\r';
    public static final char LF = '\n';
    public static final String QUESTION_MARK="?" ;

    //(+) 表示一个正确的状态信息，具体信息是当前行+后面的字符。
    public static final byte PUNCTUATION_PLUS = '+';
    // ($) 表示下一行数据长度，不包括换行符长度\r\n,$后面则是对应的长度的数据。
    public static final byte PUNCTUATION_DOLLAR = '$';
    //(-)  表示一个错误信息，具体信息是当前行－后面的字符。
    public static final byte PUNCTUATION_MINUS = '-';
    //(:) 表示返回一个数值，：后面是相应的数字节符。
    public static final byte PUNCTUATION_COLON = ':';


    public static final HyenaMsg PONG_RESULT = new HyenaMsg(PUNCTUATION_PLUS, "PONG");
    public static final HyenaMsg ERROR_NO_BODY_SET_RESULT = new HyenaMsg(PUNCTUATION_MINUS, "ERR wrong number of arguments for 'set' command");



}
