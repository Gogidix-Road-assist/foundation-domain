package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.adapter;

import com.gogidix.rapidassist.ai.contentanalysis.application.port.out.AnalysisRequestRepositoryPort;
import com.gogidix.rapidassist.ai.contentanalysis.domain.model.AnalysisRequest;
import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.AnalysisRequestEntity;
import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.mapper.AnalysisRequestPersistenceMapper;
import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.repository.AnalysisRequestMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB adapter for AnalysisRequest repository
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AnalysisRequestRepositoryAdapter implements AnalysisRequestRepositoryPort {

    private final AnalysisRequestMongoRepository mongoRepository;
    private final AnalysisRequestPersistenceMapper mapper;

    @Override
    public AnalysisRequest save(AnalysisRequest request) {
        log.debug("Saving analysis request: {}", request.getId());
        AnalysisRequestEntity entity = mapper.toEntity(request);
        AnalysisRequestEntity savedEntity = mongoRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AnalysisRequest> findById(java.util.UUID id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AnalysisRequest> findByIdAndTenantId(java.util.UUID id, String tenantId) {
        return mongoRepository.findByIdAndTenantId(id, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AnalysisRequest> findByRequestId(String requestId) {
        return mongoRepository.findByRequestId(requestId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AnalysisRequest> findByRequestIdAndTenantId(String requestId, String tenantId) {
        return mongoRepository.findByRequestIdAndTenantId(requestId, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public List<AnalysisRequest> findByContentId(String contentId) {
        return mongoRepository.findByContentId(contentId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AnalysisRequest> findByContentIdAndTenantId(String contentId, String tenantId) {
        return mongoRepository.findByContentIdAndTenantId(contentId, tenantId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AnalysisRequest> findByStatus(AnalysisRequest.RequestStatus status) {
        return mongoRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AnalysisRequest> findByStatusAndTenantId(AnalysisRequest.RequestStatus status, String tenantId) {
        return mongoRepository.findByStatusAndTenantId(status.name(), tenantId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AnalysisRequest> findByStatusOrderByPriorityDescCreatedAtAsc(AnalysisRequest.RequestStatus status) {
        return mongoRepository.findByStatusOrderByPriorityDescCreatedAtAsc(status.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AnalysisRequest> findHighPriorityPendingRequests(Integer minPriority) {
        return mongoRepository.findByStatusAndPriorityGreaterThanEqual(
                AnalysisRequest.RequestStatus.PENDING.name(), minPriority).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(java.util.UUID id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public void deleteByIdAndTenantId(java.util.UUID id, String tenantId) {
        mongoRepository.deleteByIdAndTenantId(id, tenantId);
    }

    @Override
    public void deleteOldCompletedRequests(Integer daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        // Implementation would require custom query, simplified here
        log.debug("Deleting old completed requests older than: {}", cutoffDate);
    }
}
