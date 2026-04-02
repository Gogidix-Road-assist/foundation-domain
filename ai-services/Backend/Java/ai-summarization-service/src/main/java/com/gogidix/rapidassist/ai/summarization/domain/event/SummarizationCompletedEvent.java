package com.gogidix.rapidassist.ai.summarization.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummarizationCompletedEvent {
    private UUID requestId;
    private UUID summaryId;
    private String tenantId;
    private String summarizationType;
    private double qualityScore;
    private double compressionRatio;
    private int originalWordCount;
    private int summaryWordCount;
    private LocalDateTime completedAt;

    public static SummarizationCompletedEvent create(UUID requestId, UUID summaryId, String tenantId,
                                                     String summarizationType, double qualityScore,
                                                     double compressionRatio, int originalWordCount,
                                                     int summaryWordCount) {
        return SummarizationCompletedEvent.builder()
                .requestId(requestId)
                .summaryId(summaryId)
                .tenantId(tenantId)
                .summarizationType(summarizationType)
                .qualityScore(qualityScore)
                .compressionRatio(compressionRatio)
                .originalWordCount(originalWordCount)
                .summaryWordCount(summaryWordCount)
                .completedAt(LocalDateTime.now())
                .build();
    }
}
