package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity;

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
 * MongoDB Document for TaggingResult.
 * Maps to tagging_result collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tagging_result")
public class TaggingResultEntity {

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
    private UUID tagId;

    private String tagName;

    private Double confidenceScore;

    private String taggingMethod;

    @Indexed
    private Boolean isAutoApplied;

    @Indexed
    private Boolean isVerified;

    private String verifiedBy;

    private LocalDateTime verifiedAt;

    private java.util.Map<String, Object> metadata;

    private LocalDateTime createdAt;

    private String createdBy;
}
