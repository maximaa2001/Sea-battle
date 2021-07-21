package by.bsuir;

import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Connect {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField ip_field;

    @FXML
    private TextField port_field;

    @FXML
    private Button btn_connect;

    @FXML
    private Button btn_create;

    @FXML
    private Button btn_cancel;

    @FXML
    private Label label_state;

    @FXML
    private TextField server_port_field;

    private Stage stage;
    private Scene scene;
    private int server_port;

    private InetAddress client_ip;
    private int client_port;

    private Thread waitingGamer;
    private ServerSocket serverSocket;
    private Socket socket;

    public Connect(Stage stage) throws IOException {
        this.stage = stage;
        stage.setWidth(600);
        stage.setHeight(392);
        stage.setTitle("Подключение");
        stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Connect.fxml"));
        fxmlLoader.setController(this);
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    void initialize() {
        stage.addEventHandler(KeyEvent.KEY_PRESSED, esc);
        btn_create.setOnMouseClicked(event -> {
            if (serverSocket != null) {
                if (!serverSocket.isClosed()) {
                    getMessage("Отмените созданную игру");
                    return;
                }
            }
            if (!server_port_field.getText().equals("")) {
                try {
                    server_port = Integer.parseInt(server_port_field.getText());
                } catch (NumberFormatException e) {
                    getMessage("Введите корректный порт");
                    return;
                }
                try {
                    serverSocket = new ServerSocket(server_port, 1);
                    waitingGamer = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                socket = serverSocket.accept();
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Stage stage = (Stage) btn_cancel.getScene().getWindow();
                                            stage.close();
                                            Game game = new Game(socket, true);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (!serverSocket.isClosed()) {
                                    try {
                                        serverSocket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });

                    waitingGamer.setDaemon(true);
                    waitingGamer.start();
                    label_state.setText("Ожидание второго игрока");
                    label_state.setTextFill(Color.GREEN);
                } catch (IOException e) {
                    getMessage("Попробуйте использовать другой порт");
                } catch (IllegalArgumentException e) {
                    getMessage("Попробуйте использовать другой порт");
                }
            } else {
                getMessage("Введите номер порта");
            }
        });

        btn_cancel.setOnMouseClicked(event -> {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                    label_state.setText("Игра не создана");
                    label_state.setTextFill(Color.RED);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_connect.setOnMouseClicked(event -> {
            if (serverSocket != null) {
                if (!serverSocket.isClosed()) {
                    getMessage("Отмените созданную игру");
                    return;
                }
            }
            if (!ip_field.getText().equals("")) {
                if (!port_field.getText().equals("")) {
                    try {
                        client_ip = InetAddress.getByName(ip_field.getText());
                    } catch (UnknownHostException e) {
                        getMessage("Неверный IP адрес");
                    }
                    try {
                        client_port = Integer.parseInt(port_field.getText());
                    } catch (NumberFormatException e) {
                        getMessage("Введите корректный порт");
                    }
                    try {
                        socket = new Socket(client_ip, client_port);
                        if (socket.isConnected()) {
                            stage.close();
                            Game game = new Game(socket, false);
                        } else {
                            getMessage("Сервер недоступен");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        getMessage("Неверный порт");
                    }
                } else {
                    getMessage("Введите порт");
                }
            } else {
                getMessage("Введите IP адрес");
            }
        });
    }

    EventHandler<KeyEvent> esc = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                try {
                    stage.removeEventHandler(KeyEvent.KEY_PRESSED, esc);
                    Menu menu = new Menu(stage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void getMessage(String str) {
        try {
            WarnMessage message = new WarnMessage(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
