package com.gogidix.rapidassist.ai.contentanalysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing extracted topics from content.
 * Contains topic classifications and keyword associations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentTopic {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String contentId;
    private String topicName;
    private String topicCategory;
    private Double relevanceScore;
    private Double confidence;

    private List<String> keywords;
    private List<String> keyPhrases;
    private List<String> entities;
    private List<String> concepts;

    private String mainTopic;
    private List<String> subTopics;
    private Integer topicHierarchyLevel;

    private LocalDateTime extractedAt;
    private LocalDateTime createdAt;

    /**
     * Business logic: Check if topic is highly relevant
     */
    public boolean isHighlyRelevant(Double threshold) {
        return this.relevanceScore != null && this.relevanceScore >= threshold;
    }

    /**
     * Business logic: Check if extraction is confident
     */
    public boolean hasHighConfidence(Double threshold) {
        return this.confidence != null && this.confidence >= threshold;
    }

    /**
     * Business logic: Check if topic is main topic
     */
    public boolean isMainTopic() {
        return this.topicHierarchyLevel != null && this.topicHierarchyLevel == 0;
    }

    /**
     * Business logic: Check if topic is sub topic
     */
    public boolean isSubTopic() {
        return this.topicHierarchyLevel != null && this.topicHierarchyLevel > 0;
    }

    /**
     * Business logic: Get topic description
     */
    public String getTopicDescription() {
        StringBuilder description = new StringBuilder();
        description.append("Topic: ").append(this.topicName != null ? this.topicName : "Unknown");
        if (this.topicCategory != null) {
            description.append(" (Category: ").append(this.topicCategory).append(")");
        }
        if (this.relevanceScore != null) {
            description.append(" - Relevance: ").append(String.format("%.1f%%", this.relevanceScore * 100));
        }
        return description.toString();
    }

    /**
     * Business logic: Get keyword count
     */
    public int getKeywordCount() {
        return this.keywords != null ? this.keywords.size() : 0;
    }

    /**
     * Business logic: Get entity count
     */
    public int getEntityCount() {
        return this.entities != null ? this.entities.size() : 0;
    }

    /**
     * Business logic: Check if has subtopics
     */
    public boolean hasSubTopics() {
        return this.subTopics != null && !this.subTopics.isEmpty();
    }
}
