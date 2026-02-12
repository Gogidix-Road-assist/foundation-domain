package com.gogidix.rapidassist.billing.service.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record EnhancedBillingAccount(
    String tenantId,
    BillingPlan plan,
    BillingStatus status,
    Instant updatedAt,
    Instant createdAt,
    Instant subscriptionStartsAt,
    Instant subscriptionEndsAt,
    String subscriptionId,
    String customerId,
    BigDecimal currentBalance,
    BigDecimal creditLimit,
    List<String> features,
    Map<String, Object> metadata,
    List<String> paymentMethods
) {

    public EnhancedBillingAccount(String tenantId, BillingPlan plan) {
        this(
            tenantId,
            plan,
            BillingStatus.ACTIVE,
            Instant.now(),
            Instant.now(),
            Instant.now(),
            calculatePlanExpiry(plan),
            null,
            null,
            BigDecimal.ZERO,
            plan == BillingPlan.FREE ? BigDecimal.ZERO : new BigDecimal("1000.00"),
            getDefaultFeatures(plan),
            Map.of(),
            List.of()
        );
    }

    private static Instant calculatePlanExpiry(BillingPlan plan) {
        return plan == BillingPlan.FREE ?
            Instant.MAX :
            Instant.now().plusSeconds(30 * 24 * 60 * 60); // 30 days
    }

    private static List<String> getDefaultFeatures(BillingPlan plan) {
        return switch (plan) {
            case FREE -> List.of(
                "basic_support",
                "api_access",
                "rate_limit_100_per_hour"
            );
            case PRO -> List.of(
                "priority_support",
                "api_access",
                "rate_limit_1000_per_hour",
                "advanced_analytics",
                "custom_integrations",
                "webhooks"
            );
        };
    }

    public boolean hasFeature(String feature) {
        return features.contains(feature);
    }

    public boolean isSubscriptionActive() {
        Instant now = Instant.now();
        return status == BillingStatus.ACTIVE &&
               subscriptionEndsAt.isAfter(now);
    }

    public boolean canIncurDebt(BigDecimal amount) {
        return currentBalance.add(amount).compareTo(creditLimit) <= 0;
    }

    public enum BillingStatus {
        ACTIVE,
        SUSPENDED,
        CANCELLED,
        PENDING_PAYMENT
    }
}