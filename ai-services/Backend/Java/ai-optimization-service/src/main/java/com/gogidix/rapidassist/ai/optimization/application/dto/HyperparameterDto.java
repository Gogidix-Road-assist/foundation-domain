package com.gogidix.rapidassist.ai.optimization.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Hyperparameter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HyperparameterDto {

    private UUID id;
    private String tenantId;
    private String name;
    private String type;
    private Object minValue;
    private Object maxValue;
    private Double currentValue;
    private String scale;
    private Boolean isDiscrete;
    private List<String> allowedValues;
    private String description;
    private String category;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
