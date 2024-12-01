package org.openjfx;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class LoggedInSidebarController {

    @FXML
    private ImageView createShelfButton;

    @FXML
    private ImageView seeRecommendedShelvesButton;

    @FXML
    private ImageView seeYourShelvesButton;

    @FXML
    private ImageView settingsButton;

    @FXML
    private Label usernameLabel;


    private void makeClickable(ImageView button) {
        button.setCursor(Cursor.HAND);
        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Clicked");
            }
        });
    }

    @FXML
    public void initialize() {
        makeClickable(createShelfButton);
        makeClickable(seeRecommendedShelvesButton);
        makeClickable(seeYourShelvesButton);
        makeClickable(settingsButton);
        usernameLabel.setText("burak");
    }

}
