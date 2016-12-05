package jmind.hyena.example.pojo;

import jmind.core.json.Pojo;

/**
 * Created by xieweibo on 2016/12/5.
 */
public class MyPojo extends Pojo{
    private int id ;
    private  String name;
    private String email;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
