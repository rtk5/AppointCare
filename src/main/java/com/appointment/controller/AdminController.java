package com.appointment.controller;

import com.appointment.config.AuthHelper;
import com.appointment.dto.RegisterDto;
import com.appointment.model.*;
import com.appointment.repository.UserRepository;
import com.appointment.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AuthHelper authHelper;
    private final UserService userService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public AdminController(AuthHelper authHelper, UserService userService, DoctorService doctorService,
                           AppointmentService appointmentService, PaymentService paymentService,
                           UserRepository userRepository) {
        this.authHelper = authHelper;
        this.userService = userService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User admin = authHelper.getCurrentUser();
        long totalDoctors  = doctorService.findAll().size();
        long totalPatients = userRepository.findByRole(UserRole.PATIENT).size();
        List<Appointment> all = appointmentService.findAll();
        long pendingAppts  = all.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING).count();
        Double revenue     = paymentService.getTotalRevenue();

        model.addAttribute("admin", admin);
        model.addAttribute("totalDoctors", totalDoctors);
        model.addAttribute("totalPatients", totalPatients);
        model.addAttribute("totalAppts", (long) all.size());
        model.addAttribute("pendingAppts", pendingAppts);
        model.addAttribute("revenue", revenue != null ? revenue : 0.0);
        model.addAttribute("recentAppts", all.stream().limit(8).toList());
        return "admin/dashboard";
    }

    @GetMapping("/doctors")
    public String doctors(Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("admin", authHelper.getCurrentUser());
        model.addAttribute("registerDto", new RegisterDto());
        return "admin/doctors";
    }

    @PostMapping("/doctors/add")
    public String addDoctor(@ModelAttribute RegisterDto dto, RedirectAttributes ra) {
        dto.setRole(UserRole.DOCTOR);
        try {
            userService.register(dto);
            ra.addFlashAttribute("success", "Doctor added: " + dto.getName());
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/doctors";
    }

    @PostMapping("/doctors/{id}/remove")
    public String removeDoctor(@PathVariable Long id, RedirectAttributes ra) {
        try { userService.deleteUser(id); ra.addFlashAttribute("success", "Doctor removed."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/doctors";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("admin", authHelper.getCurrentUser());
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        try { userService.deleteUser(id); ra.addFlashAttribute("success", "User deleted."); }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/admin/users";
    }

    @GetMapping("/appointments")
    public String appointments(@RequestParam(required = false) String status, Model model) {
        List<Appointment> appts = appointmentService.findAll();
        if (status != null && !status.isBlank()) {
            AppointmentStatus s = AppointmentStatus.valueOf(status.toUpperCase());
            appts = appts.stream().filter(a -> a.getStatus() == s).toList();
        }
        model.addAttribute("appointments", appts);
        model.addAttribute("statuses", AppointmentStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("admin", authHelper.getCurrentUser());
        return "admin/appointments";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        List<Appointment> all = appointmentService.findAll();
        model.addAttribute("admin", authHelper.getCurrentUser());
        model.addAttribute("totalAppts", (long) all.size());
        model.addAttribute("pending",   all.stream().filter(a -> a.getStatus() == AppointmentStatus.PENDING).count());
        model.addAttribute("approved",  all.stream().filter(a -> a.getStatus() == AppointmentStatus.APPROVED).count());
        model.addAttribute("rejected",  all.stream().filter(a -> a.getStatus() == AppointmentStatus.REJECTED).count());
        model.addAttribute("cancelled", all.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count());
        model.addAttribute("completed", all.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count());
        model.addAttribute("revenue", paymentService.getTotalRevenue());
        model.addAttribute("payments", paymentService.findAll());
        model.addAttribute("doctors", doctorService.findAll());
        return "admin/reports";
    }
}
