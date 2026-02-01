package com.reservio.reservation_system.reservationsystemdesktop.service;
import com.reservio.reservation_system.reservationsystemdesktop.model.report.PaymentReportDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.report.RoomOccupancyReportDto;
import com.reservio.reservation_system.reservationsystemdesktop.util.HttpClient;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ReportService {

    private final HttpClient httpClient;

    @Inject
    public ReportService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public PaymentReportDto getPaymentReport(LocalDateTime start, LocalDateTime end) {
        try {
            String url = "/reports/payments?start=" + start + "&end=" + end;
            return httpClient.get(url, PaymentReportDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<RoomOccupancyReportDto> getRoomOccupancyReport(LocalDateTime start, LocalDateTime end) {
        try {
            String url = "/reports/occupancy?start=" + start + "&end=" + end;
            RoomOccupancyReportDto[] array = httpClient.get(url, RoomOccupancyReportDto[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
