package com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatMessage;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatbotContext;
import com.gogidix.rapidassist.ai.chatbot.domain.model.ConversationFlow;
import com.gogidix.rapidassist.ai.chatbot.domain.repository.ChatbotSessionRepositoryPort;
import com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.entity.ChatbotSessionEntity;
import com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.mapper.ChatbotSessionPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ChatbotSessionRepositoryPort.
 * Adapters domain repository port to Spring Data JPA infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatbotSessionRepositoryImpl implements ChatbotSessionRepositoryPort {

    private final SpringDataChatbotSessionRepository springDataRepository;
    private final SpringDataChatMessageRepository messageRepository;
    private final SpringDataChatbotContextRepository contextRepository;
    private final SpringDataConversationFlowRepository flowRepository;
    private final ChatbotSessionPersistenceMapper persistenceMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ChatbotSession save(String tenantId, ChatbotSession session) {
        log.info("Saving session: {} for tenant: {}", session.getId(), tenantId);

        // Convert domain to entity
        ChatbotSessionEntity entity = persistenceMapper.toEntity(session);

        // Save entity
        ChatbotSessionEntity savedEntity = springDataRepository.save(entity);

        // Convert back to domain with child entities
        return loadSessionWithChildren(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatbotSession> findById(String tenantId, UUID sessionId) {
        log.info("Finding session by ID: {} for tenant: {}", sessionId, tenantId);

        return springDataRepository.findByUuidAndTenantId(sessionId, tenantId)
                .map(this::loadSessionWithChildren);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatbotSession> findBySessionId(String tenantId, String sessionId) {
        log.info("Finding session by session ID: {} for tenant: {}", sessionId, tenantId);

        return springDataRepository.findBySessionIdAndTenantId(sessionId, tenantId)
                .map(this::loadSessionWithChildren);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatbotSession> findByTenantId(String tenantId) {
        log.info("Finding all sessions for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::loadSessionWithChildren)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatbotSession> findByUserId(String tenantId, String userId) {
        log.info("Finding sessions for user: {} in tenant: {}", userId, tenantId);

        return springDataRepository.findByUserIdAndTenantId(userId, tenantId).stream()
                .map(this::loadSessionWithChildren)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatbotSession> findByStatus(String tenantId, String status) {
        log.info("Finding sessions by status: {} for tenant: {}", status, tenantId);

        com.gogidix.rapidassist.ai.chatbot.domain.model.SessionStatus sessionStatus =
                com.gogidix.rapidassist.ai.chatbot.domain.model.SessionStatus.valueOf(status);

        return springDataRepository.findByStatusAndTenantId(sessionStatus, tenantId).stream()
                .map(this::loadSessionWithChildren)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatbotSession> findActiveSessions(String tenantId) {
        log.info("Finding active sessions for tenant: {}", tenantId);

        return springDataRepository.findByStatusAndTenantId(
                com.gogidix.rapidassist.ai.chatbot.domain.model.SessionStatus.ACTIVE, tenantId).stream()
                .map(this::loadSessionWithChildren)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatbotSession> findIdleSessions(String tenantId, int idleThresholdMinutes) {
        log.info("Finding idle sessions for tenant: {} with threshold: {} minutes", 
                  tenantId, idleThresholdMinutes);

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(idleThresholdMinutes);
        return springDataRepository.findIdleSessions(tenantId, threshold).stream()
                .map(this::loadSessionWithChildren)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID sessionId) {
        log.info("Deleting session: {} for tenant: {}", sessionId, tenantId);

        springDataRepository.deleteByUuidAndTenantId(sessionId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String tenantId, UUID sessionId) {
        return springDataRepository.existsByUuidAndTenantId(sessionId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    /**
     * Load session with all child entities.
     */
    private ChatbotSession loadSessionWithChildren(ChatbotSessionEntity entity) {
        // Convert to domain
        ChatbotSession session = persistenceMapper.toDomain(entity);

        // Load messages
        List<ChatMessage> messages = messageRepository
                .findBySessionIdAndTenantIdOrderBySequenceNumberAsc(entity.getUuid(), entity.getTenantId())
                .stream()
                .map(persistenceMapper::toDomain)
                .toList();
        session.setMessages(messages);

        // Load contexts
        List<ChatbotContext> contexts = contextRepository
                .findBySessionIdAndTenantId(entity.getUuid(), entity.getTenantId())
                .stream()
                .map(persistenceMapper::toDomain)
                .toList();
        session.setContexts(contexts);

        // Load flows
        List<ConversationFlow> flows = flowRepository
                .findBySessionIdAndTenantIdOrderByCreatedAtAsc(entity.getUuid(), entity.getTenantId())
                .stream()
                .map(persistenceMapper::toDomain)
                .toList();
        session.setFlows(flows);

        return session;
    }
}
