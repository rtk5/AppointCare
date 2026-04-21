package com.appointment.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@DiscriminatorValue("DOCTOR")
public class Doctor extends User {

    private String specialization;
    private String qualification;
    private String hospital;
    private double consultationFee;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    public Doctor() {}

    public Doctor(String name, String email, String phone, String password,
                  String specialization, String qualification, String hospital, double consultationFee) {
        super(name, email, phone, password, UserRole.DOCTOR);
        this.specialization = specialization;
        this.qualification = qualification;
        this.hospital = hospital;
        this.consultationFee = consultationFee;
    }

    @Override
    public UserRole getDefaultRole() { return UserRole.DOCTOR; }

    public void approveAppointment(Appointment a) { a.setStatus(AppointmentStatus.APPROVED); }
    public void rejectAppointment(Appointment a)  { a.setStatus(AppointmentStatus.REJECTED); }
    public void updateAvailability(Schedule s)    { s.updateSlot(); }

    public String getSpecialization()               { return specialization; }
    public void setSpecialization(String v)         { this.specialization = v; }
    public String getQualification()                { return qualification; }
    public void setQualification(String v)          { this.qualification = v; }
    public String getHospital()                     { return hospital; }
    public void setHospital(String v)               { this.hospital = v; }
    public double getConsultationFee()              { return consultationFee; }
    public void setConsultationFee(double v)        { this.consultationFee = v; }
    public List<Schedule> getSchedules()            { return schedules; }
    public void setSchedules(List<Schedule> v)      { this.schedules = v; }
    public List<Appointment> getAppointments()      { return appointments; }
    public void setAppointments(List<Appointment> v){ this.appointments = v; }
}
