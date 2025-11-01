package com.medibook.hospital.appointmentinterface.admin.view;

import com.medibook.hospital.appointmentinterface.dao.UserDAO;
import com.medibook.hospital.appointmentinterface.doctor.view.ChangePasswordDialog;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class AdminProfileView {

    private final UserDAO userDAO;

    public AdminProfileView() {
        this.userDAO = new UserDAO();
    }

    public Node getView(int adminId, String adminUsername) {
        VBox view = new VBox(30);
        view.setPadding(new Insets(20));

        Label title = new Label("My Profile & Settings");
        title.getStyleClass().add("page-title");

        // --- Profile Information Section ---
        GridPane profileGrid = new GridPane();
        profileGrid.setHgap(15);
        profileGrid.setVgap(15);

        // For an admin, the main piece of info is their username, which is not editable.
        Label usernameLabel = new Label(adminUsername);

        profileGrid.add(new Label("Username:"), 0, 0);
        profileGrid.add(usernameLabel, 1, 0);

        // --- Account Security Section ---
        Label securityTitle = new Label("Account Security");
        securityTitle.getStyleClass().add("section-title");
        Button changePasswordBtn = new Button("Change Password");
        changePasswordBtn.getStyleClass().add("action-button-secondary");
        VBox securityBox = new VBox(15, securityTitle, changePasswordBtn);

        view.getChildren().addAll(title, profileGrid, securityBox);

        // --- Button Action ---
        changePasswordBtn.setOnAction(e -> {
            // We can reuse the same ChangePasswordDialog from the doctor's portal
            ChangePasswordDialog dialog = new ChangePasswordDialog();
            dialog.showAndWait().ifPresent(passwords -> {
                String oldPassword = passwords.getKey();
                String newPassword = passwords.getValue();

                // Call the new, reusable method in UserDAO
                boolean success = userDAO.changePassword(adminId, oldPassword, newPassword);

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
