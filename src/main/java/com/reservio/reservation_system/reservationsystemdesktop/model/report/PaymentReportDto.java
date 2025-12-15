package com.reservio.reservation_system.reservationsystemdesktop.model.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record PaymentReportDto(
        LocalDate startDate,
        LocalDate endDate,
        Integer totalDays, // liczba dni raportu

        BigDecimal totalRevenue, // suma wszystkich platnosci
        long paymentCount, // liczba dokonanych platnosci
        BigDecimal averagePaymentAmount, // srednia kwota platnosci = totalRevenue/paymentCount
        BigDecimal maxPaymentAmount,

        String mostUsedPaymentMethod,

        Map<String, BigDecimal> revenuePerPaymentMethod, // suma przychodow na kazda metode platnosci
        Map<String, Long> paymentCountPerPaymentMethod, // liczba platnosci w podziale na metody platnosci

        Map<LocalDate, BigDecimal> revenuePerDay // przychod dzienny
) {
}
