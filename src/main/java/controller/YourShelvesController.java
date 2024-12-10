package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.LoggedInUser;
import model.Shelf;

import java.io.IOException;

public class YourShelvesController {

    @FXML
    private ListView<Pane> shelvesList;

    @FXML
    private TextField filterField;

    @FXML
    private Label createAShelfLabel;

    private final ObservableList<Pane> items = FXCollections.observableArrayList();
    private Shelf[] shelves;

    private void setCreateAShelfLabel() {
        createAShelfLabel.setCursor(javafx.scene.Cursor.HAND);
        createAShelfLabel.setOnMouseClicked(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/createAShelf.fxml"));
                Scene scene = new Scene(root);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    public void initialize() {
        setCreateAShelfLabel();

        shelves = LoggedInUser.getAddedShelves();


        for (Shelf shelf : shelves) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shelf_list_element.fxml"));
                Pane pane = loader.load();
                // Get the controller of the loaded FXML
                ShelfListElementController controller = loader.getController();
                // Set the shelf name label
                controller.setShelfName(shelf.getName());
                // Set the controller as user data for the pane
                pane.setUserData(controller);
                items.add(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FilteredList<Pane> filteredItems = new FilteredList<>(items, p -> true);
        shelvesList.setItems(filteredItems);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.setPredicate(pane -> {
                ShelfListElementController controller = (ShelfListElementController) pane.getUserData();
                return controller.getShelfName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }
}