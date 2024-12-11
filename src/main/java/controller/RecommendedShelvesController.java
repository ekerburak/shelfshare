package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.LoggedInUser;
import model.Shelf;
import model.ShelfCollection;

import java.io.IOException;

public class RecommendedShelvesController {

    @FXML
    private ListView<Pane> shelvesList;

    @FXML
    private TextField recommendedFilterField;

    private final ObservableList<Pane> items = FXCollections.observableArrayList();
    private final Shelf[] shelves = ShelfCollection.getRecommendedShelves(10).toArray(new Shelf[0]);

    @FXML
    public void initialize() {
        for (Shelf shelf : shelves) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecommendedShelfListElement.fxml"));
                Pane pane = loader.load();
                // Get the controller of the loaded FXML
                RecommendedShelfListElementController controller = loader.getController();
                // Set the shelf name label
                controller.setShelfName(shelf.getName());
                // Set the controller as user data for the pane
                pane.setUserData(controller);

                controller.getJoinButton().setCursor(javafx.scene.Cursor.HAND);
                controller.setJoinButtonAction(() -> {
                    if(LoggedInUser.isLoggedIn()) {
                        LoggedInUser.joinShelf(shelf.getStandardInvitation());
                        try {
                            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/shelf.fxml"));
                            Pane shelfPane = loader2.load();
                            ShelfController controller2 = loader2.getController();
                            controller2.setShelf(shelf);
                            controller2.setShelfName(shelf.getName());
                            CurrentView.updateView(
                                    new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                                    shelfPane
                            );
                        } catch (IOException ie) {
                            ie.printStackTrace();
                        }
                    } else {
                        CurrentView.showPopUp(new FXMLLoader(getClass().getResource("/fxml/login.fxml")));
                    }
                });
                items.add(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FilteredList<Pane> filteredItems = new FilteredList<>(items, p -> true);
        shelvesList.setItems(filteredItems);

        recommendedFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.setPredicate(pane -> {
                RecommendedShelfListElementController controller = (RecommendedShelfListElementController) pane.getUserData();
                return controller.getShelfName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }
}
