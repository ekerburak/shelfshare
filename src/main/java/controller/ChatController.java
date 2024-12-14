package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.Chat;
import model.ChatListener;
import model.Message;

import java.util.ArrayList;

public class ChatController implements ChatListener {
    private Chat chat;
    private ArrayList<Message> messages;

    @FXML
    VBox messageBox;

    @FXML
    Button sendButton;

    @FXML
    TextField textField;

    public void setChat(Chat chat) {
        this.chat = chat;

        messages = chat.getAllMessages();
        for (Message message : messages) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/message.fxml"));
                Pane pane = loader.load();
                MessageController controller = loader.getController();
                controller.setMessage(message);
                messageBox.getChildren().add(pane);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        chat.addChatListener(this);
        chat.startListening();
    }

    private void sendMessage() {
        String message = textField.getText();
        if (!message.isEmpty()) {
            textField.clear();
            chat.sendMessage(message);
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

    @Override
    public void onIncomingMessage(Message msg) {
        Platform.runLater(() -> {
            messages.add(msg);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/message.fxml"));
                Pane pane = loader.load();
                MessageController controller = loader.getController();
                controller.setMessage(msg);
                messageBox.getChildren().add(pane);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
