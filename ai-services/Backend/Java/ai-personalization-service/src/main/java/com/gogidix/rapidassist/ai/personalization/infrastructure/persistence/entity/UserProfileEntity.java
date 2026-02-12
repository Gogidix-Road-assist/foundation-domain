package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for UserProfile.
 * Maps to user_profile collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_profile")
public class UserProfileEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed
    private String segmentId;

    // Demographics
    private Integer age;
    private String gender;
    private String location;
    private String language;
    private String timezone;

    // Behavioral data
    private Integer totalSessions;
    private Integer totalInteractions;
    private LocalDateTime lastActivityAt;
    private LocalDateTime firstSeenAt;

    // Preferences and interests
    private Map<String, Object> interests;
    private Map<String, Object> preferences;

    // Metrics
    private Double engagementScore;
    private Double loyaltyScore;
    private Double satisfactionScore;

    // Status
    private String status;

    // Metadata
    private Map<String, Object> attributes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
