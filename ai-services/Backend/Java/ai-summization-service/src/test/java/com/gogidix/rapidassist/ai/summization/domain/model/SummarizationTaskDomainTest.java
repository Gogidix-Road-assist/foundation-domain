package com.gogidix.rapidassist.ai.summization.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SummarizationTask domain model
 * Tests business logic, state transitions, and domain rules
 */
@DisplayName("SummarizationTask Domain Model Tests")
class SummarizationTaskDomainTest {

    private SummarizationTask task;
    private UUID testId;
    private String tenantId;
    private String userId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        tenantId = "tenant-123";
        userId = "user-456";

        task = SummarizationTask.builder()
                .id(testId)
                .tenantId(tenantId)
                .userId(userId)
                .taskId(UUID.randomUUID().toString())
                .status(TaskStatus.PENDING)
                .documentType(DocumentType.TEXT)
                .summaryStyle(SummaryStyle.BRIEF)
                .sourceLanguage("en")
                .targetLanguage("en")
                .priority(Priority.NORMAL)
                .inputText("This is a long text that needs to be summarized.")
                .retryCount(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now())
                .summaries(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should initialize summarization task correctly")
    void testInitialize() {
        SummarizationTask newTask = SummarizationTask.initialize(
                tenantId,
                userId,
                DocumentType.PDF,
                Priority.HIGH
        );

        assertNotNull(newTask.getId());
        assertEquals(tenantId, newTask.getTenantId());
        assertEquals(userId, newTask.getUserId());
        assertEquals(TaskStatus.PENDING, newTask.getStatus());
        assertEquals(DocumentType.PDF, newTask.getDocumentType());
        assertEquals(Priority.HIGH, newTask.getPriority());
        assertEquals(0, newTask.getRetryCount());
        assertEquals(3, newTask.getMaxRetries());
        assertNotNull(newTask.getTaskId());
        assertNotNull(newTask.getCreatedAt());
        assertNotNull(newTask.getSummaries());
        assertTrue(newTask.getSummaries().isEmpty());
    }

    @Test
    @DisplayName("Should initialize with normal priority when null")
    void testInitializeWithNullPriority() {
        SummarizationTask newTask = SummarizationTask.initialize(
                tenantId,
                userId,
                DocumentType.TEXT,
                null
        );

        assertEquals(Priority.NORMAL, newTask.getPriority());
    }

    @Test
    @DisplayName("Should start task successfully")
    void testStart() {
        task.start();

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertNotNull(task.getStartedAt());
    }

    @Test
    @DisplayName("Should throw exception when starting non-pending task")
    void testStartNonPending() {
        task.setStatus(TaskStatus.IN_PROGRESS);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> task.start()
        );

        assertTrue(exception.getMessage().contains("Cannot start task in status"));
    }

    @Test
    @DisplayName("Should complete task successfully")
    void testComplete() {
        task.setStatus(TaskStatus.IN_PROGRESS);
        LocalDateTime startedAt = LocalDateTime.now().minusSeconds(10);
        task.setStartedAt(startedAt);

        task.complete();

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertNotNull(task.getCompletedAt());
        assertNotNull(task.getProcessingTimeMs());
        assertTrue(task.getProcessingTimeMs() > 0);
    }

    @Test
    @DisplayName("Should throw exception when completing non-in-progress task")
    void testCompleteNonInProgress() {
        task.setStatus(TaskStatus.PENDING);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> task.complete()
        );

        assertTrue(exception.getMessage().contains("Cannot complete task in status"));
    }

    @Test
    @DisplayName("Should fail task successfully")
    void testFail() {
        task.setStatus(TaskStatus.IN_PROGRESS);
        LocalDateTime startedAt = LocalDateTime.now().minusSeconds(5);
        task.setStartedAt(startedAt);

        String errorMessage = "Processing failed";
        task.fail(errorMessage);

        assertEquals(TaskStatus.FAILED, task.getStatus());
        assertEquals(errorMessage, task.getErrorMessage());
        assertNotNull(task.getCompletedAt());
        assertNotNull(task.getProcessingTimeMs());
    }

    @Test
    @DisplayName("Should throw exception when failing non-in-progress task")
    void testFailNonInProgress() {
        task.setStatus(TaskStatus.PENDING);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> task.fail("Error")
        );

        assertTrue(exception.getMessage().contains("Cannot fail task in status"));
    }

    @Test
    @DisplayName("Should cancel task successfully")
    void testCancel() {
        task.setStatus(TaskStatus.IN_PROGRESS);

        task.cancel();

        assertEquals(TaskStatus.CANCELLED, task.getStatus());
        assertNotNull(task.getCompletedAt());
    }

    @Test
    @DisplayName("Should throw exception when cancelling completed task")
    void testCancelCompleted() {
        task.setStatus(TaskStatus.COMPLETED);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> task.cancel()
        );

        assertTrue(exception.getMessage().contains("Cannot cancel task in status"));
    }

    @Test
    @DisplayName("Should throw exception when cancelling failed task")
    void testCancelFailed() {
        task.setStatus(TaskStatus.FAILED);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> task.cancel()
        );

        assertTrue(exception.getMessage().contains("Cannot cancel task in status"));
    }

    @Test
    @DisplayName("Should check if can retry correctly")
    void testCanRetry() {
        task.setStatus(TaskStatus.FAILED);
        task.setRetryCount(1);
        task.setMaxRetries(3);

        assertTrue(task.canRetry());
    }

    @Test
    @DisplayName("Should return false when cannot retry - max retries reached")
    void testCannotRetryMaxRetries() {
        task.setStatus(TaskStatus.FAILED);
        task.setRetryCount(3);
        task.setMaxRetries(3);

        assertFalse(task.canRetry());
    }

    @Test
    @DisplayName("Should return false when cannot retry - not failed")
    void testCannotRetryNotFailed() {
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setRetryCount(1);
        task.setMaxRetries(3);

        assertFalse(task.canRetry());
    }

    @Test
    @DisplayName("Should return false when cannot retry - null values")
    void testCannotRetryNullValues() {
        task.setStatus(TaskStatus.FAILED);
        task.setRetryCount(null);
        task.setMaxRetries(null);

        assertFalse(task.canRetry());
    }

    @Test
    @DisplayName("Should increment retry count successfully")
    void testIncrementRetry() {
        task.setStatus(TaskStatus.FAILED);
        task.setRetryCount(1);
        task.setErrorMessage("Error");

        task.incrementRetry();

        assertEquals(2, task.getRetryCount());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertNull(task.getErrorMessage());
    }

    @Test
    @DisplayName("Should initialize retry count to 0 when null")
    void testIncrementRetryWithNull() {
        task.setRetryCount(null);

        task.incrementRetry();

        assertEquals(1, task.getRetryCount());
    }

    @Test
    @DisplayName("Should check if is pending correctly")
    void testIsPending() {
        task.setStatus(TaskStatus.PENDING);
        assertTrue(task.isPending());

        task.setStatus(TaskStatus.IN_PROGRESS);
        assertFalse(task.isPending());
    }

    @Test
    @DisplayName("Should check if is in progress correctly")
    void testIsInProgress() {
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertTrue(task.isInProgress());

        task.setStatus(TaskStatus.PENDING);
        assertFalse(task.isInProgress());
    }

    @Test
    @DisplayName("Should check if is completed correctly")
    void testIsCompleted() {
        task.setStatus(TaskStatus.COMPLETED);
        assertTrue(task.isCompleted());

        task.setStatus(TaskStatus.PENDING);
        assertFalse(task.isCompleted());
    }

    @Test
    @DisplayName("Should check if is failed correctly")
    void testIsFailed() {
        task.setStatus(TaskStatus.FAILED);
        assertTrue(task.isFailed());

        task.setStatus(TaskStatus.PENDING);
        assertFalse(task.isFailed());
    }

    @Test
    @DisplayName("Should check if is cancelled correctly")
    void testIsCancelled() {
        task.setStatus(TaskStatus.CANCELLED);
        assertTrue(task.isCancelled());

        task.setStatus(TaskStatus.PENDING);
        assertFalse(task.isCancelled());
    }

    @Test
    @DisplayName("Should check if has summaries correctly")
    void testHasSummaries() {
        assertFalse(task.hasSummaries());

        DocumentSummary summary = new DocumentSummary();
        task.setSummaries(List.of(summary));

        assertTrue(task.hasSummaries());
    }

    @Test
    @DisplayName("Should return false for has summaries with null list")
    void testHasSummariesNull() {
        task.setSummaries(null);
        assertFalse(task.hasSummaries());
    }

    @Test
    @DisplayName("Should get summary count correctly")
    void testGetSummaryCount() {
        assertEquals(0, task.getSummaryCount());

        DocumentSummary s1 = new DocumentSummary();
        DocumentSummary s2 = new DocumentSummary();
        task.setSummaries(List.of(s1, s2));

        assertEquals(2, task.getSummaryCount());
    }

    @Test
    @DisplayName("Should return 0 for summary count with null list")
    void testGetSummaryCountNull() {
        task.setSummaries(null);
        assertEquals(0, task.getSummaryCount());
    }

    @Test
    @DisplayName("Should add summary successfully")
    void testAddSummary() {
        DocumentSummary summary = new DocumentSummary();
        summary.setId(UUID.randomUUID());

        task.addSummary(summary);

        assertEquals(1, task.getSummaries().size());
        assertEquals(task.getId(), summary.getSummarizationTaskId());
        assertEquals(task.getTenantId(), summary.getTenantId());
    }

    @Test
    @DisplayName("Should initialize summaries list when adding to null")
    void testAddSummaryWithNullList() {
        task.setSummaries(null);

        DocumentSummary summary = new DocumentSummary();
        summary.setId(UUID.randomUUID());

        task.addSummary(summary);

        assertNotNull(task.getSummaries());
        assertEquals(1, task.getSummaries().size());
    }

    @Test
    @DisplayName("Should check if is high priority correctly")
    void testIsHighPriority() {
        task.setPriority(Priority.HIGH);
        assertTrue(task.isHighPriority());

        task.setPriority(Priority.URGENT);
        assertTrue(task.isHighPriority());

        task.setPriority(Priority.NORMAL);
        assertFalse(task.isHighPriority());

        task.setPriority(Priority.LOW);
        assertFalse(task.isHighPriority());
    }

    @Test
    @DisplayName("Should check if is urgent correctly")
    void testIsUrgent() {
        task.setPriority(Priority.URGENT);
        assertTrue(task.isUrgent());

        task.setPriority(Priority.HIGH);
        assertFalse(task.isUrgent());
    }

    @Test
    @DisplayName("Should check if has input correctly")
    void testHasInput() {
        // First clear the input from setUp
        task.setInputText(null);
        task.setInputUrl(null);
        assertFalse(task.hasInput());

        task.setInputText("Some text");
        assertTrue(task.hasInput());

        task.setInputText(null);
        task.setInputUrl("http://example.com/doc.pdf");
        assertTrue(task.hasInput());

        task.setInputText("");
        task.setInputUrl("   ");
        assertFalse(task.hasInput());
    }

    @Test
    @DisplayName("Should get task duration correctly")
    void testGetTaskDurationMs() {
        task.setStartedAt(LocalDateTime.now().minusSeconds(10));
        task.setCompletedAt(LocalDateTime.now());

        long duration = task.getTaskDurationMs();

        assertTrue(duration >= 9000 && duration <= 11000); // Allow tolerance
    }

    @Test
    @DisplayName("Should return 0 duration when dates are null")
    void testGetTaskDurationNullDates() {
        task.setStartedAt(null);
        task.setCompletedAt(null);

        assertEquals(0, task.getTaskDurationMs());
    }

    @Test
    @DisplayName("Should return 0 duration when completed at is null")
    void testGetTaskDurationNullCompleted() {
        task.setStartedAt(LocalDateTime.now().minusSeconds(10));
        task.setCompletedAt(null);

        assertEquals(0, task.getTaskDurationMs());
    }

    @Test
    @DisplayName("Should check if timed out correctly")
    void testIsTimedOut() {
        task.setStartedAt(LocalDateTime.now().minusSeconds(10));
        task.setCompletedAt(null);

        assertTrue(task.isTimedOut(5000L));
        assertFalse(task.isTimedOut(15000L));
    }

    @Test
    @DisplayName("Should return false for timeout when not started")
    void testIsTimedOutNotStarted() {
        task.setStartedAt(null);

        assertFalse(task.isTimedOut(5000L));
    }

    @Test
    @DisplayName("Should return false for timeout when completed within time")
    void testIsTimedOutCompletedInTime() {
        task.setStartedAt(LocalDateTime.now().minusSeconds(5));
        task.setCompletedAt(LocalDateTime.now());

        assertFalse(task.isTimedOut(10000L));
    }

    @Test
    @DisplayName("Should handle metadata correctly")
    void testMetadata() {
        Map<String, Object> metadata = Map.of(
                "key1", "value1",
                "key2", 123
        );

        task.setMetadata(metadata);

        assertNotNull(task.getMetadata());
        assertEquals(2, task.getMetadata().size());
        assertEquals("value1", task.getMetadata().get("key1"));
        assertEquals(123, task.getMetadata().get("key2"));
    }

    @Test
    @DisplayName("Should handle full workflow")
    void testFullWorkflow() {
        // Initialize
        SummarizationTask newTask = SummarizationTask.initialize(
                tenantId, userId, DocumentType.TEXT, Priority.NORMAL
        );
        assertEquals(TaskStatus.PENDING, newTask.getStatus());

        // Start
        newTask.start();
        assertEquals(TaskStatus.IN_PROGRESS, newTask.getStatus());

        // Complete
        newTask.complete();
        assertEquals(TaskStatus.COMPLETED, newTask.getStatus());
        assertNotNull(newTask.getProcessingTimeMs());
    }

    @Test
    @DisplayName("Should handle retry workflow")
    void testRetryWorkflow() {
        // Initialize
        SummarizationTask newTask = SummarizationTask.initialize(
                tenantId, userId, DocumentType.TEXT, Priority.NORMAL
        );

        // Start and fail
        newTask.start();
        newTask.fail("Error occurred");

        assertEquals(TaskStatus.FAILED, newTask.getStatus());
        assertTrue(newTask.canRetry());

        // Increment retry
        newTask.incrementRetry();
        assertEquals(TaskStatus.PENDING, newTask.getStatus());
        assertEquals(1, newTask.getRetryCount());
        assertNull(newTask.getErrorMessage());

        // Retry successfully
        newTask.start();
        newTask.complete();
        assertEquals(TaskStatus.COMPLETED, newTask.getStatus());
    }

    @Test
    @DisplayName("Should handle adding multiple summaries")
    void testMultipleSummaries() {
        DocumentSummary s1 = new DocumentSummary();
        s1.setId(UUID.randomUUID());

        DocumentSummary s2 = new DocumentSummary();
        s2.setId(UUID.randomUUID());

        task.addSummary(s1);
        task.addSummary(s2);

        assertEquals(2, task.getSummaries().size());
        assertTrue(task.hasSummaries());
        assertEquals(2, task.getSummaryCount());
    }

    @Test
    @DisplayName("Should handle builder correctly")
    void testBuilder() {
        SummarizationTask builtTask = SummarizationTask.builder()
                .id(testId)
                .tenantId(tenantId)
                .userId(userId)
                .status(TaskStatus.COMPLETED)
                .documentType(DocumentType.PDF)
                .priority(Priority.HIGH)
                .inputText("Test input")
                .summaries(new ArrayList<>())
                .build();

        assertEquals(testId, builtTask.getId());
        assertEquals(tenantId, builtTask.getTenantId());
        assertEquals(userId, builtTask.getUserId());
        assertEquals(TaskStatus.COMPLETED, builtTask.getStatus());
        assertEquals(DocumentType.PDF, builtTask.getDocumentType());
        assertEquals(Priority.HIGH, builtTask.getPriority());
        assertEquals("Test input", builtTask.getInputText());
    }
}
