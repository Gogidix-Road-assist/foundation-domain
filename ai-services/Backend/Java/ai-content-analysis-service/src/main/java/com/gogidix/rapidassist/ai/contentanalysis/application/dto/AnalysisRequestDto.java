package com.gogidix.rapidassist.ai.contentanalysis.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Analysis Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequestDto {

    private UUID id;
    private String tenantId;
    private String contentId;
    private String requestId;
    private String status;
    private String analysisType;
    private String contentType;

    private String callbackUrl;
    private Integer priority;
    private String requestedBy;
    private List<String> analysisOptions;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedCompletionAt;

    private String errorMessage;
    private Integer retryCount;
    private Integer maxRetries;
    private String lastAttemptAt;

    private UUID analysisResultId;
    private Integer progressPercentage;
    private String currentStep;
}
