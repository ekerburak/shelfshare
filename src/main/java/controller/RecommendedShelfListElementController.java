package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class RecommendedShelfListElementController {

    @FXML
    private Label shelfName;
    @FXML
    private Label numberOfMember, rating;
    @FXML
    private Button joinButton;

    public void setShelfName(String name) {
        shelfName.setText(name);
    }

    public String getShelfName() {
        return shelfName.getText();
    }

    public void setNumberOfMember(String number) {
        numberOfMember.setText(number);
    }

    public void setRating(String rate) { rating.setText(rate); }

    public Button getJoinButton() {
        return joinButton;
    }

    public void setJoinButtonAction(Runnable action) {
        joinButton.setOnAction(event -> action.run());
    }

    public void makeClickable(ImageView button) {
        button.setCursor(javafx.scene.Cursor.HAND);
        button.setOnMouseClicked(event -> System.out.println("Clicked"));
    }
}
