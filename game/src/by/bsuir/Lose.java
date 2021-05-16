package by.bsuir;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Lose {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btn_close;

    @FXML
    private Label label_lose;

    private Stage stage;
    private Scene scene;
    private Font font;

    public Lose(Stage stage) throws IOException {
        this.stage = stage;
        stage.setWidth(600);
        stage.setHeight(330);
        stage.setTitle("Поражение");
        font = Font.loadFont(getClass().getResourceAsStream("fonts/font.ttf"), 96);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Lose.fxml"));
        fxmlLoader.setController(this);
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    void initialize() {
        label_lose.setFont(font);
        btn_close.setOnMouseClicked(event -> {
            try {
                Menu menu = new Menu(stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}