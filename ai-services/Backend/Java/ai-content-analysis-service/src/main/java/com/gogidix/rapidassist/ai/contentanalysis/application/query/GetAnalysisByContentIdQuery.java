package com.gogidix.rapidassist.ai.contentanalysis.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get analysis by content ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAnalysisByContentIdQuery {

    private String tenantId;
    private String contentId;
    private Boolean includeTopics;
    private Boolean includeMetrics;
    private Boolean includeSentiment;
    private Boolean includeSEO;
    private Boolean includeReadability;
}
