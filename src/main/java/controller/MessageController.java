package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MessageController {
    @FXML
    private Label usernameLabel, messageLabel;

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}
