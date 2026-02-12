package com.gogidix.rapidassist.ai.pricing.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.pricing.application.dto.*;
import com.gogidix.rapidassist.ai.pricing.application.service.PricingEngineApplicationService;
import com.gogidix.rapidassist.ai.pricing.domain.model.DiscountType;
import com.gogidix.rapidassist.ai.pricing.domain.model.PriceRuleStatus;
import com.gogidix.rapidassist.ai.pricing.domain.model.PricingStrategyType;
import com.gogidix.rapidassist.ai.pricing.domain.tenant.TenantContext;
import com.gogidix.rapidassist.ai.pricing.interfaces.rest.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Pricing Engine operations.
 * API endpoint: /api/v1/pricing
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
@Tag(name = "Pricing Engine", description = "AI-Powered Pricing Engine APIs")
public class PricingEngineRestController {

    private final PricingEngineApplicationService applicationService;

    // ==================== Price Rule Operations ====================

    @PostMapping("/rules")
    @Operation(summary = "Create price rule", description = "Creates a new pricing rule for a product")
    public ResponseEntity<PriceRuleDto> createPriceRule(
            @Valid @RequestBody CreatePriceRuleRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        PriceRuleDto priceRuleDto = applicationService.createPriceRule(
                tenantId,
                request.getName(),
                request.getDescription(),
                request.getStrategyType(),
                request.getBasePrice(),
                request.getProductId(),
                request.getCategoryId(),
                request.getParameters(),
                request.getPriority(),
                request.getValidFrom(),
                request.getValidUntil(),
                request.getCreatedBy()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(priceRuleDto);
    }

    @GetMapping("/rules/{ruleId}")
    @Operation(summary = "Get price rule", description = "Retrieves a price rule by ID")
    public ResponseEntity<PriceRuleDto> getPriceRule(
            @PathVariable UUID ruleId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        PriceRuleDto priceRuleDto = applicationService.getPriceRule(ruleId, tenantId);
        return ResponseEntity.ok(priceRuleDto);
    }

    @GetMapping("/rules/product/{productId}")
    @Operation(summary = "Get price rules by product", description = "Retrieves all pricing rules for a product")
    public ResponseEntity<List<PriceRuleDto>> getPriceRulesByProduct(
            @PathVariable String productId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<PriceRuleDto> priceRules = applicationService.getPriceRulesByProduct(tenantId, productId);
        return ResponseEntity.ok(priceRules);
    }

    @PostMapping("/rules/{ruleId}/activate")
    @Operation(summary = "Activate price rule", description = "Activates a pricing rule")
    public ResponseEntity<PriceRuleDto> activatePriceRule(
            @PathVariable UUID ruleId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String updatedBy) {

        TenantContext.setTenantId(tenantId);

        PriceRuleDto priceRuleDto = applicationService.activatePriceRule(ruleId, tenantId, updatedBy);
        return ResponseEntity.ok(priceRuleDto);
    }

    @PostMapping("/rules/{ruleId}/price")
    @Operation(summary = "Update price", description = "Updates the current price for a pricing rule")
    public ResponseEntity<PriceRuleDto> updatePrice(
            @PathVariable UUID ruleId,
            @Valid @RequestBody UpdatePriceRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        PriceRuleDto priceRuleDto = applicationService.updatePrice(
                ruleId, tenantId, request.getNewPrice(), request.getReason(), request.getUpdatedBy()
        );

        return ResponseEntity.ok(priceRuleDto);
    }

    // ==================== Price Calculation ====================

    @PostMapping("/calculate")
    @Operation(summary = "Calculate price", description = "Calculates the optimal price for a product")
    public ResponseEntity<PriceCalculationDto> calculatePrice(
            @Valid @RequestBody CalculatePriceRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        PriceCalculationDto result = applicationService.calculatePrice(
                tenantId,
                request.getProductId(),
                request.getBasePrice(),
                request.getDiscountCode(),
                request.getCustomerId()
        );

        return ResponseEntity.ok(result);
    }

    // ==================== Discount Operations ====================

    @PostMapping("/discounts")
    @Operation(summary = "Create discount", description = "Creates a new discount")
    public ResponseEntity<DiscountDto> createDiscount(
            @Valid @RequestBody CreateDiscountRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        DiscountDto discountDto = applicationService.createDiscount(
                tenantId,
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.getDiscountType(),
                request.getDiscountValue(),
                request.getMaxDiscountAmount(),
                request.getMinPurchaseAmount(),
                request.getUsageLimit(),
                request.getProductId(),
                request.getCategoryId(),
                request.getCustomerId(),
                request.getValidFrom(),
                request.getValidUntil(),
                request.getCreatedBy()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(discountDto);
    }

    @GetMapping("/discounts/code/{code}")
    @Operation(summary = "Get discount by code", description = "Retrieves a discount by its code")
    public ResponseEntity<DiscountDto> getDiscountByCode(
            @PathVariable String code,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        DiscountDto discountDto = applicationService.getDiscountByCode(code, tenantId);
        return ResponseEntity.ok(discountDto);
    }

    @GetMapping("/discounts/active")
    @Operation(summary = "Get active discounts", description = "Retrieves all active discounts for a tenant")
    public ResponseEntity<List<DiscountDto>> getActiveDiscounts(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<DiscountDto> discounts = applicationService.getActiveDiscounts(tenantId);
        return ResponseEntity.ok(discounts);
    }

    // ==================== Price History ====================

    @GetMapping("/history/{productId}")
    @Operation(summary = "Get price history", description = "Retrieves price history for a product")
    public ResponseEntity<List<PriceHistoryDto>> getPriceHistory(
            @PathVariable String productId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<PriceHistoryDto> history = applicationService.getPriceHistory(tenantId, productId);
        return ResponseEntity.ok(history);
    }

    // ==================== Competitive Pricing ====================

    @PostMapping("/competitive")
    @Operation(summary = "Add competitive price", description = "Adds competitive pricing data")
    public ResponseEntity<CompetitivePriceDto> addCompetitivePrice(
            @Valid @RequestBody AddCompetitivePriceRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        CompetitivePriceDto competitivePriceDto = applicationService.addCompetitivePrice(
                tenantId,
                request.getProductId(),
                request.getCompetitorName(),
                request.getCompetitorUrl(),
                request.getCompetitorPrice(),
                request.getOurPrice(),
                request.isInStock(),
                request.getCreatedBy()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(competitivePriceDto);
    }

    @GetMapping("/competitive/{productId}")
    @Operation(summary = "Get competitive prices", description = "Retrieves competitive pricing data for a product")
    public ResponseEntity<List<CompetitivePriceDto>> getCompetitivePrices(
            @PathVariable String productId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<CompetitivePriceDto> prices = applicationService.getCompetitivePrices(tenantId, productId);
        return ResponseEntity.ok(prices);
    }

    // ==================== Price Elasticity ====================

    @PostMapping("/elasticity")
    @Operation(summary = "Calculate price elasticity", description = "Calculates price elasticity for a product")
    public ResponseEntity<PriceElasticityDto> calculateElasticity(
            @Valid @RequestBody CalculateElasticityRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        PriceElasticityDto elasticityDto = applicationService.calculateElasticity(
                tenantId,
                request.getProductId(),
                request.getCategoryId(),
                request.getOriginalPrice(),
                request.getNewPrice(),
                request.getOriginalDemand(),
                request.getNewDemand(),
                request.getPeriodStart(),
                request.getPeriodEnd()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(elasticityDto);
    }
}
