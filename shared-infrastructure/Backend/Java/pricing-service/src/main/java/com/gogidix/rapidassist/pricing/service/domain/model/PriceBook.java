package com.gogidix.rapidassist.pricing.service.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record PriceBook(
        String tenantId,
        String currency,
        Map<String, BigDecimal> prices,
        Instant updatedAt
) {
}
