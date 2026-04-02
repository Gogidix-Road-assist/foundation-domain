package com.gogidix.rapidassist.payment.service.application.usecase;

import com.gogidix.rapidassist.payment.service.domain.model.PaymentIntent;
import com.gogidix.rapidassist.payment.service.domain.model.PaymentStatus;
import com.gogidix.rapidassist.payment.service.domain.port.in.CreatePaymentIntentCommand;
import com.gogidix.rapidassist.payment.service.domain.port.out.PaymentIntentStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class CreatePaymentIntentUseCase implements CreatePaymentIntentCommand {

    private final PaymentIntentStore store;

    public CreatePaymentIntentUseCase(PaymentIntentStore store) {
        this.store = store;
    }

    @Override
    public PaymentIntent create(String tenantId, BigDecimal amount, String currency) {
        String intentId = UUID.randomUUID().toString();
        PaymentIntent intent = new PaymentIntent(tenantId, intentId, currency, amount, PaymentStatus.CREATED, Instant.now());
        return store.save(intent);
    }
}
