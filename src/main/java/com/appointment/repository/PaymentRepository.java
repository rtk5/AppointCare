package com.appointment.repository;

import com.appointment.model.Payment;
import com.appointment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByAppointmentAppointmentId(Long appointmentId);
    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = com.appointment.model.PaymentStatus.COMPLETED")
    Double getTotalRevenue();
}
