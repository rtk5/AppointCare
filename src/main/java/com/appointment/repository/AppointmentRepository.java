package com.appointment.repository;

import com.appointment.model.Appointment;
import com.appointment.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientUserId(Long patientId);
    List<Appointment> findByDoctorUserId(Long doctorId);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByPatientUserIdAndStatus(Long patientId, AppointmentStatus status);
    List<Appointment> findByDoctorUserIdAndStatus(Long doctorId, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a ORDER BY a.createdAt DESC")
    List<Appointment> findAllOrderByCreatedAtDesc();

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
    long countByStatus(AppointmentStatus status);
}
