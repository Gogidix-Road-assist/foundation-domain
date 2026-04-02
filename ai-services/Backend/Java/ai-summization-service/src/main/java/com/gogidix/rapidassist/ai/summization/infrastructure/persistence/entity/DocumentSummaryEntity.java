package com.gogidix.rapidassist.ai.summization.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.summization.domain.model.DocumentType;
import com.gogidix.rapidassist.ai.summization.domain.model.SummaryStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * MongoDB Document for DocumentSummary.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "document_summary")
public class DocumentSummaryEntity {

    @org.springframework.data.annotation.Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID summarizationTaskId;

    private String originalText;

    private String summaryText;

    private String title;

    private String sourceUrl;

    private DocumentType documentType;

    private SummaryStyle summaryStyle;

    private String language;

    private String targetLanguage;

    private Integer originalLength;

    private Integer summaryLength;

    private List<String> keyPoints;

    private String metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    private String version;

    private Long versionTimestamp;
}
