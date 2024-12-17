package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    private Label joinLabel;

    @FXML
    private Label createAShelfLabel;

    @FXML
    private ImageView backIcon;

    private final ObservableList<Pane> items = FXCollections.observableArrayList();
    private Shelf[] shelves;

    private void setBackIcon() {
        backIcon.setCursor(javafx.scene.Cursor.HAND);
        backIcon.setOnMouseClicked(e -> {
            CurrentView.updateView(
                new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                new FXMLLoader(getClass().getResource("/fxml/mainPage.fxml"))
            );
        });
    }

    private void setCreateAShelfLabel() {
        createAShelfLabel.setCursor(javafx.scene.Cursor.HAND);
        createAShelfLabel.setOnMouseClicked(e -> {
            try {
                if(LoggedInUser.isLoggedIn()) {
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/createAShelf.fxml"));
                    Scene scene = new Scene(root);
                    Stage newStage = new Stage();
                    newStage.setScene(scene);
                    newStage.show();
                } else {
                    CurrentView.showPopUp(new FXMLLoader(getClass().getResource("/fxml/logIn.fxml")));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void openInvitation(){

        joinLabel.setCursor(javafx.scene.Cursor.HAND);
        joinLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                CurrentView.showPopUp(new FXMLLoader(getClass().getResource("/fxml/joinAShelf.fxml")));
            }
        });
    }


    @FXML
    public void initialize() {
        setBackIcon();
        setCreateAShelfLabel();
        openInvitation();

        if (LoggedInUser.isLoggedIn()) {
            shelves = LoggedInUser.getAddedShelves();
            System.out.println(shelves.length);
        } else {
            shelves = new Shelf[0];
        }

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

                pane.setCursor(javafx.scene.Cursor.HAND);
                pane.setOnMouseClicked(e -> {
                    try {
                        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/shelf.fxml"));
                        Pane shelfPane = loader2.load();
                        ShelfController controller2 = loader2.getController();
                        controller2.setShelf(shelf);
                        CurrentView.updateView(
                                new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                                shelfPane
                        );
                    } catch(IOException ie) {
                        ie.printStackTrace();
                    }
                });

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