package com.gogidix.rapidassist.ai.sentiment.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Query to get user sentiment analyses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserSentimentAnalysesQuery {

    private String tenantId;
    private String userId;
    private String status;
    private String sentimentType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
