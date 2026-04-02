package com.gogidix.rapidassist.payment.service.infrastructure.persistence.memory;

import com.gogidix.rapidassist.payment.service.domain.model.PaymentIntent;
import com.gogidix.rapidassist.payment.service.domain.port.out.PaymentIntentStore;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPaymentIntentStore implements PaymentIntentStore {

    private final ConcurrentHashMap<String, PaymentIntent> byTenantAndIntentId = new ConcurrentHashMap<>();

    private String key(String tenantId, String intentId) {
        return tenantId + "::" + intentId;
    }

    @Override
    public Optional<PaymentIntent> find(String tenantId, String intentId) {
        return Optional.ofNullable(byTenantAndIntentId.get(key(tenantId, intentId)));
    }

    @Override
    public PaymentIntent save(PaymentIntent intent) {
        byTenantAndIntentId.put(key(intent.tenantId(), intent.intentId()), intent);
        return intent;
    }
}
