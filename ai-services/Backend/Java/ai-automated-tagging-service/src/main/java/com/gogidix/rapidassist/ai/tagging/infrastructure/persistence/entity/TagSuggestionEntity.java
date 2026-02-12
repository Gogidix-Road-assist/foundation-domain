package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.tagging.domain.model.TagSuggestion;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for TagSuggestion.
 * Maps to tag_suggestion collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tag_suggestion")
public class TagSuggestionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String contentId;

    @Indexed
    private String contentType;

    @Indexed
    private UUID suggestedTagId;

    private Double confidenceScore;

    @Indexed
    private TagSuggestion.SuggestionStatus status;

    private String suggestionMetadata;

    private String aiModelUsed;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private String createdBy;

    @Indexed
    private Long version;
}
