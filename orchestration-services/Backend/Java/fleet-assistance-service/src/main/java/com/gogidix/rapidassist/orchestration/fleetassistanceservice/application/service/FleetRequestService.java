package com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.service;

import com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.dto.request.CreateFleetRequestDto;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.dto.response.FleetRequestResponseDto;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.mapper.FleetRequestMapper;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.AssistanceHistory;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.FleetRequest;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.repository.AssistanceHistoryRepository;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.repository.FleetRequestRepository;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.shared.exception.NotFoundException;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.shared.requestcontext.RequestContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing fleet assistance requests
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FleetRequestService {

    private final FleetRequestRepository repository;
    private final AssistanceHistoryRepository historyRepository;
    private final FleetRequestMapper mapper;

    /**
     * Create a new fleet assistance request
     */
    @Transactional
    public FleetRequestResponseDto createRequest(CreateFleetRequestDto dto) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Creating fleet request for tenant: {}, fleet: {}", tenantId, dto.getFleetId());

        // Convert DTO location to domain location
        FleetRequest.Location location = FleetRequest.Location.builder()
                .latitude(dto.getLocation().getLatitude())
                .longitude(dto.getLocation().getLongitude())
                .address(dto.getLocation().getAddress())
                .build();

        FleetRequest request = FleetRequest.create(
                tenantId,
                dto.getFleetId(),
                dto.getServiceType(),
                dto.getPriority(),
                location
        );

        if (dto.getVehicleId() != null) {
            request.setVehicleId(dto.getVehicleId());
        }

        FleetRequest saved = repository.save(request);

        // Create history entry
        AssistanceHistory history = AssistanceHistory.create(
                tenantId,
                saved.getRequestId(),
                saved.getFleetId(),
                saved.getServiceType(),
                "REQUEST_CREATED"
        );
        historyRepository.save(history);

        log.info("Created fleet request: {}", saved.getRequestId());
        return mapper.toResponseDto(saved);
    }

    /**
     * Get request by ID
     */
    public FleetRequestResponseDto getRequest(String requestId) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Fetching fleet request: {} for tenant: {}", requestId, tenantId);

        FleetRequest request = repository.findByRequestIdAndTenantIdAndDeletedAtIsNull(requestId, tenantId)
                .orElseThrow(() -> new NotFoundException("Fleet request not found: " + requestId));

        return mapper.toResponseDto(request);
    }

    /**
     * Get all requests for tenant
     */
    public List<FleetRequestResponseDto> getAllRequests() {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Fetching all fleet requests for tenant: {}", tenantId);

        return repository.findByTenantIdAndDeletedAtIsNull(tenantId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get requests by fleet ID
     */
    public List<FleetRequestResponseDto> getRequestsByFleet(String fleetId) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Fetching fleet requests for fleet: {} and tenant: {}", fleetId, tenantId);

        return repository.findByFleetIdAndTenantIdAndDeletedAtIsNull(fleetId, tenantId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get requests by status
     */
    public List<FleetRequestResponseDto> getRequestsByStatus(FleetRequest.RequestStatus status) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Fetching {} fleet requests for tenant: {}", status, tenantId);

        return repository.findByStatusAndTenantIdAndDeletedAtIsNull(status, tenantId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Assign provider to request
     */
    @Transactional
    public FleetRequestResponseDto assignProvider(String requestId, String providerId) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Assigning provider {} to request {} for tenant: {}", providerId, requestId, tenantId);

        FleetRequest request = repository.findByRequestIdAndTenantIdAndDeletedAtIsNull(requestId, tenantId)
                .orElseThrow(() -> new NotFoundException("Fleet request not found: " + requestId));

        request.assignProvider(providerId, java.time.Instant.now().plusSeconds(1800)); // 30 min ETA
        FleetRequest updated = repository.save(request);

        // Create history entry
        AssistanceHistory history = AssistanceHistory.create(
                tenantId,
                updated.getRequestId(),
                updated.getFleetId(),
                updated.getServiceType(),
                "PROVIDER_ASSIGNED"
        );
        history.setProviderId(providerId);
        historyRepository.save(history);

        return mapper.toResponseDto(updated);
    }

    /**
     * Start service
     */
    @Transactional
    public FleetRequestResponseDto startService(String requestId) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Starting service for request: {} and tenant: {}", requestId, tenantId);

        FleetRequest request = repository.findByRequestIdAndTenantIdAndDeletedAtIsNull(requestId, tenantId)
                .orElseThrow(() -> new NotFoundException("Fleet request not found: " + requestId));

        request.startService();
        FleetRequest updated = repository.save(request);

        // Create history entry
        AssistanceHistory history = AssistanceHistory.create(
                tenantId,
                updated.getRequestId(),
                updated.getFleetId(),
                updated.getServiceType(),
                "SERVICE_STARTED"
        );
        historyRepository.save(history);

        return mapper.toResponseDto(updated);
    }

    /**
     * Complete service
     */
    @Transactional
    public FleetRequestResponseDto completeService(String requestId) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Completing service for request: {} and tenant: {}", requestId, tenantId);

        FleetRequest request = repository.findByRequestIdAndTenantIdAndDeletedAtIsNull(requestId, tenantId)
                .orElseThrow(() -> new NotFoundException("Fleet request not found: " + requestId));

        request.completeService();
        FleetRequest updated = repository.save(request);

        // Create history entry
        AssistanceHistory history = AssistanceHistory.create(
                tenantId,
                updated.getRequestId(),
                updated.getFleetId(),
                updated.getServiceType(),
                "SERVICE_COMPLETED"
        );
        history.setProviderId(updated.getAssignedFleetProviderId());
        historyRepository.save(history);

        return mapper.toResponseDto(updated);
    }

    /**
     * Cancel request
     */
    @Transactional
    public FleetRequestResponseDto cancelRequest(String requestId) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Cancelling request: {} for tenant: {}", requestId, tenantId);

        FleetRequest request = repository.findByRequestIdAndTenantIdAndDeletedAtIsNull(requestId, tenantId)
                .orElseThrow(() -> new NotFoundException("Fleet request not found: " + requestId));

        request.cancel();
        FleetRequest updated = repository.save(request);

        // Create history entry
        AssistanceHistory history = AssistanceHistory.create(
                tenantId,
                updated.getRequestId(),
                updated.getFleetId(),
                updated.getServiceType(),
                "REQUEST_CANCELLED"
        );
        historyRepository.save(history);

        return mapper.toResponseDto(updated);
    }

    /**
     * Delete request (soft delete)
     */
    @Transactional
    public void deleteRequest(String requestId) {
        String tenantId = RequestContextHolder.getTenantId();
        log.info("Deleting request: {} for tenant: {}", requestId, tenantId);

        FleetRequest request = repository.findByRequestIdAndTenantIdAndDeletedAtIsNull(requestId, tenantId)
                .orElseThrow(() -> new NotFoundException("Fleet request not found: " + requestId));

        request.softDelete();
        repository.save(request);

        log.info("Deleted fleet request: {}", requestId);
    }
}
