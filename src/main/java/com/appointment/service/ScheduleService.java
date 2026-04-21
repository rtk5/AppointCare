package com.appointment.service;

import com.appointment.model.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    Schedule addSlot(Long doctorId, LocalDate date, LocalTime time);
    List<Schedule> getAvailableSlots(Long doctorId);
    List<Schedule> getAllSlotsForDoctor(Long doctorId);
    Optional<Schedule> findById(Long id);
    void deleteSlot(Long id);
    Schedule save(Schedule schedule);
}
