package com.appointment.pattern.observer;

import com.appointment.model.*;
import com.appointment.repository.NotificationRepository;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer: In-App Notification
 * Persists notifications to the database for display in the UI.
 */
@Component
public class InAppNotifier implements AppointmentObserver {

    private final NotificationRepository notificationRepository;

    public InAppNotifier(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    @Override
    public void onAppointmentStatusChanged(Appointment appointment, AppointmentStatus newStatus) {
        String patientMsg = buildPatientMessage(appointment, newStatus);
        String doctorMsg = buildDoctorMessage(appointment, newStatus);
        NotificationType type = mapToType(newStatus);

        // Notify patient
        Notification patientNotif = new Notification(
                appointment.getPatient(), appointment, patientMsg, type
        );
        notificationRepository.save(patientNotif);

        // Notify doctor
        Notification doctorNotif = new Notification(
                appointment.getDoctor(), appointment, doctorMsg, type
        );
        notificationRepository.save(doctorNotif);

        // Simulate SMS/Email
        patientNotif.sendEmail();
        doctorNotif.sendEmail();
    }

    private String buildPatientMessage(Appointment appointment, AppointmentStatus status) {
        String doctorName = appointment.getDoctor().getName();
        String date = appointment.getDate().toString();
        return switch (status) {
            case APPROVED -> "Your appointment with Dr. " + doctorName + " on " + date + " has been APPROVED.";
            case REJECTED -> "Your appointment with Dr. " + doctorName + " on " + date + " has been REJECTED.";
            case CANCELLED -> "Your appointment with Dr. " + doctorName + " on " + date + " has been CANCELLED.";
            case COMPLETED -> "Your appointment with Dr. " + doctorName + " on " + date + " is marked COMPLETED.";
            default -> "Your appointment status has been updated to: " + status;
        };
    }

    private String buildDoctorMessage(Appointment appointment, AppointmentStatus status) {
        String patientName = appointment.getPatient().getName();
        String date = appointment.getDate().toString();
        return switch (status) {
            case PENDING -> "New appointment request from " + patientName + " on " + date + ".";
            case CANCELLED -> patientName + " has cancelled the appointment on " + date + ".";
            case COMPLETED -> "Appointment with " + patientName + " on " + date + " marked complete.";
            default -> "Appointment status changed to: " + status;
        };
    }

    private NotificationType mapToType(AppointmentStatus status) {
        return switch (status) {
            case PENDING -> NotificationType.APPOINTMENT_BOOKED;
            case APPROVED -> NotificationType.APPOINTMENT_APPROVED;
            case REJECTED -> NotificationType.APPOINTMENT_REJECTED;
            case CANCELLED -> NotificationType.APPOINTMENT_CANCELLED;
            case COMPLETED -> NotificationType.APPOINTMENT_BOOKED;
        };
    }
}
