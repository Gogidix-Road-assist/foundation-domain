package com.gogidix.rapidassist.orchestration.fleet_policy.interfaces.rest;

import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.request.PolicyRequestDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.request.PolicyValidationRequestDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.response.PolicyComplianceResponseDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.response.PolicyResponseDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.response.PolicyViolationResponseDto;
import com.gogidix.rapidassist.orchestration.fleet_policy.application.service.PolicyService;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.Policy;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyCompliance;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyViolation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Policy management
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    /**
     * Create a new policy
     */
    @PostMapping
    public ResponseEntity<PolicyResponseDto> createPolicy(@Valid @RequestBody PolicyRequestDto dto) {
        log.info("Creating policy: {}", dto.getPolicyCode());
        PolicyResponseDto response = policyService.createPolicy(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing policy
     */
    @PutMapping("/{id}")
    public ResponseEntity<PolicyResponseDto> updatePolicy(
            @PathVariable String id,
            @Valid @RequestBody PolicyRequestDto dto
    ) {
        log.info("Updating policy: {}", id);
        PolicyResponseDto response = policyService.updatePolicy(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Get policy by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponseDto> getPolicyById(@PathVariable String id) {
        log.info("Fetching policy: {}", id);
        PolicyResponseDto response = policyService.getPolicyById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all policies
     */
    @GetMapping
    public ResponseEntity<List<PolicyResponseDto>> getAllPolicies() {
        log.info("Fetching all policies");
        List<PolicyResponseDto> response = policyService.getAllPolicies();
        return ResponseEntity.ok(response);
    }

    /**
     * Get active policies
     */
    @GetMapping("/active")
    public ResponseEntity<List<PolicyResponseDto>> getActivePolicies() {
        log.info("Fetching active policies");
        List<PolicyResponseDto> response = policyService.getActivePolicies();
        return ResponseEntity.ok(response);
    }

    /**
     * Get policies by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<PolicyResponseDto>> getPoliciesByType(@PathVariable Policy.PolicyType type) {
        log.info("Fetching policies by type: {}", type);
        List<PolicyResponseDto> response = policyService.getPoliciesByType(type);
        return ResponseEntity.ok(response);
    }

    /**
     * Activate a policy
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<PolicyResponseDto> activatePolicy(@PathVariable String id) {
        log.info("Activating policy: {}", id);
        PolicyResponseDto response = policyService.activatePolicy(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate a policy
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<PolicyResponseDto> deactivatePolicy(@PathVariable String id) {
        log.info("Deactivating policy: {}", id);
        PolicyResponseDto response = policyService.deactivatePolicy(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a policy
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable String id) {
        log.info("Deleting policy: {}", id);
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Validate against a policy
     */
    @PostMapping("/validate")
    public ResponseEntity<List<PolicyViolationResponseDto>> validatePolicy(
            @Valid @RequestBody PolicyValidationRequestDto dto
    ) {
        log.info("Validating policy: {} for entity: {}", dto.getPolicyId(), dto.getEntityId());
        List<PolicyViolationResponseDto> response = policyService.validatePolicy(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Get compliance records for an entity
     */
    @GetMapping("/compliance/{entityType}/{entityId}")
    public ResponseEntity<List<PolicyComplianceResponseDto>> getEntityCompliance(
            @PathVariable PolicyCompliance.ComplianceEntityType entityType,
            @PathVariable String entityId
    ) {
        log.info("Fetching compliance for entity: {} {}", entityType, entityId);
        List<PolicyComplianceResponseDto> response = policyService.getEntityCompliance(entityType, entityId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get violations for an entity
     */
    @GetMapping("/violations/{entityType}/{entityId}")
    public ResponseEntity<List<PolicyViolationResponseDto>> getEntityViolations(
            @PathVariable PolicyViolation.ViolationEntityType entityType,
            @PathVariable String entityId
    ) {
        log.info("Fetching violations for entity: {} {}", entityType, entityId);
        List<PolicyViolationResponseDto> response = policyService.getEntityViolations(entityType, entityId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get open violations for an entity
     */
    @GetMapping("/violations/{entityType}/{entityId}/open")
    public ResponseEntity<List<PolicyViolationResponseDto>> getOpenViolations(
            @PathVariable PolicyViolation.ViolationEntityType entityType,
            @PathVariable String entityId
    ) {
        log.info("Fetching open violations for entity: {} {}", entityType, entityId);
        List<PolicyViolationResponseDto> response = policyService.getOpenViolations(entityType, entityId);
        return ResponseEntity.ok(response);
    }

    /**
     * Search policies
     */
    @GetMapping("/search")
    public ResponseEntity<List<PolicyResponseDto>> searchPolicies(@RequestParam String q) {
        log.info("Searching policies: {}", q);
        List<PolicyResponseDto> response = policyService.searchPolicies(q);
        return ResponseEntity.ok(response);
    }
}
