package com.gogidix.rapidassist.ai.pricing.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PriceRule domain model
 * Tests business logic, validation, and state transitions
 */
@DisplayName("PriceRule Domain Model Tests")
class PriceRuleTest {

    @Test
    @DisplayName("Should create price rule with all fields using builder")
    void shouldCreatePriceRuleWithBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        String name = "Summer Sale Rule";
        String description = "Summer discount pricing";
        PricingStrategyType strategyType = PricingStrategyType.DYNAMIC_PRICING;
        PriceRuleStatus status = PriceRuleStatus.DRAFT;
        BigDecimal basePrice = new BigDecimal("100.00");
        BigDecimal minPrice = new BigDecimal("80.00");
        BigDecimal maxPrice = new BigDecimal("120.00");
        BigDecimal currentPrice = new BigDecimal("100.00");
        String productId = "product-123";
        String categoryId = "category-456";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("maxDiscount", 20);
        Integer priority = 5;
        LocalDateTime validFrom = LocalDateTime.now();
        LocalDateTime validUntil = LocalDateTime.now().plusMonths(3);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String createdBy = "user-123";
        String updatedBy = "user-123";

        // When
        PriceRule priceRule = PriceRule.builder()
                .id(id)
                .tenantId(tenantId)
                .name(name)
                .description(description)
                .strategyType(strategyType)
                .status(status)
                .basePrice(basePrice)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .currentPrice(currentPrice)
                .productId(productId)
                .categoryId(categoryId)
                .parameters(parameters)
                .priority(priority)
                .validFrom(validFrom)
                .validUntil(validUntil)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .createdBy(createdBy)
                .updatedBy(updatedBy)
                .build();

        // Then
        assertNotNull(priceRule);
        assertEquals(id, priceRule.getId());
        assertEquals(tenantId, priceRule.getTenantId());
        assertEquals(name, priceRule.getName());
        assertEquals(description, priceRule.getDescription());
        assertEquals(strategyType, priceRule.getStrategyType());
        assertEquals(status, priceRule.getStatus());
        assertEquals(basePrice, priceRule.getBasePrice());
        assertEquals(minPrice, priceRule.getMinPrice());
        assertEquals(maxPrice, priceRule.getMaxPrice());
        assertEquals(currentPrice, priceRule.getCurrentPrice());
        assertEquals(productId, priceRule.getProductId());
        assertEquals(categoryId, priceRule.getCategoryId());
        assertEquals(parameters, priceRule.getParameters());
        assertEquals(priority, priceRule.getPriority());
        assertEquals(validFrom, priceRule.getValidFrom());
        assertEquals(validUntil, priceRule.getValidUntil());
        assertEquals(createdAt, priceRule.getCreatedAt());
        assertEquals(updatedAt, priceRule.getUpdatedAt());
        assertEquals(createdBy, priceRule.getCreatedBy());
        assertEquals(updatedBy, priceRule.getUpdatedBy());
    }

    @Test
    @DisplayName("Should return true when rule is ACTIVE")
    void shouldReturnTrueWhenActive() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .status(PriceRuleStatus.ACTIVE)
                .build();

        // When
        boolean isActive = priceRule.isActive();

        // Then
        assertTrue(isActive);
    }

    @Test
    @DisplayName("Should return false when rule is not ACTIVE")
    void shouldReturnFalseWhenNotActive() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .status(PriceRuleStatus.DRAFT)
                .build();

        // When
        boolean isActive = priceRule.isActive();

        // Then
        assertFalse(isActive);
    }

    @Test
    @DisplayName("Should return true when rule is within validity period")
    void shouldReturnTrueWhenValid() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        PriceRule priceRule = PriceRule.builder()
                .validFrom(now.minusDays(1))
                .validUntil(now.plusDays(1))
                .build();

        // When
        boolean isValid = priceRule.isValid();

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return false when rule is before validity period")
    void shouldReturnFalseWhenBeforeValidityPeriod() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        PriceRule priceRule = PriceRule.builder()
                .validFrom(now.plusDays(1))
                .validUntil(now.plusDays(2))
                .build();

        // When
        boolean isValid = priceRule.isValid();

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false when rule is after validity period")
    void shouldReturnFalseWhenAfterValidityPeriod() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        PriceRule priceRule = PriceRule.builder()
                .validFrom(now.minusDays(2))
                .validUntil(now.minusDays(1))
                .build();

        // When
        boolean isValid = priceRule.isValid();

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return true when no validity dates are set")
    void shouldReturnTrueWhenNoValidityDates() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .validFrom(null)
                .validUntil(null)
                .build();

        // When
        boolean isValid = priceRule.isValid();

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return true when price is within bounds")
    void shouldReturnTrueWhenPriceWithinBounds() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .minPrice(new BigDecimal("80.00"))
                .maxPrice(new BigDecimal("120.00"))
                .build();

        // When
        boolean withinBounds = priceRule.isPriceWithinBounds(new BigDecimal("100.00"));

        // Then
        assertTrue(withinBounds);
    }

    @Test
    @DisplayName("Should return false when price is below minimum")
    void shouldReturnFalseWhenPriceBelowMinimum() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .minPrice(new BigDecimal("80.00"))
                .maxPrice(new BigDecimal("120.00"))
                .build();

        // When
        boolean withinBounds = priceRule.isPriceWithinBounds(new BigDecimal("70.00"));

        // Then
        assertFalse(withinBounds);
    }

    @Test
    @DisplayName("Should return false when price is above maximum")
    void shouldReturnFalseWhenPriceAboveMaximum() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .minPrice(new BigDecimal("80.00"))
                .maxPrice(new BigDecimal("120.00"))
                .build();

        // When
        boolean withinBounds = priceRule.isPriceWithinBounds(new BigDecimal("130.00"));

        // Then
        assertFalse(withinBounds);
    }

    @Test
    @DisplayName("Should return true when no bounds are set")
    void shouldReturnTrueWhenNoBoundsSet() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .minPrice(null)
                .maxPrice(null)
                .build();

        // When
        boolean withinBounds = priceRule.isPriceWithinBounds(new BigDecimal("100.00"));

        // Then
        assertTrue(withinBounds);
    }

    @Test
    @DisplayName("Should calculate adjustment percentage correctly")
    void shouldCalculateAdjustmentPercentage() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .basePrice(new BigDecimal("100.00"))
                .currentPrice(new BigDecimal("110.00"))
                .build();

        // When
        BigDecimal adjustment = priceRule.calculateAdjustmentPercentage();

        // Then
        assertEquals(new BigDecimal("10.0000"), adjustment);
    }

    @Test
    @DisplayName("Should return zero adjustment when base price is zero")
    void shouldReturnZeroAdjustmentWhenBasePriceZero() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .basePrice(BigDecimal.ZERO)
                .currentPrice(new BigDecimal("110.00"))
                .build();

        // When
        BigDecimal adjustment = priceRule.calculateAdjustmentPercentage();

        // Then
        assertEquals(BigDecimal.ZERO, adjustment);
    }

    @Test
    @DisplayName("Should return zero adjustment when prices are null")
    void shouldReturnZeroAdjustmentWhenPricesNull() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .basePrice(null)
                .currentPrice(null)
                .build();

        // When
        BigDecimal adjustment = priceRule.calculateAdjustmentPercentage();

        // Then
        assertEquals(BigDecimal.ZERO, adjustment);
    }

    @Test
    @DisplayName("Should activate rule when in DRAFT status")
    void shouldActivateRuleWhenDraft() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .status(PriceRuleStatus.DRAFT)
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        // When
        priceRule.activate();

        // Then
        assertEquals(PriceRuleStatus.ACTIVE, priceRule.getStatus());
        assertTrue(priceRule.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @DisplayName("Should activate rule when in PAUSED status")
    void shouldActivateRuleWhenPaused() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .status(PriceRuleStatus.PAUSED)
                .build();

        // When
        priceRule.activate();

        // Then
        assertEquals(PriceRuleStatus.ACTIVE, priceRule.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when activating ACTIVE rule")
    void shouldThrowExceptionWhenActivatingActiveRule() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .status(PriceRuleStatus.ACTIVE)
                .build();

        // When & Then
        assertThrows(IllegalStateException.class, priceRule::activate);
    }

    @Test
    @DisplayName("Should throw exception when activating ARCHIVED rule")
    void shouldThrowExceptionWhenActivatingArchivedRule() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .status(PriceRuleStatus.ARCHIVED)
                .build();

        // When & Then
        assertThrows(IllegalStateException.class, priceRule::activate);
    }

    @Test
    @DisplayName("Should pause rule when ACTIVE")
    void shouldPauseRuleWhenActive() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .status(PriceRuleStatus.ACTIVE)
                .build();

        // When
        priceRule.pause();

        // Then
        assertEquals(PriceRuleStatus.PAUSED, priceRule.getStatus());
        assertTrue(priceRule.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @DisplayName("Should throw exception when pausing DRAFT rule")
    void shouldThrowExceptionWhenPausingDraftRule() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .status(PriceRuleStatus.DRAFT)
                .build();

        // When & Then
        assertThrows(IllegalStateException.class, priceRule::pause);
    }

    @Test
    @DisplayName("Should archive rule from any status")
    void shouldArchiveRule() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .status(PriceRuleStatus.ACTIVE)
                .build();

        // When
        priceRule.archive();

        // Then
        assertEquals(PriceRuleStatus.ARCHIVED, priceRule.getStatus());
        assertTrue(priceRule.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @DisplayName("Should update price when within bounds")
    void shouldUpdatePriceWhenWithinBounds() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .minPrice(new BigDecimal("80.00"))
                .maxPrice(new BigDecimal("120.00"))
                .currentPrice(new BigDecimal("100.00"))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        // When
        priceRule.updatePrice(new BigDecimal("110.00"));

        // Then
        assertEquals(new BigDecimal("110.00"), priceRule.getCurrentPrice());
        assertTrue(priceRule.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    @DisplayName("Should throw exception when updating price outside bounds")
    void shouldThrowExceptionWhenUpdatingPriceOutsideBounds() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .minPrice(new BigDecimal("80.00"))
                .maxPrice(new BigDecimal("120.00"))
                .currentPrice(new BigDecimal("100.00"))
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> priceRule.updatePrice(new BigDecimal("130.00")));
        assertEquals(new BigDecimal("100.00"), priceRule.getCurrentPrice());
    }

    @Test
    @DisplayName("Should create price rule with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        PriceRule priceRule = new PriceRule();

        // Then
        assertNotNull(priceRule);
    }

    @Test
    @DisplayName("Should create price rule with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        String name = "Test Rule";
        LocalDateTime now = LocalDateTime.now();

        // When
        PriceRule priceRule = new PriceRule(
                id, tenantId, name, "description",
                PricingStrategyType.DYNAMIC_PRICING, PriceRuleStatus.DRAFT,
                new BigDecimal("100.00"), new BigDecimal("80.00"),
                new BigDecimal("120.00"), new BigDecimal("100.00"),
                "product-123", "category-456", null, 5,
                now, now.plusMonths(3), now, now, "user-1", "user-1"
        );

        // Then
        assertEquals(id, priceRule.getId());
        assertEquals(tenantId, priceRule.getTenantId());
        assertEquals(name, priceRule.getName());
    }

    @Test
    @DisplayName("Should handle null min price in bounds check")
    void shouldHandleNullMinPriceInBoundsCheck() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .minPrice(null)
                .maxPrice(new BigDecimal("120.00"))
                .build();

        // When
        boolean withinBounds = priceRule.isPriceWithinBounds(new BigDecimal("100.00"));

        // Then
        assertTrue(withinBounds);
    }

    @Test
    @DisplayName("Should handle null max price in bounds check")
    void shouldHandleNullMaxPriceInBoundsCheck() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .minPrice(new BigDecimal("80.00"))
                .maxPrice(null)
                .build();

        // When
        boolean withinBounds = priceRule.isPriceWithinBounds(new BigDecimal("100.00"));

        // Then
        assertTrue(withinBounds);
    }

    @Test
    @DisplayName("Should calculate negative adjustment percentage")
    void shouldCalculateNegativeAdjustmentPercentage() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .basePrice(new BigDecimal("100.00"))
                .currentPrice(new BigDecimal("90.00"))
                .build();

        // When
        BigDecimal adjustment = priceRule.calculateAdjustmentPercentage();

        // Then
        assertEquals(new BigDecimal("-10.0000"), adjustment);
    }
}
