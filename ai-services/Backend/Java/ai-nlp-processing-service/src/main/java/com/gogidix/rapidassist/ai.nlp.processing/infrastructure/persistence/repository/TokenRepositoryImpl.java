package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.Token;
import com.gogidix.rapidassist.ai.nlp.processing.domain.port.out.TokenRepositoryPort;
import com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity.TokenEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepositoryPort {
    private final SpringDataTokenRepository springDataRepository;

    @Override
    public Token save(Token entity) {
        TokenEntity entityToSave = TokenEntity.builder()
                .uuid(entity.getId() != null ? entity.getId() : UUID.randomUUID())
                .tenantId(entity.getTenantId())
                .textProcessingId(entity.getTextProcessingId())
                .text(entity.getText())
                .lemma(entity.getLemma())
                .partOfSpeech(entity.getPartOfSpeech())
                .position(entity.getPosition())
                .startPosition(entity.getStartPosition())
                .endPosition(entity.getEndPosition())
                .morphology(entity.getMorphology())
                .dependency(entity.getDependency())
                .headToken(entity.getHeadToken())
                .build();
        TokenEntity saved = springDataRepository.save(entityToSave);
        return mapToDomain(saved);
    }

    @Override
    public List<Token> saveAll(List<Token> entities) {
        List<TokenEntity> entityList = entities.stream().map(entity -> TokenEntity.builder()
                .uuid(entity.getId() != null ? entity.getId() : UUID.randomUUID())
                .tenantId(entity.getTenantId())
                .textProcessingId(entity.getTextProcessingId())
                .text(entity.getText())
                .lemma(entity.getLemma())
                .partOfSpeech(entity.getPartOfSpeech())
                .position(entity.getPosition())
                .startPosition(entity.getStartPosition())
                .endPosition(entity.getEndPosition())
                .morphology(entity.getMorphology())
                .dependency(entity.getDependency())
                .headToken(entity.getHeadToken())
                .build()).collect(Collectors.toList());
        return springDataRepository.saveAll(entityList).stream()
                .map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Token> findById(UUID id) {
        return springDataRepository.findById(id.toString()).map(this::mapToDomain);
    }

    @Override
    public List<Token> findByTextProcessingId(UUID textProcessingId) {
        return springDataRepository.findByTextProcessingId(textProcessingId).stream()
                .map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public List<Token> findByTextProcessingIdOrderByPositionAsc(UUID textProcessingId) {
        return springDataRepository.findByTextProcessingIdOrderByPositionAsc(textProcessingId).stream()
                .map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public List<Token> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteByTextProcessingId(UUID textProcessingId) {
        springDataRepository.deleteByTextProcessingId(textProcessingId);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id.toString());
    }

    private Token mapToDomain(TokenEntity entity) {
        return Token.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .textProcessingId(entity.getTextProcessingId())
                .text(entity.getText())
                .lemma(entity.getLemma())
                .partOfSpeech(entity.getPartOfSpeech())
                .position(entity.getPosition())
                .startPosition(entity.getStartPosition())
                .endPosition(entity.getEndPosition())
                .morphology(entity.getMorphology())
                .dependency(entity.getDependency())
                .headToken(entity.getHeadToken())
                .build();
    }
}
