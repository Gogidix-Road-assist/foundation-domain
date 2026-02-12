package com.gogidix.rapidassist.orchestration.matching.interfaces.rest;

import com.gogidix.rapidassist.orchestration.matching.application.dto.request.CreateMatchingRequestDTO;
import com.gogidix.rapidassist.orchestration.matching.application.dto.response.MatchingResultResponseDTO;
import com.gogidix.rapidassist.orchestration.matching.application.service.MatchingService;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingAlgorithm;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult;
import com.gogidix.rapidassist.orchestration.matching.infrastructure.messaging.MatchingEventProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

/**
 * REST controller for matching operations
 */
@Slf4j
@RestController
@RequestMapping("/v1/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    private final MatchingEventProducer eventProducer;

    /**
     * Create and execute a new matching request
     */
    @PostMapping("/requests")
    public ResponseEntity<MatchingResultResponseDTO> createMatchingRequest(
        @Valid @RequestBody CreateMatchingRequestDTO requestDTO
    ) {
        log.info("Creating matching request for incident: {}", requestDTO.getIncidentId());

        MatchingRequest request = mapToMatchingRequest(requestDTO);
        MatchingResult result = matchingService.findProviders(request);

        // Publish events
        if (result.getStatus() == MatchingResult.ResultStatus.SUCCESS) {
            eventProducer.publishMatchingCompleted(result);
            eventProducer.publishProviderAssigned(result);
        } else if (result.getStatus() == MatchingResult.ResultStatus.NO_PROVIDERS_FOUND) {
            eventProducer.publishNoProvidersFound(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapToResponseDTO(result));
    }

    /**
     * Get matching result by request ID
     */
    @GetMapping("/results/{requestId}")
    public ResponseEntity<MatchingResultResponseDTO> getMatchingResult(
        @PathVariable String requestId
    ) {
        log.info("Fetching matching result for request: {}", requestId);

        MatchingResult result = matchingService.getMatchingResult(requestId);
        return ResponseEntity.ok(mapToResponseDTO(result));
    }

    /**
     * Re-score providers with a different algorithm
     */
    @PostMapping("/requests/{requestId}/rescore")
    public ResponseEntity<MatchingResultResponseDTO> rescoreProviders(
        @PathVariable String requestId,
        @RequestParam MatchingAlgorithm algorithm
    ) {
        log.info("Rescoring providers for request: {} with algorithm: {}", requestId, algorithm);

        MatchingResult result = matchingService.rescoreProviders(requestId, algorithm);

        // Publish updated matching event
        eventProducer.publishMatchingCompleted(result);

        return ResponseEntity.ok(mapToResponseDTO(result));
    }

    /**
     * Cancel a matching request
     */
    @PostMapping("/requests/{requestId}/cancel")
    public ResponseEntity<Void> cancelMatchingRequest(@PathVariable String requestId) {
        log.info("Cancelling matching request: {}", requestId);
        matchingService.cancelMatchingRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if provider is available
     */
    @GetMapping("/providers/{providerId}/availability")
    public ResponseEntity<Boolean> checkProviderAvailability(@PathVariable String providerId) {
        boolean available = matchingService.isProviderAvailable(providerId);
        return ResponseEntity.ok(available);
    }

    /**
     * Batch matching for multiple requests
     */
    @PostMapping("/requests/batch")
    public ResponseEntity<List<MatchingResultResponseDTO>> batchMatch(
        @Valid @RequestBody List<CreateMatchingRequestDTO> requestDTOs
    ) {
        log.info("Batch matching {} requests", requestDTOs.size());

        List<MatchingRequest> requests = requestDTOs.stream()
            .map(this::mapToMatchingRequest)
            .toList();

        List<MatchingResult> results = matchingService.batchMatch(requests);

        return ResponseEntity.ok(
            results.stream()
                .map(this::mapToResponseDTO)
                .toList()
        );
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Matching service is healthy");
    }

    /**
     * Map DTO to domain model
     */
    private MatchingRequest mapToMatchingRequest(CreateMatchingRequestDTO dto) {
        MatchingRequest.Location location = MatchingRequest.Location.builder()
            .coordinates(dto.getIncidentLocation().getCoordinates())
            .type("Point")
            .address(dto.getIncidentLocation().getAddress())
            .city(dto.getIncidentLocation().getCity())
            .state(dto.getIncidentLocation().getState())
            .postalCode(dto.getIncidentLocation().getPostalCode())
            .country(dto.getIncidentLocation().getCountry())
            .build();

        return MatchingRequest.builder()
            .requestId(UUID.randomUUID().toString())
            .incidentId(dto.getIncidentId())
            .algorithm(dto.getAlgorithm() != null ? dto.getAlgorithm() : MatchingAlgorithm.BEST_FIT)
            .incidentLocation(location)
            .serviceType(dto.getServiceType())
            .requiredCapabilities(dto.getRequiredCapabilities())
            .priority(dto.getPriority())
            .maxDistanceKm(dto.getMaxDistanceKm())
            .maxCost(dto.getMaxCost())
            .minProviderRating(dto.getMinProviderRating())
            .requireExactCapabilities(dto.getRequireExactCapabilities())
            .notes(dto.getNotes())
            .status(MatchingRequest.RequestStatus.PENDING)
            .requestedTime(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(30))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .version(1)
            .build();
    }

    /**
     * Map domain model to response DTO
     */
    private MatchingResultResponseDTO mapToResponseDTO(MatchingResult result) {
        MatchingResultResponseDTO.ProviderMatchDTO topProviderDTO = null;
        if (result.getTopProvider() != null) {
            topProviderDTO = mapProviderMatchToDTO(result.getTopProvider());
        }

        List<MatchingResultResponseDTO.ProviderMatchDTO> allProvidersDTO = result.getAllProviders().stream()
            .map(this::mapProviderMatchToDTO)
            .toList();

        return MatchingResultResponseDTO.builder()
            .id(result.getId())
            .requestId(result.getRequestId())
            .incidentId(result.getIncidentId())
            .algorithm(result.getAlgorithm())
            .topProvider(topProviderDTO)
            .allProviders(allProvidersDTO)
            .totalProviders(result.getTotalProviders())
            .processingTimeMs(result.getProcessingTimeMs())
            .matchedAt(result.getMatchedAt())
            .status(result.getStatus().toString())
            .statusMessage(result.getStatusMessage())
            .expiresAt(result.getExpiresAt())
            .build();
    }

    private MatchingResultResponseDTO.ProviderMatchDTO mapProviderMatchToDTO(
        MatchingResult.ProviderMatch providerMatch
    ) {
        return MatchingResultResponseDTO.ProviderMatchDTO.builder()
            .providerId(providerMatch.getProviderId())
            .providerName(providerMatch.getProviderName())
            .score(providerMatch.getScore())
            .distanceKm(providerMatch.getDistanceKm())
            .estimatedCost(providerMatch.getEstimatedCost())
            .rating(providerMatch.getRating())
            .estimatedArrival(providerMatch.getEstimatedArrival())
            .matchedCapabilities(providerMatch.getMatchedCapabilities())
            .missingCapabilities(providerMatch.getMissingCapabilities())
            .isAvailable(providerMatch.getIsAvailable())
            .rank(providerMatch.getRank())
            .distanceScore(providerMatch.getDistanceScore())
            .capabilityScore(providerMatch.getCapabilityScore())
            .availabilityScore(providerMatch.getAvailabilityScore())
            .ratingScore(providerMatch.getRatingScore())
            .costScore(providerMatch.getCostScore())
            .build();
    }
}
