package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Message;

public class MessageController {
    private Message message;

    @FXML
    private Label usernameLabel, messageLabel;

    public void setMessage(Message message) {
        this.message = message;
        usernameLabel.setText(message.getSender());
        messageLabel.setText(message.getContent());
    }
}
