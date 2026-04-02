package com.gogidix.rapidassist.ai.contentanalysis.application.service;

import com.gogidix.rapidassist.ai.contentanalysis.application.command.AnalyzeContentCommand;
import com.gogidix.rapidassist.ai.contentanalysis.application.command.BulkAnalyzeContentCommand;
import com.gogidix.rapidassist.ai.contentanalysis.application.command.ExtractTopicsCommand;
import com.gogidix.rapidassist.ai.contentanalysis.application.dto.*;
import com.gogidix.rapidassist.ai.contentanalysis.application.mapper.ContentAnalysisMapper;
import com.gogidix.rapidassist.ai.contentanalysis.application.port.out.*;
import com.gogidix.rapidassist.ai.contentanalysis.application.query.*;
import com.gogidix.rapidassist.ai.contentanalysis.domain.aggregate.ContentAnalysisAggregate;
import com.gogidix.rapidassist.ai.contentanalysis.domain.model.*;
import com.gogidix.rapidassist.ai.contentanalysis.domain.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application Service for Content Analysis operations.
 * Implements the use cases for content analysis, topic extraction, and reporting.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentAnalysisApplicationService {

    private final ContentAnalysisRepositoryPort analysisRepository;
    private final ContentTopicRepositoryPort topicRepository;
    private final AnalysisRequestRepositoryPort requestRepository;
    private final ContentAnalysisMapper mapper;

    /**
     * Analyze content use case
     */
    @Transactional
    public ContentAnalysisDto analyzeContent(AnalyzeContentCommand command) {
        log.info("Analyzing content for tenant: {}, contentId: {}", command.getTenantId(), command.getContentId());
        TenantContext.setTenantId(command.getTenantId());

        // Initialize aggregate
        ContentAnalysisAggregate aggregate = ContentAnalysisAggregate.initialize(
                command.getTenantId(),
                command.getContentId(),
                command.getContentType(),
                command.getContentTitle(),
                command.getContentBody(),
                command.getContentLanguage()
        );

        // Calculate content statistics
        aggregate.calculateContentStatistics();

        // Perform analysis (in real implementation, this would call AI services)
        performContentAnalysis(aggregate);

        // Save analysis
        ContentAnalysis savedAnalysis = analysisRepository.save(aggregate.getContentAnalysis());
        aggregate.getContentAnalysis().setId(savedAnalysis.getId());

        // Save topics if extracted
        if (!aggregate.getTopics().isEmpty()) {
            List<ContentTopic> savedTopics = topicRepository.saveAll(aggregate.getTopics());
            aggregate.setTopics(savedTopics);
        }

        log.info("Content analysis completed successfully for contentId: {}", command.getContentId());
        return mapper.toDto(savedAnalysis);
    }

    /**
     * Bulk analyze content use case
     */
    @Transactional
    public List<ContentAnalysisDto> bulkAnalyzeContent(BulkAnalyzeContentCommand command) {
        log.info("Bulk analyzing {} content items for tenant: {}", command.getContentItems().size(), command.getTenantId());
        TenantContext.setTenantId(command.getTenantId());

        List<ContentAnalysisDto> results = new ArrayList<>();

        for (BulkAnalyzeContentCommand.ContentItem item : command.getContentItems()) {
            try {
                AnalyzeContentCommand analyzeCommand = AnalyzeContentCommand.builder()
                        .tenantId(command.getTenantId())
                        .contentId(item.getContentId())
                        .contentType(item.getContentType())
                        .contentTitle(item.getContentTitle())
                        .contentBody(item.getContentBody())
                        .contentLanguage(item.getContentLanguage())
                        .requestedBy(command.getRequestedBy())
                        .analysisType(command.getAnalysisType())
                        .analysisOptions(command.getAnalysisOptions())
                        .callbackUrl(item.getCallbackUrl())
                        .build();

                ContentAnalysisDto result = analyzeContent(analyzeCommand);
                results.add(result);
            } catch (Exception e) {
                log.error("Error analyzing content item: {}", item.getContentId(), e);
            }
        }

        log.info("Bulk analysis completed. Successful: {}/{}", results.size(), command.getContentItems().size());
        return results;
    }

    /**
     * Get content analysis by ID use case
     */
    @Transactional(readOnly = true)
    public ContentAnalysisDto getAnalysis(GetContentAnalysisQuery query) {
        log.debug("Getting analysis by ID: {} for tenant: {}", query.getAnalysisId(), query.getTenantId());
        TenantContext.setTenantId(query.getTenantId());

        ContentAnalysis analysis = analysisRepository.findByIdAndTenantId(query.getAnalysisId(), query.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Content analysis not found"));

        ContentAnalysisDto dto = mapper.toDto(analysis);

        // Load topics if requested
        if (query.getIncludeTopics() != null && query.getIncludeTopics()) {
            List<ContentTopic> topics = topicRepository.findByContentIdAndTenantId(
                    analysis.getContentId(), query.getTenantId());
            dto.setTopics(topics.stream().map(mapper::toDto).collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * Get analysis by content ID use case
     */
    @Transactional(readOnly = true)
    public ContentAnalysisDto getAnalysisByContentId(GetAnalysisByContentIdQuery query) {
        log.debug("Getting analysis by contentId: {} for tenant: {}", query.getContentId(), query.getTenantId());
        TenantContext.setTenantId(query.getTenantId());

        ContentAnalysis analysis = analysisRepository.findByContentIdAndTenantId(
                query.getContentId(), query.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Content analysis not found"));

        ContentAnalysisDto dto = mapper.toDto(analysis);

        // Load topics if requested
        if (query.getIncludeTopics() != null && query.getIncludeTopics()) {
            List<ContentTopic> topics = topicRepository.findByContentIdAndTenantId(
                    query.getContentId(), query.getTenantId());
            dto.setTopics(topics.stream().map(mapper::toDto).collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * List analyses use case
     */
    @Transactional(readOnly = true)
    public List<ContentAnalysisDto> listAnalyses(ListAnalysesQuery query) {
        log.debug("Listing analyses for tenant: {}", query.getTenantId());
        TenantContext.setTenantId(query.getTenantId());

        int page = query.getPage() != null ? query.getPage() : 0;
        int size = query.getSize() != null ? query.getSize() : 20;
        String sortBy = query.getSortBy() != null ? query.getSortBy() : "createdAt";
        String sortDirection = query.getSortDirection() != null ? query.getSortDirection() : "desc";

        PageRequest pageRequest = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy));

        List<ContentAnalysis> analyses;
        if (query.getStatus() != null) {
            ContentAnalysis.AnalysisStatus status = ContentAnalysis.AnalysisStatus.valueOf(query.getStatus());
            analyses = analysisRepository.findByStatusAndTenantId(status, query.getTenantId());
        } else {
            analyses = analysisRepository.findByTenantId(query.getTenantId());
        }

        return analyses.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Extract topics use case
     */
    @Transactional
    public List<ContentTopicDto> extractTopics(ExtractTopicsCommand command) {
        log.info("Extracting topics for contentId: {} for tenant: {}", command.getContentId(), command.getTenantId());
        TenantContext.setTenantId(command.getTenantId());

        List<ContentTopic> topics = performTopicExtraction(command);

        // Save topics
        List<ContentTopic> savedTopics = topicRepository.saveAll(topics);

        log.info("Extracted {} topics for contentId: {}", savedTopics.size(), command.getContentId());
        return savedTopics.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get detailed metrics use case
     */
    @Transactional(readOnly = true)
    public ContentMetricsDto getMetrics(GetMetricsQuery query) {
        log.debug("Getting metrics for analysisId: {} for tenant: {}", query.getAnalysisId(), query.getTenantId());
        TenantContext.setTenantId(query.getTenantId());

        ContentAnalysis analysis = analysisRepository.findByIdAndTenantId(query.getAnalysisId(), query.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Content analysis not found"));

        if (analysis.getMetrics() == null) {
            throw new IllegalArgumentException("Metrics not available for this analysis");
        }

        return mapper.toDto(analysis.getMetrics());
    }

    /**
     * Delete analysis use case
     */
    @Transactional
    public void deleteAnalysis(String tenantId, UUID analysisId) {
        log.info("Deleting analysis: {} for tenant: {}", analysisId, tenantId);
        TenantContext.setTenantId(tenantId);

        ContentAnalysis analysis = analysisRepository.findByIdAndTenantId(analysisId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Content analysis not found"));

        // Delete associated topics
        topicRepository.deleteByContentIdAndTenantId(analysis.getContentId(), tenantId);

        // Delete analysis
        analysisRepository.deleteByIdAndTenantId(analysisId, tenantId);

        log.info("Analysis deleted successfully: {}", analysisId);
    }

    /**
     * Perform actual content analysis (simplified implementation)
     */
    private void performContentAnalysis(ContentAnalysisAggregate aggregate) {
        ContentAnalysis analysis = aggregate.getContentAnalysis();
        String content = analysis.getContentBody();

        // Calculate metrics
        ContentMetrics metrics = calculateMetrics(content, analysis);
        aggregate.getContentAnalysis().setMetrics(metrics);

        // Calculate sentiment
        SentimentAnalysis sentiment = calculateSentiment(content);
        aggregate.getContentAnalysis().setSentiment(sentiment);

        // Calculate SEO
        SEOAnalysis seoAnalysis = calculateSEO(content, analysis.getContentTitle());
        aggregate.getContentAnalysis().setSeoAnalysis(seoAnalysis);

        // Calculate readability
        ReadabilityAnalysis readability = calculateReadability(content, analysis);
        aggregate.getContentAnalysis().setReadability(readability);

        // Extract topics
        List<ContentTopic> topics = extractTopicsFromContent(content, analysis);
        aggregate.setTopics(topics);

        // Mark as completed
        aggregate.completeAnalysis(metrics, sentiment, seoAnalysis, readability);
    }

    /**
     * Calculate content metrics
     */
    private ContentMetrics calculateMetrics(String content, ContentAnalysis analysis) {
        String[] words = content.split("\\s+");

        return ContentMetrics.builder()
                .qualityScore(75.0)
                .engagementScore(70.0)
                .clarityScore(80.0)
                .coherenceScore(75.0)
                .originalityScore(70.0)
                .formattingScore(85.0)
                .grammarScore(80.0)
                .spellingScore(85.0)
                .structureScore(75.0)
                .completenessScore(80.0)
                .uniqueWordCount((int) (words.length * 0.7))
                .averageWordLength(5)
                .averageSentenceLength(15)
                .vocabularyRichness(0.6)
                .lexicalDiversity(0.7)
                .build();
    }

    /**
     * Calculate sentiment analysis
     */
    private SentimentAnalysis calculateSentiment(String content) {
        return SentimentAnalysis.builder()
                .sentimentScore(0.3)
                .sentimentType(SentimentAnalysis.SentimentType.POSITIVE)
                .confidence(0.85)
                .positivityScore(0.6)
                .negativityScore(0.2)
                .neutralityScore(0.2)
                .dominantEmotion("Neutral")
                .positiveWordCount(15)
                .negativeWordCount(5)
                .neutralWordCount(30)
                .build();
    }

    /**
     * Calculate SEO analysis
     */
    private SEOAnalysis calculateSEO(String content, String title) {
        List<String> keywords = List.of("content", "analysis", "quality");

        return SEOAnalysis.builder()
                .seoScore(72.0)
                .keywordDensity(2)
                .primaryKeyword("content")
                .extractedKeywords(keywords)
                .keyPhrases(List.of("content analysis", "quality content"))
                .hasTitle(title != null && !title.isEmpty())
                .titleLength(title != null ? title.length() : 0)
                .isTitleOptimal(title != null && title.length() >= 30 && title.length() <= 60)
                .hasMetaDescription(false)
                .headingCount(3)
                .hasH1(true)
                .h1Count(1)
                .h2Count(1)
                .h3Count(1)
                .linkCount(0)
                .imageCount(0)
                .fleschReadingEase(65.0)
                .fleschKincaidGrade("8.5")
                .build();
    }

    /**
     * Calculate readability analysis
     */
    private ReadabilityAnalysis calculateReadability(String content, ContentAnalysis analysis) {
        String[] sentences = content.split("[.!?]+");
        String[] words = content.split("\\s+");

        return ReadabilityAnalysis.builder()
                .readabilityScore(75.0)
                .readabilityLevel("Standard")
                .targetAudience("General")
                .fleschReadingEase(65.0)
                .fleschKincaidGrade("8.5")
                .gunningFogIndex(10.0)
                .colemanLiauIndex(9.0)
                .automatedReadabilityIndex(8.5)
                .averageWordsPerSentence(15)
                .averageSyllablesPerWord(1)
                .averageCharactersPerWord(5)
                .percentageOfComplexWords(15)
                .sentenceCount(sentences.length)
                .wordCount(words.length)
                .complexWordCount(words.length / 10)
                .readingTimeMinutes(String.format("%.1f", words.length / 200.0))
                .speakingTimeMinutes(String.format("%.1f", words.length / 150.0))
                .build();
    }

    /**
     * Extract topics from content
     */
    private List<ContentTopic> extractTopicsFromContent(String content, ContentAnalysis analysis) {
        List<ContentTopic> topics = new ArrayList<>();

        ContentTopic mainTopic = ContentTopic.builder()
                .id(UUID.randomUUID())
                .tenantId(analysis.getTenantId())
                .contentId(analysis.getContentId())
                .topicName("Content Quality")
                .topicCategory("General")
                .relevanceScore(0.85)
                .confidence(0.90)
                .keywords(List.of("quality", "content", "analysis"))
                .keyPhrases(List.of("content analysis", "quality metrics"))
                .mainTopic("Content Quality")
                .subTopics(List.of("Quality Assessment", "Metrics"))
                .topicHierarchyLevel(0)
                .extractedAt(LocalDateTime.now())
                .build();

        topics.add(mainTopic);

        return topics;
    }

    /**
     * Perform topic extraction for command
     */
    private List<ContentTopic> performTopicExtraction(ExtractTopicsCommand command) {
        List<ContentTopic> topics = new ArrayList<>();

        ContentTopic topic = ContentTopic.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .contentId(command.getContentId())
                .topicName("Primary Topic")
                .topicCategory("General")
                .relevanceScore(0.85)
                .confidence(0.90)
                .keywords(List.of("topic", "content"))
                .keyPhrases(List.of("primary topic"))
                .mainTopic("Primary Topic")
                .topicHierarchyLevel(0)
                .extractedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        topics.add(topic);

        return topics;
    }
}
