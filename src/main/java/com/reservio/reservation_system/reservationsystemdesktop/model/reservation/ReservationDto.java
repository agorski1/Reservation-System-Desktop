package com.reservio.reservation_system.reservationsystemdesktop.model.reservation;

import java.time.LocalDateTime;

public record ReservationDto(
        long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String roomType,
        Short guestCount,
        String status,
        LocalDateTime checkInDate,
        LocalDateTime checkOutDate
) {
}


