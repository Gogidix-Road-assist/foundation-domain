package com.gogidix.rapidassist.ai.sentiment.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.sentiment.domain.aggregate.SentimentAnalysis;
import com.gogidix.rapidassist.ai.sentiment.domain.repository.SentimentAnalysisRepositoryPort;
import com.gogidix.rapidassist.ai.sentiment.infrastructure.persistence.entity.SentimentAnalysisEntity;
import com.gogidix.rapidassist.ai.sentiment.infrastructure.persistence.mapper.SentimentAnalysisPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of SentimentAnalysisRepositoryPort using MongoDB
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SentimentAnalysisRepositoryImpl implements SentimentAnalysisRepositoryPort {

    private final SpringDataSentimentAnalysisRepository springDataRepository;
    private final SentimentAnalysisPersistenceMapper mapper;

    @Override
    public SentimentAnalysis save(SentimentAnalysis sentimentAnalysis) {
        log.debug("Saving sentiment analysis: {}", sentimentAnalysis.getId());
        SentimentAnalysisEntity entity = mapper.toEntity(sentimentAnalysis);
        SentimentAnalysisEntity savedEntity = springDataRepository.save(entity);
        return mapper.toAggregate(savedEntity);
    }

    @Override
    public Optional<SentimentAnalysis> findById(UUID id) {
        log.debug("Finding sentiment analysis by ID: {}", id);
        return springDataRepository.findById(id)
                .map(mapper::toAggregate);
    }

    @Override
    public Optional<SentimentAnalysis> findByTenantIdAndId(String tenantId, UUID id) {
        log.debug("Finding sentiment analysis by tenant ID and ID: {}, {}", tenantId, id);
        return springDataRepository.findByTenantIdAndId(tenantId, id)
                .map(entity -> mapper.toAggregate(entity));
    }

    @Override
    public List<SentimentAnalysis> findAllByTenantId(String tenantId) {
        log.debug("Finding all sentiment analyses by tenant ID: {}", tenantId);
        return springDataRepository.findAllByTenantId(tenantId).stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<SentimentAnalysis> findByTenantIdAndUserId(String tenantId, String userId) {
        log.debug("Finding sentiment analyses by tenant ID and user ID: {}, {}", tenantId, userId);
        return springDataRepository.findByTenantIdAndUserId(tenantId, userId).stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<SentimentAnalysis> findByTenantIdAndSource(String tenantId, String sourceType, String sourceId) {
        log.debug("Finding sentiment analyses by tenant ID and source: {}, {}, {}", tenantId, sourceType, sourceId);
        return springDataRepository.findByTenantIdAndSourceTypeAndSourceId(tenantId, sourceType, sourceId).stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<SentimentAnalysis> findByTenantIdAndStatus(String tenantId, String status) {
        log.debug("Finding sentiment analyses by tenant ID and status: {}, {}", tenantId, status);
        try {
            com.gogidix.rapidassist.ai.sentiment.domain.model.AnalysisStatus analysisStatus =
                com.gogidix.rapidassist.ai.sentiment.domain.model.AnalysisStatus.valueOf(status.toUpperCase());
            return springDataRepository.findByTenantIdAndStatus(tenantId, analysisStatus).stream()
                    .map(mapper::toAggregate)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status: {}", status);
            return List.of();
        }
    }

    @Override
    public List<SentimentAnalysis> findByTenantIdAndSentimentType(String tenantId, String sentimentType) {
        log.debug("Finding sentiment analyses by tenant ID and sentiment type: {}, {}", tenantId, sentimentType);
        try {
            com.gogidix.rapidassist.ai.sentiment.domain.model.SentimentType type =
                com.gogidix.rapidassist.ai.sentiment.domain.model.SentimentType.valueOf(sentimentType.toUpperCase());
            return springDataRepository.findByTenantIdAndOverallSentiment(tenantId, type).stream()
                    .map(mapper::toAggregate)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid sentiment type: {}", sentimentType);
            return List.of();
        }
    }

    @Override
    public List<SentimentAnalysis> findByTenantIdAndDateRange(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding sentiment analyses by tenant ID and date range: {}, {}, {}", tenantId, startDate, endDate);
        return springDataRepository.findByTenantIdAndCreatedAtBetween(tenantId, startDate, endDate).stream()
                .map(mapper::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting sentiment analysis by ID: {}", id);
        springDataRepository.deleteById(id);
    }

    @Override
    public void deleteByTenantIdAndId(String tenantId, UUID id) {
        log.debug("Deleting sentiment analysis by tenant ID and ID: {}, {}", tenantId, id);
        springDataRepository.deleteByTenantIdAndId(tenantId, id);
    }

    @Override
    public boolean existsById(UUID id) {
        return springDataRepository.existsById(id);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }
}
