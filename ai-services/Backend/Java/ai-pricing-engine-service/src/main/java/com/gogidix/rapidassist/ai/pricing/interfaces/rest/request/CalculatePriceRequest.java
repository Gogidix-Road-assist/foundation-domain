package com.gogidix.rapidassist.ai.pricing.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for calculating price.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculatePriceRequest {

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;

    private String discountCode;

    private String customerId;
}
