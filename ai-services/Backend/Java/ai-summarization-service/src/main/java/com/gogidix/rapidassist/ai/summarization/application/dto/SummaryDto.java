package com.gogidix.rapidassist.ai.summarization.application.dto;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationType;
import com.gogidix.rapidassist.ai.summarization.domain.model.SummaryLength;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDto {
    @EqualsAndHashCode.Include

    private UUID id;
    private String tenantId;
    private UUID summarizationRequestId;
    private String content;
    private SummarizationType summarizationType;
    private SummaryLength summaryLength;
    private int originalWordCount;
    private int summaryWordCount;
    private double compressionRatio;
    private Double qualityScore;
    private List<String> keySentences;
    private List<String> keyPhrases;
    private Map<String, Object> metadata;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
