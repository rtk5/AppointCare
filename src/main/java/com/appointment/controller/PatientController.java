package com.appointment.controller;

import com.appointment.config.AuthHelper;
import com.appointment.model.*;
import com.appointment.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final DoctorService doctorService;
    private final ScheduleService scheduleService;
    private final AppointmentService appointmentService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    public PatientController(AuthHelper authHelper, UserService userService,
                             DoctorService doctorService, ScheduleService scheduleService,
                             AppointmentService appointmentService, PaymentService paymentService,
                             NotificationService notificationService) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.doctorService = doctorService;
        this.scheduleService = scheduleService;
        this.appointmentService = appointmentService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Patient patient = authHelper.getCurrentPatient();
        List<Appointment> appointments = appointmentService.findByPatient(patient.getUserId());
        long pending  = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING).count();
        long approved = appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.APPROVED).count();
        long unread   = notificationService.countUnread(patient.getUserId());

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointments.stream().limit(5).toList());
        model.addAttribute("pending", pending);
        model.addAttribute("approved", approved);
        model.addAttribute("total", (long) appointments.size());
        model.addAttribute("unread", unread);
        return "patient/dashboard";
    }

    @GetMapping("/doctors")
    public String searchDoctors(@RequestParam(required = false) String query, Model model) {
        model.addAttribute("doctors", doctorService.search(query));
        model.addAttribute("query", query);
        model.addAttribute("patient", authHelper.getCurrentPatient());
        return "patient/doctors";
    }

    @GetMapping("/book/{doctorId}")
    public String showBookingForm(@PathVariable Long doctorId, Model model) {
        Doctor doctor = doctorService.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        model.addAttribute("doctor", doctor);
        model.addAttribute("slots", scheduleService.getAvailableSlots(doctorId));
        model.addAttribute("patient", authHelper.getCurrentPatient());
        return "patient/book";
    }

    @PostMapping("/book")
    public String bookAppointment(@RequestParam Long doctorId,
                                  @RequestParam Long scheduleId,
                                  @RequestParam String reason,
                                  RedirectAttributes ra) {
        Patient patient = authHelper.getCurrentPatient();
        try {
            Appointment appt = appointmentService.book(patient.getUserId(), doctorId, scheduleId, reason);
            ra.addFlashAttribute("success", "Appointment booked! ID: #" + appt.getAppointmentId());
            return "redirect:/patient/appointments";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/book/" + doctorId;
        }
    }

    @GetMapping("/appointments")
    public String myAppointments(Model model) {
        Patient patient = authHelper.getCurrentPatient();
        model.addAttribute("appointments", appointmentService.findByPatient(patient.getUserId()));
        model.addAttribute("patient", patient);
        return "patient/appointments";
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes ra) {
        try {
            appointmentService.cancel(id);
            ra.addFlashAttribute("success", "Appointment cancelled.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/patient/appointments";
    }

    @GetMapping("/appointments/{id}/reschedule")
    public String showReschedule(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));
        model.addAttribute("appointment", appointment);
        model.addAttribute("slots", scheduleService.getAvailableSlots(appointment.getDoctor().getUserId()));
        model.addAttribute("patient", authHelper.getCurrentPatient());
        return "patient/reschedule";
    }

    @PostMapping("/appointments/{id}/reschedule")
    public String reschedule(@PathVariable Long id, @RequestParam Long scheduleId, RedirectAttributes ra) {
        try {
            appointmentService.reschedule(id, scheduleId);
            ra.addFlashAttribute("success", "Appointment rescheduled.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/patient/appointments";
    }

    @GetMapping("/appointments/{id}/pay")
    public String showPayment(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found")));
        model.addAttribute("patient", authHelper.getCurrentPatient());
        return "patient/payment";
    }

    @PostMapping("/appointments/{id}/pay")
    public String processPayment(@PathVariable Long id, @RequestParam String method, RedirectAttributes ra) {
        try {
            paymentService.processPayment(id, method);
            ra.addFlashAttribute("success", "Payment successful via " + method + "!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/patient/appointments";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        Patient patient = authHelper.getCurrentPatient();
        notificationService.markAllRead(patient.getUserId());
        model.addAttribute("notifications", notificationService.getForUser(patient.getUserId()));
        model.addAttribute("patient", patient);
        return "patient/notifications";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("user", authHelper.getCurrentPatient());
        return "patient/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String name, @RequestParam String phone, RedirectAttributes ra) {
        Patient patient = authHelper.getCurrentPatient();
        try {
            userService.updateProfile(patient.getUserId(), name, phone);
            ra.addFlashAttribute("success", "Profile updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/patient/profile";
    }
}
