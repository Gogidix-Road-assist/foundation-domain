package com.gogidix.rapidassist.ai.contentanalysis.domain.aggregate;

import com.gogidix.rapidassist.ai.contentanalysis.domain.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ContentAnalysisAggregate
 */
class ContentAnalysisAggregateTest {

    @Test
    void testAggregateInitialization() {
        ContentAnalysisAggregate aggregate = ContentAnalysisAggregate.initialize(
                "tenant-1",
                "content-1",
                "article",
                "Test Article",
                "This is a test article content.",
                "en"
        );

        assertNotNull(aggregate);
        assertNotNull(aggregate.getContentAnalysis());
        assertEquals("tenant-1", aggregate.getContentAnalysis().getTenantId());
        assertEquals("content-1", aggregate.getContentAnalysis().getContentId());
        assertEquals("article", aggregate.getContentAnalysis().getContentType());
        assertEquals(ContentAnalysis.AnalysisStatus.PENDING, aggregate.getContentAnalysis().getStatus());
        assertTrue(aggregate.getTopics().isEmpty());
    }

    @Test
    void testStartAnalysis() {
        ContentAnalysisAggregate aggregate = ContentAnalysisAggregate.initialize(
                "tenant-1",
                "content-1",
                "article",
                "Test Article",
                "This is a test article content.",
                "en"
        );

        aggregate.startAnalysis();

        assertEquals(ContentAnalysis.AnalysisStatus.IN_PROGRESS, aggregate.getContentAnalysis().getStatus());
        assertTrue(aggregate.isAnalysisInProgress());
    }

    @Test
    void testStartAnalysisWhenNotPending() {
        ContentAnalysisAggregate aggregate = ContentAnalysisAggregate.initialize(
                "tenant-1",
                "content-1",
                "article",
                "Test Article",
                "This is a test article content.",
                "en"
        );

        aggregate.getContentAnalysis().setStatus(ContentAnalysis.AnalysisStatus.COMPLETED);

        assertThrows(IllegalStateException.class, aggregate::startAnalysis);
    }

    @Test
    void testCompleteAnalysis() {
        ContentAnalysisAggregate aggregate = ContentAnalysisAggregate.initialize(
                "tenant-1",
                "content-1",
                "article",
                "Test Article",
                "This is a test article content.",
                "en"
        );

        aggregate.startAnalysis();

        ContentMetrics metrics = ContentMetrics.builder()
                .qualityScore(80.0)
                .build();

        SentimentAnalysis sentiment = SentimentAnalysis.builder()
                .sentimentScore(0.6)
                .sentimentType(SentimentAnalysis.SentimentType.POSITIVE)
                .build();

        SEOAnalysis seo = SEOAnalysis.builder()
                .seoScore(75.0)
                .build();

        ReadabilityAnalysis readability = ReadabilityAnalysis.builder()
                .readabilityScore(70.0)
                .build();

        aggregate.completeAnalysis(metrics, sentiment, seo, readability);

        assertTrue(aggregate.isAnalysisComplete());
        assertEquals(ContentAnalysis.AnalysisStatus.COMPLETED, aggregate.getContentAnalysis().getStatus());
        assertNotNull(aggregate.getContentAnalysis().getAnalyzedAt());
    }

    @Test
    void testFailAnalysis() {
        ContentAnalysisAggregate aggregate = ContentAnalysisAggregate.initialize(
                "tenant-1",
                "content-1",
                "article",
                "Test Article",
                "This is a test article content.",
                "en"
        );

        aggregate.startAnalysis();
        aggregate.failAnalysis("Analysis failed due to error");

        assertTrue(aggregate.isAnalysisFailed());
        assertEquals(ContentAnalysis.AnalysisStatus.FAILED, aggregate.getContentAnalysis().getStatus());
    }

    @Test
    void testAddTopic() {
        ContentAnalysisAggregate aggregate = ContentAnalysisAggregate.initialize(
                "tenant-1",
                "content-1",
                "article",
                "Test Article",
                "This is a test article content.",
                "en"
        );

        ContentTopic topic = ContentTopic.builder()
                .id(UUID.randomUUID())
                .topicName("Test Topic")
                .topicCategory("General")
                .relevanceScore(0.85)
                .confidence(0.90)
                .build();

        aggregate.addTopic(topic);

        assertEquals(1, aggregate.getTopics().size());
        assertEquals("tenant-1", aggregate.getTopics().get(0).getTenantId());
        assertEquals("content-1", aggregate.getTopics().get(0).getContentId());
    }

    @Test
    void testGetPrimaryTopics() {
        ContentAnalysisAggregate aggregate = ContentAnalysisAggregate.initialize(
                "tenant-1",
                "content-1",
                "article",
                "Test Article",
                "This is a test article content.",
                "en"
        );

        ContentTopic highRelevance = ContentTopic.builder()
                .id(UUID.randomUUID())
                .topicName("High Relevance Topic")
                .relevanceScore(0.85)
                .build();

        ContentTopic lowRelevance = ContentTopic.builder()
                .id(UUID.randomUUID())
                .topicName("Low Relevance Topic")
                .relevanceScore(0.50)
                .build();

        aggregate.addTopic(highRelevance);
        aggregate.addTopic(lowRelevance);

        List<ContentTopic> primaryTopics = aggregate.getPrimaryTopics();

        assertEquals(1, primaryTopics.size());
        assertEquals("High Relevance Topic", primaryTopics.get(0).getTopicName());
    }

    @Test
    void testCalculateContentStatistics() {
        ContentAnalysisAggregate aggregate = ContentAnalysisAggregate.initialize(
                "tenant-1",
                "content-1",
                "article",
                "Test Article",
                "This is a test article content. It has multiple sentences. And several words.",
                "en"
        );

        aggregate.calculateContentStatistics();

        assertNotNull(aggregate.getContentAnalysis().getWordCount());
        assertNotNull(aggregate.getContentAnalysis().getCharacterCount());
        assertNotNull(aggregate.getContentAnalysis().getSentenceCount());
        assertTrue(aggregate.getContentAnalysis().getWordCount() > 0);
    }

    @Test
    void testGetAnalysisSummary() {
        ContentAnalysisAggregate aggregate = ContentAnalysisAggregate.initialize(
                "tenant-1",
                "content-1",
                "article",
                "Test Article",
                "This is a test article content.",
                "en"
        );

        String summary = aggregate.getAnalysisSummary();

        assertNotNull(summary);
        assertTrue(summary.contains("Content Analysis Summary"));
        assertTrue(summary.contains("PENDING"));
    }

    @Test
    void testSetAnalysisRequest() {
        ContentAnalysisAggregate aggregate = ContentAnalysisAggregate.initialize(
                "tenant-1",
                "content-1",
                "article",
                "Test Article",
                "This is a test article content.",
                "en"
        );

        AnalysisRequest request = AnalysisRequest.builder()
                .id(UUID.randomUUID())
                .requestId("req-1")
                .status(AnalysisRequest.RequestStatus.PENDING)
                .priority(5)
                .build();

        aggregate.setAnalysisRequest(request);

        assertNotNull(aggregate.getAnalysisRequest());
        assertEquals("tenant-1", aggregate.getAnalysisRequest().getTenantId());
        assertEquals("content-1", aggregate.getAnalysisRequest().getContentId());
    }
}
