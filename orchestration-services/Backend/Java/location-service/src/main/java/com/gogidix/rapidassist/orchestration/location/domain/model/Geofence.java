package com.gogidix.rapidassist.orchestration.location.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Geofence definition for geographic boundaries
 * Supports polygon and circle shapes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "geofences")
public class Geofence {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private GeofenceType type;

    /**
     * For CIRCLE: [longitude, latitude]
     * For POLYGON: Not used, use geometry instead
     */
    @Indexed
    private Map<String, Object> center;

    /**
     * Radius in meters (only for CIRCLE type)
     */
    private Double radius;

    /**
     * GeoJSON Polygon for complex shapes
     * Format: { "type": "Polygon", "coordinates": [[[lon1, lat1], [lon2, lat2], ...]] }
     */
    private Map<String, Object> geometry;

    private List<GeofenceRule> rules;

    @Indexed
    private GeofenceStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;

    @Builder.Default
    private Boolean monitorEntry = true;

    @Builder.Default
    private Boolean monitorExit = true;

    @Builder.Default
    private Boolean monitorDwell = false;

    /**
     * Dwell time in milliseconds (only if monitorDwell is true)
     */
    private Integer dwellTimeThreshold;

    private List<String> monitoredEntityTypes; // VEHICLE, DRIVER, etc.
    private List<String> monitoredEntityIds; // Specific entities to monitor

    private Map<String, Object> metadata;
    private String category; // warehouse, customer_zone, restricted_area, etc.

    public enum GeofenceType {
        CIRCLE,
        POLYGON
    }

    public enum GeofenceStatus {
        ACTIVE,
        INACTIVE,
        EXPIRED,
        DRAFT
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeofenceRule {
        private String id;
        private String name;
        private GeofenceAction action;
        private String targetService; // dispatching, monitoring, etc.
        private Map<String, Object> parameters;
    }

    public enum GeofenceAction {
        ALERT,
        NOTIFY,
        LOG,
        BLOCK,
        REDIRECT
    }

    /**
     * Check if geofence is currently active
     */
    public boolean isActive() {
        return status == GeofenceStatus.ACTIVE
                && (expiresAt == null || expiresAt.isAfter(LocalDateTime.now()));
    }

    /**
     * Check if geofence monitors specific entity type
     */
    public boolean monitorsEntityType(com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType) {
        return monitoredEntityTypes == null
                || monitoredEntityTypes.isEmpty()
                || monitoredEntityTypes.contains(entityType.name());
    }

    /**
     * Check if geofence monitors specific entity
     */
    public boolean monitorsEntity(String entityId) {
        return monitoredEntityIds == null
                || monitoredEntityIds.isEmpty()
                || monitoredEntityIds.contains(entityId);
    }
}
