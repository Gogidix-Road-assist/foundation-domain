package com.gogidix.rapidassist.ai.pricing.application.service;

import com.gogidix.rapidassist.ai.pricing.application.dto.*;
import com.gogidix.rapidassist.ai.pricing.domain.aggregate.PricingEngine;
import com.gogidix.rapidassist.ai.pricing.domain.model.*;
import com.gogidix.rapidassist.ai.pricing.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Application Service for Pricing Engine operations.
 * Implements business logic and orchestrates domain operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PricingEngineApplicationService {

    private final PriceRuleRepositoryPort priceRuleRepository;
    private final DiscountRepositoryPort discountRepository;
    private final PriceHistoryRepositoryPort priceHistoryRepository;
    private final CompetitivePriceRepositoryPort competitivePriceRepository;
    private final PriceElasticityRepositoryPort priceElasticityRepository;

    // ==================== Price Rule Operations ====================

    public PriceRuleDto createPriceRule(String tenantId, String name, String description,
                                       PricingStrategyType strategyType, BigDecimal basePrice,
                                       String productId, String categoryId,
                                       Map<String, Object> parameters, Integer priority,
                                       LocalDateTime validFrom, LocalDateTime validUntil,
                                       String createdBy) {
        log.info("Creating price rule: {} for tenant: {}, product: {}", name, tenantId, productId);

        PriceRule priceRule = PriceRule.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .description(description)
                .strategyType(strategyType)
                .status(PriceRuleStatus.DRAFT)
                .basePrice(basePrice)
                .currentPrice(basePrice)
                .productId(productId)
                .categoryId(categoryId)
                .parameters(parameters)
                .priority(priority != null ? priority : 0)
                .validFrom(validFrom)
                .validUntil(validUntil)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();

        PriceRule savedRule = priceRuleRepository.save(priceRule);
        return toPriceRuleDto(savedRule);
    }

    public PriceRuleDto getPriceRule(UUID ruleId, String tenantId) {
        log.info("Getting price rule: {} for tenant: {}", ruleId, tenantId);

        PriceRule priceRule = priceRuleRepository.findByIdAndTenantId(ruleId, tenantId)
                .orElseThrow(() -> new RuntimeException("Price rule not found: " + ruleId));

        return toPriceRuleDto(priceRule);
    }

    public List<PriceRuleDto> getPriceRulesByProduct(String tenantId, String productId) {
        log.info("Getting price rules for tenant: {}, product: {}", tenantId, productId);

        return priceRuleRepository.findByProductIdAndTenantId(productId, tenantId).stream()
                .map(this::toPriceRuleDto)
                .toList();
    }

    public PriceRuleDto activatePriceRule(UUID ruleId, String tenantId, String updatedBy) {
        log.info("Activating price rule: {} for tenant: {}", ruleId, tenantId);

        PriceRule priceRule = priceRuleRepository.findByIdAndTenantId(ruleId, tenantId)
                .orElseThrow(() -> new RuntimeException("Price rule not found: " + ruleId));

        priceRule.activate();
        priceRule.setUpdatedBy(updatedBy);
        PriceRule savedRule = priceRuleRepository.save(priceRule);

        return toPriceRuleDto(savedRule);
    }

    public PriceRuleDto updatePrice(UUID ruleId, String tenantId, BigDecimal newPrice, String reason, String updatedBy) {
        log.info("Updating price for rule: {} to: {} for tenant: {}", ruleId, newPrice, tenantId);

        PriceRule priceRule = priceRuleRepository.findByIdAndTenantId(ruleId, tenantId)
                .orElseThrow(() -> new RuntimeException("Price rule not found: " + ruleId));

        BigDecimal oldPrice = priceRule.getCurrentPrice();
        priceRule.updatePrice(newPrice);
        priceRule.setUpdatedBy(updatedBy);

        PriceRule savedRule = priceRuleRepository.save(priceRule);

        // Record in history
        PriceHistory history = PriceHistory.create(
                tenantId, priceRule.getProductId(), ruleId.toString(),
                oldPrice, newPrice, reason, updatedBy, "MANUAL"
        );
        priceHistoryRepository.save(history);

        return toPriceRuleDto(savedRule);
    }

    // ==================== Price Calculation ====================

    public PriceCalculationDto calculatePrice(String tenantId, String productId, BigDecimal basePrice,
                                            String discountCode, String customerId) {
        log.info("Calculating price for tenant: {}, product: {}, base price: {}", tenantId, productId, basePrice);

        // Get active price rules
        List<PriceRule> activeRules = priceRuleRepository.findActiveRulesByProductAndTenantId(productId, tenantId);

        BigDecimal calculatedPrice = basePrice;
        UUID appliedRuleId = null;
        String appliedRuleName = null;

        if (!activeRules.isEmpty()) {
            PriceRule highestPriorityRule = activeRules.stream()
                    .max((r1, r2) -> r1.getPriority().compareTo(r2.getPriority()))
                    .orElse(null);

            if (highestPriorityRule != null) {
                PricingEngine engine = PricingEngine.initialize(tenantId, "Engine", "Temp");
                calculatedPrice = engine.calculateOptimalPrice(productId, basePrice, highestPriorityRule.getParameters());
                appliedRuleId = highestPriorityRule.getId();
                appliedRuleName = highestPriorityRule.getName();
            }
        }

        // Apply discount if provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        UUID appliedDiscountId = null;
        String appliedDiscountCode = null;

        if (discountCode != null && !discountCode.isEmpty()) {
            Discount discount = discountRepository.findByCodeAndTenantId(discountCode, tenantId)
                    .orElse(null);

            if (discount != null && discount.isValid() && discount.appliesToPurchase(calculatedPrice)) {
                discountAmount = discount.calculateDiscountAmount(calculatedPrice);
                calculatedPrice = calculatedPrice.subtract(discountAmount);
                appliedDiscountId = discount.getId();
                appliedDiscountCode = discount.getCode();

                // Increment usage
                discount.incrementUsage();
                discountRepository.save(discount);
            }
        }

        return PriceCalculationDto.builder()
                .productId(productId)
                .originalPrice(basePrice)
                .calculatedPrice(calculatedPrice.add(discountAmount))
                .discountAmount(discountAmount)
                .finalPrice(calculatedPrice)
                .appliedPriceRuleId(appliedRuleId)
                .appliedPriceRuleName(appliedRuleName)
                .appliedDiscountId(appliedDiscountId)
                .appliedDiscountCode(appliedDiscountCode)
                .pricingStrategy(appliedRuleName)
                .build();
    }

    // ==================== Discount Operations ====================

    public DiscountDto createDiscount(String tenantId, String code, String name, String description,
                                     DiscountType discountType, BigDecimal discountValue,
                                     BigDecimal maxDiscountAmount, BigDecimal minPurchaseAmount,
                                     Integer usageLimit, String productId, String categoryId, String customerId,
                                     LocalDateTime validFrom, LocalDateTime validUntil, String createdBy) {
        log.info("Creating discount: {} for tenant: {}", code, tenantId);

        Discount discount = Discount.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .code(code)
                .name(name)
                .description(description)
                .discountType(discountType)
                .discountValue(discountValue)
                .maxDiscountAmount(maxDiscountAmount)
                .minPurchaseAmount(minPurchaseAmount)
                .usageLimit(usageLimit)
                .usageCount(0)
                .productId(productId)
                .categoryId(categoryId)
                .customerId(customerId)
                .validFrom(validFrom)
                .validUntil(validUntil)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();

        Discount savedDiscount = discountRepository.save(discount);
        return toDiscountDto(savedDiscount);
    }

    public DiscountDto getDiscountByCode(String code, String tenantId) {
        log.info("Getting discount by code: {} for tenant: {}", code, tenantId);

        Discount discount = discountRepository.findByCodeAndTenantId(code, tenantId)
                .orElseThrow(() -> new RuntimeException("Discount not found: " + code));

        return toDiscountDto(discount);
    }

    public List<DiscountDto> getActiveDiscounts(String tenantId) {
        log.info("Getting active discounts for tenant: {}", tenantId);

        return discountRepository.findActiveDiscountsByTenantId(tenantId).stream()
                .map(this::toDiscountDto)
                .toList();
    }

    // ==================== Price History ====================

    public List<PriceHistoryDto> getPriceHistory(String tenantId, String productId) {
        log.info("Getting price history for tenant: {}, product: {}", tenantId, productId);

        return priceHistoryRepository.findByProductIdAndTenantId(productId, tenantId).stream()
                .map(this::toPriceHistoryDto)
                .toList();
    }

    // ==================== Competitive Pricing ====================

    public CompetitivePriceDto addCompetitivePrice(String tenantId, String productId, String competitorName,
                                                   String competitorUrl, BigDecimal competitorPrice,
                                                   BigDecimal ourPrice, boolean inStock, String createdBy) {
        log.info("Adding competitive price for tenant: {}, product: {}, competitor: {}",
                tenantId, productId, competitorName);

        CompetitivePrice competitivePrice = CompetitivePrice.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .productId(productId)
                .competitorName(competitorName)
                .competitorUrl(competitorUrl)
                .competitorPrice(competitorPrice)
                .ourPrice(ourPrice)
                .lastChecked(LocalDateTime.now())
                .inStock(inStock)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        competitivePrice.updatePricing(competitorPrice, ourPrice);
        CompetitivePrice saved = competitivePriceRepository.save(competitivePrice);

        return toCompetitivePriceDto(saved);
    }

    public List<CompetitivePriceDto> getCompetitivePrices(String tenantId, String productId) {
        log.info("Getting competitive prices for tenant: {}, product: {}", tenantId, productId);

        return competitivePriceRepository.findByProductIdAndTenantId(productId, tenantId).stream()
                .map(this::toCompetitivePriceDto)
                .toList();
    }

    // ==================== Price Elasticity ====================

    public PriceElasticityDto calculateElasticity(String tenantId, String productId, String categoryId,
                                                  BigDecimal originalPrice, BigDecimal newPrice,
                                                  Integer originalDemand, Integer newDemand,
                                                  LocalDateTime periodStart, LocalDateTime periodEnd) {
        log.info("Calculating price elasticity for tenant: {}, product: {}", tenantId, productId);

        PriceElasticity elasticity = PriceElasticity.create(
                tenantId, productId, categoryId,
                originalPrice, newPrice,
                originalDemand, newDemand,
                periodStart, periodEnd
        );

        PriceElasticity saved = priceElasticityRepository.save(elasticity);
        return toPriceElasticityDto(saved);
    }

    // ==================== Mappers ====================

    private PriceRuleDto toPriceRuleDto(PriceRule priceRule) {
        return PriceRuleDto.builder()
                .id(priceRule.getId())
                .tenantId(priceRule.getTenantId())
                .name(priceRule.getName())
                .description(priceRule.getDescription())
                .strategyType(priceRule.getStrategyType())
                .status(priceRule.getStatus())
                .basePrice(priceRule.getBasePrice())
                .minPrice(priceRule.getMinPrice())
                .maxPrice(priceRule.getMaxPrice())
                .currentPrice(priceRule.getCurrentPrice())
                .productId(priceRule.getProductId())
                .categoryId(priceRule.getCategoryId())
                .parameters(priceRule.getParameters())
                .priority(priceRule.getPriority())
                .validFrom(priceRule.getValidFrom())
                .validUntil(priceRule.getValidUntil())
                .createdAt(priceRule.getCreatedAt())
                .updatedAt(priceRule.getUpdatedAt())
                .createdBy(priceRule.getCreatedBy())
                .updatedBy(priceRule.getUpdatedBy())
                .adjustmentPercentage(priceRule.calculateAdjustmentPercentage())
                .build();
    }

    private DiscountDto toDiscountDto(Discount discount) {
        return DiscountDto.builder()
                .id(discount.getId())
                .tenantId(discount.getTenantId())
                .code(discount.getCode())
                .name(discount.getName())
                .description(discount.getDescription())
                .discountType(discount.getDiscountType())
                .discountValue(discount.getDiscountValue())
                .maxDiscountAmount(discount.getMaxDiscountAmount())
                .minPurchaseAmount(discount.getMinPurchaseAmount())
                .usageLimit(discount.getUsageLimit())
                .usageCount(discount.getUsageCount())
                .remainingUsage(discount.getRemainingUsage())
                .productId(discount.getProductId())
                .categoryId(discount.getCategoryId())
                .customerId(discount.getCustomerId())
                .validFrom(discount.getValidFrom())
                .validUntil(discount.getValidUntil())
                .active(discount.isActive())
                .valid(discount.isValid())
                .createdAt(discount.getCreatedAt())
                .updatedAt(discount.getUpdatedAt())
                .createdBy(discount.getCreatedBy())
                .updatedBy(discount.getUpdatedBy())
                .build();
    }

    private PriceHistoryDto toPriceHistoryDto(PriceHistory priceHistory) {
        return PriceHistoryDto.builder()
                .id(priceHistory.getId())
                .tenantId(priceHistory.getTenantId())
                .productId(priceHistory.getProductId())
                .priceRuleId(priceHistory.getPriceRuleId())
                .oldPrice(priceHistory.getOldPrice())
                .newPrice(priceHistory.getNewPrice())
                .priceChange(priceHistory.getPriceChange())
                .percentageChange(priceHistory.getPercentageChange())
                .changeReason(priceHistory.getChangeReason())
                .changedBy(priceHistory.getChangedBy())
                .changeSource(priceHistory.getChangeSource())
                .createdAt(priceHistory.getCreatedAt())
                .build();
    }

    private CompetitivePriceDto toCompetitivePriceDto(CompetitivePrice competitivePrice) {
        return CompetitivePriceDto.builder()
                .id(competitivePrice.getId())
                .tenantId(competitivePrice.getTenantId())
                .productId(competitivePrice.getProductId())
                .competitorName(competitivePrice.getCompetitorName())
                .competitorUrl(competitivePrice.getCompetitorUrl())
                .competitorPrice(competitivePrice.getCompetitorPrice())
                .ourPrice(competitivePrice.getOurPrice())
                .priceDifference(competitivePrice.getPriceDifference())
                .percentageDifference(competitivePrice.getPercentageDifference())
                .pricePosition(competitivePrice.getPricePosition())
                .areWeCheaper(competitivePrice.areWeCheaper())
                .competitorProductName(competitivePrice.getCompetitorProductName())
                .inStock(competitivePrice.isInStock())
                .dataStale(competitivePrice.isDataStale(24))
                .lastChecked(competitivePrice.getLastChecked())
                .createdAt(competitivePrice.getCreatedAt())
                .updatedAt(competitivePrice.getUpdatedAt())
                .build();
    }

    private PriceElasticityDto toPriceElasticityDto(PriceElasticity elasticity) {
        return PriceElasticityDto.builder()
                .id(elasticity.getId())
                .tenantId(elasticity.getTenantId())
                .productId(elasticity.getProductId())
                .categoryId(elasticity.getCategoryId())
                .elasticityCoefficient(elasticity.getElasticityCoefficient())
                .elasticityType(elasticity.getElasticityType())
                .isElastic(elasticity.isElastic())
                .isInelastic(elasticity.isInelastic())
                .isUnitElastic(elasticity.isUnitElastic())
                .originalPrice(elasticity.getOriginalPrice())
                .newPrice(elasticity.getNewPrice())
                .priceChangePercent(elasticity.getPriceChangePercent())
                .originalDemand(elasticity.getOriginalDemand())
                .newDemand(elasticity.getNewDemand())
                .demandChangePercent(elasticity.getDemandChangePercent())
                .periodStart(elasticity.getPeriodStart())
                .periodEnd(elasticity.getPeriodEnd())
                .createdAt(elasticity.getCreatedAt())
                .build();
    }
}
