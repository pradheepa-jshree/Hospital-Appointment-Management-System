package com.medibook.hospital.appointmentinterface.doctor.view;

import com.medibook.hospital.appointmentinterface.dao.AppointmentDAO;
import com.medibook.hospital.appointmentinterface.dao.AvailabilityDAO;
import com.medibook.hospital.appointmentinterface.model.Availability;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AvailabilityView {

    // A list to hold the UI controls for each day
    private final List<DayRowControls> dayControls = new ArrayList<>();

    public Node getView(int doctorId) {
        VBox view = new VBox(20);
        view.setPadding(new Insets(10));
        Label title = new Label("My Availability");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Set your weekly working hours. This will determine the slots patients can book.");
        subtitle.getStyleClass().add("page-subtitle");

        // --- Create the Grid for the schedule ---
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        // Create a row of controls for each day of the week
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < days.length; i++) {
            DayRowControls controls = new DayRowControls(days[i]);
            dayControls.add(controls);
            grid.add(controls.getCheckBox(), 0, i);
            grid.add(controls.getStartTimePicker(), 1, i);
            grid.add(new Label("to"), 2, i);
            grid.add(controls.getEndTimePicker(), 3, i);
        }

        // --- Save Button ---
        Button saveBtn = new Button("Save Changes");
        saveBtn.getStyleClass().add("action-button");
        saveBtn.setOnAction(e -> handleSaveChanges(doctorId));

        HBox buttonBox = new HBox(saveBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        view.getChildren().addAll(title, subtitle, grid, buttonBox);

        // --- Load existing data ---
        loadAvailability(doctorId);

        return view;
    }

    private void loadAvailability(int doctorId) {
        // We can reuse the DAO from the appointment booking logic
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        Map<DayOfWeek, LocalTime[]> schedule = appointmentDAO.getDoctorAvailability(doctorId);

        // Convert the map to a more usable format
        Map<String, LocalTime[]> scheduleByString = schedule.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue));

        for (DayRowControls control : dayControls) {
            String day = control.getDayName().toUpperCase();
            if (scheduleByString.containsKey(day)) {
                control.setData(true, scheduleByString.get(day)[0], scheduleByString.get(day)[1]);
            } else {
                control.setData(false, LocalTime.of(9, 0), LocalTime.of(17, 0));
            }
        }
    }

    private void handleSaveChanges(int doctorId) {
        List<Availability> availabilitiesToSave = new ArrayList<>();

        for (DayRowControls control : dayControls) {
            Availability av = new Availability();
            av.setDoctorId(doctorId);
            av.setDayOfWeek(control.getDayName());
            av.setAvailable(control.getCheckBox().isSelected());
            av.setStartTime(control.getStartTimePicker().getValue());
            av.setEndTime(control.getEndTimePicker().getValue());
            availabilitiesToSave.add(av);
        }

        AvailabilityDAO availabilityDAO = new AvailabilityDAO();
        boolean success = availabilityDAO.saveAvailabilities(availabilitiesToSave);

        if (success) {
            new Alert(Alert.AlertType.INFORMATION, "Availability updated successfully.").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to save availability.").show();
        }
    }

    /**
     * Inner helper class to hold the UI controls for a single day row.
     */
    private static class DayRowControls {
        private final String dayName;
        private final CheckBox checkBox;
        private final ComboBox<LocalTime> startTimePicker;
        private final ComboBox<LocalTime> endTimePicker;

        public DayRowControls(String dayName) {
            this.dayName = dayName;
            this.checkBox = new CheckBox(dayName);
            this.startTimePicker = createTimePicker();
            this.endTimePicker = createTimePicker();

            // Bind the disabled state of time pickers to the checkbox
            startTimePicker.disableProperty().bind(checkBox.selectedProperty().not());
            endTimePicker.disableProperty().bind(checkBox.selectedProperty().not());
        }

        public void setData(boolean isAvailable, LocalTime start, LocalTime end) {
            checkBox.setSelected(isAvailable);
            startTimePicker.setValue(start);
            endTimePicker.setValue(end);
        }

        private ComboBox<LocalTime> createTimePicker() {
            ComboBox<LocalTime> picker = new ComboBox<>();
            LocalTime time = LocalTime.of(7, 0);
            while (time.isBefore(LocalTime.of(21, 1))) {
                picker.getItems().add(time);
                time = time.plusMinutes(30);
            }
            return picker;
        }

        public String getDayName() { return dayName; }
        public CheckBox getCheckBox() { return checkBox; }
        public ComboBox<LocalTime> getStartTimePicker() { return startTimePicker; }
        public ComboBox<LocalTime> getEndTimePicker() { return endTimePicker; }
    }
}