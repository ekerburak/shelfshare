package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.Book;
import model.LoggedInUser;
import model.Shelf;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class ShelfController {

    Shelf shelf;

    @FXML
    private Label shelfName;

    @FXML
    private VBox mainBox;

    @FXML
    private ImageView addIcon, addPersonIcon, settingsIcon, rateIcon, backIcon, leaveIcon;

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
        setSettingsIcon();
        openAddABookPopup();
        setAddPersonIcon();
        books = shelf.getBooks();
        for (Book book : books) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book.fxml"));
                Pane pane = loader.load();
                // Get the controller of the loaded FXML
                BookController controller = loader.getController();
                controller.setBook(book);
                controller.setShelf(shelf);
                // Set the controller as user data for the pane
                pane.setUserData(controller);

                items.add(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // add chat.fxml to mainBox
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chat.fxml"));
            Pane pane = loader.load();
            ChatController controller = loader.getController();
            controller.setChat(shelf.getForumChat());
            mainBox.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRatingShelf() {
        rateIcon.setCursor(Cursor.HAND);
        rateIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/rateShelf.fxml"));
                    Pane pane = loader.load();
                    RateShelfController controller = loader.getController();
                    controller.setShelf(shelf);
                    CurrentView.showPopUp(pane);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setBackIcon() {
        backIcon.setCursor(javafx.scene.Cursor.HAND);
        backIcon.setOnMouseClicked(e -> {
            CurrentView.updateView(
                    new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                    new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml"))
            );
        });
    }

    private String convertImageToBase64(BufferedImage image) {
        String encodedfile = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            encodedfile = Base64.getEncoder().encodeToString(bytes);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encodedfile;
    }

    private void openAddABookPopup() {
        if(shelf.getAllowBookAdd() || shelf.getAdminsIDs().contains(LoggedInUser.getInstance().getID())) {
            addIcon.setCursor(javafx.scene.Cursor.HAND);
            addIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addABook.fxml"));
                        Pane root = loader.load();

                        AddABookController controller = loader.getController();
                        controller.setShelf(shelf);

                        CurrentView.showPopUp(root);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        else {
            addIcon.setVisible(false);
        }
    }

    private void setAddPersonIcon() {
        if(shelf.getAllowInvitation() || shelf.getAdminsIDs().contains(LoggedInUser.getInstance().getID())) {
            addPersonIcon.setCursor(javafx.scene.Cursor.HAND);
            addPersonIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/invitation.fxml"));
                        Pane root = loader.load();

                        InvitationController controller = loader.getController();
                        controller.setOnlyView(shelf.getStandardInvitation());
                        controller.setEdit(shelf.getAdminInvitation());

                        CurrentView.showPopUp(root);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            addPersonIcon.setVisible(false);
        }
    }

    private void setSettingsIcon() {
        if(shelf.getAdminsIDs().contains(LoggedInUser.getInstance().getID())) {
            settingsIcon.setCursor(javafx.scene.Cursor.HAND);
            settingsIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShelfSettings.fxml"));
                        Pane root = loader.load();
                        ShelfSettingsController controller = loader.getController();
                        controller.setShelf(shelf);
                        CurrentView.showPopUp(root);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            settingsIcon.setVisible(false);
        }
    }

    private void setLeaveIcon() {
        leaveIcon.setCursor(javafx.scene.Cursor.HAND);
        leaveIcon.setOnMouseClicked(mouseEvent -> {
            LoggedInUser.leaveShelf(shelf);
            // open your shelves back
            CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                    new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml")));
        });
    }


    @FXML
    public void initialize() {
        setBackIcon();
//        setAddIcon();
        setRatingShelf();
        setLeaveIcon();

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
