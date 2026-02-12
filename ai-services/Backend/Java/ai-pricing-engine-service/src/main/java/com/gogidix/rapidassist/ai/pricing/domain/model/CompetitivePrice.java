package com.gogidix.rapidassist.ai.pricing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing competitive pricing data.
 * Pure domain model without JPA annotations.
 * Part of the PricingEngine aggregate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetitivePrice {

    private UUID id;
    private String tenantId;
    private String productId;
    private String competitorName;
    private String competitorUrl;
    private BigDecimal competitorPrice;
    private BigDecimal ourPrice;
    private BigDecimal priceDifference;
    private BigDecimal percentageDifference;
    private String competitorProductName;
    private boolean inStock;
    private LocalDateTime lastChecked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Check if we are cheaper
     */
    public boolean areWeCheaper() {
        if (ourPrice == null || competitorPrice == null) {
            return false;
        }
        return ourPrice.compareTo(competitorPrice) < 0;
    }

    /**
     * Business logic: Calculate price difference
     */
    public BigDecimal calculatePriceDifference() {
        if (ourPrice == null || competitorPrice == null) {
            return BigDecimal.ZERO;
        }
        return competitorPrice.subtract(ourPrice);
    }

    /**
     * Business logic: Calculate percentage difference
     */
    public BigDecimal calculatePercentageDifference() {
        if (ourPrice == null || competitorPrice == null || competitorPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return ourPrice.subtract(competitorPrice)
                .divide(competitorPrice, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Business logic: Update pricing data
     */
    public void updatePricing(BigDecimal newCompetitorPrice, BigDecimal newOurPrice) {
        this.competitorPrice = newCompetitorPrice;
        this.ourPrice = newOurPrice;
        this.priceDifference = calculatePriceDifference();
        this.percentageDifference = calculatePercentageDifference();
        this.lastChecked = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if data is stale (older than specified hours)
     */
    public boolean isDataStale(int staleHours) {
        if (lastChecked == null) {
            return true;
        }
        LocalDateTime threshold = LocalDateTime.now().minusHours(staleHours);
        return lastChecked.isBefore(threshold);
    }

    /**
     * Business logic: Get price position relative to competitor
     */
    public String getPricePosition() {
        if (ourPrice == null || competitorPrice == null) {
            return "UNKNOWN";
        }
        int comparison = ourPrice.compareTo(competitorPrice);
        if (comparison < 0) {
            return "LOWER";
        } else if (comparison > 0) {
            return "HIGHER";
        } else {
            return "EQUAL";
        }
    }
}
