package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
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
 * MongoDB Document for RiskFactor.
 * Maps to risk_factor collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "risk_factor")
public class RiskFactorEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID riskAssessmentId;

    private String name;

    private String description;

    @Indexed
    private RiskCategory category;

    private double weight;

    private double score;

    private double impact;

    private double likelihood;

    private Object metadata;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
