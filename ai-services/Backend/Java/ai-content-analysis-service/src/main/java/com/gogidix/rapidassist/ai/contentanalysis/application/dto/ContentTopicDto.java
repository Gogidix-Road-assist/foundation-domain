package com.gogidix.rapidassist.ai.contentanalysis.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Content Topic
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentTopicDto {

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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime extractedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
