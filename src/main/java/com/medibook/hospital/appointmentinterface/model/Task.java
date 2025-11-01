package com.medibook.hospital.appointmentinterface.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Task {
    // --- NEW FIELD ---
    private final IntegerProperty id;

    // --- EXISTING FIELDS ---
    private final StringProperty description;
    private final ObjectProperty<LocalDate> dueDate;
    private final StringProperty status;

    /**
     * CONSTRUCTOR - Updated to accept the task ID as the first argument.
     */
    public Task(int id, String description, LocalDate dueDate, String status) {
        this.id = new SimpleIntegerProperty(id); // Initialize the new ID field
        this.description = new SimpleStringProperty(description);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.status = new SimpleStringProperty(status);
    }

    // --- Standard Getters (for logic) ---
    public int getId() { return id.get(); }
    public String getDescription() { return description.get(); }
    public LocalDate getDueDate() { return dueDate.get(); }
    public String getStatus() { return status.get(); }

    // --- JavaFX Property Getters (for UI binding) ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty descriptionProperty() { return description; }
    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }
    public StringProperty statusProperty() { return status; }
}