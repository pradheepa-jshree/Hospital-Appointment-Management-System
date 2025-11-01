package com.medibook.hospital.appointmentinterface.patient.view;

/**
 * Interface for communicating actions from child views (like MyAppointmentsView)
 * back to the main parent controller (PatientPortalMainView).
 */
public interface AppointmentViewListener {
    /**
     * Called when a user requests to reschedule an appointment.
     * @param doctorId The ID of the doctor for the appointment.
     * @param oldAppointmentId The ID of the appointment to be cancelled after rescheduling.
     */
    void onRescheduleRequested(int doctorId, int oldAppointmentId);
}
