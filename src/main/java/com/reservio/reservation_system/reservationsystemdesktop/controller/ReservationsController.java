package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.reservio.reservation_system.reservationsystemdesktop.model.reservation.ReservationDesktopDto;
import com.reservio.reservation_system.reservationsystemdesktop.model.reservation.ReservationDto;
import com.reservio.reservation_system.reservationsystemdesktop.service.ReservationService;
import com.reservio.reservation_system.reservationsystemdesktop.util.ReservationStatusTranslator;
import com.reservio.reservation_system.reservationsystemdesktop.util.SceneManager;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class ReservationsController implements Initializable {

    @FXML
    private MFXTableView<ReservationDesktopDto> reservationsTable;
    @FXML
    private MFXTextField txtSearchClient;
    @FXML
    private MFXDatePicker dateFrom;
    @FXML
    private MFXDatePicker dateTo;
    @FXML
    private MFXToggleButton btnShowCompleted;
    @FXML
    private MFXButton btnCreateReservation;

    @Inject
    private ReservationService reservationService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadReservations();

        reservationsTable.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue() - 2;
            int columnsCount = reservationsTable.getTableColumns().size();
            double columnWidth = totalWidth / columnsCount;

            reservationsTable.getTableColumns().forEach(col -> {
                col.setPrefWidth(columnWidth);
                col.setMinWidth(columnWidth);
                col.setMaxWidth(columnWidth);
            });
        });

        txtSearchClient.textProperty().addListener(o -> loadReservations());
        dateFrom.valueProperty().addListener(o -> loadReservations());
        dateTo.valueProperty().addListener(o -> loadReservations());
        btnShowCompleted.selectedProperty().addListener(o -> loadReservations());

        btnCreateReservation.setOnAction(e -> openCreateReservationScene());
    }

    private void setupTableColumns() {
        reservationsTable.getTableColumns().clear();

        var clientCol = new MFXTableColumn<ReservationDesktopDto>("Klient", true);
        clientCol.setRowCellFactory(r -> new MFXTableRowCell<>(ReservationDesktopDto::clientName));

        var inCol = new MFXTableColumn<ReservationDesktopDto>("Przyjazd", true);
        inCol.setRowCellFactory(r -> new MFXTableRowCell<>(d -> d.checkInDate().toLocalDate().toString()));

        var outCol = new MFXTableColumn<ReservationDesktopDto>("Wyjazd", true);
        outCol.setRowCellFactory(r -> new MFXTableRowCell<>(d -> d.checkOutDate().toLocalDate().toString()));

        var statusCol = new MFXTableColumn<ReservationDesktopDto>("Status", true);
        statusCol.setRowCellFactory(r -> new MFXTableRowCell<>(ReservationDesktopDto::statusPolish));

        // Tymczasowo dodajemy pustą kolumnę akcji – zaraz będziemy ją nadpisywać
        var actionsCol = new MFXTableColumn<ReservationDesktopDto>("Akcje", true);
        actionsCol.setPrefWidth(120); // opcjonalnie stała szerokość

        reservationsTable.getTableColumns().addAll(clientCol, inCol, outCol, statusCol, actionsCol);
    }

    private void loadReservations() {
        Platform.runLater(() -> {
            try {
                String rawQuery = txtSearchClient.getText();
                String query = (rawQuery == null) ? "" : rawQuery.trim();

                String phone = null;
                String email = null;

                if (!query.isEmpty()) {
                    if (query.contains("@")) {
                        email = query.toLowerCase();
                    } else {
                        phone = query;
                    }
                }

                LocalDate fromDate = dateFrom.getValue();
                LocalDate toDate = dateTo.getValue();

                LocalDateTime fromDt = fromDate != null ? fromDate.atStartOfDay() : null;
                LocalDateTime toDt = toDate != null ? toDate.atTime(23, 59, 59, 999999999) : null;

                List<ReservationDto> list = reservationService.getReservations(
                        btnShowCompleted.isSelected(),
                        fromDt,
                        toDt,
                        phone,
                        email
                );

                var display = list.stream()
                        .map(r -> new ReservationDesktopDto(
                                r.id(),
                                r.firstName() + " " + r.lastName(),
                                r.phoneNumber(),
                                r.email(),
                                r.roomType(),
                                r.guestCount(),
                                ReservationStatusTranslator.toPolish(r.status()),
                                r.status(),
                                r.checkInDate(),
                                r.checkOutDate()
                        ))
                        .toList();

                updateActionsColumn();
                reservationsTable.getItems().setAll(display);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void openCreateReservationScene() {
        try {
            SceneManager.loadIntoMainContent("/fxml/newReservation.fxml", controller -> {
                if (controller instanceof com.reservio.reservation_system.reservationsystemdesktop.controller.NewReservationController newResController) {
                    System.out.println("NewReservationController załadowany poprawnie!");
                    // Tu możesz wywołać jakieś metody inizjalizujące, np. newResController.initData(...);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openReservationDetails(long id) {
        try {
            SceneManager.loadIntoMainContent("/fxml/reservationDetails.fxml", controller -> {
                if (controller instanceof ReservationDetailsController detailsController) {
                    detailsController.loadReservation(id);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateActionsColumn() {
        // Znajdujemy ostatnią kolumnę (Akcje) – zakładamy, że jest na pozycji 4
        var actionsCol = (MFXTableColumn<ReservationDesktopDto>) reservationsTable.getTableColumns().get(4);

        actionsCol.setRowCellFactory(dto -> new MFXTableRowCell<ReservationDesktopDto, String>(r -> "") {
            {
                HBox box = new HBox(8);
                box.setAlignment(Pos.CENTER);

                // Zawsze przycisk szczegółów
                Label view = new Label("[>]");
                String baseStyle = "-fx-font-size: 18; -fx-cursor: hand; -fx-padding: 0 8 0 8;";
                view.setStyle(baseStyle + "-fx-text-fill: blue;");
                view.setOnMouseClicked(e -> openReservationDetails(dto.id()));
                box.getChildren().add(view);

                // Pokazujemy ✓ i ✗ TYLKO gdy toggle jest WYŁĄCZONY (nie pokazujemy zakończonych)
                if (!btnShowCompleted.isSelected()) {
                    Label confirm = new Label("[✓]");
                    Label reject = new Label("[X]");

                    confirm.setStyle(baseStyle + "-fx-text-fill: green;");
                    reject.setStyle(baseStyle + "-fx-text-fill: red;");

                    confirm.setOnMouseClicked(e -> {
                        try {
                            reservationService.updateReservationStatus(dto.id(), "Confirmed");
                            loadReservations(); // odświeżamy całą listę po zmianie
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });

                    reject.setOnMouseClicked(e -> {
                        try {
                            reservationService.updateReservationStatus(dto.id(), "Rejected");
                            loadReservations(); // odświeżamy
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });

                    box.getChildren().add(0, reject);
                    box.getChildren().add(0, confirm);
                }

                setGraphic(box);
                setText(null);
            }
        });

        // Ważne: odświeżamy widok tabeli, żeby nowe komórki się wyrenderowały

reservationsTable.getItems().setAll(reservationsTable.getItems());
    }
}