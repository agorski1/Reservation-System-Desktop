package com.reservio.reservation_system.reservationsystemdesktop.util;

import com.reservio.reservation_system.reservationsystemdesktop.AppInjector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.Consumer;

public class SceneManager {

    private static Stage stage;
    private static Scene scene;
    private static BorderPane mainContentArea;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void setMainContentArea(BorderPane contentArea) {
        mainContentArea = contentArea;
    }

    public static void switchScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        loader.setControllerFactory(AppInjector.getInjector()::getInstance);
        Parent root = loader.load();

        if (scene == null) {
            scene = new Scene(root, 1280, 800);
        } else {
            scene.setRoot(root);
        }
        stage.setScene(scene);
        stage.show();
    }

    public static <T> T loadIntoMainContent(String fxmlPath) throws IOException {
        if (mainContentArea == null) {
            throw new IllegalStateException("mainContentArea nie jest ustawione! Wywołaj SceneManager.setMainContentArea() w MainController.");
        }

        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        loader.setControllerFactory(AppInjector.getInjector()::getInstance);
        Node view = loader.load();
        T controller = loader.getController();

        mainContentArea.setCenter(view);

        return controller;
    }

    public static void loadIntoMainContent(String fxmlPath, Consumer<Object> onLoaded) throws IOException {
        Object controller = loadIntoMainContent(fxmlPath);
        if (onLoaded != null) {
            onLoaded.accept(controller);
        }
    }
}