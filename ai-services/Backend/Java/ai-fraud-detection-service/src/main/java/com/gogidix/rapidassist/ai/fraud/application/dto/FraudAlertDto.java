package com.gogidix.rapidassist.ai.fraud.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for FraudAlert
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FraudAlertDto {

    @EqualsAndHashCode.Include
    private UUID id;
    private String tenantId;
    private UUID fraudDetectionId;
    private String alertType;
    private String severity;
    private String title;
    private String description;
    private Map<String, Object> alertDetails;
    private String status;
    private String assignedTo;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;
    private String acknowledgmentNotes;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;
    private UUID fraudCaseId;
    private Boolean caseCreated;
    private Integer escalationLevel;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
