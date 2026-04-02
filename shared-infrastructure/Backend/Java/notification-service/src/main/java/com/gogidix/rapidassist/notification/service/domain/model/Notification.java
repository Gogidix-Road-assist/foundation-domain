package com.gogidix.rapidassist.notification.service.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record Notification(
    String tenantId,
    String notificationId,
    NotificationType type,
    List<String> recipients,
    String subject,
    String content,
    Map<String, Object> metadata,
    NotificationStatus status,
    Instant createdAt,
    Instant scheduledAt,
    Instant sentAt,
    String templateId,
    List<NotificationChannel> channels,
    DeliveryResult deliveryResult,
    int retryCount,
    String externalId,
    Map<String, String> tags
) {

    public record DeliveryResult(
        String deliveryId,
        Map<NotificationChannel, ChannelDeliveryResult> channelResults,
        Instant deliveredAt,
        int totalRecipients,
        int successfulDeliveries,
        int failedDeliveries,
        Map<String, String> providerResponse
    ) {

        public double getSuccessRate() {
            if (totalRecipients == 0) return 0.0;
            return (double) successfulDeliveries / totalRecipients * 100;
        }

        public boolean isFullySuccessful() {
            return failedDeliveries == 0 && successfulDeliveries > 0;
        }

        public record ChannelDeliveryResult(
            NotificationChannel channel,
            String providerId,
            DeliveryStatus status,
            String messageId,
            String errorCode,
            String errorMessage,
            Instant deliveredAt,
            Instant openedAt,
            Instant clickedAt,
            Map<String, Object> metadata
        ) {}

        public enum DeliveryStatus {
            PENDING,
            SENT,
            DELIVERED,
            OPENED,
            CLICKED,
            BOUNCED,
            FAILED,
            UNSUBSCRIBED,
            SPAM
        }
    }

    public static Notification create(String tenantId, NotificationType type, List<String> recipients,
                                   String subject, String content) {
        return new Notification(
            tenantId,
            generateNotificationId(),
            type,
            recipients,
            subject,
            content,
            Map.of(),
            NotificationStatus.PENDING,
            Instant.now(),
            null,
            null,
            null,
            List.of(NotificationChannel.EMAIL), // Default channel
            null,
            0,
            null,
            Map.of()
        );
    }

    public Notification withTemplate(String templateId, Map<String, Object> templateData) {
        Map<String, Object> newMetadata = Map.copyOf(metadata);
        newMetadata.putAll(templateData);
        return new Notification(
            tenantId,
            notificationId,
            type,
            recipients,
            subject,
            content,
            newMetadata,
            status,
            createdAt,
            scheduledAt,
            sentAt,
            templateId,
            channels,
            deliveryResult,
            retryCount,
            externalId,
            tags
        );
    }

    public Notification scheduleFor(Instant scheduledAt) {
        return new Notification(
            tenantId,
            notificationId,
            type,
            recipients,
            subject,
            content,
            metadata,
            NotificationStatus.SCHEDULED,
            createdAt,
            scheduledAt,
            sentAt,
            templateId,
            channels,
            deliveryResult,
            retryCount,
            externalId,
            tags
        );
    }

    public boolean canRetry() {
        return retryCount < 3 && (status == NotificationStatus.FAILED || status == NotificationStatus.PARTIALLY_DELIVERED);
    }

    private static String generateNotificationId() {
        return "notif_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    public enum NotificationType {
        TRANSACTIONAL,
        MARKETING,
        ALERT,
        REMINDER,
        SYSTEM,
        VERIFICATION,
        PROMOTIONAL
    }

    public enum NotificationStatus {
        PENDING,
        SCHEDULED,
        PROCESSING,
        SENT,
        DELIVERED,
        FAILED,
        PARTIALLY_DELIVERED,
        CANCELLED
    }

    public enum NotificationChannel {
        EMAIL,
        SMS,
        PUSH,
        IN_APP,
        WEBHOOK,
        SLACK,
        TEAMS
    }
}