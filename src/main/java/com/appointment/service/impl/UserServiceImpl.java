package com.appointment.service.impl;

import com.appointment.dto.RegisterDto;
import com.appointment.model.*;
import com.appointment.pattern.factory.UserFactory;
import com.appointment.repository.UserRepository;
import com.appointment.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Design Principle: Dependency Inversion - depends on UserRepository abstraction.
 * Design Principle: Single Responsibility - only handles user-related business logic.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserFactory userFactory, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User register(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user;
        switch (dto.getRole()) {
            case PATIENT -> user = userFactory.createPatient(
                    dto.getName(), dto.getEmail(), dto.getPhone(), encodedPassword);
            case DOCTOR -> user = userFactory.createDoctor(
                    dto.getName(), dto.getEmail(), dto.getPhone(), encodedPassword,
                    dto.getSpecialization(), dto.getQualification(),
                    dto.getHospital(), dto.getConsultationFee());
            case ADMIN -> user = userFactory.createAdmin(
                    dto.getName(), dto.getEmail(), dto.getPhone(), encodedPassword,
                    dto.getDepartment() != null ? dto.getDepartment() : "Management");
            default -> throw new IllegalArgumentException("Unknown role: " + dto.getRole());
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateProfile(Long id, String name, String phone) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setName(name);
        user.setPhone(phone);
        return userRepository.save(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
