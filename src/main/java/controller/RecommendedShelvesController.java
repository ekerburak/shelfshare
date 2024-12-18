package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    @FXML
    private Button ratingSort, userSort;

    @FXML
    private ImageView backIcon;

    private final ObservableList<Pane> items = FXCollections.observableArrayList();
    private final Shelf[] shelves = ShelfCollection.getRecommendedShelves(10).toArray(new Shelf[0]);

    private void setBackIcon() {
        backIcon.setCursor(javafx.scene.Cursor.HAND);
        backIcon.setOnMouseClicked(e -> {
            CurrentView.updateView(
                    new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                    new FXMLLoader(getClass().getResource("/fxml/mainPage.fxml"))
            );
        });
    }
    private void setRatingSort() {
        ratingSort.setCursor(javafx.scene.Cursor.HAND);
        ratingSort.setOnMouseClicked(e -> {
           items.sort((pane1, pane2) -> {
                RecommendedShelfListElementController controller1 = (RecommendedShelfListElementController) pane1.getUserData();
                RecommendedShelfListElementController controller2 = (RecommendedShelfListElementController) pane2.getUserData();
                return Float.compare(controller2.getRating(), controller1.getRating());
            });
        });
    }

    private void setUserSort() {
        userSort.setCursor(javafx.scene.Cursor.HAND);
        userSort.setOnMouseClicked(e -> {
           items.sort((pane1, pane2) -> {
                RecommendedShelfListElementController controller1 = (RecommendedShelfListElementController) pane1.getUserData();
                RecommendedShelfListElementController controller2 = (RecommendedShelfListElementController) pane2.getUserData();
                return Integer.compare(controller2.getNumberOfMember(), controller1.getNumberOfMember());
            });
        });
    }

    @FXML
    public void initialize() {
        setBackIcon();
        setRatingSort();
        setUserSort();
        for (Shelf shelf : shelves) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecommendedShelfListElement.fxml"));
                Pane pane = loader.load();
                // Get the controller of the loaded FXML
                RecommendedShelfListElementController controller = loader.getController();
                // Set the shelf name label
                controller.setShelfName(shelf.getName());
                controller.setRating("" + shelf.getPopularity());
                controller.setNumberOfMember("" + shelf.getParticipantsIDs().size());
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
