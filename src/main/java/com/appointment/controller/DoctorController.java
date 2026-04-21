package com.appointment.controller;

import com.appointment.config.AuthHelper;
import com.appointment.model.*;
import com.appointment.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private final AuthHelper authHelper;
    private final AppointmentService appointmentService;
    private final ScheduleService scheduleService;
    private final NotificationService notificationService;

    public DoctorController(AuthHelper authHelper, AppointmentService appointmentService,
                            ScheduleService scheduleService, NotificationService notificationService) {
        this.authHelper = authHelper;
        this.appointmentService = appointmentService;
        this.scheduleService = scheduleService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Doctor doctor = authHelper.getCurrentDoctor();
        List<Appointment> all = appointmentService.findByDoctor(doctor.getUserId());
        long pending   = all.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING).count();
        long approved  = all.stream().filter(a -> a.getStatus() == AppointmentStatus.APPROVED).count();
        long completed = all.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
        long unread    = notificationService.countUnread(doctor.getUserId());

        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", all.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.PENDING
                          || a.getStatus() == AppointmentStatus.APPROVED)
                .limit(5).toList());
        model.addAttribute("pending", pending);
        model.addAttribute("approved", approved);
        model.addAttribute("completed", completed);
        model.addAttribute("unread", unread);
        return "doctor/dashboard";
    }

    @GetMapping("/appointments")
    public String allAppointments(@RequestParam(required = false) String status, Model model) {
        Doctor doctor = authHelper.getCurrentDoctor();
        List<Appointment> appointments = appointmentService.findByDoctor(doctor.getUserId());
        if (status != null && !status.isBlank()) {
            AppointmentStatus s = AppointmentStatus.valueOf(status.toUpperCase());
            appointments = appointments.stream().filter(a -> a.getStatus() == s).toList();
        }
        model.addAttribute("appointments", appointments);
        model.addAttribute("doctor", doctor);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", AppointmentStatus.values());
        return "doctor/appointments";
    }

    @PostMapping("/appointments/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes ra) {
        try { appointmentService.updateStatus(id, AppointmentStatus.APPROVED); ra.addFlashAttribute("success", "Approved."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/doctor/appointments";
    }

    @PostMapping("/appointments/{id}/reject")
    public String reject(@PathVariable Long id, RedirectAttributes ra) {
        try { appointmentService.updateStatus(id, AppointmentStatus.REJECTED); ra.addFlashAttribute("success", "Rejected."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/doctor/appointments";
    }

    @PostMapping("/appointments/{id}/complete")
    public String complete(@PathVariable Long id, RedirectAttributes ra) {
        try { appointmentService.complete(id); ra.addFlashAttribute("success", "Marked complete."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/doctor/appointments";
    }

    @GetMapping("/schedule")
    public String schedule(Model model) {
        Doctor doctor = authHelper.getCurrentDoctor();
        model.addAttribute("doctor", doctor);
        model.addAttribute("slots", scheduleService.getAllSlotsForDoctor(doctor.getUserId()));
        return "doctor/schedule";
    }

    @PostMapping("/schedule/add")
    public String addSlot(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
                          RedirectAttributes ra) {
        Doctor doctor = authHelper.getCurrentDoctor();
        try { scheduleService.addSlot(doctor.getUserId(), date, time); ra.addFlashAttribute("success", "Slot added."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/doctor/schedule";
    }

    @PostMapping("/schedule/{id}/delete")
    public String deleteSlot(@PathVariable Long id, RedirectAttributes ra) {
        try { scheduleService.deleteSlot(id); ra.addFlashAttribute("success", "Slot removed."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/doctor/schedule";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        Doctor doctor = authHelper.getCurrentDoctor();
        notificationService.markAllRead(doctor.getUserId());
        model.addAttribute("notifications", notificationService.getForUser(doctor.getUserId()));
        model.addAttribute("doctor", doctor);
        return "doctor/notifications";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("doctor", authHelper.getCurrentDoctor());
        return "doctor/profile";
    }
}
