package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.User;

public class ProfilePageController {
    @FXML
    private Label username, about, email;

    @FXML
    private ImageView profilePicture;

    public void setUser(User user) {
        username.setText(user.getUsername());
        about.setText(user.getAbout());
        email.setText(user.getEmail());

        System.out.println("/assets/kedi" + user.getProfilePictureOption() + ".png");
        Image image = new Image(getClass().getResourceAsStream("/assets/kedi" + user.getProfilePictureOption() + ".png"));
        profilePicture.setImage(image);
    }

    public void initialize() {

    }
}
