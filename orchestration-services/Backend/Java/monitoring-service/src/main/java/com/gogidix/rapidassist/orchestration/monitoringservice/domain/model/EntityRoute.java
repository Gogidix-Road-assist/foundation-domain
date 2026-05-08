package com.gogidix.rapidassist.orchestration.monitoringservice.domain.model;

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
@Document(collection = "entity_routes")
public class EntityRoute {

    @Id
    private String id;

    @Indexed
    private String entityId;

    @Indexed
    private String providerId;

    private RouteStatus status;

    private Location startPoint;
    private Location endPoint;
    private List<Location> waypoints;

    private Double totalDistance;
    private Integer estimatedDuration;
    private Integer actualDuration;

    private Map<String, Object> metadata;

    @Indexed
    private String tenantId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private Double latitude;
        private Double longitude;
        private String address;
    }

    public enum RouteStatus {
        PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
