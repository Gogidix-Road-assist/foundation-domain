
package com.gogidix.rapidassist.ai.chatbot.application.service;



import com.gogidix.rapidassist.ai.chatbot.application.command.*;

import com.gogidix.rapidassist.ai.chatbot.application.dto.ChatbotSessionDto;

import com.gogidix.rapidassist.ai.chatbot.application.mapper.ChatbotSessionMapper;

import com.gogidix.rapidassist.ai.chatbot.application.query.*;

import com.gogidix.rapidassist.ai.chatbot.domain.event.*;

import com.gogidix.rapidassist.ai.chatbot.domain.exception.ChatbotSessionNotFoundException;

import com.gogidix.rapidassist.ai.chatbot.domain.exception.InvalidChatbotSessionException;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatMessage;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatbotContext;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ConversationFlow;

import com.gogidix.rapidassist.ai.chatbot.domain.model.SessionStatus;

import com.gogidix.rapidassist.ai.chatbot.domain.policy.ChatbotSessionPolicy;

import com.gogidix.rapidassist.ai.chatbot.domain.repository.ChatbotContextRepositoryPort;

import com.gogidix.rapidassist.ai.chatbot.domain.repository.ChatbotMessageRepositoryPort;

import com.gogidix.rapidassist.ai.chatbot.domain.repository.ChatbotSessionRepositoryPort;

import com.gogidix.rapidassist.ai.chatbot.infrastructure.messaging.kafka.event.EventPublisher;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;



import java.time.LocalDateTime;

import java.util.List;

import java.util.UUID;



/**

 * Application Service for Chatbot Session operations.

 * Implements business logic and orchestrates domain operations.

 */




@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotSessionApplicationService {

    private final ChatbotSessionRepositoryPort sessionRepository;
    private final ChatbotMessageRepositoryPort messageRepository;
    private final ChatbotContextRepositoryPort contextRepository;
    private final ChatbotSessionMapper mapper;
    private final EventPublisher eventPublisher;

    /**
     * Create a new chatbot session.
     */
    public ChatbotSessionDto createSession(CreateChatbotSessionCommand command) {
        log.info("Creating chatbot session for tenant: {}, user: {}, channel: {}", 
                 command.getTenantId(), command.getUserId(), command.getChannel());

        // Create session using domain factory
        var session = com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession.initialize(
                command.getTenantId(),
                command.getUserId(),
                command.getChannel()
        );

        // Set metadata if provided
        if (command.getMetadata() != null) {
            command.getMetadata().forEach(session::updateMetadata);
        }

        // Save session
        var savedSession = sessionRepository.save(command.getTenantId(), session);

        // Publish event
        eventPublisher.publish(ChatbotSessionCreatedEvent.from(savedSession));

        log.info("Chatbot session created: {}", savedSession.getId());
        return mapper.toDto(savedSession);
    }

    /**
     * Get session by ID.
     */
    public ChatbotSessionDto getSession(GetChatbotSessionQuery query) {
        log.info("Getting chatbot session: {} for tenant: {}", query.getSessionId(), query.getTenantId());

        var session = sessionRepository.findById(query.getTenantId(), query.getSessionId())
                .orElseThrow(() -> new ChatbotSessionNotFoundException(
                        query.getSessionId(), query.getTenantId()));

        return mapper.toDto(session);
    }

    /**
     * Get session by session ID string.
     */
    public ChatbotSessionDto getSessionBySessionId(GetChatbotSessionBySessionIdQuery query) {
        log.info("Getting chatbot session by session ID: {} for tenant: {}", 
                 query.getSessionId(), query.getTenantId());

        var session = sessionRepository.findBySessionId(query.getTenantId(), query.getSessionId())
                .orElseThrow(() -> new ChatbotSessionNotFoundException(
                        query.getSessionId(), query.getTenantId()));

        return mapper.toDto(session);
    }

    /**
     * Get all sessions for a user.
     */
    public List<ChatbotSessionDto> getUserSessions(GetUserSessionsQuery query) {
        log.info("Getting sessions for user: {} in tenant: {}", query.getUserId(), query.getTenantId());

        List<com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession> sessions;

        if (query.getStatus() != null) {
            sessions = sessionRepository.findByStatus(
                    query.getTenantId(), 
                    query.getStatus()
            ).stream()
                    .filter(s -> s.getUserId().equals(query.getUserId()))
                    .toList();
        } else {
            sessions = sessionRepository.findByUserId(query.getTenantId(), query.getUserId());
        }

        return sessions.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Send a message to a session.
     */
    public ChatbotSessionDto sendMessage(SendMessageCommand command) {
        log.info("Sending message to session: {} for tenant: {}", 
                 command.getSessionId(), command.getTenantId());

        // Get session
        var session = sessionRepository.findById(command.getTenantId(), command.getSessionId())
                .orElseThrow(() -> new ChatbotSessionNotFoundException(
                        command.getSessionId(), command.getTenantId()));

        // Validate session can accept messages
        if (!ChatbotSessionPolicy.canAcceptMessage(session)) {
            throw new InvalidChatbotSessionException(
                    "Session cannot accept messages in status: " + session.getStatus());
        }

        // Validate message content
        if (!ChatbotSessionPolicy.isValidMessageContent(command.getContent())) {
            throw new IllegalArgumentException("Invalid message content");
        }

        // Create message
        var message = ChatMessage.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .chatbotSessionId(session.getId())
                .direction(command.getDirection())
                .content(command.getContent())
                .messageType(command.getMessageType())
                .metadata(command.getMetadata())
                .timestamp(LocalDateTime.now())
                .build();

        // Add message to session
        session.addMessage(message);

        // Activate session if needed
        if (session.isInitialized()) {
            session.activate();
            eventPublisher.publish(ChatbotSessionActivatedEvent.create(
                    session.getId(), session.getTenantId(), session.getUserId()));
        }

        // Save session with message
        var savedSession = sessionRepository.save(command.getTenantId(), session);

        // Publish message received event
        eventPublisher.publish(ChatbotMessageReceivedEvent.from(
                savedSession.getId(), savedSession.getTenantId(), savedSession.getUserId(), message));

        log.info("Message sent to session: {}", savedSession.getId());
        return mapper.toDto(savedSession);
    }

    /**
     * Update session status.
     */
    public ChatbotSessionDto updateStatus(UpdateSessionStatusCommand command) {
        log.info("Updating session status: {} to {} for tenant: {}", 
                 command.getSessionId(), command.getStatus(), command.getTenantId());

        // Get session
        var session = sessionRepository.findById(command.getTenantId(), command.getSessionId())
                .orElseThrow(() -> new ChatbotSessionNotFoundException(
                        command.getSessionId(), command.getTenantId()));

        // Validate transition
        if (!ChatbotSessionPolicy.isValidTransition(session.getStatus(), command.getStatus())) {
            throw new InvalidChatbotSessionException(
                    "Invalid status transition from " + session.getStatus() + 
                    " to " + command.getStatus());
        }

        // Update status
        switch (command.getStatus()) {
            case ACTIVE -> session.activate();
            case COMPLETED -> {
                session.complete();
                eventPublisher.publish(ChatbotSessionCompletedEvent.create(
                        session.getId(), session.getTenantId(), session.getUserId(),
                        command.getReason() != null ? command.getReason() : "Manually completed",
                        session.getMessageCount(),
                        session.getSessionDurationSeconds()
                ));
            }
            case TERMINATED -> {
                session.terminate();
                eventPublisher.publish(ChatbotSessionTerminatedEvent.create(
                        session.getId(), session.getTenantId(), session.getUserId(),
                        command.getReason() != null ? command.getReason() : "Manually terminated"
                ));
            }
            default -> throw new IllegalArgumentException(
                    "Status update to " + command.getStatus() + " not supported via this method");
        }

        // Save session
        var savedSession = sessionRepository.save(command.getTenantId(), session);

        log.info("Session status updated: {}", savedSession.getStatus());
        return mapper.toDto(savedSession);
    }

    /**
     * Add context to a session.
     */
    public ChatbotSessionDto addContext(AddContextCommand command) {
        log.info("Adding context to session: {} for tenant: {}", 
                 command.getSessionId(), command.getTenantId());

        // Get session
        var session = sessionRepository.findById(command.getTenantId(), command.getSessionId())
                .orElseThrow(() -> new ChatbotSessionNotFoundException(
                        command.getSessionId(), command.getTenantId()));

        // Create context
        var context = ChatbotContext.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .chatbotSessionId(session.getId())
                .contextKey(command.getContextKey())
                .contextValue(command.getContextValue())
                .contextType(command.getContextType())
                .additionalContext(command.getAdditionalContext())
                .ttl(command.getTtl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Add context to session
        session.addContext(context);

        // Save context
        contextRepository.save(command.getTenantId(), context);

        // Save session
        var savedSession = sessionRepository.save(command.getTenantId(), session);

        log.info("Context added to session: {}", savedSession.getId());
        return mapper.toDto(savedSession);
    }

    /**
     * Delete a session.
     */
    public void deleteSession(String tenantId, UUID sessionId) {
        log.info("Deleting session: {} for tenant: {}", sessionId, tenantId);

        // Check if session exists
        if (!sessionRepository.exists(tenantId, sessionId)) {
            throw new ChatbotSessionNotFoundException(sessionId, tenantId);
        }

        // Delete related entities
        messageRepository.deleteBySessionId(tenantId, sessionId);
        contextRepository.deleteBySessionId(tenantId, sessionId);

        // Delete session
        sessionRepository.delete(tenantId, sessionId);

        log.info("Session deleted: {}", sessionId);
    }

    /**
     * Get active sessions for a tenant.
     */
    public List<ChatbotSessionDto> getActiveSessions(String tenantId) {
        log.info("Getting active sessions for tenant: {}", tenantId);

        var sessions = sessionRepository.findActiveSessions(tenantId);
        return sessions.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Process idle sessions (auto-complete or terminate).
     */
    public int processIdleSessions(String tenantId, int idleThresholdMinutes) {
        log.info("Processing idle sessions for tenant: {} with threshold: {} minutes", 
                 tenantId, idleThresholdMinutes);

        var idleSessions = sessionRepository.findIdleSessions(tenantId, idleThresholdMinutes);
        int processedCount = 0;

        for (var session : idleSessions) {
            if (ChatbotSessionPolicy.shouldAutoComplete(session)) {
                session.complete();
                eventPublisher.publish(ChatbotSessionCompletedEvent.create(
                        session.getId(), session.getTenantId(), session.getUserId(),
                        "Auto-completed due to inactivity",
                        session.getMessageCount(),
                        session.getSessionDurationSeconds()
                ));
            } else {
                session.pause();
            }
            sessionRepository.save(tenantId, session);
            processedCount++;
        }

        log.info("Processed {} idle sessions", processedCount);
        return processedCount;
    }
}
