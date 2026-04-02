package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity;

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
 * MongoDB Document for TagDefinition.
 * Maps to tag_definition collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tag_definition")
public class TagDefinitionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private String category;

    private List<String> keywords;

    private List<String> patterns;

    private String ruleType;

    private Double minConfidenceThreshold;

    @Indexed
    private Boolean isAutoApply;

    @Indexed
    private Integer priority;

    private java.util.Map<String, Object> metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
