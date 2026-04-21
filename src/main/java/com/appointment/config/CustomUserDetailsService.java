package com.appointment.config;

import com.appointment.model.User;
import com.appointment.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}        if (!userService.emailExists("admin@clinic.com")) {
            RegisterDto dto = new RegisterDto();
            dto.setName("System Admin");
            dto.setEmail("admin@clinic.com");
            dto.setPhone("9000000001");
            dto.setPassword("admin123");
            dto.setRole(UserRole.ADMIN);
            dto.setDepartment("Management");
            userService.register(dto);
            System.out.println("✅ Admin seeded: admin@clinic.com / admin123");
        }
    }

    private void seedDoctors() {
        String[][] doctors = {
            {"Dr. Priya Sharma",    "priya@clinic.com",   "9000000002", "Cardiology",      "MD Cardiology",   "Apollo Hospital",    "800"},
            {"Dr. Rahul Mehta",     "rahul@clinic.com",   "9000000003", "Neurology",       "DM Neurology",    "Fortis Hospital",    "1000"},
            {"Dr. Anita Nair",      "anita@clinic.com",   "9000000004", "Dermatology",     "MD Dermatology",  "Manipal Hospital",   "600"},
            {"Dr. Suresh Kumar",    "suresh@clinic.com",  "9000000005", "Orthopedics",     "MS Orthopedics",  "City Hospital",      "700"},
            {"Dr. Meena Reddy",     "meena@clinic.com",   "9000000006", "Pediatrics",      "MD Pediatrics",   "Rainbow Hospital",   "500"},
            {"Dr. Vikram Joshi",    "vikram@clinic.com",  "9000000007", "Ophthalmology",   "MS Ophthalmology","Vision Care Centre", "550"},
        };

        for (String[] d : doctors) {
            if (!userService.emailExists(d[1])) {
                RegisterDto dto = new RegisterDto();
                dto.setName(d[0]);
                dto.setEmail(d[1]);
                dto.setPhone(d[2]);
                dto.setPassword("doctor123");
                dto.setRole(UserRole.DOCTOR);
                dto.setSpecialization(d[3]);
                dto.setQualification(d[4]);
                dto.setHospital(d[5]);
                dto.setConsultationFee(Double.parseDouble(d[6]));
                userService.register(dto);
            }
        }
        System.out.println("✅ Doctors seeded (password: doctor123)");
    }

    private void seedPatients() {
        String[][] patients = {
            {"Arjun Patel",   "arjun@example.com",  "9100000001"},
            {"Kavya Iyer",    "kavya@example.com",   "9100000002"},
            {"Ravi Krishnan", "ravi@example.com",    "9100000003"},
        };
        for (String[] p : patients) {
            if (!userService.emailExists(p[1])) {
                RegisterDto dto = new RegisterDto();
                dto.setName(p[0]);
                dto.setEmail(p[1]);
                dto.setPhone(p[2]);
                dto.setPassword("patient123");
                dto.setRole(UserRole.PATIENT);
                userService.register(dto);
            }
        }
        System.out.println("✅ Patients seeded (password: patient123)");
    }

    private void seedSchedules() {
        doctorRepository.findAll().forEach(doctor -> {
            LocalDate today = LocalDate.now();
            LocalTime[] times = {
                LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
                LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0)
            };
            for (int dayOffset = 1; dayOffset <= 7; dayOffset++) {
                for (LocalTime t : times) {
                    scheduleService.addSlot(doctor.getUserId(), today.plusDays(dayOffset), t);
                }
            }
        });
        System.out.println("✅ Schedules seeded (7 days × 6 slots per doctor)");
    }
}
