// In: admin/view/MasterScheduleView.java
package com.medibook.hospital.appointmentinterface.admin.view;

import com.medibook.hospital.appointmentinterface.dao.AppointmentDAO;
import com.medibook.hospital.appointmentinterface.model.Appointment;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.LocalTime;

public class MasterScheduleView {

    public Node getView() {
        VBox view = new VBox(20);
        Label title = new Label("Master Schedule");
        title.getStyleClass().add("page-title");

        // --- Create the Table ---
        TableView<Appointment> table = new TableView<>();

        // --- THIS IS THE FIX: Link columns to the correct properties in the Appointment model ---
        TableColumn<Appointment, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().appointmentDateProperty());

        TableColumn<Appointment, LocalTime> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(cellData -> cellData.getValue().appointmentTimeProperty());

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(cellData -> cellData.getValue().patientNameProperty());

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(cellData -> cellData.getValue().doctorNameProperty());

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        table.getColumns().addAll(dateCol, timeCol, patientCol, doctorCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Load ALL appointments using the new DAO method ---
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        table.setItems(FXCollections.observableArrayList(appointmentDAO.getAllAppointments()));

        view.getChildren().addAll(title, table);
        return view;
    }
}