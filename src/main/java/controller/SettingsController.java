package controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
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
                LoggedInUser.changePassword(oldPassword.getText(), newPassword.getText());
            }
            else
                throw new RuntimeException("your new passwords do not match");
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
