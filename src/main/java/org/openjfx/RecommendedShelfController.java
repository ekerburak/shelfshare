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

public class RecommendedShelfController {

    @FXML
    private ListView<Pane> recommendedShelvesList;

    @FXML
    private TextField recommendedFilterField;

    @FXML
    private SplitPane sidebarSplitPane;

    private final ObservableList<Pane> items = FXCollections.observableArrayList();
    private final String[] shelves = {"Ahmet", "Burak", "Can", "Sevval", "Ahmet", "Toprak", "Volkan", "Sinan"};

    @FXML
    public void initialize() {
        for (String shelf : shelves) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecommendedShelfListElement.fxml"));
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
        recommendedShelvesList.setItems(filteredItems);

        recommendedFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.setPredicate(pane -> {
                ShelfListElementController controller = (ShelfListElementController) pane.getUserData();
                return controller.getShelfName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }
}
