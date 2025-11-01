package com.medibook.hospital.appointmentinterface.model;

import java.time.LocalTime;

/**
 * A simple model class (Plain Old Java Object - POJO) to represent
 * a single availability entry for a doctor.
 */
public class Availability {
    private int doctorId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable; // Used by the UI to know if the checkbox is checked

    // --- Constructors ---
    public Availability() {
        // Default constructor
    }

    public Availability(int doctorId, String dayOfWeek, LocalTime startTime, LocalTime endTime, boolean isAvailable) {
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = isAvailable;
    }

    // --- Getters and Setters ---

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
