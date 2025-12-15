package com.reservio.reservation_system.reservationsystemdesktop.model.reservation;

import java.time.LocalDateTime;

public record ManualReservationRequestDto(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Short guestCount,
        Long roomId,
        LocalDateTime checkInDate,
        LocalDateTime checkOutDate
) {}
