package com.gogidix.rapidassist.pricing.service.adapters.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record PriceBookResponse(
        String currency,
        Map<String, BigDecimal> prices,
        Instant updatedAt
) {
}
