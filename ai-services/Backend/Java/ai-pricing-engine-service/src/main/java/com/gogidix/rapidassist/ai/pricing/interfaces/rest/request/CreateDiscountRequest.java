package com.gogidix.rapidassist.ai.pricing.interfaces.rest.request;

import com.gogidix.rapidassist.ai.pricing.domain.model.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for creating a discount.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDiscountRequest {

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount value must be positive")
    private BigDecimal discountValue;

    private BigDecimal maxDiscountAmount;

    private BigDecimal minPurchaseAmount;

    private Integer usageLimit;

    private String productId;

    private String categoryId;

    private String customerId;

    private LocalDateTime validFrom;

    private LocalDateTime validUntil;

    private String createdBy;
}
