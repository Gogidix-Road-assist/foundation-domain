package com.gogidix.rapidassist.ai.summization.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when a summarization task fails.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummarizationTaskFailedEvent {

    private UUID taskId;
    private String tenantId;
    private String userId;
    private String errorMessage;
    private Integer retryCount;
    private LocalDateTime createdAt;

    public static SummarizationTaskFailedEvent from(UUID taskId, String tenantId, String userId,
                                                   String errorMessage, Integer retryCount) {
        return SummarizationTaskFailedEvent.builder()
                .taskId(taskId)
                .tenantId(tenantId)
                .userId(userId)
                .errorMessage(errorMessage)
                .retryCount(retryCount)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
