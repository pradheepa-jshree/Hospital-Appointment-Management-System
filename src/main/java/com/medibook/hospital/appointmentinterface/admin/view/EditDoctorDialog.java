package com.medibook.hospital.appointmentinterface.admin.view;

import com.medibook.hospital.appointmentinterface.model.Doctor;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class EditDoctorDialog extends Dialog<Doctor> {

    public EditDoctorDialog(Doctor doctor) {
        setTitle("Edit Doctor Details");
        setHeaderText("Update information for " + doctor.getFullName());

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField fullNameField = new TextField(doctor.getFullName());
        TextField specializationField = new TextField(doctor.getSpecialization());
        ComboBox<String> statusComboBox = new ComboBox<>(FXCollections.observableArrayList("Active", "On Leave", "Inactive"));
        statusComboBox.setValue(doctor.getStatus());

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Specialization:"), 0, 1);
        grid.add(specializationField, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusComboBox, 1, 2);

        getDialogPane().setContent(grid);

        // When "Save Changes" is clicked, update the original doctor object's properties
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                doctor.fullNameProperty().set(fullNameField.getText());
                doctor.specializationProperty().set(specializationField.getText());
                doctor.statusProperty().set(statusComboBox.getValue());
                return doctor;
            }
            return null;
        });
    }
}