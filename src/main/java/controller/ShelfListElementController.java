package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ShelfListElementController {
    @FXML
    private Label shelfNameLabel;

    public void setShelfName(String name) {
        shelfNameLabel.setText(name);
    }

    public String getShelfName() {
        return shelfNameLabel.getText();
    }
}