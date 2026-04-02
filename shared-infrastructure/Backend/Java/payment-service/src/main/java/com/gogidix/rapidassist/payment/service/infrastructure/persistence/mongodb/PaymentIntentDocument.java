package com.gogidix.rapidassist.payment.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.payment.service.domain.model.PaymentIntent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "payment_intents")
public record PaymentIntentDocument(

    @Id
    String id,

    @Field("tenant_id")
    String tenantId,

    @Field("intent_id")
    String intentId,

    @Field("currency")
    String currency,

    @Field("amount")
    BigDecimal amount,

    @Field("status")
    com.gogidix.rapidassist.payment.service.domain.model.PaymentStatus status,

    @Field("created_at")
    Instant createdAt,

    @Field("updated_at")
    Instant updatedAt,

    @Field("version")
    Long version
) {

    public static PaymentIntentDocument fromDomain(PaymentIntent intent) {
        Instant now = Instant.now();
        return new PaymentIntentDocument(
            null, // MongoDB will generate ID
            intent.tenantId(),
            intent.intentId(),
            intent.currency(),
            intent.amount(),
            intent.status(),
            intent.createdAt() != null ? intent.createdAt() : now,
            now,
            1L
        );
    }

    public static PaymentIntentDocument updateFromDomain(PaymentIntentDocument existing,
                                                        PaymentIntent intent) {
        return new PaymentIntentDocument(
            existing.id(),
            intent.tenantId(),
            intent.intentId(),
            intent.currency(),
            intent.amount(),
            intent.status(),
            existing.createdAt(),
            Instant.now(),
            existing.version() + 1
        );
    }

    public PaymentIntent toDomain() {
        return new PaymentIntent(
            tenantId(),
            intentId(),
            currency(),
            amount(),
            status(),
            createdAt()
        );
    }
}