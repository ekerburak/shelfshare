package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class ShelfListElementController {

    @FXML
    private Label shelfNameLabel;

    @FXML
    private ImageView firstStar, secondStar, thirdStar, forthStar, fifthStar;
    ImageView[] stars = {firstStar, secondStar, thirdStar, forthStar, fifthStar};

    public void setShelfName(String name) {
        shelfNameLabel.setText(name);
    }

    public String getShelfName() {
        return shelfNameLabel.getText();
    }

    public void setImage(ImageView star, int fill) {
        try {
            Image fullStarImage = new Image(getClass().getResourceAsStream("/assets/fullStar.png"));

            // Assuming star is an ImageView
            if (fill == 1){
                star.setImage(fullStarImage);
            }
//            else if (fill == 0){
//                star.setImage(emptyStarImage);
//            }
        } catch (NullPointerException e) {
            System.out.println("Image not found: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void setStars(int rating){

        System.out.println(rating);
        for(int i = 0; i<5; i++){
            if(i < rating)
                setImage(stars[i], 1);
            else
                setImage(stars[i], 0);
        }
    }

    public void initialize() {
        setStars(RateShelfController.getRating());
    }
}