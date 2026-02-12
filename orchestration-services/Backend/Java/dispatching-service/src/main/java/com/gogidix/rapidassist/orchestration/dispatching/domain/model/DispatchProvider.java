package com.gogidix.rapidassist.orchestration.dispatching.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dispatch_providers")
public class DispatchProvider {

    @Id
    private String id;

    @Indexed(unique = true)
    private String providerId;

    @Indexed
    private String tenantId;

    private String userId;
    private String organizationId;

    private List<String> serviceTypes;

    private List<String> capabilities;

    private CurrentLocation currentLocation;

    @Indexed
    private ProviderStatus currentStatus;

    private Integer currentLoad;
    private Integer maxConcurrentJobs;

    private Double averageRating;
    private Integer totalCompletedJobs;

    private OperatingHours operatingHours;

    private ServiceArea serviceArea;

    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum ProviderStatus {
        AVAILABLE, BUSY, OFFLINE, ON_BREAK, UNAVAILABLE
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentLocation {
        private Double latitude;
        private Double longitude;
        private String address;
        private LocalDateTime timestamp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperatingHours {
        private String start; // "06:00"
        private String end;   // "22:00"
        private String timezone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceArea {
        private String type; // "Polygon" or "Circle"
        private List<List<Double>> coordinates;
        private Double radius; // for Circle type
        private Double centerLatitude;
        private Double centerLongitude;
    }
}
