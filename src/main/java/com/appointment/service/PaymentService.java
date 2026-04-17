package com.appointment.service;

import com.appointment.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment processPayment(Long appointmentId, String method);
    Payment refund(Long paymentId);
    Optional<Payment> findByAppointment(Long appointmentId);
    List<Payment> findAll();
    Double getTotalRevenue();
}
