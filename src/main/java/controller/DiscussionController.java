package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.Book;

import java.io.IOException;

public class DiscussionController {
    private Book book;

    @FXML
    private Label bookName;

    @FXML
    private VBox mainBox;

    public void setBook(Book book) {
        this.book = book;
        bookName.setText(book.getName());
        // add chat.fxml to mainBox
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chat.fxml"));
            Pane pane = loader.load();
            ChatController controller = loader.getController();
            controller.setChat(book.getDiscussionChat());
            mainBox.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
