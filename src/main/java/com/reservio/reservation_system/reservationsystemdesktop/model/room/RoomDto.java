package com.reservio.reservation_system.reservationsystemdesktop.model.room;

import java.math.BigDecimal;

public record RoomDto(
        Long id,
        Long number,
        String name,
        Integer capacity,
        BigDecimal pricePerNight,
        String status
) {}