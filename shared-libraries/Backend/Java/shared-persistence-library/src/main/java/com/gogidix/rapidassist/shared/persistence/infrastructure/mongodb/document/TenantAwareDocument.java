package com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.document;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * MongoDB Document base class for tenant-aware entities in a multi-tenant SaaS system.
 * <p>
 * This class provides automatic tenant isolation by including a tenant_id field
 * in all MongoDB documents extending it. It follows the Domain-Driven Design (DDD)
 * pattern for persistence-aware domain models specifically for MongoDB.
 * </p>
 * <p>
 * Documents extending this class will automatically have:
 * </p>
 * <ul>
 *   <li>{@code tenantId} - The tenant identifier for data isolation</li>
 *   <li>{@code id} - MongoDB document identifier (String/ObjectId)</li>
 *   <li>{@code createdAt} - Timestamp of document creation</li>
 *   <li>{@code updatedAt} - Timestamp of last modification</li>
 *   <li>{@code createdBy} - User who created the document</li>
 *   <li>{@code updatedBy} - User who last modified the document</li>
 *   <li>{@code deleted} - Soft-delete flag for logical deletion</li>
 * </ul>
 * <p>
 * The compound index on (tenantId, deleted) ensures efficient queries for
 * tenant-scoped, non-deleted documents.
 * </p>
 * <p>
 * Usage example:
 * <pre>{@code
 * @Document(collection = "insurance_policies")
 * public class InsurancePolicy extends TenantAwareDocument {
 *     private String policyNumber;
 *     private String coverageType;
 *     private BigDecimal premiumAmount;
 *     // other fields...
 * }
 * }</pre>
 * </p>
 * <p>
 * <strong>Important:</strong> When saving documents, the tenantId is automatically
 * populated from the {@link com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.context.MongoTenantContext}
 * if not explicitly set.
 * </p>
 *
 * @see com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.context.MongoTenantContext
 * @see com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.repository.TenantAwareMongoRepository
 */
@Document
@CompoundIndex(name = "tenant_deleted_idx", def = "{'tenantId': 1, 'deleted': 1}", background = true)
@CompoundIndex(name = "tenant_created_idx", def = "{'tenantId': 1, 'createdAt': -1}", background = true)
public abstract class TenantAwareDocument {

    /**
     * MongoDB document identifier.
     * <p>
     * This is the primary key for the document in MongoDB.
     * Spring Data MongoDB automatically generates a unique ObjectId if not set.
     * </p>
     */
    @Id
    private String id;

    /**
     * The unique identifier of the tenant that owns this document.
     * <p>
     * This field is the primary key for tenant data isolation. All queries should
     * include tenant_id to prevent cross-tenant data access. The field is indexed
     * for efficient tenant-scoped queries.
     * </p>
     * <p>
     * Default value is 1L (the default tenant). In production, this should be
     * populated from {@link com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.context.MongoTenantContext}
     * before saving.
     * </p>
     */
    @Field("tenant_id")
    @Indexed(background = true)
    private Long tenantId = 1L;

    /**
     * The timestamp when this document was created.
     * <p>
     * Automatically populated by Spring Data MongoDB auditing.
     * </p>
     */
    @Field("created_at")
    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    /**
     * The timestamp when this document was last modified.
     * <p>
     * Automatically updated by Spring Data MongoDB auditing on each save operation.
     * </p>
     */
    @Field("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * The identifier of the user who created this document.
     * <p>
     * Automatically populated by Spring Data MongoDB auditing if an auditor provider
     * is configured. Otherwise, should be set manually.
     * </p>
     */
    @Field("created_by")
    @CreatedBy
    @Indexed
    private String createdBy;

    /**
     * The identifier of the user who last modified this document.
     * <p>
     * Automatically updated by Spring Data MongoDB auditing if an auditor provider
     * is configured. Otherwise, should be set manually.
     * </p>
     */
    @Field("updated_by")
    @LastModifiedBy
    private String updatedBy;

    /**
     * Soft-delete flag for logical deletion.
     * <p>
     * When true, the document is considered deleted and should not appear in query results.
     * This enables soft-delete functionality which is essential for audit trails and
     * data recovery in multi-tenant systems.
     * </p>
     * <p>
     * Default is false (not deleted).
     * </p>
     */
    @Field("deleted")
    @Indexed
    private Boolean deleted = false;

    /**
     * Default constructor required by MongoDB mapping.
     */
    protected TenantAwareDocument() {
    }

    /**
     * Gets the MongoDB document identifier.
     *
     * @return the document ID, or null if not yet persisted
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the MongoDB document identifier.
     * <p>
     * Normally, this is automatically set by MongoDB on insert.
     * Only set this manually if you need to use a specific ID.
     * </p>
     *
     * @param id the document ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the tenant identifier for this document.
     *
     * @return the tenant ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * Sets the tenant identifier for this document.
     * <p>
     * In normal operation, this is automatically populated from the request context
     * via {@link com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.context.MongoTenantContext}.
     * </p>
     * <p>
     * <strong>Warning:</strong> Manually setting the tenant ID can bypass tenant isolation
     * and should only be done in trusted administrative operations.
     * </p>
     *
     * @param tenantId the tenant ID to set
     */
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return the timestamp when this document was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     * <p>
     * Normally, this is automatically set by Spring Data MongoDB auditing.
     * </p>
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last modification timestamp.
     *
     * @return the timestamp when this document was last modified
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last modification timestamp.
     * <p>
     * Normally, this is automatically updated by Spring Data MongoDB auditing.
     * </p>
     *
     * @param updatedAt the last modification timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the identifier of the user who created this document.
     *
     * @return the creator user ID
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the identifier of the user who created this document.
     *
     * @param createdBy the creator user ID to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the identifier of the user who last modified this document.
     *
     * @return the last modifier user ID
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the identifier of the user who last modified this document.
     *
     * @param updatedBy the last modifier user ID to set
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Gets the soft-delete flag.
     *
     * @return true if the document is marked as deleted, false otherwise
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * Sets the soft-delete flag.
     * <p>
     * Use {@link #markAsDeleted()} instead to ensure proper logical deletion.
     * </p>
     *
     * @param deleted the deleted flag to set
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Checks if this document belongs to the specified tenant.
     * <p>
     * This method is useful for tenant validation checks in business logic.
     * </p>
     *
     * @param tenantId the tenant ID to check against
     * @return true if this document belongs to the specified tenant
     */
    public boolean belongsToTenant(Long tenantId) {
        return Objects.equals(this.tenantId, tenantId);
    }

    /**
     * Marks this document as deleted (soft delete).
     * <p>
     * This is the preferred method for deleting documents as it preserves
     * audit trails and enables data recovery.
     * </p>
     */
    public void markAsDeleted() {
        this.deleted = true;
    }

    /**
     * Restores a soft-deleted document.
     * <p>
     * Sets the deleted flag back to false, making the document visible in queries again.
     * </p>
     */
    public void restore() {
        this.deleted = false;
    }

    /**
     * Checks if this document is active (not deleted).
     *
     * @return true if the document is not marked as deleted
     */
    public boolean isActive() {
        return !Boolean.TRUE.equals(deleted);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        TenantAwareDocument that = (TenantAwareDocument) o;
        return Objects.equals(id, that.id) && Objects.equals(tenantId, that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id='" + id + '\'' +
                ", tenantId=" + tenantId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deleted=" + deleted +
                '}';
    }
}
