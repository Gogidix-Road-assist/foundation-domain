package com.gogidix.rapidassist.crossdomain.bridge.adapters.in.web;

import com.gogidix.rapidassist.crossdomain.bridge.application.MechanicsInsuranceBridgeService;
import com.gogidix.rapidassist.crossdomain.bridge.domain.model.ClaimValidationRequest;
import com.gogidix.rapidassist.crossdomain.bridge.domain.model.ClaimValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bridge/mechanics-insurance")
@Slf4j
@RequiredArgsConstructor
public class MechanicsInsuranceBridgeController {

    private final MechanicsInsuranceBridgeService bridgeService;

    // ==================== CLAIM VALIDATION (Called by Insurance) ====================

    @PostMapping("/claims/validate")
    public ResponseEntity<ClaimValidationResponse> validateClaim(
        @RequestBody ClaimValidationRequest request
    ) {
        log.info("Validating claim {} for policy {}",
            request.getClaimId(), request.getPolicyNumber());

        ClaimValidationResponse response = bridgeService.validateClaimWithMechanics(request);
        return ResponseEntity.ok(response);
    }

    // ==================== CLAIM CREATION (Called by Mechanics) ====================

    @PostMapping("/claims/create")
    public ResponseEntity<ClaimValidationResponse> createClaimFromMechanics(
        @RequestBody Map<String, Object> request
    ) {
        String bookingId = (String) request.get("bookingId");
        String policyNumber = (String) request.get("policyNumber");
        ClaimValidationRequest claimDetails = (ClaimValidationRequest) request.get("claimDetails");

        log.info("Creating insurance claim from mechanics booking {}", bookingId);

        ClaimValidationResponse response = bridgeService.createInsuranceClaimFromMechanics(
            bookingId, policyNumber, claimDetails
        );
        return ResponseEntity.ok(response);
    }

    // ==================== CLAIM STATUS (Called by Mechanics) ====================

    @GetMapping("/claims/{claimId}/status")
    public ResponseEntity<String> getClaimStatus(@PathVariable String claimId) {
        log.info("Fetching status for claim {}", claimId);

        String status = bridgeService.getClaimStatus(claimId);
        return ResponseEntity.ok(status);
    }

    // ==================== CLAIM UPDATE (Called by Mechanics) ====================

    @PutMapping("/claims/{claimId}/service-details")
    public ResponseEntity<ClaimValidationResponse> updateClaimWithServiceDetails(
        @PathVariable String claimId,
        @RequestBody MechanicsInsuranceBridgeService.ServiceCompletionDetails details
    ) {
        log.info("Updating claim {} with service details", claimId);

        ClaimValidationResponse response = bridgeService.updateClaimWithServiceDetails(claimId, details);
        return ResponseEntity.ok(response);
    }

    // ==================== HEALTH CHECK ====================

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "service", "mechanics-insurance-bridge-service",
            "status", "UP",
            "description", "Cross-domain bridge between Mechanics and Insurance"
        ));
    }
}
