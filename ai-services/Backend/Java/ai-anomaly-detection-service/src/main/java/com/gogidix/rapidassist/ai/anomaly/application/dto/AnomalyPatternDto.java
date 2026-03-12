package com.gogidix.rapidassist.ai.anomaly.application.dto;

import com.gogidix.rapidassist.ai.anomaly.domain.model.PatternType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for AnomalyPattern
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnomalyPatternDto {

    @EqualsAndHashCode.Include
    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private PatternType patternType;
    private String category;
    private Map<String, Object> patternDefinition;
    private Double threshold;
    private String condition;
    private String dataSource;
    private Boolean isActive;
    private Double weight;
    private Long version;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
