package com.gogidix.rapidassist.ai.translation.application.service;

import com.gogidix.rapidassist.ai.translation.application.command.*;
import com.gogidix.rapidassist.ai.translation.application.dto.TranslationRequestDto;
import com.gogidix.rapidassist.ai.translation.application.dto.TranslationSessionDto;
import com.gogidix.rapidassist.ai.translation.application.mapper.TranslationSessionMapper;
import com.gogidix.rapidassist.ai.translation.application.query.GetTranslationSessionQuery;
import com.gogidix.rapidassist.ai.translation.application.query.GetUserSessionsQuery;
import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
import com.gogidix.rapidassist.ai.translation.domain.model.TranslationRequest;
import com.gogidix.rapidassist.ai.translation.domain.model.TranslationStatus;
import com.gogidix.rapidassist.ai.translation.domain.model.TranslationQuality;
import com.gogidix.rapidassist.ai.translation.domain.repository.TranslationSessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Application Service for TranslationSession.
 * Handles use cases for translation session management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationSessionApplicationService {

    private final TranslationSessionRepositoryPort sessionRepository;
    private final TranslationSessionMapper mapper;

    /**
     * Create a new translation session.
     */
    public TranslationSessionDto createSession(CreateTranslationSessionCommand command) {
        log.info("Creating translation session for user: {} in tenant: {}",
                  command.getUserId(), command.getTenantId());

        TranslationSession session = TranslationSession.initialize(
                command.getTenantId(),
                command.getUserId(),
                command.getSessionType(),
                command.getSourceLanguage(),
                command.getTargetLanguage(),
                command.getChannel()
        );

        if (command.getMetadata() != null) {
            command.getMetadata().forEach(session::updateMetadata);
        }

        session.activate();

        TranslationSession savedSession = sessionRepository.save(command.getTenantId(), session);

        return mapper.toDto(savedSession);
    }

    /**
     * Get a translation session by ID.
     */
    public TranslationSessionDto getSession(GetTranslationSessionQuery query) {
        log.info("Getting session: {} for tenant: {}", query.getSessionId(), query.getTenantId());

        TranslationSession session = sessionRepository.findById(
                query.getTenantId(),
                query.getSessionId()
        ).orElseThrow(() -> new RuntimeException(
                "Session not found: " + query.getSessionId()));

        return mapper.toDto(session);
    }

    /**
     * Get all sessions for a user.
     */
    public List<TranslationSessionDto> getUserSessions(GetUserSessionsQuery query) {
        log.info("Getting sessions for user: {} in tenant: {}",
                  query.getUserId(), query.getTenantId());

        List<TranslationSession> sessions;

        if (query.getStatus() != null) {
            sessions = sessionRepository.findByUserId(query.getTenantId(), query.getUserId())
                    .stream()
                    .filter(s -> s.getStatus().name().equals(query.getStatus()))
                    .toList();
        } else {
            sessions = sessionRepository.findByUserId(query.getTenantId(), query.getUserId());
        }

        return sessions.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Translate text in a session.
     */
    public TranslationSessionDto translateText(TranslateTextCommand command) {
        log.info("Translating text in session: {} for tenant: {}",
                  command.getSessionId(), command.getTenantId());

        TranslationSession session = sessionRepository.findById(
                command.getTenantId(),
                command.getSessionId()
        ).orElseThrow(() -> new RuntimeException(
                "Session not found: " + command.getSessionId()));

        // Create translation request
        TranslationRequest request = TranslationRequest.create(
                command.getTenantId(),
                session.getId(),
                command.getSourceText(),
                command.getSourceLanguage(),
                command.getTargetLanguage()
        );

        if (command.getMetadata() != null && request.getMetadata() != null) {
            command.getMetadata().forEach(request.getMetadata()::put);
        }

        // Simulate translation (in real implementation, call AI translation service)
        request.markInProgress();

        // Simulated translation result
        String translatedText = "[Translated] " + command.getSourceText();
        TranslationQuality quality = TranslationQuality.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .translationRequestId(request.getId())
                .score(0.9)
                .confidence(TranslationQuality.ConfidenceLevel.HIGH)
                .errorCount(0)
                .warningCount(0)
                .fluencyScore(0.95)
                .accuracyScore(0.92)
                .consistencyScore(0.88)
                .build();

        request.complete(translatedText, quality, 150L, "v1.0");

        session.addTranslationRequest(request);

        TranslationSession savedSession = sessionRepository.save(command.getTenantId(), session);

        return mapper.toDto(savedSession);
    }

    /**
     * Update session status.
     */
    public TranslationSessionDto updateStatus(UpdateSessionStatusCommand command) {
        log.info("Updating session status: {} to {} for tenant: {}",
                  command.getSessionId(), command.getStatus(), command.getTenantId());

        TranslationSession session = sessionRepository.findById(
                command.getTenantId(),
                command.getSessionId()
        ).orElseThrow(() -> new RuntimeException(
                "Session not found: " + command.getSessionId()));

        switch (command.getStatus()) {
            case ACTIVE -> session.activate();
            case PAUSED -> session.pause();
            case COMPLETED -> session.complete();
            case TERMINATED -> session.terminate();
            default -> throw new IllegalArgumentException(
                    "Invalid status transition: " + command.getStatus());
        }

        TranslationSession savedSession = sessionRepository.save(command.getTenantId(), session);

        return mapper.toDto(savedSession);
    }

    /**
     * Delete a session.
     */
    public void deleteSession(String tenantId, UUID sessionId) {
        log.info("Deleting session: {} for tenant: {}", sessionId, tenantId);

        sessionRepository.delete(tenantId, sessionId);
    }

    /**
     * Get active sessions for a tenant.
     */
    public List<TranslationSessionDto> getActiveSessions(String tenantId) {
        log.info("Getting active sessions for tenant: {}", tenantId);

        List<TranslationSession> sessions = sessionRepository.findActiveSessions(tenantId);

        return sessions.stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Process idle sessions.
     */
    public int processIdleSessions(String tenantId, int idleThresholdMinutes) {
        log.info("Processing idle sessions for tenant: {} with threshold: {} minutes",
                  tenantId, idleThresholdMinutes);

        List<TranslationSession> idleSessions = sessionRepository.findIdleSessions(
                tenantId, idleThresholdMinutes);

        int processedCount = 0;
        for (TranslationSession session : idleSessions) {
            session.terminate();
            sessionRepository.save(tenantId, session);
            processedCount++;
        }

        return processedCount;
    }
}
