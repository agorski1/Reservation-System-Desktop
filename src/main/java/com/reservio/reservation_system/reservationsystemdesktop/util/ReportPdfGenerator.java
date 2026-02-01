package com.reservio.reservation_system.reservationsystemdesktop.util;

import com.reservio.reservation_system.reservationsystemdesktop.model.report.PaymentReportDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.report.RoomOccupancyReportDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportPdfGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final BaseFont BASE_FONT;

    static {
        try {
            BASE_FONT = BaseFont.createFont(
                    "src/main/resources/fonts/DejaVuSans.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void generateOccupancyReport(String filePath, List<RoomOccupancyReportDto> occupancyList) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            addHeader(document, "Raport oblozenia pokoi");
            addGenerationDate(document);

            addOccupancyTable(document, occupancyList);
            addOccupancyChart1(document, occupancyList);
            addOccupancyChart2(document, occupancyList);
            addOccupancyChart3(document, occupancyList);
            addOccupancyChart4(document, occupancyList);

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

            addHeader(document, "Raport platnosci");
            addGenerationDate(document);

            addPaymentTable(document, paymentReport);
            addPaymentChart1(document, paymentReport);
            addPaymentChart2(document, paymentReport);
            addPaymentChart3(document, paymentReport);
            addPaymentChart4(document, paymentReport);

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

    private void addGenerationDate(Document document) throws DocumentException {
        Paragraph date = new Paragraph("Data wygenerowania: " + java.time.LocalDate.now().format(DATE_FORMAT),
                FontFactory.getFont(FontFactory.HELVETICA, 10));
        date.setAlignment(Element.ALIGN_RIGHT);
        document.add(date);
        document.add(new Paragraph(" "));
    }

    private void addOccupancyTable(Document document, List<RoomOccupancyReportDto> list) throws DocumentException {
        if (list.isEmpty()) {
            document.add(new Paragraph("Brak danych do wyświetlenia.", FontFactory.getFont(FontFactory.HELVETICA, 12)));
            document.add(new Paragraph(" "));
            return;
        }

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 2, 2, 2, 2, 2});

        addTableHeader(table, new String[]{
                "Pokoj",
                "Typ",
                "Pojemnosc",
                "Rezerwacje",
                "Dni zajetosci",
                "% oblozenia"
        });

        for (RoomOccupancyReportDto dto : list) {
            addTableCell(table, dto.roomNumber().toString(), Element.ALIGN_CENTER);
            addTableCell(table, dto.roomType().toString(), Element.ALIGN_CENTER);
            addTableCell(table, dto.capacity().toString(), Element.ALIGN_RIGHT);
            addTableCell(table, dto.reservationCount().toString(), Element.ALIGN_RIGHT);
            addTableCell(table, dto.occupiedDays().toString(), Element.ALIGN_RIGHT);
            addTableCell(table, String.format("%.2f", dto.occupancyRate() * 100), Element.ALIGN_RIGHT);
        }

        document.add(table);
        addEmptyParagraph(document);
    }

    private void addPaymentTable(Document document, PaymentReportDto report) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        addTableHeader(table, new String[]{
                "Metoda platnosci",
                "Liczba platnosci",
                "laczny przychod",
                "Srednia platnosc"
        });

        for (Map.Entry<String, Long> entry : report.paymentCountPerPaymentMethod().entrySet()) {
            String method = entry.getKey();
            Long count = entry.getValue();
            BigDecimal revenue = report.revenuePerPaymentMethod().getOrDefault(method, BigDecimal.ZERO);
            BigDecimal avg = count != 0 ? revenue.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

            addTableCell(table, method, Element.ALIGN_CENTER);
            addTableCell(table, String.valueOf(count), Element.ALIGN_RIGHT);
            addTableCell(table, revenue.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), Element.ALIGN_RIGHT);
            addTableCell(table, avg.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), Element.ALIGN_RIGHT);
        }

        document.add(table);
        addEmptyParagraph(document);
    }

    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addTableCell(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 12)));
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    private void addEmptyParagraph(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
    }

    private void addOccupancyChart1(Document document, List<RoomOccupancyReportDto> list) throws Exception {
        BufferedImage chart = createOccupancyBarChart(list);
        addChartImage(document, chart);
    }

    private void addOccupancyChart2(Document document, List<RoomOccupancyReportDto> list) throws Exception {
        BufferedImage chart = createOccupancyByTypeChart(list);
        addChartImage(document, chart);
    }

    private void addOccupancyChart3(Document document, List<RoomOccupancyReportDto> list) throws Exception {
        BufferedImage chart = createReservationVsOccupiedChart(list);
        addChartImage(document, chart);
    }

    private void addOccupancyChart4(Document document, List<RoomOccupancyReportDto> list) throws Exception {
        BufferedImage chart = createOccupancyHistogramChart(list);
        addChartImage(document, chart);
    }

    private void addPaymentChart1(Document document, PaymentReportDto report) throws Exception {
        BufferedImage chart = createPaymentPieChart(report);
        addChartImage(document, chart);
    }

    private void addPaymentChart2(Document document, PaymentReportDto report) throws Exception {
        BufferedImage chart = createPaymentCountBarChart(report);
        addChartImage(document, chart);
    }

    private void addPaymentChart3(Document document, PaymentReportDto report) throws Exception {
        BufferedImage chart = createRevenuePerDayLineChart(report);
        addChartImage(document, chart);
    }

    private void addPaymentChart4(Document document, PaymentReportDto report) throws Exception {
        BufferedImage chart = createMaxVsAvgPaymentChart(report);
        addChartImage(document, chart);
    }

    private void addChartImage(Document document, BufferedImage chart) throws Exception {
        BufferedImage rgbImage = new BufferedImage(
                chart.getWidth(),
                chart.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g = rgbImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, chart.getWidth(), chart.getHeight());
        g.drawImage(chart, 0, 0, null);
        g.dispose();

        com.lowagie.text.Image img = com.lowagie.text.Image.getInstance(rgbImage, null);
        img.setAlignment(Element.ALIGN_CENTER);
        document.add(img);
        addEmptyParagraph(document);
    }

    private BufferedImage createOccupancyBarChart(List<RoomOccupancyReportDto> list) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        list.forEach(dto ->
                dataset.addValue(dto.occupancyRate() * 100, "Obłożenie %", dto.roomNumber().toString())
        );

        JFreeChart chart = ChartFactory.createBarChart(
                "Poziom obłożenia poszczególnych pokoi",
                "Pokój",
                "Poziom obłożenia (%)",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();

        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);

        Font titleFont  = new Font("Dialog", Font.BOLD, 15);
        Font labelFont  = new Font("Dialog", Font.BOLD, 13);
        Font tickFont   = new Font("Dialog", Font.PLAIN, 11);

        chart.getTitle().setPaint(Color.BLACK);
        chart.getTitle().setFont(titleFont);

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setLabelFont(labelFont);
        xAxis.setTickLabelFont(tickFont);
        xAxis.setLabelPaint(Color.BLACK);
        xAxis.setTickLabelPaint(Color.BLACK);
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        ValueAxis yAxis = plot.getRangeAxis();
        yAxis.setLabelFont(labelFont);
        yAxis.setTickLabelFont(tickFont);
        yAxis.setLabelPaint(Color.BLACK);
        yAxis.setTickLabelPaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinePaint(new Color(180, 180, 180));
        plot.setRangeGridlinePaint(new Color(180, 180, 180));
        plot.setDomainGridlineStroke(new BasicStroke(0.8f));
        plot.setRangeGridlineStroke(new BasicStroke(0.8f));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(60, 120, 220));
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setItemMargin(0.1);

        return chart.createBufferedImage(600, 400);
    }

    private BufferedImage createOccupancyByTypeChart(List<RoomOccupancyReportDto> list) {
        Map<String, Double> sum = new HashMap<>();
        Map<String, Integer> count = new HashMap<>();
        for (RoomOccupancyReportDto dto : list) {
            sum.put(dto.roomType(), sum.getOrDefault(dto.roomType(), 0.0) + dto.occupancyRate());
            count.put(dto.roomType(), count.getOrDefault(dto.roomType(), 0) + 1);
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String type : sum.keySet()) {
            double avg = sum.get(type) / count.get(type);
            dataset.addValue(avg * 100, "Średnie obłożenie", type);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Średni poziom obłożenia według typu pokoju",
                "Typ pokoju",
                "Średni poziom obłożenia (%)",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();

        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);

        Font titleFont  = new Font("Dialog", Font.BOLD, 15);
        Font labelFont  = new Font("Dialog", Font.BOLD, 13);
        Font tickFont   = new Font("Dialog", Font.PLAIN, 11);

        chart.getTitle().setPaint(Color.BLACK);
        chart.getTitle().setFont(titleFont);

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setLabelFont(labelFont);
        xAxis.setTickLabelFont(tickFont);
        xAxis.setLabelPaint(Color.BLACK);
        xAxis.setTickLabelPaint(Color.BLACK);
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        ValueAxis yAxis = plot.getRangeAxis();
        yAxis.setLabelFont(labelFont);
        yAxis.setTickLabelFont(tickFont);
        yAxis.setLabelPaint(Color.BLACK);
        yAxis.setTickLabelPaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinePaint(new Color(180, 180, 180));
        plot.setRangeGridlinePaint(new Color(180, 180, 180));
        plot.setDomainGridlineStroke(new BasicStroke(0.8f));
        plot.setRangeGridlineStroke(new BasicStroke(0.8f));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(60, 120, 220));
        renderer.setBarPainter(new StandardBarPainter());

        return chart.createBufferedImage(500, 380);
    }

    private BufferedImage createReservationVsOccupiedChart(List<RoomOccupancyReportDto> list) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (RoomOccupancyReportDto dto : list) {
            dataset.addValue(dto.reservationCount(), "Rezerwacje",     dto.roomNumber().toString());
            dataset.addValue(dto.occupiedDays(),    "Dni zajętości",  dto.roomNumber().toString());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Zestawienie liczby rezerwacji i dni zajętości pokoi",
                "Pokój",
                "Liczba",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();

        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);

        Font titleFont  = new Font("Dialog", Font.BOLD, 15);
        Font labelFont  = new Font("Dialog", Font.BOLD, 13);
        Font tickFont   = new Font("Dialog", Font.PLAIN, 11);

        chart.getTitle().setPaint(Color.BLACK);
        chart.getTitle().setFont(titleFont);

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setLabelFont(labelFont);
        xAxis.setTickLabelFont(tickFont);
        xAxis.setLabelPaint(Color.BLACK);
        xAxis.setTickLabelPaint(Color.BLACK);
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        ValueAxis yAxis = plot.getRangeAxis();
        yAxis.setLabelFont(labelFont);
        yAxis.setTickLabelFont(tickFont);
        yAxis.setLabelPaint(Color.BLACK);
        yAxis.setTickLabelPaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinePaint(new Color(180, 180, 180));
        plot.setRangeGridlinePaint(new Color(180, 180, 180));
        plot.setDomainGridlineStroke(new BasicStroke(0.8f));
        plot.setRangeGridlineStroke(new BasicStroke(0.8f));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(60, 120, 220));
        renderer.setSeriesPaint(1, new Color(220, 80, 80));
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setItemMargin(0.05);

        return chart.createBufferedImage(600, 400);
    }

    private BufferedImage createOccupancyHistogramChart(List<RoomOccupancyReportDto> list) {
        Map<String, Integer> histogram = new HashMap<>();
        for (RoomOccupancyReportDto dto : list) {
            int bucket = (int) (dto.occupancyRate() * 100 / 10) * 10;
            String key = bucket + (bucket == 90 ? "-100" : "-" + (bucket + 9)) + "%";
            histogram.put(key, histogram.getOrDefault(key, 0) + 1);
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        histogram.forEach((key, value) ->
                dataset.addValue(value, "Liczba pokoi", key)
        );

        JFreeChart chart = ChartFactory.createBarChart(
                "Rozkład poziomu obłożenia pokoi",
                "Zakres poziomu obłożenia",
                "Liczba pokoi",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();

        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);

        Font titleFont  = new Font("Dialog", Font.BOLD, 15);
        Font labelFont  = new Font("Dialog", Font.BOLD, 13);
        Font tickFont   = new Font("Dialog", Font.PLAIN, 11);

        chart.getTitle().setPaint(Color.BLACK);
        chart.getTitle().setFont(titleFont);

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setLabelFont(labelFont);
        xAxis.setTickLabelFont(tickFont);
        xAxis.setLabelPaint(Color.BLACK);
        xAxis.setTickLabelPaint(Color.BLACK);

        ValueAxis yAxis = plot.getRangeAxis();
        yAxis.setLabelFont(labelFont);
        yAxis.setTickLabelFont(tickFont);
        yAxis.setLabelPaint(Color.BLACK);
        yAxis.setTickLabelPaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinePaint(new Color(180, 180, 180));
        plot.setRangeGridlinePaint(new Color(180, 180, 180));
        plot.setDomainGridlineStroke(new BasicStroke(0.8f));
        plot.setRangeGridlineStroke(new BasicStroke(0.8f));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(100, 180, 100));
        renderer.setBarPainter(new StandardBarPainter());

        return chart.createBufferedImage(520, 380);
    }

    private BufferedImage createPaymentPieChart(PaymentReportDto report) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        report.revenuePerPaymentMethod().forEach((k, v) -> dataset.setValue(k, v.doubleValue()));

        JFreeChart chart = ChartFactory.createPieChart(
                "Przychód według metod płatności",
                dataset,
                true,
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();

        plot.setBackgroundPaint(Color.WHITE);

        chart.getTitle().setPaint(Color.BLACK);

        plot.setLabelPaint(Color.BLACK);

        plot.setLabelOutlinePaint(Color.BLACK);
        plot.setLabelOutlineStroke(new BasicStroke(0.5f));

        if (chart.getLegend() != null) {
            chart.getLegend().setItemPaint(Color.BLACK);
        }

        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})",
                new DecimalFormat("0"),
                new DecimalFormat("0%")
        ));

        BufferedImage image = chart.createBufferedImage(500, 400);

        return image;
    }

    private BufferedImage createPaymentCountBarChart(PaymentReportDto report) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        report.paymentCountPerPaymentMethod().forEach((k, v) ->
                dataset.addValue(v, "Liczba płatności", k)
        );

        JFreeChart chart = ChartFactory.createBarChart(
                "Liczba płatności według metod",
                "Metoda płatności",
                "Liczba płatności",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();

        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);

        Font titleFont  = new Font("Dialog", Font.BOLD, 15);
        Font labelFont  = new Font("Dialog", Font.BOLD, 13);
        Font tickFont   = new Font("Dialog", Font.PLAIN, 11);

        chart.getTitle().setPaint(Color.BLACK);
        chart.getTitle().setFont(titleFont);

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setLabelFont(labelFont);
        xAxis.setTickLabelFont(tickFont);
        xAxis.setLabelPaint(Color.BLACK);
        xAxis.setTickLabelPaint(Color.BLACK);
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        ValueAxis yAxis = plot.getRangeAxis();
        yAxis.setLabelFont(labelFont);
        yAxis.setTickLabelFont(tickFont);
        yAxis.setLabelPaint(Color.BLACK);
        yAxis.setTickLabelPaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinePaint(new Color(180, 180, 180));
        plot.setRangeGridlinePaint(new Color(180, 180, 180));
        plot.setDomainGridlineStroke(new BasicStroke(0.8f));
        plot.setRangeGridlineStroke(new BasicStroke(0.8f));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(60, 120, 220));
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setItemMargin(0.1);

        return chart.createBufferedImage(560, 380);
    }

    private BufferedImage createRevenuePerDayLineChart(PaymentReportDto report) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        report.revenuePerDay().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> dataset.addValue(
                        entry.getValue().doubleValue(),
                        "Przychód",
                        entry.getKey().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ));

        JFreeChart chart = ChartFactory.createLineChart(
                "Przychód dzienny",
                "Data",
                "Przychód",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);

        Font labelFont = new Font("Dialog", Font.BOLD, 14);
        Font tickFont  = new Font("Dialog", Font.PLAIN, 12);

        chart.getTitle().setPaint(Color.BLACK);

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        xAxis.setLabelFont(labelFont);
        xAxis.setTickLabelFont(tickFont);
        xAxis.setLabelPaint(Color.BLACK);
        xAxis.setTickLabelPaint(Color.BLACK);

        plot.getRangeAxis().setLabelFont(labelFont);
        plot.getRangeAxis().setTickLabelFont(tickFont);
        plot.getRangeAxis().setLabelPaint(Color.BLACK);
        plot.getRangeAxis().setTickLabelPaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);

        plot.setDomainGridlinePaint(new Color(180, 180, 180));
        plot.setRangeGridlinePaint(new Color(180, 180, 180));
        plot.setDomainGridlineStroke(new BasicStroke(0.8f));
        plot.setRangeGridlineStroke(new BasicStroke(0.8f));

        var renderer = (org.jfree.chart.renderer.category.LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(0, Color.BLUE);

        BufferedImage image = chart.createBufferedImage(500, 400);

        return image;
    }

    private BufferedImage createMaxVsAvgPaymentChart(PaymentReportDto report) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(report.averagePaymentAmount().doubleValue(), "Średnia płatność", "Średnia");
        dataset.addValue(report.maxPaymentAmount().doubleValue(),    "Maksymalna płatność", "Maksymalna");

        JFreeChart chart = ChartFactory.createBarChart(
                "Porównanie średniej i maksymalnej wartości płatności",
                "",
                "Kwota (zł)",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();

        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundPaint(Color.WHITE);

        Font titleFont  = new Font("Dialog", Font.BOLD, 15);
        Font labelFont  = new Font("Dialog", Font.BOLD, 13);
        Font tickFont   = new Font("Dialog", Font.PLAIN, 11);

        chart.getTitle().setPaint(Color.BLACK);
        chart.getTitle().setFont(titleFont);

        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setLabelFont(labelFont);
        xAxis.setTickLabelFont(tickFont);
        xAxis.setLabelPaint(Color.BLACK);
        xAxis.setTickLabelPaint(Color.BLACK);

        ValueAxis yAxis = plot.getRangeAxis();
        yAxis.setLabelFont(labelFont);
        yAxis.setTickLabelFont(tickFont);
        yAxis.setLabelPaint(Color.BLACK);
        yAxis.setTickLabelPaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinePaint(new Color(180, 180, 180));
        plot.setRangeGridlinePaint(new Color(180, 180, 180));
        plot.setDomainGridlineStroke(new BasicStroke(0.8f));
        plot.setRangeGridlineStroke(new BasicStroke(0.8f));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(60, 120, 220));
        renderer.setSeriesPaint(1, new Color(220, 80, 80));
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setItemMargin(0.2);

        return chart.createBufferedImage(520, 360);
    }
}
