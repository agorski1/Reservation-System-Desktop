package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.reservio.reservation_system.reservationsystemdesktop.model.RoomType.RoomTypeDto;
import com.reservio.reservation_system.reservationsystemdesktop.service.RoomTypeService;
import com.reservio.reservation_system.reservationsystemdesktop.util.Auth;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RoomTypesController implements Initializable {

    @FXML
    private MFXTableView<RoomTypeDto> roomTypesTable;

    @FXML
    private MFXButton btnSave;

    @Inject
    private RoomTypeService roomTypeService;

    private final Map<Long, BigDecimal> changedPrices = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadRoomTypes();

        roomTypesTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue() - 2;
            int columnsCount = roomTypesTable.getTableColumns().size();
            double columnWidth = totalWidth / columnsCount;

            roomTypesTable.getTableColumns().forEach(col -> {
                col.setPrefWidth(columnWidth);
                col.setMinWidth(columnWidth);
                col.setMaxWidth(columnWidth);
            });
        });
        btnSave.setOnAction(e -> saveChanges());
    }

    private void setupTableColumns() {
        roomTypesTable.getTableColumns().clear();

        MFXTableColumn<RoomTypeDto> nameColumn = new MFXTableColumn<>("Typ pokoju", true);
        nameColumn.setRowCellFactory(param -> new MFXTableRowCell<>(RoomTypeDto::name));

        MFXTableColumn<RoomTypeDto> capacityColumn = new MFXTableColumn<>("Pojemność", true);
        capacityColumn.setRowCellFactory(param -> new MFXTableRowCell<>(RoomTypeDto::capacity));

        MFXTableColumn<RoomTypeDto> priceColumn = new MFXTableColumn<>("Cena/Noc", true);
        priceColumn.setRowCellFactory(row -> new MFXTableRowCell<>(RoomTypeDto::pricePerNight));

        MFXTableColumn<RoomTypeDto> actionsCol = new MFXTableColumn<>("Akcje", true);
        actionsCol.setRowCellFactory(dto -> new MFXTableRowCell<RoomTypeDto, String>(r -> "") {
            {
                javafx.scene.control.Button changePriceBtn = new javafx.scene.control.Button("Zmień cenę");
                changePriceBtn.setStyle("-fx-font-size: 14; -fx-cursor: hand;");

                changePriceBtn.setOnAction(e -> {
                    RoomTypeDto roomType = dto;
                    if (roomType == null) return;

                    javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(roomType.pricePerNight().toPlainString());
                    dialog.setTitle("Zmień cenę");
                    dialog.setHeaderText("Typ pokoju: " + roomType.name());
                    dialog.setContentText("Nowa cena za noc:");

                    dialog.showAndWait().ifPresent(input -> {
                        try {
                            BigDecimal newPrice = new BigDecimal(input);
                            roomTypeService.updateRoomTypePrice(roomType.id(), newPrice);

                            RoomTypeDto updated = new RoomTypeDto(
                                    roomType.id(),
                                    roomType.name(),
                                    roomType.capacity(),
                                    newPrice,
                                    roomType.description(),
                                    roomType.amenities()
                            );

                            int index = roomTypesTable.getItems().indexOf(roomType);
                            roomTypesTable.getItems().set(index, updated);

                        } catch (NumberFormatException ex) {
                            System.out.println("Niepoprawna wartość: " + input);
                        }
                    });
                });

                HBox box = new HBox(changePriceBtn);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
                setText(null);
            }
        });

        roomTypesTable.getTableColumns().addAll(nameColumn, capacityColumn, priceColumn, actionsCol);
    }

    private void loadRoomTypes() {
        try {
            List<RoomTypeDto> roomTypes = roomTypeService.getAllRoomTypes();
            roomTypesTable.setItems(FXCollections.observableArrayList(roomTypes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void commitPriceChange(RoomTypeDto dto, BigDecimal newPrice) {
        if (newPrice.compareTo(dto.pricePerNight()) != 0) {
            changedPrices.put(dto.id(), newPrice);
        } else {
            changedPrices.remove(dto.id());
        }
    }

    @FXML
    private void saveChanges() {
        if (!Auth.isAdmin()) return;

        changedPrices.forEach((roomTypeId, newPrice) -> {
            try {
                roomTypeService.updateRoomTypePrice(roomTypeId, newPrice);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        changedPrices.clear();
    }
}
