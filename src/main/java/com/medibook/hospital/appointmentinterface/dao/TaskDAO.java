// In: dao/TaskDAO.java
package com.medibook.hospital.appointmentinterface.dao;

import com.medibook.hospital.appointmentinterface.database.DatabaseConnection;
import com.medibook.hospital.appointmentinterface.model.Task;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    public List<Task> getTasksForDoctor(int doctorId) {
        List<Task> tasks = new ArrayList<>();
        // MODIFIED: Added 'id' to the SELECT statement
        String sql = "SELECT id, description, due_date, status FROM tasks WHERE doctor_id = ? ORDER BY due_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // MODIFIED: Use the new 4-argument constructor
                tasks.add(new Task(
                        rs.getInt("id"), // The new, required ID
                        rs.getString("description"),
                        rs.getDate("due_date") != null ? rs.getDate("due_date").toLocalDate() : null, // Handle possible null dates
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
    // In: dao/TaskDAO.java

    /**
     * Updates the status of a specific task.
     * @param taskId The ID of the task to update.
     * @param newStatus The new status (e.g., "Completed", "In Progress").
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateTaskStatus(int taskId, String newStatus) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, taskId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}