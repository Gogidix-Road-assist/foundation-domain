package com.gogidix.rapidassist.ai.chatbot.domain.model;

/**
 * Enumeration representing the direction of a message in a chatbot session.
 * INBOUND: Message sent from user to bot
 * OUTBOUND: Message sent from bot to user
 */
public enum MessageDirection {

    /**
     * Message received from the user (inbound to the system)
     */
    INBOUND,

    /**
     * Message sent by the chatbot to the user (outbound from the system)
     */
    OUTBOUND
}
