package com.gogidix.rapidassist.ai.sentiment.interfaces.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.sentiment.application.command.AnalyzeSentimentCommand;
import com.gogidix.rapidassist.ai.sentiment.application.command.BatchAnalyzeSentimentCommand;
import com.gogidix.rapidassist.ai.sentiment.application.dto.BatchSentimentResultDto;
import com.gogidix.rapidassist.ai.sentiment.application.dto.SentimentAnalysisDto;
import com.gogidix.rapidassist.ai.sentiment.application.dto.SentimentTrendDto;
import com.gogidix.rapidassist.ai.sentiment.application.service.SentimentAnalysisApplicationService;
import com.gogidix.rapidassist.ai.sentiment.domain.model.AnalysisStatus;
import com.gogidix.rapidassist.ai.sentiment.domain.model.SentimentType;
import com.gogidix.rapidassist.ai.sentiment.domain.model.SentimentCategory;
import com.gogidix.rapidassist.ai.sentiment.interfaces.rest.request.AnalyzeSentimentRequest;
import com.gogidix.rapidassist.ai.sentiment.interfaces.rest.request.BatchAnalyzeSentimentRequest;
import com.gogidix.rapidassist.ai.sentiment.interfaces.rest.response.SentimentAnalysisResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for SentimentAnalysisRestController
 * Tests REST API endpoints, request/response handling, and HTTP status codes
 */
@WebMvcTest(SentimentAnalysisRestController.class)
@DisplayName("SentimentAnalysisRestController Tests")
class SentimentAnalysisRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SentimentAnalysisApplicationService applicationService;

    private String tenantId;
    private String userId;
    private UUID analysisId;
    private SentimentAnalysisDto testDto;
    private SentimentAnalysisResponse testResponse;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-123";
        userId = "user-456";
        analysisId = UUID.randomUUID();

        testDto = SentimentAnalysisDto.builder()
                .id(analysisId)
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

        testResponse = SentimentAnalysisResponse.builder()
                .id(analysisId)
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
    @DisplayName("Should analyze sentiment - POST /api/v1/sentiment/analyze")
    void testAnalyzeSentiment() throws Exception {
        AnalyzeSentimentRequest request = AnalyzeSentimentRequest.builder()
                .userId(userId)
                .text("This is amazing!")
                .language("en")
                .sourceType("feedback")
                .sourceId("fb-001")
                .includeEmotions(true)
                .includeAspects(true)
                .build();

        when(applicationService.analyzeSentiment(any(AnalyzeSentimentCommand.class)))
                .thenReturn(testDto);

        mockMvc.perform(post("/api/v1/sentiment/analyze")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(analysisId.toString()))
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.text").value("This is a great product!"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.overallSentiment").value("POSITIVE"))
                .andExpect(jsonPath("$.sentimentScore").value(0.8))
                .andExpect(jsonPath("$.confidence").value(0.85));

        verify(applicationService, times(1)).analyzeSentiment(any(AnalyzeSentimentCommand.class));
    }

    @Test
    @DisplayName("Should return 400 for invalid analyze request")
    void testAnalyzeSentimentValidation() throws Exception {
        AnalyzeSentimentRequest request = AnalyzeSentimentRequest.builder()
                .userId(userId)
                .text("") // Empty text should fail validation
                .build();

        mockMvc.perform(post("/api/v1/sentiment/analyze")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(applicationService, never()).analyzeSentiment(any());
    }

    @Test
    @DisplayName("Should batch analyze sentiment - POST /api/v1/sentiment/batch-analyze")
    void testBatchAnalyzeSentiment() throws Exception {
        List<String> texts = Arrays.asList(
                "Great product!",
                "Excellent service",
                "Amazing quality"
        );

        BatchAnalyzeSentimentRequest request = BatchAnalyzeSentimentRequest.builder()
                .userId(userId)
                .texts(texts)
                .language("en")
                .sourceType("feedback")
                .sourceId("batch-001")
                .includeEmotions(true)
                .includeAspects(true)
                .build();

        BatchSentimentResultDto batchResult = BatchSentimentResultDto.builder()
                .batchId(UUID.randomUUID().toString())
                .totalCount(3)
                .successCount(3)
                .failureCount(0)
                .results(List.of(testDto, testDto, testDto))
                .build();

        when(applicationService.batchAnalyzeSentiment(any(BatchAnalyzeSentimentCommand.class)))
                .thenReturn(batchResult);

        mockMvc.perform(post("/api/v1/sentiment/batch-analyze")
                        .header("X-Tenant-ID", tenantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").exists())
                .andExpect(jsonPath("$.totalCount").value(3))
                .andExpect(jsonPath("$.successCount").value(3))
                .andExpect(jsonPath("$.failureCount").value(0))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results.length()").value(3));

        verify(applicationService, times(1)).batchAnalyzeSentiment(any(BatchAnalyzeSentimentCommand.class));
    }

    @Test
    @DisplayName("Should get sentiment analysis by ID - GET /api/v1/sentiment/analyses/{analysisId}")
    void testGetSentimentAnalysis() throws Exception {
        when(applicationService.getSentimentAnalysis(any()))
                .thenReturn(testDto);

        mockMvc.perform(get("/api/v1/sentiment/analyses/{analysisId}", analysisId)
                        .header("X-Tenant-ID", tenantId)
                        .param("includeEmotions", "true")
                        .param("includeAspects", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(analysisId.toString()))
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.text").value("This is a great product!"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.overallSentiment").value("POSITIVE"));

        verify(applicationService, times(1)).getSentimentAnalysis(any());
    }

    @Test
    @DisplayName("Should get sentiment analysis without emotions and aspects")
    void testGetSentimentAnalysisWithoutEmotionsAndAspects() throws Exception {
        testDto.setEmotions(null);
        testDto.setAspects(null);

        when(applicationService.getSentimentAnalysis(any()))
                .thenReturn(testDto);

        mockMvc.perform(get("/api/v1/sentiment/analyses/{analysisId}", analysisId)
                        .header("X-Tenant-ID", tenantId)
                        .param("includeEmotions", "false")
                        .param("includeAspects", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(analysisId.toString()))
                .andExpect(jsonPath("$.emotions").isEmpty())
                .andExpect(jsonPath("$.aspects").isEmpty());

        verify(applicationService, times(1)).getSentimentAnalysis(any());
    }

    @Test
    @DisplayName("Should return 404 when analysis not found")
    void testGetSentimentAnalysisNotFound() throws Exception {
        when(applicationService.getSentimentAnalysis(any()))
                .thenThrow(new IllegalArgumentException("Sentiment analysis not found"));

        mockMvc.perform(get("/api/v1/sentiment/analyses/{analysisId}", analysisId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNotFound());

        verify(applicationService, times(1)).getSentimentAnalysis(any());
    }

    @Test
    @DisplayName("Should get user sentiment analyses - GET /api/v1/sentiment/analyses/user/{userId}")
    void testGetUserSentimentAnalyses() throws Exception {
        List<SentimentAnalysisDto> analyses = List.of(testDto);

        when(applicationService.getUserSentimentAnalyses(any()))
                .thenReturn(analyses);

        mockMvc.perform(get("/api/v1/sentiment/analyses/user/{userId}", userId)
                        .header("X-Tenant-ID", tenantId)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(analysisId.toString()))
                .andExpect(jsonPath("$[0].userId").value(userId));

        verify(applicationService, times(1)).getUserSentimentAnalyses(any());
    }

    @Test
    @DisplayName("Should get user sentiment analyses with filters")
    void testGetUserSentimentAnalysesWithFilters() throws Exception {
        List<SentimentAnalysisDto> analyses = List.of(testDto);

        when(applicationService.getUserSentimentAnalyses(any()))
                .thenReturn(analyses);

        mockMvc.perform(get("/api/v1/sentiment/analyses/user/{userId}", userId)
                        .header("X-Tenant-ID", tenantId)
                        .param("status", "COMPLETED")
                        .param("sentimentType", "POSITIVE")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(applicationService, times(1)).getUserSentimentAnalyses(any());
    }

    @Test
    @DisplayName("Should get user sentiment analyses with date range")
    void testGetUserSentimentAnalysesWithDateRange() throws Exception {
        List<SentimentAnalysisDto> analyses = List.of(testDto);

        when(applicationService.getUserSentimentAnalyses(any()))
                .thenReturn(analyses);

        String startDate = LocalDateTime.now().minusDays(7).toString();
        String endDate = LocalDateTime.now().toString();

        mockMvc.perform(get("/api/v1/sentiment/analyses/user/{userId}", userId)
                        .header("X-Tenant-ID", tenantId)
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(applicationService, times(1)).getUserSentimentAnalyses(any());
    }

    @Test
    @DisplayName("Should return empty list when no analyses found")
    void testGetUserSentimentAnalysesEmpty() throws Exception {
        when(applicationService.getUserSentimentAnalyses(any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/sentiment/analyses/user/{userId}", userId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(applicationService, times(1)).getUserSentimentAnalyses(any());
    }

    @Test
    @DisplayName("Should get sentiment trend - GET /api/v1/sentiment/trends")
    void testGetSentimentTrend() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        Map<SentimentType, Long> distribution = new EnumMap<>(SentimentType.class);
        distribution.put(SentimentType.POSITIVE, 10L);
        distribution.put(SentimentType.NEGATIVE, 3L);
        distribution.put(SentimentType.NEUTRAL, 5L);

        SentimentTrendDto trend = SentimentTrendDto.builder()
                .tenantId(tenantId)
                .userId(userId)
                .startDate(startDate)
                .endDate(endDate)
                .totalAnalyses(18L)
                .sentimentDistribution(distribution)
                .averageSentimentScore(0.65)
                .dominantSentiment(SentimentType.POSITIVE)
                .positivePercentage(55.56)
                .negativePercentage(16.67)
                .neutralPercentage(27.78)
                .build();

        when(applicationService.getSentimentTrend(eq(tenantId), eq(userId), any(), any()))
                .thenReturn(trend);

        mockMvc.perform(get("/api/v1/sentiment/trends")
                        .header("X-Tenant-ID", tenantId)
                        .param("userId", userId)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.totalAnalyses").value(18))
                .andExpect(jsonPath("$.averageSentimentScore").value(0.65))
                .andExpect(jsonPath("$.dominantSentiment").value("POSITIVE"))
                .andExpect(jsonPath("$.positivePercentage").value(55.56))
                .andExpect(jsonPath("$.negativePercentage").value(16.67))
                .andExpect(jsonPath("$.neutralPercentage").value(27.78));

        verify(applicationService, times(1)).getSentimentTrend(eq(tenantId), eq(userId), any(), any());
    }

    @Test
    @DisplayName("Should get sentiment trend without user filter")
    void testGetSentimentTrendWithoutUser() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        SentimentTrendDto trend = SentimentTrendDto.builder()
                .tenantId(tenantId)
                .userId(null)
                .startDate(startDate)
                .endDate(endDate)
                .totalAnalyses(100L)
                .sentimentDistribution(new EnumMap<>(SentimentType.class))
                .averageSentimentScore(0.6)
                .dominantSentiment(SentimentType.POSITIVE)
                .positivePercentage(60.0)
                .negativePercentage(20.0)
                .neutralPercentage(20.0)
                .build();

        when(applicationService.getSentimentTrend(eq(tenantId), eq(null), any(), any()))
                .thenReturn(trend);

        mockMvc.perform(get("/api/v1/sentiment/trends")
                        .header("X-Tenant-ID", tenantId)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.userId").isEmpty())
                .andExpect(jsonPath("$.totalAnalyses").value(100));

        verify(applicationService, times(1)).getSentimentTrend(eq(tenantId), eq(null), any(), any());
    }

    @Test
    @DisplayName("Should delete sentiment analysis - DELETE /api/v1/sentiment/analyses/{analysisId}")
    void testDeleteSentimentAnalysis() throws Exception {
        doNothing().when(applicationService).deleteSentimentAnalysis(eq(tenantId), eq(analysisId));

        mockMvc.perform(delete("/api/v1/sentiment/analyses/{analysisId}", analysisId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNoContent());

        verify(applicationService, times(1)).deleteSentimentAnalysis(eq(tenantId), eq(analysisId));
    }

    @Test
    @DisplayName("Should require X-Tenant-ID header")
    void testRequireTenantIdHeader() throws Exception {
        AnalyzeSentimentRequest request = AnalyzeSentimentRequest.builder()
                .userId(userId)
                .text("Test text")
                .build();

        mockMvc.perform(post("/api/v1/sentiment/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(applicationService, never()).analyzeSentiment(any());
    }

    @Test
    @DisplayName("Should handle pagination parameters correctly")
    void testPaginationParameters() throws Exception {
        List<SentimentAnalysisDto> analyses = List.of(testDto);

        when(applicationService.getUserSentimentAnalyses(any()))
                .thenReturn(analyses);

        mockMvc.perform(get("/api/v1/sentiment/analyses/user/{userId}", userId)
                        .header("X-Tenant-ID", tenantId)
                        .param("page", "2")
                        .param("size", "50")
                        .param("sortBy", "sentimentScore")
                        .param("sortDirection", "asc"))
                .andExpect(status().isOk());

        verify(applicationService, times(1)).getUserSentimentAnalyses(any());
    }

    @Test
    @DisplayName("Should use default pagination values")
    void testDefaultPaginationValues() throws Exception {
        List<SentimentAnalysisDto> analyses = List.of(testDto);

        when(applicationService.getUserSentimentAnalyses(any()))
                .thenReturn(analyses);

        mockMvc.perform(get("/api/v1/sentiment/analyses/user/{userId}", userId)
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk());

        verify(applicationService, times(1)).getUserSentimentAnalyses(any());
    }

    @Test
    @DisplayName("Should handle multiple filter combinations")
    void testMultipleFilterCombinations() throws Exception {
        List<SentimentAnalysisDto> analyses = List.of(testDto);

        when(applicationService.getUserSentimentAnalyses(any()))
                .thenReturn(analyses);

        String startDate = LocalDateTime.now().minusDays(7).toString();
        String endDate = LocalDateTime.now().toString();

        mockMvc.perform(get("/api/v1/sentiment/analyses/user/{userId}", userId)
                        .header("X-Tenant-ID", tenantId)
                        .param("status", "COMPLETED")
                        .param("sentimentType", "POSITIVE")
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(applicationService, times(1)).getUserSentimentAnalyses(any());
    }

    @Test
    @DisplayName("Should return 400 for invalid UUID in analysis ID")
    void testInvalidAnalysisId() throws Exception {
        when(applicationService.getSentimentAnalysis(any()))
                .thenThrow(new IllegalArgumentException("Invalid UUID"));

        mockMvc.perform(get("/api/v1/sentiment/analyses/{analysisId}", "invalid-uuid")
                        .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNotFound());
    }
}
