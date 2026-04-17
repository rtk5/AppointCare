package com.appointment.service.impl;

import com.appointment.model.*;
import com.appointment.pattern.strategy.PaymentContext;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.PaymentRepository;
import com.appointment.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Payment service - uses the Strategy Pattern (via PaymentContext)
 * to select the correct payment processor at runtime.
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentContext paymentContext;

    public PaymentServiceImpl(PaymentRepository paymentRepository, AppointmentRepository appointmentRepository, PaymentContext paymentContext) {
        this.paymentRepository = paymentRepository;
        this.appointmentRepository = appointmentRepository;
        this.paymentContext = paymentContext;
    }


    @Override
    public Payment processPayment(Long appointmentId, String method) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // Check if payment already exists
        Optional<Payment> existing = paymentRepository.findByAppointmentAppointmentId(appointmentId);
        if (existing.isPresent() && existing.get().getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Payment already processed for this appointment");
        }

        double amount = appointment.getDoctor().getConsultationFee();
        Payment payment = existing.orElse(new Payment(appointment, amount, method));
        payment.setPaymentMethod(method);
        payment = paymentRepository.save(payment);

        // Strategy Pattern: delegate to the right payment processor
        paymentContext.executePayment(payment, method);

        return paymentRepository.save(payment);
    }

    @Override
    public Payment refund(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        payment.refund();
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> findByAppointment(Long appointmentId) {
        return paymentRepository.findByAppointmentAppointmentId(appointmentId);
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Double getTotalRevenue() {
        Double total = paymentRepository.getTotalRevenue();
        return total != null ? total : 0.0;
    }
}
