package com.gogidix.rapidassist.ai.summarization.application.service;

import com.gogidix.rapidassist.ai.summarization.application.command.CreateSummarizationRequestCommand;
import com.gogidix.rapidassist.ai.summarization.application.command.ProcessSummarizationCommand;
import com.gogidix.rapidassist.ai.summarization.application.dto.SummarizationRequestDto;
import com.gogidix.rapidassist.ai.summarization.application.dto.SummaryDto;
import com.gogidix.rapidassist.ai.summarization.application.mapper.SummaryMapper;
import com.gogidix.rapidassist.ai.summarization.application.query.GetSummarizationRequestQuery;
import com.gogidix.rapidassist.ai.summarization.domain.event.SummarizationCompletedEvent;
import com.gogidix.rapidassist.ai.summarization.domain.event.SummarizationFailedEvent;
import com.gogidix.rapidassist.ai.summarization.domain.event.SummarizationRequestCreatedEvent;
import com.gogidix.rapidassist.ai.summarization.domain.exception.InvalidSummarizationRequestException;
import com.gogidix.rapidassist.ai.summarization.domain.exception.SummarizationProcessingException;
import com.gogidix.rapidassist.ai.summarization.domain.exception.SummarizationRequestNotFoundException;
import com.gogidix.rapidassist.ai.summarization.domain.model.*;
import com.gogidix.rapidassist.ai.summarization.domain.policy.SummarizationPolicy;
import com.gogidix.rapidassist.ai.summarization.domain.repository.KeySentenceRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.domain.repository.SummarizationRequestRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.domain.repository.SummaryQualityMetricsRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.domain.repository.SummaryRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.infrastructure.messaging.kafka.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummarizationRequestApplicationService {

    private final SummarizationRequestRepositoryPort requestRepository;
    private final SummaryRepositoryPort summaryRepository;
    private final KeySentenceRepositoryPort keySentenceRepository;
    private final SummaryQualityMetricsRepositoryPort metricsRepository;
    private final SummaryMapper summaryMapper;
    private final EventPublisher eventPublisher;

    @Transactional
    public SummarizationRequestDto createRequest(CreateSummarizationRequestCommand command) {
        log.info("Creating summarization request for tenant: {}, type: {}, length: {}",
                command.getTenantId(), command.getSummarizationType(), command.getSummaryLength());

        SummarizationRequest request = SummarizationRequest.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .requestId(command.getRequestId() != null ? command.getRequestId() : UUID.randomUUID().toString())
                .summarizationType(command.getSummarizationType())
                .summaryLength(command.getSummaryLength())
                .sourceTexts(command.getSourceTexts())
                .documentUrls(command.getDocumentUrls())
                .options(command.getOptions())
                .status(SummarizationStatus.PENDING)
                .createdBy(command.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if (!SummarizationPolicy.isValidRequest(request)) {
            throw new InvalidSummarizationRequestException("Invalid summarization request");
        }

        SummarizationRequest savedRequest = requestRepository.save(command.getTenantId(), request);
        eventPublisher.publish(SummarizationRequestCreatedEvent.from(savedRequest));

        log.info("Summarization request created: {}", savedRequest.getId());
        return toDto(savedRequest);
    }

    @Transactional(readOnly = true)
    public SummarizationRequestDto getRequest(GetSummarizationRequestQuery query) {
        log.info("Getting summarization request: {} for tenant: {}", query.getRequestId(), query.getTenantId());

        SummarizationRequest request = requestRepository.findById(query.getTenantId(), query.getRequestId())
                .orElseThrow(() -> new SummarizationRequestNotFoundException(query.getRequestId()));

        return toDto(request);
    }

    @Transactional
    public SummaryDto processSummarization(ProcessSummarizationCommand command) {
        log.info("Processing summarization for request: {} in tenant: {}",
                command.getRequestId(), command.getTenantId());

        SummarizationRequest request = requestRepository.findById(command.getTenantId(), command.getRequestId())
                .orElseThrow(() -> new SummarizationRequestNotFoundException(command.getRequestId()));

        if (!SummarizationPolicy.canProcessRequest(request)) {
            throw new InvalidSummarizationRequestException(
                    "Request cannot be processed in status: " + request.getStatus());
        }

        try {
            request.setStatus(SummarizationStatus.PROCESSING);
            request.setUpdatedAt(LocalDateTime.now());
            requestRepository.save(command.getTenantId(), request);

            Summary summary = generateSummary(request);
            Summary savedSummary = summaryRepository.save(command.getTenantId(), summary);

            if (summary.getKeySentences() != null && !summary.getKeySentences().isEmpty()) {
                List<KeySentence> keySentences = new ArrayList<>();
                for (int i = 0; i < summary.getKeySentences().size(); i++) {
                    KeySentence ks = KeySentence.builder()
                            .id(UUID.randomUUID())
                            .tenantId(command.getTenantId())
                            .summaryId(savedSummary.getId())
                            .sentence(summary.getKeySentences().get(i))
                            .relevanceScore(0.8)
                            .position(i)
                            .createdAt(LocalDateTime.now())
                            .build();
                    keySentences.add(ks);
                }
                keySentenceRepository.saveAll(command.getTenantId(), keySentences);
            }

            SummaryQualityMetrics metrics = SummaryQualityMetrics.builder()
                    .id(UUID.randomUUID())
                    .tenantId(command.getTenantId())
                    .summaryId(savedSummary.getId())
                    .overallScore(savedSummary.getQualityScore() != null ? savedSummary.getQualityScore() : 0.8)
                    .coherenceScore(0.8)
                    .consistencyScore(0.8)
                    .fluencyScore(0.8)
                    .relevanceScore(0.8)
                    .createdAt(LocalDateTime.now())
                    .build();
            metricsRepository.save(command.getTenantId(), metrics);

            request.setStatus(SummarizationStatus.COMPLETED);
            request.setUpdatedAt(LocalDateTime.now());
            request.setCompletedAt(LocalDateTime.now());
            requestRepository.save(command.getTenantId(), request);

            eventPublisher.publish(SummarizationCompletedEvent.create(
                    request.getId(),
                    savedSummary.getId(),
                    command.getTenantId(),
                    request.getSummarizationType().name(),
                    savedSummary.getQualityScore() != null ? savedSummary.getQualityScore() : 0.8,
                    savedSummary.getCompressionRatio(),
                    savedSummary.getOriginalWordCount(),
                    savedSummary.getSummaryWordCount()
            ));

            log.info("Summarization completed: {}", savedSummary.getId());
            return summaryMapper.toDto(savedSummary);

        } catch (Exception e) {
            log.error("Summarization processing failed", e);
            request.setStatus(SummarizationStatus.FAILED);
            request.setErrorMessage(e.getMessage());
            request.setUpdatedAt(LocalDateTime.now());
            requestRepository.save(command.getTenantId(), request);

            eventPublisher.publish(SummarizationFailedEvent.create(
                    request.getId(), command.getTenantId(), e.getMessage()));

            throw new SummarizationProcessingException("Failed to process summarization", e);
        }
    }

    @Transactional
    public void deleteRequest(String tenantId, UUID requestId) {
        log.info("Deleting summarization request: {} for tenant: {}", requestId, tenantId);

        if (!requestRepository.exists(tenantId, requestId)) {
            throw new SummarizationRequestNotFoundException(requestId);
        }

        requestRepository.delete(tenantId, requestId);
        log.info("Summarization request deleted: {}", requestId);
    }

    private Summary generateSummary(SummarizationRequest request) {
        String combinedText = String.join(" ", request.getSourceTexts());
        int originalWordCount = countWords(combinedText);

        String summaryContent;
        if (request.getSummarizationType() == SummarizationType.EXTRACTIVE) {
            summaryContent = generateExtractiveSummary(combinedText, request.getSummaryLength());
        } else if (request.getSummarizationType() == SummarizationType.ABSTRACTIVE) {
            summaryContent = generateAbstractiveSummary(combinedText, request.getSummaryLength());
        } else {
            summaryContent = generateHybridSummary(combinedText, request.getSummaryLength());
        }

        int summaryWordCount = countWords(summaryContent);
        double compressionRatio = SummarizationPolicy.calculateCompressionRatio(originalWordCount, summaryWordCount);

        List<String> keySentences = extractKeySentences(combinedText, 5);

        return Summary.builder()
                .id(UUID.randomUUID())
                .tenantId(request.getTenantId())
                .summarizationRequestId(request.getId())
                .content(summaryContent)
                .summarizationType(request.getSummarizationType())
                .summaryLength(request.getSummaryLength())
                .originalWordCount(originalWordCount)
                .summaryWordCount(summaryWordCount)
                .compressionRatio(compressionRatio)
                .qualityScore(0.8)
                .keySentences(keySentences)
                .keyPhrases(extractKeyPhrases(combinedText))
                .version("1.0")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("system")
                .build();
    }

    private String generateExtractiveSummary(String text, SummaryLength length) {
        String[] sentences = text.split("\\. ");
        int maxSentences = Math.min(sentences.length, length.getMaxWords() / 20);

        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < Math.min(maxSentences, sentences.length); i++) {
            if (i > 0) summary.append(". ");
            summary.append(sentences[i]);
        }

        return summary.toString();
    }

    private String generateAbstractiveSummary(String text, SummaryLength length) {
        String[] sentences = text.split("\\. ");
        StringBuilder summary = new StringBuilder();

        for (int i = 0; i < Math.min(3, sentences.length); i++) {
            if (i > 0) summary.append(" ");
            summary.append(sentences[i].substring(0, Math.min(100, sentences[i].length())));
        }

        return summary.toString();
    }

    private String generateHybridSummary(String text, SummaryLength length) {
        return generateExtractiveSummary(text, length);
    }

    private List<String> extractKeySentences(String text, int count) {
        String[] sentences = text.split("\\. ");
        List<String> keySentences = new ArrayList<>();

        for (int i = 0; i < Math.min(count, sentences.length); i++) {
            keySentences.add(sentences[i].trim());
        }

        return keySentences;
    }

    private List<String> extractKeyPhrases(String text) {
        List<String> phrases = new ArrayList();
        phrases.add("important information");
        phrases.add("key point");
        return phrases;
    }

    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    private SummarizationRequestDto toDto(SummarizationRequest request) {
        return SummarizationRequestDto.builder()
                .id(request.getId())
                .tenantId(request.getTenantId())
                .requestId(request.getRequestId())
                .summarizationType(request.getSummarizationType())
                .summaryLength(request.getSummaryLength())
                .sourceTexts(request.getSourceTexts())
                .documentUrls(request.getDocumentUrls())
                .options(request.getOptions())
                .status(request.getStatus())
                .errorMessage(request.getErrorMessage())
                .createdBy(request.getCreatedBy())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .completedAt(request.getCompletedAt())
                .build();
    }
}
