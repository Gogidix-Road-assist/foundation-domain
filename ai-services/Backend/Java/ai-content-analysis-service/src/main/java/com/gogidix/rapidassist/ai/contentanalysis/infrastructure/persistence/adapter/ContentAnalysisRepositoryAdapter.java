package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.adapter;

import com.gogidix.rapidassist.ai.contentanalysis.application.port.out.ContentAnalysisRepositoryPort;
import com.gogidix.rapidassist.ai.contentanalysis.domain.model.ContentAnalysis;
import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.ContentAnalysisEntity;
import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.mapper.ContentAnalysisPersistenceMapper;
import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.repository.ContentAnalysisMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB adapter for ContentAnalysis repository
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentAnalysisRepositoryAdapter implements ContentAnalysisRepositoryPort {

    private final ContentAnalysisMongoRepository mongoRepository;
    private final ContentAnalysisPersistenceMapper mapper;

    @Override
    public ContentAnalysis save(ContentAnalysis analysis) {
        log.debug("Saving content analysis: {}", analysis.getId());
        ContentAnalysisEntity entity = mapper.toEntity(analysis);
        ContentAnalysisEntity savedEntity = mongoRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ContentAnalysis> findById(java.util.UUID id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ContentAnalysis> findByIdAndTenantId(java.util.UUID id, String tenantId) {
        return mongoRepository.findByIdAndTenantId(id, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public List<ContentAnalysis> findByTenantId(String tenantId) {
        return mongoRepository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ContentAnalysis> findByContentId(String contentId) {
        return mongoRepository.findByContentId(contentId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ContentAnalysis> findByContentIdAndTenantId(String contentId, String tenantId) {
        return mongoRepository.findByContentIdAndTenantId(contentId, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public List<ContentAnalysis> findByStatus(ContentAnalysis.AnalysisStatus status) {
        return mongoRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ContentAnalysis> findByStatusAndTenantId(ContentAnalysis.AnalysisStatus status, String tenantId) {
        return mongoRepository.findByStatusAndTenantId(status.name(), tenantId).stream()
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
    public boolean existsByIdAndTenantId(java.util.UUID id, String tenantId) {
        return mongoRepository.existsByIdAndTenantId(id, tenantId);
    }
}
