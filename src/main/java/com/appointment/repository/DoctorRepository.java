package com.appointment.repository;

import com.appointment.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);
    List<Doctor> findByHospitalContainingIgnoreCase(String hospital);

    @Query("SELECT d FROM Doctor d WHERE " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.hospital) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Doctor> searchDoctors(String query);
}
