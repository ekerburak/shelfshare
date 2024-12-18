package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.Book;
import model.Shelf;

public class BookController {
    @FXML
    private Label bookName, uploaderName;

    @FXML
    private VBox mainBox;

    @FXML
    private ImageView image;

    private Shelf shelf;

    private Book book;

    public void setBook(Book book) {
        this.book = book;
        setBookName(book.getName());
        setUploaderName(book.getUploaderName());
        System.out.println(book.getCoverImageOption());
        Image imageNew = new Image(getClass().getResource("/assets/cover" + book.getCoverImageOption() + ".png").toExternalForm());
        image.setImage(imageNew);

    }
    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
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

    @FXML
    public void initialize() {
        mainBox.setCursor(javafx.scene.Cursor.HAND);
        mainBox.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bookEditing.fxml"));
                Pane pane = loader.load();
                BookEditingController controller = loader.getController();
                controller.setShelf(shelf);
                controller.setBook(book);
                CurrentView.updateView(pane);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
