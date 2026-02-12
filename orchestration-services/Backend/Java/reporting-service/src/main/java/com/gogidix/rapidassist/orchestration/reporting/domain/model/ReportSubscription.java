package com.gogidix.rapidassist.orchestration.reporting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ReportSubscription entity representing user subscriptions to reports.
 * Users can subscribe to specific reports and receive notifications when generated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report_subscriptions")
@CompoundIndex(name = "tenant_user_idx", def = "{'tenantId': 1, 'userId': 1, 'reportId': 1}")
public class ReportSubscription {

    @Id
    private String id;

    @Indexed(unique = true)
    private String subscriptionId;

    @Indexed
    private String reportId;

    @Indexed
    private String scheduleId;

    @Indexed
    private String userId;

    @Indexed
    private String userEmail;

    @Indexed
    private SubscriptionStatus status;

    @Indexed
    @Builder.Default
    private NotificationFrequency frequency = NotificationFrequency.IMMEDIATE;

    private Map<String, Object> notificationPreferences;

    @Indexed
    private Report.ReportType reportType;

    private Map<String, Object> filters;

    @Indexed
    @Builder.Default
    private Boolean includeAttachment = true;

    private String customMessage;

    @Indexed
    private Integer deliveryCount;

    private LocalDateTime lastDeliveryAt;

    @Indexed
    private String tenantId;

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Indexed
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    /**
     * Domain logic: Check if subscription is active
     */
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }

    /**
     * Domain logic: Check if user should receive attachment
     */
    public boolean shouldIncludeAttachment() {
        return Boolean.TRUE.equals(includeAttachment);
    }

    /**
     * Domain logic: Update delivery tracking
     */
    public void recordDelivery() {
        this.deliveryCount = (deliveryCount == null ? 0 : deliveryCount) + 1;
        this.lastDeliveryAt = LocalDateTime.now();
    }

    /**
     * Domain logic: Activate subscription
     */
    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
    }

    /**
     * Domain logic: Deactivate subscription
     */
    public void deactivate() {
        this.status = SubscriptionStatus.INACTIVE;
    }

    /**
     * Domain logic: Unsubscribe
     */
    public void unsubscribe() {
        this.status = SubscriptionStatus.UNSUBSCRIBED;
    }

    /**
     * Domain logic: Validate subscription structure
     */
    public void validate() {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("User email is required");
        }
        if (reportId == null && scheduleId == null) {
            throw new IllegalArgumentException("Either report ID or schedule ID must be specified");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }
        if (!userEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Domain logic: Soft delete subscription
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.status = SubscriptionStatus.INACTIVE;
    }

    public enum SubscriptionStatus {
        ACTIVE,
        INACTIVE,
        UNSUBSCRIBED,
        PENDING
    }

    public enum NotificationFrequency {
        IMMEDIATE,
        HOURLY,
        DAILY,
        WEEKLY,
        MONTHLY
    }
}
