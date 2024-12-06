package org.openjfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SidebarController {

    static String username;

    @FXML
    private ImageView createShelfButton;

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

    @FXML
    public void initialize() {
        makeClickable(createShelfButton);
        makeClickable(seeRecommendedShelvesButton);
        makeClickable(seeYourShelvesButton);

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