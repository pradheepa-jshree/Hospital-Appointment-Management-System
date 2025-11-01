package com.medibook.hospital.appointmentinterface.dao;

import com.medibook.hospital.appointmentinterface.database.DatabaseConnection;
import com.medibook.hospital.appointmentinterface.model.Availability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class AvailabilityDAO {
    /**
     * Saves or updates a list of availability settings for a doctor.
     * Uses a transaction to ensure all updates succeed or fail together.
     * @param availabilities A List of Availability objects.
     * @return true if the save was successful, false otherwise.
     */
    public boolean saveAvailabilities(List<Availability> availabilities) {
        // This SQL command will INSERT a new row, but if a row with the same
        // unique key (doctor_id, day_of_week) already exists, it will UPDATE it instead.
        String sql = "INSERT INTO availability (doctor_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time)";

        // We also need to handle days that are now unavailable (unchecked)
        String deleteSql = "DELETE FROM availability WHERE doctor_id = ? AND day_of_week = ?";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement insertPstmt = conn.prepareStatement(sql);
                 PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {

                for (Availability availability : availabilities) {
                    if (availability.isAvailable()) {
                        // This day is checked, so we save/update it
                        insertPstmt.setInt(1, availability.getDoctorId());
                        insertPstmt.setString(2, availability.getDayOfWeek());
                        insertPstmt.setTime(3, Time.valueOf(availability.getStartTime()));
                        insertPstmt.setTime(4, Time.valueOf(availability.getEndTime()));
                        insertPstmt.addBatch();
                    } else {
                        // This day is unchecked, so we delete its record
                        deletePstmt.setInt(1, availability.getDoctorId());
                        deletePstmt.setString(2, availability.getDayOfWeek());
                        deletePstmt.addBatch();
                    }
                }
                insertPstmt.executeBatch();
                deletePstmt.executeBatch();
            }

            conn.commit(); // Finalize the transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
