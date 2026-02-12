package com.gogidix.rapidassist.ai.chatbot.domain.exception;

/**
 * Exception thrown when a chatbot session operation is invalid.
 */
public class InvalidChatbotSessionException extends RuntimeException {

    public InvalidChatbotSessionException(String message) {
        super(message);
    }

    public InvalidChatbotSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
