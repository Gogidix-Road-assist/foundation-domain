package com.gogidix.rapidassist.notification.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.notification.service.domain.model.Notification;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "notifications")
public record NotificationDocument(

    @Id
    String id,

    @Field("tenant_id")
    String tenantId,

    @Field("notification_id")
    String notificationId,

    @Field("type")
    Notification.NotificationType type,

    @Field("recipients")
    List<String> recipients,

    @Field("subject")
    String subject,

    @Field("content")
    String content,

    @Field("metadata")
    Map<String, Object> metadata,

    @Field("status")
    Notification.NotificationStatus status,

    @Field("created_at")
    Instant createdAt,

    @Field("scheduled_at")
    Instant scheduledAt,

    @Field("sent_at")
    Instant sentAt,

    @Field("template_id")
    String templateId,

    @Field("channels")
    List<Notification.NotificationChannel> channels,

    @Field("delivery_result")
    DeliveryResultEmbedded deliveryResult,

    @Field("retry_count")
    int retryCount,

    @Field("external_id")
    String externalId,

    @Field("tags")
    Map<String, String> tags,

    @Field("version")
    Long version
) {

    public static NotificationDocument fromDomain(Notification notification) {
        return new NotificationDocument(
            null, // MongoDB will generate ID
            notification.tenantId(),
            notification.notificationId(),
            notification.type(),
            notification.recipients(),
            notification.subject(),
            notification.content(),
            notification.metadata(),
            notification.status(),
            notification.createdAt(),
            notification.scheduledAt(),
            notification.sentAt(),
            notification.templateId(),
            notification.channels(),
            notification.deliveryResult() != null ?
                new DeliveryResultEmbedded(notification.deliveryResult()) : null,
            notification.retryCount(),
            notification.externalId(),
            notification.tags(),
            1L
        );
    }

    public static NotificationDocument updateFromDomain(NotificationDocument existing,
                                                      Notification notification) {
        return new NotificationDocument(
            existing.id(),
            notification.tenantId(),
            notification.notificationId(),
            notification.type(),
            notification.recipients(),
            notification.subject(),
            notification.content(),
            notification.metadata(),
            notification.status(),
            existing.createdAt(),
            notification.scheduledAt(),
            notification.sentAt(),
            notification.templateId(),
            notification.channels(),
            notification.deliveryResult() != null ?
                new DeliveryResultEmbedded(notification.deliveryResult()) : null,
            notification.retryCount(),
            notification.externalId(),
            notification.tags(),
            existing.version() + 1
        );
    }

    public Notification toDomain() {
        return new Notification(
            tenantId(),
            notificationId(),
            type(),
            recipients(),
            subject(),
            content(),
            metadata(),
            status(),
            createdAt(),
            scheduledAt(),
            sentAt(),
            templateId(),
            channels(),
            deliveryResult() != null ? deliveryResult().toDomain() : null,
            retryCount(),
            externalId(),
            tags()
        );
    }

    public record DeliveryResultEmbedded(
        String deliveryId,
        Map<String, ChannelDeliveryResultEmbedded> channelResults,
        Instant deliveredAt,
        int totalRecipients,
        int successfulDeliveries,
        int failedDeliveries,
        Map<String, String> providerResponse
    ) {

        public DeliveryResultEmbedded(Notification.DeliveryResult result) {
            this(
                result.deliveryId(),
                result.channelResults().entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                        e -> e.getKey().name(),
                        e -> new ChannelDeliveryResultEmbedded(e.getValue())
                    )),
                result.deliveredAt(),
                result.totalRecipients(),
                result.successfulDeliveries(),
                result.failedDeliveries(),
                result.providerResponse()
            );
        }

        public Notification.DeliveryResult toDomain() {
            return new Notification.DeliveryResult(
                deliveryId(),
                channelResults().entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                        e -> Notification.NotificationChannel.valueOf(e.getKey()),
                        e -> e.getValue().toDomain()
                    )),
                deliveredAt(),
                totalRecipients(),
                successfulDeliveries(),
                failedDeliveries(),
                providerResponse()
            );
        }
    }

    public record ChannelDeliveryResultEmbedded(
        Notification.NotificationChannel channel,
        String providerId,
        Notification.DeliveryResult.DeliveryStatus status,
        String messageId,
        String errorCode,
        String errorMessage,
        Instant deliveredAt,
        Instant openedAt,
        Instant clickedAt,
        Map<String, Object> metadata
    ) {

        public ChannelDeliveryResultEmbedded(Notification.DeliveryResult.ChannelDeliveryResult result) {
            this(
                result.channel(),
                result.providerId(),
                result.status(),
                result.messageId(),
                result.errorCode(),
                result.errorMessage(),
                result.deliveredAt(),
                result.openedAt(),
                result.clickedAt(),
                result.metadata()
            );
        }

        public Notification.DeliveryResult.ChannelDeliveryResult toDomain() {
            return new Notification.DeliveryResult.ChannelDeliveryResult(
                channel(),
                providerId(),
                status(),
                messageId(),
                errorCode(),
                errorMessage(),
                deliveredAt(),
                openedAt(),
                clickedAt(),
                metadata()
            );
        }
    }
}