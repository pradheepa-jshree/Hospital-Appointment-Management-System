package com.medibook.hospital.appointmentinterface.doctor.view;

import com.medibook.hospital.appointmentinterface.dao.DoctorDAO;
import com.medibook.hospital.appointmentinterface.model.Doctor;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class DoctorProfileView {

    private final DoctorDAO doctorDAO;

    public DoctorProfileView() {
        this.doctorDAO = new DoctorDAO();
    }

    /**
     * UPDATED: The getView method now takes the full loggedInDoctor object.
     * This avoids fetching the data from the database again.
     */
    public Node getView(Doctor loggedInDoctor) {
        VBox view = new VBox(30);
        view.setPadding(new Insets(20));

        Label title = new Label("My Profile & Settings");
        title.getStyleClass().add("page-title");

        // --- Profile Information Section ---
        GridPane profileGrid = new GridPane();
        profileGrid.setHgap(15);
        profileGrid.setVgap(15);

        // Use editable TextFields instead of read-only Labels
        TextField fullNameField = new TextField(loggedInDoctor.getFullName());
        TextField specializationField = new TextField(loggedInDoctor.getSpecialization());
        // Email and Status are typically not editable by the doctor themselves
        Label emailLabel = new Label(loggedInDoctor.getEmail());
        Label statusLabel = new Label(loggedInDoctor.getStatus());

        profileGrid.add(new Label("Full Name:"), 0, 0);
        profileGrid.add(fullNameField, 1, 0);
        profileGrid.add(new Label("Specialization:"), 0, 1);
        profileGrid.add(specializationField, 1, 1);
        profileGrid.add(new Label("Email:"), 0, 2);
        profileGrid.add(emailLabel, 1, 2);
        profileGrid.add(new Label("Status:"), 0, 3);
        profileGrid.add(statusLabel, 1, 3);

        Button saveProfileBtn = new Button("Save Profile Changes");
        saveProfileBtn.getStyleClass().add("action-button");

        // --- Account Security Section ---
        Label securityTitle = new Label("Account Security");
        securityTitle.getStyleClass().add("section-title");
        Button changePasswordBtn = new Button("Change Password");
        changePasswordBtn.getStyleClass().add("action-button-secondary");
        VBox securityBox = new VBox(15, securityTitle, changePasswordBtn);

        view.getChildren().addAll(title, profileGrid, saveProfileBtn, securityBox);

        // --- Button Actions ---

        saveProfileBtn.setOnAction(e -> {
            String newFullName = fullNameField.getText();
            String newSpecialization = specializationField.getText();

            boolean success = doctorDAO.updateDoctorProfile(loggedInDoctor.getId(), newFullName, newSpecialization);

            if (success) {
                // IMPORTANT: Update the in-memory object so the UI reflects the change immediately
                loggedInDoctor.fullNameProperty().set(newFullName);
                loggedInDoctor.specializationProperty().set(newSpecialization);
                showAlert("Success", "Your profile has been updated.");
            } else {
                showAlert("Error", "Failed to update profile.");
            }
        });

        changePasswordBtn.setOnAction(e -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog();
            dialog.showAndWait().ifPresent(passwords -> {
                String oldPassword = passwords.getKey();
                String newPassword = passwords.getValue();

                // Assumes your Doctor model now has a getUserId() method
                boolean success = doctorDAO.changePassword(loggedInDoctor.getUserId(), oldPassword, newPassword);

                if (success) {
                    showAlert("Success", "Your password has been changed successfully.");
                } else {
                    showAlert("Error", "Failed to change password. Please check your current password and try again.");
                }
            });
        });

        return view;
    }

    private void showAlert(String title, String message) {
        Alert.AlertType type = "Success".equals(title) ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}