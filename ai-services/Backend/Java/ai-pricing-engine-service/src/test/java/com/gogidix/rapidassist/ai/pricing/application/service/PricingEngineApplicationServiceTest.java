package com.gogidix.rapidassist.ai.pricing.application.service;

import com.gogidix.rapidassist.ai.pricing.application.dto.*;
import com.gogidix.rapidassist.ai.pricing.domain.model.*;
import com.gogidix.rapidassist.ai.pricing.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PricingEngineApplicationService
 * Tests business logic and use cases
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pricing Engine Application Service Tests")
class PricingEngineApplicationServiceTest {

    @Mock
    private PriceRuleRepositoryPort priceRuleRepository;

    @Mock
    private DiscountRepositoryPort discountRepository;

    @Mock
    private PriceHistoryRepositoryPort priceHistoryRepository;

    @Mock
    private CompetitivePriceRepositoryPort competitivePriceRepository;

    @Mock
    private PriceElasticityRepositoryPort priceElasticityRepository;

    @InjectMocks
    private PricingEngineApplicationService service;

    private final String tenantId = "tenant-123";
    private final String productId = "product-123";
    private final UUID ruleId = UUID.randomUUID();
    private final String createdBy = "user-123";

    @Test
    @DisplayName("Should create price rule successfully")
    void shouldCreatePriceRuleSuccessfully() {
        // Given
        String name = "Summer Sale";
        String description = "Summer discount";
        PricingStrategyType strategyType = PricingStrategyType.DYNAMIC_PRICING;
        BigDecimal basePrice = new BigDecimal("100.00");
        String categoryId = "category-456";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("maxDiscount", 20);
        Integer priority = 5;
        LocalDateTime validFrom = LocalDateTime.now();
        LocalDateTime validUntil = LocalDateTime.now().plusMonths(3);

        PriceRule savedRule = PriceRule.builder()
                .id(ruleId)
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
                .priority(priority)
                .validFrom(validFrom)
                .validUntil(validUntil)
                .build();

        when(priceRuleRepository.save(any(PriceRule.class))).thenReturn(savedRule);

        // When
        PriceRuleDto result = service.createPriceRule(
                tenantId, name, description, strategyType, basePrice,
                productId, categoryId, parameters, priority,
                validFrom, validUntil, createdBy
        );

        // Then
        assertNotNull(result);
        assertEquals(ruleId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals(name, result.getName());
        assertEquals(PriceRuleStatus.DRAFT, result.getStatus());
        assertEquals(basePrice, result.getCurrentPrice());
        verify(priceRuleRepository, times(1)).save(any(PriceRule.class));
    }

    @Test
    @DisplayName("Should get price rule by id")
    void shouldGetPriceRuleById() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .id(ruleId)
                .tenantId(tenantId)
                .name("Test Rule")
                .status(PriceRuleStatus.ACTIVE)
                .basePrice(new BigDecimal("100.00"))
                .currentPrice(new BigDecimal("110.00"))
                .build();

        when(priceRuleRepository.findByIdAndTenantId(ruleId, tenantId))
                .thenReturn(Optional.of(priceRule));

        // When
        PriceRuleDto result = service.getPriceRule(ruleId, tenantId);

        // Then
        assertNotNull(result);
        assertEquals(ruleId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals("Test Rule", result.getName());
        verify(priceRuleRepository, times(1)).findByIdAndTenantId(ruleId, tenantId);
    }

    @Test
    @DisplayName("Should throw exception when price rule not found")
    void shouldThrowExceptionWhenPriceRuleNotFound() {
        // Given
        when(priceRuleRepository.findByIdAndTenantId(ruleId, tenantId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> service.getPriceRule(ruleId, tenantId));
        verify(priceRuleRepository, times(1)).findByIdAndTenantId(ruleId, tenantId);
    }

    @Test
    @DisplayName("Should get price rules by product")
    void shouldGetPriceRulesByProduct() {
        // Given
        PriceRule rule1 = PriceRule.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .productId(productId)
                .name("Rule 1")
                .build();

        PriceRule rule2 = PriceRule.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .productId(productId)
                .name("Rule 2")
                .build();

        when(priceRuleRepository.findByProductIdAndTenantId(productId, tenantId))
                .thenReturn(Arrays.asList(rule1, rule2));

        // When
        List<PriceRuleDto> results = service.getPriceRulesByProduct(tenantId, productId);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(priceRuleRepository, times(1)).findByProductIdAndTenantId(productId, tenantId);
    }

    @Test
    @DisplayName("Should activate price rule successfully")
    void shouldActivatePriceRuleSuccessfully() {
        // Given
        PriceRule priceRule = PriceRule.builder()
                .id(ruleId)
                .tenantId(tenantId)
                .name("Test Rule")
                .status(PriceRuleStatus.DRAFT)
                .build();

        when(priceRuleRepository.findByIdAndTenantId(ruleId, tenantId))
                .thenReturn(Optional.of(priceRule));
        when(priceRuleRepository.save(any(PriceRule.class))).thenReturn(priceRule);

        // When
        PriceRuleDto result = service.activatePriceRule(ruleId, tenantId, createdBy);

        // Then
        assertNotNull(result);
        assertEquals(PriceRuleStatus.ACTIVE, result.getStatus());
        verify(priceRuleRepository, times(1)).findByIdAndTenantId(ruleId, tenantId);
        verify(priceRuleRepository, times(1)).save(any(PriceRule.class));
    }

    @Test
    @DisplayName("Should update price successfully")
    void shouldUpdatePriceSuccessfully() {
        // Given
        BigDecimal newPrice = new BigDecimal("110.00");
        String reason = "Market adjustment";

        PriceRule priceRule = PriceRule.builder()
                .id(ruleId)
                .tenantId(tenantId)
                .productId(productId)
                .currentPrice(new BigDecimal("100.00"))
                .minPrice(new BigDecimal("80.00"))
                .maxPrice(new BigDecimal("120.00"))
                .build();

        when(priceRuleRepository.findByIdAndTenantId(ruleId, tenantId))
                .thenReturn(Optional.of(priceRule));
        when(priceRuleRepository.save(any(PriceRule.class))).thenReturn(priceRule);
        when(priceHistoryRepository.save(any(PriceHistory.class))).thenReturn(null);

        // When
        PriceRuleDto result = service.updatePrice(ruleId, tenantId, newPrice, reason, createdBy);

        // Then
        assertNotNull(result);
        assertEquals(newPrice, result.getCurrentPrice());
        verify(priceRuleRepository, times(1)).findByIdAndTenantId(ruleId, tenantId);
        verify(priceRuleRepository, times(1)).save(any(PriceRule.class));
        verify(priceHistoryRepository, times(1)).save(any(PriceHistory.class));
    }

    @Test
    @DisplayName("Should calculate price without discount")
    void shouldCalculatePriceWithoutDiscount() {
        // Given
        BigDecimal basePrice = new BigDecimal("100.00");

        when(priceRuleRepository.findActiveRulesByProductAndTenantId(productId, tenantId))
                .thenReturn(Collections.emptyList());

        // When
        PriceCalculationDto result = service.calculatePrice(
                tenantId, productId, basePrice, null, null
        );

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(basePrice, result.getOriginalPrice());
        assertEquals(basePrice, result.getFinalPrice());
        assertEquals(BigDecimal.ZERO, result.getDiscountAmount());
        verify(priceRuleRepository, times(1))
                .findActiveRulesByProductAndTenantId(productId, tenantId);
    }

    @Test
    @DisplayName("Should calculate price with active rule")
    void shouldCalculatePriceWithActiveRule() {
        // Given
        BigDecimal basePrice = new BigDecimal("100.00");

        PriceRule activeRule = PriceRule.builder()
                .id(ruleId)
                .tenantId(tenantId)
                .productId(productId)
                .name("Dynamic Pricing Rule")
                .priority(10)
                .parameters(new HashMap<>())
                .build();

        when(priceRuleRepository.findActiveRulesByProductAndTenantId(productId, tenantId))
                .thenReturn(Collections.singletonList(activeRule));

        // When
        PriceCalculationDto result = service.calculatePrice(
                tenantId, productId, basePrice, null, null
        );

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(basePrice, result.getOriginalPrice());
        assertNotNull(result.getFinalPrice());
        assertEquals(ruleId, result.getAppliedPriceRuleId());
        verify(priceRuleRepository, times(1))
                .findActiveRulesByProductAndTenantId(productId, tenantId);
    }

    @Test
    @DisplayName("Should calculate price with discount")
    void shouldCalculatePriceWithDiscount() {
        // Given
        BigDecimal basePrice = new BigDecimal("100.00");
        String discountCode = "SAVE10";

        Discount discount = Discount.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .code(discountCode)
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(new BigDecimal("10"))
                .active(true)
                .usageCount(0)
                .usageLimit(100)
                .validFrom(LocalDateTime.now().minusDays(1))
                .validUntil(LocalDateTime.now().plusDays(30))
                .build();

        when(priceRuleRepository.findActiveRulesByProductAndTenantId(productId, tenantId))
                .thenReturn(Collections.emptyList());
        when(discountRepository.findByCodeAndTenantId(discountCode, tenantId))
                .thenReturn(Optional.of(discount));
        when(discountRepository.save(any(Discount.class))).thenReturn(discount);

        // When
        PriceCalculationDto result = service.calculatePrice(
                tenantId, productId, basePrice, discountCode, null
        );

        // Then
        assertNotNull(result);
        assertEquals(basePrice, result.getOriginalPrice());
        assertEquals(new BigDecimal("10.00"), result.getDiscountAmount());
        assertEquals(new BigDecimal("90.00"), result.getFinalPrice());
        assertEquals(discountCode, result.getAppliedDiscountCode());
        verify(discountRepository, times(1)).findByCodeAndTenantId(discountCode, tenantId);
        verify(discountRepository, times(1)).save(any(Discount.class));
    }

    @Test
    @DisplayName("Should create discount successfully")
    void shouldCreateDiscountSuccessfully() {
        // Given
        String code = "SUMMER20";
        String name = "Summer Sale";
        String description = "20% off summer items";
        DiscountType discountType = DiscountType.PERCENTAGE;
        BigDecimal discountValue = new BigDecimal("20");
        BigDecimal maxDiscountAmount = new BigDecimal("50.00");
        BigDecimal minPurchaseAmount = new BigDecimal("100.00");
        Integer usageLimit = 1000;
        LocalDateTime validFrom = LocalDateTime.now();
        LocalDateTime validUntil = LocalDateTime.now().plusMonths(3);

        UUID discountId = UUID.randomUUID();
        Discount discount = Discount.builder()
                .id(discountId)
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
                .validFrom(validFrom)
                .validUntil(validUntil)
                .active(true)
                .build();

        when(discountRepository.save(any(Discount.class))).thenReturn(discount);

        // When
        DiscountDto result = service.createDiscount(
                tenantId, code, name, description, discountType, discountValue,
                maxDiscountAmount, minPurchaseAmount, usageLimit,
                null, null, null, validFrom, validUntil, createdBy
        );

        // Then
        assertNotNull(result);
        assertEquals(discountId, result.getId());
        assertEquals(code, result.getCode());
        assertEquals(name, result.getName());
        assertEquals(discountType, result.getDiscountType());
        assertEquals(discountValue, result.getDiscountValue());
        verify(discountRepository, times(1)).save(any(Discount.class));
    }

    @Test
    @DisplayName("Should get discount by code")
    void shouldGetDiscountByCode() {
        // Given
        String code = "SAVE10";
        Discount discount = Discount.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .code(code)
                .name("Save 10%")
                .active(true)
                .build();

        when(discountRepository.findByCodeAndTenantId(code, tenantId))
                .thenReturn(Optional.of(discount));

        // When
        DiscountDto result = service.getDiscountByCode(code, tenantId);

        // Then
        assertNotNull(result);
        assertEquals(code, result.getCode());
        verify(discountRepository, times(1)).findByCodeAndTenantId(code, tenantId);
    }

    @Test
    @DisplayName("Should throw exception when discount not found")
    void shouldThrowExceptionWhenDiscountNotFound() {
        // Given
        String code = "INVALID";
        when(discountRepository.findByCodeAndTenantId(code, tenantId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> service.getDiscountByCode(code, tenantId));
        verify(discountRepository, times(1)).findByCodeAndTenantId(code, tenantId);
    }

    @Test
    @DisplayName("Should get active discounts")
    void shouldGetActiveDiscounts() {
        // Given
        Discount discount1 = Discount.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .code("SAVE10")
                .active(true)
                .build();

        Discount discount2 = Discount.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .code("SAVE20")
                .active(true)
                .build();

        when(discountRepository.findActiveDiscountsByTenantId(tenantId))
                .thenReturn(Arrays.asList(discount1, discount2));

        // When
        List<DiscountDto> results = service.getActiveDiscounts(tenantId);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(discountRepository, times(1)).findActiveDiscountsByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should get price history")
    void shouldGetPriceHistory() {
        // Given
        PriceHistory history1 = PriceHistory.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .productId(productId)
                .oldPrice(new BigDecimal("100.00"))
                .newPrice(new BigDecimal("110.00"))
                .build();

        PriceHistory history2 = PriceHistory.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .productId(productId)
                .oldPrice(new BigDecimal("110.00"))
                .newPrice(new BigDecimal("105.00"))
                .build();

        when(priceHistoryRepository.findByProductIdAndTenantId(productId, tenantId))
                .thenReturn(Arrays.asList(history1, history2));

        // When
        List<PriceHistoryDto> results = service.getPriceHistory(tenantId, productId);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(priceHistoryRepository, times(1)).findByProductIdAndTenantId(productId, tenantId);
    }

    @Test
    @DisplayName("Should add competitive price")
    void shouldAddCompetitivePrice() {
        // Given
        String competitorName = "Competitor Inc";
        String competitorUrl = "https://competitor.com/product";
        BigDecimal competitorPrice = new BigDecimal("95.00");
        BigDecimal ourPrice = new BigDecimal("100.00");

        UUID competitivePriceId = UUID.randomUUID();
        CompetitivePrice competitivePrice = CompetitivePrice.builder()
                .id(competitivePriceId)
                .tenantId(tenantId)
                .productId(productId)
                .competitorName(competitorName)
                .competitorUrl(competitorUrl)
                .competitorPrice(competitorPrice)
                .ourPrice(ourPrice)
                .inStock(true)
                .build();

        when(competitivePriceRepository.save(any(CompetitivePrice.class)))
                .thenReturn(competitivePrice);

        // When
        CompetitivePriceDto result = service.addCompetitivePrice(
                tenantId, productId, competitorName, competitorUrl,
                competitorPrice, ourPrice, true, createdBy
        );

        // Then
        assertNotNull(result);
        assertEquals(competitivePriceId, result.getId());
        assertEquals(competitorName, result.getCompetitorName());
        assertEquals(competitorPrice, result.getCompetitorPrice());
        verify(competitivePriceRepository, times(1)).save(any(CompetitivePrice.class));
    }

    @Test
    @DisplayName("Should get competitive prices")
    void shouldGetCompetitivePrices() {
        // Given
        CompetitivePrice comp1 = CompetitivePrice.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .productId(productId)
                .competitorName("Competitor 1")
                .build();

        CompetitivePrice comp2 = CompetitivePrice.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .productId(productId)
                .competitorName("Competitor 2")
                .build();

        when(competitivePriceRepository.findByProductIdAndTenantId(productId, tenantId))
                .thenReturn(Arrays.asList(comp1, comp2));

        // When
        List<CompetitivePriceDto> results = service.getCompetitivePrices(tenantId, productId);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(competitivePriceRepository, times(1))
                .findByProductIdAndTenantId(productId, tenantId);
    }

    @Test
    @DisplayName("Should calculate price elasticity")
    void shouldCalculatePriceElasticity() {
        // Given
        String categoryId = "category-456";
        BigDecimal originalPrice = new BigDecimal("100.00");
        BigDecimal newPrice = new BigDecimal("110.00");
        Integer originalDemand = 1000;
        Integer newDemand = 900;
        LocalDateTime periodStart = LocalDateTime.now().minusMonths(1);
        LocalDateTime periodEnd = LocalDateTime.now();

        PriceElasticity elasticity = PriceElasticity.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .productId(productId)
                .categoryId(categoryId)
                .build();

        when(priceElasticityRepository.save(any(PriceElasticity.class)))
                .thenReturn(elasticity);

        // When
        PriceElasticityDto result = service.calculateElasticity(
                tenantId, productId, categoryId, originalPrice, newPrice,
                originalDemand, newDemand, periodStart, periodEnd
        );

        // Then
        assertNotNull(result);
        assertEquals(tenantId, result.getTenantId());
        assertEquals(productId, result.getProductId());
        verify(priceElasticityRepository, times(1)).save(any(PriceElasticity.class));
    }

    @Test
    @DisplayName("Should handle empty price rules list")
    void shouldHandleEmptyPriceRulesList() {
        // Given
        when(priceRuleRepository.findByProductIdAndTenantId(productId, tenantId))
                .thenReturn(Collections.emptyList());

        // When
        List<PriceRuleDto> results = service.getPriceRulesByProduct(tenantId, productId);

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(priceRuleRepository, times(1)).findByProductIdAndTenantId(productId, tenantId);
    }

    @Test
    @DisplayName("Should handle empty active discounts list")
    void shouldHandleEmptyActiveDiscountsList() {
        // Given
        when(discountRepository.findActiveDiscountsByTenantId(tenantId))
                .thenReturn(Collections.emptyList());

        // When
        List<DiscountDto> results = service.getActiveDiscounts(tenantId);

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(discountRepository, times(1)).findActiveDiscountsByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should handle empty price history")
    void shouldHandleEmptyPriceHistory() {
        // Given
        when(priceHistoryRepository.findByProductIdAndTenantId(productId, tenantId))
                .thenReturn(Collections.emptyList());

        // When
        List<PriceHistoryDto> results = service.getPriceHistory(tenantId, productId);

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(priceHistoryRepository, times(1)).findByProductIdAndTenantId(productId, tenantId);
    }
}
