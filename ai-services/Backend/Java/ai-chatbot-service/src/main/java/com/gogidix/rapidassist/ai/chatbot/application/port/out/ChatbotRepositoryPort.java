package com.gogidix.rapidassist.ai.chatbot.application.port.out;

import com.gogidix.rapidassist.ai.chatbot.domain.model.Conversation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatbotRepositoryPort {
    Conversation save(Conversation entity);
    Optional<Conversation> findById(UUID id);
    Optional<Conversation> findByIdAndTenantId(UUID id, String tenantId);
    List<Conversation> findByTenantId(String tenantId);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
}
