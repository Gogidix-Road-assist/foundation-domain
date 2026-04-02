package com.gogidix.rapidassist.ai.categorization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a Category Taxonomy.
 * Taxonomies define category structures for different content types.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTaxonomy {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String code;
    private String description;
    private String contentType;
    private TaxonomyType type;
    private TaxonomyStatus status;
    private UUID rootCategoryId;
    private Integer maxDepth;
    private boolean allowMultipleCategories;
    private boolean requireCategorization;
    private java.util.Map<String, Object> configuration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Business logic: Create a new taxonomy
     */
    public static CategoryTaxonomy create(String tenantId, String name, String code, String description, String contentType) {
        return CategoryTaxonomy.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .code(code)
                .description(description)
                .contentType(contentType)
                .type(TaxonomyType.HIERARCHICAL)
                .status(TaxonomyStatus.ACTIVE)
                .maxDepth(5)
                .allowMultipleCategories(true)
                .requireCategorization(false)
                .configuration(new java.util.HashMap<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business logic: Activate taxonomy
     */
    public void activate() {
        this.status = TaxonomyStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Deactivate taxonomy
     */
    public void deactivate() {
        this.status = TaxonomyStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if taxonomy is active
     */
    public boolean isActive() {
        return TaxonomyStatus.ACTIVE.equals(this.status);
    }

    /**
     * Business logic: Update configuration
     */
    public void updateConfiguration(String key, Object value) {
        if (this.configuration == null) {
            this.configuration = new java.util.HashMap<>();
        }
        this.configuration.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Set root category
     */
    public void setRootCategory(UUID rootCategoryId) {
        this.rootCategoryId = rootCategoryId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Update details
     */
    public void updateDetails(String name, String description) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Set max depth
     */
    public void setMaxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth != null && maxDepth > 0 ? maxDepth : 5;
        this.updatedAt = LocalDateTime.now();
    }
}
