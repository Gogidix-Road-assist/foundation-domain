package com.gogidix.rapidassist.ai.sentiment.application.service;

import com.gogidix.rapidassist.ai.sentiment.application.command.AnalyzeSentimentCommand;
import com.gogidix.rapidassist.ai.sentiment.application.command.BatchAnalyzeSentimentCommand;
import com.gogidix.rapidassist.ai.sentiment.application.dto.*;
import com.gogidix.rapidassist.ai.sentiment.application.mapper.SentimentAnalysisMapper;
import com.gogidix.rapidassist.ai.sentiment.application.query.GetSentimentAnalysisQuery;
import com.gogidix.rapidassist.ai.sentiment.application.query.GetUserSentimentAnalysesQuery;
import com.gogidix.rapidassist.ai.sentiment.domain.aggregate.SentimentAnalysis;
import com.gogidix.rapidassist.ai.sentiment.domain.model.*;
import com.gogidix.rapidassist.ai.sentiment.domain.repository.SentimentAnalysisRepositoryPort;
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
 * Unit tests for SentimentAnalysisApplicationService
 * Tests application layer logic, use cases, and integration with domain
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SentimentAnalysisApplicationService Tests")
class SentimentAnalysisApplicationServiceTest {

    @Mock
    private SentimentAnalysisRepositoryPort repository;

    @Mock
    private SentimentAnalysisMapper mapper;

    @InjectMocks
    private SentimentAnalysisApplicationService applicationService;

    private SentimentAnalysis testAnalysis;
    private SentimentAnalysisDto testDto;
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

        testDto = SentimentAnalysisDto.builder()
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
                .build();
    }

    @Test
    @DisplayName("Should analyze sentiment successfully")
    void testAnalyzeSentiment() {
        AnalyzeSentimentCommand command = AnalyzeSentimentCommand.builder()
                .tenantId(tenantId)
                .userId(userId)
                .text("This is amazing!")
                .language("en")
                .sourceType("feedback")
                .sourceId("fb-001")
                .includeEmotions(true)
                .includeAspects(true)
                .build();

        when(repository.save(any(SentimentAnalysis.class))).thenReturn(testAnalysis);
        when(mapper.toDto(any(SentimentAnalysis.class))).thenReturn(testDto);

        SentimentAnalysisDto result = applicationService.analyzeSentiment(command);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals(userId, result.getUserId());
        assertEquals("COMPLETED", result.getStatus());

        verify(repository, times(2)).save(any(SentimentAnalysis.class));
        verify(mapper, times(1)).toDto(any(SentimentAnalysis.class));
    }

    @Test
    @DisplayName("Should analyze sentiment without emotions and aspects")
    void testAnalyzeSentimentWithoutEmotionsAndAspects() {
        AnalyzeSentimentCommand command = AnalyzeSentimentCommand.builder()
                .tenantId(tenantId)
                .userId(userId)
                .text("Good product")
                .sourceType("feedback")
                .sourceId("fb-002")
                .includeEmotions(false)
                .includeAspects(false)
                .build();

        when(repository.save(any(SentimentAnalysis.class))).thenReturn(testAnalysis);
        when(mapper.toDto(any(SentimentAnalysis.class))).thenReturn(testDto);

        SentimentAnalysisDto result = applicationService.analyzeSentiment(command);

        assertNotNull(result);
        verify(repository, times(2)).save(any(SentimentAnalysis.class));
    }

    @Test
    @DisplayName("Should analyze sentiment with metadata")
    void testAnalyzeSentimentWithMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "mobile-app");
        metadata.put("version", "2.0");

        AnalyzeSentimentCommand command = AnalyzeSentimentCommand.builder()
                .tenantId(tenantId)
                .userId(userId)
                .text("Great service!")
                .sourceType("feedback")
                .sourceId("fb-003")
                .metadata(metadata)
                .build();

        when(repository.save(any(SentimentAnalysis.class))).thenReturn(testAnalysis);
        when(mapper.toDto(any(SentimentAnalysis.class))).thenReturn(testDto);

        SentimentAnalysisDto result = applicationService.analyzeSentiment(command);

        assertNotNull(result);
        verify(repository, times(2)).save(any(SentimentAnalysis.class));
    }

    @Test
    @DisplayName("Should batch analyze sentiment successfully")
    void testBatchAnalyzeSentiment() {
        List<String> texts = Arrays.asList(
                "Great product!",
                "Excellent service",
                "Amazing quality"
        );

        BatchAnalyzeSentimentCommand command = BatchAnalyzeSentimentCommand.builder()
                .tenantId(tenantId)
                .userId(userId)
                .texts(texts)
                .language("en")
                .sourceType("feedback")
                .sourceId("batch-001")
                .includeEmotions(true)
                .includeAspects(true)
                .build();

        when(repository.save(any(SentimentAnalysis.class))).thenReturn(testAnalysis);
        when(mapper.toDto(any(SentimentAnalysis.class))).thenReturn(testDto);

        BatchSentimentResultDto result = applicationService.batchAnalyzeSentiment(command);

        assertNotNull(result);
        assertNotNull(result.getBatchId());
        assertEquals(3, result.getTotalCount());
        assertEquals(3, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(3, result.getResults().size());
    }

    @Test
    @DisplayName("Should handle batch with partial failures")
    void testBatchAnalyzeSentimentWithFailures() {
        List<String> texts = Arrays.asList(
                "Great product!",
                "Excellent service",
                "Amazing quality"
        );

        BatchAnalyzeSentimentCommand command = BatchAnalyzeSentimentCommand.builder()
                .tenantId(tenantId)
                .userId(userId)
                .texts(texts)
                .sourceType("feedback")
                .sourceId("batch-002")
                .build();

        when(repository.save(any(SentimentAnalysis.class)))
                .thenReturn(testAnalysis)
                .thenThrow(new RuntimeException("Processing error"))
                .thenReturn(testAnalysis);
        when(mapper.toDto(any(SentimentAnalysis.class))).thenReturn(testDto);

        BatchSentimentResultDto result = applicationService.batchAnalyzeSentiment(command);

        assertNotNull(result);
        assertEquals(3, result.getTotalCount());
        assertEquals(2, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
    }

    @Test
    @DisplayName("Should get sentiment analysis by ID")
    void testGetSentimentAnalysis() {
        GetSentimentAnalysisQuery query = GetSentimentAnalysisQuery.builder()
                .tenantId(tenantId)
                .analysisId(testId)
                .includeEmotions(true)
                .includeAspects(true)
                .build();

        when(repository.findByTenantIdAndId(tenantId, testId))
                .thenReturn(Optional.of(testAnalysis));
        when(mapper.toDto(testAnalysis)).thenReturn(testDto);

        SentimentAnalysisDto result = applicationService.getSentimentAnalysis(query);

        assertNotNull(result);
        assertEquals(testId, result.getId());

        verify(repository, times(1)).findByTenantIdAndId(tenantId, testId);
        verify(mapper, times(1)).toDto(testAnalysis);
    }

    @Test
    @DisplayName("Should throw exception when sentiment analysis not found")
    void testGetSentimentAnalysisNotFound() {
        GetSentimentAnalysisQuery query = GetSentimentAnalysisQuery.builder()
                .tenantId(tenantId)
                .analysisId(testId)
                .build();

        when(repository.findByTenantIdAndId(tenantId, testId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.getSentimentAnalysis(query);
        });
    }

    @Test
    @DisplayName("Should get sentiment analysis without emotions and aspects")
    void testGetSentimentAnalysisWithoutEmotionsAndAspects() {
        GetSentimentAnalysisQuery query = GetSentimentAnalysisQuery.builder()
                .tenantId(tenantId)
                .analysisId(testId)
                .includeEmotions(false)
                .includeAspects(false)
                .build();

        testDto.setEmotions(List.of());
        testDto.setAspects(List.of());

        when(repository.findByTenantIdAndId(tenantId, testId))
                .thenReturn(Optional.of(testAnalysis));
        when(mapper.toDto(testAnalysis)).thenReturn(testDto);

        SentimentAnalysisDto result = applicationService.getSentimentAnalysis(query);

        assertNotNull(result);
        assertNull(result.getEmotions());
        assertNull(result.getAspects());
    }

    @Test
    @DisplayName("Should get user sentiment analyses")
    void testGetUserSentimentAnalyses() {
        GetUserSentimentAnalysesQuery query = GetUserSentimentAnalysesQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .page(0)
                .size(20)
                .build();

        List<SentimentAnalysis> analyses = List.of(testAnalysis);

        when(repository.findByTenantIdAndUserId(tenantId, userId))
                .thenReturn(analyses);
        when(mapper.toDto(testAnalysis)).thenReturn(testDto);

        List<SentimentAnalysisDto> results = applicationService.getUserSentimentAnalyses(query);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testId, results.get(0).getId());

        verify(repository, times(1)).findByTenantIdAndUserId(tenantId, userId);
    }

    @Test
    @DisplayName("Should get user sentiment analyses with date range filter")
    void testGetUserSentimentAnalysesWithDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        GetUserSentimentAnalysesQuery query = GetUserSentimentAnalysesQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .startDate(startDate)
                .endDate(endDate)
                .page(0)
                .size(20)
                .build();

        List<SentimentAnalysis> analyses = List.of(testAnalysis);

        when(repository.findByTenantIdAndDateRange(tenantId, startDate, endDate))
                .thenReturn(analyses);
        when(mapper.toDto(testAnalysis)).thenReturn(testDto);

        List<SentimentAnalysisDto> results = applicationService.getUserSentimentAnalyses(query);

        assertNotNull(results);
        assertEquals(1, results.size());

        verify(repository, times(1)).findByTenantIdAndDateRange(tenantId, startDate, endDate);
    }

    @Test
    @DisplayName("Should get user sentiment analyses with status filter")
    void testGetUserSentimentAnalysesWithStatus() {
        GetUserSentimentAnalysesQuery query = GetUserSentimentAnalysesQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .status("COMPLETED")
                .page(0)
                .size(20)
                .build();

        List<SentimentAnalysis> analyses = List.of(testAnalysis);

        when(repository.findByTenantIdAndStatus(tenantId, "COMPLETED"))
                .thenReturn(analyses);
        when(mapper.toDto(testAnalysis)).thenReturn(testDto);

        List<SentimentAnalysisDto> results = applicationService.getUserSentimentAnalyses(query);

        assertNotNull(results);
        assertEquals(1, results.size());

        verify(repository, times(1)).findByTenantIdAndStatus(tenantId, "COMPLETED");
    }

    @Test
    @DisplayName("Should get user sentiment analyses with sentiment type filter")
    void testGetUserSentimentAnalysesWithSentimentType() {
        GetUserSentimentAnalysesQuery query = GetUserSentimentAnalysesQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .sentimentType("POSITIVE")
                .page(0)
                .size(20)
                .build();

        List<SentimentAnalysis> analyses = List.of(testAnalysis);

        when(repository.findByTenantIdAndSentimentType(tenantId, "POSITIVE"))
                .thenReturn(analyses);
        when(mapper.toDto(testAnalysis)).thenReturn(testDto);

        List<SentimentAnalysisDto> results = applicationService.getUserSentimentAnalyses(query);

        assertNotNull(results);
        assertEquals(1, results.size());

        verify(repository, times(1)).findByTenantIdAndSentimentType(tenantId, "POSITIVE");
    }

    @Test
    @DisplayName("Should apply pagination to user sentiment analyses")
    void testGetUserSentimentAnalysesWithPagination() {
        List<SentimentAnalysis> analyses = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            analyses.add(testAnalysis);
        }

        GetUserSentimentAnalysesQuery query = GetUserSentimentAnalysesQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .page(1)
                .size(10)
                .build();

        when(repository.findByTenantIdAndUserId(tenantId, userId))
                .thenReturn(analyses);
        when(mapper.toDto(any(SentimentAnalysis.class))).thenReturn(testDto);

        List<SentimentAnalysisDto> results = applicationService.getUserSentimentAnalyses(query);

        assertNotNull(results);
        assertEquals(10, results.size()); // Second page, size 10
    }

    @Test
    @DisplayName("Should get sentiment trend")
    void testGetSentimentTrend() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        List<SentimentAnalysis> analyses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SentimentAnalysis analysis = SentimentAnalysis.builder()
                    .id(UUID.randomUUID())
                    .tenantId(tenantId)
                    .userId(userId)
                    .overallSentiment(i % 2 == 0 ? SentimentType.POSITIVE : SentimentType.NEGATIVE)
                    .sentimentScore(0.5 + (i * 0.05))
                    .build();
            analyses.add(analysis);
        }

        when(repository.findByTenantIdAndDateRange(tenantId, startDate, endDate))
                .thenReturn(analyses);

        SentimentTrendDto trend = applicationService.getSentimentTrend(tenantId, userId, startDate, endDate);

        assertNotNull(trend);
        assertEquals(tenantId, trend.getTenantId());
        assertEquals(userId, trend.getUserId());
        assertEquals(startDate, trend.getStartDate());
        assertEquals(endDate, trend.getEndDate());
        assertEquals(10, trend.getTotalAnalyses());
        assertNotNull(trend.getSentimentDistribution());
        assertTrue(trend.getAverageSentimentScore() > 0);
        assertNotNull(trend.getDominantSentiment());

        verify(repository, times(1)).findByTenantIdAndDateRange(tenantId, startDate, endDate);
    }

    @Test
    @DisplayName("Should get sentiment trend without user filter")
    void testGetSentimentTrendWithoutUser() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        List<SentimentAnalysis> analyses = List.of(
                createAnalysisWithSentiment(SentimentType.POSITIVE, 0.8),
                createAnalysisWithSentiment(SentimentType.POSITIVE, 0.7),
                createAnalysisWithSentiment(SentimentType.NEGATIVE, 0.6)
        );

        when(repository.findByTenantIdAndDateRange(tenantId, startDate, endDate))
                .thenReturn(analyses);

        SentimentTrendDto trend = applicationService.getSentimentTrend(tenantId, null, startDate, endDate);

        assertNotNull(trend);
        assertEquals(3, trend.getTotalAnalyses());
        assertNull(trend.getUserId());

        verify(repository, times(1)).findByTenantIdAndDateRange(tenantId, startDate, endDate);
    }

    @Test
    @DisplayName("Should calculate sentiment distribution correctly")
    void testSentimentDistribution() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        List<SentimentAnalysis> analyses = List.of(
                createAnalysisWithSentiment(SentimentType.POSITIVE, 0.8),
                createAnalysisWithSentiment(SentimentType.POSITIVE, 0.7),
                createAnalysisWithSentiment(SentimentType.NEGATIVE, 0.6),
                createAnalysisWithSentiment(SentimentType.NEUTRAL, 0.5)
        );

        when(repository.findByTenantIdAndDateRange(tenantId, startDate, endDate))
                .thenReturn(analyses);

        SentimentTrendDto trend = applicationService.getSentimentTrend(tenantId, userId, startDate, endDate);

        assertNotNull(trend.getSentimentDistribution());
        assertEquals(2L, trend.getSentimentDistribution().get(SentimentType.POSITIVE));
        assertEquals(1L, trend.getSentimentDistribution().get(SentimentType.NEGATIVE));
        assertEquals(1L, trend.getSentimentDistribution().get(SentimentType.NEUTRAL));

        assertEquals(50.0, trend.getPositivePercentage(), 0.01);
        assertEquals(25.0, trend.getNegativePercentage(), 0.01);
        assertEquals(25.0, trend.getNeutralPercentage(), 0.01);
    }

    @Test
    @DisplayName("Should delete sentiment analysis")
    void testDeleteSentimentAnalysis() {
        doNothing().when(repository).deleteByTenantIdAndId(tenantId, testId);

        applicationService.deleteSentimentAnalysis(tenantId, testId);

        verify(repository, times(1)).deleteByTenantIdAndId(tenantId, testId);
    }

    @Test
    @DisplayName("Should handle empty sentiment trend")
    void testGetEmptySentimentTrend() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        when(repository.findByTenantIdAndDateRange(tenantId, startDate, endDate))
                .thenReturn(List.of());

        SentimentTrendDto trend = applicationService.getSentimentTrend(tenantId, userId, startDate, endDate);

        assertNotNull(trend);
        assertEquals(0, trend.getTotalAnalyses());
        assertEquals(0.0, trend.getAverageSentimentScore(), 0.01);
        assertNull(trend.getDominantSentiment());
        assertEquals(0.0, trend.getPositivePercentage(), 0.01);
        assertEquals(0.0, trend.getNegativePercentage(), 0.01);
        assertEquals(0.0, trend.getNeutralPercentage(), 0.01);
    }

    @Test
    @DisplayName("Should handle user filter in sentiment trend")
    void testGetSentimentTrendWithUserFilter() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        SentimentAnalysis analysis1 = createAnalysisWithSentimentAndUser(SentimentType.POSITIVE, 0.8, userId);
        SentimentAnalysis analysis2 = createAnalysisWithSentimentAndUser(SentimentType.POSITIVE, 0.7, "other-user");
        SentimentAnalysis analysis3 = createAnalysisWithSentimentAndUser(SentimentType.NEGATIVE, 0.6, userId);

        List<SentimentAnalysis> analyses = List.of(analysis1, analysis2, analysis3);

        when(repository.findByTenantIdAndDateRange(tenantId, startDate, endDate))
                .thenReturn(analyses);

        SentimentTrendDto trend = applicationService.getSentimentTrend(tenantId, userId, startDate, endDate);

        assertNotNull(trend);
        assertEquals(2, trend.getTotalAnalyses()); // Only user's analyses

        Map<SentimentType, Long> distribution = trend.getSentimentDistribution();
        assertEquals(1L, distribution.get(SentimentType.POSITIVE));
        assertEquals(1L, distribution.get(SentimentType.NEGATIVE));
    }

    private SentimentAnalysis createAnalysisWithSentiment(SentimentType sentimentType, double score) {
        return SentimentAnalysis.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .overallSentiment(sentimentType)
                .sentimentScore(score)
                .build();
    }

    private SentimentAnalysis createAnalysisWithSentimentAndUser(SentimentType sentimentType, double score, String user) {
        return SentimentAnalysis.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(user)
                .overallSentiment(sentimentType)
                .sentimentScore(score)
                .build();
    }
}
