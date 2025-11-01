package com.medibook.hospital.appointmentinterface.admin.view;

import com.medibook.hospital.appointmentinterface.dao.DoctorDAO;
import com.medibook.hospital.appointmentinterface.model.Doctor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.List;

public class ManageDoctorsView {

    private TableView<Doctor> table;
    private final DoctorDAO doctorDAO; // Now correctly initialized in the constructor
    private ObservableList<Doctor> doctorList;

    /**
     * NEW CONSTRUCTOR: This is where we initialize our final fields.
     * This method runs once when 'new ManageDoctorsView()' is called.
     */
    public ManageDoctorsView() {
        this.doctorDAO = new DoctorDAO();
    }

    public Node getView() {
        // The list is now loaded here, using the already-initialized DAO
        this.doctorList = FXCollections.observableArrayList(doctorDAO.getAllDoctors());

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));
        Label title = new Label("Manage Doctors");
        title.getStyleClass().add("page-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button addBtn = new Button("Register New Doctor");
        addBtn.getStyleClass().add("action-button");
        addBtn.setOnAction(e -> handleAddDoctor());
        header.getChildren().addAll(title, spacer, addBtn);
        layout.setTop(header);

        table = createDoctorTable();
        table.setItems(doctorList);
        layout.setCenter(table);

        return layout;
    }

    private TableView<Doctor> createDoctorTable() {
        TableView<Doctor> tv = new TableView<>();
        TableColumn<Doctor, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        TableColumn<Doctor, String> specCol = new TableColumn<>("Specialization");
        specCol.setCellValueFactory(cellData -> cellData.getValue().specializationProperty());
        TableColumn<Doctor, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        TableColumn<Doctor, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        TableColumn<Doctor, Void> actionCol = createActionColumn();
        tv.getColumns().addAll(nameCol, specCol, emailCol, statusCol, actionCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    private TableColumn<Doctor, Void> createActionColumn() {
        TableColumn<Doctor, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(10, editBtn, deleteBtn);
            {
                pane.setAlignment(Pos.CENTER);
                editBtn.getStyleClass().add("action-button-secondary");
                deleteBtn.getStyleClass().add("action-button-secondary");

                editBtn.setOnAction(event -> {
                    Doctor selectedDoctor = getTableView().getItems().get(getIndex());
                    EditDoctorDialog dialog = new EditDoctorDialog(selectedDoctor);
                    dialog.showAndWait().ifPresent(updatedDoctor -> {
                        if (doctorDAO.updateDoctor(updatedDoctor)) refreshTable();
                        else new Alert(Alert.AlertType.ERROR, "Failed to update doctor.").show();
                    });
                });

                deleteBtn.setOnAction(event -> {
                    Doctor selectedDoctor = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedDoctor.getFullName() + "? This is permanent.", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            if (doctorDAO.deleteDoctor(selectedDoctor.getId())) refreshTable();
                            else new Alert(Alert.AlertType.ERROR, "Failed to delete doctor.").show();
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
        return actionCol;
    }

    private void handleAddDoctor() {
        RegisterDoctorDialog dialog = new RegisterDoctorDialog();
        dialog.showAndWait().ifPresent(result -> {
            String fullName = result.get(0);
            String specialization = result.get(1);
            String email = result.get(2);
            String password = result.get(3);

            if (fullName.isEmpty() || specialization.isEmpty() || email.isEmpty() || password.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "All fields are required.").show();
                return;
            }

            int newDoctorId = doctorDAO.registerDoctor(fullName, specialization, email, password);
            if (newDoctorId != -1) {
                new Alert(Alert.AlertType.INFORMATION, "Doctor registered successfully.").show();
                refreshTable();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to register doctor. The email might already be in use.").show();
            }
        });
    }

    private void refreshTable() {
        doctorList.setAll(doctorDAO.getAllDoctors());
        table.refresh();
    }
}