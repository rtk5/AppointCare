package com.appointment.pattern.strategy;

import com.appointment.model.Payment;
import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: Credit/Debit Card Payment
 */
@Component
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("[CARD] Processing payment of ₹" + payment.getAmount()
                + " for appointment #" + payment.getAppointment().getAppointmentId());
        payment.processPayment();
        return true;
    }

    @Override
    public String getMethodName() {
        return "CARD";
    }
}
