package com.reservio.reservation_system.reservationsystemdesktop.model.RoomType;

import java.math.BigDecimal;
import java.util.List;

public record RoomTypeDto(
        long id,
        String name,
        int capacity,
        BigDecimal pricePerNight,
        String description,
        List<String> amenities) {
}
