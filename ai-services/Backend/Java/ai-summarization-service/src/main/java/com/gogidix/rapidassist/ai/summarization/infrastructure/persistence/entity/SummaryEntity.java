package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationType;
import com.gogidix.rapidassist.ai.summarization.domain.model.SummaryLength;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "summary")
public class SummaryEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
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
