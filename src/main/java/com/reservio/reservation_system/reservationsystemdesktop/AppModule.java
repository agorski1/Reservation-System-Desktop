package com.reservio.reservation_system.reservationsystemdesktop;

import com.google.inject.AbstractModule;
import com.reservio.reservation_system.reservationsystemdesktop.service.AuthService;
import com.reservio.reservation_system.reservationsystemdesktop.service.DashboardService;
import com.reservio.reservation_system.reservationsystemdesktop.service.ReservationService;
import com.reservio.reservation_system.reservationsystemdesktop.service.UserService;
import com.reservio.reservation_system.reservationsystemdesktop.util.HttpClient;
import com.reservio.reservation_system.reservationsystemdesktop.util.ReportPdfGenerator;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HttpClient.class).toInstance(new HttpClient("http://localhost:8080/hd")); // base URL
        bind(AuthService.class);
        bind(DashboardService.class);
        bind(ReservationService.class);
        bind(ReportPdfGenerator.class);
        bind(UserService.class);
    }
}
