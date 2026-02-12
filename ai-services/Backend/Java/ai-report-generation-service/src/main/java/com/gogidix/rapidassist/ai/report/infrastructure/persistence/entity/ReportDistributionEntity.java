package com.gogidix.rapidassist.ai.report.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.report.domain.model.DistributionType;
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
 * MongoDB Document for ReportDistribution.
 * Maps to report_distribution collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "report_distribution")
public class ReportDistributionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID reportGenerationId;

    @Indexed
    private DistributionType distributionType;

    private String destination;

    private Map<String, Object> distributionConfig;

    @Indexed
    private Boolean isSuccessful;

    private String responseMessage;

    @Indexed
    private LocalDateTime distributedAt;

    private Integer retryCount;

    private String createdBy;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;
}
