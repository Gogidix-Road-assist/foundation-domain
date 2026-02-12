package com.gogidix.rapidassist.ai.pricing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a discount.
 * Pure domain model without JPA annotations.
 * Part of the PricingEngine aggregate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

    private UUID id;
    private String tenantId;
    private String code;
    private String name;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minPurchaseAmount;
    private Integer usageLimit;
    private Integer usageCount;
    private String productId;
    private String categoryId;
    private String customerId;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Business logic: Check if discount is active and valid
     */
    public boolean isValid() {
        if (!active) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }
        if (validUntil != null && now.isAfter(validUntil)) {
            return false;
        }

        if (usageLimit != null && usageCount >= usageLimit) {
            return false;
        }

        return true;
    }

    /**
     * Business logic: Check if discount applies to purchase amount
     */
    public boolean appliesToPurchase(BigDecimal purchaseAmount) {
        if (minPurchaseAmount == null) {
            return true;
        }
        return purchaseAmount.compareTo(minPurchaseAmount) >= 0;
    }

    /**
     * Business logic: Calculate discount amount for a given price
     */
    public BigDecimal calculateDiscountAmount(BigDecimal originalPrice) {
        if (discountValue == null || originalPrice == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount;
        switch (discountType) {
            case PERCENTAGE:
                discountAmount = originalPrice.multiply(discountValue)
                        .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                break;
            case FIXED_AMOUNT:
                discountAmount = discountValue;
                break;
            default:
                discountAmount = BigDecimal.ZERO;
        }

        // Apply max discount limit if set
        if (maxDiscountAmount != null && discountAmount.compareTo(maxDiscountAmount) > 0) {
            discountAmount = maxDiscountAmount;
        }

        // Ensure discount doesn't exceed original price
        if (discountAmount.compareTo(originalPrice) > 0) {
            discountAmount = originalPrice;
        }

        return discountAmount;
    }

    /**
     * Business logic: Apply discount to price
     */
    public BigDecimal applyDiscount(BigDecimal originalPrice) {
        BigDecimal discountAmount = calculateDiscountAmount(originalPrice);
        return originalPrice.subtract(discountAmount);
    }

    /**
     * Business logic: Increment usage count
     */
    public void incrementUsage() {
        if (usageLimit != null && usageCount >= usageLimit) {
            throw new IllegalStateException("Discount usage limit reached");
        }
        this.usageCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Activate discount
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Deactivate discount
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if usage limit reached
     */
    public boolean isUsageLimitReached() {
        return usageLimit != null && usageCount >= usageLimit;
    }

    /**
     * Business logic: Get remaining usage count
     */
    public Integer getRemainingUsage() {
        if (usageLimit == null) {
            return null; // Unlimited
        }
        return Math.max(0, usageLimit - usageCount);
    }
}
