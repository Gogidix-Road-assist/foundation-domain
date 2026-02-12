package com.gogidix.rapidassist.billing.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.billing.service.domain.model.BillingPlan;
import com.gogidix.rapidassist.billing.service.domain.model.EnhancedBillingAccount;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "enhanced_billing_accounts")
public record EnhancedBillingAccountDocument(

    @Id
    String id,

    @Field("tenant_id")
    String tenantId,

    @Field("plan")
    BillingPlan plan,

    @Field("status")
    EnhancedBillingAccount.BillingStatus status,

    @Field("updated_at")
    Instant updatedAt,

    @Field("created_at")
    Instant createdAt,

    @Field("subscription_starts_at")
    Instant subscriptionStartsAt,

    @Field("subscription_ends_at")
    Instant subscriptionEndsAt,

    @Field("subscription_id")
    String subscriptionId,

    @Field("customer_id")
    String customerId,

    @Field("current_balance")
    BigDecimal currentBalance,

    @Field("credit_limit")
    BigDecimal creditLimit,

    @Field("features")
    List<String> features,

    @Field("metadata")
    Map<String, Object> metadata,

    @Field("payment_methods")
    List<String> paymentMethods,

    @Field("version")
    Long version
) {

    public static EnhancedBillingAccountDocument fromDomain(EnhancedBillingAccount account) {
        return new EnhancedBillingAccountDocument(
            null, // MongoDB will generate ID
            account.tenantId(),
            account.plan(),
            account.status(),
            account.updatedAt(),
            account.createdAt(),
            account.subscriptionStartsAt(),
            account.subscriptionEndsAt(),
            account.subscriptionId(),
            account.customerId(),
            account.currentBalance(),
            account.creditLimit(),
            account.features(),
            account.metadata(),
            account.paymentMethods(),
            1L
        );
    }

    public static EnhancedBillingAccountDocument updateFromDomain(EnhancedBillingAccountDocument existing,
                                                                EnhancedBillingAccount account) {
        return new EnhancedBillingAccountDocument(
            existing.id(),
            account.tenantId(),
            account.plan(),
            account.status(),
            account.updatedAt(),
            existing.createdAt(), // Preserve original creation time
            account.subscriptionStartsAt(),
            account.subscriptionEndsAt(),
            account.subscriptionId(),
            account.customerId(),
            account.currentBalance(),
            account.creditLimit(),
            account.features(),
            account.metadata(),
            account.paymentMethods(),
            existing.version() + 1
        );
    }

    public EnhancedBillingAccount toDomain() {
        return new EnhancedBillingAccount(
            tenantId(),
            plan(),
            status(),
            updatedAt(),
            createdAt(),
            subscriptionStartsAt(),
            subscriptionEndsAt(),
            subscriptionId(),
            customerId(),
            currentBalance(),
            creditLimit(),
            features(),
            metadata(),
            paymentMethods()
        );
    }
}