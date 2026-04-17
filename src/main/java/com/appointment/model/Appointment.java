package com.appointment.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    private LocalDate date;
    private LocalTime time;
    private String reason;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    private LocalDateTime createdAt;

    public Appointment() {}

    public Appointment(Patient patient, Doctor doctor, Schedule schedule, String reason) {
        this.patient = patient;
        this.doctor = doctor;
        this.schedule = schedule;
        this.date = schedule.getAvailableDate();
        this.time = schedule.getAvailableTime();
        this.reason = reason;
        this.status = AppointmentStatus.PENDING;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = AppointmentStatus.PENDING;
    }

    public void create() {
        this.status = AppointmentStatus.PENDING;
        if (this.schedule != null) this.schedule.markBooked();
    }

    public void cancel() {
        this.status = AppointmentStatus.CANCELLED;
        if (this.schedule != null) this.schedule.markAvailable();
    }

    public void reschedule(Schedule newSchedule) {
        if (this.schedule != null) this.schedule.markAvailable();
        this.schedule = newSchedule;
        this.date = newSchedule.getAvailableDate();
        this.time = newSchedule.getAvailableTime();
        newSchedule.markBooked();
        this.status = AppointmentStatus.PENDING;
    }

    public void complete() { this.status = AppointmentStatus.COMPLETED; }

    // Getters & Setters
    public Long getAppointmentId()              { return appointmentId; }
    public void setAppointmentId(Long v)        { this.appointmentId = v; }
    public Patient getPatient()                  { return patient; }
    public void setPatient(Patient v)            { this.patient = v; }
    public Doctor getDoctor()                    { return doctor; }
    public void setDoctor(Doctor v)              { this.doctor = v; }
    public Schedule getSchedule()                { return schedule; }
    public void setSchedule(Schedule v)          { this.schedule = v; }
    public LocalDate getDate()                   { return date; }
    public void setDate(LocalDate v)             { this.date = v; }
    public LocalTime getTime()                   { return time; }
    public void setTime(LocalTime v)             { this.time = v; }
    public String getReason()                    { return reason; }
    public void setReason(String v)              { this.reason = v; }
    public AppointmentStatus getStatus()         { return status; }
    public void setStatus(AppointmentStatus v)   { this.status = v; }
    public Payment getPayment()                  { return payment; }
    public void setPayment(Payment v)            { this.payment = v; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime v)    { this.createdAt = v; }
}
