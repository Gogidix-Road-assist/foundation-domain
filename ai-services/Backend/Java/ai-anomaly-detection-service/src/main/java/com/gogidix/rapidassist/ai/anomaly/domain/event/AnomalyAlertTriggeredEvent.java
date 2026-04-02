package com.gogidix.rapidassist.ai.anomaly.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when an anomaly alert is triggered.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyAlertTriggeredEvent {

    private UUID eventId;
    private UUID alertId;
    private String alertTitle;
    private String tenantId;
    private String severity;
    private UUID detectionId;
    private UUID ruleId;
    private LocalDateTime occurredAt;

    public static AnomalyAlertTriggeredEvent create(UUID alertId, String alertTitle, String tenantId,
                                                     String severity, UUID detectionId, UUID ruleId) {
        return AnomalyAlertTriggeredEvent.builder()
                .eventId(UUID.randomUUID())
                .alertId(alertId)
                .alertTitle(alertTitle)
                .tenantId(tenantId)
                .severity(severity)
                .detectionId(detectionId)
                .ruleId(ruleId)
                .occurredAt(LocalDateTime.now())
                .build();
    }
}
