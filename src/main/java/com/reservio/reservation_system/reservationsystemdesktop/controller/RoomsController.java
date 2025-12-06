package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.reservio.reservation_system.reservationsystemdesktop.model.room.RoomDto;
import com.reservio.reservation_system.reservationsystemdesktop.service.RoomService;
import com.reservio.reservation_system.reservationsystemdesktop.util.RoomStatusTranslator;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RoomsController implements Initializable {

    @FXML private MFXTableView<RoomDto> roomsTable;
    @FXML private MFXButton btnSaveStatuses;

    @Inject private RoomService roomService;

    private final Map<Long, String> changedStatuses = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadRooms();

        roomsTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue() - 2;
            int columnsCount = roomsTable.getTableColumns().size();
            double columnWidth = totalWidth / columnsCount;

            roomsTable.getTableColumns().forEach(col -> {
                col.setPrefWidth(columnWidth);
                col.setMinWidth(columnWidth);
                col.setMaxWidth(columnWidth);
            });
        });

        btnSaveStatuses.setOnAction(e -> saveStatuses());
    }

    private void setupTableColumns() {
        roomsTable.getTableColumns().clear();

        MFXTableColumn<RoomDto> numberColumn = new MFXTableColumn<>("Numer", true);
        numberColumn.setRowCellFactory(room -> new MFXTableRowCell<>(RoomDto::number));

        MFXTableColumn<RoomDto> nameColumn = new MFXTableColumn<>("Nazwa", true);
        nameColumn.setRowCellFactory(room -> new MFXTableRowCell<>(RoomDto::name));

        MFXTableColumn<RoomDto> capacityColumn = new MFXTableColumn<>("Pojemność", true);
        capacityColumn.setRowCellFactory(room -> new MFXTableRowCell<>(RoomDto::capacity));

        MFXTableColumn<RoomDto> priceColumn = new MFXTableColumn<>("Cena/Noc", true);
        priceColumn.setRowCellFactory(room -> new MFXTableRowCell<>(RoomDto::pricePerNight));

        MFXTableColumn<RoomDto> statusColumn = new MFXTableColumn<>("Status", true);
        statusColumn.setRowCellFactory(row -> {
            MFXTableRowCell<RoomDto, String> cell = new MFXTableRowCell<>(RoomDto::status);
            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    RoomDto dto = row;
                    ComboBox<String> comboBox = new ComboBox<>(
                            FXCollections.observableArrayList(RoomStatusTranslator.POLISH_STATUSES)
                    );
                    comboBox.setValue(dto.status());
                    comboBox.setMinWidth(130);

                    cell.setText(null);
                    cell.setGraphic(comboBox);
                    comboBox.show();

                    comboBox.setOnAction(e -> {
                        commitStatusChange(dto, comboBox.getValue(), cell);
                        cell.setGraphic(null);
                        cell.setText(comboBox.getValue());
                    });

                    comboBox.focusedProperty().addListener((obs, was, isNow) -> {
                        if (!isNow) {
                            cell.setGraphic(null);
                            cell.setText(dto.status());
                        }
                    });
                }
            });
            return cell;
        });

        roomsTable.getTableColumns().addAll(
                numberColumn,
                nameColumn,
                capacityColumn,
                priceColumn,
                statusColumn
        );

    }

    private void commitStatusChange(RoomDto original, String newPolishStatus, MFXTableRowCell<RoomDto, String> cell) {
        if (newPolishStatus == null) newPolishStatus = original.status();

        if (newPolishStatus.equals(original.status())) {
            cell.setGraphic(null);
            cell.setText(newPolishStatus);

            changedStatuses.remove(original.id());
            return;
        }

        int index = roomsTable.getItems().indexOf(original);
        if (index >= 0) {
            RoomDto updated = new RoomDto(
                    original.id(),
                    original.number(),
                    original.name(),
                    original.capacity(),
                    original.pricePerNight(),
                    newPolishStatus
            );
            roomsTable.getItems().remove(index);
            roomsTable.getItems().add(index, updated);

            changedStatuses.put(updated.id(), newPolishStatus);
        }

        cell.setText(newPolishStatus);
    }

    private void loadRooms() {
        try {
            List<RoomDto> rooms = roomService.getAllRooms();

            List<RoomDto> translatedRooms = rooms.stream()
                    .map(r -> new RoomDto(
                            r.id(),
                            r.number(),
                            r.name(),
                            r.capacity(),
                            r.pricePerNight(),
                            RoomStatusTranslator.toPolish(r.status())
                    ))
                    .toList();

            roomsTable.setItems(FXCollections.observableArrayList(translatedRooms));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveStatuses() {
        for (Map.Entry<Long, String> entry : changedStatuses.entrySet()) {
            try {
                String english = RoomStatusTranslator.toEnglish(entry.getValue());
                roomService.updateRoomStatus(entry.getKey(), english);
                System.out.println("ID = " + entry.getKey() +
                        "  STATUS = " + entry.getValue() +
                        "  EN = " + english);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        changedStatuses.clear();
    }
}
