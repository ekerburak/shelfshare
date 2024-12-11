package controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import javafx.scene.Cursor;
import javafx.stage.Stage;
import model.LoggedInUser;

public class SettingsController {

    @FXML
    public Button logOutButton;


    private void logOut() {
        LoggedInUser.logOut();
        ((Stage)logOutButton.getScene().getWindow()).close();
        CurrentView.updateView(
                new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                new FXMLLoader(getClass().getResource("/fxml/mainPage.fxml"))
        );
    }

    @FXML
    public void logOutFunction(){
        logOutButton.setCursor(Cursor.HAND);
        logOutButton.setOnMouseClicked(e -> {
            logOut();
        });
    }
    @FXML
    public void initialize() {
        logOutFunction();
    }
}
