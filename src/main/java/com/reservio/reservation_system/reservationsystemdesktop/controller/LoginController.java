package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.reservio.reservation_system.reservationsystemdesktop.service.AuthService;
import com.reservio.reservation_system.reservationsystemdesktop.util.SceneManager;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import jakarta.inject.Inject;

public class LoginController {

    @FXML private MFXTextField txtLogin;
    @FXML private MFXPasswordField txtPassword;
    @FXML private Label lblError;

    @Inject
    private AuthService authService;

    @FXML
    private void handleLogin() {
        try {
            boolean success = authService.login(txtLogin.getText(), txtPassword.getText());
            if (success) {
                lblError.setText("");
                lblError.getStyleClass().setAll("error-hidden");
                SceneManager.switchScene("/fxml/main.fxml");
            } else {
                lblError.setText("Błędny login lub hasło");
                lblError.getStyleClass().setAll("error-visible");
            }
        } catch (Exception e) {
            lblError.setText("Błąd systemu – spróbuj ponownie");
            lblError.getStyleClass().setAll("error-visible");
            e.printStackTrace();
        }
    }
}