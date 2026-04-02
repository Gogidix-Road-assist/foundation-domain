package com.gogidix.rapidassist.ai.contentanalysis.domain.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ContentMetrics domain model
 */
class ContentMetricsTest {

    @Test
    void testContentMetricsCreation() {
        ContentMetrics metrics = ContentMetrics.builder()
                .qualityScore(85.0)
                .engagementScore(75.0)
                .clarityScore(80.0)
                .coherenceScore(78.0)
                .originalityScore(70.0)
                .grammarScore(90.0)
                .spellingScore(88.0)
                .uniqueWordCount(450)
                .averageWordLength(5)
                .averageSentenceLength(15)
                .vocabularyRichness(0.65)
                .lexicalDiversity(0.72)
                .build();

        assertNotNull(metrics);
        assertEquals(85.0, metrics.getQualityScore());
        assertEquals(75.0, metrics.getEngagementScore());
        assertEquals(450, metrics.getUniqueWordCount());
    }

    @Test
    void testMeetsQualityThreshold() {
        ContentMetrics metrics = ContentMetrics.builder()
                .qualityScore(85.0)
                .build();

        assertTrue(metrics.meetsQualityThreshold(80.0));
        assertFalse(metrics.meetsQualityThreshold(90.0));
    }

    @Test
    void testHasAcceptableGrammar() {
        ContentMetrics metrics = ContentMetrics.builder()
                .grammarScore(85.0)
                .build();

        assertTrue(metrics.hasAcceptableGrammar(80.0));
        assertFalse(metrics.hasAcceptableGrammar(90.0));
    }

    @Test
    void testHasAcceptableSpelling() {
        ContentMetrics metrics = ContentMetrics.builder()
                .spellingScore(90.0)
                .build();

        assertTrue(metrics.hasAcceptableSpelling(85.0));
        assertFalse(metrics.hasAcceptableSpelling(95.0));
    }

    @Test
    void testGetComplexityLevel() {
        ContentMetrics simple = ContentMetrics.builder()
                .averageSentenceLength(8)
                .build();

        assertEquals("Simple", simple.getComplexityLevel());

        ContentMetrics moderate = ContentMetrics.builder()
                .averageSentenceLength(15)
                .build();

        assertEquals("Moderate", moderate.getComplexityLevel());

        ContentMetrics complex = ContentMetrics.builder()
                .averageSentenceLength(25)
                .build();

        assertEquals("Complex", complex.getComplexityLevel());
    }

    @Test
    void testGetTopImprovementSuggestion() {
        Map<String, String> suggestions = new HashMap<>();
        suggestions.put("grammar", "Improve grammar usage");
        suggestions.put("vocabulary", "Use more diverse vocabulary");

        ContentMetrics metrics = ContentMetrics.builder()
                .improvementSuggestions(suggestions)
                .build();

        String suggestion = metrics.getTopImprovementSuggestion();

        assertNotNull(suggestion);
        assertTrue(suggestion.contains("Improve"));
    }

    @Test
    void testGetTopImprovementSuggestionWhenEmpty() {
        ContentMetrics metrics = ContentMetrics.builder()
                .improvementSuggestions(null)
                .build();

        String suggestion = metrics.getTopImprovementSuggestion();

        assertEquals("No specific suggestions", suggestion);
    }

    @Test
    void testVeryComplexLevel() {
        ContentMetrics metrics = ContentMetrics.builder()
                .averageSentenceLength(35)
                .build();

        assertEquals("Very Complex", metrics.getComplexityLevel());
    }

    @Test
    void testUnknownComplexityLevel() {
        ContentMetrics metrics = ContentMetrics.builder()
                .averageSentenceLength(null)
                .build();

        assertEquals("Unknown", metrics.getComplexityLevel());
    }
}
