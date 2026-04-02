package com.gogidix.rapidassist.ai.inference.application.dto;

import com.gogidix.rapidassist.ai.inference.domain.model.InferenceStatus;
import com.gogidix.rapidassist.ai.inference.domain.model.InferenceType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Inference Request
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferenceRequestDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String requestId;
    private String modelId;
    private String modelVersion;
    private InferenceStatus status;
    private InferenceType inferenceType;
    private String inputData;
    private Map<String, Object> parameters;
    private Integer priority;
    private String requestedBy;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;
    private Integer retryCount;
    private Map<String, Object> metadata;
}
