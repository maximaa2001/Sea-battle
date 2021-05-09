package by.bsuir;

import java.io.Serializable;

public class Bullet implements Serializable {
    private String id;

    public Bullet(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
