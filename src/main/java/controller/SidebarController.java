package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.LoggedInUser;

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

    private void makeClickable(ImageView button) {
        button.setCursor(Cursor.HAND);
        button.setOnMouseClicked(event -> System.out.println("Clicked"));
    }
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

    @FXML
    public void initialize() {
        openRecommendedPage(seeRecommendedShelvesButton);

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