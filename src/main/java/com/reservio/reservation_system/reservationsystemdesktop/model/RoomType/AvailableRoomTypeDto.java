package com.reservio.reservation_system.reservationsystemdesktop.model.RoomType;

import java.math.BigDecimal;
import java.util.List;

public record AvailableRoomTypeDto (
    long id,
    String name,
    int capacity,
    BigDecimal pricePerNight,
    BigDecimal totalPrice,
    List<String> amenities
) {}