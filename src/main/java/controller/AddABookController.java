package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import model.Book;
import model.LoggedInUser;
import model.Shelf;
import model.ShelfCollection;
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

public class AddABookController {

    Shelf shelf;

    @FXML
    private TextField name;

    @FXML
    private ToggleGroup coverSelection;

    @FXML
    private RadioButton redCover, blueCover, pinkCover;

    private static final int RED_COVER = 0, BLUE_COVER = 1, PINK_COVER = 2;

    @FXML
    private Button fileButton, doneButton;

    String[] base64Images;

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }

    private String convertImageToBase64(BufferedImage image) {
        String encodedfile = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            encodedfile = Base64.getEncoder().encodeToString(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedfile;
    }

    public void selectFile() {
        fileButton.setCursor(javafx.scene.Cursor.HAND);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book.fxml"));
        fileButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select a PDF File");
                // Set file extension filters
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                );
                // Show open file dialog
                File file = fileChooser.showOpenDialog(null);

                if(file != null) {
                    try {
                        PDDocument document = Loader.loadPDF(file);
                        PDFRenderer renderer = new PDFRenderer(document);

                        base64Images = new String[document.getNumberOfPages()];

                        // convert pdf to images
                        for (int i = 0; i < document.getNumberOfPages(); i++) {
                            BufferedImage image = renderer.renderImage(i);
                            // convert image to base64
                            base64Images[i] = convertImageToBase64(image);
                        }
                        System.out.println("entered");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }});
    }

    public void setCoverSelection(){
//        TODO requires a book model method
        if(coverSelection.getSelectedToggle() == redCover){
            shelf.getBooks().get(shelf.getBooks().size()-1).setCoverImageOption(RED_COVER);

        }
        else if(coverSelection.getSelectedToggle() == blueCover){
            shelf.getBooks().get(shelf.getBooks().size()-1).setCoverImageOption(BLUE_COVER);

        }
        else if(coverSelection.getSelectedToggle() == pinkCover){
            shelf.getBooks().get(shelf.getBooks().size()-1).setCoverImageOption(PINK_COVER);

        }
    }

    public void setName(){
        System.out.println(name.getText());
    }

    private void setDoneButton() {
        doneButton.setCursor(javafx.scene.Cursor.HAND);
        doneButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(base64Images != null) {
                    shelf.addBook(name.getText(), false, base64Images);
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shelf.fxml"));
                try {
                    Pane pane = loader.load();
                    ShelfController controller = loader.getController();
                    controller.setShelf(shelf);
                    CurrentView.updateView(
                        new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                        pane
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // hide the popup
                ((Button) mouseEvent.getSource()).getScene().getWindow().hide();
            }
        });
    }

    public void initialize() {
        setShelf(shelf);
        selectFile();
        setCoverSelection();
        setName();
        setDoneButton();
    }
}
