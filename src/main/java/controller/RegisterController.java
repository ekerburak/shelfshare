package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.LoggedInUser;
import model.User;
import model.UserCollection;

import java.awt.*;

public class RegisterController {

    @FXML
    private Button cherryUp;

    @FXML
    private TextField emailField, usernameField, passwordField, passwordAgainField;

    @FXML
    private Label registerStatusLabel;


    private void addRegisterMechanism() {
        cherryUp.setCursor(Cursor.HAND);
        cherryUp.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // Close the sign-in popup
                Stage stage = (Stage) cherryUp.getScene().getWindow();

                if(!emailField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    registerStatusLabel.setVisible(true);
                    registerStatusLabel.setText("Invalid email address!");
                    return;
                }

                if(!passwordField.getText().equals(passwordAgainField.getText())) {
                    registerStatusLabel.setVisible(true);
                    registerStatusLabel.setText("Passwords do not match!");
                    return;
                }

                boolean registerSuccessful = UserCollection.signUp(emailField.getText(), usernameField.getText(), passwordField.getText());

                if(!registerSuccessful) {
                    registerStatusLabel.setVisible(true);
                    registerStatusLabel.setText("Username or email already in use!");
                    return;
                }

                UserCollection.logIn(emailField.getText(), passwordField.getText());
                stage.close();

                CurrentView.updateView(
                        new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                        new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml"))
                );
            }
        });
    }

    @FXML
    void initialize() {
        addRegisterMechanism();
    }
}