package com.appointment.repository;

import com.appointment.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDoctorUserIdAndIsBookedFalse(Long doctorId);
    List<Schedule> findByDoctorUserId(Long doctorId);

    @Query("SELECT s FROM Schedule s WHERE s.doctor.userId = :doctorId " +
           "AND s.isBooked = false AND s.availableDate >= :today ORDER BY s.availableDate, s.availableTime")
    List<Schedule> findAvailableSlots(Long doctorId, LocalDate today);

    List<Schedule> findByDoctorUserIdAndAvailableDateBetween(Long doctorId, LocalDate start, LocalDate end);
}
