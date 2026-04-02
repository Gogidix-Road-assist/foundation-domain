package com.gogidix.rapidassist.ai.sentiment.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get sentiment analysis by ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetSentimentAnalysisQuery {

    private String tenantId;
    private UUID analysisId;
    private Boolean includeEmotions;
    private Boolean includeAspects;
}
