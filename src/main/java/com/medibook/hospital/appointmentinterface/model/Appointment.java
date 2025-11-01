package com.medibook.hospital.appointmentinterface.model;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    // --- Properties for JavaFX TableView ---
    private final IntegerProperty id;
    private final ObjectProperty<LocalDate> appointmentDate;
    private final ObjectProperty<LocalTime> appointmentTime;
    private final StringProperty patientName;
    private final StringProperty doctorName;
    private final StringProperty status;

    // --- Plain Java field for application logic ---
    private final int doctorId; // <<< THIS IS THE NEW, CRITICAL FIELD

    /**
     * CONSTRUCTOR - Updated to accept doctorId.
     * The patientName is now accepted as an argument since it's available from the JOIN.
     */
    public Appointment(int id, int doctorId, LocalDate appointmentDate, LocalTime appointmentTime, String patientName, String doctorName, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.appointmentDate = new SimpleObjectProperty<>(appointmentDate);
        this.appointmentTime = new SimpleObjectProperty<>(appointmentTime);
        this.patientName = new SimpleStringProperty(patientName != null ? patientName : "");
        this.doctorName = new SimpleStringProperty(doctorName != null ? doctorName : "");
        this.status = new SimpleStringProperty(status);
        this.doctorId = doctorId; // Initialize the new field
    }

    // --- Standard Getters (for regular Java logic) ---
    public int getId() { return id.get(); }
    public LocalDate getAppointmentDate() { return appointmentDate.get(); }
    public LocalTime getAppointmentTime() { return appointmentTime.get(); }
    public String getPatientName() { return patientName.get(); }
    public String getDoctorName() { return doctorName.get(); }
    public String getStatus() { return status.get(); }

    /**
     * NEW GETTER - This is required for the reschedule logic to work.
     */
    public int getDoctorId() {
        return this.doctorId;
    }

    // --- JavaFX Property Getters (for binding to TableView columns) ---
    public IntegerProperty idProperty() { return id; }
    public ObjectProperty<LocalDate> appointmentDateProperty() { return appointmentDate; }
    public ObjectProperty<LocalTime> appointmentTimeProperty() { return appointmentTime; }
    public StringProperty patientNameProperty() { return patientName; }
    public StringProperty doctorNameProperty() { return doctorName; }
    public StringProperty statusProperty() { return status; }
}