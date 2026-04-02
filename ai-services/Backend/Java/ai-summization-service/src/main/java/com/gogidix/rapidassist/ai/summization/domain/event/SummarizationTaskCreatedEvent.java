package com.gogidix.rapidassist.ai.summization.domain.event;

import com.gogidix.rapidassist.ai.summization.domain.model.SummarizationTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when a summarization task is created.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummarizationTaskCreatedEvent {

    private UUID taskId;
    private String tenantId;
    private String userId;
    private String taskStringId;
    private String documentType;
    private String summaryStyle;
    private String priority;
    private LocalDateTime createdAt;

    public static SummarizationTaskCreatedEvent from(SummarizationTask task) {
        return SummarizationTaskCreatedEvent.builder()
                .taskId(task.getId())
                .tenantId(task.getTenantId())
                .userId(task.getUserId())
                .taskStringId(task.getTaskId())
                .documentType(task.getDocumentType() != null ? task.getDocumentType().name() : null)
                .summaryStyle(task.getSummaryStyle() != null ? task.getSummaryStyle().name() : null)
                .priority(task.getPriority() != null ? task.getPriority().name() : null)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static SummarizationTaskCreatedEvent create(UUID taskId, String tenantId, String userId) {
        return SummarizationTaskCreatedEvent.builder()
                .taskId(taskId)
                .tenantId(tenantId)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
