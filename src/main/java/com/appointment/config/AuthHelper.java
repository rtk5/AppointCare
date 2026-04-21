package com.appointment.config;

import com.appointment.model.*;
import com.appointment.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper to get the currently authenticated user as the correct subtype.
 * Uses role-specific repositories to avoid Hibernate proxy cast issues
 * with JOINED inheritance.
 */
@Component
public class AuthHelper {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AuthHelper(UserRepository userRepository,
                      PatientRepository patientRepository,
                      DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return auth.getName();
    }

    public User getCurrentUser() {
        String email = currentEmail();
        if (email == null) return null;
        return userRepository.findByEmail(email).orElse(null);
    }

    public Patient getCurrentPatient() {
        String email = currentEmail();
        if (email == null) return null;
        return patientRepository.findByEmail(email).orElse(null);
    }

    public Doctor getCurrentDoctor() {
        String email = currentEmail();
        if (email == null) return null;
        return doctorRepository.findByEmail(email).orElse(null);
    }
}
