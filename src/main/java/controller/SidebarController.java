package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SidebarController {

    static String username;

    @FXML
    private ImageView seeRecommendedShelvesButton;

    @FXML
    private ImageView seeYourShelvesButton;

    @FXML
    private VBox sidebarVBox;

    private void makeClickable(ImageView button) {
        button.setCursor(Cursor.HAND);
        button.setOnMouseClicked(event -> System.out.println("Clicked"));
    }
    private void openRecommendedPage(ImageView button) {
        button.setCursor(Cursor.HAND);
        button.setOnMouseClicked(event ->  CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                new FXMLLoader(getClass().getResource("/fxml/RecommendedShelves.fxml"))));
    }
    private void openYourShelvesPage(ImageView button) {
        button.setCursor(Cursor.HAND);
        button.setOnMouseClicked(event ->  CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml"))));
    }

    @FXML
    public void initialize() {
        openRecommendedPage(seeRecommendedShelvesButton);
        openYourShelvesPage(seeYourShelvesButton);

        try {
            if(username == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sidebarSignUpPart.fxml"));
                Pane profilePart = loader.load();
                sidebarVBox.getChildren().add(profilePart);
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sidebarProfilePart.fxml"));
                Pane profilePart = loader.load();
                SidebarProfilePartController controller = loader.getController();
                controller.setUsername(username);
                sidebarVBox.getChildren().add(profilePart);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}