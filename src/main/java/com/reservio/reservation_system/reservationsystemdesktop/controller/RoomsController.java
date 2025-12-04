package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.reservio.reservation_system.reservationsystemdesktop.model.room.RoomDto;
import com.reservio.reservation_system.reservationsystemdesktop.service.RoomService;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class RoomsController implements Initializable {

    @FXML
    private MFXTableView<RoomDto> roomsTable;

    @Inject
    private RoomService roomService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadRooms();
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
        statusColumn.setRowCellFactory(room -> new MFXTableRowCell<>(RoomDto::status));

        Collections.addAll(roomsTable.getTableColumns(),
                numberColumn, nameColumn, capacityColumn, priceColumn, statusColumn);
    }

    private void loadRooms() {
        List<RoomDto> rooms = List.of(
                new RoomDto(1L, 101L, "Pokój A", 2, 120.0f, "Dostępny"),
                new RoomDto(2L, 102L, "Pokój B", 4, 200.0f, "Remont"),
                new RoomDto(3L, 103L, "Pokój C", 1, 80.0f, "Niedostępny")
        );

        roomsTable.setItems(FXCollections.observableArrayList(rooms));
    }
}
