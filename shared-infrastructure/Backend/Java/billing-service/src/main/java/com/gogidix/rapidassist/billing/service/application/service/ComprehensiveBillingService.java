package com.gogidix.rapidassist.billing.service.application.service;

import com.gogidix.rapidassist.billing.service.domain.model.BillingAccount;
import com.gogidix.rapidassist.billing.service.domain.model.BillingPlan;
import com.gogidix.rapidassist.billing.service.domain.model.EnhancedBillingAccount;
import com.gogidix.rapidassist.billing.service.domain.port.in.BillingService;
import com.gogidix.rapidassist.billing.service.domain.port.out.BillingAccountStore;
import com.gogidix.rapidassist.billing.service.infrastructure.persistence.mongodb.EnhancedBillingAccountDocument;
import com.gogidix.rapidassist.billing.service.infrastructure.persistence.mongodb.EnhancedBillingAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class ComprehensiveBillingService implements BillingService {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveBillingService.class);

    private final BillingAccountStore billingAccountStore;
    private final EnhancedBillingAccountRepository enhancedAccountRepository;

    // In-memory cache for billing rules
    private final Map<String, List<String>> planFeaturesCache = new ConcurrentHashMap<>();

    public ComprehensiveBillingService(BillingAccountStore billingAccountStore,
                                      EnhancedBillingAccountRepository enhancedAccountRepository) {
        this.billingAccountStore = billingAccountStore;
        this.enhancedAccountRepository = enhancedAccountRepository;
        initializePlanFeatures();
    }

    private void initializePlanFeatures() {
        planFeaturesCache.put("FREE", Arrays.asList(
            "basic_support",
            "api_access",
            "rate_limit_100_per_hour"
        ));

        planFeaturesCache.put("PRO", Arrays.asList(
            "priority_support",
            "api_access",
            "rate_limit_1000_per_hour",
            "advanced_analytics",
            "custom_integrations",
            "webhooks",
            "dedicated_account_manager"
        ));
    }

    @Override
    public Optional<BillingAccount> getBillingAccount(String tenantId) {
        return billingAccountStore.find(tenantId);
    }

    @Override
    public BillingAccount setBillingPlan(String tenantId, BillingPlan plan) {
        logger.info("Setting billing plan {} for tenant: {}", plan, tenantId);

        BillingAccount account = new BillingAccount(
            tenantId,
            plan,
            Instant.now()
        );

        return billingAccountStore.upsert(account);
    }

    @Override
    public Optional<EnhancedBillingAccount> getEnhancedBillingAccount(String tenantId) {
        try {
            return enhancedAccountRepository.findByTenantId(tenantId)
                .map(EnhancedBillingAccountDocument::toDomain);
        } catch (Exception e) {
            logger.error("Error retrieving enhanced billing account for tenant: {}", tenantId, e);
            return Optional.empty();
        }
    }

    @Override
    public EnhancedBillingAccount createEnhancedBillingAccount(String tenantId,
                                                              BillingPlan plan) {
        logger.info("Creating enhanced billing account for tenant: {} with plan: {}", tenantId, plan);

        EnhancedBillingAccount account = new EnhancedBillingAccount(tenantId, plan);
        EnhancedBillingAccountDocument document = EnhancedBillingAccountDocument.fromDomain(account);

        EnhancedBillingAccountDocument saved = enhancedAccountRepository.save(document);
        return saved.toDomain();
    }

    @Override
    public EnhancedBillingAccount updateBillingAccount(String tenantId,
                                                      EnhancedBillingAccount account) {
        logger.info("Updating enhanced billing account for tenant: {}", tenantId);

        EnhancedBillingAccountDocument existing = enhancedAccountRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Billing account not found"));

        EnhancedBillingAccountDocument updated = EnhancedBillingAccountDocument.updateFromDomain(existing, account);
        EnhancedBillingAccountDocument saved = enhancedAccountRepository.save(updated);

        return saved.toDomain();
    }

    @Override
    public boolean cancelSubscription(String tenantId, String reason) {
        try {
            Optional<EnhancedBillingAccount> optAccount = getEnhancedBillingAccount(tenantId);
            if (optAccount.isEmpty()) {
                logger.warn("Attempted to cancel subscription for non-existent account: {}", tenantId);
                return false;
            }

            EnhancedBillingAccount account = optAccount.get();
            EnhancedBillingAccount cancelled = new EnhancedBillingAccount(
                account.tenantId(),
                account.plan(),
                EnhancedBillingAccount.BillingStatus.CANCELLED,
                Instant.now(),
                account.createdAt(),
                account.subscriptionStartsAt(),
                account.subscriptionEndsAt(),
                account.subscriptionId(),
                account.customerId(),
                account.currentBalance(),
                account.creditLimit(),
                account.features(),
                Map.of("cancellation_reason", reason),
                account.paymentMethods()
            );

            updateBillingAccount(tenantId, cancelled);
            logger.info("Subscription cancelled for tenant: {}, reason: {}", tenantId, reason);
            return true;

        } catch (Exception e) {
            logger.error("Failed to cancel subscription for tenant: {}", tenantId, e);
            return false;
        }
    }

    @Override
    public boolean reactivateSubscription(String tenantId) {
        try {
            Optional<EnhancedBillingAccount> optAccount = getEnhancedBillingAccount(tenantId);
            if (optAccount.isEmpty()) {
                return false;
            }

            EnhancedBillingAccount account = optAccount.get();
            if (account.status() == EnhancedBillingAccount.BillingStatus.ACTIVE) {
                logger.warn("Subscription already active for tenant: {}", tenantId);
                return true;
            }

            EnhancedBillingAccount reactivated = new EnhancedBillingAccount(
                account.tenantId(),
                account.plan(),
                EnhancedBillingAccount.BillingStatus.ACTIVE,
                Instant.now(),
                account.createdAt(),
                Instant.now(),
                calculatePlanExpiry(account.plan()),
                account.subscriptionId(),
                account.customerId(),
                account.currentBalance(),
                account.creditLimit(),
                account.features(),
                account.metadata(),
                account.paymentMethods()
            );

            updateBillingAccount(tenantId, reactivated);
            logger.info("Subscription reactivated for tenant: {}", tenantId);
            return true;

        } catch (Exception e) {
            logger.error("Failed to reactivate subscription for tenant: {}", tenantId, e);
            return false;
        }
    }

    @Override
    public boolean extendSubscription(String tenantId, int days) {
        try {
            Optional<EnhancedBillingAccount> optAccount = getEnhancedBillingAccount(tenantId);
            if (optAccount.isEmpty()) {
                return false;
            }

            EnhancedBillingAccount account = optAccount.get();
            Instant newExpiry = account.subscriptionEndsAt().plusSeconds(days * 24L * 60 * 60);

            EnhancedBillingAccount extended = new EnhancedBillingAccount(
                account.tenantId(),
                account.plan(),
                account.status(),
                Instant.now(),
                account.createdAt(),
                account.subscriptionStartsAt(),
                newExpiry,
                account.subscriptionId(),
                account.customerId(),
                account.currentBalance(),
                account.creditLimit(),
                account.features(),
                account.metadata(),
                account.paymentMethods()
            );

            updateBillingAccount(tenantId, extended);
            logger.info("Subscription extended for tenant: {} by {} days", tenantId, days);
            return true;

        } catch (Exception e) {
            logger.error("Failed to extend subscription for tenant: {}", tenantId, e);
            return false;
        }
    }

    @Override
    public BigDecimal calculateUsageCharges(String tenantId, Instant from, Instant to) {
        // Implementation would integrate with usage tracking systems
        logger.debug("Calculating usage charges for tenant: {} from {} to {}", tenantId, from, to);

        // For now, return a basic calculation based on plan
        Optional<BillingAccount> account = getBillingAccount(tenantId);
        if (account.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return switch (account.get().plan()) {
            case FREE -> BigDecimal.ZERO;
            case PRO -> new BigDecimal("29.99"); // Fixed monthly rate for PRO
        };
    }

    @Override
    public List<BillingInvoice> generateInvoice(String tenantId, Instant periodStart, Instant periodEnd) {
        // Placeholder implementation
        String invoiceId = UUID.randomUUID().toString();
        BigDecimal amount = calculateUsageCharges(tenantId, periodStart, periodEnd);

        BillingInvoice invoice = new BillingInvoice(
            invoiceId,
            tenantId,
            Instant.now(),
            Instant.now().plusSeconds(30 * 24 * 60 * 60), // Due in 30 days
            amount,
            "PENDING",
            List.of(new BillingLineItem(
                "Monthly subscription",
                amount,
                periodStart + " to " + periodEnd,
                Map.of("plan_type", getBillingAccount(tenantId).map(a -> a.plan().toString()).orElse("UNKNOWN"))
            ))
        );

        return List.of(invoice);
    }

    @Override
    public boolean processPayment(String tenantId, BigDecimal amount, String paymentMethodId) {
        logger.info("Processing payment of {} for tenant {} using method {}", amount, tenantId, paymentMethodId);
        // Integration with payment processor would go here
        return true;
    }

    @Override
    public boolean addFeature(String tenantId, String feature) {
        try {
            Optional<EnhancedBillingAccount> optAccount = getEnhancedBillingAccount(tenantId);
            if (optAccount.isEmpty()) {
                return false;
            }

            EnhancedBillingAccount account = optAccount.get();
            if (account.features().contains(feature)) {
                return true; // Feature already exists
            }

            List<String> newFeatures = new ArrayList<>(account.features());
            newFeatures.add(feature);

            EnhancedBillingAccount updated = new EnhancedBillingAccount(
                account.tenantId(),
                account.plan(),
                account.status(),
                Instant.now(),
                account.createdAt(),
                account.subscriptionStartsAt(),
                account.subscriptionEndsAt(),
                account.subscriptionId(),
                account.customerId(),
                account.currentBalance(),
                account.creditLimit(),
                newFeatures,
                account.metadata(),
                account.paymentMethods()
            );

            updateBillingAccount(tenantId, updated);
            logger.info("Added feature {} for tenant: {}", feature, tenantId);
            return true;

        } catch (Exception e) {
            logger.error("Failed to add feature {} for tenant: {}", feature, tenantId, e);
            return false;
        }
    }

    @Override
    public boolean removeFeature(String tenantId, String feature) {
        try {
            Optional<EnhancedBillingAccount> optAccount = getEnhancedBillingAccount(tenantId);
            if (optAccount.isEmpty()) {
                return false;
            }

            EnhancedBillingAccount account = optAccount.get();
            List<String> newFeatures = new ArrayList<>(account.features());
            newFeatures.remove(feature);

            EnhancedBillingAccount updated = new EnhancedBillingAccount(
                account.tenantId(),
                account.plan(),
                account.status(),
                Instant.now(),
                account.createdAt(),
                account.subscriptionStartsAt(),
                account.subscriptionEndsAt(),
                account.subscriptionId(),
                account.customerId(),
                account.currentBalance(),
                account.creditLimit(),
                newFeatures,
                account.metadata(),
                account.paymentMethods()
            );

            updateBillingAccount(tenantId, updated);
            logger.info("Removed feature {} for tenant: {}", feature, tenantId);
            return true;

        } catch (Exception e) {
            logger.error("Failed to remove feature {} for tenant: {}", feature, tenantId, e);
            return false;
        }
    }

    @Override
    public List<String> getAvailableFeatures(String tenantId) {
        Optional<BillingAccount> account = getBillingAccount(tenantId);
        return account.map(a -> planFeaturesCache.getOrDefault(a.plan().name(), List.of()))
                     .orElse(List.of());
    }

    @Override
    public boolean canUpgradePlan(String tenantId, BillingPlan targetPlan) {
        Optional<BillingAccount> account = getBillingAccount(tenantId);
        if (account.isEmpty()) {
            return true; // New account can choose any plan
        }

        BillingAccount current = account.get();
        return targetPlan.ordinal() > current.plan().ordinal();
    }

    @Override
    public boolean hasPermission(String tenantId, String operation) {
        Optional<EnhancedBillingAccount> account = getEnhancedBillingAccount(tenantId);
        if (account.isEmpty()) {
            return false;
        }

        EnhancedBillingAccount enhanced = account.get();

        // Check if subscription is active
        if (!enhanced.isSubscriptionActive()) {
            return false;
        }

        // Check operation-specific permissions
        return switch (operation) {
            case "api_access" -> enhanced.hasFeature("api_access");
            case "webhooks" -> enhanced.hasFeature("webhooks");
            case "analytics" -> enhanced.hasFeature("advanced_analytics");
            default -> false;
        };
    }

    private Instant calculatePlanExpiry(BillingPlan plan) {
        return plan == BillingPlan.FREE ?
            Instant.MAX :
            Instant.now().plusSeconds(30 * 24 * 60 * 60); // 30 days
    }
}