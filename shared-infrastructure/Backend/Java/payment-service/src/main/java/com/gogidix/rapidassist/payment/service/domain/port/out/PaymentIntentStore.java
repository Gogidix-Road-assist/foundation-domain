package com.gogidix.rapidassist.payment.service.domain.port.out;

import com.gogidix.rapidassist.payment.service.domain.model.PaymentIntent;

import java.util.Optional;

public interface PaymentIntentStore {

    Optional<PaymentIntent> find(String tenantId, String intentId);

    PaymentIntent save(PaymentIntent intent);
}
