package com.appointment.service.impl;

import com.appointment.model.*;
import com.appointment.pattern.decorator.*;
import com.appointment.pattern.observer.AppointmentObserver;
import com.appointment.repository.*;
import com.appointment.service.AppointmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Core appointment service.
 * Uses the Decorator Pattern to wrap booking with validation + logging.
 * Uses the Observer Pattern to notify users on status changes.
 */
@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;
    private final List<AppointmentObserver> observers;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, ScheduleRepository scheduleRepository, List<AppointmentObserver> observers) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.scheduleRepository = scheduleRepository;
        this.observers = observers;
    }


    @Override
    public Appointment book(Long patientId, Long doctorId, Long scheduleId, String reason) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule slot not found"));

        Appointment appointment = new Appointment(patient, doctor, schedule, reason);

        // Decorator Pattern: wrap processor with validation then logging
        AppointmentProcessor processor =
                new LoggingAppointmentDecorator(
                        new ValidationAppointmentDecorator(
                                new BasicAppointmentProcessor()));

        processor.process(appointment);
        schedule.markBooked();
        scheduleRepository.save(schedule);

        Appointment saved = appointmentRepository.save(appointment);

        // Observer Pattern: notify all observers
        notifyObservers(saved, AppointmentStatus.PENDING);
        return saved;
    }

    @Override
    public Appointment reschedule(Long appointmentId, Long newScheduleId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        Schedule newSchedule = scheduleRepository.findById(newScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule slot not found"));

        if (newSchedule.isBooked()) {
            throw new IllegalStateException("This slot is already booked");
        }

        appointment.reschedule(newSchedule);
        scheduleRepository.save(newSchedule);
        Appointment saved = appointmentRepository.save(appointment);
        notifyObservers(saved, AppointmentStatus.PENDING);
        return saved;
    }

    @Override
    public Appointment cancel(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        appointment.cancel();
        if (appointment.getSchedule() != null) {
            scheduleRepository.save(appointment.getSchedule());
        }
        Appointment saved = appointmentRepository.save(appointment);
        notifyObservers(saved, AppointmentStatus.CANCELLED);
        return saved;
    }

    @Override
    public Appointment updateStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        appointment.setStatus(status);
        Appointment saved = appointmentRepository.save(appointment);
        notifyObservers(saved, status);
        return saved;
    }

    @Override
    public Appointment complete(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        appointment.complete();
        Appointment saved = appointmentRepository.save(appointment);
        notifyObservers(saved, AppointmentStatus.COMPLETED);
        return saved;
    }

    @Override
    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Override
    public List<Appointment> findByPatient(Long patientId) {
        return appointmentRepository.findByPatientUserId(patientId);
    }

    @Override
    public List<Appointment> findByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorUserId(doctorId);
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentRepository.findAllOrderByCreatedAtDesc();
    }

    private void notifyObservers(Appointment appointment, AppointmentStatus status) {
        for (AppointmentObserver observer : observers) {
            try {
                observer.onAppointmentStatusChanged(appointment, status);
            } catch (Exception e) {
                System.err.println("Observer error: " + e.getMessage());
            }
        }
    }
}
