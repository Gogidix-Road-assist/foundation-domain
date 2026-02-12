package com.gogidix.rapidassist.ai.moderation.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ModerationQueue domain model
 */
class ModerationQueueTest {

    @Test
    void testCreateModerationQueue() {
        ModerationQueue queue = ModerationQueue.builder()
            .id("queue1")
            .tenantId("tenant1")
            .contentId("content1")
            .contentType("comment")
            .content("Test content")
            .userId("user1")
            .userName("Test User")
            .status(ModerationQueue.QueueStatus.PENDING)
            .priority(50)
            .submittedAt(LocalDateTime.now())
            .build();

        assertNotNull(queue);
        assertEquals("queue1", queue.getId());
        assertEquals("content1", queue.getContentId());
        assertEquals(ModerationQueue.QueueStatus.PENDING, queue.getStatus());
        assertEquals(50, queue.getPriority());
    }

    @Test
    void testAssignTo() {
        ModerationQueue queue = ModerationQueue.builder()
            .status(ModerationQueue.QueueStatus.PENDING)
            .build();

        queue.assignTo("reviewer1");

        assertEquals(ModerationQueue.QueueStatus.ASSIGNED, queue.getStatus());
        assertEquals("reviewer1", queue.getAssignedTo());
        assertNotNull(queue.getAssignedAt());
    }

    @Test
    void testStartReview() {
        ModerationQueue queue = ModerationQueue.builder()
            .status(ModerationQueue.QueueStatus.ASSIGNED)
            .build();

        queue.startReview();

        assertEquals(ModerationQueue.QueueStatus.IN_REVIEW, queue.getStatus());
    }

    @Test
    void testCompleteReview() {
        ModerationQueue queue = ModerationQueue.builder()
            .status(ModerationQueue.QueueStatus.IN_REVIEW)
            .build();

        queue.completeReview("reviewer1", ModerationQueue.QueueAction.APPROVE, "Looks good");

        assertEquals(ModerationQueue.QueueStatus.COMPLETED, queue.getStatus());
        assertEquals("reviewer1", queue.getReviewedBy());
        assertEquals(ModerationQueue.QueueAction.APPROVE, queue.getAction());
        assertEquals("Looks good", queue.getReviewNotes());
        assertNotNull(queue.getReviewedAt());
    }

    @Test
    void testCancel() {
        ModerationQueue queue = ModerationQueue.builder()
            .status(ModerationQueue.QueueStatus.PENDING)
            .build();

        queue.cancel();

        assertEquals(ModerationQueue.QueueStatus.CANCELLED, queue.getStatus());
    }

    @Test
    void testIsPending() {
        ModerationQueue pending = ModerationQueue.builder()
            .status(ModerationQueue.QueueStatus.PENDING)
            .build();

        ModerationQueue assigned = ModerationQueue.builder()
            .status(ModerationQueue.QueueStatus.ASSIGNED)
            .build();

        assertTrue(pending.isPending());
        assertFalse(assigned.isPending());
    }

    @Test
    void testIsAssigned() {
        ModerationQueue assigned = ModerationQueue.builder()
            .status(ModerationQueue.QueueStatus.ASSIGNED)
            .build();

        ModerationQueue inReview = ModerationQueue.builder()
            .status(ModerationQueue.QueueStatus.IN_REVIEW)
            .build();

        ModerationQueue pending = ModerationQueue.builder()
            .status(ModerationQueue.QueueStatus.PENDING)
            .build();

        assertTrue(assigned.isAssigned());
        assertTrue(inReview.isAssigned());
        assertFalse(pending.isAssigned());
    }
}
