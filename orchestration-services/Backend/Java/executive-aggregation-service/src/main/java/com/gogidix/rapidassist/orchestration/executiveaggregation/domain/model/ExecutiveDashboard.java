package com.gogidix.rapidassist.orchestration.executiveaggregation.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
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
@Document(collection = "executive_dashboards")
public class ExecutiveDashboard {

    @Id
    private String id;

    @Indexed
    private String period;

    private GlobalKPIs globalKPIs;
    private List<CountrySummary> countrySummaries;
    private List<Alert> activeAlerts;
    private PlatformHealth platformHealth;

    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GlobalKPIs {
        private Integer totalCountries;
        private Integer totalActiveProviders;
        private Integer totalActiveIncidents;
        private Integer totalPendingClaims;
        private Double globalRevenue;
        private Double globalAvgResponseTimeMinutes;
        private Double globalCustomerSatisfaction;
        private Double globalSlaCompliance;
        private Integer totalActiveSubscriptions;
        private Integer totalServicesToday;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountrySummary {
        private String countryCode;
        private String countryName;
        private Double revenueContribution;
        private Integer activeProviders;
        private Integer activeIncidents;
        private Double slaCompliance;
        private String healthStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Alert {
        private String alertId;
        private String severity;
        private String countryCode;
        private String domain;
        private String message;
        private LocalDateTime triggeredAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlatformHealth {
        private Integer totalServices;
        private Integer healthyServices;
        private Integer degradedServices;
        private Integer downServices;
        private Double overallUptimePercent;
    }
}
