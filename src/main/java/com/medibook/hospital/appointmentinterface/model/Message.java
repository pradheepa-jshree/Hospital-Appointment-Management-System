package com.medibook.hospital.appointmentinterface.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Message {
    // --- NEW FIELD ---
    private final int id; // The unique ID for the message

    // --- EXISTING FIELDS ---
    private final StringProperty from;
    private final StringProperty subject;
    private final StringProperty timestamp;

    /**
     * CONSTRUCTOR - Updated to accept the message ID as the first argument.
     */
    public Message(int id, String from, String subject, String timestamp) {
        this.id = id; // Initialize the new ID field
        this.from = new SimpleStringProperty(from);
        this.subject = new SimpleStringProperty(subject);
        this.timestamp = new SimpleStringProperty(timestamp);
    }

    // --- NEW GETTER ---
    // This is required for the "Read Message" feature.
    public int getId() {
        return this.id;
    }

    // --- Property Getters (for JavaFX TableView) ---
    public StringProperty fromProperty() {
        return from;
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public StringProperty timestampProperty() {
        return timestamp;
    }

    // --- Standard Getters (for regular Java code) ---
    public String getFrom() {
        return from.get();
    }

    public String getSubject() {
        return subject.get();
    }

    public String getTimestamp() {
        return timestamp.get();
    }
}