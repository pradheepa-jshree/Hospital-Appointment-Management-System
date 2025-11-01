// In: model/Patient.java
package com.medibook.hospital.appointmentinterface.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Patient {
    private final IntegerProperty id;
    private final StringProperty fullName;
    private final ObjectProperty<LocalDate> dateOfBirth;
    private final StringProperty gender;
    private final StringProperty phoneNumber; // NEW
    private final StringProperty address;     // NEW

    // Updated Constructor
    public Patient(int id, String fullName, LocalDate dateOfBirth, String gender, String phoneNumber, String address) {
        this.id = new SimpleIntegerProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.dateOfBirth = new SimpleObjectProperty<>(dateOfBirth);
        this.gender = new SimpleStringProperty(gender);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.address = new SimpleStringProperty(address);
    }

    // --- Standard Getters ---
    public int getId() { return id.get(); }
    public String getFullName() { return fullName.get(); }
    public LocalDate getDateOfBirth() { return dateOfBirth.get(); }
    public String getGender() { return gender.get(); }
    public String getPhoneNumber() { return phoneNumber.get(); }
    public String getAddress() { return address.get(); }

    // --- JavaFX Property Getters ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty fullNameProperty() { return fullName; }
    public ObjectProperty<LocalDate> dateOfBirthProperty() { return dateOfBirth; }
    public StringProperty genderProperty() { return gender; }
    public StringProperty phoneNumberProperty() { return phoneNumber; }
    public StringProperty addressProperty() { return address; }
}