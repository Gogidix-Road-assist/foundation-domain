package com.gogidix.rapidassist.crossdomain.bridge.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimValidationRequest {
    private String claimId;
    private String policyNumber;
    private String customerId;
    private String vehicleRegistration;
    private String serviceType; // REPAIR, TOWING, MOBILE_SERVICE
    private String workshopId;
    private String mechanicId;
    private List<ServiceItem> serviceItems;
    private BigDecimal estimatedCost;
    private String description;
    private LocalDateTime incidentDate;
    private String incidentLocation;
    private List<String> documentUrls; // Photos, invoices, etc.
    private String submittedBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceItem {
        private String itemCode;
        private String description;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}
