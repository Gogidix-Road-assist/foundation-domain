package com.gogidix.rapidassist.currency.converter.service.adapters.in.web;

import java.math.BigDecimal;

public record ConversionResponse(
        String fromCurrency,
        String toCurrency,
        BigDecimal amount,
        BigDecimal convertedAmount,
        BigDecimal rate
) {
}
