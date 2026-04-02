package com.gogidix.rapidassist.ai.contentanalysis.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get content analysis by ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetContentAnalysisQuery {

    private String tenantId;
    private UUID analysisId;
    private Boolean includeTopics;
    private Boolean includeMetrics;
    private Boolean includeSentiment;
    private Boolean includeSEO;
    private Boolean includeReadability;
}
