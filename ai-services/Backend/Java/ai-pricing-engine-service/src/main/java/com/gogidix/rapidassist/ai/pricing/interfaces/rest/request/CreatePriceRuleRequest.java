package com.gogidix.rapidassist.ai.pricing.interfaces.rest.request;

import com.gogidix.rapidassist.ai.pricing.domain.model.PricingStrategyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Request DTO for creating a price rule.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePriceRuleRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Strategy type is required")
    private PricingStrategyType strategyType;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;

    @NotBlank(message = "Product ID is required")
    private String productId;

    private String categoryId;

    private Map<String, Object> parameters;

    private Integer priority;

    private LocalDateTime validFrom;

    private LocalDateTime validUntil;

    private String createdBy;
}
