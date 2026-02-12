package com.gogidix.rapidassist.ai.sentiment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for batch sentiment analysis results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchSentimentResultDto {

    private List<SentimentAnalysisDto> results;
    private Integer totalCount;
    private Integer successCount;
    private Integer failureCount;
    private String batchId;
}
