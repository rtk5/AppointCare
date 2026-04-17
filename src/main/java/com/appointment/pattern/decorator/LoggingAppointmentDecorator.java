package com.appointment.pattern.decorator;

import com.appointment.model.Appointment;

import java.time.LocalDateTime;

/**
 * Concrete Decorator: Adds logging behaviour around appointment processing.
 */
public class LoggingAppointmentDecorator implements AppointmentProcessor {

    private final AppointmentProcessor wrapped;

    public LoggingAppointmentDecorator(AppointmentProcessor wrapped) {
        this.wrapped = wrapped;
    }


    @Override
    public Appointment process(Appointment appointment) {
        System.out.println("[AUDIT LOG] " + LocalDateTime.now()
                + " | Processing appointment for patient: "
                + appointment.getPatient().getName()
                + " | Doctor: " + appointment.getDoctor().getName()
                + " | Date: " + appointment.getDate());

        Appointment result = wrapped.process(appointment);

        System.out.println("[AUDIT LOG] " + LocalDateTime.now()
                + " | Appointment processed successfully | Status: " + result.getStatus());
        return result;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Logging";
    }
}
