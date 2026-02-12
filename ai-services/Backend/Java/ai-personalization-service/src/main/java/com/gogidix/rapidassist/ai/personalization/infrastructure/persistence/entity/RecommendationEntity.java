package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for Recommendation.
 * Maps to recommendation collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "recommendation")
public class RecommendationEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed
    private UUID userProfileId;

    @Indexed
    private String segmentId;

    // Recommendation content
    @Indexed
    private String recommendationType;
    private String contentId;
    private String contentType;
    private String title;
    private String description;
    private String imageUrl;
    private String actionUrl;
    private String actionType;

    // Scoring and ranking
    private Double relevanceScore;
    private Double confidenceScore;
    private Integer rank;

    // Context and reasoning
    private String reason;
    private List<String> reasons;
    private Map<String, Object> context;
    private String ruleId;

    // Display settings
    private String displayLocation;
    private String displayTemplate;
    private Integer displayPriority;
    private Map<String, Object> displayParameters;

    // Status
    @Indexed
    private String status;

    // Effectiveness tracking
    private Boolean wasShown;
    private Boolean wasClicked;
    private LocalDateTime shownAt;
    private LocalDateTime clickedAt;
    private Long timeToClickMs;
    private String conversionType;
    private Double conversionValue;

    // Validity
    @Indexed
    private LocalDateTime validFrom;
    @Indexed
    private LocalDateTime validUntil;
    private Boolean hasExpired;

    // Metadata
    private String campaignId;
    private String abTestId;
    private String source;
    private String algorithm;
    private String algorithmVersion;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
