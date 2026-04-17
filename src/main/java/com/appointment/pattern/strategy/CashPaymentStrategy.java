package com.appointment.pattern.strategy;

import com.appointment.model.Payment;
import org.springframework.stereotype.Component;

/**
 * Concrete Strategy: Cash Payment
 */
@Component
public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("[CASH] Recording cash payment of ₹" + payment.getAmount()
                + " for appointment #" + payment.getAppointment().getAppointmentId());
        payment.processPayment();
        return true;
    }

    @Override
    public String getMethodName() {
        return "CASH";
    }
}
