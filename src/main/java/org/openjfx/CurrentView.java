package org.openjfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class CurrentView {

    private static CurrentView instance;
    private static SplitPane splitPane;

    private CurrentView(FXMLLoader mainLoader, FXMLLoader sidebarLoader) {
        loadView(mainLoader, sidebarLoader);
    }

    public static void updateView(FXMLLoader mainLoader, FXMLLoader sidebarLoader) {
        closeView();
        loadView(mainLoader, sidebarLoader);
    }

    private static void closeView() {
        if (splitPane == null) {
            return;
        }
        Stage stage = (Stage) splitPane.getScene().getWindow();
        stage.close();
    }

    private static void loadView(FXMLLoader mainLoader, FXMLLoader sidebarLoader) {
        try {
            Pane mainContent = mainLoader.load();
            Pane sidebarContent = sidebarLoader.load();

            splitPane = new SplitPane();
            splitPane.getItems().addAll(sidebarContent, mainContent);

            showInNewStage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void showInNewStage() {
        Stage stage = new Stage();
        Scene scene = new Scene(splitPane);
        stage.setScene(scene);
        stage.show();
    }
}