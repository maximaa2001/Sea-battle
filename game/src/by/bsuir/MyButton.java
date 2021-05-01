package by.bsuir;

import javafx.scene.control.Button;

public class MyButton extends Button {
    private String myId;
    private boolean isShip;

    public MyButton(boolean isShip, String myId){
        this(myId);
        this.isShip = isShip;
    }

    public MyButton(String myId){
        this.myId = myId;
//        this.setStyle("-fx-background-color: #D3D3D3");
//        this.setStyle("-fx-border-color: black");
//
//        this.setOnMouseEntered(event ->{
//            this.setStyle("-fx-border-color:red");
//        });
//        this.setOnMouseExited(event ->{
//            this.setStyle("-fx-border-color: black");
//        });

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
