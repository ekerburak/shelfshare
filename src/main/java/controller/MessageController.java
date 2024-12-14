package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.LoggedInUser;
import model.Message;

public class MessageController {
    private Message message;

    @FXML
    private Label usernameLabel, messageLabel;

    public void setMessage(Message message) {
        this.message = message;
        usernameLabel.setText(message.getSender());
        if(LoggedInUser.getInstance().getUsername().equals(message.getSender())) {
            usernameLabel.setStyle("-fx-text-fill: #00a8cc");
        }
        messageLabel.setText(message.getContent());
    }
}
