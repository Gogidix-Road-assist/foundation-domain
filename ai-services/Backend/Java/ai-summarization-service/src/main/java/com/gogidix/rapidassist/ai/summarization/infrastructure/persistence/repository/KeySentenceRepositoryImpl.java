package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summarization.domain.model.KeySentence;
import com.gogidix.rapidassist.ai.summarization.domain.repository.KeySentenceRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.KeySentenceEntity;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.mapper.KeySentencePersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class KeySentenceRepositoryImpl implements KeySentenceRepositoryPort {

    private final SpringDataKeySentenceRepository springDataRepository;
    private final KeySentencePersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public KeySentence save(String tenantId, KeySentence keySentence) {
        log.info("Saving key sentence: {} for tenant: {}", keySentence.getId(), tenantId);
        KeySentenceEntity entity = persistenceMapper.toEntity(keySentence);
        KeySentenceEntity savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public List<KeySentence> saveAll(String tenantId, List<KeySentence> keySentences) {
        log.info("Saving {} key sentences for tenant: {}", keySentences.size(), tenantId);
        List<KeySentenceEntity> entities = keySentences.stream()
                .map(persistenceMapper::toEntity)
                .toList();
        List<KeySentenceEntity> savedEntities = springDataRepository.saveAll(entities);
        return savedEntities.stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KeySentence> findById(String tenantId, UUID id) {
        log.info("Finding key sentence by ID: {} for tenant: {}", id, tenantId);
        return springDataRepository.findById(String.valueOf(id))
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KeySentence> findBySummaryId(String tenantId, UUID summaryId) {
        log.info("Finding key sentences by summaryId: {} for tenant: {}", summaryId, tenantId);
        return springDataRepository.findBySummaryIdAndTenantId(summaryId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBySummaryId(String tenantId, UUID summaryId) {
        log.info("Deleting key sentences for summary: {} in tenant: {}", summaryId, tenantId);
        springDataRepository.deleteBySummaryIdAndTenantId(summaryId, tenantId);
    }
}
