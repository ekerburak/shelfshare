package controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.Cursor;
import javafx.stage.Stage;
import model.LoggedInUser;

public class SettingsController {

    @FXML
    public Button logOutButton,biographySaveButton,passwordSaveButton;

    @FXML
    PasswordField oldPassword,newPassword,repeatedPassword;

    @FXML
    TextArea biographyText;

    @FXML
    Label passwordChangeStatusLabel;

    private void logOut() {
        LoggedInUser.logOut();
        ((Stage)logOutButton.getScene().getWindow()).close();
        CurrentView.updateView(
                new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                new FXMLLoader(getClass().getResource("/fxml/mainPage.fxml"))
        );
    }

    @FXML
    public void logOutFunction(){
        logOutButton.setCursor(Cursor.HAND);
        logOutButton.setOnMouseClicked(e -> {
            logOut();
        });
    }
    @FXML
    public void biographySaveFunction(){
        biographySaveButton.setCursor(Cursor.HAND);
        biographySaveButton.setOnMouseClicked(e -> {
            LoggedInUser.getInstance().setAbout(biographyText.getText());
        });
    }
    public void setPasswordFunction(){
        passwordSaveButton.setCursor(Cursor.HAND);
        passwordSaveButton.setOnMouseClicked(e -> {
            if(newPassword.getText().equals(repeatedPassword.getText())){
                boolean successfulPasswordChange = LoggedInUser.changePassword(oldPassword.getText(), newPassword.getText());
                if(successfulPasswordChange) {
                    passwordChangeStatusLabel.setStyle("-fx-text-fill: green;");
                    passwordChangeStatusLabel.setText("Password change successful!");
                } else {
                    passwordChangeStatusLabel.setStyle("-fx-text-fill: red;");
                    passwordChangeStatusLabel.setText("Old password is incorrect!");
                }
                passwordChangeStatusLabel.setVisible(true);
            }
            else {
                passwordChangeStatusLabel.setStyle("-fx-text-fill: red;");
                passwordChangeStatusLabel.setText("Repeated password does not match new password!");
                passwordChangeStatusLabel.setVisible(true);
            }
        });
    }
    @FXML
    public void initialize() {
        biographyText.setText(LoggedInUser.getInstance().getAbout());
        logOutFunction();
        biographySaveFunction();
        setPasswordFunction();
    }
}
