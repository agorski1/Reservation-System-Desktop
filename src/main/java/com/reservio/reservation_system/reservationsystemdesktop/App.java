package com.reservio.reservation_system.reservationsystemdesktop;

import com.reservio.reservation_system.reservationsystemdesktop.util.SceneManager;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {


        Font.loadFont(App.class.getResourceAsStream("/fonts/Roboto-VariableFont_wdth,wght.ttf"), 12);
        Font.loadFont(App.class.getResourceAsStream("/fonts/Roboto-Italic-VariableFont_wdth,wght.ttf"), 12);

        UserAgentBuilder.builder()
                .themes(JavaFXThemes.MODENA)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();

        SceneManager.setStage(stage);
        SceneManager.switchScene("/fxml/login.fxml");

        SceneManager.getScene().getStylesheets().add(
                App.class.getResource("/css/theme.css").toExternalForm()
        );

        stage.setTitle("Hotel Reservation System");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
