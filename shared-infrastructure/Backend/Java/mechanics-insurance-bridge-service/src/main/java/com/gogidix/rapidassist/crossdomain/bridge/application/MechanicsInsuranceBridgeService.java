package com.gogidix.rapidassist.crossdomain.bridge.application;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.Map;

import com.gogidix.rapidassist.crossdomain.bridge.domain.model.ClaimValidationRequest;
import com.gogidix.rapidassist.crossdomain.bridge.domain.model.ClaimValidationResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Data
public class MechanicsInsuranceBridgeService {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.insurance.claims-service}")
    private String insuranceClaimsService;

    @Value("${services.insurance.policy-service}")
    private String insurancePolicyService;

    @Value("${services.mechanics.base-url}")
    private String mechanicsService;

    /**
     * Validate insurance claim with Mechanics service details
     * Called by Insurance domain to validate mechanic service details
     */
    @CircuitBreaker(name = "mechanicsService", fallbackMethod = "validateClaimWithMechanicsFallback")
    @Retry(name = "mechanicsService")
    public ClaimValidationResponse validateClaimWithMechanics(ClaimValidationRequest request) {
        log.info("Validating claim {} with mechanics service", request.getClaimId());

        try {
            // 1. Verify workshop is approved
            boolean workshopApproved = verifyWorkshopApproval(request.getWorkshopId());

            // 2. Verify mechanic is certified
            boolean mechanicCertified = verifyMechanicCertification(request.getMechanicId());

            // 3. Validate service costs against market rates
            boolean costsReasonable = validateServiceCosts(request.getServiceItems());

            // 4. Get insurance policy validation
            ClaimValidationResponse policyValidation = validatePolicy(request);

            // 5. Build final validation response
            return ClaimValidationResponse.builder()
                .claimId(request.getClaimId())
                .valid(workshopApproved && mechanicCertified && costsReasonable && policyValidation.isValid())
                .validationResult(ClaimValidationResponse.ValidationResult.builder()
                    .policyActive(policyValidation.getValidationResult().isPolicyActive())
                    .coverageValid(policyValidation.getValidationResult().isCoverageValid())
                    .withinLimit(policyValidation.getValidationResult().isWithinLimit())
                    .workshopApproved(workshopApproved)
                    .coverageType(policyValidation.getValidationResult().getCoverageType())
                    .coverageLimit(policyValidation.getValidationResult().getCoverageLimit())
                    .remainingLimit(policyValidation.getValidationResult().getRemainingLimit())
                    .deductible(policyValidation.getValidationResult().getDeductible())
                    .build())
                .status(workshopApproved && mechanicCertified && costsReasonable
                    ? "APPROVED"
                    : "REQUIRES_INFO")
                .approvedAmount(calculateApprovedAmount(request, policyValidation))
                .issues(collectValidationIssues(workshopApproved, mechanicCertified, costsReasonable))
                .validatedAt(LocalDateTime.now())
                .validatedBy("MECHANICS_INSURANCE_BRIDGE")
                .build();

        } catch (Exception e) {
            log.error("Error validating claim with mechanics: {}", e.getMessage(), e);
            return ClaimValidationResponse.builder()
                .claimId(request.getClaimId())
                .valid(false)
                .status("REJECTED")
                .rejectionReason("Validation error: " + e.getMessage())
                .validatedAt(LocalDateTime.now())
                .build();
        }
    }

    /**
     * Create claim in Insurance system from Mechanics service completion
     * Called by Mechanics domain when service is completed
     */
    @CircuitBreaker(name = "insuranceService", fallbackMethod = "createInsuranceClaimFallback")
    @Retry(name = "insuranceService")
    public ClaimValidationResponse createInsuranceClaimFromMechanics(
        String bookingId,
        String policyNumber,
        ClaimValidationRequest claimDetails
    ) {
        log.info("Creating insurance claim from mechanics booking {}", bookingId);

        return webClientBuilder.build()
            .post()
            .uri(insuranceClaimsService + "/api/claims/from-mechanics")
            .bodyValue(Map.of(
                "bookingId", bookingId,
                "policyNumber", policyNumber,
                "claimDetails", claimDetails
            ))
            .retrieve()
            .bodyToMono(ClaimValidationResponse.class)
            .block();
    }

    /**
     * Get claim status from Insurance system
     * Called by Mechanics domain to check claim status
     */
    @CircuitBreaker(name = "insuranceService", fallbackMethod = "getClaimStatusFallback")
    @Retry(name = "insuranceService")
    public String getClaimStatus(String claimId) {
        log.info("Fetching claim status for {}", claimId);

        return webClientBuilder.build()
            .get()
            .uri(insuranceClaimsService + "/api/claims/" + claimId + "/status")
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    /**
     * Update claim with mechanic service details
     * Called by Mechanics domain to provide service completion details
     */
    @CircuitBreaker(name = "insuranceService", fallbackMethod = "updateClaimFallback")
    @Retry(name = "insuranceService")
    public ClaimValidationResponse updateClaimWithServiceDetails(
        String claimId,
        ServiceCompletionDetails details
    ) {
        log.info("Updating claim {} with service details", claimId);

        return webClientBuilder.build()
            .put()
            .uri(insuranceClaimsService + "/api/claims/" + claimId + "/service-details")
            .bodyValue(details)
            .retrieve()
            .bodyToMono(ClaimValidationResponse.class)
            .block();
    }

    // ==================== PRIVATE METHODS ====================

    private boolean verifyWorkshopApproval(String workshopId) {
        try {
            return Boolean.TRUE.equals(
                webClientBuilder.build()
                    .get()
                    .uri(mechanicsService + "/api/workshops/" + workshopId + "/approved")
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block()
            );
        } catch (Exception e) {
            log.error("Failed to verify workshop approval: {}", e.getMessage());
            return false;
        }
    }

    private boolean verifyMechanicCertification(String mechanicId) {
        try {
            return Boolean.TRUE.equals(
                webClientBuilder.build()
                    .get()
                    .uri(mechanicsService + "/api/mechanics/" + mechanicId + "/certified")
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block()
            );
        } catch (Exception e) {
            log.error("Failed to verify mechanic certification: {}", e.getMessage());
            return false;
        }
    }

    private boolean validateServiceCosts(List<ClaimValidationRequest.ServiceItem> items) {
        // Simplified cost validation - in production, compare against market rates database
        return items != null && !items.isEmpty() &&
               items.stream().allMatch(item ->
                   item.getUnitPrice().compareTo(BigDecimal.ZERO) > 0 &&
                   item.getTotalPrice().compareTo(BigDecimal.valueOf(10000)) < 0 // Max €10,000 per item
               );
    }

    private ClaimValidationResponse validatePolicy(ClaimValidationRequest request) {
        try {
            return webClientBuilder.build()
                .post()
                .uri(insurancePolicyService + "/api/policies/validate")
                .bodyValue(Map.of(
                    "policyNumber", request.getPolicyNumber(),
                    "customerId", request.getCustomerId(),
                    "vehicleRegistration", request.getVehicleRegistration(),
                    "estimatedCost", request.getEstimatedCost()
                ))
                .retrieve()
                .bodyToMono(ClaimValidationResponse.class)
                .block();
        } catch (Exception e) {
            log.error("Failed to validate policy: {}", e.getMessage());
            return ClaimValidationResponse.builder()
                .valid(false)
                .validationResult(ClaimValidationResponse.ValidationResult.builder()
                    .policyActive(false)
                    .build())
                .build();
        }
    }

    private BigDecimal calculateApprovedAmount(
        ClaimValidationRequest request,
        ClaimValidationResponse policyValidation
    ) {
        BigDecimal coverageLimit = policyValidation.getValidationResult().getRemainingLimit();
        BigDecimal estimatedCost = request.getEstimatedCost();
        BigDecimal deductible = policyValidation.getValidationResult().getDeductible();

        // Approved amount = min(coverage, cost) - deductible
        BigDecimal approved = estimatedCost.min(coverageLimit);
        approved = approved.subtract(deductible);

        return approved.max(BigDecimal.ZERO);
    }

    private List<ClaimValidationResponse.ValidationIssue> collectValidationIssues(
        boolean workshopApproved,
        boolean mechanicCertified,
        boolean costsReasonable
    ) {
        List<ClaimValidationResponse.ValidationIssue> issues = new ArrayList<>();

        if (!workshopApproved) {
            issues.add(ClaimValidationResponse.ValidationIssue.builder()
                .code("WORKSHOP_NOT_APPROVED")
                .severity("ERROR")
                .message("Workshop is not approved for insurance work")
                .field("workshopId")
                .build());
        }

        if (!mechanicCertified) {
            issues.add(ClaimValidationResponse.ValidationIssue.builder()
                .code("MECHANIC_NOT_CERTIFIED")
                .severity("ERROR")
                .message("Mechanic is not certified")
                .field("mechanicId")
                .build());
        }

        if (!costsReasonable) {
            issues.add(ClaimValidationResponse.ValidationIssue.builder()
                .code("COSTS_EXCEED_LIMITS")
                .severity("WARNING")
                .message("Service costs exceed reasonable market rates")
                .field("serviceItems")
                .build());
        }

        return issues;
    }

    // ==================== FALLBACK METHODS ====================

    private ClaimValidationResponse validateClaimWithMechanicsFallback(ClaimValidationRequest request, Exception e) {
        log.error("Fallback triggered for validateClaimWithMechanics: {}", e.getMessage());
        return ClaimValidationResponse.builder()
            .claimId(request.getClaimId())
            .valid(false)
            .status("PENDING")
            .rejectionReason("Unable to validate with mechanics service. Please try again later.")
            .validatedAt(LocalDateTime.now())
            .build();
    }

    private ClaimValidationResponse createInsuranceClaimFallback(String bookingId, String policyNumber, ClaimValidationRequest details, Exception e) {
        log.error("Fallback triggered for createInsuranceClaim: {}", e.getMessage());
        return ClaimValidationResponse.builder()
            .valid(false)
            .status("PENDING")
            .rejectionReason("Unable to create claim. Please retry.")
            .build();
    }

    private String getClaimStatusFallback(String claimId, Exception e) {
        log.error("Fallback triggered for getClaimStatus: {}", e.getMessage());
        return "UNKNOWN";
    }

    private ClaimValidationResponse updateClaimFallback(String claimId, ServiceCompletionDetails details, Exception e) {
        log.error("Fallback triggered for updateClaim: {}", e.getMessage());
        return ClaimValidationResponse.builder()
            .claimId(claimId)
            .valid(false)
            .status("PENDING")
            .rejectionReason("Unable to update claim. Please retry.")
            .build();
    }

    // ==================== DTOs ====================

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ServiceCompletionDetails {
        private String bookingId;
        private String mechanicId;
        private String workshopId;
        private List<ServiceItem> servicesPerformed;
        private BigDecimal totalCost;
        private LocalDateTime completionTime;
        private List<String> photoUrls;
        private String notes;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ServiceItem {
        private String itemCode;
        private String description;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}
