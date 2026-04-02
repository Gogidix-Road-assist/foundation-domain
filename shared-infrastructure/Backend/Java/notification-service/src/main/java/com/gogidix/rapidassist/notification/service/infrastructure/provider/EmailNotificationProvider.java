package com.gogidix.rapidassist.notification.service.infrastructure.provider;

import com.gogidix.rapidassist.notification.service.domain.model.Notification;
import com.gogidix.rapidassist.notification.service.domain.port.out.NotificationProvider;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
@ConditionalOnProperty(name = "gogidix.notification.email.provider", havingValue = "sendgrid", matchIfMissing = true)
public class EmailNotificationProvider implements NotificationProvider {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationProvider.class);

    @Value("${gogidix.notification.email.sendgrid.api-key:}")
    private String sendGridApiKey;

    @Value("${gogidix.notification.email.from-email:}")
    private String fromEmail;

    @Value("${gogidix.notification.email.from-name:}")
    private String fromName;

    private SendGrid sendGrid;

    @PostConstruct
    public void init() {
        if (sendGridApiKey != null && !sendGridApiKey.isEmpty()) {
            sendGrid = new SendGrid(sendGridApiKey);
            logger.info("SendGrid email provider initialized");
        } else {
            logger.warn("SendGrid API key not configured - email provider will be in test mode");
        }
    }

    @Override
    public boolean supportsChannel(Notification.NotificationChannel channel) {
        return channel == Notification.NotificationChannel.EMAIL;
    }

    @Override
    public CompletableFuture<DeliveryResponse> send(Notification notification, String recipient) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (sendGrid == null) {
                    logger.warn("SendGrid not configured - simulating email send to: {}", recipient);
                    return new EmailDeliveryResponse(
                        "simulated_" + System.currentTimeMillis(),
                        DeliveryStatus.SENT,
                        null,
                        null,
                        "SendGrid not configured - simulated response"
                    );
                }

                Email from = new Email(fromEmail, fromName);
                Email to = new Email(recipient);

                Content content;
                if (notification.metadata() != null && notification.metadata().containsKey("htmlContent")) {
                    content = new Content("text/html", (String) notification.metadata().get("htmlContent"));
                } else {
                    content = new Content("text/plain", notification.content());
                }

                Mail mail = new Mail(from, notification.subject(), to, content);

                // Add personalization if needed
                Personalization personalization = new Personalization();
                personalization.addTo(to);

                // Add template data if present
                if (notification.metadata() != null) {
                    notification.metadata().forEach((key, value) -> {
                        if (value instanceof String) {
                            personalization.addDynamicTemplateData(key, value);
                        }
                    });
                }

                mail.addPersonalization(personalization);

                Request request = new Request();
                request.setMethod(Method.POST);
                request.setEndpoint("mail/send");
                request.setBody(mail.build());

                Response response = sendGrid.api(request);

                if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                    logger.info("Email sent successfully: recipient={}, notificationId={}",
                        recipient, notification.notificationId());

                    // Extract message ID from response headers if available
                    String messageId = response.getHeaders() != null ?
                        response.getHeaders().getOrDefault("X-Message-Id", "generated_" + System.currentTimeMillis()) :
                        "generated_" + System.currentTimeMillis();

                    return new EmailDeliveryResponse(
                        messageId,
                        DeliveryStatus.SENT,
                        null,
                        null,
                        response.getBody()
                    );
                } else {
                    logger.error("Failed to send email: recipient={}, statusCode={}, body={}",
                        recipient, response.getStatusCode(), response.getBody());

                    return new EmailDeliveryResponse(
                        null,
                        DeliveryStatus.FAILED,
                        String.valueOf(response.getStatusCode()),
                        "SendGrid API error: " + response.getBody(),
                        response.getBody()
                    );
                }

            } catch (IOException e) {
                logger.error("IO error sending email to: {}", recipient, e);
                return new EmailDeliveryResponse(
                    null,
                    DeliveryStatus.FAILED,
                    "IO_ERROR",
                    e.getMessage(),
                    null
                );
            } catch (Exception e) {
                logger.error("Unexpected error sending email to: {}", recipient, e);
                return new EmailDeliveryResponse(
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
            // In a real implementation, you would query SendGrid for message status
            // For now, return SENT as default
            logger.debug("Getting status for email message: {}", messageId);
            return DeliveryStatus.SENT;
        });
    }

    @Override
    public CompletableFuture<Boolean> cancel(String messageId) {
        return CompletableFuture.supplyAsync(() -> {
            logger.warn("Cancel operation not supported for email messages: {}", messageId);
            return false;
        });
    }

    private record EmailDeliveryResponse(
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