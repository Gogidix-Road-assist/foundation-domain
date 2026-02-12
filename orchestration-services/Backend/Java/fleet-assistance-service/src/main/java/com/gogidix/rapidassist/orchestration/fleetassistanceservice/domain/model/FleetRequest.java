package com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

/**
 * Fleet-specific assistance request
 * MongoDB Collection: fleet_requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fleet_requests")
@TypeAlias("fleet_request")
@CompoundIndex(name = "tenant_fleet_status_idx", def = "{'tenantId': 1, 'fleetId': 1, 'status': 1}")
public class FleetRequest {

    @Id
    private String id;

    @Indexed
    @Field("request_id")
    private String requestId;

    @Indexed
    @Field("fleet_id")
    private String fleetId;

    @Field("status")
    private RequestStatus status;

    @Field("service_type")
    private String serviceType;

    @Field("priority")
    private Priority priority;

    @Field("vehicle_id")
    private String vehicleId;

    @Field("location")
    private Location location;

    @Field("assigned_fleet_provider_id")
    private String assignedFleetProviderId;

    @Field("estimated_arrival")
    private Instant estimatedArrival;

    @Field("actual_arrival")
    private Instant actualArrival;

    @Field("completion_time")
    private Instant completionTime;

    @Field("metadata")
    private Map<String, Object> metadata;

    /**
     * MANDATORY: Tenant ID for multi-tenancy
     * All queries MUST filter by this field
     */
    @Indexed
    @Field("tenant_id")
    private String tenantId;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;

    @Field("deleted_at")
    private Instant deletedAt;

    public enum RequestStatus {
        PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED, ON_HOLD
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, EMERGENCY
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        @Field("latitude")
        private Double latitude;

        @Field("longitude")
        private Double longitude;

        @GeoSpatialIndexed
        @Field("address")
        private String address;
    }

    /**
     * Domain logic: Create new fleet request
     */
    public static FleetRequest create(String tenantId, String fleetId, String serviceType, Priority priority, Location location) {
        FleetRequest request = FleetRequest.builder()
                .tenantId(tenantId)
                .fleetId(fleetId)
                .serviceType(serviceType)
                .priority(priority)
                .location(location)
                .status(RequestStatus.PENDING)
                .requestId(java.util.UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return request;
    }

    /**
     * Domain logic: Assign provider
     */
    public void assignProvider(String providerId, Instant estimatedArrival) {
        if (this.status != RequestStatus.PENDING) {
            throw new IllegalStateException("Can only assign providers to pending requests");
        }
        this.assignedFleetProviderId = providerId;
        this.estimatedArrival = estimatedArrival;
        this.status = RequestStatus.ASSIGNED;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Start service
     */
    public void startService() {
        if (this.status != RequestStatus.ASSIGNED) {
            throw new IllegalStateException("Can only start assigned requests");
        }
        this.status = RequestStatus.IN_PROGRESS;
        this.actualArrival = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Complete service
     */
    public void completeService() {
        if (this.status != RequestStatus.IN_PROGRESS) {
            throw new IllegalStateException("Can only complete in-progress requests");
        }
        this.status = RequestStatus.COMPLETED;
        this.completionTime = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Cancel request
     */
    public void cancel() {
        if (this.status == RequestStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed requests");
        }
        this.status = RequestStatus.CANCELLED;
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
