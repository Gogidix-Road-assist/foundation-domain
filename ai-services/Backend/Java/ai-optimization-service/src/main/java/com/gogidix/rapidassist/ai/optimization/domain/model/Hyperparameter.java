package com.gogidix.rapidassist.ai.optimization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Domain model representing a hyperparameter.
 * Pure domain model without persistence annotations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hyperparameter {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String type;
    private Object minValue;
    private Object maxValue;
    private Double currentValue;
    private String scale;
    private Boolean isDiscrete;
    private java.util.List<String> allowedValues;
    private String description;
    private String category;
    private java.util.Map<String, Object> metadata;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
