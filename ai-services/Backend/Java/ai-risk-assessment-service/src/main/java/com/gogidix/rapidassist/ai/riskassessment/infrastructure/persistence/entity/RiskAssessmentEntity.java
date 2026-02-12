package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AssessmentStatus;
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
 * MongoDB Document for RiskAssessment.
 * Maps to risk_assessment collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "risk_assessment")
public class RiskAssessmentEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed(unique = true)
    private String assessmentCode;

    @Indexed
    private String subjectId;

    private String subjectType;

    private String title;

    private String description;

    @Indexed
    private AssessmentStatus status;

    @Indexed
    private RiskCategory category;

    private double overallRiskScore;

    private RiskLevel riskLevel;

    private String assessedBy;

    private String reviewedBy;

    private LocalDateTime reviewedAt;

    private String approvedBy;

    private LocalDateTime approvedAt;

    private String rejectionReason;

    private Object metadata;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    private Long version;
}
