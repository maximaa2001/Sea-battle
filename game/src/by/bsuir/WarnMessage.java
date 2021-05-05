package by.bsuir;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WarnMessage {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label label_message;

    @FXML
    private Button btn_close;

    private Stage stage;
    private String message;

    public WarnMessage(String message) throws IOException {
        this.message = message;
        stage = new Stage();
        stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/WarnMessage.fxml"));
        fxmlLoader.setController(this);
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Внимание");
        stage.show();
    }

    @FXML
    void initialize() {
        label_message.setText(this.message);
        btn_close.setOnMouseClicked(event -> {
            stage.close();
        });
    }
}
