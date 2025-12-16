package com.reservio.reservation_system.reservationsystemdesktop.model.report;

import java.time.LocalDate;

public record RoomOccupancyReportDto (
    LocalDate startDate,
    LocalDate endDate,
    Integer totalDays, // liczba dni raportu

    Short roomNumber,
    String roomType,
    Long capacity,

    Integer reservationCount, // liczba pobytow
    Integer occupiedDays, // laczna liczba dni, kiedy pokoj byl zajety w danym okresie
    Float occupancyRate // procent zajetosci
) {}
