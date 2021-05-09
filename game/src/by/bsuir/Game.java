package by.bsuir;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.vecmath.Vector2f;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;

public class Game {
    private Socket socket;
    private Stage stage;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isServer;
    private volatile boolean isCanSend;
    private volatile boolean isReady;
    private volatile int time;
    private volatile Bullet bullet;

    private Label label;
    private Label whoIsMove;

    private Thread connection;
    private Thread timeThread;
    private Thread gameProc;

    public static Field field = new Field();
    private Field enemy;
    public static Ship[] ships = {new Ship(4, new Vector2f(50, 450)),
            new Ship(3, new Vector2f(50, 500)), new Ship(3, new Vector2f(190, 500)),
            new Ship(2, new Vector2f(50, 550)), new Ship(2, new Vector2f(150, 550)),
            new Ship(2, new Vector2f(250, 550)), new Ship(1, new Vector2f(50, 600)),
            new Ship(1, new Vector2f(110, 600)), new Ship(1, new Vector2f(170, 600)),
            new Ship(1, new Vector2f(230, 600))};

    public static AnchorPane root = new AnchorPane();
    public static Rectangle rectangle = new Rectangle(50, 450, 400, 200);

    EventHandler<MouseEvent> clickButton = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(isCanSend) {
                MyButton myButton = (MyButton) event.getSource();
                bullet = new Bullet(myButton.getMyId());
                isCanSend = false;
            }
        }
    };



    public Game(Socket socket, boolean isServer) throws IOException {
        this.socket = socket;
        this.isServer = isServer;
        isCanSend = true;
        isReady = false;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        label = new Label();
        label.setPrefWidth(60);
        label.setPrefHeight(60);
        label.getStyleClass().add("timer");
        label.setLayoutX(500);

        whoIsMove = new Label();
        whoIsMove.setPrefWidth(400);
        whoIsMove.setPrefHeight(100);
        whoIsMove.setLayoutX(350);
        whoIsMove.setLayoutY(400);
        whoIsMove.getStyleClass().add("whoIsMove");

        connection = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!socket.isClosed() && socket.isConnected()) {
                    try {
                        System.out.println("www");
                        Proxy proxy = (Proxy) in.readObject();
                        System.out.println("eee");
                        if (proxy.getIsGet()) {
                            break;
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        if (!socket.isClosed()) {
                            try {
                                socket.close();
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Menu menu = new Menu(stage);
                                            WarnMessage message = new WarnMessage("Время ожидания истекло");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    }
                }
                while (!isReady) {
                }
                setTimer();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        enemy = new Field();
                        enemy.setPrefSize(400, 400);
                        enemy.setLayoutX(570);
                        for (int i = 0; i < 100; i++) {
                          enemy.getButtons()[i].addEventHandler(MouseEvent.MOUSE_CLICKED,clickButton);
                        }
                        root.getChildren().add(enemy);
                        gameProc = new Thread(gameProcess);
                        gameProc.setDaemon(true);
                        gameProc.start();
                    }
                });
            }
        });
        timeThread = new Thread(timer);
        //  timeThread.setName("first");
        timeThread.setDaemon(true);
        timeThread.start();

        connection.setDaemon(true);
        connection.start();

        Button button = new Button("Готов");
        button.setLayoutX(50);
        button.setLayoutY(410);
        button.setPrefWidth(400);
        button.setPrefHeight(20);
        button.getStyleClass().add("ready");
        root.getChildren().add(button);
        root.getChildren().add(label);
        root.getChildren().add(whoIsMove);

        initializeRootPane();
        initializeMyField();
        initializeMyShipPane();
        initializeMyShips();

        stage = new Stage();
        if (isServer) {
            stage.setTitle("Server");
        } else {
            stage.setTitle("Client");
        }
        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add(0, "by/bsuir/styles/style.css");
        stage.addEventHandler(KeyEvent.KEY_PRESSED, escape);
        stage.addEventHandler(KeyEvent.KEY_PRESSED, alt);

        button.setOnMouseClicked(event -> {
            for (int i = 0; i < ships.length; i++) {
                if (ships[i].getIsCanMoved()) {
                    return;
                }
            }
            for (int i = 0; i < ships.length; i++) {
                ships[i].readyForBattle();
            }
            root.getChildren().remove(button);
            root.getChildren().remove(rectangle);
            stage.removeEventHandler(KeyEvent.KEY_PRESSED, escape);
            stage.removeEventHandler(KeyEvent.KEY_PRESSED, alt);
            try {
                out.writeObject(new Proxy(true));
                out.flush();
                isReady = true;
                System.out.println("send bool");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(1000);
        stage.setHeight(800);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> {
            if (socket.isConnected() && !socket.isClosed()) {
                try {
                    socket.close();
                    WarnMessage message = new WarnMessage("Соединение потеряно");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void initializeRootPane() {
        root.setPrefWidth(1000);
        root.setPrefHeight(800);
    }

    public void initializeMyShipPane() {
        rectangle.setFill(Color.valueOf("#808080"));
        root.getChildren().add(rectangle);
    }

    public void initializeMyField() {
        field.setPrefSize(400, 400);
        field.setLayoutX(50);
        root.getChildren().add(field);
    }

    public void initializeMyShips() {
        for (int i = 0; i < ships.length; i++) {
            ships[i].drawShip();
        }
    }

    EventHandler<KeyEvent> escape = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                for (int i = 0; i < ships.length; i++) {
                    if (ships[i].getIsChoose()) {
                        ships[i].returnShip();
                    }
                }
            }
        }
    };

    EventHandler<KeyEvent> alt = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.ALT) {
                for (int i = 0; i < ships.length; i++) {
                    if (ships[i].getIsMoved()) {
                        ships[i].rotate();
                    }
                }
            }
        }
    };

    Runnable timer = new Runnable() {
        @Override
        public void run() {
            boolean isRun = true;
            for (int i = 80; i > 0; i--) {
                //   System.out.println(Thread.currentThread().getName());
                Game.this.time = i;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        label.setText(String.valueOf(Game.this.time));
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                 //   e.printStackTrace();
                    isRun = false;
                    break;
                }
                ///     System.out.println(Thread.currentThread().getName() + " 8888999  ");
            }
            if (isRun && !socket.isClosed()) {
                try {
                    socket.close();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Menu menu = new Menu(stage);
                                WarnMessage message = new WarnMessage("Время ожидания истекло");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Runnable gameProcess = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (isServer) {
                    makeShot();
                    try {
                        getAnswer();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    setTimer();
                    boolean isSuccess = getHit();
                    if(!isSuccess){
                        return;
                    }
                } else {
                    boolean isSuccess = getHit();
                    if(!isSuccess){
                        return;
                    }
                    setTimer();
                    makeShot();
                    try {
                        getAnswer();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                setTimer();
            }
        }
    };
    private void makeShot(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                whoIsMove.setText("Вы ходите");
                whoIsMove.setTextFill(Color.GREEN);
            }
        });
        while (true) {
            if (bullet != null) {
                try {
                    out.writeObject(bullet);
                 //   System.out.println("+");
                  //  System.out.println(bullet.getId());
                    out.flush();
                    bullet = null;
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setTimer(){
        timeThread.interrupt();
        timeThread = new Thread(timer);
        timeThread.setDaemon(true);
        timeThread.start();
    }

    private boolean getHit(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                whoIsMove.setText("Ходит соперник");
                whoIsMove.setTextFill(Color.RED);
            }
        });
        while (true) {
            try {
                Bullet bullet = (Bullet) in.readObject();
                Proxy proxy = field.checkedField(bullet.getId());
                if(proxy.getIsGet() && proxy.getState().equals(State.WOUND)){
                    field.getButtonById(bullet.getId()).getStyleClass().remove("aliveButton");
                    field.getButtonById(bullet.getId()).getStyleClass().add("killButton");
                }else if(proxy.getIsGet() && proxy.getState().equals(State.KILL)) {
                    field.getButtonById(bullet.getId()).getStyleClass().remove("aliveButton");
                    field.getButtonById(bullet.getId()).getStyleClass().add("killButton");
                    MyButton buttonById = field.getButtonById(bullet.getId());
                    Vector2f vector2f = new Vector2f((float) buttonById.getLayoutX(), (float) buttonById.getLayoutY());
                    int myRow = (int) vector2f.y / 40;
                    int myColumn = (int) vector2f.x / 40;
                    //  System.out.println(vector2f.x + "   " + vector2f.y);
                    Ship ship = null;
                    boolean isFind = false;
                    for (int i = 0; i < ships.length; i++) {
                        for (int j = 0; j < ships[i].getShipImage().length; j++) {
                            int r = (int) ships[i].getShipImage()[j].getLayoutY() / 40;
                            int c = (int) ships[i].getShipImage()[j].getLayoutX() / 40;
//                            System.out.println(ships[i].getShipImage()[j].getLayoutX() + "   " +  vector2f.x );
//                            System.out.println(ships[i].getShipImage()[j].getLayoutY() + "   " +  vector2f.y );
                            if ((myRow == r) && (myColumn == c)) {
                                ship = ships[i];
                                isFind = true;
                                break;
                            }
                            if (isFind) {
                                break;
                            }
                        }
                    }
                    int column = (int) (ship.getShipImage()[0].getLayoutX() / 40);
                    int row = (int) (ship.getShipImage()[0].getLayoutY() / 40);
                    int length = ship.getLength();
                    boolean isHoriz = ship.getIsHorizontal();
                    makeAroundYellowField(column,row,length,isHoriz,field,false);
                }else if (!proxy.getIsGet()) {
                    field.getButtonById(bullet.getId()).getStyleClass().add("mimoButton");
                }
          //      System.out.println(bullet.getId());
                // отпраивть ответ
                out.writeObject(proxy);
                out.flush();
                isCanSend = true;
                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                try {
                    socket.close();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Menu menu = new Menu(stage);
                                WarnMessage message = new WarnMessage("Соединение с игроком потеряно");
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }

                        }
                    });
                    return false;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }


    private void getAnswer() throws IOException, ClassNotFoundException {
        Proxy proxy = null;
        while (true){
           proxy = (Proxy) in.readObject();
           break;
        }
        if(proxy.getIsGet() && proxy.getState().equals(State.WOUND)){
            enemy.getButtonById(proxy.getId()).getStyleClass().remove("aliveButton");
            enemy.getButtonById(proxy.getId()).getStyleClass().add("killButton");
            enemy.getButtonById(proxy.getId()).removeEventHandler(MouseEvent.MOUSE_CLICKED,clickButton);
        }else if(proxy.getIsGet() && proxy.getState().equals(State.KILL)) {
            enemy.getButtonById(proxy.getId()).getStyleClass().remove("aliveButton");
            enemy.getButtonById(proxy.getId()).getStyleClass().add("killButton");
            String id = proxy.getId();
            int column;
            int row;
            if(id.length() == 1){
                row = 0;
                column = Integer.parseInt(id);
            }else {
                row = Integer.parseInt(String.valueOf(id.charAt(0)));
                column = Integer.parseInt(String.valueOf(id.charAt(1)));
            }
            int length = 0;
            boolean isHoriz = false;
            int columnMostLeft = column;
            int rowMostTop = row;
            if(((column - 1) >= 0 && enemy.getButtonById(enemy.getIdByCoordinate(row,column-1)).getStyleClass().contains("killButton"))
            || ((column + 1) <= 9 && enemy.getButtonById(enemy.getIdByCoordinate(row,column+1)).getStyleClass().contains("killButton"))) {
                int copyColumn = column;
                while (true) {
                    if ((copyColumn - 1) >= 0 && enemy.getButtonById(enemy.getIdByCoordinate(row,copyColumn-1)).getStyleClass().contains("killButton")) {
                        copyColumn--;
                        columnMostLeft--;
                    }else {
                        break;
                    }
                }
                while (true){
                    if (copyColumn <= 9 && enemy.getButtonById(enemy.getIdByCoordinate(row,copyColumn)).getStyleClass().contains("killButton")) {
                        length++;
                        copyColumn++;
                    }else {
                        break;
                    }
                }
                isHoriz = true;
            }else if((row - 1) >= 0 && enemy.getButtonById(enemy.getIdByCoordinate(row-1,column)).getStyleClass().contains("killButton")
            || ((row + 1) <= 9 && enemy.getButtonById(enemy.getIdByCoordinate(row+1,column)).getStyleClass().contains("killButton"))){
                int copyRow= row;
                while (true) {
                    if ((copyRow - 1) >= 0 && enemy.getButtonById(enemy.getIdByCoordinate(copyRow-1,column)).getStyleClass().contains("killButton")) {
                        copyRow--;
                        rowMostTop--;
                    }else {
                        break;
                    }
                }
                while (true){
                    if (copyRow <= 9 && enemy.getButtonById(enemy.getIdByCoordinate(copyRow,column)).getStyleClass().contains("killButton")) {
                        length++;
                        copyRow++;
                    }else {
                        break;
                    }
                }
                isHoriz = false;
            }else {
                length = 1;
            }
            if(isHoriz) {
                makeAroundYellowField(columnMostLeft, row, length, true, enemy, true);
            }else {
                makeAroundYellowField(column, rowMostTop, length, false, enemy, true);
            }
            enemy.getButtonById(proxy.getId()).removeEventHandler(MouseEvent.MOUSE_CLICKED,clickButton);
        }else if (!proxy.getIsGet()) {
            enemy.getButtonById(proxy.getId()).getStyleClass().add("mimoButton");
            enemy.getButtonById(proxy.getId()).removeEventHandler(MouseEvent.MOUSE_CLICKED,clickButton);
        }
    }

    private void makeAroundYellowField(int column , int row, int length, boolean isHoriz, Field field, boolean isClosedEvent){
        if (isHoriz) {
            Vector2f vector = new Vector2f(column, row);
            int vectorStartX = (int) vector.x;
            int vectorEndX = vectorStartX + (length - 1);
            int vectorY = (int) vector.y;

            for (int i = vectorY - 1; i <= vectorY + 1; i++) {
                if ((i < 0) || (i > 9)) {
                    continue;
                }
                for (int j = vectorStartX - 1; j <= vectorEndX + 1; j++) {
                    if ((j < 0) || (j > 9)) {
                        continue;
                    }
                    if (i == vectorY) {
                        if ((vectorStartX - 1) >= 0) {
                            String id = "";
                            if (i == 0) {
                                id += String.valueOf(vectorStartX - 1);
                            } else {
                                id += String.valueOf(i);
                                id += String.valueOf(vectorStartX - 1);
                            }
                            field.getButtonById(id).getStyleClass().add("mimoButton");
                            if(isClosedEvent){
                                field.getButtonById(id).removeEventHandler(MouseEvent.MOUSE_CLICKED,clickButton);
                            }
                        }
                        if ((vectorEndX + 1) <= 9) {
                            String id = "";
                            if (i == 0) {
                                id += String.valueOf(vectorEndX + 1);
                            } else {
                                id += String.valueOf(i);
                                id += String.valueOf(vectorEndX + 1);
                            }
                            field.getButtonById(id).getStyleClass().add("mimoButton");
                            if(isClosedEvent){
                                field.getButtonById(id).removeEventHandler(MouseEvent.MOUSE_CLICKED,clickButton);
                            }
                        }
                        break;
                    } else {
                        String id = "";
                        if (i == 0) {
                            id += String.valueOf(j);
                        } else {
                            id += String.valueOf(i);
                            id += String.valueOf(j);
                        }
                        field.getButtonById(id).getStyleClass().add("mimoButton");
                        if(isClosedEvent){
                            field.getButtonById(id).removeEventHandler(MouseEvent.MOUSE_CLICKED,clickButton);
                        }
                    }
                }
            }
            // для вертикали еще надо исправить
        } else {
            Vector2f vector = new Vector2f(column,row);
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
                            String id = "";
                            //   if (j == 0) {
                            if ((vectorStartY -1) == 0 ) {
                                id += String.valueOf(i);
                            } else {
                                id += String.valueOf(vectorStartY - 1);
                                id += String.valueOf(i);
                            }
                            field.getButtonById(id).getStyleClass().add("mimoButton");
                            if(isClosedEvent){
                                field.getButtonById(id).removeEventHandler(MouseEvent.MOUSE_CLICKED,clickButton);
                            }
                        }
                        if((vectorEndY + 1) <= 9) {
                            String id = "";
                            //      if (j == 0) {
                            //           id += String.valueOf(vectorEndY + 1);
                            //      } else {
                            id += String.valueOf(vectorEndY + 1);
                            id += String.valueOf(i);
                            //       }
                            field.getButtonById(id).getStyleClass().add("mimoButton");
                            if(isClosedEvent){
                                field.getButtonById(id).removeEventHandler(MouseEvent.MOUSE_CLICKED,clickButton);
                            }
                        }
                        break;
                    }else {
                        String id = "";
                        if (j == 0) {
                            id += String.valueOf(i);
                        } else {
                            id += String.valueOf(j);
                            id += String.valueOf(i);
                        }
                        field.getButtonById(id).getStyleClass().add("mimoButton");
                        if(isClosedEvent){
                            field.getButtonById(id).removeEventHandler(MouseEvent.MOUSE_CLICKED,clickButton);
                        }
                    }
                }
            }
        }
    }
}
