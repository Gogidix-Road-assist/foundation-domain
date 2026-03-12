package com.gogidix.rapidassist.ai.pricing.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a price rule.
 * Pure domain model without JPA annotations.
 * Part of the PricingEngine aggregate.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceRule {

    @EqualsAndHashCode.Include


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

    /**
     * Business logic: Check if rule is active
     */
    public boolean isActive() {
        return PriceRuleStatus.ACTIVE.equals(this.status);
    }

    /**
     * Business logic: Check if rule is valid for current date
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }
        if (validUntil != null && now.isAfter(validUntil)) {
            return false;
        }
        return true;
    }

    /**
     * Business logic: Check if price is within bounds
     */
    public boolean isPriceWithinBounds(BigDecimal price) {
        if (minPrice != null && price.compareTo(minPrice) < 0) {
            return false;
        }
        if (maxPrice != null && price.compareTo(maxPrice) > 0) {
            return false;
        }
        return true;
    }

    /**
     * Business logic: Calculate price adjustment percentage
     */
    public BigDecimal calculateAdjustmentPercentage() {
        if (basePrice == null || currentPrice == null || basePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentPrice.subtract(basePrice)
                .divide(basePrice, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Business logic: Activate rule
     */
    public void activate() {
        if (this.status == PriceRuleStatus.DRAFT || this.status == PriceRuleStatus.PAUSED) {
            this.status = PriceRuleStatus.ACTIVE;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot activate rule in status: " + this.status);
        }
    }

    /**
     * Business logic: Pause rule
     */
    public void pause() {
        if (this.status == PriceRuleStatus.ACTIVE) {
            this.status = PriceRuleStatus.PAUSED;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot pause rule in status: " + this.status);
        }
    }

    /**
     * Business logic: Archive rule
     */
    public void archive() {
        if (this.status != PriceRuleStatus.ARCHIVED) {
            this.status = PriceRuleStatus.ARCHIVED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Business logic: Update current price
     */
    public void updatePrice(BigDecimal newPrice) {
        if (!isPriceWithinBounds(newPrice)) {
            throw new IllegalArgumentException("Price " + newPrice + " is outside allowed bounds");
        }
        this.currentPrice = newPrice;
        this.updatedAt = LocalDateTime.now();
    }
}
