package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ShelfSettingsController {
    @FXML
    private Label shelfName;

    @FXML
    private Button save, leave;

    @FXML
    private TextField nameField;

    @FXML
    private CheckBox makePublic, addBooks, annotate, comment, invite;

    @FXML
    private ListView listView;

    private final ObservableList<Pane> items = FXCollections.observableArrayList();
    private final String[] books = {"lotr", "cancna", "tutku"};

    private void setShelfName(String name) {
        shelfName.setText(name);
    }

    @FXML
    public void initialize() {
        setShelfName("My Shelf");
        save.setCursor(javafx.scene.Cursor.HAND);
        leave.setCursor(javafx.scene.Cursor.HAND);
        makePublic.setCursor(javafx.scene.Cursor.HAND);
        addBooks.setCursor(javafx.scene.Cursor.HAND);
        annotate.setCursor(javafx.scene.Cursor.HAND);
        comment.setCursor(javafx.scene.Cursor.HAND);
        invite.setCursor(javafx.scene.Cursor.HAND);


        for (String book : books) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bookRemoveSetting.fxml"));
                Pane pane = loader.load();
                // Get the controller of the loaded FXML
                BookRemoveSettingController controller = loader.getController();
                // Set the shelf name label
                controller.setBookName(book);
                // Set the controller as user data for the pane
                pane.setUserData(controller);
                items.add(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        listView.setItems(items);
    }
}
