package com.gogidix.rapidassist.payment.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.payment.service.domain.model.EnhancedPaymentIntent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "enhanced_payment_intents")
public record EnhancedPaymentIntentDocument(

    @Id
    String id,

    @Field("tenant_id")
    String tenantId,

    @Field("intent_id")
    String intentId,

    @Field("customer_id")
    String customerId,

    @Field("currency")
    String currency,

    @Field("amount")
    java.math.BigDecimal amount,

    @Field("status")
    EnhancedPaymentIntent.PaymentStatus status,

    @Field("payment_method")
    EnhancedPaymentIntent.PaymentMethod paymentMethod,

    @Field("created_at")
    Instant createdAt,

    @Field("updated_at")
    Instant updatedAt,

    @Field("expires_at")
    Instant expiresAt,

    @Field("description")
    String description,

    @Field("metadata")
    Map<String, String> metadata,

    @Field("transactions")
    List<PaymentTransactionEmbedded> transactions,

    @Field("refund_info")
    RefundInfoEmbedded refundInfo,

    @Field("failure_code")
    String failureCode,

    @Field("failure_message")
    String failureMessage,

    @Field("external_id")
    String externalId,

    @Field("client_secret")
    String clientSecret,

    @Field("payment_method_data")
    Map<String, Object> paymentMethodData,

    @Field("allowed_payment_methods")
    List<String> allowedPaymentMethods,

    @Field("require_action")
    boolean requireAction,

    @Field("next_action")
    String nextAction,

    @Field("receipt_info")
    ReceiptInfoEmbedded receiptInfo,

    @Field("fraud_check")
    FraudCheckEmbedded fraudCheck,

    @Field("version")
    Long versionNumber
) {

    public static EnhancedPaymentIntentDocument fromDomain(EnhancedPaymentIntent intent) {
        return new EnhancedPaymentIntentDocument(
            null, // MongoDB will generate ID
            intent.tenantId(),
            intent.intentId(),
            intent.customerId(),
            intent.currency(),
            intent.amount(),
            intent.status(),
            intent.paymentMethod(),
            intent.createdAt(),
            intent.updatedAt(),
            intent.expiresAt(),
            intent.description(),
            intent.metadata(),
            intent.transactions().stream()
                .map(t -> new PaymentTransactionEmbedded(
                    t.transactionId(),
                    t.gateway(),
                    t.status(),
                    t.amount(),
                    t.currency(),
                    t.timestamp(),
                    t.gatewayResponse()
                ))
                .toList(),
            intent.refundInfo() != null ? new RefundInfoEmbedded(
                intent.refundInfo().totalRefunded(),
                intent.refundInfo().refundableAmount(),
                intent.refundInfo().refunds().stream()
                    .map(r -> new RefundTransactionEmbedded(
                        r.refundId(),
                        r.amount(),
                        r.reason(),
                        r.status(),
                        r.createdAt(),
                        r.externalRefundId()
                    ))
                    .toList()
            ) : null,
            intent.failureCode(),
            intent.failureMessage(),
            intent.externalId(),
            intent.clientSecret(),
            intent.paymentMethodData(),
            intent.allowedPaymentMethods(),
            intent.requireAction(),
            intent.nextAction(),
            intent.receiptInfo() != null ? new ReceiptInfoEmbedded(
                intent.receiptInfo().receiptId(),
                intent.receiptInfo().receiptUrl(),
                intent.receiptInfo().receiptCreatedAt()
            ) : null,
            new FraudCheckEmbedded(
                intent.fraudCheck().status(),
                intent.fraudCheck().riskScore(),
                intent.fraudCheck().details()
            ),
            1L
        );
    }

    public static EnhancedPaymentIntentDocument updateFromDomain(EnhancedPaymentIntentDocument existing,
                                                               EnhancedPaymentIntent intent) {
        return new EnhancedPaymentIntentDocument(
            existing.id(),
            intent.tenantId(),
            intent.intentId(),
            intent.customerId(),
            intent.currency(),
            intent.amount(),
            intent.status(),
            intent.paymentMethod(),
            existing.createdAt(),
            intent.updatedAt(),
            intent.expiresAt(),
            intent.description(),
            intent.metadata(),
            intent.transactions().stream()
                .map(t -> new PaymentTransactionEmbedded(
                    t.transactionId(),
                    t.gateway(),
                    t.status(),
                    t.amount(),
                    t.currency(),
                    t.timestamp(),
                    t.gatewayResponse()
                ))
                .toList(),
            intent.refundInfo() != null ? new RefundInfoEmbedded(
                intent.refundInfo().totalRefunded(),
                intent.refundInfo().refundableAmount(),
                intent.refundInfo().refunds().stream()
                    .map(r -> new RefundTransactionEmbedded(
                        r.refundId(),
                        r.amount(),
                        r.reason(),
                        r.status(),
                        r.createdAt(),
                        r.externalRefundId()
                    ))
                    .toList()
            ) : null,
            intent.failureCode(),
            intent.failureMessage(),
            intent.externalId(),
            intent.clientSecret(),
            intent.paymentMethodData(),
            intent.allowedPaymentMethods(),
            intent.requireAction(),
            intent.nextAction(),
            intent.receiptInfo() != null ? new ReceiptInfoEmbedded(
                intent.receiptInfo().receiptId(),
                intent.receiptInfo().receiptUrl(),
                intent.receiptInfo().receiptCreatedAt()
            ) : null,
            new FraudCheckEmbedded(
                intent.fraudCheck().status(),
                intent.fraudCheck().riskScore(),
                intent.fraudCheck().details()
            ),
            existing.versionNumber() + 1
        );
    }

    public EnhancedPaymentIntent toDomain() {
        return new EnhancedPaymentIntent(
            tenantId(),
            intentId(),
            customerId(),
            currency(),
            amount(),
            status(),
            paymentMethod(),
            createdAt(),
            updatedAt(),
            expiresAt(),
            description(),
            metadata(),
            transactions().stream()
                .map(t -> new EnhancedPaymentIntent.PaymentTransaction(
                    t.transactionId(),
                    t.gateway(),
                    t.status(),
                    t.amount(),
                    t.currency(),
                    t.timestamp(),
                    t.gatewayResponse()
                ))
                .toList(),
            refundInfo() != null ? new EnhancedPaymentIntent.RefundInfo(
                refundInfo().totalRefunded(),
                refundInfo().refundableAmount(),
                refundInfo().refunds().stream()
                    .map(r -> new EnhancedPaymentIntent.RefundTransaction(
                        r.refundId(),
                        r.amount(),
                        r.reason(),
                        r.status(),
                        r.createdAt(),
                        r.externalRefundId()
                    ))
                    .toList()
            ) : null,
            failureCode(),
            failureMessage(),
            externalId(),
            clientSecret(),
            paymentMethodData(),
            allowedPaymentMethods(),
            requireAction(),
            nextAction(),
            receiptInfo() != null ? new EnhancedPaymentIntent.ReceiptInfo(
                receiptInfo().receiptId(),
                receiptInfo().receiptUrl(),
                receiptInfo().receiptCreatedAt()
            ) : null,
            new EnhancedPaymentIntent.FraudCheckResult(
                fraudCheck().status(),
                fraudCheck().riskScore(),
                fraudCheck().details()
            )
        );
    }

    public record PaymentTransactionEmbedded(
        String transactionId,
        String gateway,
        EnhancedPaymentIntent.PaymentStatus status,
        java.math.BigDecimal amount,
        String currency,
        Instant timestamp,
        Map<String, String> gatewayResponse
    ) {}

    public record RefundInfoEmbedded(
        java.math.BigDecimal totalRefunded,
        java.math.BigDecimal refundableAmount,
        List<RefundTransactionEmbedded> refunds
    ) {}

    public record RefundTransactionEmbedded(
        String refundId,
        java.math.BigDecimal amount,
        String reason,
        EnhancedPaymentIntent.RefundStatus status,
        Instant createdAt,
        String externalRefundId
    ) {}

    public record ReceiptInfoEmbedded(
        String receiptId,
        String receiptUrl,
        Instant receiptCreatedAt
    ) {}

    public record FraudCheckEmbedded(
        EnhancedPaymentIntent.FraudCheckStatus status,
        double riskScore,
        Map<String, Object> details
    ) {}
}