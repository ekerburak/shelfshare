package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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

    private static Pane loadFXML(FXMLLoader mainLoader) {
        try {
            return mainLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void showPopUp(FXMLLoader loader) {
        try {
            Pane pane = loader.load();
            Scene scene = new Scene(pane);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void showPopUp(Parent root) {
        Scene scene = new Scene(root);
        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.show();
    }

    public static void updateView(FXMLLoader sidebarLoader, FXMLLoader mainLoader) {
        loadView(loadFXML(sidebarLoader), loadFXML(mainLoader));
    }
    public static void updateView(FXMLLoader sidebarLoader, Pane mainLoader) {
        loadView(loadFXML(sidebarLoader), mainLoader);
    }
    public static void updateView(FXMLLoader mainLoader) {
        loadView(loadFXML(mainLoader));
    }


    private static void loadView(Pane sidebarContent, Pane mainContent) {
        node = new SplitPane();
        ((SplitPane)node).getItems().addAll(sidebarContent, mainContent);
        ((SplitPane)node).setDividerPositions(0.2, 1.0);
        mainContent.setMaxWidth(Double.MAX_VALUE);

        showInStage();
    }

    private static void loadView(Pane mainContent) {
        node = new VBox();
        ((VBox)node).getChildren().add(mainContent);

        showInStage();
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