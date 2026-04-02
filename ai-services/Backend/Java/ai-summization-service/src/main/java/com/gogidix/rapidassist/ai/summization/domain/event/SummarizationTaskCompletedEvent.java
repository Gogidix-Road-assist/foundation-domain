package com.gogidix.rapidassist.ai.summization.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when a summarization task is completed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummarizationTaskCompletedEvent {

    private UUID taskId;
    private String tenantId;
    private String userId;
    private UUID summaryId;
    private Integer summaryCount;
    private Long processingTimeMs;
    private String completionReason;
    private LocalDateTime createdAt;

    public static SummarizationTaskCompletedEvent create(UUID taskId, String tenantId, String userId,
                                                          UUID summaryId, Integer summaryCount,
                                                          Long processingTimeMs, String reason) {
        return SummarizationTaskCompletedEvent.builder()
                .taskId(taskId)
                .tenantId(tenantId)
                .userId(userId)
                .summaryId(summaryId)
                .summaryCount(summaryCount)
                .processingTimeMs(processingTimeMs)
                .completionReason(reason)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
