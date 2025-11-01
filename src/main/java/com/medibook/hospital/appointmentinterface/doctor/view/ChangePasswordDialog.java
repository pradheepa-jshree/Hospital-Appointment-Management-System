package com.medibook.hospital.appointmentinterface.doctor.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class ChangePasswordDialog extends Dialog<Pair<String, String>> {

    public ChangePasswordDialog() {
        setTitle("Change Password");
        setHeaderText("Enter your current and new password.");

        ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(currentPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        getDialogPane().setContent(grid);

        // Add validation before closing the dialog
        final Button changeButton = (Button) getDialogPane().lookupButton(changeButtonType);
        changeButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                new Alert(Alert.AlertType.ERROR, "New passwords do not match.").show();
                event.consume(); // Prevent the dialog from closing
            }
        });

        setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                return new Pair<>(currentPasswordField.getText(), newPasswordField.getText());
            }
            return null;
        });
    }
}
