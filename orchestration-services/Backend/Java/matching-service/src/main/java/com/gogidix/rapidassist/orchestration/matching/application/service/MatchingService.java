package com.gogidix.rapidassist.orchestration.matching.application.service;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingAlgorithm;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingCriteria;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult;
import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import com.gogidix.rapidassist.orchestration.matching.domain.port.in.MatchingServicePort;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.MatchingCriteriaRepositoryPort;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.MatchingRequestRepositoryPort;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.MatchingResultRepositoryPort;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.ProviderProfileRepositoryPort;
import com.gogidix.rapidassist.orchestration.matching.domain.policy.MatchingAlgorithmStrategy;
import com.gogidix.rapidassist.orchestration.matching.shared.exception.InvalidMatchingRequestException;
import com.gogidix.rapidassist.orchestration.matching.shared.exception.NoProvidersFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Application service for matching operations
 * Implements the business logic for provider matching
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService implements MatchingServicePort {

    private final MatchingRequestRepositoryPort requestRepository;
    private final MatchingResultRepositoryPort resultRepository;
    private final ProviderProfileRepositoryPort providerRepository;
    private final MatchingCriteriaRepositoryPort criteriaRepository;

    private final List<MatchingAlgorithmStrategy> algorithmStrategies;

    @Override
    @Transactional
    public MatchingResult findProviders(MatchingRequest request) {
        log.info("Finding providers for request: {}", request.getRequestId());

        // TODO: Extract tenant ID from request context when shared library is available
        if (request.getTenantId() == null) {
            request.setTenantId("default-tenant");
        }

        // Validate request
        validateRequest(request);

        // Set initial status
        request.setStatus(MatchingRequest.RequestStatus.PROCESSING);
        request.setStatusChangedAt(LocalDateTime.now());
        requestRepository.save(request);

        try {
            // Get matching criteria
            MatchingCriteria criteria = getMatchingCriteria(request);

            // Find eligible providers
            List<ProviderProfile> providers = findEligibleProviders(request, criteria);

            if (providers.isEmpty()) {
                return handleNoProvidersFound(request);
            }

            // Execute matching algorithm
            MatchingAlgorithmStrategy strategy = getAlgorithmStrategy(request.getAlgorithm());
            MatchingResult result = strategy.match(request, providers, criteria);

            // Save result
            resultRepository.save(result);

            // Update request status
            request.setStatus(MatchingRequest.RequestStatus.COMPLETED);
            request.setStatusChangedAt(LocalDateTime.now());
            if (result.getTopProvider() != null) {
                request.setAssignedProviderId(result.getTopProvider().getProviderId());
            }
            requestRepository.save(request);

            log.info("Matching completed for request: {}, found {} providers",
                request.getRequestId(), result.getTotalProviders());

            return result;

        } catch (Exception e) {
            log.error("Error finding providers for request: {}", request.getRequestId(), e);
            request.setStatus(MatchingRequest.RequestStatus.FAILED);
            request.setStatusChangedAt(LocalDateTime.now());
            requestRepository.save(request);
            throw e;
        }
    }

    @Override
    @Transactional
    public MatchingResult findProvidersWithAlgorithm(String requestId, MatchingAlgorithm algorithm) {
        MatchingRequest request = requestRepository.findByRequestId(requestId)
            .orElseThrow(() -> new InvalidMatchingRequestException(
                "Matching request not found: " + requestId));

        request.setAlgorithm(algorithm);
        return findProviders(request);
    }

    @Override
    @Transactional
    public MatchingResult rescoreProviders(String requestId, MatchingAlgorithm newAlgorithm) {
        log.info("Rescoring providers for request: {} with algorithm: {}", requestId, newAlgorithm);

        MatchingRequest request = requestRepository.findByRequestId(requestId)
            .orElseThrow(() -> new InvalidMatchingRequestException(
                "Matching request not found: " + requestId));

        request.setAlgorithm(newAlgorithm);
        return findProviders(request);
    }

    @Override
    public MatchingResult getMatchingResult(String requestId) {
        return resultRepository.findByRequestId(requestId)
            .orElseThrow(() -> new NoProvidersFoundException(
                "Matching result not found for request: " + requestId));
    }

    @Override
    @Transactional
    public void cancelMatchingRequest(String requestId) {
        log.info("Cancelling matching request: {}", requestId);

        MatchingRequest request = requestRepository.findByRequestId(requestId)
            .orElseThrow(() -> new InvalidMatchingRequestException(
                "Matching request not found: " + requestId));

        if (request.getStatus() == MatchingRequest.RequestStatus.COMPLETED ||
            request.getStatus() == MatchingRequest.RequestStatus.CANCELLED) {
            throw new InvalidMatchingRequestException(
                "Cannot cancel request in status: " + request.getStatus());
        }

        request.setStatus(MatchingRequest.RequestStatus.CANCELLED);
        request.setStatusChangedAt(LocalDateTime.now());
        requestRepository.save(request);
    }

    @Override
    public boolean isProviderAvailable(String providerId) {
        return providerRepository.findByProviderId(providerId)
            .map(profile -> profile.getStatus() == ProviderProfile.ProviderStatus.AVAILABLE &&
                           profile.getIsActive())
            .orElse(false);
    }

    @Override
    @Transactional
    public List<MatchingResult> batchMatch(List<MatchingRequest> requests) {
        log.info("Batch matching {} requests", requests.size());

        return requests.stream()
            .map(this::findProviders)
            .toList();
    }

    @Override
    public boolean validateMatchingCriteria(MatchingRequest request) {
        try {
            validateRequest(request);
            return true;
        } catch (InvalidMatchingRequestException e) {
            return false;
        }
    }

    /**
     * Validate matching request
     */
    private void validateRequest(MatchingRequest request) {
        if (request.getRequestId() == null || request.getRequestId().isBlank()) {
            throw new InvalidMatchingRequestException("Request ID is required");
        }

        if (request.getIncidentLocation() == null) {
            throw new InvalidMatchingRequestException("Incident location is required");
        }

        if (request.getIncidentLocation().getCoordinates() == null ||
            request.getIncidentLocation().getCoordinates().length != 2) {
            throw new InvalidMatchingRequestException("Invalid incident coordinates");
        }

        if (request.getAlgorithm() == null) {
            request.setAlgorithm(MatchingAlgorithm.BEST_FIT);
        }
    }

    /**
     * Get matching criteria for request
     */
    private MatchingCriteria getMatchingCriteria(MatchingRequest request) {
        Optional<MatchingCriteria> criteriaOpt = criteriaRepository
            .findByServiceType(request.getServiceType())
            .stream()
            .filter(c -> c.getTenantId().equals(request.getTenantId()) && c.getIsActive())
            .filter(c -> c.getValidFrom() == null || c.getValidFrom().isBefore(LocalDateTime.now()))
            .filter(c -> c.getValidTo() == null || c.getValidTo().isAfter(LocalDateTime.now()))
            .findFirst();

        return criteriaOpt.orElse(null);
    }

    /**
     * Find eligible providers
     */
    private List<ProviderProfile> findEligibleProviders(
        MatchingRequest request,
        MatchingCriteria criteria
    ) {
        // Find providers near the incident location
        List<ProviderProfile> nearbyProviders = providerRepository.findProvidersNearLocation(
            request.getIncidentLocation().getCoordinates()[0],
            request.getIncidentLocation().getCoordinates()[1],
            request.getMaxDistanceKm() != null ? request.getMaxDistanceKm() : 200.0
        );

        // Filter by tenant and status
        return nearbyProviders.stream()
            .filter(p -> p.getTenantId().equals(request.getTenantId()))
            .filter(p -> p.getIsActive())
            .toList();
    }

    /**
     * Get algorithm strategy
     */
    private MatchingAlgorithmStrategy getAlgorithmStrategy(MatchingAlgorithm algorithm) {
        return algorithmStrategies.stream()
            .filter(strategy -> strategy.getAlgorithmType() == algorithm)
            .findFirst()
            .orElseThrow(() -> new InvalidMatchingRequestException(
                "Algorithm not implemented: " + algorithm));
    }

    /**
     * Handle no providers found scenario
     */
    private MatchingResult handleNoProvidersFound(MatchingRequest request) {
        request.setStatus(MatchingRequest.RequestStatus.COMPLETED);
        request.setStatusChangedAt(LocalDateTime.now());
        requestRepository.save(request);

        MatchingResult result = MatchingResult.builder()
            .tenantId(request.getTenantId())
            .requestId(request.getRequestId())
            .incidentId(request.getIncidentId())
            .algorithm(request.getAlgorithm())
            .topProvider(null)
            .allProviders(List.of())
            .totalProviders(0)
            .processingTimeMs(0.0)
            .matchedAt(LocalDateTime.now())
            .status(MatchingResult.ResultStatus.NO_PROVIDERS_FOUND)
            .statusMessage("No eligible providers found matching the criteria")
            .expiresAt(request.getExpiresAt())
            .isExpired(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        return resultRepository.save(result);
    }
}
