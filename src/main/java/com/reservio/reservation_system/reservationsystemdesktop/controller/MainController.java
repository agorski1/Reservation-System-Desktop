package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.reservio.reservation_system.reservationsystemdesktop.service.AuthService;
import com.reservio.reservation_system.reservationsystemdesktop.util.Auth;
import com.reservio.reservation_system.reservationsystemdesktop.util.SceneManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

import java.io.IOException;

public class MainController {

    @FXML private VBox sidebar;

    @FXML private MFXButton btnDashboard;
    @FXML private MFXButton btnRooms;
    @FXML private MFXButton btnReservations;
    @FXML private MFXButton btnReports;
    @FXML private MFXButton btnSettings;
    @FXML private MFXButton btnLogout;

    @FXML private BorderPane contentArea;

    private String role;

    public MainController() {
    }

    @FXML
    private void initialize() {
        role = Auth.getRole();

        if (!"Admin".equals(role)) {
            btnSettings.setVisible(false);
            btnSettings.setManaged(false);
        }

        btnDashboard.setOnAction(e -> loadView("/fxml/dashboard.fxml"));
        btnRooms.setOnAction(e -> loadView("/fxml/rooms.fxml"));
        btnReservations.setOnAction(e -> loadView("/fxml/reservations.fxml"));
        btnReports.setOnAction(e -> loadView("/fxml/reports.fxml"));
        btnSettings.setOnAction(e -> loadView("/fxml/settings.fxml"));

        btnLogout.setOnAction(e -> logout());

        loadView("/fxml/dashboard.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            SceneManager.loadView(fxmlPath); // ← TO JEST DOBRZE!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        try {
            SceneManager.switchScene("/fxml/login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
