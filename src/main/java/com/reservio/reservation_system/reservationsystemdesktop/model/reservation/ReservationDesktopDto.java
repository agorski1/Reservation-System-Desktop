package com.reservio.reservation_system.reservationsystemdesktop.model.reservation;

import java.time.LocalDateTime;

public record ReservationDesktopDto(
        long id,
        String clientName,
        String phoneNumber,
        String email,
        String roomType,
        Short guestCount,
        String statusPolish,
        String statusEnglish,
        LocalDateTime checkInDate,
        LocalDateTime checkOutDate
) {}
