package controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import javafx.scene.Cursor;
public class SettingsController {

    @FXML
    static Button logOutButton;

    static boolean isClicked = false;

    @FXML
    public void logOutFunction(){
        logOutButton.setCursor(Cursor.HAND);
        logOutButton.setOnMouseClicked(event ->  CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                new FXMLLoader(getClass().getResource("/fxml/RecommendedShelves.fxml"))));
    }
    @FXML
    public void initialize() {
        logOutFunction();
//        try {
//            if() {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sidebarSignUpPart.fxml"));
//                Pane profilePart = loader.load();
//                SidebarController.sidebarVBox.getChildren().add(profilePart);
//            } else {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sidebarProfilePart.fxml"));
//                Pane profilePart = loader.load();
//                SidebarProfilePartController controller = loader.getController();
//                controller.setUsername(SidebarController.username);
//                SidebarController.sidebarVBox.getChildren().add(profilePart);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
