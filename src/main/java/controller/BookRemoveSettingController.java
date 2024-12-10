package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class BookRemoveSettingController {
    @FXML
    private Label bookName;

    @FXML
    private ImageView deleteIcon;

    public void setBookName(String name) {
        bookName.setText(name);
    }

    private void setDeleteIcon() {
        deleteIcon.setCursor(javafx.scene.Cursor.HAND);
        deleteIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // delete book
            }
        });
    }

    @FXML
    public void initialize() {
        setDeleteIcon();
    }
}
