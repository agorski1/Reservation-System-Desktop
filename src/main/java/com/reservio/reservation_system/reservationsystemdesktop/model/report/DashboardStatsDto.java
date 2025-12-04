package com.reservio.reservation_system.reservationsystemdesktop.model.report;

import java.math.BigDecimal;

public record DashboardStatsDto(
     BigDecimal todayRevenue,
     int todayReservationCount,
     float hotelOccupancyRate,
     int availableRoomsToday
) {}