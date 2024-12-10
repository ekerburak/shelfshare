package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/RecommendedShelves.fxml")),
               new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")));
//        CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/ShelfSettings.fxml")));
    }

    public static void main(String[] args) {
        launch(args);
    }
}