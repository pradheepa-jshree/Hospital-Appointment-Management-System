package com.medibook.hospital.appointmentinterface.dao;

import com.medibook.hospital.appointmentinterface.database.DatabaseConnection;
import com.medibook.hospital.appointmentinterface.model.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    /**
     * Fetches a single doctor's complete profile by their primary ID.
     */
    public Doctor getDoctorById(int doctorId) {
        // MODIFIED: Added user_id to the SELECT statement
        String sql = "SELECT id, user_id, full_name, specialization, email, status FROM doctors WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // MODIFIED: Use the new 6-argument constructor
                return new Doctor(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("specialization"),
                        rs.getString("email"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetches a list of all doctors in the system.
     */
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        // MODIFIED: Added user_id to the SELECT statement
        String sql = "SELECT id, user_id, full_name, specialization, email, status FROM doctors ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // MODIFIED: Use the new 6-argument constructor
                doctors.add(new Doctor(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("specialization"),
                        rs.getString("email"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    /**
     * Finds a doctor's primary ID from the doctors table based on their user login ID.
     */
    public int getDoctorIdByUserId(int userId) {
        String sql = "SELECT id FROM doctors WHERE user_id = ?";
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

    /**
     * Searches for active doctors, filtering by name and/or specialty.
     */
    public List<Doctor> searchDoctors(String nameQuery, String specialtyQuery) {
        List<Doctor> doctors = new ArrayList<>();
        // MODIFIED: Added user_id to the SELECT statement
        StringBuilder sql = new StringBuilder("SELECT id, user_id, full_name, specialization, email, status FROM doctors WHERE status = 'Active'");

        boolean hasNameQuery = nameQuery != null && !nameQuery.trim().isEmpty();
        boolean hasSpecialtyQuery = specialtyQuery != null && !specialtyQuery.trim().isEmpty() && !specialtyQuery.equalsIgnoreCase("All");

        if (hasNameQuery) {
            sql.append(" AND full_name LIKE ?");
        }
        if (hasSpecialtyQuery) {
            sql.append(" AND specialization = ?");
        }
        sql.append(" ORDER BY full_name");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hasNameQuery) {
                pstmt.setString(paramIndex++, "%" + nameQuery + "%");
            }
            if (hasSpecialtyQuery) {
                pstmt.setString(paramIndex, specialtyQuery);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // MODIFIED: Use the new 6-argument constructor
                doctors.add(new Doctor(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("specialization"),
                        rs.getString("email"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    /**
     * Registers a new doctor within a single database transaction.
     */
    public int registerDoctor(String fullName, String specialization, String email, String password) {
        String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'Doctor')";
        String doctorSql = "INSERT INTO doctors (full_name, specialization, email, user_id) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        int newDoctorId = -1;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            try (PreparedStatement userPstmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userPstmt.setString(1, email);
                userPstmt.setString(2, password);
                userPstmt.executeUpdate();
                ResultSet generatedKeys = userPstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newUserId = generatedKeys.getInt(1);
                    try (PreparedStatement doctorPstmt = conn.prepareStatement(doctorSql, Statement.RETURN_GENERATED_KEYS)) {
                        doctorPstmt.setString(1, fullName);
                        doctorPstmt.setString(2, specialization);
                        doctorPstmt.setString(3, email);
                        doctorPstmt.setInt(4, newUserId);
                        doctorPstmt.executeUpdate();
                        ResultSet docKeys = doctorPstmt.getGeneratedKeys();
                        if (docKeys.next()){
                            newDoctorId = docKeys.getInt(1);
                        }
                    }
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Transaction is being rolled back");
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return -1;
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
        return newDoctorId;
    }

    /**
     * Updates an existing doctor's details (for Admin use).
     */
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET full_name = ?, specialization = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctor.getFullName());
            pstmt.setString(2, doctor.getSpecialization());
            pstmt.setString(3, doctor.getStatus());
            pstmt.setInt(4, doctor.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a doctor's own profile information.
     */
    public boolean updateDoctorProfile(int doctorId, String fullName, String specialization) {
        String sql = "UPDATE doctors SET full_name = ?, specialization = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.setString(2, specialization);
            pstmt.setInt(3, doctorId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a doctor and their associated user login.
     */
    public boolean deleteDoctor(int doctorId) {
        String sql = "DELETE FROM users WHERE id = (SELECT user_id FROM doctors WHERE id = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Securely changes a user's password.
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        String sqlSelect = "SELECT password FROM users WHERE id = ?";
        String sqlUpdate = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            String currentHashedPassword = null;
            try (PreparedStatement selectPstmt = conn.prepareStatement(sqlSelect)) {
                selectPstmt.setInt(1, userId);
                ResultSet rs = selectPstmt.executeQuery();
                if (rs.next()) {
                    currentHashedPassword = rs.getString("password");
                }
            }
            if (currentHashedPassword == null) {
                return false; // User not found
            }
            // Replace this with your hashing check (e.g., BCrypt.checkpw)
            if (oldPassword.equals(currentHashedPassword)) {
                // Replace this with your hashing logic (e.g., BCrypt.hashpw)
                try (PreparedStatement updatePstmt = conn.prepareStatement(sqlUpdate)) {
                    updatePstmt.setString(1, newPassword);
                    updatePstmt.setInt(2, userId);
                    return updatePstmt.executeUpdate() > 0;
                }
            } else {
                return false; // Old password did not match
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}