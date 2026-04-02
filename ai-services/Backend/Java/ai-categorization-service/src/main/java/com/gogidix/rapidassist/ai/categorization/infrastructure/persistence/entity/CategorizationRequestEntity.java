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
@Document(collection = "categorization_request")
public class CategorizationRequestEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String contentId;

    @Indexed
    private String contentType;

    private String content;

    @Indexed
    private UUID taxonomyId;

    private Boolean batchRequest;

    private Integer maxCategories;

    private Double minConfidence;

    @Indexed
    private String status;

    private String errorMessage;

    private String metadata;

    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    private String createdBy;
}
