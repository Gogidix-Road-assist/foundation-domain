package com.gogidix.rapidassist.ai.pricing.domain.aggregate;

import com.gogidix.rapidassist.ai.pricing.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Aggregate Root for Pricing Engine.
 * Manages the lifecycle and business logic of pricing operations.
 * Contains: PriceRule, Discount, PriceHistory, CompetitivePrice, and PriceElasticity as child entities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingEngine {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private PricingEngineStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Child entities (part of aggregate)
    @Builder.Default
    private List<PriceRule> priceRules = new ArrayList<>();

    @Builder.Default
    private List<Discount> discounts = new ArrayList<>();

    @Builder.Default
    private List<PriceHistory> priceHistory = new ArrayList<>();

    @Builder.Default
    private List<CompetitivePrice> competitivePrices = new ArrayList<>();

    @Builder.Default
    private List<PriceElasticity> elasticityMetrics = new ArrayList<>();

    /**
     * Business logic: Initialize a new pricing engine
     */
    public static PricingEngine initialize(String tenantId, String name, String description) {
        return PricingEngine.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .description(description)
                .status(PricingEngineStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .priceRules(new ArrayList<>())
                .discounts(new ArrayList<>())
                .priceHistory(new ArrayList<>())
                .competitivePrices(new ArrayList<>())
                .elasticityMetrics(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Add price rule
     */
    public void addPriceRule(PriceRule priceRule) {
        priceRule.setTenantId(this.tenantId);
        this.priceRules.add(priceRule);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add discount
     */
    public void addDiscount(Discount discount) {
        discount.setTenantId(this.tenantId);
        this.discounts.add(discount);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Calculate optimal price for a product
     */
    public BigDecimal calculateOptimalPrice(String productId, BigDecimal basePrice, Map<String, Object> context) {
        // Get active price rules for product
        List<PriceRule> activeRules = priceRules.stream()
                .filter(rule -> rule.isActive() && rule.isValid() && productId.equals(rule.getProductId()))
                .sorted(Comparator.comparingInt(PriceRule::getPriority).reversed())
                .toList();

        if (activeRules.isEmpty()) {
            return basePrice;
        }

        // Apply highest priority rule
        PriceRule primaryRule = activeRules.get(0);
        BigDecimal optimizedPrice = applyPricingStrategy(primaryRule, basePrice, context);

        // Check bounds
        if (primaryRule.getMinPrice() != null && optimizedPrice.compareTo(primaryRule.getMinPrice()) < 0) {
            optimizedPrice = primaryRule.getMinPrice();
        }
        if (primaryRule.getMaxPrice() != null && optimizedPrice.compareTo(primaryRule.getMaxPrice()) > 0) {
            optimizedPrice = primaryRule.getMaxPrice();
        }

        return optimizedPrice;
    }

    /**
     * Business logic: Apply pricing strategy
     */
    private BigDecimal applyPricingStrategy(PriceRule rule, BigDecimal basePrice, Map<String, Object> context) {
        return switch (rule.getStrategyType()) {
            case COST_PLUS -> applyCostPlusPricing(rule, basePrice, context);
            case VALUE_BASED -> applyValueBasedPricing(rule, basePrice, context);
            case COMPETITIVE_BASED -> applyCompetitiveBasedPricing(rule, basePrice, context);
            case DYNAMIC_PRICING -> applyDynamicPricing(rule, basePrice, context);
            case PENETRATION -> applyPenetrationPricing(rule, basePrice, context);
            case PSYCHOLOGICAL_PRICING -> applyPsychologicalPricing(rule, basePrice, context);
            default -> basePrice;
        };
    }

    private BigDecimal applyCostPlusPricing(PriceRule rule, BigDecimal basePrice, Map<String, Object> context) {
        BigDecimal marginPercent = rule.getParameters() != null ?
                (BigDecimal) rule.getParameters().getOrDefault("marginPercent", BigDecimal.valueOf(20)) :
                BigDecimal.valueOf(20);
        return basePrice.multiply(BigDecimal.ONE.add(marginPercent.divide(BigDecimal.valueOf(100))));
    }

    private BigDecimal applyValueBasedPricing(PriceRule rule, BigDecimal basePrice, Map<String, Object> context) {
        BigDecimal valueMultiplier = rule.getParameters() != null ?
                (BigDecimal) rule.getParameters().getOrDefault("valueMultiplier", BigDecimal.valueOf(1.5)) :
                BigDecimal.valueOf(1.5);
        return basePrice.multiply(valueMultiplier);
    }

    private BigDecimal applyCompetitiveBasedPricing(PriceRule rule, BigDecimal basePrice, Map<String, Object> context) {
        Optional<CompetitivePrice> competitorPrice = competitivePrices.stream()
                .filter(cp -> cp.getProductId().equals(rule.getProductId()))
                .filter(cp -> !cp.isDataStale(24))
                .findFirst();

        if (competitorPrice.isPresent()) {
            BigDecimal adjustment = rule.getParameters() != null ?
                    (BigDecimal) rule.getParameters().getOrDefault("competitiveAdjustment", BigDecimal.valueOf(-0.05)) :
                    BigDecimal.valueOf(-0.05);
            return competitorPrice.get().getCompetitorPrice().multiply(BigDecimal.ONE.add(adjustment));
        }
        return basePrice;
    }

    private BigDecimal applyDynamicPricing(PriceRule rule, BigDecimal basePrice, Map<String, Object> context) {
        // Get latest elasticity metric
        Optional<PriceElasticity> elasticity = elasticityMetrics.stream()
                .filter(e -> e.getProductId().equals(rule.getProductId()))
                .max(Comparator.comparing(PriceElasticity::getCreatedAt));

        if (elasticity.isPresent() && elasticity.get().isInelastic()) {
            // Demand is inelastic, can increase price
            return basePrice.multiply(BigDecimal.valueOf(1.05));
        } else if (elasticity.isPresent() && elasticity.get().isElastic()) {
            // Demand is elastic, consider decreasing price
            return basePrice.multiply(BigDecimal.valueOf(0.95));
        }
        return basePrice;
    }

    private BigDecimal applyPenetrationPricing(PriceRule rule, BigDecimal basePrice, Map<String, Object> context) {
        // Lower price to gain market share
        return basePrice.multiply(BigDecimal.valueOf(0.85));
    }

    private BigDecimal applyPsychologicalPricing(PriceRule rule, BigDecimal basePrice, Map<String, Object> context) {
        // Round to nearest .99
        BigDecimal rounded = basePrice.setScale(0, java.math.RoundingMode.UP);
        if (basePrice.compareTo(rounded) < 0) {
            return rounded.subtract(BigDecimal.valueOf(0.01));
        }
        return basePrice;
    }

    /**
     * Business logic: Apply best available discount
     */
    public BigDecimal applyBestDiscount(String productId, BigDecimal price, String customerId) {
        Optional<Discount> bestDiscount = discounts.stream()
                .filter(discount -> discount.isValid())
                .filter(discount -> discount.appliesToPurchase(price))
                .filter(discount -> productId.equals(discount.getProductId()) ||
                        discount.getProductId() == null)
                .filter(discount -> customerId == null ||
                        customerId.equals(discount.getCustomerId()) ||
                        discount.getCustomerId() == null)
                .max(Comparator.comparing(d -> d.calculateDiscountAmount(price)));

        if (bestDiscount.isPresent()) {
            Discount discount = bestDiscount.get();
            BigDecimal discountedPrice = discount.applyDiscount(price);
            discount.incrementUsage();
            return discountedPrice;
        }

        return price;
    }

    /**
     * Business logic: Record price change in history
     */
    public void recordPriceChange(String productId, String priceRuleId,
                                  BigDecimal oldPrice, BigDecimal newPrice,
                                  String reason, String changedBy, String source) {
        PriceHistory history = PriceHistory.create(
                tenantId, productId, priceRuleId,
                oldPrice, newPrice,
                reason, changedBy, source
        );
        this.priceHistory.add(history);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Get active price rules for product
     */
    public List<PriceRule> getActivePriceRules(String productId) {
        return priceRules.stream()
                .filter(rule -> rule.isActive())
                .filter(rule -> rule.isValid())
                .filter(rule -> productId.equals(rule.getProductId()))
                .sorted(Comparator.comparingInt(PriceRule::getPriority).reversed())
                .toList();
    }

    /**
     * Business logic: Get valid discounts for product
     */
    public List<Discount> getValidDiscounts(String productId) {
        LocalDateTime now = LocalDateTime.now();
        return discounts.stream()
                .filter(discount -> discount.isActive())
                .filter(discount -> discount.getValidFrom() == null || !now.isBefore(discount.getValidFrom()))
                .filter(discount -> discount.getValidUntil() == null || !now.isAfter(discount.getValidUntil()))
                .filter(discount -> !discount.isUsageLimitReached())
                .filter(discount -> productId.equals(discount.getProductId()) ||
                        discount.getProductId() == null)
                .toList();
    }

    /**
     * Business logic: Update competitive price data
     */
    public void updateCompetitivePrice(CompetitivePrice competitivePrice) {
        competitivePrice.setTenantId(this.tenantId);
        competitivePrices.removeIf(cp -> cp.getProductId().equals(competitivePrice.getProductId()) &&
                cp.getCompetitorName().equals(competitivePrice.getCompetitorName()));
        competitivePrices.add(competitivePrice);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add elasticity metric
     */
    public void addElasticityMetric(PriceElasticity elasticity) {
        elasticity.setTenantId(this.tenantId);
        this.elasticityMetrics.add(elasticity);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Calculate price adjustment percentage
     */
    public BigDecimal calculateAveragePriceAdjustment(String productId) {
        List<PriceHistory> productHistory = priceHistory.stream()
                .filter(ph -> productId.equals(ph.getProductId()))
                .toList();

        if (productHistory.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalChange = productHistory.stream()
                .map(PriceHistory::getPercentageChange)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalChange.divide(BigDecimal.valueOf(productHistory.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Business logic: Activate pricing engine
     */
    public void activate() {
        if (this.status == PricingEngineStatus.INACTIVE) {
            this.status = PricingEngineStatus.ACTIVE;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Business logic: Deactivate pricing engine
     */
    public void deactivate() {
        if (this.status == PricingEngineStatus.ACTIVE) {
            this.status = PricingEngineStatus.INACTIVE;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Enum for pricing engine status
     */
    public enum PricingEngineStatus {
        ACTIVE,
        INACTIVE
    }
}
