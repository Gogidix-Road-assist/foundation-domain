package com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

/**
 * Catalog of available service types
 * MongoDB Collection: service_types
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "service_types")
@TypeAlias("service_type")
public class ServiceType {

    @Id
    private String id;

    @Indexed
    @Field("service_type_code")
    private String serviceTypeCode;

    @Field("service_name")
    private String serviceName;

    @Field("description")
    private String description;

    @Field("category")
    private String category;

    @Field("base_price")
    private Double basePrice;

    @Field("estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @Field("is_active")
    private Boolean isActive;

    @Field("metadata")
    private Map<String, Object> metadata;

    @Indexed
    @Field("tenant_id")
    private String tenantId;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;

    @Field("deleted_at")
    private Instant deletedAt;

    /**
     * Domain logic: Create new service type
     */
    public static ServiceType create(String tenantId, String serviceTypeCode, String serviceName, String category, Double basePrice) {
        return ServiceType.builder()
                .tenantId(tenantId)
                .serviceTypeCode(serviceTypeCode)
                .serviceName(serviceName)
                .category(category)
                .basePrice(basePrice)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    /**
     * Domain logic: Update price
     */
    public void updatePrice(Double newPrice) {
        if (newPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.basePrice = newPrice;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Activate service
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Deactivate service
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Soft delete
     */
    public void softDelete() {
        this.deletedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Check if deleted
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
