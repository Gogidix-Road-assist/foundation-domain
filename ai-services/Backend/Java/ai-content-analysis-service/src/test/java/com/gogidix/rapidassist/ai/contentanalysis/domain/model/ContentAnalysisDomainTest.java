package com.gogidix.rapidassist.ai.contentanalysis.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for ContentAnalysis domain model
 * Tests all business logic, getters, setters, and state transitions
 */
@DisplayName("ContentAnalysis Domain Model Tests")
class ContentAnalysisDomainTest {

    @Test
    @DisplayName("Should create ContentAnalysis with builder")
    void shouldCreateContentAnalysisWithBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        String contentId = "content-456";
        ContentAnalysis.AnalysisStatus status = ContentAnalysis.AnalysisStatus.PENDING;

        // When
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(id)
                .tenantId(tenantId)
                .contentId(contentId)
                .status(status)
                .contentType("article")
                .contentTitle("Test Article")
                .contentBody("This is test content")
                .contentLanguage("en")
                .build();

        // Then
        assertNotNull(analysis);
        assertEquals(id, analysis.getId());
        assertEquals(tenantId, analysis.getTenantId());
        assertEquals(contentId, analysis.getContentId());
        assertEquals(status, analysis.getStatus());
        assertEquals("article", analysis.getContentType());
        assertEquals("Test Article", analysis.getContentTitle());
    }

    @Test
    @DisplayName("Should identify completed analysis correctly")
    void shouldIdentifyCompletedAnalysis() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .status(ContentAnalysis.AnalysisStatus.COMPLETED)
                .build();

        // When & Then
        assertTrue(analysis.isCompleted());
        assertFalse(analysis.isInProgress());
        assertFalse(analysis.isFailed());
    }

    @Test
    @DisplayName("Should identify in-progress analysis correctly")
    void shouldIdentifyInProgressAnalysis() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .status(ContentAnalysis.AnalysisStatus.IN_PROGRESS)
                .build();

        // When & Then
        assertTrue(analysis.isInProgress());
        assertFalse(analysis.isCompleted());
        assertFalse(analysis.isFailed());
    }

    @Test
    @DisplayName("Should identify failed analysis correctly")
    void shouldIdentifyFailedAnalysis() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .status(ContentAnalysis.AnalysisStatus.FAILED)
                .build();

        // When & Then
        assertTrue(analysis.isFailed());
        assertFalse(analysis.isCompleted());
        assertFalse(analysis.isInProgress());
    }

    @Test
    @DisplayName("Should calculate overall score with all metrics")
    void shouldCalculateOverallScoreWithAllMetrics() {
        // Given
        ContentMetrics metrics = ContentMetrics.builder()
                .qualityScore(80.0)
                .build();

        SentimentAnalysis sentiment = SentimentAnalysis.builder()
                .sentimentScore(0.7)
                .build();

        SEOAnalysis seoAnalysis = SEOAnalysis.builder()
                .seoScore(75.0)
                .build();

        ReadabilityAnalysis readability = ReadabilityAnalysis.builder()
                .readabilityScore(85.0)
                .build();

        ContentAnalysis analysis = ContentAnalysis.builder()
                .metrics(metrics)
                .sentiment(sentiment)
                .seoAnalysis(seoAnalysis)
                .readability(readability)
                .build();

        // When
        analysis.calculateOverallScore();

        // Then
        assertNotNull(analysis.getOverallScore());
        // Weighted: (80*0.30) + (85*0.25) + (0.7*0.20) + (75*0.25)
        // = 24.0 + 21.25 + 0.14 + 18.75 = 64.14
        assertTrue(analysis.getOverallScore() > 64.0 && analysis.getOverallScore() < 65.0);
    }

    @Test
    @DisplayName("Should return 0 score when metrics are null")
    void shouldReturn0ScoreWhenMetricsAreNull() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .metrics(null)
                .build();

        // When
        analysis.calculateOverallScore();

        // Then
        assertEquals(0.0, analysis.getOverallScore());
    }

    @Test
    @DisplayName("Should calculate score with partial metrics")
    void shouldCalculateScoreWithPartialMetrics() {
        // Given
        ContentMetrics metrics = ContentMetrics.builder()
                .qualityScore(90.0)
                .build();

        ContentAnalysis analysis = ContentAnalysis.builder()
                .metrics(metrics)
                .sentiment(null)
                .seoAnalysis(null)
                .readability(null)
                .build();

        // When
        analysis.calculateOverallScore();

        // Then
        assertEquals(27.0, analysis.getOverallScore()); // 90 * 0.30
    }

    @Test
    @DisplayName("Should mark analysis as completed")
    void shouldMarkAnalysisAsCompleted() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .status(ContentAnalysis.AnalysisStatus.IN_PROGRESS)
                .metrics(ContentMetrics.builder().qualityScore(85.0).build())
                .build();

        // When
        analysis.markAsCompleted();

        // Then
        assertEquals(ContentAnalysis.AnalysisStatus.COMPLETED, analysis.getStatus());
        assertNotNull(analysis.getAnalyzedAt());
        assertNotNull(analysis.getUpdatedAt());
        assertNotNull(analysis.getOverallScore());
    }

    @Test
    @DisplayName("Should mark analysis as failed")
    void shouldMarkAnalysisAsFailed() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .status(ContentAnalysis.AnalysisStatus.IN_PROGRESS)
                .build();

        // When
        analysis.markAsFailed();

        // Then
        assertEquals(ContentAnalysis.AnalysisStatus.FAILED, analysis.getStatus());
        assertNotNull(analysis.getUpdatedAt());
    }

    @Test
    @DisplayName("Should mark analysis as in progress")
    void shouldMarkAnalysisAsInProgress() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .status(ContentAnalysis.AnalysisStatus.PENDING)
                .build();

        // When
        analysis.markAsInProgress();

        // Then
        assertEquals(ContentAnalysis.AnalysisStatus.IN_PROGRESS, analysis.getStatus());
        assertNotNull(analysis.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return grade A for score 90+")
    void shouldReturnGradeAFor90Plus() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .overallScore(92.0)
                .build();

        // When
        String grade = analysis.getQualityGrade();

        // Then
        assertEquals("A", grade);
    }

    @Test
    @DisplayName("Should return grade B for score 80-89")
    void shouldReturnGradeBFor80To89() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .overallScore(85.0)
                .build();

        // When
        String grade = analysis.getQualityGrade();

        // Then
        assertEquals("B", grade);
    }

    @Test
    @DisplayName("Should return grade C for score 70-79")
    void shouldReturnGradeCFor70To79() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .overallScore(75.0)
                .build();

        // When
        String grade = analysis.getQualityGrade();

        // Then
        assertEquals("C", grade);
    }

    @Test
    @DisplayName("Should return grade D for score 60-69")
    void shouldReturnGradeDFor60To69() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .overallScore(65.0)
                .build();

        // When
        String grade = analysis.getQualityGrade();

        // Then
        assertEquals("D", grade);
    }

    @Test
    @DisplayName("Should return grade F for score below 60")
    void shouldReturnGradeFForBelow60() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .overallScore(55.0)
                .build();

        // When
        String grade = analysis.getQualityGrade();

        // Then
        assertEquals("F", grade);
    }

    @Test
    @DisplayName("Should return N/A when overall score is null")
    void shouldReturnNAWhenOverallScoreIsNull() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .overallScore(null)
                .build();

        // When
        String grade = analysis.getQualityGrade();

        // Then
        assertEquals("N/A", grade);
    }

    @Test
    @DisplayName("Should return grade A for exact score 90")
    void shouldReturnGradeAForExact90() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .overallScore(90.0)
                .build();

        // When
        String grade = analysis.getQualityGrade();

        // Then
        assertEquals("A", grade);
    }

    @Test
    @DisplayName("Should return grade B for exact score 80")
    void shouldReturnGradeBForExact80() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .overallScore(80.0)
                .build();

        // When
        String grade = analysis.getQualityGrade();

        // Then
        assertEquals("B", grade);
    }

    @Test
    @DisplayName("Should return grade C for exact score 70")
    void shouldReturnGradeCForExact70() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .overallScore(70.0)
                .build();

        // When
        String grade = analysis.getQualityGrade();

        // Then
        assertEquals("C", grade);
    }

    @Test
    @DisplayName("Should return grade D for exact score 60")
    void shouldReturnGradeDForExact60() {
        // Given
        ContentAnalysis analysis = ContentAnalysis.builder()
                .overallScore(60.0)
                .build();

        // When
        String grade = analysis.getQualityGrade();

        // Then
        assertEquals("D", grade);
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        ContentAnalysis analysis = new ContentAnalysis();

        // Then
        assertNotNull(analysis);
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // When
        ContentAnalysis analysis = new ContentAnalysis(
                id, "tenant1", "content1",
                ContentAnalysis.AnalysisStatus.COMPLETED,
                "article", "Title", "Body", "en",
                null, null, null, null,
                100, 500, 10, 2,
                now, now, now, "user1", "user2",
                "v1.0", 85.0
        );

        // Then
        assertNotNull(analysis);
        assertEquals(id, analysis.getId());
        assertEquals("tenant1", analysis.getTenantId());
        assertEquals(85.0, analysis.getOverallScore());
    }

    @Test
    @DisplayName("Should use Lombok setters correctly")
    void shouldUseSettersCorrectly() {
        // Given
        ContentAnalysis analysis = new ContentAnalysis();

        // When
        analysis.setId(UUID.randomUUID());
        analysis.setTenantId("tenant-123");
        analysis.setContentId("content-456");
        analysis.setStatus(ContentAnalysis.AnalysisStatus.COMPLETED);
        analysis.setOverallScore(88.0);

        // Then
        assertNotNull(analysis.getId());
        assertEquals("tenant-123", analysis.getTenantId());
        assertEquals("content-456", analysis.getContentId());
        assertEquals(ContentAnalysis.AnalysisStatus.COMPLETED, analysis.getStatus());
        assertEquals(88.0, analysis.getOverallScore());
    }

    @Test
    @DisplayName("Should handle all status enum values")
    void shouldHandleAllStatusEnumValues() {
        // Given & When & Then
        assertEquals(4, ContentAnalysis.AnalysisStatus.values().length);
        assertEquals(ContentAnalysis.AnalysisStatus.PENDING, ContentAnalysis.AnalysisStatus.valueOf("PENDING"));
        assertEquals(ContentAnalysis.AnalysisStatus.IN_PROGRESS, ContentAnalysis.AnalysisStatus.valueOf("IN_PROGRESS"));
        assertEquals(ContentAnalysis.AnalysisStatus.COMPLETED, ContentAnalysis.AnalysisStatus.valueOf("COMPLETED"));
        assertEquals(ContentAnalysis.AnalysisStatus.FAILED, ContentAnalysis.AnalysisStatus.valueOf("FAILED"));
    }

    @Test
    @DisplayName("Should calculate score with null sentiment score")
    void shouldCalculateScoreWithNullSentimentScore() {
        // Given
        ContentMetrics metrics = ContentMetrics.builder()
                .qualityScore(80.0)
                .build();

        SentimentAnalysis nullSentimentScore = SentimentAnalysis.builder()
                .sentimentScore(null)
                .build();

        ContentAnalysis analysis = ContentAnalysis.builder()
                .metrics(metrics)
                .sentiment(nullSentimentScore)
                .seoAnalysis(null)
                .readability(null)
                .build();

        // When
        analysis.calculateOverallScore();

        // Then
        assertEquals(24.0, analysis.getOverallScore()); // 80 * 0.30
    }

    @Test
    @DisplayName("Should update analyzed at timestamp when marking as completed")
    void shouldUpdateAnalyzedAtWhenMarkingAsCompleted() {
        // Given
        LocalDateTime before = LocalDateTime.now();
        ContentAnalysis analysis = ContentAnalysis.builder()
                .status(ContentAnalysis.AnalysisStatus.IN_PROGRESS)
                .metrics(ContentMetrics.builder().qualityScore(75.0).build())
                .build();

        // When
        analysis.markAsCompleted();
        LocalDateTime after = LocalDateTime.now();

        // Then
        assertNotNull(analysis.getAnalyzedAt());
        assertTrue(analysis.getAnalyzedAt().isAfter(before.minusSeconds(1)));
        assertTrue(analysis.getAnalyzedAt().isBefore(after.plusSeconds(1)));
    }
}
