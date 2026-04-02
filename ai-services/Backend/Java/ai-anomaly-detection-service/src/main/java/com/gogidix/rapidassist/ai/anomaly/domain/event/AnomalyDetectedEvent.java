package com.gogidix.rapidassist.ai.anomaly.domain.event;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyDetection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when an anomaly is detected.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyDetectedEvent {

    private UUID eventId;
    private UUID detectionId;
    private String tenantId;
    private String dataSource;
    private String severity;
    private Double anomalyScore;
    private LocalDateTime occurredAt;
    private String detectionMethod;

    public static AnomalyDetectedEvent from(AnomalyDetection detection) {
        return AnomalyDetectedEvent.builder()
                .eventId(UUID.randomUUID())
                .detectionId(detection.getId())
                .tenantId(detection.getTenantId())
                .dataSource(detection.getDataSource())
                .severity(detection.getSeverity() != null ? detection.getSeverity().name() : null)
                .anomalyScore(detection.getAnomalyScore())
                .occurredAt(LocalDateTime.now())
                .detectionMethod(detection.getDetectionMethod())
                .build();
    }
}
