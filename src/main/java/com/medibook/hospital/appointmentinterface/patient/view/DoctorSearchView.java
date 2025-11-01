// Create this new file: patient/view/DoctorSearchView.java
package com.medibook.hospital.appointmentinterface.patient.view;

import com.medibook.hospital.appointmentinterface.dao.DoctorDAO;
import com.medibook.hospital.appointmentinterface.model.Doctor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.function.Consumer;

public class DoctorSearchView {

    private DoctorDAO doctorDAO;
    private TextField nameSearchField;
    private ComboBox<String> specialtyFilter;
    private ListView<Doctor> resultsList;
    private ObservableList<Doctor> doctorObservableList;

    public DoctorSearchView() {
        this.doctorDAO = new DoctorDAO();
        this.doctorObservableList = FXCollections.observableArrayList();
    }

    public Node getView(Consumer<Doctor> onDoctorSelected) {
        BorderPane layout = new BorderPane();

        Label title = new Label("Find a Doctor");
        title.getStyleClass().add("page-title");

        // --- Search and Filter Controls ---
        nameSearchField = new TextField();
        nameSearchField.setPromptText("Search by name...");
        nameSearchField.setPrefWidth(300);

        specialtyFilter = new ComboBox<>();
        // In a real app, you would fetch these specialties from the database
        specialtyFilter.setItems(FXCollections.observableArrayList("All", "Cardiology", "Pediatrics", "Dermatology"));
        specialtyFilter.setValue("All");

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("action-button");
        searchBtn.setOnAction(e -> performSearch());

        HBox filterBox = new HBox(10, new Label("Doctor Name:"), nameSearchField, new Label("Specialty:"), specialtyFilter, searchBtn);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        VBox topControls = new VBox(20, title, filterBox);
        layout.setTop(topControls);

        // --- Results List ---
        resultsList = new ListView<>(doctorObservableList);
        resultsList.setCellFactory(param -> new DoctorListCell(onDoctorSelected));
        layout.setCenter(resultsList);

        BorderPane.setMargin(resultsList, new Insets(20, 0, 0, 0));

        // Perform an initial search to show all doctors
        performSearch();

        return layout;
    }

    private void performSearch() {
        String nameQuery = nameSearchField.getText();
        String specialtyQuery = specialtyFilter.getValue();

        List<Doctor> foundDoctors = doctorDAO.searchDoctors(nameQuery, specialtyQuery);
        doctorObservableList.setAll(foundDoctors);
    }

    // --- Custom ListCell to display doctor info ---
    private static class DoctorListCell extends ListCell<Doctor> {
        private final Consumer<Doctor> onDoctorSelected;

        public DoctorListCell(Consumer<Doctor> onDoctorSelected) {
            this.onDoctorSelected = onDoctorSelected;
        }

        @Override
        protected void updateItem(Doctor doctor, boolean empty) {
            super.updateItem(doctor, empty);
            if (empty || doctor == null) {
                setText(null);
                setGraphic(null);
            } else {
                Label nameLabel = new Label(doctor.getFullName());
                nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                Label specialtyLabel = new Label(doctor.getSpecialization());
                specialtyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

                VBox doctorInfoBox = new VBox(5, nameLabel, specialtyLabel);

                Button selectBtn = new Button("View Schedule");
                selectBtn.getStyleClass().add("action-button-secondary");
                selectBtn.setOnAction(e -> onDoctorSelected.accept(doctor));

                GridPane grid = new GridPane();
                grid.add(doctorInfoBox, 0, 0);
                grid.add(selectBtn, 1, 0);
                grid.setHgap(20);
                grid.setAlignment(Pos.CENTER_LEFT);

                setGraphic(grid);
            }
        }
    }
}