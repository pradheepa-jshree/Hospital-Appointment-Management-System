package com.medibook.hospital.appointmentinterface.dao;

import com.medibook.hospital.appointmentinterface.database.DatabaseConnection;
import com.medibook.hospital.appointmentinterface.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    /**
     * Fetches all messages for a specific doctor from the database.
     * This method has been UPDATED to fetch the message 'id' as well.
     */
    public List<Message> getMessagesForDoctor(int doctorId) {
        List<Message> messages = new ArrayList<>();
        // MODIFIED: Added 'id' to the SELECT statement
        String sql = "SELECT id, sender_name, subject, received_timestamp FROM messages WHERE recipient_doctor_id = ? ORDER BY received_timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // MODIFIED: Use the new constructor that includes the message ID
                messages.add(new Message(
                        rs.getInt("id"), // The new, required ID
                        rs.getString("sender_name"),
                        rs.getString("subject"),
                        rs.getString("received_timestamp")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching messages: " + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * Inserts a new message into the database.
     * @return true if the message was sent successfully, false otherwise.
     */
    public boolean sendMessage(int recipientDoctorId, String senderName, String subject, String messageBody) {
        String sql = "INSERT INTO messages (recipient_doctor_id, sender_name, subject, message_body) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, recipientDoctorId);
            pstmt.setString(2, senderName);
            pstmt.setString(3, subject);
            pstmt.setString(4, messageBody);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * NEW METHOD: Retrieves the full message body for a given message ID.
     * This is required for the "Read Message" feature.
     * @param messageId The ID of the message to read.
     * @return The full text of the message, or null if not found.
     */
    public String getMessageBody(int messageId) {
        String sql = "SELECT message_body FROM messages WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, messageId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("message_body");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if message not found or on error
    }

    // --- Your existing test method, updated to show the message ID ---
    public static void main(String[] args) {
        MessageDAO dao = new MessageDAO();

        // From our setup script, Dr. Smith's ID is 1. Let's test with that.
        int testDoctorId = 1;

        System.out.println("Testing DAO: Fetching messages for doctor ID: " + testDoctorId);
        List<Message> messages = dao.getMessagesForDoctor(testDoctorId);

        if (messages.isEmpty()) {
            System.out.println("RESULT: No messages found. Check the doctor ID and the database table.");
            System.out.println("Possible issues: Database connection failed, or no messages for this doctor in the 'messages' table.");
        } else {
            System.out.println("SUCCESS! Found " + messages.size() + " messages:");
            for (Message msg : messages) {
                // Now we can also print the ID to confirm our change works
                System.out.println("  -> ID: " + msg.getId() + " | From: " + msg.getFrom() + " | Subject: " + msg.getSubject());
            }
        }
    }
}