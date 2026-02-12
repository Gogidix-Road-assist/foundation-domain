package com.gogidix.rapidassist.ai.nlp.processing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing Named Entity Recognition results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NERResult {

    private UUID id;
    private String tenantId;
    private UUID textProcessingId;
    private List<Entity> entities;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entity {
        private String text;
        private String entityType;
        private double confidence;
        private int startPosition;
        private int endPosition;

        public enum EntityType {
            PERSON,
            ORGANIZATION,
            LOCATION,
            DATE,
            TIME,
            MONEY,
            PERCENT,
            NUMBER,
            EMAIL,
            PHONE,
            URL,
            CUSTOM
        }
    }
}
