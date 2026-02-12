package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityProvider {

    @Id
    private String id;

    @Indexed
    private String providerId;

    @Indexed
    private String tenantId;

    @Indexed
    private ProviderStatus status;

    private String name;
    private String email;
    private String phone;

    private Location currentLocation;

    public ProviderStatus getCurrentStatus() {
        return status;
    }

    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
    }

    private Boolean isAvailable;
    private Boolean isOnline;
    private LocalDateTime lastHeartbeat;

    private Double averageRating;
    private Integer totalAssignments;
    private Integer completedAssignments;

    private Map<String, Object> capabilities;
    private String serviceArea;

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

    public enum ProviderStatus {
        AVAILABLE, BUSY, OFFLINE, INACTIVE
    }
}
