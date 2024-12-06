package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/yourShelves.fxml")),
                new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")));
    }

    public static void main(String[] args) {
        launch(args);
    }
}