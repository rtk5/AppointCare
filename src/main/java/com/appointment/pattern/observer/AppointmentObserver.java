package com.appointment.pattern.observer;

import com.appointment.model.Appointment;
import com.appointment.model.AppointmentStatus;

/**
 * BEHAVIORAL PATTERN: Observer Pattern
 *
 * Purpose: When an appointment's status changes, all registered observers
 * (e.g., EmailNotifier, SMSNotifier, InAppNotifier) are automatically
 * notified without tight coupling.
 *
 * Applied to: Appointment status change events triggering notifications
 * to patients and doctors.
 */
public interface AppointmentObserver {
    void onAppointmentStatusChanged(Appointment appointment, AppointmentStatus newStatus);
}
