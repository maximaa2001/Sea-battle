package by.bsuir;

import java.io.Serializable;

public class Proxy implements Serializable {
    private String id;
    private boolean isGet;
    private State state;

    public Proxy(boolean isGet){
        this.isGet = isGet;
    }

    public boolean getIsGet() {
        return isGet;
    }

    public State getState() {
        return state;
    }

    public String getId() {
        return id;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setId(String id) {
        this.id = id;
    }
}
