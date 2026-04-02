package com.gogidix.rapidassist.ai.moderation.application.dto;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for ModerationQueue
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationQueueDto {

    private String id;
    private String tenantId;
    private String contentId;
    private String contentType;
    private String content;
    private String userId;
    private String userName;
    private ModerationQueue.QueueStatus status;
    private Integer priority;
    private LocalDateTime submittedAt;
    private LocalDateTime assignedAt;
    private String assignedTo;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private ModerationQueue.QueueAction action;
    private String reviewNotes;
    private Map<String, Object> context;
    private Map<String, Object> metadata;
}
