package org.openjfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class CurrentView {

    private static Node node;

    private CurrentView(FXMLLoader mainLoader, FXMLLoader sidebarLoader) {
        loadView(mainLoader, sidebarLoader);
    }

    public static void updateView(FXMLLoader mainLoader, FXMLLoader sidebarLoader) {
        closeView();
        loadView(mainLoader, sidebarLoader);
    }

    public static void updateView(FXMLLoader mainLoader) {
        closeView();
        loadView(mainLoader);
    }

    private static void closeView() {
        if (node == null) {
            return;
        }
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    private static void loadView(FXMLLoader mainLoader, FXMLLoader sidebarLoader) {
        try {
            Pane mainContent = mainLoader.load();
            Pane sidebarContent = sidebarLoader.load();

            node = new SplitPane();
            ((SplitPane)node).getItems().addAll(sidebarContent, mainContent);

            showInNewStage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadView(FXMLLoader mainLoader) {
        try {
            Pane mainContent = mainLoader.load();

            node = new VBox();
            ((VBox)node).getChildren().add(mainContent);

            showInNewStage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void showInNewStage() {
        Stage stage = new Stage();
        Scene scene;
        if(node instanceof SplitPane) {
            scene = new Scene((SplitPane) node, Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
        } else {
            scene = new Scene((Pane) node, Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
        }
        stage.setScene(scene);
        stage.show();
    }
}