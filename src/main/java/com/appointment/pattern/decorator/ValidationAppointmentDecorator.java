package com.appointment.pattern.decorator;

import com.appointment.model.Appointment;

/**
 * Concrete Decorator: Adds validation checks before processing an appointment.
 */
public class ValidationAppointmentDecorator implements AppointmentProcessor {

    private final AppointmentProcessor wrapped;

    public ValidationAppointmentDecorator(AppointmentProcessor wrapped) {
        this.wrapped = wrapped;
    }


    @Override
    public Appointment process(Appointment appointment) {
        validate(appointment);
        return wrapped.process(appointment);
    }

    private void validate(Appointment appointment) {
        if (appointment.getPatient() == null) {
            throw new IllegalStateException("Appointment must have a patient.");
        }
        if (appointment.getDoctor() == null) {
            throw new IllegalStateException("Appointment must have a doctor.");
        }
        if (appointment.getSchedule() == null) {
            throw new IllegalStateException("Appointment must have a schedule slot.");
        }
        if (appointment.getSchedule().isBooked()) {
            throw new IllegalStateException("This time slot is already booked.");
        }
        System.out.println("[VALIDATION] Appointment validation passed.");
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Validation";
    }
}
