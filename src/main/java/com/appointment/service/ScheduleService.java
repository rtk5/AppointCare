package com.appointment.service.impl;

import com.appointment.model.Doctor;
import com.appointment.model.Schedule;
import com.appointment.repository.DoctorRepository;
import com.appointment.repository.ScheduleRepository;
import com.appointment.service.ScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, DoctorRepository doctorRepository) {
        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
    }


    @Override
    public Schedule addSlot(Long doctorId, LocalDate date, LocalTime time) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));
        Schedule schedule = new Schedule(doctor, date, time);
        return scheduleRepository.save(schedule);
    }

    @Override
    public List<Schedule> getAvailableSlots(Long doctorId) {
        return scheduleRepository.findAvailableSlots(doctorId, LocalDate.now());
    }

    @Override
    public List<Schedule> getAllSlotsForDoctor(Long doctorId) {
        return scheduleRepository.findByDoctorUserId(doctorId);
    }

    @Override
    public Optional<Schedule> findById(Long id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public void deleteSlot(Long id) {
        scheduleRepository.deleteById(id);
    }

    @Override
    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }
}
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
