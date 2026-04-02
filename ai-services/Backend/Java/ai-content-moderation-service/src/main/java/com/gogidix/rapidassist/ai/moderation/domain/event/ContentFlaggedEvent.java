package com.gogidix.rapidassist.ai.moderation.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when content is flagged for review.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentFlaggedEvent {

    private String eventId;

    private String tenantId;

    private String contentId;

    private String contentType;

    private String flagReason;

    private LocalDateTime occurredAt;

    public static ContentFlaggedEvent create(String tenantId, String contentId, String contentType, String flagReason) {
        return ContentFlaggedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .tenantId(tenantId)
            .contentId(contentId)
            .contentType(contentType)
            .flagReason(flagReason)
            .occurredAt(LocalDateTime.now())
            .build();
    }
}
