package com.gogidix.rapidassist.ai.summization.domain.policy;

import com.gogidix.rapidassist.ai.summization.domain.model.DocumentType;
import com.gogidix.rapidassist.ai.summization.domain.model.SummarizationTask;
import com.gogidix.rapidassist.ai.summization.domain.model.TaskStatus;

/**
 * Business policy and validation rules for summarization tasks.
 */
public class SummarizationTaskPolicy {

    private static final int MAX_INPUT_TEXT_LENGTH = 100000;
    private static final int MIN_INPUT_TEXT_LENGTH = 10;
    private static final int MAX_TASK_IDLE_TIME_MS = 3600000; // 1 hour

    /**
     * Check if a task can be started.
     */
    public static boolean canStart(SummarizationTask task) {
        return task != null
                && task.getStatus() == TaskStatus.PENDING
                && task.hasInput();
    }

    /**
     * Check if input text is valid.
     */
    public static boolean isValidInputText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        int length = text.length();
        return length >= MIN_INPUT_TEXT_LENGTH && length <= MAX_INPUT_TEXT_LENGTH;
    }

    /**
     * Check if document type is supported.
     */
    public static boolean isSupportedDocumentType(DocumentType type) {
        return type != null && type != DocumentType.OTHER;
    }

    /**
     * Check if task should be auto-retried.
     */
    public static boolean shouldAutoRetry(SummarizationTask task) {
        return task != null
                && task.isFailed()
                && task.canRetry();
    }

    /**
     * Check if task has timed out.
     */
    public static boolean hasTimedOut(SummarizationTask task) {
        return task != null && task.isTimedOut(MAX_TASK_IDLE_TIME_MS);
    }

    /**
     * Check if task can be cancelled.
     */
    public static boolean canCancel(SummarizationTask task) {
        return task != null
                && !task.isCompleted()
                && !task.isFailed()
                && !task.isCancelled();
    }

    /**
     * Check if task priority is valid.
     */
    public static boolean isValidPriority(SummarizationTask task) {
        return task != null && task.getPriority() != null;
    }

    /**
     * Validate summarization request.
     */
    public static boolean isValidRequest(String inputText, DocumentType documentType) {
        return isValidInputText(inputText) && isSupportedDocumentType(documentType);
    }

    /**
     * Check if task is ready for processing.
     */
    public static boolean isReadyForProcessing(SummarizationTask task) {
        return task != null
                && task.isPending()
                && task.hasInput()
                && isValidPriority(task);
    }
}
