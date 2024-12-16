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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
                        shelf.addBook(false, base64Images);
                        shelf.getBooks().get(shelf.getBooks().size()-1).setName(name.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }});
    }

    public void setCoverSelection(){
//        TODO requires a book model method
        if(coverSelection.getSelectedToggle() == redCover){
//            shelf.getBooks().get(shelf.getBooks().size()-1).setCover(redCover);

        }
        else if(coverSelection.getSelectedToggle() == blueCover){
//            shelf.getBooks().get(shelf.getBooks().size()-1).setCover(blueCover);

        }
        else if(coverSelection.getSelectedToggle() == pinkCover){
//            shelf.getBooks().get(shelf.getBooks().size()-1).setCover(pinkCover);

        }
    }

    public void setName(){
        System.out.println(name.getText());
        doneButton.setCursor(javafx.scene.Cursor.HAND);
        doneButton.setOnMouseClicked(event -> {
            shelf.getBooks().get(shelf.getBooks().size()-1).setName(name.getText());
        });
    }

    public void initialize() {
        setShelf(shelf);
        selectFile();
        setCoverSelection();
        setName();

    }
}
