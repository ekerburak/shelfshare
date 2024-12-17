package controller;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Shelf;

public class RateShelfController {
    Shelf shelf;

    @FXML
    ImageView firstStar, secondStar, thirdStar, forthStar, fifthStar;

    @FXML
    Label shelfName;

    @FXML
    Button doneButton;

    static int rating = 0;

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
        shelfName.setText(shelf.getName());
    }

    public static int getRating() {
        return rating;
    }
    public void rating(ImageView imageView){
        if(imageView == firstStar){

            setImage(firstStar,1);
            setImage(secondStar,0);
            setImage(thirdStar,0);
            setImage(forthStar,0);
            setImage(fifthStar,0);
            rating = 1;
        }
        else if(imageView == secondStar){

            setImage(firstStar,1);
            setImage(secondStar,1);
            setImage(thirdStar,0);
            setImage(forthStar,0);
            setImage(fifthStar,0);
            rating = 2;
        }
        else if(imageView == thirdStar){

            setImage(firstStar,1);
            setImage(secondStar,1);
            setImage(thirdStar,1);
            setImage(forthStar,0);
            setImage(fifthStar,0);
            rating = 3;
        }
        else if(imageView == forthStar){

            setImage(firstStar,1);
            setImage(secondStar,1);
            setImage(thirdStar,1);
            setImage(forthStar,1);
            setImage(fifthStar,0);
            rating = 4;
        }
        else if(imageView == fifthStar){

            setImage(firstStar,1);
            setImage(secondStar,1);
            setImage(thirdStar,1);
            setImage(forthStar,1);
            setImage(fifthStar,1);
            rating = 5;
        }
    }

    public void rateAShelf(){
        firstStar.setCursor(Cursor.HAND);
        firstStar.setOnMouseClicked(e -> {
            rating(firstStar);
        });
        secondStar.setCursor(Cursor.HAND);
        secondStar.setOnMouseClicked(e -> {
            rating(secondStar);
        });
        thirdStar.setCursor(Cursor.HAND);
        thirdStar.setOnMouseClicked(e -> {
            rating(thirdStar);
        });
        forthStar.setCursor(Cursor.HAND);
        forthStar.setOnMouseClicked(e -> {
            rating(forthStar);
        });
        fifthStar.setCursor(Cursor.HAND);
        fifthStar.setOnMouseClicked(e -> {
            rating(fifthStar);
        });
    }

    private void setDoneButton() {
        doneButton.setCursor(Cursor.HAND);
        doneButton.setOnMouseClicked(e -> {
            // send rating
            if(!shelf.rateShelf(rating)) {
                // send message to the user
                Alert alert = new Alert(Alert.AlertType.ERROR, "You have already rated this shelf");
                alert.showAndWait();
            }
            // close window after rating
            doneButton.getScene().getWindow().hide();
        });
    }
    public void setImage(ImageView star, int fill) {
        try {
            Image fullStarImage = new Image(getClass().getResourceAsStream("/assets/fullStar.png"));
            Image emptyStarImage = new Image(getClass().getResourceAsStream("/assets/emptyStar.png"));

            // Assuming star is an ImageView
            if (fill == 1){
                star.setImage(fullStarImage);
            }
            else if (fill == 0){
                star.setImage(emptyStarImage);
            }
        } catch (NullPointerException e) {
            System.out.println("Image not found: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initialize(){
        setDoneButton();
        rateAShelf();
    }
}
