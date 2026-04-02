package com.gogidix.rapidassist.ai.analytics.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a metric is created
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricCreatedEvent {

    private UUID eventId;
    private UUID metricId;
    private String tenantId;
    private String metricName;
    private String metricCode;
    private String metricType;
    private LocalDateTime occurredAt;
    private String createdBy;
}
