package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Book;

public class BookController {
    @FXML
    private Label bookName, uploaderName;

    private Book book;

    public void setBook(Book book) {
        this.book = book;
        setBookName(book.getName());
        setUploaderName(book.getUploaderName());
    }
    public void setBookName(String name) {
        bookName.setText(name);
    }
    public void setUploaderName(String name) {
        uploaderName.setText(name);
    }
    public String getBookName() {
        return book.getName();
    }
}
