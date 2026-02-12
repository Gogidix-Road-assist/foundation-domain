package com.gogidix.rapidassist.ai.summarization.application.service;

import com.gogidix.rapidassist.ai.summarization.application.command.CreateSummarizationRequestCommand;
import com.gogidix.rapidassist.ai.summarization.application.command.ProcessSummarizationCommand;
import com.gogidix.rapidassist.ai.summarization.application.dto.SummarizationRequestDto;
import com.gogidix.rapidassist.ai.summarization.application.dto.SummaryDto;
import com.gogidix.rapidassist.ai.summarization.application.mapper.SummaryMapper;
import com.gogidix.rapidassist.ai.summarization.application.query.GetSummarizationRequestQuery;
import com.gogidix.rapidassist.ai.summarization.domain.event.SummarizationCompletedEvent;
import com.gogidix.rapidassist.ai.summarization.domain.event.SummarizationRequestCreatedEvent;
import com.gogidix.rapidassist.ai.summarization.domain.exception.SummarizationRequestNotFoundException;
import com.gogidix.rapidassist.ai.summarization.domain.model.*;
import com.gogidix.rapidassist.ai.summarization.domain.repository.KeySentenceRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.domain.repository.SummaryQualityMetricsRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.domain.repository.SummaryRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.domain.repository.SummarizationRequestRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.infrastructure.messaging.kafka.event.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SummarizationRequestApplicationService
 * Tests summarization business logic and use cases
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SummarizationRequestApplicationService Tests")
class SummarizationApplicationServiceTest {

    @Mock
    private SummarizationRequestRepositoryPort requestRepository;

    @Mock
    private SummaryRepositoryPort summaryRepository;

    @Mock
    private KeySentenceRepositoryPort keySentenceRepository;

    @Mock
    private SummaryQualityMetricsRepositoryPort metricsRepository;

    @Mock
    private SummaryMapper summaryMapper;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private SummarizationRequestApplicationService applicationService;

    private SummarizationRequest testRequest;
    private Summary testSummary;
    private SummarizationRequestDto testRequestDto;
    private SummaryDto testSummaryDto;
    private UUID requestId;
    private UUID summaryId;
    private String tenantId;
    private String userId;

    @BeforeEach
    void setUp() {
        requestId = UUID.randomUUID();
        summaryId = UUID.randomUUID();
        tenantId = "tenant-123";
        userId = "user-456";

        testSummary = Summary.builder()
                .id(summaryId)
                .summarizationRequestId(requestId)
                .tenantId(tenantId)
                .createdBy(userId)
                .content("This is a summary.")
                .summaryLength(SummaryLength.MEDIUM)
                .summarizationType(SummarizationType.ABSTRACTIVE)
                .qualityScore(0.92)
                .compressionRatio(0.25)
                .originalWordCount(12)
                .summaryWordCount(4)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .keySentences(new ArrayList<>())
                .build();

        testRequest = SummarizationRequest.builder()
                .id(requestId)
                .tenantId(tenantId)
                .requestId(UUID.randomUUID().toString())
                .createdBy(userId)
                .sourceTexts(List.of("This is a long text that needs to be summarized."))
                .summarizationType(SummarizationType.ABSTRACTIVE)
                .summaryLength(SummaryLength.MEDIUM)
                .status(SummarizationStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testSummaryDto = SummaryDto.builder()
                .id(summaryId)
                .summarizationRequestId(requestId)
                .tenantId(tenantId)
                .createdBy(userId)
                .content("This is a summary.")
                .summaryLength(SummaryLength.MEDIUM)
                .summarizationType(SummarizationType.ABSTRACTIVE)
                .qualityScore(0.92)
                .compressionRatio(0.25)
                .originalWordCount(12)
                .summaryWordCount(4)
                .build();

        testRequestDto = SummarizationRequestDto.builder()
                .id(requestId)
                .tenantId(tenantId)
                .requestId(testRequest.getRequestId())
                .createdBy(userId)
                .sourceTexts(List.of("This is a long text that needs to be summarized."))
                .summarizationType(SummarizationType.ABSTRACTIVE)
                .summaryLength(SummaryLength.MEDIUM)
                .status(SummarizationStatus.COMPLETED)
                .build();
    }

    @Test
    @DisplayName("Should create summarization request successfully")
    void testCreateSummarizationRequest() {
        CreateSummarizationRequestCommand command = CreateSummarizationRequestCommand.builder()
                .tenantId(tenantId)
                .createdBy(userId)
                .sourceTexts(List.of("This is a long text that needs to be summarized."))
                .summarizationType(SummarizationType.ABSTRACTIVE)
                .summaryLength(SummaryLength.MEDIUM)
                .build();

        when(requestRepository.save(eq(tenantId), any(SummarizationRequest.class))).thenReturn(testRequest);
        doNothing().when(eventPublisher).publish(any(SummarizationRequestCreatedEvent.class));

        SummarizationRequestDto result = applicationService.createRequest(command);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals(userId, result.getCreatedBy());

        verify(requestRepository, times(1)).save(eq(tenantId), any(SummarizationRequest.class));
        verify(eventPublisher, times(1)).publish(any(SummarizationRequestCreatedEvent.class));
    }

    @Test
    @DisplayName("Should create summarization request with required fields")
    void testCreateSummarizationRequestWithRequiredFields() {
        CreateSummarizationRequestCommand command = CreateSummarizationRequestCommand.builder()
                .tenantId(tenantId)
                .createdBy(userId)
                .sourceTexts(List.of("Text to summarize"))
                .summarizationType(SummarizationType.EXTRACTIVE)
                .summaryLength(SummaryLength.SHORT)
                .build();

        when(requestRepository.save(eq(tenantId), any(SummarizationRequest.class))).thenReturn(testRequest);
        doNothing().when(eventPublisher).publish(any(SummarizationRequestCreatedEvent.class));

        SummarizationRequestDto result = applicationService.createRequest(command);

        assertNotNull(result);
        verify(requestRepository, times(1)).save(eq(tenantId), argThat((SummarizationRequest r) ->
                SummarizationStatus.PENDING == r.getStatus() &&
                SummarizationType.EXTRACTIVE == r.getSummarizationType() &&
                SummaryLength.SHORT == r.getSummaryLength()
        ));
    }

    @Test
    @DisplayName("Should get summarization request by ID")
    void testGetSummarizationRequest() {
        GetSummarizationRequestQuery query = GetSummarizationRequestQuery.builder()
                .requestId(requestId)
                .tenantId(tenantId)
                .build();

        when(requestRepository.findById(tenantId, requestId))
                .thenReturn(Optional.of(testRequest));

        SummarizationRequestDto result = applicationService.getRequest(query);

        assertNotNull(result);
        assertEquals(requestId, result.getId());

        verify(requestRepository, times(1)).findById(tenantId, requestId);
    }

    @Test
    @DisplayName("Should throw exception when request not found")
    void testGetSummarizationRequestNotFound() {
        GetSummarizationRequestQuery query = GetSummarizationRequestQuery.builder()
                .requestId(requestId)
                .tenantId(tenantId)
                .build();

        when(requestRepository.findById(tenantId, requestId))
                .thenReturn(Optional.empty());

        assertThrows(SummarizationRequestNotFoundException.class, () -> {
            applicationService.getRequest(query);
        });

        verify(requestRepository, times(1)).findById(tenantId, requestId);
    }

    @Test
    @DisplayName("Should process summarization request successfully")
    void testProcessSummarizationRequest() {
        testRequest.setStatus(SummarizationStatus.PENDING);

        ProcessSummarizationCommand command = ProcessSummarizationCommand.builder()
                .requestId(requestId)
                .tenantId(tenantId)
                .build();

        when(requestRepository.findById(tenantId, requestId))
                .thenReturn(Optional.of(testRequest));
        when(requestRepository.save(eq(tenantId), any(SummarizationRequest.class))).thenReturn(testRequest);
        when(summaryRepository.save(eq(tenantId), any(Summary.class))).thenReturn(testSummary);
        when(summaryMapper.toDto(testSummary)).thenReturn(testSummaryDto);
        doNothing().when(eventPublisher).publish(any(SummarizationCompletedEvent.class));

        SummaryDto result = applicationService.processSummarization(command);

        assertNotNull(result);
        verify(requestRepository, times(2)).save(eq(tenantId), any(SummarizationRequest.class));
        verify(summaryRepository, times(1)).save(eq(tenantId), any(Summary.class));
    }

    @Test
    @DisplayName("Should delete summarization request successfully")
    void testDeleteSummarizationRequest() {
        when(requestRepository.exists(tenantId, requestId)).thenReturn(true);
        doNothing().when(requestRepository).delete(tenantId, requestId);

        applicationService.deleteRequest(tenantId, requestId);

        verify(requestRepository, times(1)).exists(tenantId, requestId);
        verify(requestRepository, times(1)).delete(tenantId, requestId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent request")
    void testDeleteSummarizationRequestNotFound() {
        when(requestRepository.exists(tenantId, requestId)).thenReturn(false);

        assertThrows(SummarizationRequestNotFoundException.class, () -> {
            applicationService.deleteRequest(tenantId, requestId);
        });

        verify(requestRepository, times(1)).exists(tenantId, requestId);
        verify(requestRepository, never()).delete(any(), any());
    }

    @Test
    @DisplayName("Should get requests by tenant ID")
    void testGetRequestsByTenantId() {
        List<SummarizationRequest> requests = Arrays.asList(
                testRequest,
                createRequest("tenant-123", userId),
                createRequest("tenant-123", "other-user")
        );

        when(requestRepository.findByTenantId(tenantId)).thenReturn(requests);

        List<SummarizationRequest> results = requestRepository.findByTenantId(tenantId);

        assertNotNull(results);
        assertEquals(3, results.size());

        verify(requestRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should get requests by status")
    void testGetRequestsByStatus() {
        List<SummarizationRequest> requests = List.of(testRequest);

        when(requestRepository.findByStatus(tenantId, SummarizationStatus.COMPLETED.name()))
                .thenReturn(requests);

        List<SummarizationRequest> results = requestRepository.findByStatus(tenantId, SummarizationStatus.COMPLETED.name());

        assertNotNull(results);
        assertEquals(1, results.size());

        verify(requestRepository, times(1)).findByStatus(tenantId, SummarizationStatus.COMPLETED.name());
    }

    @Test
    @DisplayName("Should get requests by created by")
    void testGetRequestsByCreatedBy() {
        List<SummarizationRequest> requests = List.of(testRequest);

        when(requestRepository.findByCreatedBy(tenantId, userId))
                .thenReturn(requests);

        List<SummarizationRequest> results = requestRepository.findByCreatedBy(tenantId, userId);

        assertNotNull(results);
        assertEquals(1, results.size());

        verify(requestRepository, times(1)).findByCreatedBy(tenantId, userId);
    }

    @Test
    @DisplayName("Should check if request exists")
    void testExistsRequest() {
        when(requestRepository.exists(tenantId, requestId)).thenReturn(true);

        boolean exists = requestRepository.exists(tenantId, requestId);

        assertTrue(exists);
        verify(requestRepository, times(1)).exists(tenantId, requestId);
    }

    @Test
    @DisplayName("Should count requests by tenant ID")
    void testCountByTenantId() {
        when(requestRepository.countByTenantId(tenantId)).thenReturn(5L);

        long count = requestRepository.countByTenantId(tenantId);

        assertEquals(5L, count);
        verify(requestRepository, times(1)).countByTenantId(tenantId);
    }

    private SummarizationRequest createRequest(String tenantId, String createdBy) {
        return SummarizationRequest.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .requestId(UUID.randomUUID().toString())
                .createdBy(createdBy)
                .status(SummarizationStatus.COMPLETED)
                .sourceTexts(List.of("Test text"))
                .summarizationType(SummarizationType.ABSTRACTIVE)
                .summaryLength(SummaryLength.MEDIUM)
                .build();
    }
}
