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
@Document(collection = "category_taxonomy")
public class CategoryTaxonomyEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    @Indexed
    private String code;

    private String description;

    @Indexed
    private String contentType;

    private String type;

    private String status;

    @Indexed
    private UUID rootCategoryId;

    private Integer maxDepth;

    private Boolean allowMultipleCategories;

    private Boolean requireCategorization;

    private String configuration;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
