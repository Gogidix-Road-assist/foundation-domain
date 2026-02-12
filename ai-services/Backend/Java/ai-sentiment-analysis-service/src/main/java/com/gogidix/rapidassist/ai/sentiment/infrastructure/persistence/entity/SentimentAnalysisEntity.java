package com.gogidix.rapidassist.ai.sentiment.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.sentiment.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * MongoDB entity for SentimentAnalysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sentiment_analyses")
public class SentimentAnalysisEntity {

    @Id
    private UUID id;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed
    private String sourceType;

    @Indexed
    private String sourceId;

    private String text;
    private String language;
    private AnalysisStatus status;
    private SentimentType overallSentiment;
    private SentimentCategory sentimentCategory;
    private Double sentimentScore;
    private Double confidence;

    private List<Emotion> emotions;
    private List<Aspect> aspects;

    private Integer wordCount;
    private Integer characterCount;
    private String errorMessage;

    private java.util.Map<String, Object> metadata;

    @Indexed
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime analyzedAt;

    private String createdBy;
    private String updatedBy;
}
