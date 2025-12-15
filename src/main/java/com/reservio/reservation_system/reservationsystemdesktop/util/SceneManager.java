package com.reservio.reservation_system.reservationsystemdesktop.util;

import com.reservio.reservation_system.reservationsystemdesktop.AppInjector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class SceneManager {

    private static Stage stage;
    private static Scene scene;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
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

    public static void loadView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        loader.setControllerFactory(AppInjector.getInjector()::getInstance); // ← KLUCZOWE
        Parent view = loader.load();

        BorderPane contentArea = (BorderPane) scene.getRoot().lookup("#contentArea");

        if (contentArea != null) {
            contentArea.setCenter(view);
        } else {
            System.err.println("Nie znaleziono #contentArea! Czy main.fxml ma fx:id=\"contentArea\" na BorderPane?");
        }
    }

    public static void loadView(String fxmlPath, Consumer<Object> controllerConsumer) throws IOException { FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
    loader.setControllerFactory(AppInjector.getInjector()::getInstance);
    Parent view = loader.load();

    if (controllerConsumer != null) {
        Object controller = loader.getController();
        controllerConsumer.accept(controller);
    }

    BorderPane contentArea = (BorderPane) scene.getRoot().lookup("#contentArea");
    if (contentArea != null) {
        contentArea.setCenter(view);
    } else {
        System.err.println("Nie znaleziono #contentArea! Czy main.fxml ma fx:id=\"contentArea\" na BorderPane?");
    }
}

    public static Stage getStage() { return stage; }
    public static Scene getScene() { return scene; }
}
