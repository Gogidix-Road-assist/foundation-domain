package com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.document;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MongoDB document for ModerationResult
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "moderation_results")
public class ModerationResultDocument {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String contentId;

    private String contentType;

    private String content;

    @Indexed
    private ModerationResult.ModerationStatus status;

    private Double confidenceScore;

    private List<ModerationResult.RuleViolation> violations;

    private Map<String, Object> analysisDetails;

    private String moderatedBy;

    @Indexed
    private LocalDateTime moderatedAt;

    private LocalDateTime createdAt;

    private String reviewNotes;

    private Map<String, Object> metadata;
}
