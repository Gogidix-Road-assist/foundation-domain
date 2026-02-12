package com.gogidix.rapidassist.ai.chatbot.domain.model;

/**
 * Enumeration representing the status of a chatbot session.
 */
public enum SessionStatus {

    /**
     * Session has been created but not yet active
     */
    INITIALIZED,

    /**
     * Session is actively processing messages
     */
    ACTIVE,

    /**
     * Session is temporarily paused
     */
    PAUSED,

    /**
     * Session has been completed/closed
     */
    COMPLETED,

    /**
     * Session was terminated due to error or timeout
     */
    TERMINATED
}
