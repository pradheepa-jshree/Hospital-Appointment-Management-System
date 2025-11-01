// Create this new file: patient/view/DoctorScheduleSelectionView.java
package com.medibook.hospital.appointmentinterface.patient.view;

import com.medibook.hospital.appointmentinterface.dao.AppointmentDAO;
import com.medibook.hospital.appointmentinterface.model.Doctor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class DoctorScheduleSelectionView {
    private Doctor selectedDoctor;
    private AppointmentDAO appointmentDAO;
    private DatePicker calendar;
    private TilePane timeSlotPane;
    private Map<DayOfWeek, LocalTime[]> availabilityMap;

    public DoctorScheduleSelectionView(Doctor doctor) {
        this.selectedDoctor = doctor;
        this.appointmentDAO = new AppointmentDAO();
        this.availabilityMap = appointmentDAO.getDoctorAvailability(doctor.getId());
    }

    public Node getView(Consumer<AppointmentSelection> onSlotSelected) {
        BorderPane layout = new BorderPane();

        // --- Header ---
        Label title = new Label("Select an Appointment Time");
        title.getStyleClass().add("page-title");
        Label doctorInfo = new Label("Booking with: " + selectedDoctor.getFullName() + " (" + selectedDoctor.getSpecialization() + ")");
        doctorInfo.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");

        // --- Calendar ---
        calendar = new DatePicker(LocalDate.now());
        calendar.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Disable past dates and days the doctor is not available
                if (date.isBefore(LocalDate.now()) || !availabilityMap.containsKey(date.getDayOfWeek())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Pinkish background for disabled
                }
            }
        });
        calendar.setOnAction(e -> updateAvailableTimeSlots(onSlotSelected));

        VBox topControls = new VBox(15, title, doctorInfo, new Label("Select a Date:"), calendar);
        layout.setTop(topControls);

        // --- Time Slot Grid ---
        timeSlotPane = new TilePane();
        timeSlotPane.setPadding(new Insets(20, 0, 0, 0));
        timeSlotPane.setHgap(10);
        timeSlotPane.setVgap(10);

        ScrollPane scrollPane = new ScrollPane(timeSlotPane);
        scrollPane.setFitToWidth(true);
        layout.setCenter(scrollPane);

        // Initially populate time slots for today (or the first available day)
        updateAvailableTimeSlots(onSlotSelected);

        return layout;
    }

    private void updateAvailableTimeSlots(Consumer<AppointmentSelection> onSlotSelected) {
        timeSlotPane.getChildren().clear();
        LocalDate selectedDate = calendar.getValue();
        DayOfWeek selectedDay = selectedDate.getDayOfWeek();

        if (!availabilityMap.containsKey(selectedDay)) {
            timeSlotPane.getChildren().add(new Label("Dr. " + selectedDoctor.getFullName().split(" ")[1] + " is not available on this day."));
            return;
        }

        LocalTime[] workHours = availabilityMap.get(selectedDay);
        LocalTime startTime = workHours[0];
        LocalTime endTime = workHours[1];

        Set<LocalTime> bookedSlots = appointmentDAO.getBookedSlots(selectedDoctor.getId(), selectedDate);

        // Generate time slots (e.g., every 30 minutes)
        for (LocalTime slot = startTime; slot.isBefore(endTime); slot = slot.plusMinutes(30)) {
            Button timeButton = new Button(slot.toString());
            timeButton.setPrefWidth(100);

            if (bookedSlots.contains(slot)) {
                timeButton.setDisable(true); // Gray out booked slots
            } else {
                timeButton.getStyleClass().add("action-button-secondary");
                // When a slot is clicked, pass the selection details up
                LocalTime finalSlot = slot;
                timeButton.setOnAction(e -> {
                    onSlotSelected.accept(new AppointmentSelection(selectedDoctor, selectedDate, finalSlot));
                });
            }
            timeSlotPane.getChildren().add(timeButton);
        }
    }

    // Helper class to pass selection data
    public static class AppointmentSelection {
        public final Doctor doctor;
        public final LocalDate date;
        public final LocalTime time;
        public AppointmentSelection(Doctor d, LocalDate dt, LocalTime t) {
            doctor = d; date = dt; time = t;
        }
    }
}