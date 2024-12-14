package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Book;
import model.Shelf;

public class BookRemoveSettingController {
    private Shelf shelf;
    private Book book;

    @FXML
    private Label bookName;

    @FXML
    private ImageView deleteIcon;

    private void setBookName(String name) {
        bookName.setText(name);
    }

    public void setBook(Book book) {
        this.book = book;
        setBookName(book.getName());
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }

    private void setDeleteIcon() {
        deleteIcon.setCursor(javafx.scene.Cursor.HAND);
        deleteIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                shelf.removeBook(book.getID());
                deleteIcon.getScene().getWindow().hide();
            }
        });
    }

    @FXML
    public void initialize() {
        setDeleteIcon();
    }
}
