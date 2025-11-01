package com.medibook.hospital.appointmentinterface.doctor.view;

import com.medibook.hospital.appointmentinterface.dao.AppointmentDAO;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DoctorDashboardView {

    private Label totalAppointmentsValueLabel;
    private Label completedAppointmentsValueLabel;
    private Label pendingAppointmentsValueLabel;

    private final AppointmentDAO appointmentDAO;

    public DoctorDashboardView() {
        this.appointmentDAO = new AppointmentDAO();
    }

    public Node getView(Runnable onScheduleClick, Runnable onPatientSearchClick) {
        VBox view = new VBox(30);
        Label title = new Label("Dashboard");
        title.getStyleClass().add("page-title");
        HBox statsBox = createStatsBox();
        Node actionItemsBox = createActionItemsSection();
        Node quickLinksBox = createQuickLinksSection(onScheduleClick, onPatientSearchClick);
        view.getChildren().addAll(title, statsBox, actionItemsBox, quickLinksBox);
        return view;
    }

    private HBox createStatsBox() {
        HBox statsBox = new HBox(20);
        totalAppointmentsValueLabel = new Label("...");
        completedAppointmentsValueLabel = new Label("...");
        pendingAppointmentsValueLabel = new Label("...");
        statsBox.getChildren().addAll(
                createStatCard("Total Appointments", totalAppointmentsValueLabel),
                createStatCard("Completed", completedAppointmentsValueLabel),
                createStatCard("Pending", pendingAppointmentsValueLabel)
        );
        return statsBox;
    }

    private Node createActionItemsSection() {
        VBox actionItemsContainer = new VBox(15);
        Label actionItemsTitle = new Label("Action Items");
        actionItemsTitle.getStyleClass().add("section-title");
        Node labResultsItem = createActionItem(FontAwesomeIcon.FLASK, "Review 3 Urgent Lab Results");
        Node messageItem = createActionItem(FontAwesomeIcon.ENVELOPE, "1 New Message from Dr. Smith");
        actionItemsContainer.getChildren().addAll(actionItemsTitle, labResultsItem, messageItem);
        return actionItemsContainer;
    }

    private Node createActionItem(FontAwesomeIcon iconName, String text) {
        FontAwesomeIconView icon = new FontAwesomeIconView(iconName);
        icon.getStyleClass().add("action-item-icon");
        Label label = new Label(text);
        label.getStyleClass().add("action-item-label");
        HBox itemBox = new HBox(15);
        itemBox.getChildren().addAll(icon, label);
        return itemBox;
    }

    private Node createQuickLinksSection(Runnable onScheduleClick, Runnable onPatientSearchClick) {
        HBox quickLinksBox = new HBox(15);
        Button viewScheduleBtn = new Button("View Full Schedule");
        Button patientSearchBtn = new Button("Patient Search");
        viewScheduleBtn.getStyleClass().add("action-button");
        patientSearchBtn.getStyleClass().add("action-button-secondary");
        viewScheduleBtn.setOnAction(e -> onScheduleClick.run());
        patientSearchBtn.setOnAction(e -> onPatientSearchClick.run());
        quickLinksBox.getChildren().addAll(viewScheduleBtn, patientSearchBtn);
        return quickLinksBox;
    }

    /**
     * UPDATED: Loads data on a background thread to keep the UI responsive.
     */
    public void loadDashboardData(int doctorId) {
        // --- STEP 1: Start a new background thread for the database queries ---
        new Thread(() -> {
            // This part runs in the background and does not freeze the UI
            int totalCount = appointmentDAO.getTotalAppointmentsToday(doctorId);
            int completedCount = appointmentDAO.getAppointmentsByStatusToday(doctorId, "Completed");
            // LOGIC FIX: Fetch pending count directly for accuracy
            int pendingCount = appointmentDAO.getAppointmentsByStatusToday(doctorId, "Pending");

            // --- STEP 2: Schedule the UI update on the main JavaFX thread ---
            Platform.runLater(() -> {
                // This part is guaranteed to run safely on the UI thread
                totalAppointmentsValueLabel.setText(String.valueOf(totalCount));
                completedAppointmentsValueLabel.setText(String.valueOf(completedCount));
                pendingAppointmentsValueLabel.setText(String.valueOf(pendingCount));
            });
        }).start();
    }

    private VBox createStatCard(String title, Label valueLabel) {
        VBox card = new VBox(5);
        card.getStyleClass().add("stat-card");
        valueLabel.getStyleClass().add("stat-value-label");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title-label");
        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }
}