package com.gogidix.rapidassist.payment.service.adapters.in.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePaymentIntentRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String currency
) {
}
