package com.appointment.pattern.decorator;

import com.appointment.model.Appointment;
import com.appointment.model.AppointmentStatus;

/**
 * Concrete Component: Basic appointment processor that sets status to PENDING.
 */
public class BasicAppointmentProcessor implements AppointmentProcessor {

    @Override
    public Appointment process(Appointment appointment) {
        appointment.setStatus(AppointmentStatus.PENDING);
        return appointment;
    }

    @Override
    public String getDescription() {
        return "Basic Appointment Processor";
    }
}
