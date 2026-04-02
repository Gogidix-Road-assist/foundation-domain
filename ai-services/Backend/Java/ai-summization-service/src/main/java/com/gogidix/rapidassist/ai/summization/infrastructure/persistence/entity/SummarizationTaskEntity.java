package com.gogidix.rapidassist.ai.summization.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.summization.domain.model.DocumentType;
import com.gogidix.rapidassist.ai.summization.domain.model.Priority;
import com.gogidix.rapidassist.ai.summization.domain.model.SummaryStyle;
import com.gogidix.rapidassist.ai.summization.domain.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for SummarizationTask.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "summarization_task")
public class SummarizationTaskEntity {

    @org.springframework.data.annotation.Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed(unique = true)
    private String taskId;

    private TaskStatus status;

    private DocumentType documentType;

    private SummaryStyle summaryStyle;

    private String sourceLanguage;

    private String targetLanguage;

    private Priority priority;

    @Indexed
    private UUID summaryConfigId;

    private String inputText;

    private String inputUrl;

    private String errorMessage;

    private LocalDateTime createdAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private String createdBy;

    private String updatedBy;

    private Long processingTimeMs;

    private Integer retryCount;

    private Integer maxRetries;

    private String metadata;

    private Long version;
}
