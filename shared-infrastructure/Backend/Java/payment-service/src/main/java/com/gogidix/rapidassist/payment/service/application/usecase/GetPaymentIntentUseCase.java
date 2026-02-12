package com.gogidix.rapidassist.payment.service.application.usecase;

import com.gogidix.rapidassist.payment.service.domain.model.PaymentIntent;
import com.gogidix.rapidassist.payment.service.domain.port.in.GetPaymentIntentQuery;
import com.gogidix.rapidassist.payment.service.domain.port.out.PaymentIntentStore;
import org.springframework.stereotype.Service;

@Service
public class GetPaymentIntentUseCase implements GetPaymentIntentQuery {

    private final PaymentIntentStore store;

    public GetPaymentIntentUseCase(PaymentIntentStore store) {
        this.store = store;
    }

    @Override
    public PaymentIntent get(String tenantId, String intentId) {
        return store.find(tenantId, intentId).orElse(null);
    }
}
