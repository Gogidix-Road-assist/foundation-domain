package com.gogidix.rapidassist.pricing.service.adapters.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;

public record UpsertPriceBookRequest(
        @NotBlank String currency,
        @NotNull Map<String, BigDecimal> prices
) {
}
