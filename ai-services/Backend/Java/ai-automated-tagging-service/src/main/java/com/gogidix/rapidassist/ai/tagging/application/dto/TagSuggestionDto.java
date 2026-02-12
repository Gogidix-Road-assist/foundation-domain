package com.gogidix.rapidassist.ai.tagging.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.ai.tagging.domain.model.TagSuggestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for TagSuggestion entity
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagSuggestionDto {

    private UUID id;
    private String tenantId;
    private String contentId;
    private String contentType;
    private UUID suggestedTagId;
    private TagDto suggestedTag;
    private Double confidenceScore;
    private TagSuggestion.SuggestionStatus status;
    private String suggestionMetadata;
    private String aiModelUsed;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;

    private String createdBy;
    private Long version;
}
