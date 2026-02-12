package com.gogidix.rapidassist.ai.tagging.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a TaggingResult.
 * Stores tagging results with confidence scores.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaggingResult {

    private UUID id;
    private String tenantId;
    private String contentId;
    private String contentType;
    private UUID tagId;
    private String tagName;
    private Double confidenceScore;
    private String taggingMethod;
    private Boolean isAutoApplied;
    private Boolean isVerified;
    private String verifiedBy;
    private LocalDateTime verifiedAt;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private String createdBy;
}
