package com.gogidix.rapidassist.ai.pricing.application.dto;

import com.gogidix.rapidassist.ai.pricing.domain.model.PriceRuleStatus;
import com.gogidix.rapidassist.ai.pricing.domain.model.PricingStrategyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for PriceRule.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceRuleDto {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private PricingStrategyType strategyType;
    private PriceRuleStatus status;
    private BigDecimal basePrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal currentPrice;
    private String productId;
    private String categoryId;
    private Map<String, Object> parameters;
    private Integer priority;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private BigDecimal adjustmentPercentage;
}
