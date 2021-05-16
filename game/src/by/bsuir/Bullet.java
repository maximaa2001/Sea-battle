package by.bsuir;

import java.io.Serializable;

public class Bullet implements Serializable {
    private String id;
    private boolean isEndGame;

    public Bullet(String id){
        this.id = id;
        isEndGame = false;
    }

    public String getId() {
        return id;
    }

    public boolean getIsEndGame() {
        return isEndGame;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsEndGame(boolean endGame) {
        isEndGame = endGame;
    }
}
