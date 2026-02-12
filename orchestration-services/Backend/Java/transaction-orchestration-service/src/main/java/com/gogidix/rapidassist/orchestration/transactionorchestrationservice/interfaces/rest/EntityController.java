package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.interfaces.rest;

import com.gogidix.rapidassist.orchestration.transactionorchestrationservice.application.dto.*;
import com.gogidix.rapidassist.orchestration.transactionorchestrationservice.application.service.EntityService;
import com.gogidix.rapidassist.orchestration.transactionorchestrationservice.domain.model.EntityProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/entityes")
@RequiredArgsConstructor
public class EntityController {

    private final EntityService entityService;

    @PostMapping
    public ResponseEntity<EntityResponse> createEntity(@Valid @RequestBody CreateEntityRequest request) {
        log.info("REST request to create entity for request: {}", request.getRequestId());
        EntityResponse response = entityService.createEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{entityId}")
    public ResponseEntity<EntityResponse> getEntity(@PathVariable String entityId) {
        log.info("REST request to get entity: {}", entityId);
        EntityResponse response = entityService.getEntity(entityId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EntityResponse>> getEntityesByRequest(
            @RequestParam String requestId) {
        log.info("REST request to get entityes for request: {}", requestId);
        List<EntityResponse> responses = entityService.getEntityesByRequest(requestId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tenant/{tenantId}/active")
    public ResponseEntity<List<EntityResponse>> getActiveEntityesByTenant(
            @PathVariable String tenantId) {
        log.info("REST request to get active entityes for tenant: {}", tenantId);
        List<EntityResponse> responses = entityService.getActiveEntityesByTenant(tenantId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{entityId}/status")
    public ResponseEntity<EntityResponse> updateEntityStatus(
            @PathVariable String entityId,
            @Valid @RequestBody UpdateEntityStatusRequest request) {
        log.info("REST request to update status for entity: {}", entityId);
        EntityResponse response = entityService.updateEntityStatus(entityId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{entityId}/assignments")
    public ResponseEntity<AssignmentResponse> assignProvider(
            @PathVariable String entityId,
            @Valid @RequestBody ProviderAssignmentRequest request) {
        log.info("REST request to assign provider {} to entity: {}", request.getProviderId(), entityId);
        AssignmentResponse response = entityService.assignProvider(entityId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{entityId}/assignments")
    public ResponseEntity<List<AssignmentResponse>> getAssignments(
            @PathVariable String entityId) {
        log.info("REST request to get assignments for entity: {}", entityId);
        List<AssignmentResponse> responses = entityService.getAssignmentsByEntity(entityId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{entityId}/assignments/{providerId}/accept")
    public ResponseEntity<AssignmentResponse> acceptAssignment(
            @PathVariable String entityId,
            @PathVariable String providerId) {
        log.info("REST request to accept assignment for entity: {} by provider: {}", entityId, providerId);
        AssignmentResponse response = entityService.acceptAssignment(entityId, providerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{entityId}/assignments/{providerId}/reject")
    public ResponseEntity<AssignmentResponse> rejectAssignment(
            @PathVariable String entityId,
            @PathVariable String providerId,
            @RequestParam(required = false) String reason) {
        log.info("REST request to reject assignment for entity: {} by provider: {}", entityId, providerId);
        AssignmentResponse response = entityService.rejectAssignment(entityId, providerId, reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/providers/{providerId}/assignments")
    public ResponseEntity<List<AssignmentResponse>> getProviderAssignments(
            @PathVariable String providerId) {
        log.info("REST request to get assignments for provider: {}", providerId);
        List<AssignmentResponse> responses = entityService.getProviderAssignments(providerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/providers/{providerId}/active")
    public ResponseEntity<List<EntityResponse>> getProviderActiveEntityes(
            @PathVariable String providerId) {
        log.info("REST request to get active entityes for provider: {}", providerId);
        List<EntityResponse> responses = entityService.getProviderActiveEntityes(providerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/providers/available")
    public ResponseEntity<List<EntityProvider>> getAvailableProviders(
            @RequestParam String tenantId) {
        log.info("REST request to get available providers for tenant: {}", tenantId);
        List<EntityProvider> providers = entityService.getAvailableProviders(tenantId);
        return ResponseEntity.ok(providers);
    }

    @PutMapping("/providers/{providerId}/location")
    public ResponseEntity<Void> updateProviderLocation(
            @PathVariable String providerId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) String address) {
        log.info("REST request to update location for provider: {}", providerId);
        entityService.updateProviderLocation(providerId, latitude, longitude, address);
        return ResponseEntity.ok().build();
    }
}
