package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.tagging.domain.model.TagCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for TagCategory.
 * Maps to tag_category collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tag_category")
public class TagCategoryEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    private String icon;

    @Indexed
    private Integer displayOrder;

    private TagCategory.CategoryStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    @Indexed
    private Long version;
}
