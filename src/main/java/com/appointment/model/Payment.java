package com.appointment.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    private double amount;
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime paidAt;

    public Payment() {}

    public Payment(Appointment appointment, double amount, String paymentMethod) {
        this.appointment = appointment;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
    }

    public void processPayment() {
        this.status = PaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }

    public void refund() { this.status = PaymentStatus.REFUNDED; }

    public Long getPaymentId()              { return paymentId; }
    public void setPaymentId(Long v)        { this.paymentId = v; }
    public Appointment getAppointment()     { return appointment; }
    public void setAppointment(Appointment v){ this.appointment = v; }
    public double getAmount()               { return amount; }
    public void setAmount(double v)         { this.amount = v; }
    public String getPaymentMethod()        { return paymentMethod; }
    public void setPaymentMethod(String v)  { this.paymentMethod = v; }
    public PaymentStatus getStatus()        { return status; }
    public void setStatus(PaymentStatus v)  { this.status = v; }
    public LocalDateTime getPaidAt()        { return paidAt; }
    public void setPaidAt(LocalDateTime v)  { this.paidAt = v; }
}
