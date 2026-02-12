package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for Campaign.
 * Maps to campaign collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "campaign")
public class CampaignEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed(unique = true)
    private String campaignCode;

    private String campaignName;
    private String description;

    // Campaign configuration
    @Indexed
    private String campaignType;
    private String objective;
    private Map<String, Object> configuration;
    private List<String> ruleIds;
    private List<String> abTestIds;

    // Targeting
    private List<String> targetSegmentIds;
    private Map<String, Object> targetCriteria;
    private Integer targetAudienceSize;

    // Schedule
    @Indexed
    private LocalDateTime startDate;
    @Indexed
    private LocalDateTime endDate;
    private String schedule;
    private Boolean isEvergreen;

    // Budget and spend
    private BigDecimal budget;
    private BigDecimal dailyBudget;
    private BigDecimal totalSpent;
    private BigDecimal dailySpent;

    // Performance metrics
    private Integer totalImpressions;
    private Integer totalClicks;
    private Integer totalConversions;
    private Double ctr;
    private Double conversionRate;
    private Double cpa;
    private Double roas;

    // Status
    @Indexed
    private String status;

    // Approval
    private Boolean needsApproval;
    private Boolean isApproved;
    private String approvedBy;
    private LocalDateTime approvedAt;

    // Metadata
    private String category;
    private List<String> tags;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
