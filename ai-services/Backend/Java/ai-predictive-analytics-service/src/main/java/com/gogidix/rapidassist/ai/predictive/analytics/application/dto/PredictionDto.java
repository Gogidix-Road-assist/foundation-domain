package com.gogidix.rapidassist.ai.predictive.analytics.application.dto;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.PredictionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Prediction
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionDto {

    private UUID id;
    private String tenantId;
    private UUID modelId;
    private String modelName;
    private Map<String, Object> inputData;
    private Object predictionResult;
    private Double confidenceScore;
    private PredictionStatus status;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Long processingTimeMs;
}
