package com.gogidix.rapidassist.ai.pricing.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing price elasticity metrics.
 * Pure domain model without JPA annotations.
 * Part of the PricingEngine aggregate.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceElasticity {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String productId;
    private String categoryId;
    private BigDecimal elasticityCoefficient;
    private String elasticityType; // ELASTIC, INELASTIC, UNIT_ELASTIC
    private BigDecimal originalPrice;
    private BigDecimal newPrice;
    private BigDecimal priceChangePercent;
    private Integer originalDemand;
    private Integer newDemand;
    private BigDecimal demandChangePercent;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private LocalDateTime createdAt;

    /**
     * Business logic: Calculate elasticity coefficient
     * Formula: % Change in Quantity Demanded / % Change in Price
     */
    public static BigDecimal calculateElasticityCoefficient(BigDecimal priceChangePercent, BigDecimal demandChangePercent) {
        if (priceChangePercent == null || demandChangePercent == null || priceChangePercent.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return demandChangePercent.divide(priceChangePercent, 4, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Business logic: Determine elasticity type
     */
    public static String determineElasticityType(BigDecimal coefficient) {
        if (coefficient == null) {
            return "UNKNOWN";
        }
        // Absolute value
        BigDecimal absCoefficient = coefficient.abs();
        if (absCoefficient.compareTo(BigDecimal.ONE) > 0) {
            return "ELASTIC";
        } else if (absCoefficient.compareTo(BigDecimal.ONE) < 0) {
            return "INELASTIC";
        } else {
            return "UNIT_ELASTIC";
        }
    }

    /**
     * Business logic: Check if demand is elastic (sensitive to price changes)
     */
    public boolean isElastic() {
        return "ELASTIC".equals(this.elasticityType);
    }

    /**
     * Business logic: Check if demand is inelastic (not sensitive to price changes)
     */
    public boolean isInelastic() {
        return "INELASTIC".equals(this.elasticityType);
    }

    /**
     * Business logic: Check if demand is unit elastic
     */
    public boolean isUnitElastic() {
        return "UNIT_ELASTIC".equals(this.elasticityType);
    }

    /**
     * Business logic: Predict demand change for a price change
     */
    public BigDecimal predictDemandChangePercent(BigDecimal priceChangePercent) {
        if (elasticityCoefficient == null || priceChangePercent == null) {
            return BigDecimal.ZERO;
        }
        return elasticityCoefficient.multiply(priceChangePercent);
    }

    /**
     * Business logic: Create elasticity analysis
     */
    public static PriceElasticity create(String tenantId, String productId, String categoryId,
                                        BigDecimal originalPrice, BigDecimal newPrice,
                                        Integer originalDemand, Integer newDemand,
                                        LocalDateTime periodStart, LocalDateTime periodEnd) {
        // Calculate percentage changes
        BigDecimal priceChangePercent = calculatePercentageChange(originalPrice, newPrice);
        BigDecimal demandChangePercent = calculatePercentageChange(
                BigDecimal.valueOf(originalDemand),
                BigDecimal.valueOf(newDemand)
        );

        // Calculate elasticity coefficient
        BigDecimal coefficient = calculateElasticityCoefficient(priceChangePercent, demandChangePercent);

        // Determine elasticity type
        String type = determineElasticityType(coefficient);

        return PriceElasticity.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .productId(productId)
                .categoryId(categoryId)
                .elasticityCoefficient(coefficient)
                .elasticityType(type)
                .originalPrice(originalPrice)
                .newPrice(newPrice)
                .priceChangePercent(priceChangePercent)
                .originalDemand(originalDemand)
                .newDemand(newDemand)
                .demandChangePercent(demandChangePercent)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static BigDecimal calculatePercentageChange(BigDecimal original, BigDecimal newValue) {
        if (original == null || newValue == null || original.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return newValue.subtract(original)
                .divide(original, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
