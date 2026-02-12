package com.gogidix.rapidassist.ai.sentiment.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.sentiment.domain.aggregate.SentimentAnalysis;
import com.gogidix.rapidassist.ai.sentiment.domain.model.*;
import com.gogidix.rapidassist.ai.sentiment.infrastructure.persistence.entity.SentimentAnalysisEntity;
import com.gogidix.rapidassist.ai.sentiment.infrastructure.persistence.mapper.SentimentAnalysisPersistenceMapper;
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
import static org.mockito.Mockito.*;

/**
 * Unit tests for SentimentAnalysisRepositoryImpl
 * Tests persistence layer operations, MongoDB interactions, and data mapping
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SentimentAnalysisRepositoryImpl Tests")
class SentimentAnalysisRepositoryImplTest {

    @Mock
    private SpringDataSentimentAnalysisRepository springDataRepository;

    @Mock
    private SentimentAnalysisPersistenceMapper mapper;

    @InjectMocks
    private SentimentAnalysisRepositoryImpl repository;

    private SentimentAnalysis testAnalysis;
    private SentimentAnalysisEntity testEntity;
    private UUID testId;
    private String tenantId;
    private String userId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        tenantId = "tenant-123";
        userId = "user-456";

        testAnalysis = SentimentAnalysis.builder()
                .id(testId)
                .tenantId(tenantId)
                .userId(userId)
                .text("This is a great product!")
                .sourceType("feedback")
                .sourceId("fb-001")
                .status(AnalysisStatus.COMPLETED)
                .overallSentiment(SentimentType.POSITIVE)
                .sentimentCategory(SentimentCategory.POSITIVE)
                .sentimentScore(0.8)
                .confidence(0.85)
                .language("en")
                .wordCount(5)
                .characterCount(23)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .analyzedAt(LocalDateTime.now())
                .emotions(new ArrayList<>())
                .aspects(new ArrayList<>())
                .build();

        testEntity = new SentimentAnalysisEntity();
        testEntity.setId(testId);
        testEntity.setTenantId(tenantId);
        testEntity.setUserId(userId);
        testEntity.setText("This is a great product!");
        testEntity.setSourceType("feedback");
        testEntity.setSourceId("fb-001");
        testEntity.setStatus(AnalysisStatus.COMPLETED);
        testEntity.setOverallSentiment(SentimentType.POSITIVE);
        testEntity.setSentimentCategory(SentimentCategory.POSITIVE);
        testEntity.setSentimentScore(0.8);
        testEntity.setConfidence(0.85);
        testEntity.setLanguage("en");
        testEntity.setCreatedAt(LocalDateTime.now());
        testEntity.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save sentiment analysis successfully")
    void testSave() {
        when(mapper.toEntity(testAnalysis)).thenReturn(testEntity);
        when(springDataRepository.save(testEntity)).thenReturn(testEntity);
        when(mapper.toAggregate(testEntity)).thenReturn(testAnalysis);

        SentimentAnalysis result = repository.save(testAnalysis);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals(userId, result.getUserId());

        verify(mapper, times(1)).toEntity(testAnalysis);
        verify(springDataRepository, times(1)).save(testEntity);
        verify(mapper, times(1)).toAggregate(testEntity);
    }

    @Test
    @DisplayName("Should find sentiment analysis by ID")
    void testFindById() {
        when(springDataRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(mapper.toAggregate(testEntity)).thenReturn(testAnalysis);

        Optional<SentimentAnalysis> result = repository.findById(testId);

        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());

        verify(springDataRepository, times(1)).findById(testId);
        verify(mapper, times(1)).toAggregate(testEntity);
    }

    @Test
    @DisplayName("Should return empty when not found by ID")
    void testFindByIdNotFound() {
        when(springDataRepository.findById(testId)).thenReturn(Optional.empty());

        Optional<SentimentAnalysis> result = repository.findById(testId);

        assertFalse(result.isPresent());
        verify(springDataRepository, times(1)).findById(testId);
        verify(mapper, never()).toAggregate(any());
    }

    @Test
    @DisplayName("Should find sentiment analysis by tenant ID and ID")
    void testFindByTenantIdAndId() {
        when(springDataRepository.findByTenantIdAndId(tenantId, testId))
                .thenReturn(Optional.of(testEntity));
        when(mapper.toAggregate(testEntity)).thenReturn(testAnalysis);

        Optional<SentimentAnalysis> result = repository.findByTenantIdAndId(tenantId, testId);

        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());
        assertEquals(tenantId, result.get().getTenantId());

        verify(springDataRepository, times(1)).findByTenantIdAndId(tenantId, testId);
        verify(mapper, times(1)).toAggregate(testEntity);
    }

    @Test
    @DisplayName("Should return empty when not found by tenant ID and ID")
    void testFindByTenantIdAndIdNotFound() {
        when(springDataRepository.findByTenantIdAndId(tenantId, testId))
                .thenReturn(Optional.empty());

        Optional<SentimentAnalysis> result = repository.findByTenantIdAndId(tenantId, testId);

        assertFalse(result.isPresent());
        verify(springDataRepository, times(1)).findByTenantIdAndId(tenantId, testId);
        verify(mapper, never()).toAggregate(any());
    }

    @Test
    @DisplayName("Should find all sentiment analyses by tenant ID")
    void testFindAllByTenantId() {
        List<SentimentAnalysisEntity> entities = Arrays.asList(testEntity, testEntity);

        when(springDataRepository.findAllByTenantId(tenantId)).thenReturn(entities);
        when(mapper.toAggregate(testEntity)).thenReturn(testAnalysis);

        List<SentimentAnalysis> results = repository.findAllByTenantId(tenantId);

        assertNotNull(results);
        assertEquals(2, results.size());

        verify(springDataRepository, times(1)).findAllByTenantId(tenantId);
        verify(mapper, times(2)).toAggregate(testEntity);
    }

    @Test
    @DisplayName("Should return empty list when no analyses found for tenant")
    void testFindAllByTenantIdEmpty() {
        when(springDataRepository.findAllByTenantId(tenantId)).thenReturn(List.of());

        List<SentimentAnalysis> results = repository.findAllByTenantId(tenantId);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(springDataRepository, times(1)).findAllByTenantId(tenantId);
        verify(mapper, never()).toAggregate(any());
    }

    @Test
    @DisplayName("Should find sentiment analyses by tenant ID and user ID")
    void testFindByTenantIdAndUserId() {
        List<SentimentAnalysisEntity> entities = List.of(testEntity);

        when(springDataRepository.findByTenantIdAndUserId(tenantId, userId))
                .thenReturn(entities);
        when(mapper.toAggregate(testEntity)).thenReturn(testAnalysis);

        List<SentimentAnalysis> results = repository.findByTenantIdAndUserId(tenantId, userId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(userId, results.get(0).getUserId());

        verify(springDataRepository, times(1)).findByTenantIdAndUserId(tenantId, userId);
        verify(mapper, times(1)).toAggregate(testEntity);
    }

    @Test
    @DisplayName("Should find sentiment analyses by tenant ID and source")
    void testFindByTenantIdAndSource() {
        String sourceType = "feedback";
        String sourceId = "fb-001";

        List<SentimentAnalysisEntity> entities = List.of(testEntity);

        when(springDataRepository.findByTenantIdAndSourceTypeAndSourceId(tenantId, sourceType, sourceId))
                .thenReturn(entities);
        when(mapper.toAggregate(testEntity)).thenReturn(testAnalysis);

        List<SentimentAnalysis> results = repository.findByTenantIdAndSource(tenantId, sourceType, sourceId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(sourceType, results.get(0).getSourceType());
        assertEquals(sourceId, results.get(0).getSourceId());

        verify(springDataRepository, times(1)).findByTenantIdAndSourceTypeAndSourceId(tenantId, sourceType, sourceId);
        verify(mapper, times(1)).toAggregate(testEntity);
    }

    @Test
    @DisplayName("Should find sentiment analyses by tenant ID and status")
    void testFindByTenantIdAndStatus() {
        String status = "COMPLETED";
        List<SentimentAnalysisEntity> entities = List.of(testEntity);

        when(springDataRepository.findByTenantIdAndStatus(tenantId, AnalysisStatus.COMPLETED))
                .thenReturn(entities);
        when(mapper.toAggregate(testEntity)).thenReturn(testAnalysis);

        List<SentimentAnalysis> results = repository.findByTenantIdAndStatus(tenantId, status);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(AnalysisStatus.COMPLETED, results.get(0).getStatus());

        verify(springDataRepository, times(1)).findByTenantIdAndStatus(tenantId, AnalysisStatus.COMPLETED);
        verify(mapper, times(1)).toAggregate(testEntity);
    }

    @Test
    @DisplayName("Should return empty list for invalid status")
    void testFindByTenantIdAndInvalidStatus() {
        String invalidStatus = "INVALID_STATUS";

        when(springDataRepository.findByTenantIdAndStatus(tenantId, AnalysisStatus.COMPLETED))
                .thenReturn(List.of());

        List<SentimentAnalysis> results = repository.findByTenantIdAndStatus(tenantId, invalidStatus);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(springDataRepository, never()).findByTenantIdAndStatus(any(), any());
        verify(mapper, never()).toAggregate(any());
    }

    @Test
    @DisplayName("Should find sentiment analyses by tenant ID and sentiment type")
    void testFindByTenantIdAndSentimentType() {
        String sentimentType = "POSITIVE";
        List<SentimentAnalysisEntity> entities = List.of(testEntity);

        when(springDataRepository.findByTenantIdAndOverallSentiment(tenantId, SentimentType.POSITIVE))
                .thenReturn(entities);
        when(mapper.toAggregate(testEntity)).thenReturn(testAnalysis);

        List<SentimentAnalysis> results = repository.findByTenantIdAndSentimentType(tenantId, sentimentType);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(SentimentType.POSITIVE, results.get(0).getOverallSentiment());

        verify(springDataRepository, times(1)).findByTenantIdAndOverallSentiment(tenantId, SentimentType.POSITIVE);
        verify(mapper, times(1)).toAggregate(testEntity);
    }

    @Test
    @DisplayName("Should return empty list for invalid sentiment type")
    void testFindByTenantIdAndInvalidSentimentType() {
        String invalidSentimentType = "INVALID_TYPE";

        List<SentimentAnalysis> results = repository.findByTenantIdAndSentimentType(tenantId, invalidSentimentType);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(springDataRepository, never()).findByTenantIdAndOverallSentiment(any(), any());
        verify(mapper, never()).toAggregate(any());
    }

    @Test
    @DisplayName("Should find sentiment analyses by tenant ID and date range")
    void testFindByTenantIdAndDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        List<SentimentAnalysisEntity> entities = List.of(testEntity);

        when(springDataRepository.findByTenantIdAndCreatedAtBetween(tenantId, startDate, endDate))
                .thenReturn(entities);
        when(mapper.toAggregate(testEntity)).thenReturn(testAnalysis);

        List<SentimentAnalysis> results = repository.findByTenantIdAndDateRange(tenantId, startDate, endDate);

        assertNotNull(results);
        assertEquals(1, results.size());

        verify(springDataRepository, times(1)).findByTenantIdAndCreatedAtBetween(tenantId, startDate, endDate);
        verify(mapper, times(1)).toAggregate(testEntity);
    }

    @Test
    @DisplayName("Should delete sentiment analysis by ID")
    void testDeleteById() {
        doNothing().when(springDataRepository).deleteById(testId);

        repository.deleteById(testId);

        verify(springDataRepository, times(1)).deleteById(testId);
    }

    @Test
    @DisplayName("Should delete sentiment analysis by tenant ID and ID")
    void testDeleteByTenantIdAndId() {
        doNothing().when(springDataRepository).deleteByTenantIdAndId(tenantId, testId);

        repository.deleteByTenantIdAndId(tenantId, testId);

        verify(springDataRepository, times(1)).deleteByTenantIdAndId(tenantId, testId);
    }

    @Test
    @DisplayName("Should check if sentiment analysis exists by ID")
    void testExistsById() {
        when(springDataRepository.existsById(testId)).thenReturn(true);

        boolean exists = repository.existsById(testId);

        assertTrue(exists);
        verify(springDataRepository, times(1)).existsById(testId);
    }

    @Test
    @DisplayName("Should return false when sentiment analysis does not exist")
    void testNotExistsById() {
        when(springDataRepository.existsById(testId)).thenReturn(false);

        boolean exists = repository.existsById(testId);

        assertFalse(exists);
        verify(springDataRepository, times(1)).existsById(testId);
    }

    @Test
    @DisplayName("Should count sentiment analyses by tenant ID")
    void testCountByTenantId() {
        when(springDataRepository.countByTenantId(tenantId)).thenReturn(25L);

        long count = repository.countByTenantId(tenantId);

        assertEquals(25L, count);
        verify(springDataRepository, times(1)).countByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should return 0 count for tenant with no analyses")
    void testCountByTenantIdZero() {
        when(springDataRepository.countByTenantId(tenantId)).thenReturn(0L);

        long count = repository.countByTenantId(tenantId);

        assertEquals(0L, count);
        verify(springDataRepository, times(1)).countByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should handle multiple saves correctly")
    void testMultipleSaves() {
        when(mapper.toEntity(any(SentimentAnalysis.class))).thenReturn(testEntity);
        when(springDataRepository.save(testEntity)).thenReturn(testEntity);
        when(mapper.toAggregate(testEntity)).thenReturn(testAnalysis);

        repository.save(testAnalysis);
        repository.save(testAnalysis);
        repository.save(testAnalysis);

        verify(mapper, times(3)).toEntity(any(SentimentAnalysis.class));
        verify(springDataRepository, times(3)).save(testEntity);
        verify(mapper, times(3)).toAggregate(testEntity);
    }

    @Test
    @DisplayName("Should handle empty query results")
    void testEmptyQueryResults() {
        when(springDataRepository.findByTenantIdAndUserId(tenantId, userId))
                .thenReturn(List.of());

        List<SentimentAnalysis> results = repository.findByTenantIdAndUserId(tenantId, userId);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(mapper, never()).toAggregate(any());
    }

    @Test
    @DisplayName("Should handle date range with no results")
    void testDateRangeNoResults() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        when(springDataRepository.findByTenantIdAndCreatedAtBetween(tenantId, startDate, endDate))
                .thenReturn(List.of());

        List<SentimentAnalysis> results = repository.findByTenantIdAndDateRange(tenantId, startDate, endDate);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(mapper, never()).toAggregate(any());
    }
}
