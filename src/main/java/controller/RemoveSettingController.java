package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class RemoveSettingController {
    @FXML
    private Label name;

    @FXML
    private ImageView deleteIcon;

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setDeleteIcon(Runnable runnable) {
        deleteIcon.setCursor(javafx.scene.Cursor.HAND);
        deleteIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                runnable.run();
            }
        });
    }
}
