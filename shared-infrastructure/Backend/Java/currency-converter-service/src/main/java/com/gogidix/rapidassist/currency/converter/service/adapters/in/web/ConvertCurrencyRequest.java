package com.gogidix.rapidassist.currency.converter.service.adapters.in.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConvertCurrencyRequest(
        @NotNull @DecimalMin("0.00") BigDecimal amount,
        @NotBlank String fromCurrency,
        @NotBlank String toCurrency
) {
}
