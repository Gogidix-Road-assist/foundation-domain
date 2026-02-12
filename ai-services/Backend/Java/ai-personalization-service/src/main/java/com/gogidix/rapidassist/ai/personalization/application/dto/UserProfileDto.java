package com.gogidix.rapidassist.ai.personalization.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO representing a user profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private UUID id;
    private String tenantId;
    private String userId;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastActivityAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;
    private Long version;
}
