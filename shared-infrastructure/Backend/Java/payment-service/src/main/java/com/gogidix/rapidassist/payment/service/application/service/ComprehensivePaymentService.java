package com.gogidix.rapidassist.payment.service.application.service;

import com.gogidix.rapidassist.payment.service.domain.model.EnhancedPaymentIntent;
import com.gogidix.rapidassist.payment.service.infrastructure.persistence.mongodb.EnhancedPaymentIntentDocument;
import com.gogidix.rapidassist.payment.service.infrastructure.persistence.mongodb.EnhancedPaymentIntentRepository;
import com.gogidix.rapidassist.payment.service.infrastructure.provider.StripePaymentProvider;
import com.stripe.exception.StripeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Transactional
public class ComprehensivePaymentService {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensivePaymentService.class);

    private final EnhancedPaymentIntentRepository repository;
    private final StripePaymentProvider stripeProvider;

    public ComprehensivePaymentService(EnhancedPaymentIntentRepository repository,
                                       StripePaymentProvider stripeProvider) {
        this.repository = repository;
        this.stripeProvider = stripeProvider;
    }

    public EnhancedPaymentIntent createPaymentIntent(String tenantId, String customerId,
                                                    String currency, BigDecimal amount,
                                                    String description) {
        logger.info("Creating payment intent: tenant={}, customerId={}, amount={} {}",
            tenantId, customerId, amount, currency);

        // Create domain object
        EnhancedPaymentIntent intent = EnhancedPaymentIntent.create(
            tenantId, customerId, currency, amount, description);

        try {
            // Create payment intent with Stripe
            PaymentProviderResult result = stripeProvider.createPaymentIntent(
                intent.intentId(),
                amount,
                currency,
                customerId,
                description
            );

            // Update with Stripe response
            intent = new EnhancedPaymentIntent(
                intent.tenantId(),
                intent.intentId(),
                intent.customerId(),
                intent.currency(),
                intent.amount(),
                EnhancedPaymentIntent.PaymentStatus.REQUIRES_PAYMENT_METHOD,
                intent.paymentMethod(),
                intent.createdAt(),
                Instant.now(),
                intent.expiresAt(),
                intent.description(),
                intent.metadata(),
                intent.transactions(),
                intent.refundInfo(),
                intent.failureCode(),
                intent.failureMessage(),
                result.externalId(),
                result.clientSecret(),
                Map.of("stripe_intent_id", result.externalId()),
                intent.allowedPaymentMethods(),
                result.requiresAction(),
                result.nextAction(),
                null,
                intent.fraudCheck()
            );

            // Save to database
            EnhancedPaymentIntentDocument document = EnhancedPaymentIntentDocument.fromDomain(intent);
            repository.save(document);

            logger.info("Payment intent created successfully: tenant={}, intentId={}, stripeId={}",
                tenantId, intent.intentId(), result.externalId());
            return intent;

        } catch (StripeException e) {
            logger.error("Failed to create payment intent with Stripe: tenant={}", tenantId, e);
            throw new PaymentException("Failed to create payment intent", e);
        }
    }

    public Optional<EnhancedPaymentIntent> getPaymentIntent(String tenantId, String intentId) {
        Optional<EnhancedPaymentIntentDocument> document = repository.findByTenantIdAndIntentId(tenantId, intentId);
        return document.map(EnhancedPaymentIntentDocument::toDomain);
    }

    public EnhancedPaymentIntent confirmPayment(String tenantId, String intentId, String paymentMethodId) {
        logger.info("Confirming payment: tenant={}, intentId={}, paymentMethodId={}",
            tenantId, intentId, paymentMethodId);

        Optional<EnhancedPaymentIntentDocument> optDocument = repository.findByTenantIdAndIntentId(tenantId, intentId);
        if (optDocument.isEmpty()) {
            throw new PaymentException("Payment intent not found: " + intentId);
        }

        EnhancedPaymentIntent intent = optDocument.get().toDomain();

        try {
            PaymentProviderResult result = stripeProvider.confirmPayment(
                intent.externalId(),
                paymentMethodId
            );

            // Update intent based on result
            EnhancedPaymentIntent updated = updateIntentFromProviderResult(intent, result);

            EnhancedPaymentIntentDocument document = EnhancedPaymentIntentDocument.updateFromDomain(
                optDocument.get(), updated);
            repository.save(document);

            logger.info("Payment confirmation completed: tenant={}, intentId={}, status={}",
                tenantId, intentId, updated.status());
            return updated;

        } catch (StripeException e) {
            logger.error("Failed to confirm payment: tenant={}, intentId={}", tenantId, intentId, e);

            // Update with failure
            EnhancedPaymentIntent failed = new EnhancedPaymentIntent(
                intent.tenantId(),
                intent.intentId(),
                intent.customerId(),
                intent.currency(),
                intent.amount(),
                EnhancedPaymentIntent.PaymentStatus.FAILED,
                intent.paymentMethod(),
                intent.createdAt(),
                Instant.now(),
                intent.expiresAt(),
                intent.description(),
                intent.metadata(),
                intent.transactions(),
                intent.refundInfo(),
                "stripe_error",
                e.getMessage(),
                intent.externalId(),
                intent.clientSecret(),
                intent.paymentMethodData(),
                intent.allowedPaymentMethods(),
                intent.requireAction(),
                intent.nextAction(),
                intent.receiptInfo(),
                intent.fraudCheck()
            );

            EnhancedPaymentIntentDocument document = EnhancedPaymentIntentDocument.updateFromDomain(
                optDocument.get(), failed);
            repository.save(document);

            throw new PaymentException("Failed to confirm payment", e);
        }
    }

    public RefundResult refundPayment(String tenantId, String intentId, BigDecimal amount, String reason) {
        logger.info("Refunding payment: tenant={}, intentId={}, amount={}, reason={}",
            tenantId, intentId, amount, reason);

        Optional<EnhancedPaymentIntentDocument> optDocument = repository.findByTenantIdAndIntentId(tenantId, intentId);
        if (optDocument.isEmpty()) {
            throw new PaymentException("Payment intent not found: " + intentId);
        }

        EnhancedPaymentIntent intent = optDocument.get().toDomain();

        if (!intent.canBeRefunded() || amount.compareTo(intent.getRefundableAmount()) > 0) {
            throw new PaymentException("Payment cannot be refunded or amount exceeds refundable amount");
        }

        try {
            RefundProviderResult result = stripeProvider.createRefund(
                intent.externalId(),
                amount,
                reason
            );

            // Update refund info
            EnhancedPaymentIntent updated = updateIntentWithRefund(intent, result);

            EnhancedPaymentIntentDocument document = EnhancedPaymentIntentDocument.updateFromDomain(
                optDocument.get(), updated);
            repository.save(document);

            logger.info("Refund created: tenant={}, intentId={}, refundId={}",
                tenantId, intentId, result.externalRefundId());
            return new RefundResult(result.externalRefundId(), result.status(), amount);

        } catch (StripeException e) {
            logger.error("Failed to create refund: tenant={}, intentId={}", tenantId, intentId, e);
            throw new PaymentException("Failed to create refund", e);
        }
    }

    public List<EnhancedPaymentIntent> getCustomerPaymentHistory(String tenantId, String customerId) {
        List<EnhancedPaymentIntentDocument> documents = repository.findByTenantIdAndCustomerId(tenantId, customerId);
        return documents.stream()
            .map(EnhancedPaymentIntentDocument::toDomain)
            .toList();
    }

    public List<EnhancedPaymentIntent> getPendingActions() {
        return repository.findPendingActions(Instant.now().minusSeconds(300)).stream()
            .map(EnhancedPaymentIntentDocument::toDomain)
            .toList();
    }

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void processExpiredIntents() {
        logger.debug("Processing expired payment intents");

        List<EnhancedPaymentIntentDocument> expired = repository.findExpiredIntents(Instant.now());
        for (EnhancedPaymentIntentDocument doc : expired) {
            EnhancedPaymentIntent intent = doc.toDomain();

            // Cancel with Stripe
            try {
                stripeProvider.cancelPaymentIntent(intent.externalId());
            } catch (Exception e) {
                logger.warn("Failed to cancel expired intent with Stripe: intentId={}", intent.intentId(), e);
            }

            // Update local status
            EnhancedPaymentIntent cancelled = new EnhancedPaymentIntent(
                intent.tenantId(),
                intent.intentId(),
                intent.customerId(),
                intent.currency(),
                intent.amount(),
                EnhancedPaymentIntent.PaymentStatus.CANCELED,
                intent.paymentMethod(),
                intent.createdAt(),
                Instant.now(),
                intent.expiresAt(),
                intent.description(),
                intent.metadata(),
                intent.transactions(),
                intent.refundInfo(),
                "expired",
                "Payment intent expired",
                intent.externalId(),
                intent.clientSecret(),
                intent.paymentMethodData(),
                intent.allowedPaymentMethods(),
                intent.requireAction(),
                intent.nextAction(),
                intent.receiptInfo(),
                intent.fraudCheck()
            );

            EnhancedPaymentIntentDocument updated = EnhancedPaymentIntentDocument.updateFromDomain(doc, cancelled);
            repository.save(updated);
        }

        logger.info("Processed {} expired payment intents", expired.size());
    }

    private EnhancedPaymentIntent updateIntentFromProviderResult(EnhancedPaymentIntent intent,
                                                                PaymentProviderResult result) {
        EnhancedPaymentIntent.PaymentStatus status = switch (result.status()) {
            case "succeeded" -> EnhancedPaymentIntent.PaymentStatus.SUCCEEDED;
            case "requires_action" -> EnhancedPaymentIntent.PaymentStatus.REQUIRES_ACTION;
            case "processing" -> EnhancedPaymentIntent.PaymentStatus.PROCESSING;
            case "canceled" -> EnhancedPaymentIntent.PaymentStatus.CANCELED;
            default -> EnhancedPaymentIntent.PaymentStatus.FAILED;
        };

        // Create transaction record
        EnhancedPaymentIntent.PaymentTransaction transaction = new EnhancedPaymentIntent.PaymentTransaction(
            UUID.randomUUID().toString(),
            "stripe",
            status,
            intent.amount(),
            intent.currency(),
            Instant.now(),
            result.metadata()
        );

        return new EnhancedPaymentIntent(
            intent.tenantId(),
            intent.intentId(),
            intent.customerId(),
            intent.currency(),
            intent.amount(),
            status,
            intent.paymentMethod(),
            intent.createdAt(),
            Instant.now(),
            intent.expiresAt(),
            intent.description(),
            intent.metadata(),
            List.of(transaction),
            intent.refundInfo(),
            intent.failureCode(),
            intent.failureMessage(),
            intent.externalId(),
            intent.clientSecret(),
            intent.paymentMethodData(),
            intent.allowedPaymentMethods(),
            result.requiresAction(),
            result.nextAction(),
            result.receiptUrl() != null ? new EnhancedPaymentIntent.ReceiptInfo(
                UUID.randomUUID().toString(),
                result.receiptUrl(),
                Instant.now()
            ) : null,
            intent.fraudCheck()
        );
    }

    private EnhancedPaymentIntent updateIntentWithRefund(EnhancedPaymentIntent intent,
                                                        RefundProviderResult result) {
        EnhancedPaymentIntent.RefundTransaction refund = new EnhancedPaymentIntent.RefundTransaction(
            result.externalRefundId(),
            result.amount(),
            result.reason(),
            result.status().equals("succeeded") ? EnhancedPaymentIntent.RefundStatus.SUCCEEDED :
                result.status().equals("failed") ? EnhancedPaymentIntent.RefundStatus.FAILED :
                EnhancedPaymentIntent.RefundStatus.PENDING,
            Instant.now(),
            result.externalRefundId()
        );

        // Update refund info
        BigDecimal totalRefunded = intent.refundInfo() != null ?
            intent.refundInfo().totalRefunded().add(result.amount()) :
            result.amount();

        BigDecimal refundableAmount = intent.amount().subtract(totalRefunded);

        EnhancedPaymentIntent.RefundInfo refundInfo = new EnhancedPaymentIntent.RefundInfo(
            totalRefunded,
            refundableAmount,
            intent.refundInfo() != null ?
                Stream.concat(intent.refundInfo().refunds().stream(), Stream.of(refund)).toList() :
                List.of(refund)
        );

        EnhancedPaymentIntent.PaymentStatus newStatus = refundableAmount.compareTo(BigDecimal.ZERO) == 0 ?
            EnhancedPaymentIntent.PaymentStatus.REFUNDED :
            EnhancedPaymentIntent.PaymentStatus.PARTIALLY_REFUNDED;

        return new EnhancedPaymentIntent(
            intent.tenantId(),
            intent.intentId(),
            intent.customerId(),
            intent.currency(),
            intent.amount(),
            newStatus,
            intent.paymentMethod(),
            intent.createdAt(),
            Instant.now(),
            intent.expiresAt(),
            intent.description(),
            intent.metadata(),
            intent.transactions(),
            refundInfo,
            intent.failureCode(),
            intent.failureMessage(),
            intent.externalId(),
            intent.clientSecret(),
            intent.paymentMethodData(),
            intent.allowedPaymentMethods(),
            intent.requireAction(),
            intent.nextAction(),
            intent.receiptInfo(),
            intent.fraudCheck()
        );
    }

    public record PaymentProviderResult(
        String externalId,
        String status,
        String clientSecret,
        boolean requiresAction,
        String nextAction,
        Map<String, String> metadata,
        String receiptUrl
    ) {}

    public record RefundProviderResult(
        String externalRefundId,
        String status,
        BigDecimal amount,
        String reason
    ) {}

    public record RefundResult(
        String refundId,
        String status,
        BigDecimal amount
    ) {}

    public static class PaymentException extends RuntimeException {
        public PaymentException(String message) {
            super(message);
        }

        public PaymentException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}