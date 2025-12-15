package com.reservio.reservation_system.reservationsystemdesktop.controller;

import com.reservio.reservation_system.reservationsystemdesktop.model.user.EmployeeDto;
import com.reservio.reservation_system.reservationsystemdesktop.service.UserService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UsersController implements Initializable {

    @FXML private MFXTableView<EmployeeDto> tableUsers;
    @FXML private MFXTextField txtFirstName;
    @FXML private MFXTextField txtLastName;
    @FXML private MFXTextField txtEmail;
    @FXML private MFXTextField txtPhone;
    @FXML private MFXTextField txtCity;
    @FXML private MFXTextField txtZipCode;
    @FXML private MFXTextField txtStreet;
    @FXML private PasswordField txtPassword;
    @FXML private MFXButton btnSave;
    @FXML private MFXButton btnPasswordReset;
    @FXML private MFXButton btnAddUser;

    private final UserService userService;
    private EmployeeDto selectedUser;
    private boolean isAddingNewUser = false;

    @Inject
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumns();
        initTableSelection();
        initActions();
        loadUsers();
        setDetailsDisabled(true);

        tableUsers.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue() - 2;
            int columnsCount = tableUsers.getTableColumns().size();
            if (columnsCount == 0) return;
            double columnWidth = totalWidth / columnsCount;
            tableUsers.getTableColumns().forEach(col -> {
                col.setPrefWidth(columnWidth);
                col.setMinWidth(columnWidth);
                col.setMaxWidth(columnWidth);
            });
        });
    }

    private void setupColumns() {
        MFXTableColumn<EmployeeDto> firstNameCol = new MFXTableColumn<>("Imię", true);
        firstNameCol.setRowCellFactory(u -> new MFXTableRowCell<>(EmployeeDto::firstName));

        MFXTableColumn<EmployeeDto> lastNameCol = new MFXTableColumn<>("Nazwisko", true);
        lastNameCol.setRowCellFactory(u -> new MFXTableRowCell<>(EmployeeDto::lastName));

        MFXTableColumn<EmployeeDto> emailCol = new MFXTableColumn<>("Email", true);
        emailCol.setRowCellFactory(u -> new MFXTableRowCell<>(EmployeeDto::email));

        tableUsers.getTableColumns().setAll(firstNameCol, lastNameCol, emailCol);
    }

    private void initTableSelection() {
        tableUsers.getSelectionModel().setAllowsMultipleSelection(false);
        tableUsers.getSelectionModel().selectionProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                selectedUser = newVal.values().stream().findFirst().orElse(null);
                if (selectedUser != null) {
                    isAddingNewUser = false;
                    fillUserDetails(selectedUser);
                    btnSave.setText("Zapisz zmiany");
                    btnPasswordReset.setDisable(false);
                    setDetailsDisabled(false);
                }
            } else {
                // Odznaczenie – jeśli nie jesteśmy w trybie dodawania
                if (!isAddingNewUser) {
                    selectedUser = null;
                    clearUserDetails();
                    btnSave.setText("Zapisz zmiany");
                    btnPasswordReset.setDisable(true);
                    setDetailsDisabled(true);
                }
            }
        });
    }

    private void loadUsers() {
        List<EmployeeDto> users = userService.getAllEmployees();
        tableUsers.getItems().clear();
        tableUsers.getItems().addAll(users);
    }

    private void fillUserDetails(EmployeeDto user) {
        txtFirstName.setText(safeString(user.firstName()));
        txtLastName.setText(safeString(user.lastName()));
        txtEmail.setText(safeString(user.email()));
        txtPhone.setText(safeString(user.phoneNumber()));
        txtCity.setText(safeString(user.city()));
        txtZipCode.setText(safeString(user.zipCode()));
        txtStreet.setText(safeString(user.street()));
        txtPassword.clear();
    }

    private void clearUserDetails() {
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtCity.clear();
        txtZipCode.clear();
        txtStreet.clear();
        txtPassword.clear();
    }

    private void setDetailsDisabled(boolean disabled) {
        txtFirstName.setDisable(disabled);
        txtLastName.setDisable(disabled);
        txtEmail.setDisable(disabled);
        txtPhone.setDisable(disabled);
        txtCity.setDisable(disabled);
        txtZipCode.setDisable(disabled);
        txtStreet.setDisable(disabled);
        txtPassword.setDisable(disabled);
        btnSave.setDisable(disabled);
        btnPasswordReset.setDisable(disabled);
    }

    private void initActions() {
        btnSave.setOnAction(e -> saveUserChanges());
        btnPasswordReset.setOnAction(e -> resetPassword());
        btnAddUser.setOnAction(e -> startAddingNewUser());
    }

    // KLIKNIĘCIE "Dodaj użytkownika"
    private void startAddingNewUser() {
        // Jeśli są niezapisane zmiany – zapytaj
        if (selectedUser != null && isFormDirty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Niezapisane zmiany");
            alert.setHeaderText("Masz niezapisane zmiany w danych użytkownika.");
            alert.setContentText("Zapisać przed dodaniem nowego?");

            ButtonType save = new ButtonType("Zapisz");
            ButtonType discard = new ButtonType("Odrzuć");
            ButtonType cancel = ButtonType.CANCEL;

            alert.getButtonTypes().setAll(save, discard, cancel);

            alert.showAndWait().ifPresent(response -> {
                if (response == save) {
                    saveUserChanges();
                } else if (response == cancel) {
                    return; // nie rób nic
                }
                // discard lub po zapisie – kontynuuj
                proceedToAddNewUser();
            });
        } else {
            proceedToAddNewUser();
        }
    }

    private void proceedToAddNewUser() {
        isAddingNewUser = true;
        selectedUser = null;

        tableUsers.getSelectionModel().clearSelection();
        clearUserDetails();

        btnSave.setText("Zapisz nowego użytkownika");
        btnPasswordReset.setDisable(true);

        setDetailsDisabled(false);

        txtFirstName.requestFocus();
    }

    // ZAPIS – działa dla edycji i dodawania
    private void saveUserChanges() {
        EmployeeDto dto = new EmployeeDto(
                isAddingNewUser ? null : selectedUser.id(),
                txtFirstName.getText().isBlank() ? null : txtFirstName.getText().trim(),
                txtLastName.getText().isBlank() ? null : txtLastName.getText().trim(),
                txtEmail.getText().isBlank() ? null : txtEmail.getText().trim(),
                txtPhone.getText().isBlank() ? null : txtPhone.getText().trim(),
                txtCity.getText().isBlank() ? null : txtCity.getText().trim(),
                txtZipCode.getText().isBlank() ? null : txtZipCode.getText().trim(),
                txtStreet.getText().isBlank() ? null : txtStreet.getText().trim()
        );

        if (isAddingNewUser) {
            userService.createUserByAdmin(dto);
        } else {
            userService.updateUserByAdmin(dto);
        }

        loadUsers();

        // Po zapisie wracamy do trybu normalnego
        isAddingNewUser = false;
        selectedUser = null;
        tableUsers.getSelectionModel().clearSelection();
        clearUserDetails();
        btnSave.setText("Zapisz zmiany");
        btnPasswordReset.setDisable(true);
        setDetailsDisabled(true);
    }

    private void resetPassword() {
        if (selectedUser == null || isAddingNewUser) return;

        String newPassword = txtPassword.getText();
        if (newPassword == null || newPassword.isBlank()) return;

        userService.changeUserPasswordByAdmin(selectedUser.id(), newPassword);
        txtPassword.clear();
    }

    private boolean isFormDirty() {
        if (selectedUser == null) return false;

        return !safeString(selectedUser.firstName()).equals(txtFirstName.getText().trim()) ||
               !safeString(selectedUser.lastName()).equals(txtLastName.getText().trim()) ||
               !safeString(selectedUser.email()).equals(txtEmail.getText().trim()) ||
               !safeString(selectedUser.phoneNumber()).equals(txtPhone.getText().trim()) ||
               !safeString(selectedUser.city()).equals(txtCity.getText().trim()) ||
               !safeString(selectedUser.zipCode()).equals(txtZipCode.getText().trim()) ||
               !safeString(selectedUser.street()).equals(txtStreet.getText().trim());
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}