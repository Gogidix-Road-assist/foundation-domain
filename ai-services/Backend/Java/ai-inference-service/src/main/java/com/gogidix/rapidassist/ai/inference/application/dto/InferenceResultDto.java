package com.gogidix.rapidassist.ai.inference.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Inference Result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferenceResultDto {

    private UUID id;
    private UUID inferenceRequestId;
    private String tenantId;
    private String outputData;
    private String modelVersion;
    private Double confidence;
    private Integer latencyMillis;
    private LocalDateTime timestamp;
    private Map<String, Object> additionalData;
    private String resultType;
}
