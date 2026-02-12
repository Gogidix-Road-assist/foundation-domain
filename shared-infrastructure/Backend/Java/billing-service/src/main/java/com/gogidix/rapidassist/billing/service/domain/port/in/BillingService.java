package com.gogidix.rapidassist.billing.service.domain.port.in;

import com.gogidix.rapidassist.billing.service.domain.model.BillingAccount;
import com.gogidix.rapidassist.billing.service.domain.model.BillingPlan;
import com.gogidix.rapidassist.billing.service.domain.model.EnhancedBillingAccount;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BillingService {

    // Basic operations
    Optional<BillingAccount> getBillingAccount(String tenantId);
    BillingAccount setBillingPlan(String tenantId, BillingPlan plan);

    // Enhanced operations
    Optional<EnhancedBillingAccount> getEnhancedBillingAccount(String tenantId);
    EnhancedBillingAccount createEnhancedBillingAccount(String tenantId,
                                                      BillingPlan plan);
    EnhancedBillingAccount updateBillingAccount(String tenantId,
                                               EnhancedBillingAccount account);

    // Subscription management
    boolean cancelSubscription(String tenantId, String reason);
    boolean reactivateSubscription(String tenantId);
    boolean extendSubscription(String tenantId, int days);

    // Billing operations
    BigDecimal calculateUsageCharges(String tenantId, Instant from, Instant to);
    List<BillingInvoice> generateInvoice(String tenantId, Instant periodStart, Instant periodEnd);
    boolean processPayment(String tenantId, BigDecimal amount, String paymentMethodId);

    // Feature management
    boolean addFeature(String tenantId, String feature);
    boolean removeFeature(String tenantId, String feature);
    List<String> getAvailableFeatures(String tenantId);

    // Validation
    boolean canUpgradePlan(String tenantId, BillingPlan targetPlan);
    boolean hasPermission(String tenantId, String operation);

    record BillingInvoice(
        String id,
        String tenantId,
        Instant issuedAt,
        Instant dueAt,
        BigDecimal amount,
        String status,
        List<BillingLineItem> lineItems
    ) {}

    record BillingLineItem(
        String description,
        BigDecimal amount,
        String period,
        Map<String, Object> metadata
    ) {}
}