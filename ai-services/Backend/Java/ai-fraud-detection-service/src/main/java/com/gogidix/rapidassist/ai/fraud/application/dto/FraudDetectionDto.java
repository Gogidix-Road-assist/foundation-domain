package com.gogidix.rapidassist.ai.fraud.application.dto;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRiskLevel;
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
 * DTO for FraudDetection
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FraudDetectionDto {

    @EqualsAndHashCode.Include
    private UUID id;
    private String tenantId;
    private String entityType;
    private String entityId;
    private FraudRiskLevel riskLevel;
    private Double riskScore;
    private String detectionMethod;
    private Map<String, Object> detectionDetails;
    private List<String> detectedPatterns;
    private String status;
    private Boolean requiresReview;
    private String assignedTo;
    private LocalDateTime detectedAt;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private String reviewNotes;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
