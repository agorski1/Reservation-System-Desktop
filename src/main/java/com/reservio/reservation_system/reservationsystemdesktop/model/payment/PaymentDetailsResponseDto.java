package com.reservio.reservation_system.reservationsystemdesktop.model.payment;

public record PaymentDetailsResponseDto(
        PaymentSummary summary,
        PaymentEntryDto[] entries
) { }