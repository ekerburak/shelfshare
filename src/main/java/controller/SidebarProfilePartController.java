package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class SidebarProfilePartController {
    @FXML
    private ImageView settingsButton;

    @FXML
    private Label usernameLabel;

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }
}
