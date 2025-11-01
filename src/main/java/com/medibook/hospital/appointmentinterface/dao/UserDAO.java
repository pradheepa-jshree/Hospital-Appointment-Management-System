package com.medibook.hospital.appointmentinterface.dao;

import com.medibook.hospital.appointmentinterface.database.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt; // Import BCrypt for hashing

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * SECURELY validates a user's login credentials.
     * Fetches the user by username and then uses BCrypt to check the password.
     * (This is the version you should be using once hashing is implemented)
     */
    public User validateLogin(String username, String plainTextPassword, String role) {
        String sql = "SELECT id, username, password, role FROM users WHERE username = ? AND role = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, role);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPasswordFromDB = rs.getString("password");

                // --- PASSWORD CHECK ---
                // Replace the second line with the first once you've implemented hashing
                // if (BCrypt.checkpw(plainTextPassword, hashedPasswordFromDB)) {
                if (plainTextPassword.equals(hashedPasswordFromDB)) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("role"));
                }
            }
            return null; // User not found or password incorrect
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * NEW METHOD: Fetches a user's username by their ID.
     * This is required for the Admin Profile View.
     */
    public String getUsernameById(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if not found
    }

    /**
     * REFACTORED: Securely changes a user's password in a single transaction.
     * This method is reusable for all roles (Admin, Doctor, Patient).
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        String sqlSelect = "SELECT password FROM users WHERE id = ?";
        String sqlUpdate = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Step 1: Fetch the current password
            String currentPasswordFromDB = null;
            try (PreparedStatement selectPstmt = conn.prepareStatement(sqlSelect)) {
                selectPstmt.setInt(1, userId);
                ResultSet rs = selectPstmt.executeQuery();
                if (rs.next()) {
                    currentPasswordFromDB = rs.getString("password");
                }
            }

            if (currentPasswordFromDB == null) {
                return false; // User not found
            }

            // Step 2: Check if the old password matches
            // boolean oldPasswordMatches = BCrypt.checkpw(oldPassword, currentPasswordFromDB); // Use this with hashing
            boolean oldPasswordMatches = oldPassword.equals(currentPasswordFromDB); // Use this for plain text

            if (oldPasswordMatches) {
                // Step 3: If it matches, update to the new password
                try (PreparedStatement updatePstmt = conn.prepareStatement(sqlUpdate)) {
                    // String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt()); // Use this with hashing
                    // updatePstmt.setString(1, newHashedPassword); // Use this with hashing
                    updatePstmt.setString(1, newPassword); // Use this for plain text

                    updatePstmt.setInt(2, userId);
                    return updatePstmt.executeUpdate() > 0;
                }
            } else {
                return false; // Old password was incorrect
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}