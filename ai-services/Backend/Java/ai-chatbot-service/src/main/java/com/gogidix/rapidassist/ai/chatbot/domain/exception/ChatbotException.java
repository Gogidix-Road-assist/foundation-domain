package com.gogidix.rapidassist.ai.chatbot.domain.exception;

/**
 * Base exception for Chatbot domain.
 */
public class ChatbotException extends RuntimeException {

    private final String errorCode;

    public ChatbotException(String message) {
        super(message);
        this.errorCode = "CHATBOT_ERROR";
    }

    public ChatbotException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ChatbotException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "CHATBOT_ERROR";
    }

    public ChatbotException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ChatbotException(String errorCode, String message, String tenantId) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
