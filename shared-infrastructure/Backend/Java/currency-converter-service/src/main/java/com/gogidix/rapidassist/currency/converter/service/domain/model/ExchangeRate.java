package com.gogidix.rapidassist.currency.converter.service.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record ExchangeRate(
        String fromCurrency,
        String toCurrency,
        BigDecimal rate,
        Instant updatedAt
) {
}
