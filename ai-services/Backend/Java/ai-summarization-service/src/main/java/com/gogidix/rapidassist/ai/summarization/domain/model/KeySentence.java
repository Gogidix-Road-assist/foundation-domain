package com.gogidix.rapidassist.ai.summarization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeySentence {
    @EqualsAndHashCode.Include

    private UUID id;
    private String tenantId;
    private UUID summaryId;
    private String sentence;
    private double relevanceScore;
    private int position;
    private LocalDateTime createdAt;
}
