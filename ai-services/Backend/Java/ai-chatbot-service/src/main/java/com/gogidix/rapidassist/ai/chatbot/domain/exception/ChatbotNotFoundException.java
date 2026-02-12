package com.gogidix.rapidassist.ai.chatbot.domain.exception;

/**
 * Exception thrown when a chatbot request is not found.
 */
public class ChatbotNotFoundException extends ChatbotException {

    public ChatbotNotFoundException(String message) {
        super("CHATBOT_NOT_FOUND", message);
    }

    public ChatbotNotFoundException(String id, String tenantId) {
        super("CHATBOT_NOT_FOUND", String.format("Chatbot request not found: id=%s, tenantId=%s", id, tenantId));
    }
}
