package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.tagging.domain.model.Tag;
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
 * MongoDB Document for Tag.
 * Maps to tag collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tag")
public class TagEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    @Indexed
    private String category;

    private String description;

    private String color;

    @Indexed
    private Tag.TagStatus status;

    @Indexed
    private Integer usageCount;

    @Indexed
    private Long version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    /**
     * Embedded object for TagCategory reference
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagCategoryEmbedded {
        private UUID id;
        private String name;
        private String status; // Stored as string, will be converted to CategoryStatus enum
    }
}
