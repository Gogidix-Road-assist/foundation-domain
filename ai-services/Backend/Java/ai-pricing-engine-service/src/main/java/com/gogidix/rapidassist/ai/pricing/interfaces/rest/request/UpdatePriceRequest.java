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
 * Request DTO for updating price.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePriceRequest {

    @NotNull(message = "New price is required")
    @Positive(message = "New price must be positive")
    private BigDecimal newPrice;

    @NotBlank(message = "Reason is required")
    private String reason;

    @NotBlank(message = "Updated by is required")
    private String updatedBy;
}
