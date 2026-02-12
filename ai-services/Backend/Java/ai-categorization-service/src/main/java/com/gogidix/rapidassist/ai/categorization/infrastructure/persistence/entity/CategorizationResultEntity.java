package com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "categorization_result")
public class CategorizationResultEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID requestId;

    @Indexed
    private String contentId;

    @Indexed
    private String contentType;

    @Indexed
    private UUID taxonomyId;

    private String predictions;

    @Indexed
    private String status;

    private String errorMessage;

    private String modelVersion;

    private Double processingTimeMs;

    private String metadata;

    private LocalDateTime createdAt;

    private String createdBy;
}
