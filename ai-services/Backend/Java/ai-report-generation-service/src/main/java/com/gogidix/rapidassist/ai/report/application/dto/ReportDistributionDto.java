package com.gogidix.rapidassist.ai.report.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.ai.report.domain.model.DistributionType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO representing a report distribution record.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDistributionDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID reportGenerationId;
    private DistributionType distributionType;
    private String destination;
    private Map<String, Object> distributionConfig;
    private Boolean isSuccessful;
    private String responseMessage;
    private Integer retryCount;
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime distributedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
