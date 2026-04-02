package com.gogidix.rapidassist.ai.tagging.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ContentTag domain model
 */
@DisplayName("ContentTag Domain Model Tests")
class ContentTagTest {

    @Test
    @DisplayName("Should create content tag with builder")
    void shouldCreateContentTagWithBuilder() {
        UUID tagId = UUID.randomUUID();
        ContentTag contentTag = ContentTag.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .contentId("content-456")
                .contentType("article")
                .tagId(tagId)
                .taggingSource(ContentTag.TaggingSource.AUTOMATIC_AI)
                .confidenceScore(0.95)
                .manuallyVerified(false)
                .taggedBy("ai-system")
                .createdAt(LocalDateTime.now())
                .version(1L)
                .build();

        assertNotNull(contentTag);
        assertEquals("content-456", contentTag.getContentId());
        assertEquals("article", contentTag.getContentType());
        assertEquals(tagId, contentTag.getTagId());
        assertEquals(ContentTag.TaggingSource.AUTOMATIC_AI, contentTag.getTaggingSource());
        assertEquals(0.95, contentTag.getConfidenceScore());
    }

    @Test
    @DisplayName("Should return true for automatic tagging source")
    void shouldReturnTrueForAutomaticSource() {
        ContentTag contentTag = ContentTag.builder()
                .id(UUID.randomUUID())
                .taggingSource(ContentTag.TaggingSource.AUTOMATIC_AI)
                .build();

        assertTrue(contentTag.isAutomatic());
    }

    @Test
    @DisplayName("Should return true for automatic rule tagging source")
    void shouldReturnTrueForAutomaticRuleSource() {
        ContentTag contentTag = ContentTag.builder()
                .id(UUID.randomUUID())
                .taggingSource(ContentTag.TaggingSource.AUTOMATIC_RULE)
                .build();

        assertTrue(contentTag.isAutomatic());
    }

    @Test
    @DisplayName("Should return false for manual tagging source")
    void shouldReturnFalseForManualSource() {
        ContentTag contentTag = ContentTag.builder()
                .id(UUID.randomUUID())
                .taggingSource(ContentTag.TaggingSource.MANUAL)
                .build();

        assertFalse(contentTag.isAutomatic());
    }

    @Test
    @DisplayName("Should return true for manual tagging")
    void shouldReturnTrueForManualTagging() {
        ContentTag contentTag = ContentTag.builder()
                .id(UUID.randomUUID())
                .taggingSource(ContentTag.TaggingSource.MANUAL)
                .build();

        assertTrue(contentTag.isManual());
    }

    @Test
    @DisplayName("Should return false for automatic tagging when checking isManual")
    void shouldReturnFalseForAutomaticWhenCheckingIsManual() {
        ContentTag contentTag = ContentTag.builder()
                .id(UUID.randomUUID())
                .taggingSource(ContentTag.TaggingSource.AUTOMATIC_AI)
                .build();

        assertFalse(contentTag.isManual());
    }

    @Test
    @DisplayName("Should return true when verified")
    void shouldReturnTrueWhenVerified() {
        ContentTag contentTag = ContentTag.builder()
                .id(UUID.randomUUID())
                .manuallyVerified(true)
                .build();

        assertTrue(contentTag.isVerified());
    }

    @Test
    @DisplayName("Should return false when not verified")
    void shouldReturnFalseWhenNotVerified() {
        ContentTag contentTag = ContentTag.builder()
                .id(UUID.randomUUID())
                .manuallyVerified(false)
                .build();

        assertFalse(contentTag.isVerified());
    }

    @Test
    @DisplayName("Should return false when manually verified is null")
    void shouldReturnFalseWhenManuallyVerifiedIsNull() {
        ContentTag contentTag = ContentTag.builder()
                .id(UUID.randomUUID())
                .manuallyVerified(null)
                .build();

        assertFalse(contentTag.isVerified());
    }

    @Test
    @DisplayName("Should verify tagging")
    void shouldVerifyTagging() {
        ContentTag contentTag = ContentTag.builder()
                .id(UUID.randomUUID())
                .manuallyVerified(false)
                .build();

        contentTag.verify();

        assertTrue(contentTag.getManuallyVerified());
    }

    @Test
    @DisplayName("Should unverify tagging")
    void shouldUnverifyTagging() {
        ContentTag contentTag = ContentTag.builder()
                .id(UUID.randomUUID())
                .manuallyVerified(true)
                .build();

        contentTag.unverify();

        assertFalse(contentTag.getManuallyVerified());
    }
}
