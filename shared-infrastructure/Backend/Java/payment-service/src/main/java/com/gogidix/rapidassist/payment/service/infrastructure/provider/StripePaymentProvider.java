package com.gogidix.rapidassist.payment.service.infrastructure.provider;

import com.gogidix.rapidassist.payment.service.application.service.ComprehensivePaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.SetupIntentCreateParams;
import com.stripe.param.CustomerCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "gogidix.payment.stripe.enabled", havingValue = "true", matchIfMissing = true)
public class StripePaymentProvider {

    private static final Logger logger = LoggerFactory.getLogger(StripePaymentProvider.class);

    @Value("${gogidix.payment.stripe.secret-key:}")
    private String secretKey;

    @Value("${gogidix.payment.stripe.publishable-key:}")
    private String publishableKey;

    @Value("${gogidix.payment.stripe.webhook-secret:}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        if (secretKey != null && !secretKey.isEmpty()) {
            Stripe.apiKey = secretKey;
            logger.info("Stripe payment provider initialized");
        } else {
            logger.warn("Stripe secret key not configured - payment provider will be in test mode");
        }
    }

    public ComprehensivePaymentService.PaymentProviderResult createPaymentIntent(
            String intentId,
            BigDecimal amount,
            String currency,
            String customerId,
            String description) throws StripeException {

        // Convert amount to cents (Stripe uses smallest currency unit)
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(amountInCents)
            .setCurrency(currency.toLowerCase())
            .setCustomer(customerId)
            .setDescription(description)
            .putAllMetadata(Map.of("internal_intent_id", intentId))
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .build()
            )
            .build();

        PaymentIntent stripeIntent = PaymentIntent.create(params);

        logger.debug("Created Stripe payment intent: {}", stripeIntent.getId());

        return new ComprehensivePaymentService.PaymentProviderResult(
            stripeIntent.getId(),
            stripeIntent.getStatus(),
            stripeIntent.getClientSecret(),
            stripeIntent.getNextAction() != null,
            stripeIntent.getNextAction() != null ? stripeIntent.getNextAction().getType().toString() : null,
            Map.of(
                "stripe_intent_id", stripeIntent.getId(),
                "stripe_status", stripeIntent.getStatus()
            ),
            null // Receipt URL available after payment
        );
    }

    public ComprehensivePaymentService.PaymentProviderResult confirmPayment(
            String stripeIntentId,
            String paymentMethodId) throws StripeException {

        PaymentIntent intent = PaymentIntent.retrieve(stripeIntentId);

        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
            .setPaymentMethod(paymentMethodId)
            .build();

        PaymentIntent confirmedIntent = intent.confirm(params);

        logger.debug("Confirmed Stripe payment intent: {}, status: {}", stripeIntentId, confirmedIntent.getStatus());

        // Check for charges to get receipt URL
        String receiptUrl = null;
        // Note: Receipt URL is typically available after charge succeeds
        // In newer Stripe API versions, charges are accessed differently

        return new ComprehensivePaymentService.PaymentProviderResult(
            confirmedIntent.getId(),
            confirmedIntent.getStatus(),
            confirmedIntent.getClientSecret(),
            confirmedIntent.getNextAction() != null,
            confirmedIntent.getNextAction() != null ? confirmedIntent.getNextAction().getType().toString() : null,
            Map.of(
                "stripe_intent_id", confirmedIntent.getId(),
                "stripe_status", confirmedIntent.getStatus()
            ),
            receiptUrl
        );
    }

    public ComprehensivePaymentService.RefundProviderResult createRefund(
            String stripeIntentId,
            BigDecimal amount,
            String reason) throws StripeException {

        // Convert amount to cents
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        RefundCreateParams params = RefundCreateParams.builder()
            .setPaymentIntent(stripeIntentId)
            .setAmount(amountInCents)
            .setReason(convertReason(reason))
            .build();

        Refund refund = Refund.create(params);

        logger.debug("Created refund for intent: {}, refund ID: {}", stripeIntentId, refund.getId());

        return new ComprehensivePaymentService.RefundProviderResult(
            refund.getId(),
            refund.getStatus(),
            BigDecimal.valueOf(refund.getAmount(), 2).divide(BigDecimal.valueOf(100)),
            refund.getReason()
        );
    }

    public PaymentIntent cancelPaymentIntent(String stripeIntentId) throws StripeException {
        PaymentIntent intent = PaymentIntent.retrieve(stripeIntentId);
        PaymentIntent canceled = intent.cancel();
        logger.debug("Canceled Stripe payment intent: {}", stripeIntentId);
        return canceled;
    }

    public Customer createCustomer(String email, String name, Map<String, String> metadata) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
            .setEmail(email)
            .setName(name)
            .setMetadata(metadata)
            .build();

        Customer customer = Customer.create(params);
        logger.debug("Created Stripe customer: {}", customer.getId());
        return customer;
    }

    public SetupIntent createSetupIntent(String customerId) throws StripeException {
        SetupIntentCreateParams params = SetupIntentCreateParams.builder()
            .setCustomer(customerId)
            .setUsage(SetupIntentCreateParams.Usage.OFF_SESSION)
            .build();

        SetupIntent setupIntent = SetupIntent.create(params);
        logger.debug("Created Stripe setup intent: {}", setupIntent.getId());
        return setupIntent;
    }

    public Session createCheckoutSession(
            String customerId,
            List<SessionCreateParams.LineItem> lineItems,
            String successUrl,
            String cancelUrl,
            Map<String, String> metadata) throws StripeException {

        SessionCreateParams params = SessionCreateParams.builder()
            .setCustomer(customerId)
            .addAllLineItem(lineItems)
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(successUrl)
            .setCancelUrl(cancelUrl)
            .putAllMetadata(metadata)
            .build();

        Session session = Session.create(params);
        logger.debug("Created Stripe checkout session: {}", session.getId());
        return session;
    }

    public boolean handleWebhook(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentFailed(event);
                    break;
                case "payment_intent.requires_action":
                    handleRequiresAction(event);
                    break;
                case "charge.dispute.created":
                    handleDisputeCreated(event);
                    break;
                default:
                    logger.debug("Unhandled webhook event type: {}", event.getType());
            }

            return true;

        } catch (Exception e) {
            logger.error("Failed to handle Stripe webhook", e);
            return false;
        }
    }

    private void handlePaymentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
        logger.info("Payment succeeded: intentId={}, amount={} {}",
            paymentIntent.getId(),
            BigDecimal.valueOf(paymentIntent.getAmount(), 2).divide(BigDecimal.valueOf(100)),
            paymentIntent.getCurrency());
    }

    private void handlePaymentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
        logger.warn("Payment failed: intentId={}, reason={}",
            paymentIntent.getId(),
            paymentIntent.getLastPaymentError() != null ? paymentIntent.getLastPaymentError().getMessage() : "Unknown");
    }

    private void handleRequiresAction(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
        logger.info("Payment requires action: intentId={}, action={}",
            paymentIntent.getId(),
            paymentIntent.getNextAction() != null ? paymentIntent.getNextAction().getType() : "Unknown");
    }

    private void handleDisputeCreated(Event event) {
        Charge charge = (Charge) event.getDataObjectDeserializer().getObject().get();
        logger.warn("Charge dispute created: chargeId={}, amount={} {}",
            charge.getId(),
            BigDecimal.valueOf(charge.getAmount(), 2).divide(BigDecimal.valueOf(100)),
            charge.getCurrency());
    }

    private RefundCreateParams.Reason convertReason(String reason) {
        return switch (reason.toLowerCase()) {
            case "duplicate" -> RefundCreateParams.Reason.DUPLICATE;
            case "fraud" -> RefundCreateParams.Reason.FRAUDULENT;
            case "requested_by_customer" -> RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER;
            default -> RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER;
        };
    }
}