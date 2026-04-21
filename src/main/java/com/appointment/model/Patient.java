package com.appointment.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
@DiscriminatorValue("PATIENT")
public class Patient extends User {

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    public Patient() {}

    public Patient(String name, String email, String phone, String password) {
        super(name, email, phone, password, UserRole.PATIENT);
    }

    @Override
    public UserRole getDefaultRole() { return UserRole.PATIENT; }

    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> v) { this.appointments = v; }

    public void bookAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointment.setPatient(this);
    }

    public void cancelAppointment(Appointment appointment) {
        appointment.cancel();
    }
}
