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
import model.Shelf;

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

                LoggedInUser.joinShelf(invitationCodeText.getText());
                // TODO: check if the invitation code is valid
                isJoined = true;

                if(!isJoined){
                    warningLabel.setTextFill(Color.RED);
                    warningLabel.setText("Your invitation code is invalid.");
                } else {
                    // close the window
                    warningLabel.getScene().getWindow().hide();
                    // update the view
                    CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                            new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml")));
                }
            }
        });
    }

    public void initialize() {
        joinWithInvitationCode();
    }
}
