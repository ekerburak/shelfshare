package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.Book;
import model.LoggedInUser;
import model.Shelf;
import model.ShelfCollection;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;

public class ShelfSettingsController {
    Shelf shelf;

    @FXML
    private Label shelfName;

    @FXML
    private Button save, deleteShelf;

    @FXML
    private TextField nameField;

    @FXML
    private CheckBox makePublic, addBooks, annotate, comment, invite;

    @FXML
    private ListView listView;

    @FXML
    private ImageView addPersonIcon;


    private final ObservableList<Pane> items = FXCollections.observableArrayList();

    private final ArrayList<String> booksToDelete = new ArrayList<>();

    private void setShelfName(String name) {
        shelfName.setText(name);
    }

    private void setNameField(String name) {
        nameField.setText(name);
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

            for (String book : booksToDelete) {
                shelf.deleteBook(new ObjectId(book));
            }

            // close the settings
            save.getScene().getWindow().hide();
            // open the shelf

            try {
            FXMLLoader shelfLoader = new FXMLLoader(getClass().getResource("/fxml/shelf.fxml"));
            Pane shelf = shelfLoader.load();
            ShelfController controller = shelfLoader.getController();
            controller.setShelf(this.shelf);
            CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")), shelf);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void listBooks() {
        // clear the list view
        listView.getItems().clear();

        ArrayList<Book> books = shelf.getBooks();
        for (Book book : books) {
            if (booksToDelete.contains(book.getID().toString())) {
                continue;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bookRemoveSetting.fxml"));
                Pane pane = loader.load();
                // Get the controller of the loaded FXML
                BookRemoveSettingController controller = loader.getController();
                // Set the shelf name label
                controller.setShelf(shelf);
                controller.setBook(book);
                controller.setDeleteIcon(() -> {
                    booksToDelete.add(book.getID().toString());
                    listBooks();
                });
                // Set the controller as user data for the pane
                pane.setUserData(controller);
                items.add(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        listView.setItems(items);
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
        listBooks();
    }

    private void setAddPersonIcon() {
        addPersonIcon.setCursor(javafx.scene.Cursor.HAND);
        addPersonIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/memberRemoveSettings.fxml"));
                    Pane root = loader.load();

                    memberRemoveController controller = loader.getController();
                    CurrentView.showPopUp(root);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    public void initialize() {
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
