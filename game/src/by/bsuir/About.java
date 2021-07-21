package by.bsuir;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class About {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label label;

    @FXML
    private Label back_link;

    @FXML
    private ImageView imageview;

    private Stage stage;
    private Scene scene;
    private Font font;

    public About(Stage stage) throws IOException {
        this.stage = stage;
        stage.setWidth(1000);
        stage.setHeight(840);
        stage.setTitle("Об игре");
        font = Font.loadFont(getClass().getResourceAsStream("fonts/font.ttf"), 35);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/About.fxml"));
        fxmlLoader.setController(this);
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    void initialize() {
        label.setFont(font);
        back_link.setFont(font);
        imageview.setImage(new Image("by/bsuir/image/qr_code.png"));
        stage.addEventHandler(KeyEvent.KEY_PRESSED, esc);
    }

    EventHandler<KeyEvent> esc = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                try {
                    stage.close();
                    Menu menu = new Menu(new Stage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
