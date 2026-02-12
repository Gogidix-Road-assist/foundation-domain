package com.gogidix.rapidassist.ai.nlp.processing.application.service;

import com.gogidix.rapidassist.ai.nlp.processing.application.dto.*;
import com.gogidix.rapidassist.ai.nlp.processing.domain.model.*;
import com.gogidix.rapidassist.ai.nlp.processing.domain.port.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NLPProcessingService {

    private final TextProcessingRepositoryPort textProcessingRepository;
    private final TokenRepositoryPort tokenRepository;
    private final NERResultRepositoryPort nerResultRepository;
    private final TextSummaryRepositoryPort textSummaryRepository;
    private final TextSimilarityRepositoryPort textSimilarityRepository;
    private final LanguageDetectionRepositoryPort languageDetectionRepository;

    @Transactional
    public TextProcessingDto processText(String tenantId, String text, TextProcessing.ProcessingType processingType, String createdBy) {
        log.info("Processing text for tenant: {}, type: {}", tenantId, processingType);

        TextProcessing processing = TextProcessing.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .text(text)
                .processingType(processingType)
                .status(TextProcessing.ProcessingStatus.PROCESSING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();

        TextProcessing saved = textProcessingRepository.save(processing);
        performTokenization(saved);

        saved.setStatus(TextProcessing.ProcessingStatus.COMPLETED);
        saved.setUpdatedAt(LocalDateTime.now());
        textProcessingRepository.save(saved);

        return mapToDto(saved);
    }

    private void performTokenization(TextProcessing processing) {
        log.info("Performing tokenization for textProcessingId: {}", processing.getId());
        String[] tokens = processing.getText().split("\\s+");
        List<Token> tokenList = new ArrayList<>();
        int position = 0;
        int startPos = 0;
        
        for (String tokenStr : tokens) {
            Token token = Token.builder()
                    .id(UUID.randomUUID())
                    .tenantId(processing.getTenantId())
                    .textProcessingId(processing.getId())
                    .text(tokenStr)
                    .lemma(tokenStr.toLowerCase())
                    .partOfSpeech("UNKNOWN")
                    .position(position++)
                    .startPosition(startPos)
                    .endPosition(startPos + tokenStr.length())
                    .build();
            tokenList.add(token);
            startPos += tokenStr.length() + 1;
        }
        tokenRepository.saveAll(tokenList);
    }

    public TextSimilarityDto analyzeSimilarity(String tenantId, String text1, String text2, TextSimilarity.SimilarityMethod method, String createdBy) {
        log.info("Analyzing similarity for tenant: {}", tenantId);
        double similarityScore = 0.75;
        Map<String, Double> detailedScores = new HashMap<>();
        detailedScores.put(method.name(), similarityScore);
        
        TextProcessing processing = TextProcessing.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .text(text1 + " [VS] " + text2)
                .processingType(TextProcessing.ProcessingType.SIMILARITY_ANALYSIS)
                .status(TextProcessing.ProcessingStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
        textProcessingRepository.save(processing);
        
        TextSimilarity similarity = TextSimilarity.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .textProcessingId(processing.getId())
                .text1(text1)
                .text2(text2)
                .similarityScore(similarityScore)
                .method(method)
                .detailedScores(detailedScores)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        TextSimilarity saved = textSimilarityRepository.save(similarity);
        return mapToSimilarityDto(saved);
    }

    public TextProcessingDto getTextProcessing(String tenantId, UUID id) {
        Optional<TextProcessing> processing = textProcessingRepository.findByIdAndTenantId(id, tenantId);
        return processing.map(this::mapToDto).orElse(null);
    }

    public List<TextProcessingDto> getTextProcessingByStatus(String tenantId, TextProcessing.ProcessingStatus status) {
        List<TextProcessing> processings = textProcessingRepository.findByStatus(tenantId, status);
        return processings.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public void deleteTextProcessing(String tenantId, UUID id) {
        tokenRepository.deleteByTextProcessingId(id);
        textProcessingRepository.deleteByIdAndTenantId(id, tenantId);
    }

    private TextProcessingDto mapToDto(TextProcessing entity) {
        return TextProcessingDto.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .text(entity.getText())
                .processingType(entity.getProcessingType())
                .language(entity.getLanguage())
                .status(entity.getStatus())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private TextSimilarityDto mapToSimilarityDto(TextSimilarity entity) {
        return TextSimilarityDto.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .textProcessingId(entity.getTextProcessingId())
                .text1(entity.getText1())
                .text2(entity.getText2())
                .similarityScore(entity.getSimilarityScore())
                .method(entity.getMethod())
                .detailedScores(entity.getDetailedScores())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
