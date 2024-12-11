package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


import java.io.IOException;

public class SidebarProfilePartController {
    @FXML
    public ImageView settingsIcon;

    @FXML
    private Label usernameLabel;

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }

    private void openSettings() {
        settingsIcon.setCursor(Cursor.HAND);
        settingsIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                CurrentView.showPopUp(new FXMLLoader(getClass().getResource("/fxml/setting.fxml")));
            }
        });
    }
    @FXML
    public void initialize() {
        openSettings();
    }
}
