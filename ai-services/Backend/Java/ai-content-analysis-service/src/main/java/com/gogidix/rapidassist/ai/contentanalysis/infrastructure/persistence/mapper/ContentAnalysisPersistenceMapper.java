package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.contentanalysis.domain.model.ContentAnalysis;
import com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.ContentAnalysisEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for ContentAnalysis persistence
 */
@Mapper(componentModel = "spring")
public interface ContentAnalysisPersistenceMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    @Mapping(target = "metrics", source = "metrics", qualifiedByName = "entityToMetrics")
    @Mapping(target = "sentiment", source = "sentiment", qualifiedByName = "entityToSentiment")
    @Mapping(target = "seoAnalysis", source = "seoAnalysis", qualifiedByName = "entityToSeo")
    @Mapping(target = "readability", source = "readability", qualifiedByName = "entityToReadability")
    ContentAnalysis toDomain(ContentAnalysisEntity entity);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "metrics", source = "metrics", qualifiedByName = "metricsToEntity")
    @Mapping(target = "sentiment", source = "sentiment", qualifiedByName = "sentimentToEntity")
    @Mapping(target = "seoAnalysis", source = "seoAnalysis", qualifiedByName = "seoToEntity")
    @Mapping(target = "readability", source = "readability", qualifiedByName = "readabilityToEntity")
    ContentAnalysisEntity toEntity(ContentAnalysis domain);

    @Named("statusToString")
    static String statusToString(ContentAnalysis.AnalysisStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToStatus")
    static ContentAnalysis.AnalysisStatus stringToStatus(String status) {
        return status != null ? ContentAnalysis.AnalysisStatus.valueOf(status) : null;
    }

    @Named("entityToMetrics")
    default com.gogidix.rapidassist.ai.contentanalysis.domain.model.ContentMetrics entityToMetrics(
            com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.ContentMetricsEntity entity) {
        if (entity == null) return null;
        return com.gogidix.rapidassist.ai.contentanalysis.domain.model.ContentMetrics.builder()
                .qualityScore(entity.getQualityScore())
                .engagementScore(entity.getEngagementScore())
                .clarityScore(entity.getClarityScore())
                .coherenceScore(entity.getCoherenceScore())
                .originalityScore(entity.getOriginalityScore())
                .formattingScore(entity.getFormattingScore())
                .grammarScore(entity.getGrammarScore())
                .spellingScore(entity.getSpellingScore())
                .structureScore(entity.getStructureScore())
                .completenessScore(entity.getCompletenessScore())
                .uniqueWordCount(entity.getUniqueWordCount())
                .averageWordLength(entity.getAverageWordLength())
                .averageSentenceLength(entity.getAverageSentenceLength())
                .averageParagraphLength(entity.getAverageParagraphLength())
                .vocabularyRichness(entity.getVocabularyRichness())
                .lexicalDiversity(entity.getLexicalDiversity())
                .additionalMetrics(entity.getAdditionalMetrics())
                .qualityFlags(entity.getQualityFlags())
                .improvementSuggestions(entity.getImprovementSuggestions())
                .build();
    }

    @Named("metricsToEntity")
    default com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.ContentMetricsEntity metricsToEntity(
            com.gogidix.rapidassist.ai.contentanalysis.domain.model.ContentMetrics domain) {
        if (domain == null) return null;
        return com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.ContentMetricsEntity.builder()
                .qualityScore(domain.getQualityScore())
                .engagementScore(domain.getEngagementScore())
                .clarityScore(domain.getClarityScore())
                .coherenceScore(domain.getCoherenceScore())
                .originalityScore(domain.getOriginalityScore())
                .formattingScore(domain.getFormattingScore())
                .grammarScore(domain.getGrammarScore())
                .spellingScore(domain.getSpellingScore())
                .structureScore(domain.getStructureScore())
                .completenessScore(domain.getCompletenessScore())
                .uniqueWordCount(domain.getUniqueWordCount())
                .averageWordLength(domain.getAverageWordLength())
                .averageSentenceLength(domain.getAverageSentenceLength())
                .averageParagraphLength(domain.getAverageParagraphLength())
                .vocabularyRichness(domain.getVocabularyRichness())
                .lexicalDiversity(domain.getLexicalDiversity())
                .additionalMetrics(domain.getAdditionalMetrics())
                .qualityFlags(domain.getQualityFlags())
                .improvementSuggestions(domain.getImprovementSuggestions())
                .build();
    }

    @Named("entityToSentiment")
    default com.gogidix.rapidassist.ai.contentanalysis.domain.model.SentimentAnalysis entityToSentiment(
            com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.SentimentAnalysisEntity entity) {
        if (entity == null) return null;
        return com.gogidix.rapidassist.ai.contentanalysis.domain.model.SentimentAnalysis.builder()
                .sentimentScore(entity.getSentimentScore())
                .sentimentType(entity.getSentimentType() != null ?
                        com.gogidix.rapidassist.ai.contentanalysis.domain.model.SentimentAnalysis.SentimentType.valueOf(entity.getSentimentType()) : null)
                .confidence(entity.getConfidence())
                .positivityScore(entity.getPositivityScore())
                .negativityScore(entity.getNegativityScore())
                .neutralityScore(entity.getNeutralityScore())
                .dominantEmotion(entity.getDominantEmotion())
                .emotionBreakdown(entity.getEmotionBreakdown())
                .sentimentKeywords(entity.getSentimentKeywords())
                .positiveWordCount(entity.getPositiveWordCount())
                .negativeWordCount(entity.getNegativeWordCount())
                .neutralWordCount(entity.getNeutralWordCount())
                .build();
    }

    @Named("sentimentToEntity")
    default com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.SentimentAnalysisEntity sentimentToEntity(
            com.gogidix.rapidassist.ai.contentanalysis.domain.model.SentimentAnalysis domain) {
        if (domain == null) return null;
        return com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.SentimentAnalysisEntity.builder()
                .sentimentScore(domain.getSentimentScore())
                .sentimentType(domain.getSentimentType() != null ? domain.getSentimentType().name() : null)
                .confidence(domain.getConfidence())
                .positivityScore(domain.getPositivityScore())
                .negativityScore(domain.getNegativityScore())
                .neutralityScore(domain.getNeutralityScore())
                .dominantEmotion(domain.getDominantEmotion())
                .emotionBreakdown(domain.getEmotionBreakdown())
                .sentimentKeywords(domain.getSentimentKeywords())
                .positiveWordCount(domain.getPositiveWordCount())
                .negativeWordCount(domain.getNegativeWordCount())
                .neutralWordCount(domain.getNeutralWordCount())
                .build();
    }

    @Named("entityToSeo")
    default com.gogidix.rapidassist.ai.contentanalysis.domain.model.SEOAnalysis entityToSeo(
            com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.SEOAnalysisEntity entity) {
        if (entity == null) return null;
        return com.gogidix.rapidassist.ai.contentanalysis.domain.model.SEOAnalysis.builder()
                .seoScore(entity.getSeoScore())
                .keywordDensity(entity.getKeywordDensity())
                .primaryKeyword(entity.getPrimaryKeyword())
                .extractedKeywords(entity.getExtractedKeywords())
                .keyPhrases(entity.getKeyPhrases())
                .missingKeywords(entity.getMissingKeywords())
                .hasTitle(entity.getHasTitle())
                .titleLength(entity.getTitleLength())
                .isTitleOptimal(entity.getIsTitleOptimal())
                .hasMetaDescription(entity.getHasMetaDescription())
                .metaDescriptionLength(entity.getMetaDescriptionLength())
                .isMetaDescriptionOptimal(entity.getIsMetaDescriptionOptimal())
                .headingCount(entity.getHeadingCount())
                .hasH1(entity.getHasH1())
                .h1Count(entity.getH1Count())
                .h2Count(entity.getH2Count())
                .h3Count(entity.getH3Count())
                .linkCount(entity.getLinkCount())
                .internalLinkCount(entity.getInternalLinkCount())
                .externalLinkCount(entity.getExternalLinkCount())
                .hasBrokenLinks(entity.getHasBrokenLinks())
                .imageCount(entity.getImageCount())
                .imagesWithoutAlt(entity.getImagesWithoutAlt())
                .imageOptimizationScore(entity.getImageOptimizationScore())
                .readabilityScore(entity.getReadabilityScore())
                .fleschReadingEase(entity.getFleschReadingEase())
                .fleschKincaidGrade(entity.getFleschKincaidGrade())
                .seoRecommendations(entity.getSeoRecommendations())
                .checklistItems(entity.getChecklistItems())
                .improvementSuggestions(entity.getImprovementSuggestions())
                .build();
    }

    @Named("seoToEntity")
    default com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.SEOAnalysisEntity seoToEntity(
            com.gogidix.rapidassist.ai.contentanalysis.domain.model.SEOAnalysis domain) {
        if (domain == null) return null;
        return com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.SEOAnalysisEntity.builder()
                .seoScore(domain.getSeoScore())
                .keywordDensity(domain.getKeywordDensity())
                .primaryKeyword(domain.getPrimaryKeyword())
                .extractedKeywords(domain.getExtractedKeywords())
                .keyPhrases(domain.getKeyPhrases())
                .missingKeywords(domain.getMissingKeywords())
                .hasTitle(domain.getHasTitle())
                .titleLength(domain.getTitleLength())
                .isTitleOptimal(domain.getIsTitleOptimal())
                .hasMetaDescription(domain.getHasMetaDescription())
                .metaDescriptionLength(domain.getMetaDescriptionLength())
                .isMetaDescriptionOptimal(domain.getIsMetaDescriptionOptimal())
                .headingCount(domain.getHeadingCount())
                .hasH1(domain.getHasH1())
                .h1Count(domain.getH1Count())
                .h2Count(domain.getH2Count())
                .h3Count(domain.getH3Count())
                .linkCount(domain.getLinkCount())
                .internalLinkCount(domain.getInternalLinkCount())
                .externalLinkCount(domain.getExternalLinkCount())
                .hasBrokenLinks(domain.getHasBrokenLinks())
                .imageCount(domain.getImageCount())
                .imagesWithoutAlt(domain.getImagesWithoutAlt())
                .imageOptimizationScore(domain.getImageOptimizationScore())
                .readabilityScore(domain.getReadabilityScore())
                .fleschReadingEase(domain.getFleschReadingEase())
                .fleschKincaidGrade(domain.getFleschKincaidGrade())
                .seoRecommendations(domain.getSeoRecommendations())
                .checklistItems(domain.getChecklistItems())
                .improvementSuggestions(domain.getImprovementSuggestions())
                .build();
    }

    @Named("entityToReadability")
    default com.gogidix.rapidassist.ai.contentanalysis.domain.model.ReadabilityAnalysis entityToReadability(
            com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.ReadabilityAnalysisEntity entity) {
        if (entity == null) return null;
        return com.gogidix.rapidassist.ai.contentanalysis.domain.model.ReadabilityAnalysis.builder()
                .readabilityScore(entity.getReadabilityScore())
                .readabilityLevel(entity.getReadabilityLevel())
                .targetAudience(entity.getTargetAudience())
                .fleschReadingEase(entity.getFleschReadingEase())
                .fleschKincaidGrade(entity.getFleschKincaidGrade())
                .gunningFogIndex(entity.getGunningFogIndex())
                .colemanLiauIndex(entity.getColemanLiauIndex())
                .automatedReadabilityIndex(entity.getAutomatedReadabilityIndex())
                .averageWordsPerSentence(entity.getAverageWordsPerSentence())
                .averageSyllablesPerWord(entity.getAverageSyllablesPerWord())
                .averageCharactersPerWord(entity.getAverageCharactersPerWord())
                .percentageOfComplexWords(entity.getPercentageOfComplexWords())
                .sentenceCount(entity.getSentenceCount())
                .wordCount(entity.getWordCount())
                .complexWordCount(entity.getComplexWordCount())
                .syllableCount(entity.getSyllableCount())
                .readingTimeMinutes(entity.getReadingTimeMinutes())
                .speakingTimeMinutes(entity.getSpeakingTimeMinutes())
                .estimatedPages(entity.getEstimatedPages())
                .gradeLevelBreakdown(entity.getGradeLevelBreakdown())
                .difficultyFactors(entity.getDifficultyFactors())
                .complexWords(entity.getComplexWords())
                .difficultSentences(entity.getDifficultSentences())
                .build();
    }

    @Named("readabilityToEntity")
    default com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.ReadabilityAnalysisEntity readabilityToEntity(
            com.gogidix.rapidassist.ai.contentanalysis.domain.model.ReadabilityAnalysis domain) {
        if (domain == null) return null;
        return com.gogidix.rapidassist.ai.contentanalysis.infrastructure.persistence.entity.ReadabilityAnalysisEntity.builder()
                .readabilityScore(domain.getReadabilityScore())
                .readabilityLevel(domain.getReadabilityLevel())
                .targetAudience(domain.getTargetAudience())
                .fleschReadingEase(domain.getFleschReadingEase())
                .fleschKincaidGrade(domain.getFleschKincaidGrade())
                .gunningFogIndex(domain.getGunningFogIndex())
                .colemanLiauIndex(domain.getColemanLiauIndex())
                .automatedReadabilityIndex(domain.getAutomatedReadabilityIndex())
                .averageWordsPerSentence(domain.getAverageWordsPerSentence())
                .averageSyllablesPerWord(domain.getAverageSyllablesPerWord())
                .averageCharactersPerWord(domain.getAverageCharactersPerWord())
                .percentageOfComplexWords(domain.getPercentageOfComplexWords())
                .sentenceCount(domain.getSentenceCount())
                .wordCount(domain.getWordCount())
                .complexWordCount(domain.getComplexWordCount())
                .syllableCount(domain.getSyllableCount())
                .readingTimeMinutes(domain.getReadingTimeMinutes())
                .speakingTimeMinutes(domain.getSpeakingTimeMinutes())
                .estimatedPages(domain.getEstimatedPages())
                .gradeLevelBreakdown(domain.getGradeLevelBreakdown())
                .difficultyFactors(domain.getDifficultyFactors())
                .complexWords(domain.getComplexWords())
                .difficultSentences(domain.getDifficultSentences())
                .build();
    }
}
