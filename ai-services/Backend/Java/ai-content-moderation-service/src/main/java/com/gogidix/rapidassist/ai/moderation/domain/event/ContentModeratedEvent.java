package com.gogidix.rapidassist.ai.moderation.domain.event;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when content has been moderated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentModeratedEvent {

    private String eventId;

    private String tenantId;

    private String contentId;

    private String contentType;

    private ModerationResult.ModerationStatus status;

    private Double confidenceScore;

    private LocalDateTime occurredAt;

    public static ContentModeratedEvent create(String tenantId, String contentId, String contentType,
                                               ModerationResult.ModerationStatus status, Double confidenceScore) {
        return ContentModeratedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .tenantId(tenantId)
            .contentId(contentId)
            .contentType(contentType)
            .status(status)
            .confidenceScore(confidenceScore)
            .occurredAt(LocalDateTime.now())
            .build();
    }
}
