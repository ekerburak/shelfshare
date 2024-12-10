package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BookController {
    @FXML
    private Label bookName, uploaderName;

    public void setBookName(String name) {
        bookName.setText(name);
    }
    public void setUploaderName(String name) {
        uploaderName.setText(name);
    }

    public String getBookName() {
        return bookName.getText();
    }
}
