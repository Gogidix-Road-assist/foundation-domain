package com.gogidix.rapidassist.notification.service.application.service;

import com.gogidix.rapidassist.notification.service.domain.model.*;
import com.gogidix.rapidassist.notification.service.domain.port.in.NotificationCommand;
import com.gogidix.rapidassist.notification.service.domain.port.out.NotificationProvider;
import com.gogidix.rapidassist.notification.service.domain.port.out.NotificationStore;
import com.gogidix.rapidassist.notification.service.domain.port.out.TemplateStore;
import com.gogidix.rapidassist.notification.service.infrastructure.persistence.mongodb.NotificationRepository;
import com.gogidix.rapidassist.notification.service.infrastructure.persistence.mongodb.TemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComprehensiveNotificationService implements NotificationCommand {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveNotificationService.class);

    private final NotificationStore notificationStore;
    private final TemplateStore templateStore;
    private final List<NotificationProvider> providers;
    private final Map<Notification.NotificationChannel, NotificationProvider> providerMap;

    public ComprehensiveNotificationService(NotificationStore notificationStore,
                                           TemplateStore templateStore,
                                           List<NotificationProvider> providers) {
        this.notificationStore = notificationStore;
        this.templateStore = templateStore;
        this.providers = providers;
        this.providerMap = new ConcurrentHashMap<>();

        // Initialize provider mapping
        for (NotificationProvider provider : providers) {
            for (Notification.NotificationChannel channel : Notification.NotificationChannel.values()) {
                if (provider.supportsChannel(channel) && !providerMap.containsKey(channel)) {
                    providerMap.put(channel, provider);
                    logger.debug("Registered provider for channel: {}", channel);
                }
            }
        }
    }

    @Override
    public Notification sendImmediate(String tenantId, Notification.NotificationType type,
                                   List<String> recipients, String subject, String content,
                                   List<Notification.NotificationChannel> channels) {
        logger.info("Sending immediate notification: tenant={}, type={}, recipients={}",
            tenantId, type, recipients.size());

        Notification notification = Notification.create(tenantId, type, recipients, subject, content);
        notification = new Notification(
            tenantId,
            notification.notificationId(),
            type,
            recipients,
            subject,
            content,
            notification.metadata(),
            Notification.NotificationStatus.PROCESSING,
            notification.createdAt(),
            notification.scheduledAt(),
            notification.sentAt(),
            notification.templateId(),
            channels,
            notification.deliveryResult(),
            notification.retryCount(),
            notification.externalId(),
            notification.tags()
        );

        notification = notificationStore.save(notification);
        deliverNotification(notification);
        return notification;
    }

    @Override
    public Notification sendFromTemplate(String tenantId, String templateId,
                                       List<String> recipients, Map<String, Object> templateData,
                                       List<Notification.NotificationChannel> channels) {
        logger.info("Sending notification from template: tenant={}, templateId={}, recipients={}",
            tenantId, templateId, recipients.size());

        Optional<NotificationTemplate> optTemplate = templateStore.find(tenantId, templateId);
        if (optTemplate.isEmpty()) {
            throw new NotificationServiceException("Template not found: " + templateId);
        }

        NotificationTemplate template = optTemplate.get();

        // Process template for each channel
        Map<Notification.NotificationChannel, String> processedContent = new HashMap<>();
        String processedSubject = processTemplateVariables(templateData, template.defaultVariables());

        for (Notification.NotificationChannel channel : channels) {
            NotificationTemplate.TemplateContent content = template.getContentForChannel(channel);
            if (content != null) {
                processedContent.put(channel, processTemplate(templateData, content));
            }
        }

        // Create notification with processed content
        Notification notification = Notification.create(tenantId, template.type(), recipients,
                                                        processedSubject, processedContent.values().iterator().next());

        notification = new Notification(
            tenantId,
            notification.notificationId(),
            template.type(),
            recipients,
            processedSubject,
            processedContent.values().iterator().next(),
            templateData,
            Notification.NotificationStatus.PROCESSING,
            notification.createdAt(),
            notification.scheduledAt(),
            notification.sentAt(),
            templateId,
            channels,
            notification.deliveryResult(),
            notification.retryCount(),
            notification.externalId(),
            notification.tags()
        );

        notification = notificationStore.save(notification);
        deliverNotification(notification);
        return notification;
    }

    @Override
    public Notification scheduleNotification(String tenantId, Notification.NotificationType type,
                                           List<String> recipients, String subject, String content,
                                           Instant scheduledAt,
                                           List<Notification.NotificationChannel> channels) {
        logger.info("Scheduling notification: tenant={}, type={}, scheduledAt={}",
            tenantId, type, scheduledAt);

        Notification notification = Notification.create(tenantId, type, recipients, subject, content)
            .scheduleFor(scheduledAt);

        notification = new Notification(
            tenantId,
            notification.notificationId(),
            type,
            recipients,
            subject,
            content,
            notification.metadata(),
            Notification.NotificationStatus.SCHEDULED,
            notification.createdAt(),
            scheduledAt,
            notification.sentAt(),
            notification.templateId(),
            channels,
            notification.deliveryResult(),
            notification.retryCount(),
            notification.externalId(),
            notification.tags()
        );

        return notificationStore.save(notification);
    }

    @Override
    public boolean cancelNotification(String tenantId, String notificationId) {
        logger.info("Canceling notification: tenant={}, notificationId={}", tenantId, notificationId);

        Optional<Notification> optNotification = notificationStore.find(tenantId, notificationId);
        if (optNotification.isEmpty()) {
            return false;
        }

        Notification notification = optNotification.get();

        // Can only cancel scheduled or pending notifications
        if (notification.status() == Notification.NotificationStatus.SCHEDULED ||
            notification.status() == Notification.NotificationStatus.PENDING) {

            Notification canceled = new Notification(
                tenantId,
                notificationId,
                notification.type(),
                notification.recipients(),
                notification.subject(),
                notification.content(),
                notification.metadata(),
                Notification.NotificationStatus.CANCELLED,
                notification.createdAt(),
                notification.scheduledAt(),
                Instant.now(),
                notification.templateId(),
                notification.channels(),
                notification.deliveryResult(),
                notification.retryCount(),
                notification.externalId(),
                notification.tags()
            );

            notificationStore.save(canceled);
            return true;
        }

        return false;
    }

    @Override
    public Notification resendNotification(String tenantId, String notificationId) {
        logger.info("Resending notification: tenant={}, notificationId={}", tenantId, notificationId);

        Optional<Notification> optNotification = notificationStore.find(tenantId, notificationId);
        if (optNotification.isEmpty()) {
            throw new NotificationServiceException("Notification not found: " + notificationId);
        }

        Notification notification = optNotification.get();

        // Only resend failed or partially delivered notifications
        if (!notification.canRetry()) {
            throw new NotificationServiceException("Notification cannot be retried: " + notificationId);
        }

        Notification retryNotification = new Notification(
            tenantId,
            notification.notificationId(),
            notification.type(),
            notification.recipients(),
            notification.subject(),
            notification.content(),
            notification.metadata(),
            Notification.NotificationStatus.PROCESSING,
            notification.createdAt(),
            notification.scheduledAt(),
            notification.sentAt(),
            notification.templateId(),
            notification.channels(),
            notification.deliveryResult(),
            notification.retryCount() + 1,
            notification.externalId(),
            notification.tags()
        );

        notificationStore.save(retryNotification);
        deliverNotification(retryNotification);
        return retryNotification;
    }

    @Override
    public List<Notification> getPendingNotifications(String tenantId) {
        return notificationStore.findByStatus(tenantId, Notification.NotificationStatus.PENDING);
    }

    @Override
    public List<Notification> getScheduledNotifications(String tenantId) {
        return notificationStore.findByStatus(tenantId, Notification.NotificationStatus.SCHEDULED);
    }

    @Override
    public Map<Notification.NotificationStatus, Long> getNotificationStatistics(String tenantId,
                                                                               Instant from,
                                                                               Instant to) {
        List<Notification> notifications = notificationStore.findByDateRange(tenantId, from, to);

        return notifications.stream()
            .collect(Collectors.groupingBy(
                Notification::status,
                Collectors.counting()
            ));
    }

    // Internal method to deliver notification
    private void deliverNotification(Notification notification) {
        logger.debug("Delivering notification: {}", notification.notificationId());

        Map<Notification.NotificationChannel, CompletableFuture<NotificationProvider.DeliveryResponse>> futures = new HashMap<>();

        // Send through each channel
        for (Notification.NotificationChannel channel : notification.channels()) {
            NotificationProvider provider = providerMap.get(channel);
            if (provider == null) {
                logger.warn("No provider found for channel: {}", channel);
                continue;
            }

            // Send to each recipient
            for (String recipient : notification.recipients()) {
                CompletableFuture<NotificationProvider.DeliveryResponse> future = provider.send(notification, recipient);
                futures.put(channel, future);
            }
        }

        // Wait for all deliveries to complete and update notification
        CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0]))
            .thenRun(() -> {
                Map<Notification.NotificationChannel, Notification.DeliveryResult.ChannelDeliveryResult> channelResults = new HashMap<>();
                int totalRecipients = notification.recipients().size();
                int successfulDeliveries = 0;
                int failedDeliveries = 0;

                for (Map.Entry<Notification.NotificationChannel, CompletableFuture<NotificationProvider.DeliveryResponse>> entry : futures.entrySet()) {
                    try {
                        NotificationProvider.DeliveryResponse response = entry.getValue().get();
                        Notification.DeliveryResult.DeliveryStatus status = convertDeliveryStatus(response.getStatus());

                        if (status == Notification.DeliveryResult.DeliveryStatus.DELIVERED ||
                            status == Notification.DeliveryResult.DeliveryStatus.SENT) {
                            successfulDeliveries++;
                        } else {
                            failedDeliveries++;
                        }

                        Notification.DeliveryResult.ChannelDeliveryResult channelResult =
                            new Notification.DeliveryResult.ChannelDeliveryResult(
                                entry.getKey(),
                                entry.getKey().name(),
                                status,
                                response.getMessageId(),
                                response.getErrorCode(),
                                response.getErrorMessage(),
                                Instant.now(),
                                null,
                                null,
                                Map.of()
                            );

                        channelResults.put(entry.getKey(), channelResult);

                    } catch (Exception e) {
                        logger.error("Error processing delivery result for channel: {}", entry.getKey(), e);
                        failedDeliveries++;
                    }
                }

                // Create delivery result
                Notification.DeliveryResult deliveryResult = new Notification.DeliveryResult(
                    UUID.randomUUID().toString(),
                    channelResults,
                    Instant.now(),
                    totalRecipients,
                    successfulDeliveries,
                    failedDeliveries,
                    Map.of()
                );

                // Update notification status
                Notification.NotificationStatus newStatus = failedDeliveries == 0 ?
                    Notification.NotificationStatus.SENT :
                    successfulDeliveries > 0 ?
                    Notification.NotificationStatus.PARTIALLY_DELIVERED :
                    Notification.NotificationStatus.FAILED;

                Notification updated = new Notification(
                    notification.tenantId(),
                    notification.notificationId(),
                    notification.type(),
                    notification.recipients(),
                    notification.subject(),
                    notification.content(),
                    notification.metadata(),
                    newStatus,
                    notification.createdAt(),
                    notification.scheduledAt(),
                    Instant.now(),
                    notification.templateId(),
                    notification.channels(),
                    deliveryResult,
                    notification.retryCount(),
                    notification.externalId(),
                    notification.tags()
                );

                notificationStore.save(updated);
                logger.info("Notification delivery completed: {}, status={}, success={}, failed={}",
                    notification.notificationId(), newStatus, successfulDeliveries, failedDeliveries);
            });
    }

    // Scheduled task to process scheduled notifications
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void processScheduledNotifications() {
        logger.debug("Processing scheduled notifications");

        List<Notification> scheduled = notificationStore.findScheduledNotificationsReadyToSend();
        for (Notification notification : scheduled) {
            if (notification.status() == Notification.NotificationStatus.SCHEDULED) {
                logger.info("Processing scheduled notification: {}", notification.notificationId());
                deliverNotification(notification);
            }
        }
    }

    // Scheduled task to retry failed notifications
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void processFailedNotifications() {
        logger.debug("Processing failed notifications for retry");

        List<Notification> failed = notificationStore.findNotificationsForRetry(null, 3);
        for (Notification notification : failed) {
            if (notification.canRetry()) {
                logger.info("Retrying failed notification: {}", notification.notificationId());
                deliverNotification(notification);
            }
        }
    }

    private String processTemplate(Map<String, Object> data, String template) {
        String result = template;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", String.valueOf(entry.getValue()));
        }
        return result;
    }

    private String processTemplateVariables(Map<String, Object> data, Map<String, String> defaultVariables) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : defaultVariables.entrySet()) {
            String value = String.valueOf(data.getOrDefault(entry.getKey(), entry.getValue()));
            result.append(entry.getKey()).append(": ").append(value).append("\n");
        }
        return result.toString();
    }

    private String processTemplate(Map<String, Object> data, NotificationTemplate.TemplateContent content) {
        return processTemplate(data, content.body());
    }

    private Notification.DeliveryResult.DeliveryStatus convertDeliveryStatus(NotificationProvider.DeliveryStatus status) {
        return switch (status) {
            case SENT -> Notification.DeliveryResult.DeliveryStatus.SENT;
            case DELIVERED -> Notification.DeliveryResult.DeliveryStatus.DELIVERED;
            case FAILED -> Notification.DeliveryResult.DeliveryStatus.FAILED;
            case PENDING -> Notification.DeliveryResult.DeliveryStatus.PENDING;
        };
    }

    public static class NotificationServiceException extends RuntimeException {
        public NotificationServiceException(String message) {
            super(message);
        }

        public NotificationServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}