package com.gogidix.rapidassist.ai.summarization.domain.repository;

import com.gogidix.rapidassist.ai.summarization.domain.model.KeySentence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KeySentenceRepositoryPort {
    KeySentence save(String tenantId, KeySentence keySentence);
    List<KeySentence> saveAll(String tenantId, List<KeySentence> keySentences);
    Optional<KeySentence> findById(String tenantId, UUID id);
    List<KeySentence> findBySummaryId(String tenantId, UUID summaryId);
    void deleteBySummaryId(String tenantId, UUID summaryId);
}
