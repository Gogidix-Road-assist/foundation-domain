package com.gogidix.rapidassist.payment.service.domain.port.in;

import com.gogidix.rapidassist.payment.service.domain.model.PaymentIntent;

import java.math.BigDecimal;

public interface CreatePaymentIntentCommand {

    PaymentIntent create(String tenantId, BigDecimal amount, String currency);
}
