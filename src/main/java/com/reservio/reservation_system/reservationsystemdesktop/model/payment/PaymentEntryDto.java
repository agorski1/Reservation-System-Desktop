package com.reservio.reservation_system.reservationsystemdesktop.model.payment;

public record PaymentEntryDto(
        Float amount,
        String date,
        String method,
        String status
) {}