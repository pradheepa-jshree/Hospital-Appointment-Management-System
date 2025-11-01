package com.medibook.hospital.appointmentinterface.admin.view;

import com.medibook.hospital.appointmentinterface.dao.PatientDAO;
import com.medibook.hospital.appointmentinterface.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.Optional;

public class ManagePatientsView {

    private TableView<Patient> table;
    private final PatientDAO patientDAO;
    private ObservableList<Patient> patientList;

    public ManagePatientsView() {
        this.patientDAO = new PatientDAO();
    }

    public Node getView() {
        this.patientList = FXCollections.observableArrayList(patientDAO.getAllPatients());

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));

        // --- Header with Title and "Register" button ---
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));
        Label title = new Label("Manage Patients");
        title.getStyleClass().add("page-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button registerBtn = new Button("Register New Patient");
        registerBtn.getStyleClass().add("action-button");
        // --- FIX: This now correctly calls your original method ---
        registerBtn.setOnAction(e -> handleRegisterPatient());
        header.getChildren().addAll(title, spacer, registerBtn);
        layout.setTop(header);

        // --- Table View ---
        table = createPatientTable();
        table.setItems(patientList);
        layout.setCenter(table);

        return layout;
    }

    private TableView<Patient> createPatientTable() {
        TableView<Patient> tv = new TableView<>();
        TableColumn<Patient, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        TableColumn<Patient, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        TableColumn<Patient, LocalDate> dobCol = new TableColumn<>("Date of Birth");
        dobCol.setCellValueFactory(cellData -> cellData.getValue().dateOfBirthProperty());
        TableColumn<Patient, String> phoneCol = new TableColumn<>("Phone Number");
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        TableColumn<Patient, Void> actionCol = createActionColumn();
        tv.getColumns().addAll(idCol, nameCol, dobCol, phoneCol, actionCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    private TableColumn<Patient, Void> createActionColumn() {
        TableColumn<Patient, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(10, editBtn, deleteBtn);
            {
                pane.setAlignment(Pos.CENTER);
                editBtn.getStyleClass().add("action-button-secondary");
                deleteBtn.getStyleClass().add("action-button-secondary");

                editBtn.setOnAction(event -> {
                    Patient selectedPatient = getTableView().getItems().get(getIndex());
                    EditPatientDialog dialog = new EditPatientDialog(selectedPatient);
                    dialog.showAndWait().ifPresent(updatedPatient -> {
                        boolean success = patientDAO.updatePatient(updatedPatient);
                        if (success) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Patient details updated.");
                            refreshTable();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update patient details.");
                        }
                    });
                });

                deleteBtn.setOnAction(event -> {
                    Patient selectedPatient = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedPatient.getFullName() + "? This action is permanent and will delete their login.", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            boolean success = patientDAO.deletePatient(selectedPatient.getId());
                            if (success) {
                                showAlert(Alert.AlertType.INFORMATION, "Success", "Patient deleted.");
                                refreshTable();
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Delete Failed", "Could not delete patient.");
                            }
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
        return actionCol;
    }

    /**
     * --- FIX: This is your original registration logic, renamed and integrated ---
     * It handles showing the dialog for creating a NEW patient.
     */
    private void handleRegisterPatient() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Register New Patient");

        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        DatePicker dobPicker = new DatePicker();
        ComboBox<String> genderComboBox = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username (Email)");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Date of Birth:"), 0, 1);
        grid.add(dobPicker, 1, 1);
        grid.add(new Label("Gender:"), 0, 2);
        grid.add(genderComboBox, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(new Label("Username:"), 0, 5);
        grid.add(usernameField, 1, 5);
        grid.add(new Label("Password:"), 0, 6);
        grid.add(passwordField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == registerButtonType) {
            if (fullNameField.getText().isEmpty() || dobPicker.getValue() == null || usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Full Name, DOB, Username, and Password are required.");
                return;
            }

            // You will need to ensure a 'registerPatientWithCredentials' method exists in your PatientDAO
            int newUserId = patientDAO.registerPatientWithCredentials(
                    fullNameField.getText(),
                    dobPicker.getValue(),
                    genderComboBox.getValue(),
                    phoneField.getText(),
                    addressField.getText(),
                    usernameField.getText(),
                    passwordField.getText()
            );

            if (newUserId != -1) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Patient registered successfully.");
                refreshTable(); // Refresh the table
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Could not register patient. The username might already exist.");
            }
        }
    }

    private void refreshTable() {
        patientList.setAll(patientDAO.getAllPatients());
        table.refresh();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}