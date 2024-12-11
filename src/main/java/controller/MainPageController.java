package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import model.LoggedInUser;

public class MainPageController {
    @FXML
    private HBox exploreYourShelves, explorePublicShelves;

    @FXML
    private ListView<String> listView;

    private final String[] recentlyReadBooks = {"Book 1", "Book 2", "Book 3", "Book 4", "Book 5"};

    private void addRecentlyReadBooks() {
        for (String book : recentlyReadBooks) {
            listView.getItems().add(book);
        }
    }

    private void setExploreYourShelves() {
        exploreYourShelves.setCursor(javafx.scene.Cursor.HAND);
        exploreYourShelves.setOnMouseClicked(event -> {
            if(LoggedInUser.isLoggedIn()) {
                CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                        new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml")));
            } else {
                CurrentView.showPopUp(new FXMLLoader(getClass().getResource("/fxml/logIn.fxml")));
            }
        });
    }

    private void setExplorePublicShelves() {
        explorePublicShelves.setCursor(javafx.scene.Cursor.HAND);
        explorePublicShelves.setOnMouseClicked(event -> {
            CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                    new FXMLLoader(getClass().getResource("/fxml/recommendedShelves.fxml")));
        });
    }

    @FXML
    public void initialize() {
        setExploreYourShelves();
        setExplorePublicShelves();
        addRecentlyReadBooks();
    }
}
