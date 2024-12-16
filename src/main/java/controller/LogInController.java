package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.LoggedInUser;
import model.UserCollection;

import java.io.IOException;

public class LogInController {

    @FXML
    private Button cherryIn;

    @FXML
    private Button joinNow;

    @FXML
    private TextField emailField, passwordField;

    @FXML
    private Label warningLabel;


    private void addRegisterPopUp(Button button) {
        button.setCursor(Cursor.HAND);
        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    // Close the current stage (login popup)
                    Stage currentStage = (Stage) button.getScene().getWindow();
                    currentStage.close();

                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));

                    Scene scene = new Scene(root);
                    Stage newStage = new Stage();

                    newStage.setScene(scene);
                    newStage.show();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void logInAction() {
        boolean successfulLogin = UserCollection.logIn(emailField.getText(), passwordField.getText());

        if (!successfulLogin) {
            warningLabel.setVisible(true);
            return;
        }
        // Close the sign-in popup
        Stage stage = (Stage) cherryIn.getScene().getWindow();
        stage.close();

        CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                new FXMLLoader(getClass().getResource("/fxml/mainPage.fxml")));
    }

    private void addLogInMechanism() {
        cherryIn.setCursor(Cursor.HAND);
        cherryIn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
               logInAction();
            }
        });
        passwordField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode().toString().equals("ENTER")) {
                    logInAction();
                }
            }
        });
    }

    @FXML
    public void initialize() {
        addRegisterPopUp(joinNow);
        addLogInMechanism();
    }

}

