package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * MongoDB entity for ContentTopic
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "content_topics")
@CompoundIndex(name = "tenant_content_idx", def = "{'tenantId': 1, 'contentId': 1}")
@CompoundIndex(name = "tenant_relevance_idx", def = "{'tenantId': 1, 'relevanceScore': -1}")
public class ContentTopicEntity {

    @Id
    private UUID id;

    @Indexed
    private String tenantId;

    @Indexed
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
}
