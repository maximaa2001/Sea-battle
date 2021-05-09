package by.bsuir;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Menu {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label label_game;

    @FXML
    private Label label_about;

    @FXML
    private Label label_exit;

    private int number_choose;
    private Stage stage;
    private Scene scene;
    private Font font;

    public Menu(Stage stage) throws IOException {
        this.stage = stage;
        stage.setWidth(1000);
        stage.setHeight(840);
        stage.setTitle("Меню");
        number_choose = 1;
        font = Font.loadFont(getClass().getResourceAsStream("fonts/font.ttf"), 45);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Menu.fxml"));
        fxmlLoader.setController(this);
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }


    @FXML
    void initialize() {
        label_game.getStyleClass().add("red_label");
        label_game.setFont(font);
        label_about.setFont(font);
        label_exit.setFont(font);
        stage.addEventHandler(KeyEvent.KEY_PRESSED,down);
        stage.addEventHandler(KeyEvent.KEY_PRESSED,up);
        stage.addEventHandler(KeyEvent.KEY_PRESSED,enter);
    }

    EventHandler<KeyEvent> down = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.DOWN) {
                switch (number_choose) {
                    case 1:
                        number_choose = 2;
                        label_game.getStyleClass().remove("red_label");
                        label_about.getStyleClass().add("red_label");
                        break;
                    case 2:
                        number_choose = 3;
                        label_about.getStyleClass().remove("red_label");
                        label_exit.getStyleClass().add("red_label");
                        break;
                    case 3:
                        number_choose = 1;
                        label_exit.getStyleClass().remove("red_label");
                        label_game.getStyleClass().add("red_label");
                        break;
                }
            }
        }
    };

    EventHandler<KeyEvent> up = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.UP) {
                switch (number_choose) {
                    case 1:
                        number_choose = 3;
                        label_game.getStyleClass().remove("red_label");
                        label_exit.getStyleClass().add("red_label");
                        break;
                    case 2:
                        number_choose = 1;
                        label_about.getStyleClass().remove("red_label");
                        label_game.getStyleClass().add("red_label");
                        break;
                    case 3:
                        number_choose = 2;
                        label_exit.getStyleClass().remove("red_label");
                        label_about.getStyleClass().add("red_label");
                        break;
                }
            }
        }
    };

    EventHandler<KeyEvent> enter = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                switch (number_choose){
                    case 1:
                        try {
                         //   Stage stage = new Stage();
                         //   Menu.this.stage.close();
                            stage.removeEventHandler(KeyEvent.KEY_PRESSED,down);
                            stage.removeEventHandler(KeyEvent.KEY_PRESSED,up);
                            stage.removeEventHandler(KeyEvent.KEY_PRESSED,enter);
                            Connect connect = new Connect(stage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        break;
                    case 3:
                        Menu.this.stage.close();
                        break;
                }
            }
        }
    };
}
