package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
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
        create.setCursor(javafx.scene.Cursor.HAND);
        create.setOnAction(e -> {
            Shelf shelf = ShelfCollection.createShelf(
                    textField.getText(),
                    isPublic.isSelected(),
                    addBooks.isSelected(),
                    annotate.isSelected(),
                    comment.isSelected(),
                    invite.isSelected()
            );
            // close the window
            create.getScene().getWindow().hide();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shelf.fxml"));
                Pane shelfPane = loader.load();
                ShelfController controller = loader.getController();
                controller.setShelf(shelf);
                CurrentView.updateView(
                        new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                        shelfPane
                );
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    @FXML
    public void initialize() {
        setCreate();
    }
}
