package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.reservio.reservation_system.reservationsystemdesktop.AppInjector;
import com.reservio.reservation_system.reservationsystemdesktop.util.Auth;
import com.reservio.reservation_system.reservationsystemdesktop.util.SceneManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {

    @FXML private VBox sidebar;

    @FXML private MFXButton btnDashboard;
    @FXML private MFXButton btnRooms;
    @FXML private MFXButton btnReservations;
    @FXML private MFXButton btnReports;
    @FXML private MFXButton btnUsers;
    @FXML private MFXButton btnRoomTypes;
    @FXML private MFXButton btnLogout;

    @FXML private BorderPane contentArea;

    private String role;

    public MainController() {
    }

    @FXML
    private void initialize() {
        role = Auth.getRole();

        if (!"Admin".equals(role)) {
            btnUsers.setVisible(false);
            btnUsers.setManaged(false);

            btnRoomTypes.setVisible(false);
            btnRoomTypes.setManaged(false);
        }

        btnDashboard.setOnAction(e -> loadView("/fxml/dashboard.fxml"));
        btnRooms.setOnAction(e -> loadView("/fxml/rooms.fxml"));
        btnReservations.setOnAction(e -> loadView("/fxml/reservations.fxml"));
        btnReports.setOnAction(e -> loadView("/fxml/reports.fxml"));
        btnUsers.setOnAction(e -> loadView("/fxml/users.fxml"));
        btnRoomTypes.setOnAction(e -> loadView("/fxml/roomTypes.fxml"));

        btnLogout.setOnAction(e -> logout());

        SceneManager.setMainContentArea(contentArea);
        loadView("/fxml/dashboard.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(AppInjector.getInjector()::getInstance);
            Node view = loader.load();

            contentArea.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
            // Można dodać komunikat błędu dla użytkownika
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
