package org.openjfx;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BookEditingController {
    @FXML
    private ImageView imageView;

    @FXML
    private ImageView goBackIcon, leftArrow, rightArrow, underlineIcon, highlightIcon, eraseIcon;

    @FXML
    private Label shelfName, bookName;

    @FXML
    private Pane drawPane;

    private double startX, startY;

    private int currentSelection = -1;

    void setShelfName(String shelfName) {
        this.shelfName.setText(shelfName);
    }

    void setBookName(String bookName) {
        this.bookName.setText(bookName);
    }

    void leftArrowMechanism(ImageView leftArrow) {
        leftArrow.setCursor(Cursor.HAND);
        leftArrow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // TODO: Implement left arrow mechanism
            }
        });
    }

    void rightArrowMechanism(ImageView rightArrow) {
        rightArrow.setCursor(Cursor.HAND);
        rightArrow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

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
                }
            }
        });
    }

    // TODO: Implement goBackIconMechanism

    /**
     * Draws a highlight rectangle on the given pane
     */
    private void drawHighlight(double startX, double startY, double endX, double endY) {
        Rectangle highlight = new Rectangle(
                startX, startY - 6, (endX - startX), 12
        );
        highlight.setFill(Color.YELLOW);
        highlight.setOpacity(0.3);
        drawPane.getChildren().add(highlight);
    }

    /**
     * Draws a line on the given pane
     */
    private void drawLine(double startX, double startY, double endX, double endY) {
        Rectangle line = new Rectangle(
                startX, startY - 1, (endX - startX), 2
        );
        line.setFill(Color.BLACK);
        drawPane.getChildren().add(line);
    }

    /**
     * Erases the objects at the given coordinates
     */
    private void erase(double x, double y) {
        drawPane.getChildren().removeIf(node -> node.contains(x, y));
    }

    private void annotatingMechanism(Pane pane) {
        pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                startX = event.getX();
                startY = event.getY();
            }
        });

        pane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double endX = event.getX();
                double endY = event.getY();

                if(currentSelection == 0) {
                    drawHighlight(startX, startY, endX, endY);
                } else if (currentSelection == 1) {
                    drawLine(startX, startY, endX, endY);
                } else if (currentSelection == 2) {
                    erase(endX, endY);
                }
            }
        });
    }

    @FXML
    public void initialize() {
        leftArrowMechanism(leftArrow);
        rightArrowMechanism(rightArrow);
        highlightIconMechanism(highlightIcon);
        underlineIconMechanism(underlineIcon);
        eraseIconMechanism(eraseIcon);
        imageView.setImage(new Image(getClass().getResourceAsStream("/assets/a4-paper.jpeg")));
        imageView.setFitHeight(950);
        annotatingMechanism(drawPane);
    }
}