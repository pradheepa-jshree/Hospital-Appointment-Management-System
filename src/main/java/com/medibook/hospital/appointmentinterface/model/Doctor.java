package com.medibook.hospital.appointmentinterface.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Doctor {
    private final int id;
    private final int userId; // <<< THIS IS THE NEW, CRITICAL FIELD
    private final StringProperty fullName;
    private final StringProperty specialization;
    private final StringProperty email;
    private final StringProperty status;

    /**
     * CONSTRUCTOR - Updated to accept the userId.
     */
    public Doctor(int id, int userId, String fullName, String specialization, String email, String status) {
        this.id = id;
        this.userId = userId; // Initialize the new field
        this.fullName = new SimpleStringProperty(fullName);
        this.specialization = new SimpleStringProperty(specialization);
        this.email = new SimpleStringProperty(email);
        this.status = new SimpleStringProperty(status);
    }

    // --- Standard Getters ---
    public int getId() {
        return id;
    }

    /**
     * NEW GETTER - This is the method your DoctorProfileView needs.
     */
    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName.get();
    }
    public String getSpecialization() {
        return specialization.get();
    }
    public String getEmail() {
        return email.get();
    }
    public String getStatus() {
        return status.get();
    }

    // --- JavaFX Property Getters ---
    public StringProperty fullNameProperty() {
        return fullName;
    }
    public StringProperty specializationProperty() {
        return specialization;
    }
    public StringProperty emailProperty() {
        return email;
    }
    public StringProperty statusProperty() {
        return status;
    }
}