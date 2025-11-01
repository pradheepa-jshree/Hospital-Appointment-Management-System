// In: dao/PatientDAO.java
package com.medibook.hospital.appointmentinterface.dao;

import com.medibook.hospital.appointmentinterface.database.DatabaseConnection;
import com.medibook.hospital.appointmentinterface.model.Patient;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    // --- "READ" METHODS ---

    public Patient getPatientById(int patientId) {
        String sql = "SELECT id, full_name, date_of_birth, gender, phone_number, address FROM patients WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Patient(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null,
                        rs.getString("gender"),
                        rs.getString("phone_number"),
                        rs.getString("address")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT id, full_name, date_of_birth, gender, phone_number, address FROM patients ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                patients.add(new Patient(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null,
                        rs.getString("gender"),
                        rs.getString("phone_number"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return patients;
    }

    public List<Patient> getPatientsForDoctor(int doctorId) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT DISTINCT p.id, p.full_name, p.date_of_birth, p.gender, p.phone_number, p.address " +
                "FROM patients p JOIN appointments a ON p.id = a.patient_id WHERE a.doctor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                patients.add(new Patient(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null,
                        rs.getString("gender"),
                        rs.getString("phone_number"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return patients;
    }

    public int getPatientIdByUserId(int userId) {
        String sql = "SELECT id FROM patients WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // --- "CREATE" and "UPDATE" METHODS ---

    public boolean updatePatientProfile(int patientId, String fullName, LocalDate dateOfBirth, String gender, String phone, String address) {
        String sql = "UPDATE patients SET full_name = ?, date_of_birth = ?, gender = ?, phone_number = ?, address = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.setDate(2, java.sql.Date.valueOf(dateOfBirth));
            pstmt.setString(3, gender);
            pstmt.setString(4, phone);
            pstmt.setString(5, address);
            pstmt.setInt(6, patientId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int registerPatient(String fullName, String email, String password) {
        return registerPatientWithCredentials(fullName, null, null, null, null, email, password);
    }

    public int registerPatientWithCredentials(String fullName, LocalDate dateOfBirth, String gender, String phone, String address, String username, String password) {
        String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'Patient')";
        String patientSql = "INSERT INTO patients (full_name, date_of_birth, gender, phone_number, address, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            int newUserId;
            try (PreparedStatement userPstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userPstmt.setString(1, username);
                userPstmt.setString(2, password);
                if (userPstmt.executeUpdate() == 0) throw new SQLException("Creating user failed.");
                try (ResultSet generatedKeys = userPstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) newUserId = generatedKeys.getInt(1);
                    else throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            try (PreparedStatement patientPstmt = conn.prepareStatement(patientSql)) {
                patientPstmt.setString(1, fullName);
                patientPstmt.setDate(2, (dateOfBirth != null) ? java.sql.Date.valueOf(dateOfBirth) : null);
                patientPstmt.setString(3, gender);
                patientPstmt.setString(4, phone);
                patientPstmt.setString(5, address);
                patientPstmt.setInt(6, newUserId);
                patientPstmt.executeUpdate();
            }
            conn.commit();
            return newUserId;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    public int getUserIdByPatientId(int patientId) {
        String sql = "SELECT user_id FROM patients WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }
    public boolean isProfileComplete(int patientId) {
        String sql = "SELECT date_of_birth, phone_number FROM patients WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Check if either the DOB or phone number is null or empty
                return rs.getDate("date_of_birth") != null &&
                        rs.getString("phone_number") != null &&
                        !rs.getString("phone_number").trim().isEmpty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Assume incomplete if there's an error or no patient found
    }
    // In: dao/PatientDAO.java

// ... (your existing getAllPatients() and other methods)

    /**
     * Updates an existing patient's details in the database.
     * @param patient The Patient object with updated information.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET full_name = ?, date_of_birth = ?, gender = ?, phone_number = ?, address = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getFullName());
            pstmt.setDate(2, java.sql.Date.valueOf(patient.getDateOfBirth()));
            pstmt.setString(3, patient.getGender());
            pstmt.setString(4, patient.getPhoneNumber());
            pstmt.setString(5, patient.getAddress());
            pstmt.setInt(6, patient.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a patient and their associated user login from the database.
     * This relies on the ON DELETE CASCADE constraint on the 'patients' table.
     * @param patientId The ID of the patient to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean deletePatient(int patientId) {
        // IMPORTANT: We delete the USER record. The `ON DELETE CASCADE` in your database
        // schema will automatically delete the corresponding `patients` record.
        // This is the cleanest way to remove a user and all their related data.
        String sql = "DELETE FROM users WHERE id = (SELECT user_id FROM patients WHERE id = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}