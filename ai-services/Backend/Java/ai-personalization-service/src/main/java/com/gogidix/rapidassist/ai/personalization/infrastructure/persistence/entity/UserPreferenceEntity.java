package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for UserPreference.
 * Maps to user_preference collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_preference")
public class UserPreferenceEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed
    private UUID profileId;

    // Preference key and value
    @Indexed
    private String preferenceKey;
    private String preferenceValue;
    private String preferenceType;

    // Category and context
    private String category;
    private String context;

    // Priority and weight
    private Integer priority;
    private Double weight;

    // Metadata
    private String source;
    private String confidenceLevel;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer hitCount;
    private LocalDateTime lastAccessedAt;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
