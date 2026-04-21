package com.appointment.service;

import com.appointment.model.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<Doctor> findAll();
    Optional<Doctor> findById(Long id);
    List<Doctor> search(String query);
    Doctor save(Doctor doctor);
    void delete(Long id);
    List<Doctor> findBySpecialization(String specialization);
}
