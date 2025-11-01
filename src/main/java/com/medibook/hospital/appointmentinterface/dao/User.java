package com.medibook.hospital.appointmentinterface.dao;

public class User {
    private final int id;
    private final String username; // <<< NEW FIELD
    private final String role;

    /**
     * UPDATED CONSTRUCTOR: Now accepts id, username, and role.
     */
    public User(int id, String username, String role) {
        this.id = id;
        this.username = username; // <<< INITIALIZE THE NEW FIELD
        this.role = role;
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    /**
     * NEW GETTER for the username.
     */
    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}