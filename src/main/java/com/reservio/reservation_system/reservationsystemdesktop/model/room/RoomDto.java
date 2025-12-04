package com.reservio.reservation_system.reservationsystemdesktop.model.room;

public record RoomDto(
        Long id,
        Long number,
        String name,
        Integer capacity,
        float pricePerNight,
        String status
) {}