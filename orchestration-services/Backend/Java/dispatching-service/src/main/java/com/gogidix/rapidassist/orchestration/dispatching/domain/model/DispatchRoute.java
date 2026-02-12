package com.gogidix.rapidassist.orchestration.dispatching.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dispatch_routes")
public class DispatchRoute {

    @Id
    private String id;

    @Indexed
    private String dispatchId;

    @Indexed
    private RouteStatus status;

    private List<Waypoint> waypoints;

    private Double totalDistance; // in meters
    private Integer estimatedDuration; // in seconds
    private Integer actualDuration; // in seconds

    private String polyline; // encoded polyline for map display

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    @Indexed
    private String tenantId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum RouteStatus {
        PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Waypoint {
        private String waypointId;
        private String location;
        private Double latitude;
        private Double longitude;
        private String address;
        private Integer order;
        private LocalDateTime estimatedArrival;
        private LocalDateTime actualArrival;
        private Boolean visited;
    }
}
