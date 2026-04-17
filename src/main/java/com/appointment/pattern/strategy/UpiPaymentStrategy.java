package com.appointment.pattern.strategy;

import com.appointment.model.Payment;
import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: UPI Payment
 */
@Component
public class UpiPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        // Simulate UPI payment processing
        System.out.println("[UPI] Processing payment of ₹" + payment.getAmount()
                + " for appointment #" + payment.getAppointment().getAppointmentId());
        payment.processPayment();
        return true;
    }

    @Override
    public String getMethodName() {
        return "UPI";
    }
}
