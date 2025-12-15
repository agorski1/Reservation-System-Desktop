package com.reservio.reservation_system.reservationsystemdesktop.util;

import com.reservio.reservation_system.reservationsystemdesktop.model.report.PaymentReportDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.report.RoomOccupancyReportDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportPdfGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void generateOccupancyReport(String filePath, List<RoomOccupancyReportDto> occupancyList) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            addHeader(document, "Raport obłożenia pokoi");
            addOccupancyTable(document, occupancyList);
            addOccupancyChart(document, occupancyList);

            document.close();
            System.out.println("PDF obłożenia zapisany: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generatePaymentReport(String filePath, PaymentReportDto paymentReport) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            addHeader(document, "Raport płatności");
            addPaymentSummary(document, paymentReport);
            addPaymentMethodChart(document, paymentReport);

            document.close();
            System.out.println("PDF płatności zapisany: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addHeader(Document document, String title) throws DocumentException {
        Paragraph header = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
        document.add(new Paragraph(" "));
    }

    private void addOccupancyTable(Document document, List<RoomOccupancyReportDto> occupancyList) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{2, 2, 2, 2, 2, 2});

        table.addCell("Pokój");
        table.addCell("Typ");
        table.addCell("Pojemność");
        table.addCell("Rezerwacje");
        table.addCell("Dni zajętości");
        table.addCell("% obłożenia");

        for (RoomOccupancyReportDto dto : occupancyList) {
            table.addCell(String.valueOf(dto.roomNumber()));
            table.addCell(String.valueOf(dto.roomType()));
            table.addCell(String.valueOf(dto.capacity()));
            table.addCell(String.valueOf(dto.reservationCount()));
            table.addCell(String.valueOf(dto.occupiedDays()));
            table.addCell(String.format("%.2f", dto.occupancyRate() * 100));
        }

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void addOccupancyChart(Document document, List<RoomOccupancyReportDto> occupancyList) throws Exception {
        BufferedImage chart = createOccupancyChart(occupancyList);
        Image chartImage = Image.getInstance(chart, null);
        chartImage.setAlignment(Image.ALIGN_CENTER);
        document.add(chartImage);
        document.add(new Paragraph(" "));
    }

    private BufferedImage createOccupancyChart(List<RoomOccupancyReportDto> occupancyList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (RoomOccupancyReportDto dto : occupancyList) {
            dataset.addValue(dto.occupancyRate() * 100, "Obłożenie %", dto.roomNumber().toString());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Obłożenie pokoi",
                "Pokój",
                "Zajętość (%)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        return chart.createBufferedImage(500, 300);
    }

    private void addPaymentSummary(Document document, PaymentReportDto report) throws DocumentException {
        document.add(new Paragraph("Zakres: " + report.startDate() + " - " + report.endDate()));
        document.add(new Paragraph(" "));

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(50);

        summaryTable.addCell("Łączny przychód");
        summaryTable.addCell(report.totalRevenue().toString());

        summaryTable.addCell("Liczba płatności");
        summaryTable.addCell(String.valueOf(report.paymentCount()));

        summaryTable.addCell("Średnia płatność");
        summaryTable.addCell(report.averagePaymentAmount().toString());

        summaryTable.addCell("Maksymalna płatność");
        summaryTable.addCell(report.maxPaymentAmount().toString());

        summaryTable.addCell("Najczęściej używana metoda");
        summaryTable.addCell(report.mostUsedPaymentMethod());

        document.add(summaryTable);
        document.add(new Paragraph(" "));

        PdfPTable methodTable = new PdfPTable(2);
        methodTable.setWidthPercentage(50);
        methodTable.addCell("Metoda");
        methodTable.addCell("Przychód");

        for (Map.Entry<String, ?> entry : report.revenuePerPaymentMethod().entrySet()) {
            methodTable.addCell(entry.getKey());
            methodTable.addCell(entry.getValue().toString());
        }

        document.add(methodTable);
        document.add(new Paragraph(" "));
    }

    private void addPaymentMethodChart(Document document, PaymentReportDto report) throws Exception {
        BufferedImage chart = createPaymentMethodPieChart(report);
        Image chartImage = Image.getInstance(chart, null);
        chartImage.setAlignment(Image.ALIGN_CENTER);
        document.add(chartImage);
        document.add(new Paragraph(" "));
    }

    private BufferedImage createPaymentMethodPieChart(PaymentReportDto report) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        report.revenuePerPaymentMethod().forEach((method, revenue) -> dataset.setValue(method, revenue.doubleValue()));

        JFreeChart chart = ChartFactory.createPieChart(
                "Przychód według metod płatności",
                dataset,
                true, true, false
        );

        return chart.createBufferedImage(500, 300);
    }
}