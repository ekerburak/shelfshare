package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import model.User;

import java.util.ArrayList;

public class memberRemoveController {

    @FXML
    private ListView<Pane> memberList;

    private void listMembers() {
        memberList.getItems().clear();

        ArrayList<User> users;
    }
}
