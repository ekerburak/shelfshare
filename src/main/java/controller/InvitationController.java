package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class InvitationController {
    @FXML
    private TextField onlyView, edit;

    public void setOnlyView(String text) {
        onlyView.setText(text);
    }

    public void setEdit(String text) {
        edit.setText(text);
    }
}
