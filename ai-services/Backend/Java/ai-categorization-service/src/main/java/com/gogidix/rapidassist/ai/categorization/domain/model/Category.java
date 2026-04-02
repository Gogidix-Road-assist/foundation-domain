package com.gogidix.rapidassist.ai.categorization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a Category.
 * Categories organize content into hierarchical taxonomies.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String code;
    private String description;
    private UUID parentCategoryId;
    private Integer level;
    private String path;
    private CategoryStatus status;
    private Integer weight;
    private String icon;
    private String color;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Child categories (part of aggregate)
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    /**
     * Business logic: Create a new root category
     */
    public static Category createRoot(String tenantId, String name, String code, String description) {
        return Category.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .code(code)
                .description(description)
                .parentCategoryId(null)
                .level(0)
                .path("/" + code)
                .status(CategoryStatus.ACTIVE)
                .weight(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .children(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Create a child category
     */
    public static Category createChild(String tenantId, String name, String code, String description, UUID parentId, String parentPath) {
        return Category.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .code(code)
                .description(description)
                .parentCategoryId(parentId)
                .level(0)
                .path(parentPath + "/" + code)
                .status(CategoryStatus.ACTIVE)
                .weight(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .children(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Add child category
     */
    public void addChild(Category child) {
        child.setLevel(this.level + 1);
        child.setPath(this.path + "/" + child.getCode());
        this.children.add(child);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Activate category
     */
    public void activate() {
        this.status = CategoryStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Deactivate category
     */
    public void deactivate() {
        this.status = CategoryStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Archive category
     */
    public void archive() {
        this.status = CategoryStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if category is active
     */
    public boolean isActive() {
        return CategoryStatus.ACTIVE.equals(this.status);
    }

    /**
     * Business logic: Check if category is root
     */
    public boolean isRoot() {
        return this.parentCategoryId == null;
    }

    /**
     * Business logic: Check if category has children
     */
    public boolean hasChildren() {
        return this.children != null && !this.children.isEmpty();
    }

    /**
     * Business logic: Update category details
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
}
