package com.medibook.hospital.appointmentinterface.admin.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.Arrays;
import java.util.List;

public class RegisterDoctorDialog extends Dialog<List<String>> {

    public RegisterDoctorDialog() {
        setTitle("Register New Doctor");
        setHeaderText("Enter the new doctor's credentials.");

        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name (e.g., Dr. John Smith)");
        TextField specializationField = new TextField();
        specializationField.setPromptText("Specialization (e.g., Cardiology)");
        TextField emailField = new TextField();
        emailField.setPromptText("Email (will be their username)");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Initial Password");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Specialization:"), 0, 1);
        grid.add(specializationField, 1, 1);
        grid.add(new Label("Email/Username:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);

        getDialogPane().setContent(grid);

        // When "Register" is clicked, return a list of the entered values.
        setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                return Arrays.asList(
                        fullNameField.getText(),
                        specializationField.getText(),
                        emailField.getText(),
                        passwordField.getText()
                );
            }
            return null;
        });
    }
}