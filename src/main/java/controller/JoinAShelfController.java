package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import model.LoggedInUser;

public class JoinAShelfController {

    @FXML
    Button JoinButton;

    @FXML
    TextField invitationCodeText;

    @FXML
    Label warningLabel;

    public void joinWithInvitationCode() {
        JoinButton.setCursor(javafx.scene.Cursor.HAND);
        JoinButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            boolean isJoined;
            @Override
            public void handle(MouseEvent mouseEvent) {
                isJoined = false;
                for(int i = 0; i < LoggedInUser.getAddedShelves().length; i++){
                    if(LoggedInUser.getAddedShelves()[i].getAdminInvitation().equals(invitationCodeText.getText())){
                        LoggedInUser.joinShelf(invitationCodeText.getText());
                        isJoined = true;
                    }
                }
                if(!isJoined){
                    warningLabel.setTextFill(Color.RED);
                    warningLabel.setText("Your invitation code is invalid.");
                }
            }
        });
    }

    public void initialize() {
        joinWithInvitationCode();
    }
}
