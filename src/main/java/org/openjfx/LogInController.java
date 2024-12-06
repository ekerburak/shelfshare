package org.openjfx;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;

public class LogInController {

    @FXML
    private Button cherryIn;

    @FXML
    private Button joinNow;

    @FXML
    private TextField usernameField, passwordField;

    private void addSignInPopUp(Button button) {
        button.setCursor(Cursor.HAND);
        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    // Close the current stage (login popup)
                    Stage currentStage = (Stage) button.getScene().getWindow();
                    currentStage.close();

                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/signIn.fxml"));

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

    private void addLogInMechanishm(Button button) {
        button.setCursor(Cursor.HAND);
        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // Close the current stage (login popup)
                Stage currentStage = (Stage) button.getScene().getWindow();
                currentStage.close();

                System.out.println("Log in!");
                // Close the sign-in popup
                Stage stage = (Stage) cherryIn.getScene().getWindow();
                stage.close();
                SidebarController.username = usernameField.getText();
                CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml")),
                        new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")));
            }
        });
    }

    @FXML
    public void initialize() {
        addSignInPopUp(joinNow);
        addLogInMechanishm(cherryIn);
    }

}

