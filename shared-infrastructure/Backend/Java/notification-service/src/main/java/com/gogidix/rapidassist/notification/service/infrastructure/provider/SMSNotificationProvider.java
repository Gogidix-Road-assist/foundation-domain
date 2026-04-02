package com.gogidix.rapidassist.notification.service.infrastructure.provider;

import com.gogidix.rapidassist.notification.service.domain.model.Notification;
import com.gogidix.rapidassist.notification.service.domain.port.out.NotificationProvider;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Component
@ConditionalOnProperty(name = "gogidix.notification.sms.provider", havingValue = "twilio", matchIfMissing = true)
public class SMSNotificationProvider implements NotificationProvider {

    private static final Logger logger = LoggerFactory.getLogger(SMSNotificationProvider.class);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    @Value("${gogidix.notification.sms.twilio.account-sid:}")
    private String accountSid;

    @Value("${gogidix.notification.sms.twilio.auth-token:}")
    private String authToken;

    @Value("${gogidix.notification.sms.from-number:}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        if (accountSid != null && authToken != null && !accountSid.isEmpty() && !authToken.isEmpty()) {
            Twilio.init(accountSid, authToken);
            logger.info("Twilio SMS provider initialized with from number: {}", fromNumber);
        } else {
            logger.warn("Twilio credentials not configured - SMS provider will be in test mode");
        }
    }

    @Override
    public boolean supportsChannel(Notification.NotificationChannel channel) {
        return channel == Notification.NotificationChannel.SMS;
    }

    @Override
    public CompletableFuture<DeliveryResponse> send(Notification notification, String recipient) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Validate phone number
                if (!PHONE_PATTERN.matcher(recipient).matches()) {
                    logger.warn("Invalid phone number format: {}", recipient);
                    return new SMSDeliveryResponse(
                        null,
                        DeliveryStatus.FAILED,
                        "INVALID_PHONE",
                        "Invalid phone number format",
                        null
                    );
                }

                if (accountSid == null || authToken == null) {
                    logger.warn("Twilio not configured - simulating SMS send to: {}", recipient);
                    return new SMSDeliveryResponse(
                        "simulated_" + System.currentTimeMillis(),
                        DeliveryStatus.SENT,
                        null,
                        null,
                        "Twilio not configured - simulated response"
                    );
                }

                Message message = Message.creator(
                    new PhoneNumber(recipient),
                    new PhoneNumber(fromNumber),
                    notification.content()
                ).create();

                logger.info("SMS sent successfully: recipient={}, notificationId={}, sid={}",
                    recipient, notification.notificationId(), message.getSid());

                DeliveryStatus status = switch (message.getStatus().toString()) {
                    case "queued", "accepted", "sending" -> DeliveryStatus.SENT;
                    case "sent", "delivered" -> DeliveryStatus.DELIVERED;
                    case "failed", "undelivered" -> DeliveryStatus.FAILED;
                    default -> DeliveryStatus.PENDING;
                };

                return new SMSDeliveryResponse(
                    message.getSid(),
                    status,
                    message.getErrorCode() != null ? message.getErrorCode().toString() : null,
                    message.getErrorMessage(),
                    null  // toJson() not available in current Twilio version
                );

            } catch (com.twilio.exception.ApiException e) {
                logger.error("Twilio API error sending SMS to: {}", recipient, e);
                return new SMSDeliveryResponse(
                    null,
                    DeliveryStatus.FAILED,
                    String.valueOf(e.getCode()),
                    e.getMessage(),
                    null  // getResponseBody() not available in current Twilio version
                );
            } catch (Exception e) {
                logger.error("Unexpected error sending SMS to: {}", recipient, e);
                return new SMSDeliveryResponse(
                    null,
                    DeliveryStatus.FAILED,
                    "UNKNOWN_ERROR",
                    e.getMessage(),
                    null
                );
            }
        });
    }

    @Override
    public CompletableFuture<DeliveryStatus> getStatus(String messageId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (accountSid == null || authToken == null) {
                    return DeliveryStatus.SENT;
                }

                Message message = Message.fetcher(messageId).fetch();
                return switch (message.getStatus().toString()) {
                    case "queued", "accepted", "sending" -> DeliveryStatus.PENDING;
                    case "sent" -> DeliveryStatus.SENT;
                    case "delivered" -> DeliveryStatus.DELIVERED;
                    case "failed", "undelivered" -> DeliveryStatus.FAILED;
                    default -> DeliveryStatus.PENDING;
                };
            } catch (Exception e) {
                logger.error("Error fetching SMS status for message: {}", messageId, e);
                return DeliveryStatus.FAILED;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> cancel(String messageId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (accountSid == null || authToken == null) {
                    logger.warn("Twilio not configured - cannot cancel message: {}", messageId);
                    return false;
                }

                // Check if message can be canceled (only if it's still in queue)
                Message message = Message.fetcher(messageId).fetch();
                if ("queued".equals(message.getStatus().toString())) {
                    // Use the updater without status - cancellation is done by deleting the message
                    Message.deleter(messageId).delete();
                    logger.info("SMS message canceled: {}", messageId);
                    return true;
                }
                return false;
            } catch (Exception e) {
                logger.error("Error canceling SMS message: {}", messageId, e);
                return false;
            }
        });
    }

    private record SMSDeliveryResponse(
        String messageId,
        DeliveryStatus status,
        String errorCode,
        String errorMessage,
        Object providerResponse
    ) implements DeliveryResponse {

        @Override
        public String getMessageId() { return messageId; }

        @Override
        public DeliveryStatus getStatus() { return status; }

        @Override
        public String getErrorCode() { return errorCode; }

        @Override
        public String getErrorMessage() { return errorMessage; }

        @Override
        public Object getProviderResponse() { return providerResponse; }
    }
}