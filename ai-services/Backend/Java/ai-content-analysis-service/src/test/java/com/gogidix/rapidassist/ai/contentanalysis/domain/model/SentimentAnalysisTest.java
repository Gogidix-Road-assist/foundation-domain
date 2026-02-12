package com.gogidix.rapidassist.ai.contentanalysis.domain.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SentimentAnalysis domain model
 */
class SentimentAnalysisTest {

    @Test
    void testSentimentAnalysisCreation() {
        SentimentAnalysis sentiment = SentimentAnalysis.builder()
                .sentimentScore(0.7)
                .sentimentType(SentimentAnalysis.SentimentType.POSITIVE)
                .confidence(0.85)
                .positivityScore(0.8)
                .negativityScore(0.1)
                .neutralityScore(0.1)
                .dominantEmotion("Joy")
                .positiveWordCount(25)
                .negativeWordCount(3)
                .neutralWordCount(12)
                .build();

        assertNotNull(sentiment);
        assertEquals(0.7, sentiment.getSentimentScore());
        assertEquals(SentimentAnalysis.SentimentType.POSITIVE, sentiment.getSentimentType());
        assertEquals(0.85, sentiment.getConfidence());
    }

    @Test
    void testIsPositive() {
        SentimentAnalysis sentiment = SentimentAnalysis.builder()
                .sentimentType(SentimentAnalysis.SentimentType.POSITIVE)
                .build();

        assertTrue(sentiment.isPositive());
        assertFalse(sentiment.isNegative());
        assertFalse(sentiment.isNeutral());
    }

    @Test
    void testIsNegative() {
        SentimentAnalysis sentiment = SentimentAnalysis.builder()
                .sentimentType(SentimentAnalysis.SentimentType.NEGATIVE)
                .build();

        assertTrue(sentiment.isNegative());
        assertFalse(sentiment.isPositive());
        assertFalse(sentiment.isNeutral());
    }

    @Test
    void testIsNeutral() {
        SentimentAnalysis sentiment = SentimentAnalysis.builder()
                .sentimentType(SentimentAnalysis.SentimentType.NEUTRAL)
                .build();

        assertTrue(sentiment.isNeutral());
        assertFalse(sentiment.isPositive());
        assertFalse(sentiment.isNegative());
    }

    @Test
    void testHasHighConfidence() {
        SentimentAnalysis sentiment = SentimentAnalysis.builder()
                .confidence(0.90)
                .build();

        assertTrue(sentiment.hasHighConfidence(0.85));
        assertFalse(sentiment.hasHighConfidence(0.95));
    }

    @Test
    void testGetSentimentDescription() {
        SentimentAnalysis positive = SentimentAnalysis.builder()
                .sentimentType(SentimentAnalysis.SentimentType.POSITIVE)
                .build();

        String desc = positive.getSentimentDescription();
        assertTrue(desc.contains("positive"));

        SentimentAnalysis negative = SentimentAnalysis.builder()
                .sentimentType(SentimentAnalysis.SentimentType.NEGATIVE)
                .build();

        desc = negative.getSentimentDescription();
        assertTrue(desc.contains("negative"));
    }

    @Test
    void testGetEmotionIntensity() {
        SentimentAnalysis veryStrong = SentimentAnalysis.builder()
                .sentimentScore(0.85)
                .build();

        assertEquals("Very Strong", veryStrong.getEmotionIntensity());

        SentimentAnalysis strong = SentimentAnalysis.builder()
                .sentimentScore(0.65)
                .build();

        assertEquals("Strong", strong.getEmotionIntensity());

        SentimentAnalysis moderate = SentimentAnalysis.builder()
                .sentimentScore(0.45)
                .build();

        assertEquals("Moderate", moderate.getEmotionIntensity());

        SentimentAnalysis mild = SentimentAnalysis.builder()
                .sentimentScore(0.25)
                .build();

        assertEquals("Mild", mild.getEmotionIntensity());

        SentimentAnalysis weak = SentimentAnalysis.builder()
                .sentimentScore(0.1)
                .build();

        assertEquals("Weak", weak.getEmotionIntensity());
    }

    @Test
    void testGetEmotionIntensityNull() {
        SentimentAnalysis sentiment = SentimentAnalysis.builder()
                .sentimentScore(null)
                .build();

        assertEquals("Unknown", sentiment.getEmotionIntensity());
    }

    @Test
    void testGetSentimentDescriptionUnknown() {
        SentimentAnalysis sentiment = SentimentAnalysis.builder()
                .sentimentType(null)
                .build();

        assertEquals("Unknown", sentiment.getSentimentDescription());
    }

    @Test
    void testMixedSentiment() {
        SentimentAnalysis sentiment = SentimentAnalysis.builder()
                .sentimentType(SentimentAnalysis.SentimentType.MIXED)
                .build();

        String desc = sentiment.getSentimentDescription();
        assertTrue(desc.contains("mixed"));
    }
}
