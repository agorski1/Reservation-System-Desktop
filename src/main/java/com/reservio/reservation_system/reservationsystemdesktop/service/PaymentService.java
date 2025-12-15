package com.reservio.reservation_system.reservationsystemdesktop.service;

import com.reservio.reservation_system.reservationsystemdesktop.model.payment.PaymentDetailsResponseDto;
import jakarta.inject.Inject;
import com.reservio.reservation_system.reservationsystemdesktop.util.HttpClient;

public class PaymentService {

    private final HttpClient httpClient;

    @Inject
    public PaymentService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public PaymentDetailsResponseDto getPaymentsForReservation(long reservationId) {
        try {
            String url = "/payments/" + reservationId;
            return httpClient.get(url, PaymentDetailsResponseDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
