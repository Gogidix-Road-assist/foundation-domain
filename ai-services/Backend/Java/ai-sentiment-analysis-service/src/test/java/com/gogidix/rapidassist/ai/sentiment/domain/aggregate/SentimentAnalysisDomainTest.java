package com.gogidix.rapidassist.ai.sentiment.domain.aggregate;

import com.gogidix.rapidassist.ai.sentiment.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SentimentAnalysis domain aggregate
 * Tests business logic, state transitions, and domain rules
 */
@DisplayName("SentimentAnalysis Domain Tests")
class SentimentAnalysisDomainTest {

    private SentimentAnalysis sentimentAnalysis;
    private UUID testId;
    private String tenantId;
    private String userId;
    private String testText;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        tenantId = "tenant-123";
        userId = "user-456";
        testText = "This is a great product! I love it!";

        sentimentAnalysis = SentimentAnalysis.builder()
                .id(testId)
                .tenantId(tenantId)
                .userId(userId)
                .text(testText)
                .sourceType("feedback")
                .sourceId("feedback-789")
                .status(AnalysisStatus.PENDING)
                .language("en")
                .emotions(new ArrayList<>())
                .aspects(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should initialize sentiment analysis correctly")
    void testInitialize() {
        SentimentAnalysis analysis = SentimentAnalysis.initialize(
                "tenant-123",
                "user-456",
                "Amazing service!",
                "feedback",
                "fb-001"
        );

        assertNotNull(analysis.getId());
        assertEquals("tenant-123", analysis.getTenantId());
        assertEquals("user-456", analysis.getUserId());
        assertEquals("Amazing service!", analysis.getText());
        assertEquals("feedback", analysis.getSourceType());
        assertEquals("fb-001", analysis.getSourceId());
        assertEquals(AnalysisStatus.PENDING, analysis.getStatus());
        assertEquals("en", analysis.getLanguage());
        assertEquals(2, analysis.getWordCount());
        assertEquals(16, analysis.getCharacterCount());
        assertNotNull(analysis.getCreatedAt());
        assertNotNull(analysis.getUpdatedAt());
        assertTrue(analysis.getEmotions().isEmpty());
        assertTrue(analysis.getAspects().isEmpty());
    }

    @Test
    @DisplayName("Should initialize with null text")
    void testInitializeWithNullText() {
        SentimentAnalysis analysis = SentimentAnalysis.initialize(
                "tenant-123",
                "user-456",
                null,
                "feedback",
                "fb-001"
        );

        assertNull(analysis.getText());
        assertEquals(0, analysis.getWordCount());
        assertEquals(0, analysis.getCharacterCount());
    }

    @Test
    @DisplayName("Should mark as processing from pending status")
    void testMarkAsProcessing() {
        sentimentAnalysis.markAsProcessing();

        assertEquals(AnalysisStatus.PROCESSING, sentimentAnalysis.getStatus());
        assertNotNull(sentimentAnalysis.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when marking non-pending as processing")
    void testMarkAsProcessingFromNonPending() {
        sentimentAnalysis.setStatus(AnalysisStatus.COMPLETED);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> sentimentAnalysis.markAsProcessing()
        );

        assertTrue(exception.getMessage().contains("Cannot start processing from status"));
    }

    @Test
    @DisplayName("Should mark as completed with results")
    void testMarkAsCompleted() {
        sentimentAnalysis.setStatus(AnalysisStatus.PROCESSING);
        sentimentAnalysis.markAsCompleted(
                SentimentType.POSITIVE,
                SentimentCategory.VERY_POSITIVE,
                0.95,
                0.87
        );

        assertEquals(AnalysisStatus.COMPLETED, sentimentAnalysis.getStatus());
        assertEquals(SentimentType.POSITIVE, sentimentAnalysis.getOverallSentiment());
        assertEquals(SentimentCategory.VERY_POSITIVE, sentimentAnalysis.getSentimentCategory());
        assertEquals(0.95, sentimentAnalysis.getSentimentScore());
        assertEquals(0.87, sentimentAnalysis.getConfidence());
        assertNotNull(sentimentAnalysis.getAnalyzedAt());
        assertNotNull(sentimentAnalysis.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when marking non-processing as completed")
    void testMarkAsCompletedFromNonProcessing() {
        sentimentAnalysis.setStatus(AnalysisStatus.PENDING);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> sentimentAnalysis.markAsCompleted(
                        SentimentType.POSITIVE,
                        SentimentCategory.POSITIVE,
                        0.8,
                        0.7
                )
        );

        assertTrue(exception.getMessage().contains("Cannot complete from status"));
    }

    @Test
    @DisplayName("Should mark as failed with error message")
    void testMarkAsFailed() {
        String errorMessage = "AI service unavailable";
        sentimentAnalysis.markAsFailed(errorMessage);

        assertEquals(AnalysisStatus.FAILED, sentimentAnalysis.getStatus());
        assertEquals(errorMessage, sentimentAnalysis.getErrorMessage());
        assertNotNull(sentimentAnalysis.getUpdatedAt());
    }

    @Test
    @DisplayName("Should add emotion to analysis")
    void testAddEmotion() {
        Emotion emotion = Emotion.builder()
                .emotionType(EmotionType.JOY)
                .intensity(0.9)
                .confidence(0.85)
                .description("Joy detected")
                .build();

        sentimentAnalysis.addEmotion(emotion);

        assertEquals(1, sentimentAnalysis.getEmotions().size());
        assertTrue(sentimentAnalysis.getEmotions().contains(emotion));
    }

    @Test
    @DisplayName("Should initialize emotions list if null when adding emotion")
    void testAddEmotionWithNullList() {
        sentimentAnalysis.setEmotions(null);

        Emotion emotion = Emotion.builder()
                .emotionType(EmotionType.JOY)
                .intensity(0.9)
                .confidence(0.85)
                .build();

        sentimentAnalysis.addEmotion(emotion);

        assertNotNull(sentimentAnalysis.getEmotions());
        assertEquals(1, sentimentAnalysis.getEmotions().size());
    }

    @Test
    @DisplayName("Should add aspect to analysis")
    void testAddAspect() {
        Aspect aspect = Aspect.builder()
                .aspectName("service")
                .sentiment(SentimentType.POSITIVE)
                .confidence(0.88)
                .opinionText("Great service quality")
                .build();

        sentimentAnalysis.addAspect(aspect);

        assertEquals(1, sentimentAnalysis.getAspects().size());
        assertTrue(sentimentAnalysis.getAspects().contains(aspect));
    }

    @Test
    @DisplayName("Should initialize aspects list if null when adding aspect")
    void testAddAspectWithNullList() {
        sentimentAnalysis.setAspects(null);

        Aspect aspect = Aspect.builder()
                .aspectName("quality")
                .sentiment(SentimentType.POSITIVE)
                .confidence(0.9)
                .build();

        sentimentAnalysis.addAspect(aspect);

        assertNotNull(sentimentAnalysis.getAspects());
        assertEquals(1, sentimentAnalysis.getAspects().size());
    }

    @Test
    @DisplayName("Should check if analysis is completed")
    void testIsCompleted() {
        sentimentAnalysis.setStatus(AnalysisStatus.COMPLETED);

        assertTrue(sentimentAnalysis.isCompleted());

        sentimentAnalysis.setStatus(AnalysisStatus.PENDING);
        assertFalse(sentimentAnalysis.isCompleted());
    }

    @Test
    @DisplayName("Should check if analysis is failed")
    void testIsFailed() {
        sentimentAnalysis.setStatus(AnalysisStatus.FAILED);

        assertTrue(sentimentAnalysis.isFailed());

        sentimentAnalysis.setStatus(AnalysisStatus.COMPLETED);
        assertFalse(sentimentAnalysis.isFailed());
    }

    @Test
    @DisplayName("Should check if analysis is processing")
    void testIsProcessing() {
        sentimentAnalysis.setStatus(AnalysisStatus.PROCESSING);

        assertTrue(sentimentAnalysis.isProcessing());

        sentimentAnalysis.setStatus(AnalysisStatus.PENDING);
        assertFalse(sentimentAnalysis.isProcessing());
    }

    @Test
    @DisplayName("Should check if sentiment is positive")
    void testIsPositive() {
        sentimentAnalysis.setOverallSentiment(SentimentType.POSITIVE);

        assertTrue(sentimentAnalysis.isPositive());

        sentimentAnalysis.setOverallSentiment(SentimentType.NEGATIVE);
        assertFalse(sentimentAnalysis.isPositive());
    }

    @Test
    @DisplayName("Should check if sentiment is negative")
    void testIsNegative() {
        sentimentAnalysis.setOverallSentiment(SentimentType.NEGATIVE);

        assertTrue(sentimentAnalysis.isNegative());

        sentimentAnalysis.setOverallSentiment(SentimentType.POSITIVE);
        assertFalse(sentimentAnalysis.isNegative());
    }

    @Test
    @DisplayName("Should check if sentiment is neutral")
    void testIsNeutral() {
        sentimentAnalysis.setOverallSentiment(SentimentType.NEUTRAL);

        assertTrue(sentimentAnalysis.isNeutral());

        sentimentAnalysis.setOverallSentiment(SentimentType.POSITIVE);
        assertFalse(sentimentAnalysis.isNeutral());
    }

    @Test
    @DisplayName("Should check if sentiment is mixed")
    void testIsMixed() {
        sentimentAnalysis.setOverallSentiment(SentimentType.MIXED);

        assertTrue(sentimentAnalysis.isMixed());

        sentimentAnalysis.setOverallSentiment(SentimentType.POSITIVE);
        assertFalse(sentimentAnalysis.isMixed());
    }

    @Test
    @DisplayName("Should get dominant emotion")
    void testGetDominantEmotion() {
        Emotion joy = Emotion.builder()
                .emotionType(EmotionType.JOY)
                .intensity(0.9)
                .confidence(0.85)
                .build();

        Emotion sadness = Emotion.builder()
                .emotionType(EmotionType.SADNESS)
                .intensity(0.5)
                .confidence(0.7)
                .build();

        sentimentAnalysis.setEmotions(List.of(joy, sadness));

        Emotion dominant = sentimentAnalysis.getDominantEmotion();

        assertNotNull(dominant);
        assertEquals(EmotionType.JOY, dominant.getEmotionType());
        assertEquals(0.9, dominant.getIntensity());
    }

    @Test
    @DisplayName("Should return null when getting dominant emotion from empty list")
    void testGetDominantEmotionEmpty() {
        sentimentAnalysis.setEmotions(new ArrayList<>());

        Emotion dominant = sentimentAnalysis.getDominantEmotion();

        assertNull(dominant);
    }

    @Test
    @DisplayName("Should return null when getting dominant emotion from null list")
    void testGetDominantEmotionNull() {
        sentimentAnalysis.setEmotions(null);

        Emotion dominant = sentimentAnalysis.getDominantEmotion();

        assertNull(dominant);
    }

    @Test
    @DisplayName("Should get emotions by type")
    void testGetEmotionsByType() {
        Emotion joy1 = Emotion.builder()
                .emotionType(EmotionType.JOY)
                .intensity(0.9)
                .confidence(0.85)
                .build();

        Emotion joy2 = Emotion.builder()
                .emotionType(EmotionType.JOY)
                .intensity(0.7)
                .confidence(0.75)
                .build();

        Emotion sadness = Emotion.builder()
                .emotionType(EmotionType.SADNESS)
                .intensity(0.5)
                .confidence(0.7)
                .build();

        sentimentAnalysis.setEmotions(List.of(joy1, sadness, joy2));

        List<Emotion> joyEmotions = sentimentAnalysis.getEmotionsByType(EmotionType.JOY);

        assertEquals(2, joyEmotions.size());
        assertTrue(joyEmotions.stream().allMatch(e -> e.getEmotionType() == EmotionType.JOY));
    }

    @Test
    @DisplayName("Should return empty list when getting emotions by type from null list")
    void testGetEmotionsByTypeNull() {
        sentimentAnalysis.setEmotions(null);

        List<Emotion> emotions = sentimentAnalysis.getEmotionsByType(EmotionType.JOY);

        assertNotNull(emotions);
        assertTrue(emotions.isEmpty());
    }

    @Test
    @DisplayName("Should get positive aspects")
    void testGetPositiveAspects() {
        Aspect positive1 = Aspect.builder()
                .aspectName("service")
                .sentiment(SentimentType.POSITIVE)
                .confidence(0.9)
                .build();

        Aspect negative = Aspect.builder()
                .aspectName("price")
                .sentiment(SentimentType.NEGATIVE)
                .confidence(0.8)
                .build();

        Aspect positive2 = Aspect.builder()
                .aspectName("quality")
                .sentiment(SentimentType.POSITIVE)
                .confidence(0.85)
                .build();

        sentimentAnalysis.setAspects(List.of(positive1, negative, positive2));

        List<Aspect> positiveAspects = sentimentAnalysis.getPositiveAspects();

        assertEquals(2, positiveAspects.size());
        assertTrue(positiveAspects.stream().allMatch(a -> a.getSentiment() == SentimentType.POSITIVE));
    }

    @Test
    @DisplayName("Should return empty list when getting positive aspects from null list")
    void testGetPositiveAspectsNull() {
        sentimentAnalysis.setAspects(null);

        List<Aspect> aspects = sentimentAnalysis.getPositiveAspects();

        assertNotNull(aspects);
        assertTrue(aspects.isEmpty());
    }

    @Test
    @DisplayName("Should get negative aspects")
    void testGetNegativeAspects() {
        Aspect positive = Aspect.builder()
                .aspectName("service")
                .sentiment(SentimentType.POSITIVE)
                .confidence(0.9)
                .build();

        Aspect negative1 = Aspect.builder()
                .aspectName("price")
                .sentiment(SentimentType.NEGATIVE)
                .confidence(0.8)
                .build();

        Aspect negative2 = Aspect.builder()
                .aspectName("delivery")
                .sentiment(SentimentType.NEGATIVE)
                .confidence(0.75)
                .build();

        sentimentAnalysis.setAspects(List.of(positive, negative1, negative2));

        List<Aspect> negativeAspects = sentimentAnalysis.getNegativeAspects();

        assertEquals(2, negativeAspects.size());
        assertTrue(negativeAspects.stream().allMatch(a -> a.getSentiment() == SentimentType.NEGATIVE));
    }

    @Test
    @DisplayName("Should get neutral aspects")
    void testGetNeutralAspects() {
        Aspect positive = Aspect.builder()
                .aspectName("service")
                .sentiment(SentimentType.POSITIVE)
                .confidence(0.9)
                .build();

        Aspect neutral1 = Aspect.builder()
                .aspectName("location")
                .sentiment(SentimentType.NEUTRAL)
                .confidence(0.7)
                .build();

        Aspect neutral2 = Aspect.builder()
                .aspectName("availability")
                .sentiment(SentimentType.NEUTRAL)
                .confidence(0.65)
                .build();

        sentimentAnalysis.setAspects(List.of(positive, neutral1, neutral2));

        List<Aspect> neutralAspects = sentimentAnalysis.getNeutralAspects();

        assertEquals(2, neutralAspects.size());
        assertTrue(neutralAspects.stream().allMatch(a -> a.getSentiment() == SentimentType.NEUTRAL));
    }

    @Test
    @DisplayName("Should update metadata")
    void testUpdateMetadata() {
        sentimentAnalysis.updateMetadata("source", "mobile-app");
        sentimentAnalysis.updateMetadata("version", "2.1.0");

        assertNotNull(sentimentAnalysis.getMetadata());
        assertEquals("mobile-app", sentimentAnalysis.getMetadata().get("source"));
        assertEquals("2.1.0", sentimentAnalysis.getMetadata().get("version"));
        assertNotNull(sentimentAnalysis.getUpdatedAt());
    }

    @Test
    @DisplayName("Should initialize metadata map if null when updating")
    void testUpdateMetadataWithNullMap() {
        sentimentAnalysis.setMetadata(null);

        sentimentAnalysis.updateMetadata("key", "value");

        assertNotNull(sentimentAnalysis.getMetadata());
        assertEquals("value", sentimentAnalysis.getMetadata().get("key"));
    }

    @Test
    @DisplayName("Should calculate analysis duration in seconds")
    void testGetAnalysisDurationSeconds() {
        LocalDateTime createdAt = LocalDateTime.now().minusSeconds(5);
        LocalDateTime analyzedAt = LocalDateTime.now();

        sentimentAnalysis.setCreatedAt(createdAt);
        sentimentAnalysis.setAnalyzedAt(analyzedAt);

        long duration = sentimentAnalysis.getAnalysisDurationSeconds();

        assertTrue(duration >= 4 && duration <= 6); // Allow some tolerance
    }

    @Test
    @DisplayName("Should return 0 duration when dates are null")
    void testGetAnalysisDurationSecondsWithNullDates() {
        sentimentAnalysis.setCreatedAt(null);
        sentimentAnalysis.setAnalyzedAt(null);

        long duration = sentimentAnalysis.getAnalysisDurationSeconds();

        assertEquals(0, duration);
    }

    @Test
    @DisplayName("Should return 0 duration when createdAt is null")
    void testGetAnalysisDurationSecondsWithNullCreatedAt() {
        sentimentAnalysis.setCreatedAt(null);
        sentimentAnalysis.setAnalyzedAt(LocalDateTime.now());

        long duration = sentimentAnalysis.getAnalysisDurationSeconds();

        assertEquals(0, duration);
    }

    @Test
    @DisplayName("Should return 0 duration when analyzedAt is null")
    void testGetAnalysisDurationSecondsWithNullAnalyzedAt() {
        sentimentAnalysis.setCreatedAt(LocalDateTime.now());
        sentimentAnalysis.setAnalyzedAt(null);

        long duration = sentimentAnalysis.getAnalysisDurationSeconds();

        assertEquals(0, duration);
    }

    @Test
    @DisplayName("Should handle multiple status transitions correctly")
    void testStatusTransitions() {
        assertEquals(AnalysisStatus.PENDING, sentimentAnalysis.getStatus());

        sentimentAnalysis.markAsProcessing();
        assertEquals(AnalysisStatus.PROCESSING, sentimentAnalysis.getStatus());

        sentimentAnalysis.markAsCompleted(
                SentimentType.POSITIVE,
                SentimentCategory.POSITIVE,
                0.8,
                0.75
        );
        assertEquals(AnalysisStatus.COMPLETED, sentimentAnalysis.getStatus());
    }

    @Test
    @DisplayName("Should handle multiple emotions and aspects")
    void testMultipleEmotionsAndAspects() {
        Emotion joy = Emotion.builder()
                .emotionType(EmotionType.JOY)
                .intensity(0.9)
                .confidence(0.85)
                .build();

        Emotion trust = Emotion.builder()
                .emotionType(EmotionType.TRUST)
                .intensity(0.7)
                .confidence(0.8)
                .build();

        Aspect service = Aspect.builder()
                .aspectName("service")
                .sentiment(SentimentType.POSITIVE)
                .confidence(0.9)
                .build();

        Aspect price = Aspect.builder()
                .aspectName("price")
                .sentiment(SentimentType.NEGATIVE)
                .confidence(0.8)
                .build();

        sentimentAnalysis.addEmotion(joy);
        sentimentAnalysis.addEmotion(trust);
        sentimentAnalysis.addAspect(service);
        sentimentAnalysis.addAspect(price);

        assertEquals(2, sentimentAnalysis.getEmotions().size());
        assertEquals(2, sentimentAnalysis.getAspects().size());
        assertEquals(1, sentimentAnalysis.getPositiveAspects().size());
        assertEquals(1, sentimentAnalysis.getNegativeAspects().size());
    }
}
