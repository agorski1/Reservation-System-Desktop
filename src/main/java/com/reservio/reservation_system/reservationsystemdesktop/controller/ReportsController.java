package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.reservio.reservation_system.reservationsystemdesktop.model.report.PaymentReportDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.report.RoomOccupancyReportDto;
import com.reservio.reservation_system.reservationsystemdesktop.service.ReportService;
import com.reservio.reservation_system.reservationsystemdesktop.util.ReportPdfGenerator;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import javafx.fxml.FXML;

import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ReportsController {

    @FXML
    private MFXDatePicker dateFrom;

    @FXML
    private MFXDatePicker dateTo;

    @FXML
    private MFXButton btnGeneratePDFOccupancy;

    @FXML
    private MFXButton btnGeneratePDFPayments;

    private final ReportService reportService;

    private final ReportPdfGenerator reportPdfGenerator;

    @Inject
    public ReportsController(ReportService reportService, ReportPdfGenerator reportPdfGenerator) {
        this.reportService = reportService;
        this.reportPdfGenerator = reportPdfGenerator;
    }

    @FXML
    public void initialize() {
        btnGeneratePDFOccupancy.setOnAction(event -> generateOccupancyPdf());
        btnGeneratePDFPayments.setOnAction(event -> generatePaymentPdf());
    }

    private void generateOccupancyPdf() {
        LocalDate startDate = dateFrom.getValue();
        LocalDate endDate = dateTo.getValue();

        if (startDate == null || endDate == null) {
            System.out.println("Wybierz zakres dat");
            return;
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<RoomOccupancyReportDto> report = reportService.getRoomOccupancyReport(start, end);
        String filePath = "raport_oblozenia_" + startDate + "_to_" + endDate + ".pdf";
        reportPdfGenerator.generateOccupancyReport(filePath, report);
    }

    private void generatePaymentPdf() {
        LocalDate startDate = dateFrom.getValue();
        LocalDate endDate = dateTo.getValue();

        if (startDate == null || endDate == null) {
            System.out.println("Wybierz zakres dat");
            return;
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        PaymentReportDto report = reportService.getPaymentReport(start, end);
        String filePath = "raport_platnosci_" + startDate + "_to_" + endDate + ".pdf";
        reportPdfGenerator.generatePaymentReport(filePath, report);
    }
}
