package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.reservio.reservation_system.reservationsystemdesktop.model.room.RoomDto;
import com.reservio.reservation_system.reservationsystemdesktop.service.RoomService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class RoomsController implements Initializable {

    @FXML private MFXTableView<RoomDto> roomsTable;
    @FXML private MFXButton btnSaveStatuses;

    @Inject private RoomService roomService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadRooms();

        btnSaveStatuses.setOnAction(e -> saveStatuses());
    }

    private void setupTableColumns() {
        MFXTableColumn<RoomDto> numberColumn = new MFXTableColumn<>("Numer", true);
        numberColumn.setRowCellFactory(room -> new MFXTableRowCell<>(RoomDto::number));

        MFXTableColumn<RoomDto> nameColumn = new MFXTableColumn<>("Nazwa", true);
        nameColumn.setRowCellFactory(room -> new MFXTableRowCell<>(RoomDto::name));

        MFXTableColumn<RoomDto> capacityColumn = new MFXTableColumn<>("Pojemność", true);
        capacityColumn.setRowCellFactory(room -> new MFXTableRowCell<>(RoomDto::capacity));

        MFXTableColumn<RoomDto> priceColumn = new MFXTableColumn<>("Cena/Noc", true);
        priceColumn.setRowCellFactory(room -> new MFXTableRowCell<>(RoomDto::pricePerNight));

        MFXTableColumn<RoomDto> statusColumn = new MFXTableColumn<>("Status", true);

        statusColumn.setRowCellFactory(room -> {
            ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(
                    room.status(), "Remont", "Niedostępny"
            ));
            comboBox.setValue(room.status());

            comboBox.setOnAction(e -> {
                int index = roomsTable.getItems().indexOf(room);
                RoomDto updated = new RoomDto(
                        room.id(),
                        room.number(),
                        room.name(),
                        room.capacity(),
                        room.pricePerNight(),
                        comboBox.getValue()
                );
                roomsTable.getItems().set(index, updated);
            });

            return new MFXTableRowCell<>(r -> comboBox);
        });

        Collections.addAll(roomsTable.getTableColumns(),
                numberColumn, nameColumn, capacityColumn, priceColumn, statusColumn);
    }

    private void loadRooms() {
        try {
            List<RoomDto> rooms = roomService.getAllRooms();
            roomsTable.setItems(FXCollections.observableArrayList(rooms));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveStatuses() {
        for (RoomDto room : roomsTable.getItems()) {
            try {
                roomService.updateRoomStatus(room.id(), room.status());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        // Opcjonalnie: odśwież tabelę po zapisie
        loadRooms();
    }
}


