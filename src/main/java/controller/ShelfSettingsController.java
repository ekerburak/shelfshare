package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import model.Book;
import model.LoggedInUser;
import model.Shelf;
import model.ShelfCollection;

import java.io.IOException;
import java.util.ArrayList;

public class ShelfSettingsController {
    Shelf shelf;

    @FXML
    private Label shelfName;

    @FXML
    private Button save, leave, deleteShelf;

    @FXML
    private TextField nameField;

    @FXML
    private CheckBox makePublic, addBooks, annotate, comment, invite;

    @FXML
    private ListView listView;

    private final ObservableList<Pane> items = FXCollections.observableArrayList();

    private void setShelfName(String name) {
        shelfName.setText(name);
    }

    private void setNameField(String name) {
        nameField.setText(name);
    }

    private void setLeave() {
        leave.setCursor(javafx.scene.Cursor.HAND);
        leave.setOnMouseClicked(mouseEvent -> {
            LoggedInUser.leaveShelf(shelf);
            // close the settings
            leave.getScene().getWindow().hide();
            // open your shelves back
            CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                    new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml")));
        });
    }

    private void setSave() {
        save.setCursor(javafx.scene.Cursor.HAND);
        save.setOnMouseClicked(mouseEvent -> {
            shelf.setName(nameField.getText());
            shelf.setIsPublic(makePublic.isSelected());
            shelf.setAllowBookAdd(addBooks.isSelected());
            shelf.setAllowBookAnnotate(annotate.isSelected());
            shelf.setAllowDiscussion(comment.isSelected());
            shelf.setAllowInvitation(invite.isSelected());
            // close the settings
            save.getScene().getWindow().hide();
        });
    }

    private void setDeleteShelf() {
        deleteShelf.setCursor(javafx.scene.Cursor.HAND);
        deleteShelf.setOnMouseClicked(mouseEvent -> {
            ShelfCollection.deleteShelf(shelf.getID());
            // close the settings
            deleteShelf.getScene().getWindow().hide();
            // open your shelves back
            CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                    new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml")));
        });
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
        setShelfName(shelf.getName());
        setNameField(shelf.getName());
        makePublic.setSelected(shelf.getIsPublic());
        addBooks.setSelected(shelf.getAllowBookAdd());
        annotate.setSelected(shelf.getAllowBookAnnotate());
        comment.setSelected(shelf.getAllowDiscussion());
        invite.setSelected(shelf.getAllowInvitation());

        ArrayList<Book> books = shelf.getBooks();

        for (Book book : books) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bookRemoveSetting.fxml"));
                Pane pane = loader.load();
                // Get the controller of the loaded FXML
                BookRemoveSettingController controller = loader.getController();
                // Set the shelf name label
                controller.setShelf(shelf);
                controller.setBook(book);
                // Set the controller as user data for the pane
                pane.setUserData(controller);
                items.add(pane);
                listView.setItems(items);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void initialize() {
        setLeave();
        setSave();
        setDeleteShelf();
        save.setCursor(javafx.scene.Cursor.HAND);
        makePublic.setCursor(javafx.scene.Cursor.HAND);
        addBooks.setCursor(javafx.scene.Cursor.HAND);
        annotate.setCursor(javafx.scene.Cursor.HAND);
        comment.setCursor(javafx.scene.Cursor.HAND);
        invite.setCursor(javafx.scene.Cursor.HAND);
    }
}
