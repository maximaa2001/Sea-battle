package by.bsuir;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.effect.Light;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.io.File;
import java.io.IOException;
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
    public void start(Stage stage) throws IOException {
        stage.setResizable(false);
        Button button = new Button("Готов");
        button.setLayoutX(50);
        button.setLayoutY(410);
        button.setPrefWidth(400);
        button.setPrefHeight(20);
        button.getStyleClass().add("ready");
        root.getChildren().add(button);

        initializeRootPane();
        initializeMyField();
        initializeMyShipPane();
        initializeMyShips();


        Scene scene = new Scene(root,1000,800);
        scene.getStylesheets().add(0, "by/bsuir/styles/style.css");
        stage.addEventHandler(KeyEvent.KEY_PRESSED,escape);
        stage.addEventHandler(KeyEvent.KEY_PRESSED,alt);

        button.setOnMouseClicked(event -> {
            for (int i = 0; i < ships.length; i++) {
                if(ships[i].getIsCanMoved()){
                    return;
                }
            }
            for (int i = 0; i < ships.length; i++) {
                ships[i].readyForBattle();
            }
            root.getChildren().remove(button);
            root.getChildren().remove(rectangle);
            stage.removeEventHandler(KeyEvent.KEY_PRESSED,escape);
            stage.removeEventHandler(KeyEvent.KEY_PRESSED,alt);
        });

      Menu menu = new Menu(stage);

       // stage.setScene(scene);
      //  stage.setTitle("Морской бой");
      //  stage.show();
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

    EventHandler<KeyEvent> alt = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if(keyEvent.getCode() == KeyCode.ALT){
                for (int i = 0; i < ships.length; i++) {
                    if(ships[i].getIsMoved()){
                        ships[i].rotate();
                    }
                }
            }
        }
    };
}
