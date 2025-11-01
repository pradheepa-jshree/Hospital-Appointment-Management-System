package com.medibook.hospital.appointmentinterface.patient.view;

import com.medibook.hospital.appointmentinterface.dao.AppointmentDAO;
import com.medibook.hospital.appointmentinterface.model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class MyAppointmentsView {

    private final AppointmentDAO appointmentDAO;
    private ObservableList<Appointment> allAppointments;
    private final int patientId;
    private final AppointmentViewListener listener; // CHANGED: Added listener field

    /**
     * CHANGED: Using a constructor to receive dependencies (patientId and the listener).
     */
    public MyAppointmentsView(int patientId, AppointmentViewListener listener) {
        this.patientId = patientId;
        this.listener = listener;
        this.appointmentDAO = new AppointmentDAO();
    }

    /**
     * CHANGED: getView no longer needs arguments as they are stored from the constructor.
     */
    public Node getView() {
        this.allAppointments = FXCollections.observableArrayList(appointmentDAO.getAppointmentsForPatient(patientId));

        VBox view = new VBox(20);
        Label title = new Label("My Appointments");
        title.getStyleClass().add("page-title");

        TabPane tabPane = new TabPane();
        Tab upcomingTab = new Tab("Upcoming Appointments", createTableView(true));
        Tab pastTab = new Tab("Past Appointments", createTableView(false));
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(upcomingTab, pastTab);

        view.getChildren().addAll(title, tabPane);
        return view;
    }

    private TableView<Appointment> createTableView(boolean isUpcoming) {
        TableView<Appointment> table = new TableView<>();
        TableColumn<Appointment, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().appointmentDateProperty());
        TableColumn<Appointment, LocalTime> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(cellData -> cellData.getValue().appointmentTimeProperty());
        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(cellData -> cellData.getValue().doctorNameProperty());
        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        if (isUpcoming) {
            List<Appointment> upcoming = allAppointments.stream().filter(a -> !a.getAppointmentDate().isBefore(LocalDate.now())).collect(Collectors.toList());
            table.setItems(FXCollections.observableArrayList(upcoming));
            TableColumn<Appointment, Void> actionCol = createActionColumn();
            table.getColumns().addAll(dateCol, timeCol, doctorCol, statusCol, actionCol);
        } else {
            List<Appointment> past = allAppointments.stream().filter(a -> a.getAppointmentDate().isBefore(LocalDate.now())).collect(Collectors.toList());
            table.setItems(FXCollections.observableArrayList(past));
            table.getColumns().addAll(dateCol, timeCol, doctorCol, statusCol);
        }
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private TableColumn<Appointment, Void> createActionColumn() {
        TableColumn<Appointment, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button cancelBtn = new Button("Cancel");
            private final Button rescheduleBtn = new Button("Reschedule");
            private final HBox pane = new HBox(10, cancelBtn, rescheduleBtn);

            {
                pane.setAlignment(Pos.CENTER);
                cancelBtn.getStyleClass().add("action-button-secondary");
                rescheduleBtn.getStyleClass().add("action-button-secondary");

                cancelBtn.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel your appointment with " + appointment.getDoctorName() + "?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            boolean success = appointmentDAO.cancelAppointment(appointment.getId());
                            if (success) {
                                allAppointments.setAll(appointmentDAO.getAppointmentsForPatient(patientId));
                                getTableView().setItems(allAppointments.stream().filter(a -> !a.getAppointmentDate().isBefore(LocalDate.now())).collect(Collectors.toCollection(FXCollections::observableArrayList)));
                            } else {
                                showAlert("Error", "Failed to cancel the appointment.");
                            }
                        }
                    });
                });

                // --- THIS IS THE CRITICAL CHANGE ---
                rescheduleBtn.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    // Use the listener to tell the main view to handle the reschedule request
                    if (listener != null) {
                        listener.onRescheduleRequested(appointment.getDoctorId(), appointment.getId());
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
                    String status = appointment.getStatus();
                    if ("Pending".equalsIgnoreCase(status) || "Confirmed".equalsIgnoreCase(status)) {
                        setGraphic(pane);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        return actionCol;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}