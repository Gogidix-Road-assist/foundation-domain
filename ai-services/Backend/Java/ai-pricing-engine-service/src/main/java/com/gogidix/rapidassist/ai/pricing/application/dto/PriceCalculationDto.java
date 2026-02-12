package com.gogidix.rapidassist.ai.pricing.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO for price calculation result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationDto {

    private String productId;
    private BigDecimal originalPrice;
    private BigDecimal calculatedPrice;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
    private UUID appliedPriceRuleId;
    private String appliedPriceRuleName;
    private UUID appliedDiscountId;
    private String appliedDiscountCode;
    private List<String> appliedFactors;
    private String pricingStrategy;
}
