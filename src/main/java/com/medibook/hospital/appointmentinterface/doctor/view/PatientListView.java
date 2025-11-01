// In: doctor/view/PatientListView.java
package com.medibook.hospital.appointmentinterface.doctor.view;

import com.medibook.hospital.appointmentinterface.dao.PatientDAO;
import com.medibook.hospital.appointmentinterface.model.Patient;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import java.time.LocalDate;

public class PatientListView {

    public Node getView(int doctorId) {
        VBox view = new VBox(20);
        Label title = new Label("My Patients");
        title.getStyleClass().add("page-title");

        // --- Create the Table with Correct Property Names ---
        TableView<Patient> table = new TableView<>();

        // FIX: Use 'fullNameProperty' instead of 'nameProperty'
        TableColumn<Patient, String> nameCol = new TableColumn<>("Patient Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());

        // FIX: Use 'dateOfBirthProperty' instead of 'dobProperty'
        TableColumn<Patient, LocalDate> dobCol = new TableColumn<>("Date of Birth");
        dobCol.setCellValueFactory(cellData -> cellData.getValue().dateOfBirthProperty());

        TableColumn<Patient, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(cellData -> cellData.getValue().genderProperty());

        // FIX: Remove the "Last Visit" column as it's not in our model
        table.getColumns().addAll(nameCol, dobCol, genderCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Load Data Dynamically from the DAO ---
        // FIX: This replaces any old dummy data and incorrect constructor calls
        PatientDAO patientDAO = new PatientDAO();
        table.setItems(FXCollections.observableArrayList(patientDAO.getPatientsForDoctor(doctorId)));

        view.getChildren().addAll(title, table);
        return view;
    }
}