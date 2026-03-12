package com.gogidix.rapidassist.ai.pricing.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for PriceElasticity.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceElasticityDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String productId;
    private String categoryId;
    private BigDecimal elasticityCoefficient;
    private String elasticityType;
    private boolean isElastic;
    private boolean isInelastic;
    private boolean isUnitElastic;
    private BigDecimal originalPrice;
    private BigDecimal newPrice;
    private BigDecimal priceChangePercent;
    private Integer originalDemand;
    private Integer newDemand;
    private BigDecimal demandChangePercent;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private LocalDateTime createdAt;
}
