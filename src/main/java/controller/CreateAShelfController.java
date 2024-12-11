package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import model.LoggedInUser;
import model.Shelf;
import model.ShelfCollection;

public class CreateAShelfController {
    @FXML
    private TextField textField;

    @FXML
    private CheckBox isPublic, addBooks, annotate, comment, invite;

    @FXML
    private Button create;


    private void setCreate() {
        create.setOnAction(e -> {
            LoggedInUser.joinShelf(ShelfCollection.createShelf(
                    textField.getText(),
                    isPublic.isSelected(),
                    addBooks.isSelected(),
                    annotate.isSelected(),
                    comment.isSelected(),
                    invite.isSelected()
            ).getAdminInvitation());
            // close the window
            create.getScene().getWindow().hide();
        });
    }

    @FXML
    public void initialize() {
        setCreate();
    }
}
