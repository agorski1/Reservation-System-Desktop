package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.google.inject.Inject;
import com.reservio.reservation_system.reservationsystemdesktop.model.report.DashboardStatsDto;
import com.reservio.reservation_system.reservationsystemdesktop.service.DashboardService;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.math.RoundingMode;

public class DashboardController {

    @FXML private Label lblRevenue;
    @FXML private Label lblReservations;
    @FXML private Label lblAvailableRooms;
    @FXML private PieChart pieOccupancy;

    @Inject
    private DashboardService dashboardService;

    @FXML
    public void initialize() {
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        try {
            DashboardStatsDto stats = dashboardService.getTodayStats();
            if (stats == null) return;

            lblRevenue.setText(stats.todayRevenue().setScale(2, RoundingMode.HALF_UP) + " zł");
            lblReservations.setText(String.valueOf(stats.todayReservationCount()));
            lblAvailableRooms.setText(String.valueOf(stats.availableRoomsToday()));

            PieChart.Data occupied = new PieChart.Data("Zajęte", stats.hotelOccupancyRate());
            PieChart.Data free = new PieChart.Data("Dostępne", 100 - stats.hotelOccupancyRate());
            pieOccupancy.getData().setAll(occupied, free);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
