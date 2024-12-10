package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ShelfController {
    @FXML
    private Label shelfName;

    @FXML
    private ImageView addIcon, addPersonIcon, settingsIcon;

    @FXML
    private TextField filterField;

    @FXML
    private ListView<Pane> bookList;

    private final ObservableList<Pane> items = FXCollections.observableArrayList();
    private final String[] books = {"lotr", "cancna", "tutku"};

    private void setShelfName(String name) {
        shelfName.setText(name);
    }

    private void setAddIcon() {
        addIcon.setCursor(javafx.scene.Cursor.HAND);
//        addIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                try {
//                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/addBook.fxml"));
//
//                    Scene scene = new Scene(root);
//                    Stage newStage = new Stage();
//
//                    newStage.setScene(scene);
//                    newStage.show();
//                } catch(IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    private void setAddPersonIcon() {
        addPersonIcon.setCursor(javafx.scene.Cursor.HAND);
        addPersonIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/invitation.fxml"));

                    Scene scene = new Scene(root);
                    Stage newStage = new Stage();

                    newStage.setScene(scene);
                    newStage.show();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setSettingsIcon() {
        settingsIcon.setCursor(javafx.scene.Cursor.HAND);
        settingsIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/ShelfSettings.fxml"));

                    Scene scene = new Scene(root);
                    Stage newStage = new Stage();

                    newStage.setScene(scene);
                    newStage.show();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    public void initialize() {
        // Set the shelf name
        setShelfName("Ahmet's Shelf");
        setAddIcon();
        setAddPersonIcon();
        setSettingsIcon();

        for (String book : books) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book.fxml"));
                Pane pane = loader.load();

                // Get the controller of the loaded FXML
                BookController controller = loader.getController();
                // Set the shelf name label
                controller.setBookName(book);
                controller.setUploaderName("Ahmet");

                // Set the controller as user data for the pane
                pane.setUserData(controller);

                items.add(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FilteredList<Pane> filteredItems = new FilteredList<>(items, p -> true);
        bookList.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
        bookList.setItems(filteredItems);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredItems.setPredicate(pane -> {
                BookController controller = (BookController) pane.getUserData();
                return controller.getBookName().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }
}
