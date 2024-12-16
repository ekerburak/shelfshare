package controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.Book;
import model.PageListener;
import model.Shelf;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class BookEditingController implements PageListener {
    private Book book;
    private Shelf shelf;
    private Rectangle lastHighlight, lastUnderline;

    @FXML
    private ImageView imageView;

    @FXML
    private ImageView goBackIcon, leftArrow, rightArrow, underlineIcon, highlightIcon, eraseIcon, stickyNoteIcon, chatIcon;

    @FXML
    private Label shelfName, bookName;

    @FXML
    private Pane drawPane;

    private int startX, startY;

    private int currentSelection = -1;

    public void setBook(Book book) {
        this.book = book;
        book.addCurrentPageListener(this);
        book.startListening();
        bookName.setText(book.getName());
        renderImage();
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
        shelfName.setText(shelf.getName());
    }

    private void renderImage() {
        imageView.setImage(decodeImageFromBase64(book.getCurrentPage().getImage()));
        imageView.setFitHeight(950);
        drawPane.getChildren().clear();
        ArrayList<ArrayList<Integer>> coordinates = book.getCurrentPage().getHighlightCoordinates();
//        ArrayList<Color> colors = book.getCurrentPage().getHighlightColors();
        for(int i = 0; i < coordinates.size(); i++) {
            ArrayList<Integer> coordinate = coordinates.get(i);
//            Color color = colors.get(i);
            drawHighlight(coordinate.get(0), coordinate.get(1), coordinate.get(2));
        }

        coordinates = book.getCurrentPage().getLineCoordinates();

        for(int i = 0; i < coordinates.size(); i++) {
            ArrayList<Integer> coordinate = coordinates.get(i);
            drawLine(coordinate.get(0), coordinate.get(1), coordinate.get(2));
        }
    }

    private void leftArrowMechanism(ImageView leftArrow) {
        leftArrow.setCursor(Cursor.HAND);
        leftArrow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                book.goToNextPage();
                renderImage();
            }
        });
    }

    void rightArrowMechanism(ImageView rightArrow) {
        rightArrow.setCursor(Cursor.HAND);
        rightArrow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                book.goToPrevPage();
                renderImage();
            }
        });
    }

    private void setHighlightIconColor(boolean selected) {
        if(selected) {
            highlightIcon.setImage(new Image(getClass().getResourceAsStream("/assets/ink_highlighter_blue.png")));
        } else {
            highlightIcon.setImage(new Image(getClass().getResourceAsStream("/assets/ink_highlighter.png")));
        }
    }

    private void setUnderlineIconColor(boolean selected) {
        if(selected) {
            underlineIcon.setImage(new Image(getClass().getResourceAsStream("/assets/format_underlined_blue.png")));
        } else {
            underlineIcon.setImage(new Image(getClass().getResourceAsStream("/assets/format_underlined.png")));
        }
    }

    private void setEraseIconColor(boolean selected) {
        if(selected) {
            eraseIcon.setImage(new Image(getClass().getResourceAsStream("/assets/ink_eraser_red.png")));
        } else {
            eraseIcon.setImage(new Image(getClass().getResourceAsStream("/assets/ink_eraser.png")));
        }
    }

    private void setStickyNoteIconColor(boolean selected) {
        if (selected) {
            stickyNoteIcon.setImage(new Image(getClass().getResourceAsStream("/assets/sticky_note_blue.png")));
        } else {
            stickyNoteIcon.setImage(new Image(getClass().getResourceAsStream("/assets/sticky_note.png")));
        }
    }

    void highlightIconMechanism(ImageView highlightIcon) {
        highlightIcon.setCursor(Cursor.HAND);
        highlightIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(currentSelection == 0) {
                    currentSelection = -1;
                    setHighlightIconColor(false);
                } else {
                    currentSelection = 0;
                    setHighlightIconColor(true);
                    setUnderlineIconColor(false);
                    setEraseIconColor(false);
                    setStickyNoteIconColor(false);
                }
            }
        });
    }

    void underlineIconMechanism(ImageView underlineIcon) {
        underlineIcon.setCursor(Cursor.HAND);
        underlineIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(currentSelection == 1) {
                    currentSelection = -1;
                    setUnderlineIconColor(false);
                } else {
                    currentSelection = 1;
                    setUnderlineIconColor(true);
                    setHighlightIconColor(false);
                    setEraseIconColor(false);
                    setStickyNoteIconColor(false);
                }
            }
        });
    }

    void eraseIconMechanism(ImageView eraseIcon) {
        eraseIcon.setCursor(Cursor.HAND);
        eraseIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(currentSelection == 2) {
                    currentSelection = -1;
                    setEraseIconColor(false);
                } else {
                    currentSelection = 2;
                    setEraseIconColor(true);
                    setHighlightIconColor(false);
                    setUnderlineIconColor(false);
                    setStickyNoteIconColor(false);
                }
            }
        });
    }

    private void setGoBackIcon() {
        goBackIcon.setCursor(Cursor.HAND);
        goBackIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shelf.fxml"));
                    Pane pane = loader.load();
                    ShelfController controller = loader.getController();
                    controller.setShelf(shelf);
                    CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")), pane);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setChatIcon() {
        chatIcon.setCursor(Cursor.HAND);
        chatIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/discussion.fxml"));
                    Pane pane = loader.load();
                    DiscussionController controller = loader.getController();
                    controller.setBook(book);
                    CurrentView.showPopUp(pane);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Add a method to handle sticky note icon click
    void stickyNoteIconMechanism(ImageView stickyNoteIcon) {
        stickyNoteIcon.setCursor(Cursor.HAND);
        stickyNoteIcon.setOnMouseClicked(event -> {
            if (currentSelection == 3) {
                currentSelection = -1;
                setStickyNoteIconColor(false);
            } else {
                currentSelection = 3;
                setStickyNoteIconColor(true);
                setHighlightIconColor(false);
                setUnderlineIconColor(false);
                setEraseIconColor(false);
            }
        });
    }

    /**
     * Draws a highlight rectangle on the given pane
     */
    private Rectangle drawHighlight(int startX, int startY, int endX) {
        Rectangle highlight = new Rectangle(
                startX, startY - 6, (endX - startX), 12
        );
        highlight.setFill(Color.rgb(255, 255, 0, 0.3));
        drawPane.getChildren().add(highlight);
        return highlight;
    }

    /**
     * Draws a line on the given pane
     */
    private Rectangle drawLine(int startX, int startY, int endX) {
        Rectangle line = new Rectangle(
                startX, startY - 1, (endX - startX), 2
        );
        line.setFill(Color.BLACK);
        drawPane.getChildren().add(line);
        return line;
    }

    // Method to show input dialog and get user input
    private String getStickyNoteContent() {
        TextInputDialog dialog = new TextInputDialog("Sticky Note");
        dialog.setTitle("Sticky Note");
        dialog.setHeaderText("Enter the content of the sticky note:");
        dialog.setContentText("Content:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse("");
    }

    // Method to add a sticky note
    private void addStickyNote(int x, int y) {
        String content = getStickyNoteContent();
        if (content.isEmpty()) {
            return;
        }

        Label stickyNote = new Label(content);
        stickyNote.setStyle("-fx-background-color: yellow; -fx-padding: 10px;");
        stickyNote.setLayoutX(x);
        stickyNote.setLayoutY(y);

        // Make the sticky note draggable
        stickyNote.setOnMousePressed(event -> {
            startX = (int)event.getX();
            startY = (int)event.getY();
        });

        drawPane.getChildren().add(stickyNote);
    }

    /**
     * Erases the objects at the given coordinates
     */
    private void erase(int x, int y) {
        drawPane.getChildren().removeIf(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                return label.getBoundsInParent().contains(x, y);
            }
            return node.contains(x, y);
        });

    }
    private void annotatingMechanism(Pane pane) {
        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                startX = (int)event.getX();
                startY = (int)event.getY();
            }
        });

        pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int endX = (int)(event.getX());

                if (currentSelection == 0) {
                    if (lastHighlight != null) {
                        drawPane.getChildren().remove(lastHighlight);
                    }
                    lastHighlight = drawHighlight(startX, startY, endX);
                } else if (currentSelection == 1) {
                    if(lastUnderline != null) {
                        drawPane.getChildren().remove(lastUnderline);
                    }
                    lastUnderline = drawLine(startX, startY, endX);
                }
            }
        });

        pane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(currentSelection == 0) {
                    if (lastHighlight != null) {
                        drawPane.getChildren().remove(lastHighlight);
                    }
                    int endX = (int) (event.getX());
                    if (endX < startX) {
                        int temp = startX;
                        startX = endX;
                        endX = temp;
                    }
                    book.addHighlightToCurrentPage(new ArrayList<Integer>(List.of(startX, startY, endX)), Color.rgb(255, 255, 0, 0.3));
                }
                if(currentSelection == 1) {
                    if(lastUnderline != null) {
                        drawPane.getChildren().remove(lastUnderline);
                    }
                    int endX = (int) (event.getX());
                    if (endX < startX) {
                        int temp = startX;
                        startX = endX;
                        endX = temp;
                    }
                    book.addUnderlineToCurrentPage(new ArrayList<Integer>(List.of(startX, startY, endX)), Color.BLACK);
                }
                lastUnderline = lastHighlight = null;
                if (currentSelection == 2) {
                    erase((int)event.getX(), (int)event.getY());
                } else if (currentSelection == 3) {
                    addStickyNote((int)event.getX(), (int)event.getY());
                }
            }
        });
    }

    private Image decodeImageFromBase64(String base64) {
        try {
            byte[] imageByte = Base64.getDecoder().decode(base64);
            return new Image(new ByteArrayInputStream(imageByte));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void initialize() {
        setGoBackIcon();
        setChatIcon();
        leftArrowMechanism(leftArrow);
        rightArrowMechanism(rightArrow);
        highlightIconMechanism(highlightIcon);
        underlineIconMechanism(underlineIcon);
        eraseIconMechanism(eraseIcon);
        stickyNoteIconMechanism(stickyNoteIcon);
        annotatingMechanism(drawPane);

        // Add key event handler for left arrow key
        drawPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case LEFT:
                            book.goToPrevPage();
                            renderImage();
                            break;
                        case RIGHT:
                            book.goToNextPage();
                            renderImage();
                            break;
                    }
                });
            }
        });


    }

    @Override
    public void onPageHighlightAdded(ArrayList<Integer> coordinate, Color color) {
        Platform.runLater(() -> {
            drawHighlight(coordinate.get(0), coordinate.get(1), coordinate.get(2));
        });
    }

    @Override
    public void onPageUnderlineAdded(ArrayList<Integer> coordinate, Color color) {
        Platform.runLater(() -> {
            drawLine(coordinate.get(0), coordinate.get(1), coordinate.get(2));
        });
    }

    @Override
    public void onPageHighlightRemoved(ArrayList<ArrayList<Integer>> remainingCoordinates, ArrayList<Color> remainingColors) {

    }

    @Override
    public void onPageUnderlineRemoved(ArrayList<ArrayList<Integer>> remainingCoordinates, ArrayList<Color> remainingColors) {

    }
}