package com.appointment.pattern.strategy;

import com.appointment.model.Payment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Context class for the Strategy Pattern.
 * Selects the appropriate PaymentStrategy at runtime based on method name.
 */
@Component
public class PaymentContext {

    private final List<PaymentStrategy> strategies;

    public PaymentContext(List<PaymentStrategy> strategies) {
        this.strategies = strategies;
    }


    public boolean executePayment(Payment payment, String method) {
        PaymentStrategy strategy = strategies.stream()
                .filter(s -> s.getMethodName().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown payment method: " + method));
        return strategy.processPayment(payment);
    }
}
