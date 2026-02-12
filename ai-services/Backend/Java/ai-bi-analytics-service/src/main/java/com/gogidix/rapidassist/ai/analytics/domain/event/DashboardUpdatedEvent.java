package com.gogidix.rapidassist.ai.analytics.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a dashboard is updated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardUpdatedEvent {

    private UUID eventId;
    private UUID dashboardId;
    private String tenantId;
    private String dashboardName;
    private LocalDateTime occurredAt;
    private String updatedBy;
    private Boolean isPublic;
}
