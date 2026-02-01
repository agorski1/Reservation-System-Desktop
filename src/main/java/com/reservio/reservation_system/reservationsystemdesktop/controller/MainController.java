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

    @FXML
    private VBox sidebar;

    @FXML
    private MFXButton btnDashboard;
    @FXML
    private MFXButton btnRooms;
    @FXML
    private MFXButton btnReservations;
    @FXML
    private MFXButton btnReports;
    @FXML
    private MFXButton btnUsers;
    @FXML
    private MFXButton btnRoomTypes;
    @FXML
    private MFXButton btnLogout;
    private MFXButton activeButton = null;

    @FXML
    private BorderPane contentArea;

    private String role;

    public MainController() {
    }

    @FXML
    private void initialize() {
        SceneManager.setMainController(this);
        role = Auth.getRole();

        if (!"Admin".equals(role)) {
            btnUsers.setVisible(false);
            btnUsers.setManaged(false);

            btnRoomTypes.setVisible(false);
            btnRoomTypes.setManaged(false);
        }

        SceneManager.setMainContentArea(contentArea);

        setActiveButton(btnDashboard);
        loadView("/fxml/dashboard.fxml"); // ✅ TYLKO JEDEN RAZ

        btnDashboard.setOnAction(e -> {
            setActiveButton(btnDashboard);
            loadView("/fxml/dashboard.fxml");
        });
        btnRooms.setOnAction(e -> {
            setActiveButton(btnRooms);
            loadView("/fxml/rooms.fxml");
        });
        btnReservations.setOnAction(e -> {
            setActiveButton(btnReservations);
            loadView("/fxml/reservations.fxml");
        });
        btnReports.setOnAction(e -> {
            setActiveButton(btnReports);
            loadView("/fxml/reports.fxml");
        });
        btnUsers.setOnAction(e -> {
            setActiveButton(btnUsers);
            loadView("/fxml/users.fxml");
        });
        btnRoomTypes.setOnAction(e -> {
            setActiveButton(btnRoomTypes);
            loadView("/fxml/roomTypes.fxml");
        });

        btnLogout.setOnAction(e -> logout());
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(AppInjector.getInjector()::getInstance);
            Node view = loader.load();

            contentArea.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(MFXButton button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }
        button.getStyleClass().add("active");
        activeButton = button;
    }

    private void logout() {
        try {
            SceneManager.switchScene("/fxml/login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void activateReservationsButton() {
        setActiveButton(btnReservations);
    }
}

