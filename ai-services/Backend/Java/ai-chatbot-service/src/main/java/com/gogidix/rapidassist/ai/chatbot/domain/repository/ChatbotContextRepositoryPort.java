package com.gogidix.rapidassist.ai.chatbot.domain.repository;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatbotContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for ChatbotContext entities.
 */
public interface ChatbotContextRepositoryPort {

    /**
     * Save context.
     */
    ChatbotContext save(String tenantId, ChatbotContext context);

    /**
     * Find context by ID and tenant.
     */
    Optional<ChatbotContext> findById(String tenantId, UUID contextId);

    /**
     * Find all contexts for a session.
     */
    List<ChatbotContext> findBySessionId(String tenantId, UUID sessionId);

    /**
     * Find context by key and session.
     */
    Optional<ChatbotContext> findBySessionIdAndKey(String tenantId, UUID sessionId, String contextKey);

    /**
     * Delete all contexts for a session.
     */
    void deleteBySessionId(String tenantId, UUID sessionId);

    /**
     * Delete expired contexts.
     */
    int deleteExpiredContexts(String tenantId);
}
