package com.gogidix.rapidassist.ai.chatbot.domain.exception;

/**
 * Exception thrown when chatbot request data is invalid.
 */
public class InvalidChatbotRequestException extends ChatbotException {

    public InvalidChatbotRequestException(String message) {
        super("INVALID_REQUEST", message);
    }

    public InvalidChatbotRequestException(String field, String reason) {
        super("INVALID_REQUEST", String.format("Invalid request: %s - %s", field, reason));
    }
}
