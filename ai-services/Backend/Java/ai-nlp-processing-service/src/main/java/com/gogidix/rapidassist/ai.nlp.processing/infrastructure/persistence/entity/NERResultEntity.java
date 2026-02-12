package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * MongoDB Document for NERResult.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ner_result")
public class NERResultEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private UUID uuid;
    @Indexed
    private String tenantId;
    @Indexed
    private UUID textProcessingId;
    private List<com.gogidix.rapidassist.ai.nlp.processing.domain.model.NERResult.Entity> entities;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
