package com.gogidix.rapidassist.payment.service.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record EnhancedPaymentIntent(
    String tenantId,
    String intentId,
    String customerId,
    String currency,
    BigDecimal amount,
    PaymentStatus status,
    PaymentMethod paymentMethod,
    Instant createdAt,
    Instant updatedAt,
    Instant expiresAt,
    String description,
    Map<String, String> metadata,
    List<PaymentTransaction> transactions,
    RefundInfo refundInfo,
    String failureCode,
    String failureMessage,
    String externalId, // External payment gateway ID
    String clientSecret,
    Map<String, Object> paymentMethodData,
    List<String> allowedPaymentMethods,
    boolean requireAction,
    String nextAction,
    ReceiptInfo receiptInfo,
    FraudCheckResult fraudCheck
) {

    public static EnhancedPaymentIntent create(String tenantId, String customerId, String currency,
                                             BigDecimal amount, String description) {
        return new EnhancedPaymentIntent(
            tenantId,
            generateIntentId(),
            customerId,
            currency,
            amount,
            PaymentStatus.CREATED,
            PaymentMethod.CARD,
            Instant.now(),
            Instant.now(),
            Instant.now().plusSeconds(1800), // 30 minutes default expiry
            description,
            Map.of(),
            List.of(),
            null,
            null,
            null,
            null,
            null,
            Map.of(),
            List.of("card", "sepa_debit"),
            false,
            null,
            null,
            new FraudCheckResult(FraudCheckStatus.PENDING, 0.0, Map.of())
        );
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean canBeCaptured() {
        return status == PaymentStatus.AUTHORIZED && !isExpired();
    }

    public boolean canBeRefunded() {
        if (refundInfo == null) return false;
        BigDecimal refundableAmount = refundInfo.refundableAmount();
        return refundableAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getRefundableAmount() {
        if (refundInfo == null) return BigDecimal.ZERO;
        return refundInfo.refundableAmount();
    }

    private static String generateIntentId() {
        return "pi_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    public enum PaymentStatus {
        CREATED,
        REQUIRES_PAYMENT_METHOD,
        REQUIRES_CONFIRMATION,
        REQUIRES_ACTION,
        PROCESSING,
        AUTHORIZED,
        SUCCEEDED,
        CANCELED,
        FAILED,
        PARTIALLY_REFUNDED,
        REFUNDED,
        DISPUTED
    }

    public enum PaymentMethod {
        CARD,
        SEPA_DEBIT,
        IDEAL,
        BANCONTACT,
        GIROPAY,
        SOFORT,
        KLARNA,
        PAYPAL,
        APPLE_PAY,
        GOOGLE_PAY,
        BANK_TRANSFER
    }

    public record PaymentTransaction(
        String transactionId,
        String gateway,
        PaymentStatus status,
        BigDecimal amount,
        String currency,
        Instant timestamp,
        Map<String, String> gatewayResponse
    ) {}

    public record RefundInfo(
        BigDecimal totalRefunded,
        BigDecimal refundableAmount,
        List<RefundTransaction> refunds
    ) {}

    public record RefundTransaction(
        String refundId,
        BigDecimal amount,
        String reason,
        RefundStatus status,
        Instant createdAt,
        String externalRefundId
    ) {}

    public enum RefundStatus {
        PENDING,
        SUCCEEDED,
        FAILED,
        CANCELED
    }

    public record ReceiptInfo(
        String receiptId,
        String receiptUrl,
        Instant receiptCreatedAt
    ) {}

    public record FraudCheckResult(
        FraudCheckStatus status,
        double riskScore,
        Map<String, Object> details
    ) {}

    public enum FraudCheckStatus {
        PENDING,
        APPROVED,
        REJECTED,
        MANUAL_REVIEW
    }
}