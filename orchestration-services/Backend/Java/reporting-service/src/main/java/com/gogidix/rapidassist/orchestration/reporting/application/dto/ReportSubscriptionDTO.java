package com.gogidix.rapidassist.orchestration.reporting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for ReportSubscription responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSubscriptionDTO {
    private String id;
    private String subscriptionId;
    private String reportId;
    private String scheduleId;
    private String userId;
    private String userEmail;
    private SubscriptionStatusDTO status;
    private NotificationFrequencyDTO frequency;
    private Map<String, Object> notificationPreferences;
    private ReportDTO.ReportTypeDTO reportType;
    private Map<String, Object> filters;
    private Boolean includeAttachment;
    private String customMessage;
    private Integer deliveryCount;
    private LocalDateTime lastDeliveryAt;
    private String tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum SubscriptionStatusDTO {
        ACTIVE, INACTIVE, UNSUBSCRIBED, PENDING
    }

    public enum NotificationFrequencyDTO {
        IMMEDIATE, HOURLY, DAILY, WEEKLY, MONTHLY
    }
}
