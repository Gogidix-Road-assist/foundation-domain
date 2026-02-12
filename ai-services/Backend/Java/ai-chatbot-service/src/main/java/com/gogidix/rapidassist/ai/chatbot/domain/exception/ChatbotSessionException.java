package com.gogidix.rapidassist.ai.chatbot.domain.exception;

/**
 * Base exception for chatbot session related errors.
 */
public class ChatbotSessionException extends RuntimeException {

    public ChatbotSessionException(String message) {
        super(message);
    }

    public ChatbotSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
