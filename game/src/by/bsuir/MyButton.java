package by.bsuir;

import javafx.scene.control.Button;

public class MyButton extends Button {
    private String myId;
    private boolean isShip;

    public MyButton(boolean isShip, String myId) {
        this(myId);
        this.isShip = isShip;
    }

    public MyButton(String myId) {
        this.myId = myId;
    }

    public boolean getIsShip() {
        return isShip;
    }

    public String getMyId() {
        return myId;
    }

    public void setIsShip(boolean ship) {
        isShip = ship;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }
}
