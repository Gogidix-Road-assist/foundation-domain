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
 * Request DTO for adding competitive price.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCompetitivePriceRequest {

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Competitor name is required")
    private String competitorName;

    private String competitorUrl;

    @NotNull(message = "Competitor price is required")
    @Positive(message = "Competitor price must be positive")
    private BigDecimal competitorPrice;

    @NotNull(message = "Our price is required")
    @Positive(message = "Our price must be positive")
    private BigDecimal ourPrice;

    @Builder.Default
    private boolean inStock = true;

    private String createdBy;
}
