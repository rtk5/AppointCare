package com.appointment.service;

import com.appointment.dto.RegisterDto;
import com.appointment.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(RegisterDto dto);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAll();
    void deleteUser(Long id);
    User updateProfile(Long id, String name, String phone);
    boolean emailExists(String email);
}
