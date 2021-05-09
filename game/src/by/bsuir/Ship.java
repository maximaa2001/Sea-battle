package by.bsuir;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector2f;
import java.awt.*;
import java.io.IOException;

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

    public boolean getIsCanMoved() {
        return isCanMoved;
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

    public boolean getIsHorizontal() {
        return isHorizontal;
    }

    public void setIsHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    public void drawShip(){
        for (int i = 0; i < length; i++) {
            ship_image[i].setLayoutX(posInRectangle.x+(i*40));
            ship_image[i].setLayoutY(posInRectangle.y);
            Game.root.getChildren().add(ship_image[i]);
        }
    }

    public void returnShip(){
        currentImage = new Image("by/bsuir/image/black_block.png");
        boolean isRemoveAround = false;
        for (int i = 0; i < length; i++) {
            if(!Game.root.getChildren().contains(ship_image[i])){
                int column =(int) (ship_image[i].getLayoutX() / 40);
                int row =(int) (ship_image[i].getLayoutY() / 40);
                if(!isRemoveAround){
                    if(isHorizontal){
                        markAroundShipX(new Vector2f(column,row),"0");
                    }else {
                        markAroundShipY(new Vector2f(column,row),"0");
                    }
                    isRemoveAround = true;
                }
                Game.field.getField()[row][column] = "0";

                Game.field.getChildren().remove(ship_image[i]);
                Game.root.getChildren().add(ship_image[i]);
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
            isMoved = false;
        }
        addSome();
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
                    /*
                    Проверка свободного места в массиве
                     */
                    if(isHorizontal){
                        if(!checkFreeSpaceX(new Vector2f(column,row))){
                            returnShip();
                            getMessage("Сюда нельзя ставить корабль");
                            return;
                        }
                    }else {
                       if(!checkFreeSpaceY(new Vector2f(column,row))){
                           returnShip();
                           getMessage("Сюда нельзя ставить корабль");
                           return;
                       }
                    }
                    /*
                    добавление корабля на поле
                     */
                    for (int i = 0; i < length; i++) {
                        if(isHorizontal) {
                            Game.field.add(ship_image[i], column + i, row);
                        }else {
                            Game.field.add(ship_image[i], column , row + i);
                        }
                        Game.root.getChildren().remove(ship_image[i]);
                    }
                    /*
                    отметка в массиве занятого места
                     */
                    if(isHorizontal){
                        for (int i = 0; i < length ; i++) {
                            Game.field.getField()[row][column+i] = "*";
                        }
                    }else {
                        for (int i = 0; i < length ; i++) {
                            Game.field.getField()[row+i][column] = "*";
                        }
                    }
                    /*
                    Отметить в массиве поле вокруг корабля
                     */
                    Vector2f vector = new Vector2f(column, row);
                    if(isHorizontal){
                        markAroundShipX(vector, "-");
                    }else {
                        markAroundShipY(vector, "-");
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

//            for (int i = 0; i < Game.field.getField().length; i++) {
//                for (int j = 0; j < Game.field.getField()[i].length; j++) {
//                    System.out.print(Game.field.getField()[i][j] + "  ");
//                }
//                System.out.println();
//            }
//            System.out.println();
//            System.out.println();
        }
    };

    EventHandler<MouseEvent> clicked = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(!isCanMoved) {
                for (int i = 0; i < Game.ships.length; i++) {
                    if(Game.ships[i].getIsChoose()){
                        currentImage = new Image("by/bsuir/image/black_block.png");
                        for (int j = 0; j < Game.ships[i].getLength(); j++) {
                            Game.ships[i].getShipImage()[j].setImage(currentImage);
                        }
                        Game.ships[i].setIsChoose(false);
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

    private void markAroundShipX(Vector2f coordinate, String str){
        Vector2f vector = coordinate;

        int vectorStartX = (int) vector.x;
        int vectorEndX = vectorStartX + (length -1);
        int vectorY = (int) vector.y;

        for (int i = vectorY - 1; i <= vectorY + 1 ; i++) {
            if((i < 0) || (i > 9)){
                continue;
            }
            for (int j = vectorStartX - 1; j <= vectorEndX + 1 ; j++) {
                if((j < 0) || (j > 9)){
                    continue;
                }
                if(i == vectorY){
                    if((vectorStartX - 1) >= 0) {
                        Game.field.getField()[i][vectorStartX - 1] = str;
                    }
                    if((vectorEndX + 1) <= 9) {
                        Game.field.getField()[i][vectorEndX + 1] = str;
                    }
                    break;
                }else {
                    Game.field.getField()[i][j] = str;
                }
            }
        }
    }

    private void markAroundShipY(Vector2f coordinate, String str){
        Vector2f vector = coordinate;
        int vectorStartY = (int) vector.y;
        int vectorEndY = vectorStartY + (length - 1);
        int vectorX = (int) vector.x;

        for (int i = vectorX - 1; i <= vectorX + 1 ; i++) {
            if((i < 0) || (i > 9)){
                continue;
            }
            for (int j = vectorStartY - 1; j <= vectorEndY + 1 ; j++) {
                if((j < 0) || (j > 9)){
                    continue;
                }
                if(i == vectorX){
                    if(vectorStartY - 1 >= 0) {
                        Game.field.getField()[vectorStartY - 1][i] = str;
                    }
                    if((vectorEndY + 1) <= 9) {
                        Game.field.getField()[vectorEndY + 1][i] = str;
                    }
                    break;
                }else {
                    Game.field.getField()[j][i] = str;
                }
            }
        }
    }

    private boolean checkFreeSpaceX(Vector2f vector){
        int startX = (int) vector.x;
        int endX = (int) vector.x + (length-1);
        int y = (int) vector.y;

        for (int i = startX; i <= endX; i++) {
            if (Game.field.getField()[y][i].equals("-") || Game.field.getField()[y][i].equals("*")) {
                return false;
            }
        }

        for (int i = y - 1; i < y + 1 ; i++) {
            if((i < 0) || (i > 9)){
                continue;
            }
            for (int j = startX - 1; j <= endX + 1; j++) {
                if((j < 0) || (j > 9)){
                    continue;
                }
                if(i == y){
                    if((startX - 1) >= 0) {
                        if (Game.field.getField()[i][startX - 1].equals("*")) {
                            return false;
                        }
                    }
                    if((endX + 1) <= 9){
                        if (Game.field.getField()[i][endX + 1].equals("*")) {
                            return false;
                        }
                    }
                    break;
                }
                if(Game.field.getField()[i][j].equals("*")){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkFreeSpaceY(Vector2f vector){
        int startY = (int) vector.y;
        int endY = (int) vector.y + (length-1);
        int x = (int) vector.x;

        for (int i = startY; i <= endY ; i++) {
            if(Game.field.getField()[i][x].equals("-") || Game.field.getField()[i][x].equals("*")){
                return false;
            }
        }

        for (int i = x - 1; i < x + 1 ; i++) {
            if((i < 0) || (i > 9)){
                continue;
            }
            for (int j = startY - 1; j <= endY + 1; j++) {
                if((j < 0) || (j > 9)){
                    continue;
                }
                if(i == x){
                    if((startY - 1) >= 0) {
                        if (Game.field.getField()[startY - 1][i].equals("*")) {
                            return false;
                        }
                    }
                    if((endY + 1) <= 9){
                        if (Game.field.getField()[endY + 1][i].equals("*")) {
                            return false;
                        }
                    }
                    break;
                }
                if(Game.field.getField()[j][i].equals("*")){
                    return false;
                }
            }
        }
        return true;
    }

    private void addSome(){  // при отмене позиции корабля установка необходимых -
        for (int i = 0; i < Game.field.getField().length; i++) {
            for (int j = 0; j < Game.field.getField()[i].length; j++) {
                if(Game.field.getField()[i][j].equals("*")){
                    if(i - 1 >= 0 && !(Game.field.getField()[i-1][j].equals("*"))){
                        Game.field.getField()[i-1][j] = "-";
                    }
                    if(i - 1 >= 0 && j-1 >=0 && !(Game.field.getField()[i-1][j-1].equals("*"))){
                        Game.field.getField()[i-1][j-1] = "-";
                    }
                    if(j-1 >=0 && !(Game.field.getField()[i][j-1].equals("*"))){
                        Game.field.getField()[i][j-1] = "-";
                    }
                    if(i + 1 <= 9 && j-1 >=0 && !(Game.field.getField()[i+1][j-1].equals("*"))){
                        Game.field.getField()[i+1][j-1] = "-";
                    }
                    if(i + 1 <= 9 && !(Game.field.getField()[i+1][j].equals("*"))){
                        Game.field.getField()[i+1][j] = "-";
                    }
                    if(i + 1 <= 9 && j+1 <=9 && !(Game.field.getField()[i+1][j+1].equals("*"))){
                        Game.field.getField()[i+1][j+1] = "-";
                    }
                    if(j+1 <= 9 && !(Game.field.getField()[i][j+1].equals("*"))){
                        Game.field.getField()[i][j+1] = "-";
                    }
                    if(i - 1 >= 0 && j+1 <=9 && !(Game.field.getField()[i-1][j+1].equals("*"))){
                        Game.field.getField()[i-1][j+1] = "-";
                    }
                }
            }
        }
    }

    private void getMessage(String message){
        try {
            WarnMessage warnMessage = new WarnMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readyForBattle(){
      //  Image image = new Image("by/bsuir/image/green_block.png");
        for (int i = 0; i < length; i++) {
       //     ship_image[i].setImage(image);
            ship_image[i].setImage(null);
            int x = (int)ship_image[i].getLayoutX();
            int y = (int) ship_image[i].getLayoutY();
            int column = x / 40;
            int row = y / 40;
            Game.field.getButtonByCoordinate(row,column);
            ship_image[i].removeEventHandler(MouseEvent.MOUSE_DRAGGED,dragged);
            ship_image[i].removeEventHandler(MouseEvent.MOUSE_PRESSED,pressed);
            ship_image[i].removeEventHandler(MouseEvent.MOUSE_RELEASED,released);
            ship_image[i].removeEventHandler(MouseEvent.MOUSE_CLICKED,clicked);
        }
    }
}
