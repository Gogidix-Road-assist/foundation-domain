package com.gogidix.rapidassist.ai.chatbot.domain.policy;

/**
 * Business policy for chat message operations.
 */
public class ChatMessagePolicy {

    private static final int MAX_MESSAGE_LENGTH = 4000;
    private static final int MIN_MESSAGE_LENGTH = 1;
    private static final int MAX_METADATA_SIZE = 10;

    /**
     * Check if message content is valid
     */
    public static boolean isValidContent(String content) {
        if (content == null) {
            return false;
        }
        int length = content.trim().length();
        return length >= MIN_MESSAGE_LENGTH && length <= MAX_MESSAGE_LENGTH;
    }

    /**
     * Check if metadata size is within limits
     */
    public static boolean isValidMetadata(java.util.Map<String, Object> metadata) {
        return metadata == null || metadata.size() <= MAX_METADATA_SIZE;
    }

    /**
     * Sanitize message content
     */
    public static String sanitizeContent(String content) {
        if (content == null) {
            return "";
        }
        // Remove excessive whitespace
        return content.trim().replaceAll("\\s+", " ");
    }

    /**
     * Check if message contains sensitive data (basic check)
     */
    public static boolean containsSensitiveData(String content) {
        if (content == null) {
            return false;
        }
        // Basic patterns for sensitive data (can be enhanced)
        return content.matches(".*\\d{4}[-\\s]?\\d{4}[-\\s]?\\d{4}[-\\s]?\\d{4}.*") || // Credit card pattern
               content.matches(".*\\d{3}-\\d{2}-\\d{4}.*"); // SSN pattern
    }

    /**
     * Truncate content to max length
     */
    public static String truncateContent(String content, int maxLength) {
        if (content == null) {
            return "";
        }
        int actualMaxLength = Math.min(maxLength, MAX_MESSAGE_LENGTH);
        return content.length() > actualMaxLength ? 
               content.substring(0, actualMaxLength) : content;
    }
}
