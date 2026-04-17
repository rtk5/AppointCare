package com.appointment.service;

import com.appointment.model.Appointment;
import com.appointment.model.AppointmentStatus;

import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Appointment book(Long patientId, Long doctorId, Long scheduleId, String reason);
    Appointment reschedule(Long appointmentId, Long newScheduleId);
    Appointment cancel(Long appointmentId);
    Appointment updateStatus(Long appointmentId, AppointmentStatus status);
    Optional<Appointment> findById(Long id);
    List<Appointment> findByPatient(Long patientId);
    List<Appointment> findByDoctor(Long doctorId);
    List<Appointment> findAll();
    Appointment complete(Long appointmentId);
}
