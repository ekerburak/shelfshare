package controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Book;
import model.LoggedInUser;
import model.Shelf;
import model.ShelfCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ShelfController {
    Shelf shelf;

    @FXML
    private Label shelfName;

    @FXML
    private VBox mainBox;

    @FXML
    private ImageView addIcon, addPersonIcon, settingsIcon, rateIcon;

    @FXML
    private TextField filterField;

    @FXML
    private ListView<Pane> bookList;

    private final ObservableList<Pane> items = FXCollections.observableArrayList();
    private ArrayList<Book> books;

    public void setShelfName(String name) {
        shelfName.setText(name);
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
        setShelfName(shelf.getName());
        books = shelf.getBooks();

        for (Book book : books) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book.fxml"));
                Pane pane = loader.load();

                // Get the controller of the loaded FXML
                BookController controller = loader.getController();
                // Set the shelf name label
                controller.setBook(book);
                // Set the controller as user data for the pane
                pane.setUserData(controller);

                items.add(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void ratingShelf() {
        rateIcon.setCursor(Cursor.HAND);
        rateIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/rateShelf.fxml"));

                    System.out.println("a");
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

    public void start() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String base64String = FileToBase64.encodeFileToBase64(file);
         //   shelf.addBook(true,base64String);
            if (base64String != null) {
                System.out.println("Base64 Encoded String: ");
                System.out.println(base64String);
            } else {
                System.out.println("Failed to encode file.");
            }
        }
    }

    private void setAddIcon() {
        addIcon.setCursor(javafx.scene.Cursor.HAND);
        addIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select an Image File");

                // Set file extension filters
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
                );

                // Show open file dialog
                File file = fileChooser.showOpenDialog(null);

            }});

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
        setAddIcon();
        setAddPersonIcon();
        setSettingsIcon();
        ratingShelf();

        // add chat.fxml to mainBox
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chat.fxml"));
            Pane pane = loader.load();
            ChatContoller controller = loader.getController();
            mainBox.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
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
