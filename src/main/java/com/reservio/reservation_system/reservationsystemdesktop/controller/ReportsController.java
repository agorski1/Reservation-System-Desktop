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
    private MFXButton btnPreviewOccupancy;

    @FXML
    private MFXButton btnGeneratePDFOccupancy;

    @FXML
    private MFXButton btnPreviewPayments;

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
        btnGeneratePDFOccupancy.setOnAction(event -> {
            LocalDateTime start = dateFrom.getValue() != null ? dateFrom.getValue().atStartOfDay() : LocalDateTime.now().minusDays(7);
            LocalDateTime end = dateTo.getValue() != null ? dateTo.getValue().atTime(LocalTime.MAX) : LocalDateTime.now();

            List<RoomOccupancyReportDto> occupancy = reportService.getRoomOccupancyReport(start, end);
            String filePath = "raport_oblozenia_" + start.toLocalDate() + "_to_" + end.toLocalDate() + ".pdf";
            reportPdfGenerator.generateOccupancyReport(filePath, occupancy);
        });

        btnGeneratePDFPayments.setOnAction(event -> {
            LocalDateTime start = dateFrom.getValue() != null ? dateFrom.getValue().atStartOfDay() : LocalDateTime.now().minusDays(7);
            LocalDateTime end = dateTo.getValue() != null ? dateTo.getValue().atTime(LocalTime.MAX) : LocalDateTime.now();

            PaymentReportDto payments = reportService.getPaymentReport(start, end);
            String filePath = "raport_platnosci_" + start.toLocalDate() + "_to_" + end.toLocalDate() + ".pdf";
            reportPdfGenerator.generatePaymentReport(filePath, payments);
        });
    }

    private void generateOccupancyPdf() {
        try {
            LocalDate start = dateFrom.getValue();
            LocalDate end = dateTo.getValue();

            if (start == null || end == null) {
                System.out.println("Wybierz zakres dat");
                return;
            }

            List<RoomOccupancyReportDto> report = reportService.getRoomOccupancyReport(
                    start.atStartOfDay(), end.atStartOfDay()
            );

            String filePath = "raport_oblozenia_" + start + "_to_" + end + ".pdf";
            reportPdfGenerator.generateOccupancyReport(filePath, report);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void generatePaymentPdf() {
        try {
            LocalDate start = dateFrom.getValue();
            LocalDate end = dateTo.getValue();

            if (start == null || end == null) {
                System.out.println("Wybierz zakres dat");
                return;
            }

            PaymentReportDto report = reportService.getPaymentReport(
                    start.atStartOfDay(), end.atStartOfDay()
            );

            String filePath = "raport_platnosci_" + start + "_to_" + end + ".pdf";
            reportPdfGenerator.generatePaymentReport(filePath, report);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
