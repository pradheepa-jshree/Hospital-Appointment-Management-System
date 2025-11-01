// Create this new file: patient/view/AppointmentConfirmationView.java
package com.medibook.hospital.appointmentinterface.patient.view;

import com.medibook.hospital.appointmentinterface.dao.AppointmentDAO;
import com.medibook.hospital.appointmentinterface.patient.view.DoctorScheduleSelectionView.AppointmentSelection;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AppointmentConfirmationView {

    private AppointmentSelection selection;
    private int patientId;

    public AppointmentConfirmationView(AppointmentSelection selection, int patientId) {
        this.selection = selection;
        this.patientId = patientId;
    }

    public Node getView(Runnable onBookingConfirmed, Runnable onGoBack) {
        VBox view = new VBox(20);

        Label title = new Label("Confirm Your Appointment");
        title.getStyleClass().add("page-title");

        // --- Summary View ---
        GridPane summaryGrid = new GridPane();
        summaryGrid.getStyleClass().add("profile-grid"); // Reuse profile grid style

        addRow(summaryGrid, 0, "Doctor:", selection.doctor.getFullName() + " (" + selection.doctor.getSpecialization() + ")");
        addRow(summaryGrid, 1, "Date:", selection.date.toString());
        addRow(summaryGrid, 2, "Time:", selection.time.toString());
        // You can add a location if you add it to your doctors table
        addRow(summaryGrid, 3, "Location:", "Main Clinic, 123 Health St.");

        // --- Reason for Visit ---
        Label reasonLabel = new Label("Reason for Visit (Optional):");
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("e.g., Annual checkup, follow-up, new symptom...");
        reasonArea.setWrapText(true);
        reasonArea.setPrefRowCount(3);

        VBox reasonBox = new VBox(5, reasonLabel, reasonArea);

        // --- Action Buttons ---
        Button confirmBtn = new Button("Confirm & Book");
        confirmBtn.getStyleClass().add("action-button");

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("action-button-secondary");

        HBox buttonBox = new HBox(15, backBtn, confirmBtn);

        // --- Button Actions ---
        backBtn.setOnAction(e -> onGoBack.run());
        confirmBtn.setOnAction(e -> {
            AppointmentDAO appointmentDAO = new AppointmentDAO();
            boolean success = appointmentDAO.bookAppointment(
                    selection.doctor.getId(),
                    patientId,
                    selection.date,
                    selection.time,
                    reasonArea.getText()
            );

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Your appointment has been booked! You will see it listed under 'My Appointments'.");
                onBookingConfirmed.run(); // Navigate back to the main appointments list
            } else {
                showAlert(Alert.AlertType.ERROR, "Booking Failed", "Could not book the appointment. The slot may have just been taken. Please try again.");
            }
        });

        view.getChildren().addAll(title, summaryGrid, reasonBox, buttonBox);
        return view;
    }

    private void addRow(GridPane grid, int row, String labelText, String valueText) {
        Label label = new Label(labelText);
        label.getStyleClass().add("profile-label");
        Label value = new Label(valueText);
        value.getStyleClass().add("profile-value");
        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}