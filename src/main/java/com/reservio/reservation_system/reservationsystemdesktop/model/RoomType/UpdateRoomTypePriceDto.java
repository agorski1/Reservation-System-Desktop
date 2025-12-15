package com.reservio.reservation_system.reservationsystemdesktop.model.RoomType;

import java.math.BigDecimal;

public record UpdateRoomTypePriceDto(
        Long roomTypeId,
        BigDecimal pricePerNight
) {}
