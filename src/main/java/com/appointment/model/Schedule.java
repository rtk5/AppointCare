package com.appointment.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
public class Schedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    private LocalDate availableDate;
    private LocalTime availableTime;
    private boolean isBooked;

    public Schedule() {}

    public Schedule(Doctor doctor, LocalDate availableDate, LocalTime availableTime) {
        this.doctor = doctor;
        this.availableDate = availableDate;
        this.availableTime = availableTime;
        this.isBooked = false;
    }

    public void updateSlot()   { this.isBooked = !this.isBooked; }
    public void markBooked()   { this.isBooked = true; }
    public void markAvailable(){ this.isBooked = false; }

    public Long getScheduleId()              { return scheduleId; }
    public void setScheduleId(Long v)        { this.scheduleId = v; }
    public Doctor getDoctor()                { return doctor; }
    public void setDoctor(Doctor v)          { this.doctor = v; }
    public LocalDate getAvailableDate()      { return availableDate; }
    public void setAvailableDate(LocalDate v){ this.availableDate = v; }
    public LocalTime getAvailableTime()      { return availableTime; }
    public void setAvailableTime(LocalTime v){ this.availableTime = v; }
    public boolean isBooked()                { return isBooked; }
    public void setBooked(boolean v)         { this.isBooked = v; }
}
