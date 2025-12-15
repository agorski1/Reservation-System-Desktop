package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.reservio.reservation_system.reservationsystemdesktop.model.payment.PaymentDetailsResponseDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.payment.PaymentEntryDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.reservation.ReservationDto;
import com.reservio.reservation_system.reservationsystemdesktop.service.PaymentService;
import com.reservio.reservation_system.reservationsystemdesktop.service.ReservationService;
import com.reservio.reservation_system.reservationsystemdesktop.util.SceneManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class ReservationDetailsController {

    @FXML private Label lblTitle;

    @FXML private Label lblFirstName;
    @FXML private Label lblLastName;
    @FXML private Label lblEmail;
    @FXML private Label lblPhone;

    @FXML private Label lblCheckIn;
    @FXML private Label lblCheckOut;
    @FXML private Label lblGuestCount;
    @FXML private Label lblRoomType;
    @FXML private Label lblReservationStatus;

    @FXML private Label lblTotalPaid;
    @FXML private Label lblRemaining;

    @FXML private MFXTableView<PaymentEntryDto> tblPayments;

    @FXML private MFXButton btnBack;

    @Inject private ReservationService reservationService;
    @Inject private PaymentService paymentService;

    @FXML
    private void initialize() {
        setupPaymentTable();
        btnBack.setOnAction(e -> goBack());
    }

    private void setupPaymentTable() {
        tblPayments.getTableColumns().clear();

        MFXTableColumn<PaymentEntryDto> amountCol = new MFXTableColumn<>("Kwota", true);
        amountCol.setRowCellFactory(entry -> new MFXTableRowCell<>(PaymentEntryDto::amount));

        MFXTableColumn<PaymentEntryDto> dateCol = new MFXTableColumn<>("Data", true);
        dateCol.setRowCellFactory(entry ->
                new MFXTableRowCell<>(p -> p.date().toString())
        );

        MFXTableColumn<PaymentEntryDto> methodCol = new MFXTableColumn<>("Metoda", true);
        methodCol.setRowCellFactory(entry ->
                new MFXTableRowCell<>(p -> p.method())
        );

        MFXTableColumn<PaymentEntryDto> statusCol = new MFXTableColumn<>("Status", true);
        statusCol.setRowCellFactory(entry ->
                new MFXTableRowCell<>(p -> p.status())
        );

        tblPayments.getTableColumns().addAll(amountCol, dateCol, methodCol, statusCol);

        tblPayments.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue() - 2;
            int columnsCount = tblPayments.getTableColumns().size();
            double columnWidth = totalWidth / columnsCount;

            tblPayments.getTableColumns().forEach(col -> {
                col.setPrefWidth(columnWidth);
                col.setMinWidth(columnWidth);
                col.setMaxWidth(columnWidth);
            });
        });
    }

    public void loadReservation(long id) {

        ReservationDto reservation = reservationService.getReservationById(id);
        PaymentDetailsResponseDto payments = paymentService.getPaymentsForReservation(id);

        lblTitle.setText("SZCZEGÓŁY REZERWACJI #" + id);

        lblFirstName.setText(reservation.firstName());
        lblLastName.setText(reservation.lastName());
        lblEmail.setText(reservation.email());
        lblPhone.setText(reservation.phoneNumber());

        lblCheckIn.setText(reservation.checkInDate().toString());
        lblCheckOut.setText(reservation.checkOutDate().toString());
        lblGuestCount.setText(String.valueOf(reservation.guestCount()));
        lblRoomType.setText(reservation.roomType());
        lblReservationStatus.setText(reservation.status());

        lblTotalPaid.setText(payments.summary().totalPaid().toString());
        lblRemaining.setText(payments.summary().remainingAmount().toString());

        tblPayments.setItems(FXCollections.observableArrayList(payments.entries()));
    }

    /* ------------------------- GO BACK ------------------------- */
    private void goBack() {
        try {
            SceneManager.loadIntoMainContent("/fxml/reservations.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
