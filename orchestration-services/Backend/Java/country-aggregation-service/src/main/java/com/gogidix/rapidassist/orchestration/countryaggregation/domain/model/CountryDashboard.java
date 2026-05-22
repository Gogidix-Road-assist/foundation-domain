package com.gogidix.rapidassist.orchestration.countryaggregation.domain.model;

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
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "country_dashboards")
public class CountryDashboard {

    @Id
    private String id;

    @Indexed(unique = true)
    private String countryCode;

    private String countryName;

    private OperationalMetrics operationalMetrics;
    private FinancialMetrics financialMetrics;
    private ServiceMetrics serviceMetrics;

    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    @Indexed
    private String tenantId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationalMetrics {
        private Integer activeProviders;
        private Integer activeIncidents;
        private Integer pendingClaims;
        private Double avgResponseTimeMinutes;
        private Integer activeEmergencies;
        private Integer crossBorderOperations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialMetrics {
        private Double dailyRevenue;
        private Double monthlyRevenue;
        private Double totalPayouts;
        private Integer activeSubscriptions;
        private Double avgClaimValue;
        private String currency;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceMetrics {
        private Integer totalServicesToday;
        private Integer completedToday;
        private Integer cancelledToday;
        private Double customerSatisfactionScore;
        private Double slaCompliancePercent;
        private Double firstResponseRate;
    }
}
