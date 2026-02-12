package com.gogidix.rapidassist.ai.contentanalysis.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ContentAnalysis domain model
 */
class ContentAnalysisTest {

    @Test
    void testContentAnalysisCreation() {
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .contentId("content-1")
                .status(ContentAnalysis.AnalysisStatus.PENDING)
                .contentType("article")
                .contentTitle("Test Article")
                .contentBody("This is a test content body.")
                .contentLanguage("en")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertNotNull(analysis);
        assertEquals("tenant-1", analysis.getTenantId());
        assertEquals("content-1", analysis.getContentId());
        assertEquals(ContentAnalysis.AnalysisStatus.PENDING, analysis.getStatus());
        assertEquals("article", analysis.getContentType());
    }

    @Test
    void testCalculateOverallScore() {
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .contentId("content-1")
                .status(ContentAnalysis.AnalysisStatus.PENDING)
                .metrics(ContentMetrics.builder()
                        .qualityScore(80.0)
                        .build())
                .readability(ReadabilityAnalysis.builder()
                        .readabilityScore(75.0)
                        .build())
                .sentiment(SentimentAnalysis.builder()
                        .sentimentScore(0.6)
                        .build())
                .seoAnalysis(SEOAnalysis.builder()
                        .seoScore(70.0)
                        .build())
                .build();

        analysis.calculateOverallScore();

        assertNotNull(analysis.getOverallScore());
        assertTrue(analysis.getOverallScore() > 0);
    }

    @Test
    void testMarkAsCompleted() {
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .contentId("content-1")
                .status(ContentAnalysis.AnalysisStatus.IN_PROGRESS)
                .build();

        analysis.markAsCompleted();

        assertEquals(ContentAnalysis.AnalysisStatus.COMPLETED, analysis.getStatus());
        assertNotNull(analysis.getAnalyzedAt());
    }

    @Test
    void testMarkAsFailed() {
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .contentId("content-1")
                .status(ContentAnalysis.AnalysisStatus.IN_PROGRESS)
                .build();

        analysis.markAsFailed();

        assertEquals(ContentAnalysis.AnalysisStatus.FAILED, analysis.getStatus());
    }

    @Test
    void testIsCompleted() {
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(UUID.randomUUID())
                .status(ContentAnalysis.AnalysisStatus.COMPLETED)
                .build();

        assertTrue(analysis.isCompleted());
    }

    @Test
    void testGetQualityGrade() {
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(UUID.randomUUID())
                .overallScore(85.0)
                .build();

        String grade = analysis.getQualityGrade();
        assertEquals("B", grade);
    }

    @Test
    void testGetQualityGradeA() {
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(UUID.randomUUID())
                .overallScore(92.0)
                .build();

        String grade = analysis.getQualityGrade();
        assertEquals("A", grade);
    }

    @Test
    void testIsInProgress() {
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(UUID.randomUUID())
                .status(ContentAnalysis.AnalysisStatus.IN_PROGRESS)
                .build();

        assertTrue(analysis.isInProgress());
    }

    @Test
    void testIsFailed() {
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(UUID.randomUUID())
                .status(ContentAnalysis.AnalysisStatus.FAILED)
                .build();

        assertTrue(analysis.isFailed());
    }
}
