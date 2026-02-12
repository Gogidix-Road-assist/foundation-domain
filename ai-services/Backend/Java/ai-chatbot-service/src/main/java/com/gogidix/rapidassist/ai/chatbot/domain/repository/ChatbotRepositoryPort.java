package com.gogidix.rapidassist.ai.chatbot.domain.repository;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatbotRequest;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ChannelType;
import com.gogidix.rapidassist.ai.chatbot.domain.model.RequestStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ChatbotRequest aggregate.
 * Following Hexagonal Architecture principles.
 */
public interface ChatbotRepositoryPort {

    ChatbotRequest save(ChatbotRequest request);

    Optional<ChatbotRequest> findById(UUID id);

    Optional<ChatbotRequest> findByTenantIdAndId(String tenantId, UUID id);

    List<ChatbotRequest> findByTenantId(String tenantId);

    List<ChatbotRequest> findByTenantIdAndStatus(String tenantId, RequestStatus status);

    List<ChatbotRequest> findByTenantIdAndSessionId(String tenantId, String sessionId);

    List<ChatbotRequest> findByTenantIdAndChannelType(String tenantId, ChannelType channelType);

    List<ChatbotRequest> findPendingRequests(String tenantId);

    void deleteById(UUID id);

    boolean existsByTenantIdAndSessionId(String tenantId, String sessionId);
}
