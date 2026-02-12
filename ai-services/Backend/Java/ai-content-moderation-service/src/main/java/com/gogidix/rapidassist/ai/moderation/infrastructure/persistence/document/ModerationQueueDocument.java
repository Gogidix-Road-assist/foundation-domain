package com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.document;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * MongoDB document for ModerationQueue
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "moderation_queue")
public class ModerationQueueDocument {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String contentId;

    private String contentType;

    private String content;

    private String userId;

    private String userName;

    @Indexed
    private ModerationQueue.QueueStatus status;

    @Indexed
    private Integer priority;

    @Indexed
    private LocalDateTime submittedAt;

    private LocalDateTime assignedAt;

    @Indexed
    private String assignedTo;

    private LocalDateTime reviewedAt;

    private String reviewedBy;

    private ModerationQueue.QueueAction action;

    private String reviewNotes;

    private Map<String, Object> context;

    private Map<String, Object> metadata;
}
