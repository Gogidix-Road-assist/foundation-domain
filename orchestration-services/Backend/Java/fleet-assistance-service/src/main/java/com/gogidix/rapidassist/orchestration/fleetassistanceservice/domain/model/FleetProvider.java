package com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Fleet provider information and coverage
 * MongoDB Collection: fleet_providers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fleet_providers")
@TypeAlias("fleet_provider")
public class FleetProvider {

    @Id
    private String id;

    @Indexed
    @Field("provider_id")
    private String providerId;

    @Field("provider_name")
    private String providerName;

    @Field("contact_email")
    private String contactEmail;

    @Field("contact_phone")
    private String contactPhone;

    @Field("service_types")
    private List<String> serviceTypes;

    @Field("coverage_area")
    private CoverageArea coverageArea;

    @Field("rating")
    private Double rating;

    @Field("total_assignments")
    private Integer totalAssignments;

    @Field("completed_assignments")
    private Integer completedAssignments;

    @Field("average_response_time_minutes")
    private Double averageResponseTimeMinutes;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoverageArea {
        @GeoSpatialIndexed
        @Field("center_latitude")
        private Double centerLatitude;

        @GeoSpatialIndexed
        @Field("center_longitude")
        private Double centerLongitude;

        @Field("radius_km")
        private Double radiusKm;
    }

    /**
     * Domain logic: Create new provider
     */
    public static FleetProvider create(String tenantId, String providerId, String providerName, List<String> serviceTypes, CoverageArea coverageArea) {
        return FleetProvider.builder()
                .tenantId(tenantId)
                .providerId(providerId)
                .providerName(providerName)
                .serviceTypes(serviceTypes)
                .coverageArea(coverageArea)
                .isActive(true)
                .totalAssignments(0)
                .completedAssignments(0)
                .averageResponseTimeMinutes(0.0)
                .rating(5.0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    /**
     * Domain logic: Update rating
     */
    public void updateRating(Double newRating) {
        if (newRating < 0.0 || newRating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.rating = newRating;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Record assignment
     */
    public void recordAssignment() {
        this.totalAssignments++;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Record completion
     */
    public void recordCompletion(Double responseTimeMinutes) {
        this.completedAssignments++;
        // Update average response time
        double totalResponseTime = this.averageResponseTimeMinutes * (this.completedAssignments - 1) + responseTimeMinutes;
        this.averageResponseTimeMinutes = totalResponseTime / this.completedAssignments;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Activate provider
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Deactivate provider
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
