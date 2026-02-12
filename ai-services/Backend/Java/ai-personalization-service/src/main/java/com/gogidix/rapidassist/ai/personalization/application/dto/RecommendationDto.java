package com.gogidix.rapidassist.ai.personalization.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO representing a recommendation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {

    private UUID id;
    private String tenantId;
    private String userId;
    private UUID userProfileId;
    private String segmentId;

    // Recommendation content
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
    private String status;

    // Effectiveness tracking
    private Boolean wasShown;
    private Boolean wasClicked;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime shownAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime clickedAt;

    private Long timeToClickMs;
    private String conversionType;
    private Double conversionValue;

    // Metadata
    private String campaignId;
    private String abTestId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
