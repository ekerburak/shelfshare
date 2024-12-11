package controller;

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
    private static Stage stage;

    private CurrentView(FXMLLoader sidebarLoader, FXMLLoader mainLoader) {
        loadView(sidebarLoader, mainLoader);
    }

    public static void updateView(FXMLLoader sidebarLoader, FXMLLoader mainLoader) {
        loadView(sidebarLoader, mainLoader);
    }

    public static void updateView(FXMLLoader mainLoader) {
        loadView(mainLoader);
    }

    private static void loadView(FXMLLoader sidebarLoader, FXMLLoader mainLoader) {
        try {
            Pane sidebarContent = sidebarLoader.load();
            Pane mainContent = mainLoader.load();

            node = new SplitPane();
            ((SplitPane)node).getItems().addAll(sidebarContent, mainContent);
            ((SplitPane)node).setDividerPositions(0.2);

            showInStage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadView(FXMLLoader mainLoader) {
        try {
            Pane mainContent = mainLoader.load();

            node = new VBox();
            ((VBox)node).getChildren().add(mainContent);

            showInStage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getScene() {
        Scene scene;
        if(node instanceof SplitPane) {
            scene = new Scene((SplitPane) node, Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
        } else {
            scene = new Scene((Pane) node, Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
        }
        stage.setScene(scene);
    }

    private static void showInStage() {
        Scene scene;
        if (stage == null) {
            stage = new Stage();
            getScene();
            stage.show();
        } else {
            scene = stage.getScene();
            if (scene == null) {
                getScene();
            } else {
                if (node instanceof SplitPane) {
                    scene.setRoot((SplitPane) node);
                } else {
                    scene.setRoot((Pane) node);
                }
            }
        }
    }
}