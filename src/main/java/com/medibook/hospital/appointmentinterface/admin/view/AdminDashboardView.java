// In: admin/view/AdminDashboardView.java
package com.medibook.hospital.appointmentinterface.admin.view;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdminDashboardView {
    private Label totalAppointmentsLabel;
    private Label completedAppointmentsLabel;
    private Label doctorsOnDutyLabel;

    public Node getView(Runnable onManageDoctorsClick, Runnable onManagePatientsClick, Runnable onMasterScheduleClick) {
        VBox view = new VBox(30);
        Label title = new Label("Administrative Dashboard");
        title.getStyleClass().add("page-title");
        HBox kpiBox = createKpiBox();
        VBox alertsBox = createAlertsBox();
        VBox quickLinksSection = createQuickLinksSection(onManageDoctorsClick, onManagePatientsClick, onMasterScheduleClick);
        view.getChildren().addAll(title, kpiBox, alertsBox, quickLinksSection);
        return view;
    }

    public void loadDashboardData() {
        totalAppointmentsLabel.setText("152");
        completedAppointmentsLabel.setText("110");
        doctorsOnDutyLabel.setText("14");
    }

    private HBox createKpiBox() {
        totalAppointmentsLabel = new Label("...");
        completedAppointmentsLabel = new Label("...");
        doctorsOnDutyLabel = new Label("...");
        HBox kpiBox = new HBox(20);
        kpiBox.getChildren().addAll(
                createStatCard("Total Appointments Today", totalAppointmentsLabel),
                createStatCard("Completed Today", completedAppointmentsLabel),
                createStatCard("Doctors on Duty", doctorsOnDutyLabel)
        );
        return kpiBox;
    }

    private VBox createAlertsBox() {
        VBox alertsBox = new VBox(10);
        Label alertsTitle = new Label("Actionable Items & Alerts");
        alertsTitle.getStyleClass().add("section-title");
        Label alert1 = new Label("• 2 Pending Doctor Approvals");
        Label alert2 = new Label("• 5 Unassigned Appointments for tomorrow");
        alert1.getStyleClass().add("action-item-label");
        alert2.getStyleClass().add("action-item-label");
        alertsBox.getChildren().addAll(alertsTitle, alert1, alert2);
        return alertsBox;
    }

    private VBox createQuickLinksSection(Runnable onManageDoctorsClick, Runnable onManagePatientsClick, Runnable onMasterScheduleClick) {
        Label quickLinksTitle = new Label("Quick Links");
        quickLinksTitle.getStyleClass().add("section-title");
        Button manageDoctorsBtn = new Button("Manage Doctors");
        Button managePatientsBtn = new Button("Manage Patients");
        Button scheduleBtn = new Button("Master Schedule");
        manageDoctorsBtn.getStyleClass().add("action-button");
        managePatientsBtn.getStyleClass().add("action-button");
        scheduleBtn.getStyleClass().add("action-button");
        manageDoctorsBtn.setOnAction(e -> onManageDoctorsClick.run());
        managePatientsBtn.setOnAction(e -> onManagePatientsClick.run());
        scheduleBtn.setOnAction(e -> onMasterScheduleClick.run());
        HBox quickLinksBox = new HBox(15, manageDoctorsBtn, managePatientsBtn, scheduleBtn);
        return new VBox(10, quickLinksTitle, quickLinksBox);
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