package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class RecommendedShelfListElementController {

    @FXML
    private Label shelfName;
    @FXML
    private Label numberOfMember;
    @FXML
    private ImageView firstStar;
    @FXML
    private ImageView secondStar;
    @FXML
    private ImageView thirdStar;
    @FXML
    private ImageView forthStar;
    @FXML
    private ImageView fifthStar;
    @FXML
    private Button joinButton;

    public void setShelfName(String name) {
        shelfName.setText(name);
    }
    public void setNumberOfMember(String number) {
        numberOfMember.setText(number);
    }
    public void setFirstStar(boolean isStar) {
        firstStar.setVisible(isStar);
    }
    public void setSecondStar(boolean isStar) {
        secondStar.setVisible(isStar);
    }
    public void setThirdStar(boolean isStar) {
        thirdStar.setVisible(isStar);
    }
    public void setForthStar(boolean isStar) {
        forthStar.setVisible(isStar);
    }
    public void setFifthStar(boolean isStar) {
        fifthStar.setVisible(isStar);
    }
    public void setJoinButton(boolean isJoin) {
        joinButton.setVisible(isJoin);
    }
    public String getShelfName() {
        return shelfName.getText();
    }
    public String getNumberOfMember() {
        return numberOfMember.getText();
    }
    public Button getJoinButton() {
        return joinButton;
    }
    public ImageView getFirstStar() {
        return firstStar;
    }
    public ImageView getSecondStar() {
        return secondStar;
    }
    public ImageView getThirdStar() {
        return thirdStar;
    }
    public ImageView getForthStar() {
        return forthStar;
    }
    public ImageView getFifthStar() {
        return fifthStar;
    }

    public void setJoinButtonAction(Runnable action) {
        joinButton.setOnAction(event -> action.run());
    }

    public void makeClickable(ImageView button) {
        button.setCursor(javafx.scene.Cursor.HAND);
        button.setOnMouseClicked(event -> System.out.println("Clicked"));
    }
}
