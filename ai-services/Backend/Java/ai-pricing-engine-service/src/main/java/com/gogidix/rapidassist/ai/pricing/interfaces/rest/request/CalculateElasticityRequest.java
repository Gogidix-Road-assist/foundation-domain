package com.gogidix.rapidassist.ai.pricing.interfaces.rest.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for calculating price elasticity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateElasticityRequest {

    @NotNull(message = "Product ID is required")
    private String productId;

    private String categoryId;

    @NotNull(message = "Original price is required")
    @Positive(message = "Original price must be positive")
    private BigDecimal originalPrice;

    @NotNull(message = "New price is required")
    @Positive(message = "New price must be positive")
    private BigDecimal newPrice;

    @NotNull(message = "Original demand is required")
    @PositiveOrZero(message = "Original demand must be positive or zero")
    private Integer originalDemand;

    @NotNull(message = "New demand is required")
    @PositiveOrZero(message = "New demand must be positive or zero")
    private Integer newDemand;

    @NotNull(message = "Period start is required")
    private LocalDateTime periodStart;

    @NotNull(message = "Period end is required")
    private LocalDateTime periodEnd;
}
