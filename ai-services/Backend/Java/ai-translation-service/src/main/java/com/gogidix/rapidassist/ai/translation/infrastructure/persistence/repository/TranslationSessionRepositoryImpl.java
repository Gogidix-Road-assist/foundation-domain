package com.gogidix.rapidassist.ai.translation.infrastructure.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
import com.gogidix.rapidassist.ai.translation.domain.model.TranslationRequest;
import com.gogidix.rapidassist.ai.translation.domain.repository.TranslationSessionRepositoryPort;
import com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity.TranslationSessionEntity;
import com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity.TranslationRequestEntity;
import com.gogidix.rapidassist.ai.translation.infrastructure.persistence.mapper.TranslationSessionPersistenceMapper;
import com.gogidix.rapidassist.ai.translation.infrastructure.persistence.mapper.TranslationRequestPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of TranslationSessionRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class TranslationSessionRepositoryImpl implements TranslationSessionRepositoryPort {

    private final SpringDataTranslationSessionRepository springDataRepository;
    private final SpringDataTranslationRequestRepository requestRepository;
    private final TranslationSessionPersistenceMapper sessionMapper;
    private final TranslationRequestPersistenceMapper requestMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public TranslationSession save(String tenantId, TranslationSession session) {
        log.info("Saving session: {} for tenant: {}", session.getId(), tenantId);

        TranslationSessionEntity entity = sessionMapper.toEntity(session);
        TranslationSessionEntity savedEntity = springDataRepository.save(entity);

        return loadSessionWithRequests(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TranslationSession> findById(String tenantId, UUID sessionId) {
        log.info("Finding session by ID: {} for tenant: {}", sessionId, tenantId);

        return springDataRepository.findByUuidAndTenantId(sessionId, tenantId)
                .map(this::loadSessionWithRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TranslationSession> findBySessionId(String tenantId, String sessionId) {
        log.info("Finding session by session ID: {} for tenant: {}", sessionId, tenantId);

        return springDataRepository.findBySessionIdAndTenantId(sessionId, tenantId)
                .map(this::loadSessionWithRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TranslationSession> findByTenantId(String tenantId) {
        log.info("Finding all sessions for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::loadSessionWithRequests)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TranslationSession> findByUserId(String tenantId, String userId) {
        log.info("Finding sessions for user: {} in tenant: {}", userId, tenantId);

        return springDataRepository.findByUserIdAndTenantId(userId, tenantId).stream()
                .map(this::loadSessionWithRequests)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TranslationSession> findByStatus(String tenantId, String status) {
        log.info("Finding sessions by status: {} for tenant: {}", status, tenantId);

        var sessionStatus = TranslationSession.SessionStatus.valueOf(status);

        return springDataRepository.findByStatusAndTenantId(sessionStatus, tenantId).stream()
                .map(this::loadSessionWithRequests)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TranslationSession> findActiveSessions(String tenantId) {
        log.info("Finding active sessions for tenant: {}", tenantId);

        return springDataRepository.findByStatusAndTenantId(
                TranslationSession.SessionStatus.ACTIVE, tenantId).stream()
                .map(this::loadSessionWithRequests)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TranslationSession> findIdleSessions(String tenantId, int idleThresholdMinutes) {
        log.info("Finding idle sessions for tenant: {} with threshold: {} minutes",
                  tenantId, idleThresholdMinutes);

        var threshold = java.time.LocalDateTime.now().minusMinutes(idleThresholdMinutes);
        return springDataRepository.findIdleSessions(tenantId, threshold).stream()
                .map(this::loadSessionWithRequests)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TranslationSession> findBySessionType(String tenantId, String sessionType) {
        log.info("Finding sessions by type: {} for tenant: {}", sessionType, tenantId);

        var type = TranslationSession.SessionType.valueOf(sessionType);

        return springDataRepository.findBySessionTypeAndTenantId(type, tenantId).stream()
                .map(this::loadSessionWithRequests)
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
     * Load session with all translation requests.
     */
    private TranslationSession loadSessionWithRequests(TranslationSessionEntity entity) {
        TranslationSession session = sessionMapper.toDomain(entity);

        List<TranslationRequest> requests = requestRepository
                .findByTranslationSessionIdAndTenantId(entity.getUuid(), entity.getTenantId())
                .stream()
                .map(requestMapper::toDomain)
                .toList();

        session.setTranslationRequests(requests);

        return session;
    }
}
