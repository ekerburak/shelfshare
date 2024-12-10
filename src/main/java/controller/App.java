package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/sidebar.fxml")),
                new FXMLLoader(getClass().getResource("/fxml/shelf.fxml")));
//        CurrentView.updateView(new FXMLLoader(getClass().getResource("/fxml/settingsAccount.fxml")));
    }

    public static void main(String[] args) {
        launch(args);
    }
}