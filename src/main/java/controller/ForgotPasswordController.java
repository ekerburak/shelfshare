package controller;

import com.postmarkapp.postmark.Postmark;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.Message;
import com.postmarkapp.postmark.client.data.model.message.MessageResponse;
import com.postmarkapp.postmark.client.exception.PostmarkException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.LoggedInUser;
import model.UserCollection;

import java.io.IOException;
import java.util.Random;

public class ForgotPasswordController {

    @FXML
    Button submit;

    @FXML
    TextField email;

    @FXML
    Label warning;

    @FXML
    ImageView back;

    private void setBackIcon() {
        back.setCursor(javafx.scene.Cursor.HAND);
        back.setOnMouseClicked(e -> {
            // close the register popup
            Stage stage = (Stage) back.getScene().getWindow();
            stage.close();
            // show the login popup
            CurrentView.showPopUp(
                    new FXMLLoader(getClass().getResource("/fxml/logIn.fxml"))
            );
        });
    }

    private String generateNewPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    private void setSubmitButton() {
        submit.setCursor(javafx.scene.Cursor.HAND);
        warning.setVisible(false);
        submit.setOnMouseClicked(e -> {
            // check if the email is valid
            if (email.getText().isEmpty()) {
                warning.setText("Please enter your email address.");
            } else {
                String template = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "  <meta charset=\"UTF-8\">\n" +
                        "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "  <style>\n" +
                        "    body {\n" +
                        "      font-family: Arial, sans-serif;\n" +
                        "      background-color: #f4f4f9;\n" +
                        "      color: #333333;\n" +
                        "      margin: 0;\n" +
                        "      padding: 0;\n" +
                        "    }\n" +
                        "    .container {\n" +
                        "      max-width: 600px;\n" +
                        "      margin: 20px auto;\n" +
                        "      background: #ffffff;\n" +
                        "      border-radius: 8px;\n" +
                        "      overflow: hidden;\n" +
                        "      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
                        "    }\n" +
                        "    .header {\n" +
                        "      background: #4CAF50;\n" +
                        "      color: #ffffff;\n" +
                        "      padding: 20px;\n" +
                        "      text-align: center;\n" +
                        "    }\n" +
                        "    .content {\n" +
                        "      padding: 20px;\n" +
                        "    }\n" +
                        "    .button {\n" +
                        "      display: inline-block;\n" +
                        "      margin: 20px 0;\n" +
                        "      padding: 10px 20px;\n" +
                        "      background-color: #4CAF50;\n" +
                        "      color: #ffffff;\n" +
                        "      text-decoration: none;\n" +
                        "      border-radius: 4px;\n" +
                        "    }\n" +
                        "    .footer {\n" +
                        "      text-align: center;\n" +
                        "      padding: 10px;\n" +
                        "      background: #eeeeee;\n" +
                        "      font-size: 12px;\n" +
                        "      color: #666666;\n" +
                        "    }\n" +
                        "  </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "  <div class=\"container\">\n" +
                        "    <div class=\"header\">\n" +
                        "      <h1>ShelfShare - Password Change Request</h1>\n" +
                        "    </div>\n" +
                        "    <div class=\"content\">\n" +
                        "      <p>Your password has been successfully changed. Below is your new password:</p>\n" +
                        "      <p><strong style=\"font-size: 18px; color: #4CAF50;\">%s</strong></p>\n" +
                        "      <p>For security purposes, we recommend that you log in immediately and change your password to something memorable.</p>\n" +
                        "    </div>\n" +
                        "    <div class=\"footer\">\n" +
                        "      <p>Thank you, <br> The CherryOnTop Team</p>\n" +
                        "    </div>\n" +
                        "  </div>\n" +
                        "</body>\n" +
                        "</html>\n";
                String newPassword = generateNewPassword();
                // send the email
                ApiClient client = Postmark.getApiClient("<DELETED FOR SECURITY REASONS>");
                Message message = new Message("<DELETED FOR SECURITY REASONS>", email.getText(), "New password generation request", String.format(template, newPassword));
                try {
                    MessageResponse response = client.deliverMessage(message);
                } catch (PostmarkException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                warning.setVisible(true);
                UserCollection.updatePassword(email.getText(), newPassword);
            }
        });
    }

    public void initialize() {
        setBackIcon();
        setSubmitButton();
    }

}
