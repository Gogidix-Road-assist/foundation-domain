package com.gogidix.rapidassist.currency.converter.service.domain.model;

import java.math.BigDecimal;

public record ConversionResult(
        String fromCurrency,
        String toCurrency,
        BigDecimal amount,
        BigDecimal convertedAmount,
        BigDecimal rate
) {
}
