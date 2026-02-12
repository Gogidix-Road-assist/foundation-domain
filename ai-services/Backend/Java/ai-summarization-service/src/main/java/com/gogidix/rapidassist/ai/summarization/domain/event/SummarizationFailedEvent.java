package com.gogidix.rapidassist.ai.summarization.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummarizationFailedEvent {
    private UUID requestId;
    private String tenantId;
    private String errorMessage;
    private LocalDateTime failedAt;

    public static SummarizationFailedEvent create(UUID requestId, String tenantId, String errorMessage) {
        return SummarizationFailedEvent.builder()
                .requestId(requestId)
                .tenantId(tenantId)
                .errorMessage(errorMessage)
                .failedAt(LocalDateTime.now())
                .build();
    }
}
