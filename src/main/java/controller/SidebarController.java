package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.LoggedInUser;
import model.Shelf;
import model.ShelfCollection;

import java.io.IOException;

public class SidebarController {

    @FXML
    private ImageView seeRecommendedShelvesButton;

    @FXML
    private ImageView seeYourShelvesButton;

    @FXML
    private VBox sidebarVBox;

    @FXML
    private VBox yourShelvesPart;

    @FXML
    private SplitPane splitPane;

    @FXML
    private ListView<String> recommendedShelvesList, yourShelvesList;

    private void openRecommendedPage(ImageView button) {
        button.setCursor(Cursor.HAND);
        button.setOnMouseClicked(event ->  CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                new FXMLLoader(getClass().getResource("/fxml/recommendedShelves.fxml"))));
    }
    private void openYourShelvesPage(ImageView button) {
        button.setCursor(Cursor.HAND);
        button.setOnMouseClicked(event ->  CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml"))));
    }
    private void setYourShelves() {
        Shelf[] shelves = LoggedInUser.getAddedShelves();
        for (Shelf shelf : shelves) {
            yourShelvesList.getItems().add(shelf.getName());
        }
        yourShelvesList.setCursor(Cursor.HAND);
        yourShelvesList.setOnMouseClicked(event -> {
            String selectedShelfName = yourShelvesList.getSelectionModel().getSelectedItem();
            Shelf shelf = null;

            for (Shelf s : shelves) {
                if (s.getName().equals(selectedShelfName)) {
                    shelf = s;
                    break;
                }
            }
            if (selectedShelfName != null) {
                try {
                    FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/shelf.fxml"));
                    Pane shelfPane = loader2.load();
                    ShelfController controller2 = loader2.getController();
                    controller2.setShelf(shelf);
                    CurrentView.updateView(
                            new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                            shelfPane
                    );
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setRecommendedShelves() {
        Shelf[] shelves = ShelfCollection.getRecommendedShelves(10).toArray(new Shelf[0]);
        for (Shelf shelf : shelves) {
            recommendedShelvesList.getItems().add(shelf.getName());
        }

        recommendedShelvesList.setCursor(Cursor.HAND);
        recommendedShelvesList.setOnMouseClicked(event -> {
        CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                new FXMLLoader(getClass().getResource("/fxml/recommendedShelves.fxml")));
        });
    }

    @FXML
    public void initialize() {
        openRecommendedPage(seeRecommendedShelvesButton);
        setRecommendedShelves();
        try {
            if(!LoggedInUser.isLoggedIn()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sidebarSignUpPart.fxml"));
                Pane profilePart = loader.load();
                sidebarVBox.getChildren().add(profilePart);
                yourShelvesPart.setVisible(false);
                yourShelvesPart.setManaged(false);
                splitPane.setDividerPositions(0.0);
                Node divider = splitPane.lookup(".split-pane-divider");
                if(divider!=null){
                    divider.setStyle("-fx-padding: 0;");
                }
            } else {
                openYourShelvesPage(seeYourShelvesButton);
                setYourShelves();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sidebarProfilePart.fxml"));
                Pane profilePart = loader.load();
                SidebarProfilePartController controller = loader.getController();
                controller.setUsername(LoggedInUser.getInstance().getUsername());
                sidebarVBox.getChildren().add(profilePart);
                yourShelvesPart.setManaged(true);
                yourShelvesPart.setVisible(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}