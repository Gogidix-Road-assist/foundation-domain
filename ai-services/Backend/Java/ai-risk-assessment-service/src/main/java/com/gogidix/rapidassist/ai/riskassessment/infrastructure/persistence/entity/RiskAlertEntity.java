package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertPriority;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertStatus;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for RiskAlert.
 * Maps to risk_alert collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "risk_alert")
public class RiskAlertEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID riskAssessmentId;

    private String title;

    private String description;

    @Indexed
    private AlertPriority priority;

    @Indexed
    private AlertStatus status;

    private double riskScore;

    private RiskLevel riskLevel;

    @Indexed
    private RiskCategory category;

    @Indexed
    private String assignedTo;

    private String acknowledgedBy;

    private LocalDateTime acknowledgedAt;

    private LocalDateTime resolvedAt;

    private String resolution;

    private Object metadata;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
