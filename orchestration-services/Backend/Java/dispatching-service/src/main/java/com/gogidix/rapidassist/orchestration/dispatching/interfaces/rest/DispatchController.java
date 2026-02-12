package com.gogidix.rapidassist.orchestration.dispatching.interfaces.rest;

import com.gogidix.rapidassist.orchestration.dispatching.application.dto.*;
import com.gogidix.rapidassist.orchestration.dispatching.application.service.DispatchService;
import com.gogidix.rapidassist.orchestration.dispatching.domain.model.DispatchProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/dispatches")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    @PostMapping
    public ResponseEntity<DispatchResponse> createDispatch(@Valid @RequestBody CreateDispatchRequest request) {
        log.info("REST request to create dispatch for request: {}", request.getRequestId());
        DispatchResponse response = dispatchService.createDispatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{dispatchId}")
    public ResponseEntity<DispatchResponse> getDispatch(@PathVariable String dispatchId) {
        log.info("REST request to get dispatch: {}", dispatchId);
        DispatchResponse response = dispatchService.getDispatch(dispatchId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DispatchResponse>> getDispatchesByRequest(
            @RequestParam String requestId) {
        log.info("REST request to get dispatches for request: {}", requestId);
        List<DispatchResponse> responses = dispatchService.getDispatchesByRequest(requestId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tenant/{tenantId}/active")
    public ResponseEntity<List<DispatchResponse>> getActiveDispatchesByTenant(
            @PathVariable String tenantId) {
        log.info("REST request to get active dispatches for tenant: {}", tenantId);
        List<DispatchResponse> responses = dispatchService.getActiveDispatchesByTenant(tenantId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{dispatchId}/status")
    public ResponseEntity<DispatchResponse> updateDispatchStatus(
            @PathVariable String dispatchId,
            @Valid @RequestBody UpdateDispatchStatusRequest request) {
        log.info("REST request to update status for dispatch: {}", dispatchId);
        DispatchResponse response = dispatchService.updateDispatchStatus(dispatchId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{dispatchId}/assignments")
    public ResponseEntity<AssignmentResponse> assignProvider(
            @PathVariable String dispatchId,
            @Valid @RequestBody ProviderAssignmentRequest request) {
        log.info("REST request to assign provider {} to dispatch: {}", request.getProviderId(), dispatchId);
        AssignmentResponse response = dispatchService.assignProvider(dispatchId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{dispatchId}/assignments")
    public ResponseEntity<List<AssignmentResponse>> getAssignments(
            @PathVariable String dispatchId) {
        log.info("REST request to get assignments for dispatch: {}", dispatchId);
        List<AssignmentResponse> responses = dispatchService.getAssignmentsByDispatch(dispatchId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{dispatchId}/assignments/{providerId}/accept")
    public ResponseEntity<AssignmentResponse> acceptAssignment(
            @PathVariable String dispatchId,
            @PathVariable String providerId) {
        log.info("REST request to accept assignment for dispatch: {} by provider: {}", dispatchId, providerId);
        AssignmentResponse response = dispatchService.acceptAssignment(dispatchId, providerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{dispatchId}/assignments/{providerId}/reject")
    public ResponseEntity<AssignmentResponse> rejectAssignment(
            @PathVariable String dispatchId,
            @PathVariable String providerId,
            @RequestParam(required = false) String reason) {
        log.info("REST request to reject assignment for dispatch: {} by provider: {}", dispatchId, providerId);
        AssignmentResponse response = dispatchService.rejectAssignment(dispatchId, providerId, reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/providers/{providerId}/assignments")
    public ResponseEntity<List<AssignmentResponse>> getProviderAssignments(
            @PathVariable String providerId) {
        log.info("REST request to get assignments for provider: {}", providerId);
        List<AssignmentResponse> responses = dispatchService.getProviderAssignments(providerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/providers/{providerId}/active")
    public ResponseEntity<List<DispatchResponse>> getProviderActiveDispatches(
            @PathVariable String providerId) {
        log.info("REST request to get active dispatches for provider: {}", providerId);
        List<DispatchResponse> responses = dispatchService.getProviderActiveDispatches(providerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/providers/available")
    public ResponseEntity<List<DispatchProvider>> getAvailableProviders(
            @RequestParam String tenantId) {
        log.info("REST request to get available providers for tenant: {}", tenantId);
        List<DispatchProvider> providers = dispatchService.getAvailableProviders(tenantId);
        return ResponseEntity.ok(providers);
    }

    @PutMapping("/providers/{providerId}/location")
    public ResponseEntity<Void> updateProviderLocation(
            @PathVariable String providerId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) String address) {
        log.info("REST request to update location for provider: {}", providerId);
        dispatchService.updateProviderLocation(providerId, latitude, longitude, address);
        return ResponseEntity.ok().build();
    }
}
