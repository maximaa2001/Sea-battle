package by.bsuir;

import javafx.event.EventHandler;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.vecmath.Vector2f;
import java.awt.*;

public class Ship{
    private int length;
    private Image currentImage;
    private ImageView[] ship_image;
    private Vector2f posInRectangle;
    private Vector2f currentPos;

    private Vector2f startClick;
    private float shiftX;
    private float shiftY;

    private boolean isShift;
    private boolean isCanMoved;
    private boolean isChoose;
    private boolean isMoved;
    private boolean isHorizontal;

    private Point pointDruggedShip;

    public Ship(int length, Vector2f posInRectangle){
        this.length = length;
        this.posInRectangle = posInRectangle;
        isShift = false;
        isCanMoved = true;
        isChoose = false;
        isHorizontal = true;
        isMoved = false;
        currentPos = new Vector2f(posInRectangle.x,posInRectangle.y);
        currentImage = new Image("by/bsuir/image/black_block.png");
        ship_image = new ImageView[length];
        for (int i = 0; i < ship_image.length ; i++) {
            ship_image[i] = new ImageView(currentImage);
            ship_image[i].addEventHandler(MouseEvent.MOUSE_DRAGGED,dragged);
            ship_image[i].addEventHandler(MouseEvent.MOUSE_PRESSED,pressed);
            ship_image[i].addEventHandler(MouseEvent.MOUSE_RELEASED,released);
            ship_image[i].addEventHandler(MouseEvent.MOUSE_CLICKED,clicked);
        }
    }

    public int getLength() {
        return length;
    }

    public ImageView[] getShip() {
        return ship_image;
    }

    public Vector2f getPos() {
        return posInRectangle;
    }

    public boolean getIsChoose() {
        return isChoose;
    }

    public ImageView[] getShipImage() {
        return ship_image;
    }

    public boolean getIsMoved() {
        return isMoved;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setShip(ImageView[] ship_image) {
        this.ship_image = ship_image;
    }

    public void setPos(Vector2f posInRectangle) {
        this.posInRectangle = posInRectangle;
    }

    public void setIsChoose(boolean isChoose) {
        this.isChoose = isChoose;
    }

    public void setIsMoved(boolean isMoved) {
        this.isMoved = isMoved;
    }

    public void drawShip(){
        for (int i = 0; i < length; i++) {
            ship_image[i].setLayoutX(posInRectangle.x+(i*40));
            ship_image[i].setLayoutY(posInRectangle.y);
            Main.root.getChildren().add(ship_image[i]);
        }
    }

    public void returnShip(){
        currentImage = new Image("by/bsuir/image/black_block.png");
        for (int i = 0; i < length; i++) {
            if(!Main.root.getChildren().contains(ship_image[i])){
                Main.field.getChildren().remove(ship_image[i]);
                Main.root.getChildren().add(ship_image[i]);
            }

            ship_image[i].setLayoutX(posInRectangle.x + (i * 40));
            ship_image[i].setLayoutY(posInRectangle.y);

            ship_image[i].setImage(currentImage);
            currentPos.x = posInRectangle.x;
            currentPos.y = posInRectangle.y;
            isShift = false;
            isChoose = false;
            isCanMoved = true;
            isHorizontal = true;
        }
    }

    EventHandler<MouseEvent> dragged = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (isCanMoved) {
                isMoved = true;
                for (int i = 0; i < length; i++) {
                    if (event.getSceneX() < 0 || event.getSceneX() > 1000 || event.getSceneY() < 0 || event.getSceneY() > 800) {
                        returnShip();
                        return;
                    }
                    if(isHorizontal) {
                        ship_image[i].setLayoutX((event.getSceneX() - shiftX) + (i * 40));
                        ship_image[i].setLayoutY(event.getSceneY() - shiftY);
                    }else {
                     //   ship_image[i].setLayoutX(event.getSceneX() - shiftX);
                     //   ship_image[i].setLayoutY(event.getSceneY() - shiftY + (i * 40));
                        ship_image[i].setLayoutX(event.getSceneX());
                        ship_image[i].setLayoutY(event.getSceneY() + (i * 40));
                    }
                    pointDruggedShip = new Point((int)event.getSceneX(),(int)event.getSceneY());
                }
                currentPos.x = (float) ship_image[0].getLayoutX();
                currentPos.y = (float) ship_image[0].getLayoutY();
            }
        }
    };
    EventHandler<MouseEvent> pressed = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(!isShift) {
                startClick = new Vector2f((float) event.getSceneX(), (float) event.getSceneY());
                shiftX = startClick.x - currentPos.x;
                shiftY = startClick.y - currentPos.y;
                isShift = true;
            }
        }
    };

    EventHandler<MouseEvent> released = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(isCanMoved) {
                if (ship_image[0].getLayoutX() > 50 && (ship_image[length-1].getLayoutX() + 30) < 450 &&
                        ship_image[0].getLayoutY() > 0 && (ship_image[length-1].getLayoutY()+30) < 400) {
                    Vector2f vector2f = new Vector2f(currentPos.x,currentPos.y);
                    vector2f.x -= 50;
                    float modCol =  vector2f.x % 40;
                    int column;
                    if (modCol >= 20){
                        column = ((int) (vector2f.x / 40)) + 1;
                    }else {
                        column = ((int) (vector2f.x / 40));
                    }
                    float modRow =  vector2f.y % 40;
                    int row;
                    if (modRow >= 20){
                        row = ((int) vector2f.y / 40) + 1;
                    }else {
                        row = ((int) vector2f.y / 40);
                    }
                    for (int i = 0; i < length; i++) {
                        if(isHorizontal) {
                            Main.field.add(ship_image[i], column + i, row);
                        }else {
                            Main.field.add(ship_image[i], column , row + i);
                        }
                        Main.root.getChildren().remove(ship_image[i]);
                    }
                    isCanMoved = false;
                }
                else {
                    returnShip();
                }
                currentPos.x = (float) ship_image[0].getLayoutX();
                currentPos.y = (float) ship_image[0].getLayoutY();
                isShift = false;
                isMoved = false;
            }
        }
    };

    EventHandler<MouseEvent> clicked = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(!isCanMoved) {
                for (int i = 0; i < Main.ships.length; i++) {
                    if(Main.ships[i].getIsChoose()){
                        currentImage = new Image("by/bsuir/image/black_block.png");
                        for (int j = 0; j < Main.ships[i].getLength(); j++) {
                            Main.ships[i].getShipImage()[j].setImage(currentImage);
                        }
                        Main.ships[i].setIsChoose(false);
                    }
                }
                currentImage = new Image("by/bsuir/image/red_block.png");
                for (int i = 0; i < Ship.this.length; i++) {
                    Ship.this.ship_image[i].setImage(currentImage);
                }
                Ship.this.isChoose = true;
            }
        }
    };

    public void rotate(){
        float startX = (float) ship_image[0].getLayoutX();
        float startY = (float) ship_image[0].getLayoutY();
        if(isHorizontal){
            for (int i = 0; i < length ; i++) {
                ship_image[i].setLayoutX(pointDruggedShip.x);
                ship_image[i].setLayoutY(pointDruggedShip.y + (40 * i));
            }
            isHorizontal = false;
        }else {
            for (int i = 0; i < length ; i++) {
                ship_image[i].setLayoutX(pointDruggedShip.x - shiftX+ (40 * i));
                ship_image[i].setLayoutY(pointDruggedShip.y - shiftY);
            }
            isHorizontal = true;
        }
    }
}
