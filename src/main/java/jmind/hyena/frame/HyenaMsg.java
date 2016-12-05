package jmind.hyena.frame;

import jmind.hyena.util.HyenaConst;

import java.io.UnsupportedEncodingException;

/**
 * 返回结果
 * Created by xieweibo on 2016/11/25.
 */
public class HyenaMsg {
    // 标识
    private  byte punctuation;
    // 结果
    private String result ;

    public HyenaMsg(){
        this.punctuation=HyenaConst.PUNCTUATION_DOLLAR;
    }

    public HyenaMsg(byte punctuation,String result){
        this.punctuation=punctuation;
        this.result=result;
    }

    public byte getPunctuation() {
        return punctuation;
    }

    public void setPunctuation(byte punctuation) {
        this.punctuation = punctuation;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public byte[] getResultBytes(){
        if(result==null) return null;
        try {
            return result.getBytes(HyenaConst.UTF8);
        } catch (UnsupportedEncodingException e) {
            return result.getBytes();
      }
    }

    public String toString(){
      return  punctuation+result;
    }
}
