package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.reservio.reservation_system.reservationsystemdesktop.model.reservation.ManualReservationRequestDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.room.AvailableRoomDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.RoomType.AvailableRoomTypeDto;
import com.reservio.reservation_system.reservationsystemdesktop.service.ReservationService;
import com.reservio.reservation_system.reservationsystemdesktop.service.RoomService;
import com.reservio.reservation_system.reservationsystemdesktop.service.RoomTypeService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.models.spinner.IntegerSpinnerModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class NewReservationController {

    @FXML
    private MFXTextField txtFirstName, txtLastName, txtEmail, txtPhone;

    @FXML
    private MFXDatePicker dateCheckIn, dateCheckOut;

    @FXML
    private MFXComboBox<AvailableRoomTypeDto> comboRoomType;

    @FXML
    private MFXComboBox<AvailableRoomDto> comboRoom;

    @FXML
    private MFXSpinner spinnerGuests;

    @FXML
    private Label lblSummaryRoomType, lblSummaryRoom, lblSummaryDate, lblSummaryGuestCount,
            lblSummaryPricePerNight, lblSummaryTotalPrice;

    @FXML
    private MFXButton btnSaveReservation;

    private final RoomTypeService roomTypeService;
    private final RoomService roomService;
    private final ReservationService reservationService;

    @Inject
    public NewReservationController(RoomTypeService roomTypeService, RoomService roomService, ReservationService reservationService) {
        this.roomTypeService = roomTypeService;
        this.roomService = roomService;
        this.reservationService = reservationService;
    }

    @FXML
    public void initialize() {
        IntegerSpinnerModel model = new IntegerSpinnerModel(1);
        model.setMin(1);
        model.setMax(10);
        model.setIncrement(1);

        spinnerGuests.setSpinnerModel(model);
        spinnerGuests.setValue(1);
        loadRoomTypes();

        comboRoomType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dateCheckIn.getValue() != null && dateCheckOut.getValue() != null) {
                loadAvailableRooms(newVal, dateCheckIn.getValue(), dateCheckOut.getValue());
                lblSummaryRoomType.setText(newVal.name());
                lblSummaryPricePerNight.setText(newVal.pricePerNight().toString());
                updateSpinnerMaxFromRoomType();
                updateSummary();
            }
        });

        comboRoom.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblSummaryRoom.setText(newVal.number().toString());
                updateSummary();
            }
        });

        dateCheckIn.valueProperty().addListener((obs, oldVal, newVal) -> reloadRooms());
        dateCheckOut.valueProperty().addListener((obs, oldVal, newVal) -> reloadRooms());

        spinnerGuests.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblSummaryGuestCount.setText(newVal != null ? newVal.toString() : "");
            updateSpinnerMaxFromRoomType();
            updateSummary();
        });

        btnSaveReservation.setOnAction(e -> handleCreateReservation());
    }

    private void loadRoomTypes() {
        new Thread(() -> {
            List<AvailableRoomTypeDto> roomTypes = roomTypeService.getAvailableRoomTypes(
                    null, null, null, null, null, null
            );
            Platform.runLater(() -> {
                comboRoomType.setItems(FXCollections.observableArrayList(roomTypes));

                comboRoomType.setConverter(new javafx.util.StringConverter<>() {
                    @Override
                    public String toString(AvailableRoomTypeDto object) {
                        return object != null ? object.name() : "";
                    }

                    @Override
                    public AvailableRoomTypeDto fromString(String string) {
                        return comboRoomType.getItems().stream()
                                .filter(rt -> rt.name().equals(string))
                                .findFirst()
                                .orElse(null);
                    }
                });
            });
        }).start();
    }

    private void loadAvailableRooms(AvailableRoomTypeDto roomType, LocalDate checkIn, LocalDate checkOut) {
        new Thread(() -> {
            LocalDateTime from = LocalDateTime.of(checkIn, LocalTime.of(14, 0));
            LocalDateTime to = LocalDateTime.of(checkOut, LocalTime.of(12, 0));
            List<AvailableRoomDto> rooms = roomService.getAvailableRooms(roomType.id(), from, to);
            Platform.runLater(() -> {
                comboRoom.setItems(FXCollections.observableArrayList(rooms));

                comboRoom.setConverter(new javafx.util.StringConverter<>() {
                    @Override
                    public String toString(AvailableRoomDto object) {
                        return object != null ? object.number().toString() : "";
                    }

                    @Override
                    public AvailableRoomDto fromString(String string) {
                        return comboRoom.getItems().stream()
                                .filter(r -> r.number().toString().equals(string))
                                .findFirst()
                                .orElse(null);
                    }
                });
            });
        }).start();
    }

    private void reloadRooms() {
        AvailableRoomTypeDto selectedType = comboRoomType.getValue();
        LocalDate checkIn = dateCheckIn.getValue();
        LocalDate checkOut = dateCheckOut.getValue();
        if (selectedType != null && checkIn != null && checkOut != null) {
            loadAvailableRooms(selectedType, checkIn, checkOut);
            updateSummary();
        }
    }

    private void updateSummary() {
        AvailableRoomTypeDto roomType = comboRoomType.getValue();
        AvailableRoomDto room = comboRoom.getValue();
        LocalDate checkIn = dateCheckIn.getValue();
        LocalDate checkOut = dateCheckOut.getValue();
        Integer guests = (Integer) spinnerGuests.getValue();

        if (roomType != null) {
            lblSummaryRoomType.setText(roomType.name());
            lblSummaryPricePerNight.setText(roomType.pricePerNight().toString());
        } else {
            lblSummaryRoomType.setText("");
            lblSummaryPricePerNight.setText("");
        }

        lblSummaryRoom.setText(room != null ? room.number().toString() : "");

        lblSummaryGuestCount.setText(guests != null ? guests.toString() : "");

        if (checkIn != null && checkOut != null && roomType != null) {
            long nights = checkOut.toEpochDay() - checkIn.toEpochDay();
            lblSummaryDate.setText(checkIn + " - " + checkOut);
            lblSummaryTotalPrice.setText(roomType.pricePerNight().multiply(new java.math.BigDecimal(nights)).toString());
        } else {
            lblSummaryDate.setText("");
            lblSummaryTotalPrice.setText("");
        }
    }

    @FXML
    private void handleCreateReservation() {
        try {
            String firstName = txtFirstName.getText();
            String lastName = txtLastName.getText();
            String email = txtEmail.getText();
            String phone = txtPhone.getText();

            Integer guests = (Integer) spinnerGuests.getValue();
            Short guestCount = guests != null ? guests.shortValue() : 0;

            AvailableRoomDto selectedRoom = comboRoom.getValue();
            if (selectedRoom == null) {
                return;
            }

            Long roomId = selectedRoom.id();

            LocalDate checkIn = dateCheckIn.getValue();
            LocalDate checkOut = dateCheckOut.getValue();
            if (checkIn == null || checkOut == null) {
                return;
            }

            LocalDateTime checkInDT = checkIn.atTime(12, 0);
            LocalDateTime checkOutDT = checkOut.atTime(10, 0);

            ManualReservationRequestDto dto = new ManualReservationRequestDto(
                    firstName, lastName, email, phone, guestCount, roomId, checkInDT, checkOutDT
            );

            reservationService.createManualReservation(dto);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSpinnerMaxFromRoomType() {
        AvailableRoomTypeDto type = comboRoomType.getValue();
        if (type == null) return;

        IntegerSpinnerModel model = (IntegerSpinnerModel) spinnerGuests.getSpinnerModel();

        model.setMax(type.capacity());

        int current = (int) spinnerGuests.getValue();
        if (current > model.getMax()) {
            spinnerGuests.setValue(model.getMax());
        }
    }
}

