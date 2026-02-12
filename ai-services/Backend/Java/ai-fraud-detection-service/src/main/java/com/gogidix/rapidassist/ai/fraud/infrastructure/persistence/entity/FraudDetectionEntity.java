package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRiskLevel;
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
 * MongoDB entity for FraudDetection
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fraud_detections")
public class FraudDetectionEntity {

    @Id
    private UUID id;

    @Indexed
    private String tenantId;

    @Indexed
    private String entityType;

    @Indexed
    private String entityId;

    @Indexed
    private FraudRiskLevel riskLevel;

    private Double riskScore;
    private String detectionMethod;
    private Map<String, Object> detectionDetails;
    private List<String> detectedPatterns;

    @Indexed
    private String status;

    @Indexed
    private Boolean requiresReview;

    @Indexed
    private String assignedTo;

    @Indexed
    private LocalDateTime detectedAt;

    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private String reviewNotes;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
