package com.gogidix.rapidassist.ai.chatbot.domain.model;

/**
 * Enumeration representing the intent type identified by the chatbot.
 */
public enum IntentType {

    /**
     * User is asking a general question
     */
    QUESTION,

    /**
     * User is requesting assistance
     */
    REQUEST_ASSISTANCE,

    /**
     * User is reporting an issue
     */
    REPORT_ISSUE,

    /**
     * User is requesting information
     */
    REQUEST_INFO,

    /**
     * User is providing feedback
     */
    FEEDBACK,

    /**
     * User is requesting a specific action
     */
    ACTION_REQUEST,

    /**
     * User is greeting
     */
    GREETING,

    /**
     * User is ending the conversation
     */
    FAREWELL,

    /**
     * Intent could not be determined
     */
    UNKNOWN,

    /**
     * User is out of context
     */
    OUT_OF_SCOPE
}
