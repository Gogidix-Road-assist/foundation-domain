package com.gogidix.rapidassist.ai.chatbot.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a chatbot session is not found.
 */
public class ChatbotSessionNotFoundException extends RuntimeException {

    public ChatbotSessionNotFoundException(String message) {
        super(message);
    }

    public ChatbotSessionNotFoundException(UUID sessionId, String tenantId) {
        super(String.format("Chatbot session not found: sessionId=%s, tenantId=%s", sessionId, tenantId));
    }

    public ChatbotSessionNotFoundException(String sessionId, String tenantId) {
        super(String.format("Chatbot session not found: sessionId=%s, tenantId=%s", sessionId, tenantId));
    }
}
