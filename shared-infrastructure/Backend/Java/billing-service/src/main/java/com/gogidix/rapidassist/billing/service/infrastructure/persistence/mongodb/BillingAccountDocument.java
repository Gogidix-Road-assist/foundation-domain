package com.gogidix.rapidassist.billing.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.billing.service.domain.model.BillingAccount;
import com.gogidix.rapidassist.billing.service.domain.model.BillingPlan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "billing_accounts")
public record BillingAccountDocument(

    @Id
    String id,

    @Field("tenant_id")
    String tenantId,

    @Field("plan")
    BillingPlan plan,

    @Field("updated_at")
    Instant updatedAt,

    @Field("created_at")
    Instant createdAt,

    @Field("version")
    Long version
) {

    public static BillingAccountDocument fromDomain(BillingAccount account) {
        Instant now = Instant.now();
        return new BillingAccountDocument(
            null, // MongoDB will generate ID
            account.tenantId(),
            account.plan(),
            account.updatedAt() != null ? account.updatedAt() : now,
            now,
            1L
        );
    }

    public static BillingAccountDocument updateFromDomain(BillingAccountDocument existing, BillingAccount account) {
        return new BillingAccountDocument(
            existing.id(),
            account.tenantId(),
            account.plan(),
            account.updatedAt() != null ? account.updatedAt() : Instant.now(),
            existing.createdAt(),
            existing.version() + 1
        );
    }

    public BillingAccount toDomain() {
        return new BillingAccount(
            tenantId(),
            plan(),
            updatedAt()
        );
    }
}