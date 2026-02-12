package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.contentanalysis.domain.model.AnalysisRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * MongoDB entity for AnalysisRequest
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analysis_requests")
@CompoundIndex(name = "tenant_content_idx", def = "{'tenantId': 1, 'contentId': 1}")
@CompoundIndex(name = "tenant_status_priority_idx", def = "{'tenantId': 1, 'status': 1, 'priority': -1}")
public class AnalysisRequestEntity {

    @Id
    private UUID id;

    @Indexed
    private String tenantId;

    @Indexed
    private String contentId;

    @Indexed(unique = true)
    private String requestId;

    @Indexed
    private String status;

    @Indexed
    private String analysisType;

    private String contentType;
    private String callbackUrl;
    private Integer priority;

    private String requestedBy;
    private List<String> analysisOptions;

    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime estimatedCompletionAt;

    private String errorMessage;
    private Integer retryCount;
    private Integer maxRetries;
    private String lastAttemptAt;

    private UUID analysisResultId;
    private Integer progressPercentage;
    private String currentStep;
}
