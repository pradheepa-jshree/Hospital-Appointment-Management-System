package com.medibook.hospital.appointmentinterface.doctor.view;

import com.medibook.hospital.appointmentinterface.dao.AppointmentDAO;
import com.medibook.hospital.appointmentinterface.model.Appointment;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;

public class DoctorScheduleView {

    public Node getView(int doctorId) {
        VBox view = new VBox(20);
        Label title = new Label("My Schedule");
        title.getStyleClass().add("page-title");

        // --- Create the Table and its Columns ---
        TableView<Appointment> table = new TableView<>();

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(cellData -> cellData.getValue().patientNameProperty());

        TableColumn<Appointment, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().appointmentDateProperty());

        TableColumn<Appointment, LocalTime> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(cellData -> cellData.getValue().appointmentTimeProperty());

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // --- FIX: This is the single, correct line to add all columns, including the action column ---
        TableColumn<Appointment, Void> actionCol = createActionColumn();
        table.getColumns().addAll(patientCol, dateCol, timeCol, statusCol, actionCol); // Using the correct variable 'patientCol'

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Load Data Dynamically from the DAO ---
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        table.setItems(FXCollections.observableArrayList(appointmentDAO.getAppointmentsForDoctor(doctorId)));

        view.getChildren().addAll(title, table);
        return view;
    }

    private TableColumn<Appointment, Void> createActionColumn() {
        TableColumn<Appointment, Void> actionCol = new TableColumn<>("Actions");
        AppointmentDAO appointmentDAO = new AppointmentDAO();

        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button confirmBtn = new Button("Confirm");
            private final Button cancelBtn = new Button("Cancel");
            private final HBox pane = new HBox(10);

            {
                pane.setAlignment(Pos.CENTER);
                confirmBtn.getStyleClass().add("action-button");
                cancelBtn.getStyleClass().add("action-button-secondary");

                // --- CANCEL BUTTON LOGIC ---
                cancelBtn.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel this appointment?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            boolean success = appointmentDAO.updateAppointmentStatus(appointment.getId(), "Cancelled by Doctor");
                            if (success) {
                                getTableView().getItems().remove(appointment);
                                getTableView().refresh();
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Failed to cancel appointment.").show();
                            }
                        }
                    });
                });

                // --- CONFIRM BUTTON LOGIC ---
                confirmBtn.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    boolean success = appointmentDAO.updateAppointmentStatus(appointment.getId(), "Confirmed");
                    if (success) {
                        // Update the status in the UI by setting the JavaFX property
                        appointment.statusProperty().set("Confirmed");
                        getTableView().refresh();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to confirm appointment.").show();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    pane.getChildren().clear(); // Clear old buttons
                    String status = appointment.getStatus();

                    if ("Pending".equalsIgnoreCase(status)) {
                        pane.getChildren().addAll(confirmBtn, cancelBtn);
                        setGraphic(pane);
                    } else if ("Confirmed".equalsIgnoreCase(status)) {
                        pane.getChildren().add(cancelBtn); // Can still cancel a confirmed appointment
                        setGraphic(pane);
                    } else {
                        // Don't show any buttons for "Completed" or "Cancelled" appointments
                        setGraphic(null);
                    }
                }
            }
        });

        return actionCol;
    }
}