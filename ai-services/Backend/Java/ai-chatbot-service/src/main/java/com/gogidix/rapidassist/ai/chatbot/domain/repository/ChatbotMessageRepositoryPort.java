package com.gogidix.rapidassist.ai.chatbot.domain.repository;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for ChatMessage entities.
 */
public interface ChatbotMessageRepositoryPort {

    /**
     * Save a message.
     */
    ChatMessage save(String tenantId, ChatMessage message);

    /**
     * Find a message by ID and tenant.
     */
    Optional<ChatMessage> findById(String tenantId, UUID messageId);

    /**
     * Find all messages for a session.
     */
    List<ChatMessage> findBySessionId(String tenantId, UUID sessionId);

    /**
     * Find messages by direction for a session.
     */
    List<ChatMessage> findBySessionIdAndDirection(String tenantId, UUID sessionId, String direction);

    /**
     * Delete all messages for a session.
     */
    void deleteBySessionId(String tenantId, UUID sessionId);

    /**
     * Count messages for a session.
     */
    long countBySessionId(String tenantId, UUID sessionId);
}
