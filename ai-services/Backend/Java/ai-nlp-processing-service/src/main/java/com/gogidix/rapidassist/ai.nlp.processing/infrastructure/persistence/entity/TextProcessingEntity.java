package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for TextProcessing.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "text_processing")
public class TextProcessingEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private UUID uuid;
    @Indexed
    private String tenantId;
    @Indexed
    private String text;
    @Indexed
    private com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextProcessing.ProcessingType processingType;
    private String language;
    @Indexed
    private com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextProcessing.ProcessingStatus status;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
