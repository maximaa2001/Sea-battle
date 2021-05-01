package by.bsuir;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.vecmath.Vector2f;
import java.time.LocalTime;


public class Main extends Application{

    public static AnchorPane root = new AnchorPane();
    public static Rectangle rectangle = new Rectangle(50,450,400,200);
    public static Field field = new Field();
    public static Ship[] ships =  {new Ship(4,new Vector2f(50,450)),
            new Ship(3,new Vector2f(50,500)),new Ship(3,new Vector2f(190,500)),
            new Ship(2,new Vector2f(50,550)),  new Ship(2,new Vector2f(150,550)),
            new Ship(2,new Vector2f(250,550)),  new Ship(1,new Vector2f(50,600)),
            new Ship(1,new Vector2f(110,600)),  new Ship(1,new Vector2f(170,600)),
            new Ship(1,new Vector2f(230,600))};

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setResizable(false);

        initializeRootPane();
        initializeMyField();
        initializeMyShipPane();
        initializeMyShips();

        Scene scene = new Scene(root,1000,800);
        scene.getStylesheets().add(0, "by/bsuir/styles/style.css");
        stage.setScene(scene);
        stage.setTitle("Морской бой");
        stage.show();


        scene.addEventHandler(KeyEvent.KEY_PRESSED,escape);
        scene.addEventHandler(KeyEvent.KEY_PRESSED,shift);
    }

    public void initializeRootPane(){
        root.setPrefWidth(1000);
        root.setPrefHeight(800);
    }

    public void initializeMyShipPane(){
        rectangle.setFill(Color.valueOf("#808080"));
        root.getChildren().add(rectangle);
    }

    public void initializeMyField(){
        field.setPrefSize(400,400);
        field.setLayoutX(50);
        root.getChildren().add(field);
    }

    public void initializeMyShips(){
        for (int i = 0; i < ships.length ; i++) {
            ships[i].drawShip();
        }
    }

    EventHandler<KeyEvent> escape = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if(keyEvent.getCode() == KeyCode.ESCAPE){
                for (int i = 0; i < ships.length ; i++) {
                    if(ships[i].getIsChoose() ){
                        ships[i].returnShip();
                    }
                }
            }
        }
    };

    EventHandler<KeyEvent> shift = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if(keyEvent.getCode() == KeyCode.SHIFT){
                for (int i = 0; i < ships.length; i++) {
                    if(ships[i].getIsMoved()){
                        ships[i].rotate();
                    }
                }
            }
        }
    };
}
