package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SignInController {

    @FXML
    private Button cherryUp;

    @FXML
    private TextField emailField, usernameField, passwordField, passwordAgainField;

    private void addSignInMechanishm(Button button) {
        button.setCursor(Cursor.HAND);
        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("Sign In!");
                // Close the sign-in popup
                Stage stage = (Stage) cherryUp.getScene().getWindow();
                stage.close();
                SidebarController.username = usernameField.getText();
                CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml")),
                        new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")));
            }
        });
    }

    @FXML
    void initialize() {
        addSignInMechanishm(cherryUp);
    }
}