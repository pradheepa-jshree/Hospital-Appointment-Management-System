// In: patient/view/MyProfileView.java
package com.medibook.hospital.appointmentinterface.patient.view;

import com.medibook.hospital.appointmentinterface.dao.PatientDAO;
import com.medibook.hospital.appointmentinterface.model.Patient;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import com.medibook.hospital.appointmentinterface.dao.UserDAO;
import java.time.LocalDate;
import java.util.Optional;

public class MyProfileView {

    // --- Form components as class fields ---
    private TextField fullNameField;
    private DatePicker dobPicker;
    private ComboBox<String> genderComboBox;
    private TextField phoneField;
    private TextArea addressArea;
    private PatientDAO patientDAO;
    private int patientId;

    public Node getView(int patientId) {
        this.patientId = patientId;
        this.patientDAO = new PatientDAO();

        VBox view = new VBox(20);
        Label title = new Label("My Profile & Settings");
        title.getStyleClass().add("page-title");

        // --- Create all three sections ---
        GridPane profileFormGrid = createProfileForm(); // Renamed to avoid conflict
        VBox securitySection = createSecuritySection();
        VBox notificationsSection = createNotificationsSection();

        // --- Populate the main form with existing data ---
        loadPatientData();

        // --- Save Button for the main form ---
        Button saveButton = new Button("Save Changes");
        saveButton.getStyleClass().add("action-button");
        saveButton.setOnAction(e -> handleSaveChanges());

        // --- Incomplete Profile Warning ---
        Label warningLabel = new Label("Please complete your profile (Date of Birth and Phone Number) to enable appointment booking.");
        warningLabel.setStyle("-fx-text-fill: red; -fx-font-style: italic;");
        warningLabel.setVisible(false); // Initially hidden

        // Check if the profile is incomplete and show the warning if needed
        if (dobPicker.getValue() == null || phoneField.getText() == null || phoneField.getText().isEmpty()) {
            warningLabel.setVisible(true);
        }

        // --- Define Separators for visual distinction ---
        Separator separator1 = new Separator();
        Separator separator2 = new Separator();
        VBox.setMargin(separator1, new Insets(20, 0, 20, 0));
        VBox.setMargin(separator2, new Insets(20, 0, 20, 0));

        // --- Add ALL components to the main VBox in the correct order ---
        view.getChildren().addAll(
                title,
                warningLabel,
                profileFormGrid, // Use the profile form grid here
                saveButton,
                separator1,
                securitySection,
                separator2,
                notificationsSection
        );
        return view;
    }

    private GridPane createProfileForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);

        // Initialize components
        fullNameField = new TextField();
        dobPicker = new DatePicker();
        genderComboBox = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
        phoneField = new TextField();
        addressArea = new TextArea();
        addressArea.setPrefRowCount(3);
        addressArea.setWrapText(true);

        // Add components to grid
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Date of Birth:"), 0, 1);
        grid.add(dobPicker, 1, 1);
        grid.add(new Label("Gender:"), 0, 2);
        grid.add(genderComboBox, 1, 2);
        grid.add(new Label("Phone Number:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressArea, 1, 4);

        return grid;
    }

    private void loadPatientData() {
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient != null) {
            fullNameField.setText(patient.getFullName());
            dobPicker.setValue(patient.getDateOfBirth());
            genderComboBox.setValue(patient.getGender());
            phoneField.setText(patient.getPhoneNumber());
            addressArea.setText(patient.getAddress());
        }
    }

    private void handleSaveChanges() {
        // Validation: Check for required fields
        if (fullNameField.getText().isEmpty() || dobPicker.getValue() == null || phoneField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Full Name, Date of Birth, and Phone Number are required fields.");
            return;
        }

        boolean success = patientDAO.updatePatientProfile(
                patientId,
                fullNameField.getText(),
                dobPicker.getValue(),
                genderComboBox.getValue(),
                phoneField.getText(),
                addressArea.getText()
        );

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Your profile has been updated successfully.");
            // Optionally, refresh the view or disable the warning message
            // For now, the data is saved, and will be reloaded next time they visit the page.
        } else {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update your profile. Please try again.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // In MyProfileView.java, add these methods
// In: patient/view/MyProfileView.java

    private VBox createSecuritySection() {
        VBox securityBox = new VBox(10);
        Label securityTitle = new Label("Account Security");
        securityTitle.getStyleClass().add("section-title");

        Button changePasswordBtn = new Button("Change Password");
        changePasswordBtn.getStyleClass().add("action-button-secondary");

        // --- THIS IS THE FIX ---
        // The button now opens a dedicated dialog
        changePasswordBtn.setOnAction(e -> showChangePasswordDialog());

        securityBox.getChildren().addAll(securityTitle, changePasswordBtn);
        return securityBox;
    }

    /**
     * Creates and shows a dialog for changing the user's password.
     */
    private void showChangePasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");

        // --- Setup Buttons ---
        ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        // --- Create Form ---
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

        dialog.getDialogPane().setContent(grid);

        // --- Process Result ---
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == changeButtonType) {
            String currentPass = currentPasswordField.getText();
            String newPass = newPasswordField.getText();
            String confirmPass = confirmPasswordField.getText();

            // --- Validation ---
            if (currentPass.isEmpty() || newPass.isEmpty() || !newPass.equals(confirmPass)) {
                showAlert(Alert.AlertType.ERROR, "Password Error", "Please fill all fields. New passwords must match.");
                return;
            }

            // Get the user_id associated with this patient
            int userId = patientDAO.getUserIdByPatientId(patientId);
            if (userId == -1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not find an associated user account.");
                return;
            }

            // --- Call the DAO ---
            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.changePassword(userId, currentPass, newPass);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Your password has been changed.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Could not change your password. Please check your current password and try again.");
            }
        }
    }


    private VBox createNotificationsSection() {
        VBox notificationsBox = new VBox(10);
        Label notificationsTitle = new Label("Notification Preferences");
        notificationsTitle.getStyleClass().add("section-title");

        CheckBox emailCheckbox = new CheckBox("Receive email reminders for appointments");
        CheckBox smsCheckbox = new CheckBox("Receive SMS text message reminders");
        emailCheckbox.setSelected(true); // Default value

        notificationsBox.getChildren().addAll(notificationsTitle, emailCheckbox, smsCheckbox);
        return notificationsBox;
    }
}