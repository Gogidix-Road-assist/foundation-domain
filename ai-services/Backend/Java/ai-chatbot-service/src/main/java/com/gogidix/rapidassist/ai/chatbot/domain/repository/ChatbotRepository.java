package com.gogidix.rapidassist.ai.chatbot.domain.repository;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatbotRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatbotRepository {
    ChatbotRequest save(ChatbotRequest entity);
    Optional<ChatbotRequest> findById(String id);
    Optional<ChatbotRequest> findByIdAndTenantId(String id, String tenantId);
    List<ChatbotRequest> findByTenantId(String tenantId);
    void deleteById(String id);
    void deleteByIdAndTenantId(String id, String tenantId);
    List<ChatbotRequest> findByTenantIdAndStatus(String tenantId, String status);
}
