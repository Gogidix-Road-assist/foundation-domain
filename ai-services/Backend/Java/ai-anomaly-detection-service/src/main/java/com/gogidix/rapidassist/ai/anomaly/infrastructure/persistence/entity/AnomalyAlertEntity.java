package com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AlertSeverity;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AlertStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for AnomalyAlert.
 * Maps to anomaly_alert collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "anomaly_alert")
public class AnomalyAlertEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed(unique = true)
    private String alertId;

    @Indexed
    private UUID detectionId;

    @Indexed
    private UUID ruleId;

    private String title;

    private String message;

    @Indexed
    private AlertSeverity severity;

    @Indexed
    private AlertStatus status;

    private List<String> notificationChannels;

    private Map<String, Object> context;

    @Indexed
    private String assignedTo;

    @Indexed
    private LocalDateTime triggeredAt;

    private LocalDateTime acknowledgedAt;

    private String acknowledgedBy;

    private LocalDateTime resolvedAt;

    private String resolvedBy;

    private String resolutionNotes;

    private Integer escalationLevel;

    private LocalDateTime lastEscalatedAt;

    private Map<String, Object> metadata;

    @Indexed
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
