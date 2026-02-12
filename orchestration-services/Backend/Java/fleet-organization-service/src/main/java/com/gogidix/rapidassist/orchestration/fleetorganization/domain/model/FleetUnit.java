package com.gogidix.rapidassist.orchestration.fleetorganization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * FleetUnit entity representing individual fleet units within organizations.
 * Units can be vehicles, equipment, or personnel assigned to an organization.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fleet_units")
@CompoundIndex(name = "tenant_org_idx", def = "{'tenantId': 1, 'organizationId': 1}")
public class FleetUnit {

    @Id
    private String id;

    @Indexed(unique = true)
    private String unitId;

    @Indexed
    private String organizationId;

    @Indexed
    private String unitName;

    @Indexed
    private UnitType unitType;

    private String unitCode; // e.g., "VH-001", "EQ-042"

    private String vin; // For vehicle units

    private String make;
    private String model;
    private Integer year;

    @Indexed
    private UnitStatus status;

    @Indexed
    private String currentDriverId;

    @Indexed
    private String currentAssignmentId;

    private String licensePlate;

    private String description;

    private Location currentLocation;

    private Double mileage;

    private Map<String, Object> specifications;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    @Indexed
    private String tenantId;

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Indexed
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    /**
     * Domain logic: Check if unit is available for assignment
     */
    public boolean isAvailable() {
        return isActive && status == UnitStatus.AVAILABLE;
    }

    /**
     * Domain logic: Assign unit to a driver/assignment
     */
    public void assign(String driverId, String assignmentId) {
        if (!isAvailable()) {
            throw new IllegalStateException("Unit is not available for assignment");
        }
        this.currentDriverId = driverId;
        this.currentAssignmentId = assignmentId;
        this.status = UnitStatus.ASSIGNED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Domain logic: Release unit from assignment
     */
    public void release() {
        this.currentDriverId = null;
        this.currentAssignmentId = null;
        this.status = UnitStatus.AVAILABLE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Domain logic: Validate fleet unit
     */
    public void validate() {
        if (unitName == null || unitName.isBlank()) {
            throw new IllegalArgumentException("Unit name cannot be blank");
        }
        if (unitType == null) {
            throw new IllegalArgumentException("Unit type is required");
        }
        if (organizationId == null || organizationId.isBlank()) {
            throw new IllegalArgumentException("Organization ID is required");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }
    }

    /**
     * Domain logic: Soft delete unit
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private Double latitude;
        private Double longitude;
        private String address;
        private LocalDateTime timestamp;
    }

    public enum UnitType {
        VEHICLE,
        EQUIPMENT,
        PERSONNEL,
        TRAILER,
        CONTAINER
    }

    public enum UnitStatus {
        AVAILABLE,
        ASSIGNED,
        IN_TRANSIT,
        MAINTENANCE,
        OUT_OF_SERVICE,
        RETIRED
    }
}
