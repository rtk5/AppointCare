package com.appointment.pattern.factory;

import com.appointment.model.*;
import org.springframework.stereotype.Component;

/**
 * CREATIONAL PATTERN: Factory Method Pattern
 *
 * Purpose: Centralizes user object creation, decoupling the creation logic
 * from the rest of the application. New user types can be added by extending
 * this factory without changing client code.
 *
 * Applied to: Creating Patient, Doctor, and Admin instances consistently
 * throughout the application.
 */
@Component
public class UserFactory {

    /**
     * Factory method to create the correct User subtype based on role.
     */
    public User createUser(UserRole role, String name, String email,
                           String phone, String password, String extra) {
        return switch (role) {
            case PATIENT -> new Patient(name, email, phone, password);
            case DOCTOR -> {
                // extra = "specialization|qualification|hospital|fee"
                String[] parts = extra != null ? extra.split("\\|") : new String[]{"General", "MBBS", "City Hospital", "500"};
                String specialization = parts.length > 0 ? parts[0] : "General";
                String qualification = parts.length > 1 ? parts[1] : "MBBS";
                String hospital = parts.length > 2 ? parts[2] : "City Hospital";
                double fee = parts.length > 3 ? Double.parseDouble(parts[3]) : 500.0;
                yield new Doctor(name, email, phone, password, specialization, qualification, hospital, fee);
            }
            case ADMIN -> new Admin(name, email, phone, password, extra != null ? extra : "Management");
        };
    }

    public Patient createPatient(String name, String email, String phone, String password) {
        return new Patient(name, email, phone, password);
    }

    public Doctor createDoctor(String name, String email, String phone, String password,
                               String specialization, String qualification, String hospital, double fee) {
        return new Doctor(name, email, phone, password, specialization, qualification, hospital, fee);
    }

    public Admin createAdmin(String name, String email, String phone, String password, String department) {
        return new Admin(name, email, phone, password, department);
    }
}
