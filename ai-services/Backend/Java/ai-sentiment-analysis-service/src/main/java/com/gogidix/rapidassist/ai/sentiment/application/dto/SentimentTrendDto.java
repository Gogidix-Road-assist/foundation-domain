package com.gogidix.rapidassist.ai.sentiment.application.dto;

import com.gogidix.rapidassist.ai.sentiment.domain.model.SentimentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for sentiment trend analysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentTrendDto {

    private String tenantId;
    private String userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long totalAnalyses;
    private Map<SentimentType, Long> sentimentDistribution;
    private Double averageSentimentScore;
    private SentimentType dominantSentiment;
    private Double positivePercentage;
    private Double negativePercentage;
    private Double neutralPercentage;
}
