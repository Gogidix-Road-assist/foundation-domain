package com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

/**
 * Fleet coordination and scheduling
 * MongoDB Collection: fleet_coordination
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fleet_coordination")
@TypeAlias("fleet_coordination")
@CompoundIndex(name = "fleet_coord_date_idx", def = "{'fleetId': 1, 'coordinationDate': -1}")
public class FleetCoordination {

    @Id
    private String id;

    @Indexed
    @Field("coordination_id")
    private String coordinationId;

    @Indexed
    @Field("fleet_id")
    private String fleetId;

    @Field("coordination_type")
    private String coordinationType;

    @Field("coordination_date")
    private Instant coordinationDate;

    @Field("assigned_providers")
    private Integer assignedProviders;

    @Field("active_requests")
    private Integer activeRequests;

    @Field("completed_requests")
    private Integer completedRequests;

    @Field("average_response_time_minutes")
    private Double averageResponseTimeMinutes;

    @Field("utilization_percentage")
    private Double utilizationPercentage;

    @Field("notes")
    private String notes;

    @Field("metadata")
    private Map<String, Object> metadata;

    @Indexed
    @Field("tenant_id")
    private String tenantId;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;

    /**
     * Domain logic: Create new coordination record
     */
    public static FleetCoordination create(String tenantId, String fleetId, String coordinationType, Instant coordinationDate) {
        return FleetCoordination.builder()
                .tenantId(tenantId)
                .fleetId(fleetId)
                .coordinationType(coordinationType)
                .coordinationDate(coordinationDate)
                .coordinationId(java.util.UUID.randomUUID().toString())
                .assignedProviders(0)
                .activeRequests(0)
                .completedRequests(0)
                .averageResponseTimeMinutes(0.0)
                .utilizationPercentage(0.0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    /**
     * Domain logic: Update statistics
     */
    public void updateStatistics(Integer activeRequests, Integer completedRequests, Double avgResponseTime, Double utilization) {
        this.activeRequests = activeRequests;
        this.completedRequests = completedRequests;
        this.averageResponseTimeMinutes = avgResponseTime;
        this.utilizationPercentage = utilization;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Increment assigned providers
     */
    public void incrementAssignedProviders() {
        this.assignedProviders++;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: Add notes
     */
    public void addNotes(String notes) {
        if (this.notes == null) {
            this.notes = notes;
        } else {
            this.notes = this.notes + "\n" + notes;
        }
        this.updatedAt = Instant.now();
    }
}
