package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class CreateAShelfController {
    @FXML
    private TextField textField;

    @FXML
    private CheckBox isPublic, addBooks, annotate, comment, invite;

    @FXML
    private Button create;

    private void setCreate() {
        create.setOnAction(e -> {

        });
    }

    @FXML
    public void initialize() {
        setCreate();
    }
}
