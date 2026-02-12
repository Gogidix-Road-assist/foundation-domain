package com.gogidix.rapidassist.orchestration.matching.application.service;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingAlgorithm;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingResult;
import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.MatchingCriteriaRepositoryPort;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.MatchingRequestRepositoryPort;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.MatchingResultRepositoryPort;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.ProviderProfileRepositoryPort;
import com.gogidix.rapidassist.orchestration.matching.domain.policy.MatchingAlgorithmStrategy;
import com.gogidix.rapidassist.orchestration.matching.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.matching.shared.requestcontext.RequestContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private MatchingRequestRepositoryPort requestRepository;

    @Mock
    private MatchingResultRepositoryPort resultRepository;

    @Mock
    private ProviderProfileRepositoryPort providerRepository;

    @Mock
    private MatchingCriteriaRepositoryPort criteriaRepository;

    @Mock
    private List<MatchingAlgorithmStrategy> algorithmStrategies;

    @Mock
    private MatchingAlgorithmStrategy algorithmStrategy;

    @InjectMocks
    private MatchingService matchingService;

    private RequestContext requestContext;
    private MatchingRequest matchingRequest;
    private ProviderProfile providerProfile;

    @BeforeEach
    void setUp() {
        requestContext = RequestContext.builder()
            .tenantId("tenant-123")
            .build();

        matchingRequest = MatchingRequest.builder()
            .requestId("req-123")
            .incidentId("incident-123")
            .algorithm(MatchingAlgorithm.BEST_FIT)
            .status(MatchingRequest.RequestStatus.PENDING)
            .requestedTime(LocalDateTime.now())
            .build();

        providerProfile = ProviderProfile.builder()
            .id("provider-1")
            .providerId("prov-123")
            .providerName("Test Provider")
            .status(ProviderProfile.ProviderStatus.AVAILABLE)
            .isActive(true)
            .rating(4.5)
            .build();
    }

    @Test
    void findProviders_WithValidRequest_ReturnsMatchingResult() {
        // Arrange
        when(requestRepository.save(any(MatchingRequest.class))).thenReturn(matchingRequest);
        when(providerRepository.findProvidersNearLocation(any(), any(), any()))
            .thenReturn(Collections.singletonList(providerProfile));

        MatchingResult expectedResult = MatchingResult.builder()
            .requestId("req-123")
            .totalProviders(1)
            .status(MatchingResult.ResultStatus.SUCCESS)
            .build();

        when(algorithmStrategy.match(any(), any(), any())).thenReturn(expectedResult);
        when(algorithmStrategies.stream()).thenReturn(List.of(algorithmStrategy).stream());
        when(resultRepository.save(any(MatchingResult.class))).thenReturn(expectedResult);

        try (MockedStatic<RequestContextHolder> contextHolder = mockStatic(RequestContextHolder.class)) {
            contextHolder.when(RequestContextHolder::getContext).thenReturn(requestContext);

            // Act
            MatchingResult result = matchingService.findProviders(matchingRequest);

            // Assert
            assertNotNull(result);
            assertEquals("req-123", result.getRequestId());
            assertEquals(MatchingResult.ResultStatus.SUCCESS, result.getStatus());
        }
    }

    @Test
    void findProviders_WithNoProviders_ReturnsNoProvidersFound() {
        // Arrange
        when(requestRepository.save(any(MatchingRequest.class))).thenReturn(matchingRequest);
        when(providerRepository.findProvidersNearLocation(any(), any(), any()))
            .thenReturn(Collections.emptyList());

        try (MockedStatic<RequestContextHolder> contextHolder = mockStatic(RequestContextHolder.class)) {
            contextHolder.when(RequestContextHolder::getContext).thenReturn(requestContext);

            // Act
            MatchingResult result = matchingService.findProviders(matchingRequest);

            // Assert
            assertNotNull(result);
            assertEquals(MatchingResult.ResultStatus.NO_PROVIDERS_FOUND, result.getStatus());
            assertEquals(0, result.getTotalProviders());
        }
    }

    @Test
    void cancelMatchingRequest_WithValidRequest_CancelsRequest() {
        // Arrange
        matchingRequest.setStatus(MatchingRequest.RequestStatus.PROCESSING);
        when(requestRepository.findByRequestId("req-123"))
            .thenReturn(Optional.of(matchingRequest));
        when(requestRepository.save(any(MatchingRequest.class))).thenReturn(matchingRequest);

        // Act
        matchingService.cancelMatchingRequest("req-123");

        // Assert
        assertEquals(MatchingRequest.RequestStatus.CANCELLED, matchingRequest.getStatus());
        verify(requestRepository).save(matchingRequest);
    }

    @Test
    void isProviderAvailable_WithAvailableProvider_ReturnsTrue() {
        // Arrange
        when(providerRepository.findByProviderId("prov-123"))
            .thenReturn(Optional.of(providerProfile));

        // Act
        boolean available = matchingService.isProviderAvailable("prov-123");

        // Assert
        assertTrue(available);
    }

    @Test
    void isProviderAvailable_WithUnavailableProvider_ReturnsFalse() {
        // Arrange
        providerProfile.setStatus(ProviderProfile.ProviderStatus.BUSY);
        when(providerRepository.findByProviderId("prov-123"))
            .thenReturn(Optional.of(providerProfile));

        // Act
        boolean available = matchingService.isProviderAvailable("prov-123");

        // Assert
        assertFalse(available);
    }

    @Test
    void validateMatchingCriteria_WithValidRequest_ReturnsTrue() {
        // Arrange
        matchingRequest.setIncidentLocation(MatchingRequest.Location.builder()
            .coordinates(new Double[]{0.0, 0.0})
            .build());

        // Act
        boolean isValid = matchingService.validateMatchingCriteria(matchingRequest);

        // Assert
        assertTrue(isValid);
    }
}
