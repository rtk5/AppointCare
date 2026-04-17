package com.appointment.pattern.decorator;

import com.appointment.model.Appointment;

/**
 * STRUCTURAL PATTERN: Decorator Pattern
 *
 * Purpose: Dynamically adds responsibilities to appointment processing
 * (e.g., logging, validation, auditing) without altering the core
 * appointment service. Wraps the base component and adds behaviour
 * transparently.
 *
 * Applied to: Augmenting appointment booking with logging and audit trails.
 */
public interface AppointmentProcessor {
    Appointment process(Appointment appointment);
    String getDescription();
}
