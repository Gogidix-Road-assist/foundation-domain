package com.gogidix.rapidassist.payment.service.domain.port.in;

import com.gogidix.rapidassist.payment.service.domain.model.PaymentIntent;

public interface GetPaymentIntentQuery {

    PaymentIntent get(String tenantId, String intentId);
}
