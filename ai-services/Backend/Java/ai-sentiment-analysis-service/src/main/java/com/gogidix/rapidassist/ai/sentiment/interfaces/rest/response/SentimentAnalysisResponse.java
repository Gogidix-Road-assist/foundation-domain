package com.gogidix.rapidassist.ai.sentiment.interfaces.rest.response;

import com.gogidix.rapidassist.ai.sentiment.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST response for sentiment analysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentAnalysisResponse {

    private UUID id;
    private String tenantId;
    private String userId;
    private String sourceType;
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
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime analyzedAt;
    private String createdBy;
    private String updatedBy;
    private Long durationSeconds;
    private Emotion dominantEmotion;
    private Integer positiveAspectCount;
    private Integer negativeAspectCount;
    private Integer neutralAspectCount;
}
