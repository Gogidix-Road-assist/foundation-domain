package com.gogidix.rapidassist.ai.tagging.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Tag domain model
 */
@DisplayName("Tag Domain Model Tests")
class TagTest {

    @Test
    @DisplayName("Should create tag with builder")
    void shouldCreateTagWithBuilder() {
        Tag tag = Tag.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .name("Urgent")
                .description("Urgent items requiring immediate attention")
                .color("#FF0000")
                .status(Tag.TagStatus.ACTIVE)
                .usageCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .version(1L)
                .build();

        assertNotNull(tag);
        assertEquals("Urgent", tag.getName());
        assertEquals("tenant-123", tag.getTenantId());
        assertEquals(Tag.TagStatus.ACTIVE, tag.getStatus());
        assertEquals(0, tag.getUsageCount());
    }

    @Test
    @DisplayName("Should increment usage count")
    void shouldIncrementUsageCount() {
        Tag tag = Tag.builder()
                .id(UUID.randomUUID())
                .usageCount(5)
                .build();

        tag.incrementUsage();

        assertEquals(6, tag.getUsageCount());
        assertNotNull(tag.getUpdatedAt());
    }

    @Test
    @DisplayName("Should decrement usage count when greater than zero")
    void shouldDecrementUsageCount() {
        Tag tag = Tag.builder()
                .id(UUID.randomUUID())
                .usageCount(5)
                .build();

        tag.decrementUsage();

        assertEquals(4, tag.getUsageCount());
    }

    @Test
    @DisplayName("Should not decrement usage count below zero")
    void shouldNotDecrementBelowZero() {
        Tag tag = Tag.builder()
                .id(UUID.randomUUID())
                .usageCount(0)
                .build();

        tag.decrementUsage();

        assertEquals(0, tag.getUsageCount());
    }

    @Test
    @DisplayName("Should activate tag")
    void shouldActivateTag() {
        Tag tag = Tag.builder()
                .id(UUID.randomUUID())
                .status(Tag.TagStatus.INACTIVE)
                .build();

        tag.activate();

        assertEquals(Tag.TagStatus.ACTIVE, tag.getStatus());
        assertNotNull(tag.getUpdatedAt());
    }

    @Test
    @DisplayName("Should deactivate tag")
    void shouldDeactivateTag() {
        Tag tag = Tag.builder()
                .id(UUID.randomUUID())
                .status(Tag.TagStatus.ACTIVE)
                .build();

        tag.deactivate();

        assertEquals(Tag.TagStatus.INACTIVE, tag.getStatus());
    }

    @Test
    @DisplayName("Should archive tag")
    void shouldArchiveTag() {
        Tag tag = Tag.builder()
                .id(UUID.randomUUID())
                .status(Tag.TagStatus.ACTIVE)
                .build();

        tag.archive();

        assertEquals(Tag.TagStatus.ARCHIVED, tag.getStatus());
    }

    @Test
    @DisplayName("Should return true when tag is active")
    void shouldReturnTrueWhenActive() {
        Tag tag = Tag.builder()
                .id(UUID.randomUUID())
                .status(Tag.TagStatus.ACTIVE)
                .build();

        assertTrue(tag.isActive());
    }

    @Test
    @DisplayName("Should return false when tag is not active")
    void shouldReturnFalseWhenNotActive() {
        Tag tag = Tag.builder()
                .id(UUID.randomUUID())
                .status(Tag.TagStatus.INACTIVE)
                .build();

        assertFalse(tag.isActive());
    }

    @Test
    @DisplayName("Should handle null usage count on increment")
    void shouldHandleNullUsageCountOnIncrement() {
        Tag tag = Tag.builder()
                .id(UUID.randomUUID())
                .usageCount(null)
                .build();

        tag.incrementUsage();

        assertEquals(1, tag.getUsageCount());
    }
}
