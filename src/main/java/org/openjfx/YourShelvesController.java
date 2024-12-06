package org.openjfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class YourShelvesController {

    @FXML
    private ListView<Pane> shelvesList;

    @FXML
    private TextField filterField;

    @FXML
    private SplitPane sidebarSplitPane;

    private final ObservableList<Pane> items = FXCollections.observableArrayList();
    private final String[] shelves = {"Ahmet", "Burak", "Ahmet", "Burak", "Ahmet", "Burak", "Ahmet", "Burak"};

    @FXML
    public void initialize() {
        for (String shelf : shelves) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shelf_list_element.fxml"));
                Pane pane = loader.load();

                // Get the controller of the loaded FXML
                ShelfListElementController controller = loader.getController();
                // Set the shelf name label
                controller.setShelfName(shelf);

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