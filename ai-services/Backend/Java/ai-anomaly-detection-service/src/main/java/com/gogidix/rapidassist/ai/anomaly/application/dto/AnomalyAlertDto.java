package com.gogidix.rapidassist.ai.anomaly.application.dto;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AlertSeverity;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AlertStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for AnomalyAlert
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnomalyAlertDto {

    @EqualsAndHashCode.Include
    private UUID id;
    private String tenantId;
    private String alertId;
    private UUID detectionId;
    private UUID ruleId;
    private String title;
    private String message;
    private AlertSeverity severity;
    private AlertStatus status;
    private List<String> notificationChannels;
    private Map<String, Object> context;
    private String assignedTo;
    private LocalDateTime triggeredAt;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;
    private Integer escalationLevel;
    private LocalDateTime lastEscalatedAt;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
