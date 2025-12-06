package com.reservio.reservation_system.reservationsystemdesktop.service;

import com.reservio.reservation_system.reservationsystemdesktop.model.reservation.ReservationDto; // <-- ten z backendu
import com.reservio.reservation_system.reservationsystemdesktop.util.HttpClient;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ReservationService {

    private final HttpClient httpClient;

    @Inject
    public ReservationService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<ReservationDto> getReservations(
            boolean all,
            LocalDateTime from,
            LocalDateTime to,
            String phone,
            String email
    ) {
        try {
            StringBuilder url = new StringBuilder("/reservations?");
            url.append("all=").append(all);

            if (from != null) url.append("&from=").append(from);
            if (to != null)   url.append("&to=").append(to);
            if (phone != null && !phone.isBlank()) url.append("&phone=").append(phone);
            if (email != null && !email.isBlank()) url.append("&email=").append(email);

            ReservationDto[] array = httpClient.get(url.toString(), ReservationDto[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void updateReservationStatus(long reservationId, String newStatus) {
        try {
            var body = new Object() { public final String status = newStatus; };
            httpClient.patch("/reservations/" + reservationId + "/status", body, Void.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}