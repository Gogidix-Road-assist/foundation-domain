package com.gogidix.rapidassist.ai.tagging.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TagSuggestion domain model
 */
@DisplayName("TagSuggestion Domain Model Tests")
class TagSuggestionTest {

    @Test
    @DisplayName("Should create tag suggestion with builder")
    void shouldCreateTagSuggestionWithBuilder() {
        UUID suggestedTagId = UUID.randomUUID();
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .contentId("content-456")
                .contentType("article")
                .suggestedTagId(suggestedTagId)
                .confidenceScore(0.92)
                .status(TagSuggestion.SuggestionStatus.PENDING)
                .aiModelUsed("gpt-4")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .version(1L)
                .build();

        assertNotNull(suggestion);
        assertEquals("content-456", suggestion.getContentId());
        assertEquals(suggestedTagId, suggestion.getSuggestedTagId());
        assertEquals(0.92, suggestion.getConfidenceScore());
        assertEquals(TagSuggestion.SuggestionStatus.PENDING, suggestion.getStatus());
    }

    @Test
    @DisplayName("Should accept suggestion")
    void shouldAcceptSuggestion() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .status(TagSuggestion.SuggestionStatus.PENDING)
                .build();

        suggestion.accept();

        assertEquals(TagSuggestion.SuggestionStatus.ACCEPTED, suggestion.getStatus());
    }

    @Test
    @DisplayName("Should reject suggestion")
    void shouldRejectSuggestion() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .status(TagSuggestion.SuggestionStatus.PENDING)
                .build();

        suggestion.reject();

        assertEquals(TagSuggestion.SuggestionStatus.REJECTED, suggestion.getStatus());
    }

    @Test
    @DisplayName("Should mark as expired")
    void shouldMarkAsExpired() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .status(TagSuggestion.SuggestionStatus.PENDING)
                .build();

        suggestion.markAsExpired();

        assertEquals(TagSuggestion.SuggestionStatus.EXPIRED, suggestion.getStatus());
    }

    @Test
    @DisplayName("Should return true when pending")
    void shouldReturnTrueWhenPending() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .status(TagSuggestion.SuggestionStatus.PENDING)
                .build();

        assertTrue(suggestion.isPending());
    }

    @Test
    @DisplayName("Should return false when not pending")
    void shouldReturnFalseWhenNotPending() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .status(TagSuggestion.SuggestionStatus.ACCEPTED)
                .build();

        assertFalse(suggestion.isPending());
    }

    @Test
    @DisplayName("Should return true when expired")
    void shouldReturnTrueWhenExpired() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .expiresAt(LocalDateTime.now().minusHours(1))
                .build();

        assertTrue(suggestion.isExpired());
    }

    @Test
    @DisplayName("Should return false when not expired")
    void shouldReturnFalseWhenNotExpired() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        assertFalse(suggestion.isExpired());
    }

    @Test
    @DisplayName("Should return false when expiresAt is null")
    void shouldReturnFalseWhenExpiresAtIsNull() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .expiresAt(null)
                .build();

        assertFalse(suggestion.isExpired());
    }

    @Test
    @DisplayName("Should return true for high confidence")
    void shouldReturnTrueForHighConfidence() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .confidenceScore(0.85)
                .build();

        assertTrue(suggestion.isHighConfidence());
    }

    @Test
    @DisplayName("Should return false for not high confidence")
    void shouldReturnFalseForNotHighConfidence() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .confidenceScore(0.75)
                .build();

        assertFalse(suggestion.isHighConfidence());
    }

    @Test
    @DisplayName("Should return false when confidence is null")
    void shouldReturnFalseWhenConfidenceIsNull() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .confidenceScore(null)
                .build();

        assertFalse(suggestion.isHighConfidence());
    }

    @Test
    @DisplayName("Should return true for medium confidence")
    void shouldReturnTrueForMediumConfidence() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .confidenceScore(0.65)
                .build();

        assertTrue(suggestion.isMediumConfidence());
    }

    @Test
    @DisplayName("Should return false for not medium confidence")
    void shouldReturnFalseForNotMediumConfidence() {
        TagSuggestion suggestion = TagSuggestion.builder()
                .id(UUID.randomUUID())
                .confidenceScore(0.85)
                .build();

        assertFalse(suggestion.isMediumConfidence());
    }
}
