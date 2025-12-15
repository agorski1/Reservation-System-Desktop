package com.reservio.reservation_system.reservationsystemdesktop.model.payment;

public record PaymentSummary(
            Long reservationId,
            Float totalPaid,
            Float remainingAmount
    ) {}
