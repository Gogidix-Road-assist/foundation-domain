package com.gogidix.rapidassist.ai.tagging.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.ai.tagging.domain.model.ContentTag;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for ContentTag entity
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentTagDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String contentId;
    private String contentType;
    private UUID tagId;
    private TagDto tag;
    private ContentTag.TaggingSource taggingSource;
    private Double confidenceScore;
    private Boolean manuallyVerified;
    private String taggedBy;
    private String taggingMetadata;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private String createdBy;
    private Long version;
}
