package com.appointment.pattern.strategy;

import com.appointment.model.Payment;

/**
 * BEHAVIORAL PATTERN: Strategy Pattern
 *
 * Purpose: Defines a family of payment algorithms (UPI, Card, Cash),
 * encapsulates each one, and makes them interchangeable at runtime.
 * The payment processing logic can vary independently from the clients
 * that use it.
 *
 * Applied to: Payment processing in the appointment system.
 */
public interface PaymentStrategy {
    boolean processPayment(Payment payment);
    String getMethodName();
}
