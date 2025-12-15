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
import javafx.scene.control.PasswordField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UsersController implements Initializable {

    @FXML
    private MFXTableView<EmployeeDto> tableUsers;

    @FXML
    private MFXTextField txtSearchClient;

    @FXML
    private MFXTextField txtFirstName;
    @FXML
    private MFXTextField txtLastName;
    @FXML
    private MFXTextField txtEmail;
    @FXML
    private MFXTextField txtPhone;
    @FXML
    private MFXTextField txtCity;
    @FXML
    private MFXTextField txtZipCode;
    @FXML
    private MFXTextField txtStreet;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private MFXButton btnSave;
    @FXML
    private MFXButton btnPasswordReset;
    @FXML
    private MFXButton btnAddUser;

    private final UserService userService;

    private EmployeeDto selectedUser;

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

        // DOKŁADNIE TAK SAMO JAK W ROOMSCONTROLLER
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

    private void distributeColumnsEvenly() {
        double totalWidth = tableUsers.getWidth() - 2;
        int columnsCount = tableUsers.getTableColumns().size();
        if (columnsCount == 0 || totalWidth <= 0) return;

        double columnWidth = totalWidth / columnsCount;

        tableUsers.getTableColumns().forEach(col -> {
            col.setPrefWidth(columnWidth);
            col.setMinWidth(100);
            col.setMaxWidth(Double.MAX_VALUE);
        });
    }

    private void setupColumns() {

        MFXTableColumn<EmployeeDto> firstNameCol =
                new MFXTableColumn<>("Imię", true);
        firstNameCol.setRowCellFactory(u ->
                new MFXTableRowCell<>(EmployeeDto::firstName));

        MFXTableColumn<EmployeeDto> lastNameCol =
                new MFXTableColumn<>("Nazwisko", true);
        lastNameCol.setRowCellFactory(u ->
                new MFXTableRowCell<>(EmployeeDto::lastName));

        MFXTableColumn<EmployeeDto> emailCol =
                new MFXTableColumn<>("Email", true);
        emailCol.setRowCellFactory(u ->
                new MFXTableRowCell<>(EmployeeDto::email));

        tableUsers.getTableColumns().setAll(
                firstNameCol,
                lastNameCol,
                emailCol
        );
    }

    private void initTableSelection() {
        tableUsers.getSelectionModel().setAllowsMultipleSelection(false);

        tableUsers.getSelectionModel().selectionProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null && !newVal.isEmpty()) {
                        selectedUser = newVal.values()
                                .stream()
                                .findFirst()
                                .orElse(null);

                        if (selectedUser != null) {
                            fillUserDetails(selectedUser);
                            setDetailsDisabled(false);
                        }
                    }
                });
    }

    private void loadUsers() {
        List<EmployeeDto> users = userService.getAllEmployees();
        tableUsers.getItems().setAll(users);
    }

    private void fillUserDetails(EmployeeDto user) {
        txtFirstName.setText(user.firstName());
        txtLastName.setText(user.lastName());
        txtEmail.setText(user.email());
        txtPhone.setText(user.phoneNumber());
        txtCity.setText(user.city());
        txtZipCode.setText(user.zipCode());
        txtStreet.setText(user.street());
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
    }

    private void saveUserChanges() {
        if (selectedUser == null) return;

        EmployeeDto updated = new EmployeeDto(
                selectedUser.id(),
                txtFirstName.getText(),
                txtLastName.getText(),
                txtEmail.getText(),
                txtPhone.getText(),
                txtCity.getText(),
                txtZipCode.getText(),
                txtStreet.getText()
        );

        System.out.println("Saving user: " + updated);

        // TODO: userService.updateUser(updated);
    }

    private void resetPassword() {
        if (selectedUser == null) return;

        String newPassword = txtPassword.getText();
        if (newPassword == null || newPassword.isBlank()) return;

        userService.changeUserPasswordByAdmin(
                selectedUser.id(),
                newPassword
        );

        txtPassword.clear();
        System.out.println("Password reset for userId=" + selectedUser.id());
    }
}
