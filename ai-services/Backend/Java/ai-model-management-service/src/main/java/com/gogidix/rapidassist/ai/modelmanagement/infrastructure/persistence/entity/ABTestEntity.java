package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ABTestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Entity for ABTest.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ab_test")
public class ABTestEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;
    @Indexed
    private UUID controlModelVersionId;
    @Indexed
    private UUID treatmentModelVersionId;

    private Double trafficSplitPercentage;
    private ABTestStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long totalParticipants;
    private Long controlParticipants;
    private Long treatmentParticipants;
    private Map<String, Double> controlMetrics;
    private Map<String, Double> treatmentMetrics;
    private String winner;
    private Double statisticalSignificance;
    private Map<String, Object> metadata;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
