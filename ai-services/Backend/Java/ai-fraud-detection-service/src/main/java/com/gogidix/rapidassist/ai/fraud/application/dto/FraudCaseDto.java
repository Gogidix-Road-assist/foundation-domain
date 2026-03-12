package com.gogidix.rapidassist.ai.fraud.application.dto;

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
 * DTO for FraudCase
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FraudCaseDto {

    @EqualsAndHashCode.Include
    private UUID id;
    private String tenantId;
    private String caseNumber;
    private String caseType;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String assignedTo;
    private String assignedTeam;
    private List<UUID> linkedAlerts;
    private List<UUID> linkedDetections;
    private String investigationSummary;
    private Map<String, Object> investigationDetails;
    private String finding;
    private String decision;
    private String actionTaken;
    private String outcome;
    private Double estimatedLoss;
    private Double recoveredAmount;
    private LocalDateTime openedDate;
    private LocalDateTime assignedDate;
    private LocalDateTime startDate;
    private LocalDateTime closedDate;
    private String closedBy;
    private String closureReason;
    private Integer durationDays;
    private String createdBy;
    private String updatedBy;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
