package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationStatus;
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
@Document(collection = "summarization_request")
public class SummarizationRequestEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed(unique = true)
    private String requestId;

    private SummarizationType summarizationType;

    private SummaryLength summaryLength;

    private List<String> sourceTexts;

    private List<String> documentUrls;

    private Map<String, Object> options;

    private SummarizationStatus status;

    private String errorMessage;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;
}
