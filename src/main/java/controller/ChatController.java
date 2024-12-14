package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ChatController {
    @FXML
    VBox messageBox;

    @FXML
    Button sendButton;

    @FXML
    TextField textField;

    private void sendMessage() {
        String message = textField.getText();
        if (!message.isEmpty()) {
            textField.clear();
            // send message
        }
    }

    @FXML
    private void setSendButton() {
        sendButton.setCursor(javafx.scene.Cursor.HAND);
        sendButton.setOnMouseClicked(e -> {
            sendMessage();
        });
    }

    @FXML
    public void initialize() {
        setSendButton();
    }
}
