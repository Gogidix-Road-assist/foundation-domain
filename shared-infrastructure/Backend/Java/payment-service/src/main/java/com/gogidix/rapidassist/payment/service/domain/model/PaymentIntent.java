package com.gogidix.rapidassist.payment.service.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentIntent(
        String tenantId,
        String intentId,
        String currency,
        BigDecimal amount,
        PaymentStatus status,
        Instant createdAt
) {
}
