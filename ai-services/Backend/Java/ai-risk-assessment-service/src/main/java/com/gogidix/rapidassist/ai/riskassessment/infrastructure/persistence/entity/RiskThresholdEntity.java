package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertPriority;
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
 * MongoDB Document for RiskThreshold.
 * Maps to risk_threshold collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "risk_threshold")
public class RiskThresholdEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed(unique = true)
    private String name;

    private String description;

    @Indexed
    private RiskCategory category;

    private double lowThreshold;

    private double mediumThreshold;

    private double highThreshold;

    private double criticalThreshold;

    private boolean alertEnabled;

    private AlertPriority defaultPriority;

    @Indexed
    private boolean active;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
