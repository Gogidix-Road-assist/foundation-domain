package com.gogidix.rapidassist.ai.inference.application.dto;

import com.gogidix.rapidassist.ai.inference.domain.model.BatchStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Batch Inference Request
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchInferenceRequestDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String batchId;
    private String modelId;
    private String modelVersion;
    private BatchStatus status;
    private List<String> inputItems;
    private List<String> outputItems;
    private Integer totalItems;
    private Integer completedItems;
    private Integer failedItems;
    private Double progress;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String createdBy;
    private Map<String, Object> parameters;
    private String errorMessage;
}
