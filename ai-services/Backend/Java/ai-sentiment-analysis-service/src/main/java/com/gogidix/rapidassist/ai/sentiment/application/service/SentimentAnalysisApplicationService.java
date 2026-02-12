package com.gogidix.rapidassist.ai.sentiment.application.service;

import com.gogidix.rapidassist.ai.sentiment.application.command.AnalyzeSentimentCommand;
import com.gogidix.rapidassist.ai.sentiment.application.command.BatchAnalyzeSentimentCommand;
import com.gogidix.rapidassist.ai.sentiment.application.dto.*;
import com.gogidix.rapidassist.ai.sentiment.application.mapper.SentimentAnalysisMapper;
import com.gogidix.rapidassist.ai.sentiment.application.query.GetSentimentAnalysisQuery;
import com.gogidix.rapidassist.ai.sentiment.application.query.GetUserSentimentAnalysesQuery;
import com.gogidix.rapidassist.ai.sentiment.domain.aggregate.SentimentAnalysis;
import com.gogidix.rapidassist.ai.sentiment.domain.model.*;
import com.gogidix.rapidassist.ai.sentiment.domain.repository.SentimentAnalysisRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Application service for Sentiment Analysis operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SentimentAnalysisApplicationService {

    private final SentimentAnalysisRepositoryPort repository;
    private final SentimentAnalysisMapper mapper;

    /**
     * Analyze sentiment of text
     */
    @Transactional
    public SentimentAnalysisDto analyzeSentiment(AnalyzeSentimentCommand command) {
        log.info("Analyzing sentiment for tenant: {}, user: {}", command.getTenantId(), command.getUserId());

        // Initialize sentiment analysis
        SentimentAnalysis analysis = SentimentAnalysis.initialize(
            command.getTenantId(),
            command.getUserId(),
            command.getText(),
            command.getSourceType(),
            command.getSourceId()
        );

        if (command.getLanguage() != null) {
            analysis.setLanguage(command.getLanguage());
        }

        if (command.getMetadata() != null) {
            analysis.setMetadata(command.getMetadata());
        }

        // Save initial state
        analysis = repository.save(analysis);

        // Process sentiment analysis
        analysis = processSentimentAnalysis(analysis, command.getIncludeEmotions(), command.getIncludeAspects());

        // Save results
        analysis = repository.save(analysis);

        return enrichDto(mapper.toDto(analysis), analysis);
    }

    /**
     * Batch analyze sentiment
     */
    @Transactional
    public BatchSentimentResultDto batchAnalyzeSentiment(BatchAnalyzeSentimentCommand command) {
        log.info("Batch analyzing sentiment for tenant: {}, user: {}, count: {}",
            command.getTenantId(), command.getUserId(), command.getTexts().size());

        String batchId = UUID.randomUUID().toString();
        List<SentimentAnalysisDto> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (String text : command.getTexts()) {
            try {
                AnalyzeSentimentCommand singleCommand = AnalyzeSentimentCommand.builder()
                    .tenantId(command.getTenantId())
                    .userId(command.getUserId())
                    .text(text)
                    .language(command.getLanguage())
                    .sourceType(command.getSourceType())
                    .sourceId(command.getSourceId())
                    .includeEmotions(command.getIncludeEmotions())
                    .includeAspects(command.getIncludeAspects())
                    .metadata(command.getMetadata())
                    .build();

                SentimentAnalysisDto result = analyzeSentiment(singleCommand);
                results.add(result);
                successCount++;
            } catch (Exception e) {
                log.error("Error analyzing text in batch: {}", e.getMessage());
                failureCount++;
            }
        }

        return BatchSentimentResultDto.builder()
            .results(results)
            .totalCount(command.getTexts().size())
            .successCount(successCount)
            .failureCount(failureCount)
            .batchId(batchId)
            .build();
    }

    /**
     * Get sentiment analysis by ID
     */
    public SentimentAnalysisDto getSentimentAnalysis(GetSentimentAnalysisQuery query) {
        log.info("Getting sentiment analysis: {}", query.getAnalysisId());

        SentimentAnalysis analysis = repository.findByTenantIdAndId(query.getTenantId(), query.getAnalysisId())
            .orElseThrow(() -> new IllegalArgumentException("Sentiment analysis not found"));

        SentimentAnalysisDto dto = mapper.toDto(analysis);

        // Apply filters
        if (!Boolean.TRUE.equals(query.getIncludeEmotions())) {
            dto.setEmotions(null);
        }
        if (!Boolean.TRUE.equals(query.getIncludeAspects())) {
            dto.setAspects(null);
        }

        return enrichDto(dto, analysis);
    }

    /**
     * Get user sentiment analyses
     */
    public List<SentimentAnalysisDto> getUserSentimentAnalyses(GetUserSentimentAnalysesQuery query) {
        log.info("Getting sentiment analyses for user: {}", query.getUserId());

        List<SentimentAnalysis> analyses;

        // Apply filters
        if (query.getStartDate() != null && query.getEndDate() != null) {
            analyses = repository.findByTenantIdAndDateRange(
                query.getTenantId(), query.getStartDate(), query.getEndDate()
            );
        } else if (query.getStatus() != null) {
            analyses = repository.findByTenantIdAndStatus(query.getTenantId(), query.getStatus());
        } else if (query.getSentimentType() != null) {
            analyses = repository.findByTenantIdAndSentimentType(query.getTenantId(), query.getSentimentType());
        } else {
            analyses = repository.findByTenantIdAndUserId(query.getTenantId(), query.getUserId());
        }

        // Filter by user ID and apply pagination
        return analyses.stream()
            .filter(a -> a.getUserId().equals(query.getUserId()))
            .skip((long) (query.getPage() != null ? query.getPage() : 0) * (query.getSize() != null ? query.getSize() : 20))
            .limit(query.getSize() != null ? query.getSize() : 20)
            .map(analysis -> enrichDto(mapper.toDto(analysis), analysis))
            .collect(Collectors.toList());
    }

    /**
     * Get sentiment trend analysis
     */
    public SentimentTrendDto getSentimentTrend(String tenantId, String userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting sentiment trend for tenant: {}, user: {}, from: {} to: {}", tenantId, userId, startDate, endDate);

        List<SentimentAnalysis> analyses = repository.findByTenantIdAndDateRange(tenantId, startDate, endDate);

        // Filter by user ID if provided
        if (userId != null) {
            analyses = analyses.stream()
                .filter(a -> a.getUserId().equals(userId))
                .collect(Collectors.toList());
        }

        long totalCount = analyses.size();
        Map<SentimentType, Long> sentimentDistribution = new EnumMap<>(SentimentType.class);
        double totalScore = 0.0;

        for (SentimentAnalysis analysis : analyses) {
            if (analysis.getOverallSentiment() != null && analysis.getSentimentScore() != null) {
                sentimentDistribution.merge(analysis.getOverallSentiment(), 1L, Long::sum);
                totalScore += analysis.getSentimentScore();
            }
        }

        double avgScore = totalCount > 0 ? totalScore / totalCount : 0.0;
        SentimentType dominantSentiment = sentimentDistribution.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);

        long positiveCount = sentimentDistribution.getOrDefault(SentimentType.POSITIVE, 0L);
        long negativeCount = sentimentDistribution.getOrDefault(SentimentType.NEGATIVE, 0L);
        long neutralCount = sentimentDistribution.getOrDefault(SentimentType.NEUTRAL, 0L);

        return SentimentTrendDto.builder()
            .tenantId(tenantId)
            .userId(userId)
            .startDate(startDate)
            .endDate(endDate)
            .totalAnalyses(totalCount)
            .sentimentDistribution(sentimentDistribution)
            .averageSentimentScore(avgScore)
            .dominantSentiment(dominantSentiment)
            .positivePercentage(totalCount > 0 ? (positiveCount * 100.0 / totalCount) : 0.0)
            .negativePercentage(totalCount > 0 ? (negativeCount * 100.0 / totalCount) : 0.0)
            .neutralPercentage(totalCount > 0 ? (neutralCount * 100.0 / totalCount) : 0.0)
            .build();
    }

    /**
     * Delete sentiment analysis
     */
    @Transactional
    public void deleteSentimentAnalysis(String tenantId, UUID analysisId) {
        log.info("Deleting sentiment analysis: {}", analysisId);
        repository.deleteByTenantIdAndId(tenantId, analysisId);
    }

    /**
     * Process sentiment analysis (simulated AI processing)
     */
    private SentimentAnalysis processSentimentAnalysis(SentimentAnalysis analysis, Boolean includeEmotions, Boolean includeAspects) {
        analysis.markAsProcessing();

        String text = analysis.getText().toLowerCase();

        // Simple sentiment analysis logic
        SentimentType sentiment = analyzeSentimentType(text);
        SentimentCategory category = categorizeSentiment(text);
        double score = calculateSentimentScore(text);
        double confidence = calculateConfidence(text);

        analysis.markAsCompleted(sentiment, category, score, confidence);

        // Add emotions if requested
        if (Boolean.TRUE.equals(includeEmotions)) {
            addEmotions(analysis, text);
        }

        // Add aspects if requested
        if (Boolean.TRUE.equals(includeAspects)) {
            addAspects(analysis, text);
        }

        return analysis;
    }

    /**
     * Analyze sentiment type
     */
    private SentimentType analyzeSentimentType(String text) {
        int positiveCount = countWords(text, positiveWords);
        int negativeCount = countWords(text, negativeWords);

        if (positiveCount > negativeCount * 1.5) {
            return SentimentType.POSITIVE;
        } else if (negativeCount > positiveCount * 1.5) {
            return SentimentType.NEGATIVE;
        } else if (positiveCount > 0 && negativeCount > 0) {
            return SentimentType.MIXED;
        } else {
            return SentimentType.NEUTRAL;
        }
    }

    /**
     * Categorize sentiment
     */
    private SentimentCategory categorizeSentiment(String text) {
        int positiveCount = countWords(text, positiveWords);
        int negativeCount = countWords(text, negativeWords);
        int score = positiveCount - negativeCount;

        if (score >= 4) return SentimentCategory.VERY_POSITIVE;
        if (score >= 2) return SentimentCategory.POSITIVE;
        if (score >= 1) return SentimentCategory.SLIGHTLY_POSITIVE;
        if (score == 0) return SentimentCategory.NEUTRAL;
        if (score >= -1) return SentimentCategory.SLIGHTLY_NEGATIVE;
        if (score >= -2) return SentimentCategory.NEGATIVE;
        return SentimentCategory.VERY_NEGATIVE;
    }

    /**
     * Calculate sentiment score
     */
    private double calculateSentimentScore(String text) {
        int positiveCount = countWords(text, positiveWords);
        int negativeCount = countWords(text, negativeWords);
        int total = positiveCount + negativeCount;

        if (total == 0) return 0.0;
        return (positiveCount - negativeCount) * 1.0 / total;
    }

    /**
     * Calculate confidence
     */
    private double calculateConfidence(String text) {
        int positiveCount = countWords(text, positiveWords);
        int negativeCount = countWords(text, negativeWords);
        int total = positiveCount + negativeCount;

        if (total == 0) return 0.5;
        return Math.min(0.95, 0.5 + (total * 0.05));
    }

    /**
     * Add emotions to analysis
     */
    private void addEmotions(SentimentAnalysis analysis, String text) {
        for (EmotionType emotionType : EmotionType.values()) {
            double intensity = calculateEmotionIntensity(text, emotionType);
            if (intensity > 0.3) {
                Emotion emotion = Emotion.builder()
                    .emotionType(emotionType)
                    .intensity(intensity)
                    .confidence(0.7 + Math.random() * 0.25)
                    .description(emotionType.name().toLowerCase() + " emotion detected")
                    .build();
                analysis.addEmotion(emotion);
            }
        }
    }

    /**
     * Calculate emotion intensity
     */
    private double calculateEmotionIntensity(String text, EmotionType emotionType) {
        List<String> emotionWords = emotionWordsMap.getOrDefault(emotionType, List.of());
        int count = countWords(text, emotionWords);
        return Math.min(1.0, count * 0.2);
    }

    /**
     * Add aspects to analysis
     */
    private void addAspects(SentimentAnalysis analysis, String text) {
        // Simple aspect extraction (looking for keywords with surrounding context)
        String[] aspectsToCheck = {"service", "quality", "price", "support", "product", "delivery", "feature", "performance"};

        for (String aspect : aspectsToCheck) {
            if (text.contains(aspect)) {
                SentimentType aspectSentiment = analyzeSentimentType(text);
                Aspect asp = Aspect.builder()
                    .aspectName(aspect)
                    .sentiment(aspectSentiment)
                    .confidence(0.7 + Math.random() * 0.25)
                    .opinionText(extractOpinionText(text, aspect))
                    .build();
                analysis.addAspect(asp);
            }
        }
    }

    /**
     * Extract opinion text for aspect
     */
    private String extractOpinionText(String text, String aspect) {
        int index = text.indexOf(aspect);
        int start = Math.max(0, index - 20);
        int end = Math.min(text.length(), index + aspect.length() + 20);
        return text.substring(start, end).trim();
    }

    /**
     * Count words in text
     */
    private int countWords(String text, List<String> words) {
        int count = 0;
        for (String word : words) {
            if (text.contains(word)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Enrich DTO with additional computed fields
     */
    private SentimentAnalysisDto enrichDto(SentimentAnalysisDto dto, SentimentAnalysis analysis) {
        dto.setDurationSeconds(analysis.getAnalysisDurationSeconds());
        dto.setDominantEmotion(analysis.getDominantEmotion());
        dto.setPositiveAspectCount(analysis.getPositiveAspects().size());
        dto.setNegativeAspectCount(analysis.getNegativeAspects().size());
        dto.setNeutralAspectCount(analysis.getNeutralAspects().size());
        return dto;
    }

    // Sentiment word lists
    private static final List<String> positiveWords = Arrays.asList(
        "good", "great", "excellent", "amazing", "wonderful", "fantastic",
        "awesome", "outstanding", "brilliant", "superb", "love", "happy",
        "pleased", "satisfied", "delighted", "perfect", "best", "nice"
    );

    private static final List<String> negativeWords = Arrays.asList(
        "bad", "terrible", "awful", "horrible", "poor", "worst", "hate",
        "disappointed", "unsatisfied", "angry", "frustrated", "annoying",
        "disgusting", "pathetic", "useless", "waste", "broken", "slow"
    );

    private static final Map<EmotionType, List<String>> emotionWordsMap = new EnumMap<>(EmotionType.class);

    static {
        emotionWordsMap.put(EmotionType.JOY, Arrays.asList("joy", "happy", "excited", "thrilled", "elated"));
        emotionWordsMap.put(EmotionType.SADNESS, Arrays.asList("sad", "unhappy", "depressed", "down", "disappointed"));
        emotionWordsMap.put(EmotionType.ANGER, Arrays.asList("angry", "furious", "mad", "irritated", "frustrated"));
        emotionWordsMap.put(EmotionType.FEAR, Arrays.asList("afraid", "scared", "fearful", "anxious", "worried"));
        emotionWordsMap.put(EmotionType.DISGUST, Arrays.asList("disgusted", "revolted", "repulsed", "sickened"));
        emotionWordsMap.put(EmotionType.SURPRISE, Arrays.asList("surprised", "shocked", "amazed", "astonished"));
        emotionWordsMap.put(EmotionType.TRUST, Arrays.asList("trust", "confident", "secure", "relying"));
        emotionWordsMap.put(EmotionType.ANTICIPATION, Arrays.asList("excited", "eager", "looking forward", "anticipating"));
    }
}
