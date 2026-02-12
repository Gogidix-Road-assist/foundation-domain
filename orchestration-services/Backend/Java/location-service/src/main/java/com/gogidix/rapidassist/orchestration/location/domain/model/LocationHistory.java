package com.gogidix.rapidassist.orchestration.location.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Historical location trail for entities
 * Stores past positions for route reconstruction and analytics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "location_history")
public class LocationHistory {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType;

    @Indexed
    private String entityId;

    /**
     * GeoJSON Point for geospatial queries
     */
    @Indexed
    private Map<String, Object> coordinates;

    private Double latitude;
    private Double longitude;

    private Double altitude;
    private Double accuracy;
    private Double bearing;
    private Double speed;

    private LocalDateTime timestamp;

    private String address;
    private String city;
    private String state;
    private String country;

    private String sessionId; // For grouping continuous tracking sessions
    private Integer sequenceNumber; // Order within session

    private Map<String, Object> metadata;

    /**
     * Factory method to create history from current location
     */
    public static LocationHistory fromLocation(Location location) {
        return LocationHistory.builder()
                .tenantId(location.getTenantId())
                .entityType(location.getEntityType())
                .entityId(location.getEntityId())
                .coordinates(location.getCoordinates())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .altitude(location.getAltitude())
                .accuracy(location.getAccuracy())
                .bearing(location.getBearing())
                .speed(location.getSpeed())
                .timestamp(location.getTimestamp())
                .address(location.getAddress())
                .city(location.getCity())
                .state(location.getState())
                .country(location.getCountry())
                .metadata(location.getMetadata())
                .build();
    }
}
