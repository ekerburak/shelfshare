package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ForgotPasswordController {

    @FXML
    Button submit;

    @FXML
    TextField email;

    @FXML
    Label warning;

    @FXML
    ImageView back;

    private void setBackIcon() {
        back.setCursor(javafx.scene.Cursor.HAND);
        back.setOnMouseClicked(e -> {
            // close the register popup
            Stage stage = (Stage) back.getScene().getWindow();
            stage.close();
            // show the login popup
            CurrentView.showPopUp(
                    new FXMLLoader(getClass().getResource("/fxml/logIn.fxml"))
            );
        });
    }

    public void initialize() {
        setBackIcon();
    }

}
