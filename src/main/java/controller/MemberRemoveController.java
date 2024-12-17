package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import model.LoggedInUser;
import model.Shelf;
import model.User;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;

public class MemberRemoveController {
    Shelf shelf;
    private final ArrayList<String> usersToDelete = new ArrayList<>();
    private final ObservableList<Pane> items = FXCollections.observableArrayList();

    @FXML
    private ListView memberList;

    @FXML
    private Button save;

    private void listUsers() {
        // clear the list view
        memberList.getItems().clear();

        ArrayList<User> users = shelf.getParticipants();
        users.remove(0);
        for (User user: users) {
            if (usersToDelete.contains(user.getID().toString())) {
                continue;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/removeSetting.fxml"));
                Pane pane = loader.load();
                // Get the controller of the loaded FXML
                RemoveSettingController controller = loader.getController();
                // Set the shelf name label
                controller.setName(user.getUsername());
                controller.setDeleteIcon(() -> {
                    usersToDelete.add(user.getID().toString());
                    listUsers();
                });
                // Set the controller as user data for the pane
                pane.setUserData(controller);
                items.add(pane);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        memberList.setItems(items);
    }

    private void setSave() {
        save.setCursor(javafx.scene.Cursor.HAND);
        save.setOnMouseClicked(mouseEvent -> {
            for (String id : usersToDelete) {
                shelf.kickUser(new ObjectId(id));
            }
            // close the settings
            save.getScene().getWindow().hide();
        });
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
        listUsers();
    }

    @FXML
    public void initialize() {
        setSave();
    }
}
