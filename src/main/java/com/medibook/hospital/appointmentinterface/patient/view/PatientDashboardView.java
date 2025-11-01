// In: patient/view/PatientDashboardView.java
package com.medibook.hospital.appointmentinterface.patient.view;

import com.medibook.hospital.appointmentinterface.dao.AppointmentDAO;
import com.medibook.hospital.appointmentinterface.model.Appointment;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PatientDashboardView {

    public Node getView(String patientName, Runnable bookAppointmentAction, Runnable viewAppointmentsAction, Runnable viewProfileAction) {
        VBox view = new VBox(40);
        view.setAlignment(Pos.TOP_CENTER);
        view.setPadding(new Insets(20, 0, 0, 0)); // Add some top padding

        // --- Welcome Message ---
        Label welcomeLabel = new Label("Welcome back, " + patientName.split(" ")[0] + "!");
        welcomeLabel.getStyleClass().add("page-title");

        // --- Upcoming Appointment Card ---
        VBox appointmentCard = createUpcomingAppointmentCard(patientName);

        // --- Quick Links/Actions ---
        Button bookBtn = new Button("Book a New Appointment");
        bookBtn.getStyleClass().add("action-button");
        bookBtn.setPrefWidth(250);
        bookBtn.setOnAction(e -> bookAppointmentAction.run());

        Button viewAllBtn = new Button("View All My Appointments");
        viewAllBtn.getStyleClass().add("action-button-secondary");
        viewAllBtn.setPrefWidth(250);
        viewAllBtn.setOnAction(e -> viewAppointmentsAction.run());

        Button viewProfileBtn = new Button("View My Profile");
        viewProfileBtn.getStyleClass().add("action-button-secondary");
        viewProfileBtn.setPrefWidth(250);
        viewProfileBtn.setOnAction(e -> viewProfileAction.run());

        VBox buttonBox = new VBox(15, bookBtn, viewAllBtn, viewProfileBtn);
        buttonBox.setAlignment(Pos.CENTER);

        view.getChildren().addAll(welcomeLabel, appointmentCard, buttonBox);
        return view;
    }

    private VBox createUpcomingAppointmentCard(String patientName) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stat-card"); // Reuse the stat-card style for a consistent look
        card.setPadding(new Insets(20));
        card.setMaxWidth(400);

        Label cardTitle = new Label("Your Next Appointment");
        cardTitle.getStyleClass().add("section-title");
        card.getChildren().add(cardTitle);

        // Fetch the data from the DAO
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        // We need the patient's ID, not their name. We'll handle this in the main portal view.
        // For now, this logic will be moved. We will pass the Appointment object in.

        // This is a placeholder until we update the main view.
        card.getChildren().add(new Label("Fetching appointment details..."));

        return card;
    }
}