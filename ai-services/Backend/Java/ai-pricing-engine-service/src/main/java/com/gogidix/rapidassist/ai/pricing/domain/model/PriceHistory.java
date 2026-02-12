package com.gogidix.rapidassist.ai.pricing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing price history.
 * Pure domain model without JPA annotations.
 * Part of the PricingEngine aggregate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistory {

    private UUID id;
    private String tenantId;
    private String productId;
    private String priceRuleId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private BigDecimal priceChange;
    private BigDecimal percentageChange;
    private String changeReason;
    private String changedBy;
    private String changeSource; // MANUAL, AUTOMATIC, SCHEDULED
    private LocalDateTime createdAt;

    /**
     * Business logic: Check if price increased
     */
    public boolean isPriceIncrease() {
        return priceChange != null && priceChange.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Business logic: Check if price decreased
     */
    public boolean isPriceDecrease() {
        return priceChange != null && priceChange.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Business logic: Calculate price change
     */
    public static BigDecimal calculateChange(BigDecimal oldPrice, BigDecimal newPrice) {
        if (oldPrice == null || newPrice == null) {
            return BigDecimal.ZERO;
        }
        return newPrice.subtract(oldPrice);
    }

    /**
     * Business logic: Calculate percentage change
     */
    public static BigDecimal calculatePercentageChange(BigDecimal oldPrice, BigDecimal newPrice) {
        if (oldPrice == null || newPrice == null || oldPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return newPrice.subtract(oldPrice)
                .divide(oldPrice, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Business logic: Create price history entry
     */
    public static PriceHistory create(String tenantId, String productId, String priceRuleId,
                                     BigDecimal oldPrice, BigDecimal newPrice,
                                     String changeReason, String changedBy, String changeSource) {
        BigDecimal priceChange = calculateChange(oldPrice, newPrice);
        BigDecimal percentageChange = calculatePercentageChange(oldPrice, newPrice);

        return PriceHistory.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .productId(productId)
                .priceRuleId(priceRuleId)
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .priceChange(priceChange)
                .percentageChange(percentageChange)
                .changeReason(changeReason)
                .changedBy(changedBy)
                .changeSource(changeSource)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
