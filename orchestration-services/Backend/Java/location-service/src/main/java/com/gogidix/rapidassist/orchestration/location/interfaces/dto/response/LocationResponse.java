package com.gogidix.rapidassist.orchestration.location.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for Location
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {

    private String id;
    private String tenantId;
    private String entityType;
    private String entityId;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double accuracy;
    private Double bearing;
    private Double speed;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String status;
    private LocalDateTime timestamp;
    private LocalDateTime lastUpdated;
    private Boolean active;
    private Map<String, Object> metadata;
    private String provider;
}
