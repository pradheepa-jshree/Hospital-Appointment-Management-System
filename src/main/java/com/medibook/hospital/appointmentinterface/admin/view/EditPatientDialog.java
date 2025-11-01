package com.medibook.hospital.appointmentinterface.admin.view;

import com.medibook.hospital.appointmentinterface.model.Patient;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;

public class EditPatientDialog extends Dialog<Patient> {

    /**
     * Creates a dialog pre-filled with an existing patient's data.
     * @param patient The patient to edit.
     */
    public EditPatientDialog(Patient patient) {
        setTitle("Edit Patient Details");
        setHeaderText("Update the information for " + patient.getFullName());

        // Setup Buttons
        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create UI Components
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField fullNameField = new TextField(patient.getFullName());
        DatePicker dobPicker = new DatePicker(patient.getDateOfBirth());
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        genderComboBox.setValue(patient.getGender());
        TextField phoneField = new TextField(patient.getPhoneNumber());
        TextField addressField = new TextField(patient.getAddress());

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Date of Birth:"), 0, 1);
        grid.add(dobPicker, 1, 1);
        grid.add(new Label("Gender:"), 0, 2);
        grid.add(genderComboBox, 1, 2);
        grid.add(new Label("Phone Number:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);

        getDialogPane().setContent(grid);

        // When the "Save Changes" button is clicked, update the original patient object
        // and return it as the result.
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // --- THIS IS THE CORRECTED CODE ---
                // Instead of calling patient.setFullName(), we get the property and call .set() on it.
                patient.fullNameProperty().set(fullNameField.getText());
                patient.dateOfBirthProperty().set(dobPicker.getValue());
                patient.genderProperty().set(genderComboBox.getValue());
                patient.phoneNumberProperty().set(phoneField.getText());
                patient.addressProperty().set(addressField.getText());
                return patient;
            }
            return null;
        });
    }
}