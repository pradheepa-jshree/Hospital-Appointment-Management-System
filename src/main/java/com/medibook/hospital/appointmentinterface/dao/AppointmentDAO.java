package com.medibook.hospital.appointmentinterface.dao;

import com.medibook.hospital.appointmentinterface.database.DatabaseConnection;
import com.medibook.hospital.appointmentinterface.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AppointmentDAO {

    // --- METHODS FOR DOCTOR DASHBOARD COUNTS ---
    public int getTotalAppointmentsToday(int doctorId) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getAppointmentsByStatusToday(int doctorId, String status) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = CURDATE() AND status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        // MODIFIED: Added a.doctor_id to the SELECT statement
        String sql = "SELECT a.id, a.doctor_id, a.appointment_date, a.appointment_time, a.status, " +
                "p.full_name AS patient_name, d.full_name AS doctor_name " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN doctors d ON a.doctor_id = d.id " +
                "WHERE a.doctor_id = ? " +
                "ORDER BY a.appointment_date, a.appointment_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // MODIFIED: Use the new 7-argument constructor
                appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("doctor_id"), // The missing argument
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public Appointment getUpcomingAppointmentForPatient(int patientId) {
        // MODIFIED: Added a.doctor_id to the SELECT statement
        String sql = "SELECT a.id, a.doctor_id, a.appointment_date, a.appointment_time, a.status, d.full_name AS doctor_name " +
                "FROM appointments a JOIN doctors d ON a.doctor_id = d.id " +
                "WHERE a.patient_id = ? AND a.appointment_date >= CURDATE() " +
                "ORDER BY a.appointment_date ASC, a.appointment_time ASC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // MODIFIED: Use the new 7-argument constructor
                return new Appointment(
                        rs.getInt("id"),
                        rs.getInt("doctor_id"), // The missing argument
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        null, // patientName is not needed for this view
                        rs.getString("doctor_name"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        // MODIFIED: Added a.doctor_id to the SELECT statement
        String sql = "SELECT a.id, a.doctor_id, a.appointment_date, a.appointment_time, a.status, " +
                "p.full_name AS patient_name, d.full_name AS doctor_name " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN doctors d ON a.doctor_id = d.id " +
                "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // MODIFIED: Use the new 7-argument constructor
                appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("doctor_id"), // The missing argument
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    // This method was already correct from the previous step, no changes needed here.
    public List<Appointment> getAppointmentsForPatient(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT " +
                "    a.id, " +
                "    a.doctor_id, " +
                "    a.appointment_date, " +
                "    a.appointment_time, " +
                "    a.status, " +
                "    d.full_name AS doctor_full_name, " +
                "    p.full_name AS patient_full_name " +
                "FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.id " +
                "JOIN patients p ON a.patient_id = p.id " +
                "WHERE a.patient_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int doctorId = rs.getInt("doctor_id");
                LocalDate date = rs.getDate("appointment_date").toLocalDate();
                LocalTime time = rs.getTime("appointment_time").toLocalTime();
                String status = rs.getString("status");
                String doctorName = rs.getString("doctor_full_name");
                String patientName = rs.getString("patient_full_name");
                Appointment appointment = new Appointment(id, doctorId, date, time, patientName, doctorName, status);
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching appointments for patient: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    public Map<DayOfWeek, LocalTime[]> getDoctorAvailability(int doctorId) {
        Map<DayOfWeek, LocalTime[]> availabilityMap = new HashMap<>();
        String sql = "SELECT day_of_week, start_time, end_time FROM availability WHERE doctor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                DayOfWeek day = DayOfWeek.valueOf(rs.getString("day_of_week").toUpperCase());
                LocalTime start = rs.getTime("start_time").toLocalTime();
                LocalTime end = rs.getTime("end_time").toLocalTime();
                availabilityMap.put(day, new LocalTime[]{start, end});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availabilityMap;
    }

    public Set<LocalTime> getBookedSlots(int doctorId, LocalDate date) {
        Set<LocalTime> bookedSlots = new HashSet<>();
        String sql = "SELECT appointment_time FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookedSlots.add(rs.getTime("appointment_time").toLocalTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookedSlots;
    }

    public boolean bookAppointment(int doctorId, int patientId, LocalDate date, LocalTime time, String reason) {
        String sql = "INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time, status, reason) VALUES (?, ?, ?, ?, 'Pending', ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.setInt(2, patientId);
            pstmt.setDate(3, java.sql.Date.valueOf(date));
            pstmt.setTime(4, java.sql.Time.valueOf(time));
            pstmt.setString(5, reason);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelAppointment(int appointmentId) {
        String sql = "UPDATE appointments SET status = 'Cancelled' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateAppointmentStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, appointmentId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}