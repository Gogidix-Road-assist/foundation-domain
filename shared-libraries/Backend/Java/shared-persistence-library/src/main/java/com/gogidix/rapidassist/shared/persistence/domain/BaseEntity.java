package com.gogidix.rapidassist.shared.persistence.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract base class for all entities in the SaaS platform.
 * Provides common fields including UUID ID, audit fields, and soft delete support.
 * Extends TenantAwareEntity for automatic tenant isolation.
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class BaseEntity extends TenantAwareEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("created_by")
    private UUID createdBy;

    @Field("updated_by")
    private UUID updatedBy;

    @Field("deleted")
    @Builder.Default
    private Boolean deleted = false;

    @Field("version")
    @Builder.Default
    private Long version = 0L;

    /**
     * Called before entity is persisted.
     * Sets creation and update timestamps.
     */
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * Called before entity is updated.
     * Updates the timestamp.
     */
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Soft delete this entity.
     */
    public void softDelete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Restore a soft-deleted entity.
     */
    public void restore() {
        this.deleted = false;
        this.updatedAt = LocalDateTime.now();
    }
}
