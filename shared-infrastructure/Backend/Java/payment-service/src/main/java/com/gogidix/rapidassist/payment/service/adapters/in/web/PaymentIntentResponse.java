package com.gogidix.rapidassist.payment.service.adapters.in.web;

import com.gogidix.rapidassist.payment.service.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentIntentResponse(
        String intentId,
        String currency,
        BigDecimal amount,
        PaymentStatus status,
        Instant createdAt
) {
}
