package com.reservio.reservation_system.reservationsystemdesktop.service;

import com.reservio.reservation_system.reservationsystemdesktop.model.report.DashboardStatsDto;
import com.reservio.reservation_system.reservationsystemdesktop.util.HttpClient;
import jakarta.inject.Inject;

public class DashboardService {

    private final HttpClient httpClient;

    @Inject
    public DashboardService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public DashboardStatsDto getTodayStats() {
        try {
            return httpClient.get("/dashboard/today", DashboardStatsDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}