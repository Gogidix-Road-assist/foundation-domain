package com.gogidix.rapidassist.ai.chatbot.domain.exception;

/**
 * Exception thrown when Chatbot data is invalid.
 */
public class InvalidChatbotDataException extends ChatbotException {

    public InvalidChatbotDataException(String message) {
        super("INVALID_DATA", message);
    }

    public InvalidChatbotDataException(String message, String tenantId) {
        super("INVALID_DATA", message, tenantId);
    }

    public InvalidChatbotDataException(String message, Throwable cause) {
        super("INVALID_DATA", message, cause);
    }
}
