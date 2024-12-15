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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.LoggedInUser;


import java.io.IOException;

public class SidebarProfilePartController {
    @FXML
    public ImageView settingsIcon, homeButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private HBox profileBox;

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

    private void profileBoxFunction() {
        profileBox.setCursor(Cursor.HAND);
        profileBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profilePage.fxml"));
                    Pane pane = loader.load();
                    ProfilePageController controller = loader.getController();
                    controller.setUser(LoggedInUser.getInstance());
                    CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                            pane);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setHomeButton() {
        homeButton.setCursor(javafx.scene.Cursor.HAND);
        homeButton.setOnMouseClicked(e -> {
            CurrentView.updateView(
                    new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                    new FXMLLoader(getClass().getResource("/fxml/mainPage.fxml"))
            );
        });
    }

    @FXML
    public void initialize() {
        openSettings();
        profileBoxFunction();
        setHomeButton();
    }
}
