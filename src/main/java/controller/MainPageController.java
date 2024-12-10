package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

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
            CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                    new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml")));
        });
    }

    private void setExplorePublicShelves() {
        explorePublicShelves.setCursor(javafx.scene.Cursor.HAND);
        explorePublicShelves.setOnMouseClicked(event -> {
            CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                    new FXMLLoader(getClass().getResource("/fxml/publicShelves.fxml")));
        });
    }

    @FXML
    public void initialize() {
        setExploreYourShelves();
        addRecentlyReadBooks();
        setExplorePublicShelves();
    }
}
