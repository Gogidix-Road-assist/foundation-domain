package com.gogidix.rapidassist.ai.contentanalysis.domain.aggregate;

import com.gogidix.rapidassist.ai.contentanalysis.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root for Content Analysis.
 * Manages the lifecycle and business logic of content analysis operations.
 * Contains: ContentAnalysis, ContentTopic, and AnalysisRequest as child entities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentAnalysisAggregate {

    private ContentAnalysis contentAnalysis;
    @Builder.Default
    private List<ContentTopic> topics = new ArrayList<>();
    private AnalysisRequest analysisRequest;

    /**
     * Business logic: Initialize a new content analysis
     */
    public static ContentAnalysisAggregate initialize(String tenantId, String contentId,
                                                      String contentType, String contentTitle,
                                                      String contentBody, String contentLanguage) {
        ContentAnalysis analysis = ContentAnalysis.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .contentId(contentId)
                .contentType(contentType)
                .contentTitle(contentTitle)
                .contentBody(contentBody)
                .contentLanguage(contentLanguage)
                .status(ContentAnalysis.AnalysisStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .analysisVersion("1.0")
                .build();

        return ContentAnalysisAggregate.builder()
                .contentAnalysis(analysis)
                .topics(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Start analysis process
     */
    public void startAnalysis() {
        if (this.contentAnalysis == null) {
            throw new IllegalStateException("Content analysis not initialized");
        }

        if (!this.contentAnalysis.getStatus().equals(ContentAnalysis.AnalysisStatus.PENDING)) {
            throw new IllegalStateException("Analysis can only be started from PENDING status");
        }

        this.contentAnalysis.markAsInProgress();

        if (this.analysisRequest != null) {
            this.analysisRequest.markAsStarted();
        }
    }

    /**
     * Business logic: Complete analysis with results
     */
    public void completeAnalysis(ContentMetrics metrics, SentimentAnalysis sentiment,
                                SEOAnalysis seoAnalysis, ReadabilityAnalysis readability) {
        if (this.contentAnalysis == null) {
            throw new IllegalStateException("Content analysis not initialized");
        }

        this.contentAnalysis.setMetrics(metrics);
        this.contentAnalysis.setSentiment(sentiment);
        this.contentAnalysis.setSeoAnalysis(seoAnalysis);
        this.contentAnalysis.setReadability(readability);
        this.contentAnalysis.markAsCompleted();

        if (this.analysisRequest != null) {
            this.analysisRequest.markAsCompleted(this.contentAnalysis.getId());
        }
    }

    /**
     * Business logic: Fail analysis with error message
     */
    public void failAnalysis(String errorMessage) {
        if (this.contentAnalysis == null) {
            throw new IllegalStateException("Content analysis not initialized");
        }

        this.contentAnalysis.markAsFailed();

        if (this.analysisRequest != null) {
            this.analysisRequest.markAsFailed(errorMessage);
        }
    }

    /**
     * Business logic: Add extracted topic
     */
    public void addTopic(ContentTopic topic) {
        if (topic == null) {
            throw new IllegalArgumentException("Topic cannot be null");
        }

        topic.setTenantId(this.contentAnalysis.getTenantId());
        topic.setContentId(this.contentAnalysis.getContentId());
        topic.setExtractedAt(LocalDateTime.now());

        if (topic.getCreatedAt() == null) {
            topic.setCreatedAt(LocalDateTime.now());
        }

        this.topics.add(topic);
    }

    /**
     * Business logic: Set analysis request
     */
    public void setAnalysisRequest(AnalysisRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        request.setTenantId(this.contentAnalysis.getTenantId());
        request.setContentId(this.contentAnalysis.getContentId());

        if (request.getCreatedAt() == null) {
            request.setCreatedAt(LocalDateTime.now());
        }

        this.analysisRequest = request;
    }

    /**
     * Business logic: Get primary topics (relevance > 0.7)
     */
    public List<ContentTopic> getPrimaryTopics() {
        return this.topics.stream()
                .filter(topic -> topic.getRelevanceScore() != null && topic.getRelevanceScore() >= 0.7)
                .toList();
    }

    /**
     * Business logic: Get main topic
     */
    public ContentTopic getMainTopic() {
        return this.topics.stream()
                .filter(ContentTopic::isMainTopic)
                .findFirst()
                .orElse(null);
    }

    /**
     * Business logic: Check if analysis is complete
     */
    public boolean isAnalysisComplete() {
        return this.contentAnalysis != null && this.contentAnalysis.isCompleted();
    }

    /**
     * Business logic: Check if analysis failed
     */
    public boolean isAnalysisFailed() {
        return this.contentAnalysis != null && this.contentAnalysis.isFailed();
    }

    /**
     * Business logic: Check if analysis is in progress
     */
    public boolean isAnalysisInProgress() {
        return this.contentAnalysis != null && this.contentAnalysis.isInProgress();
    }

    /**
     * Business logic: Get analysis summary
     */
    public String getAnalysisSummary() {
        if (this.contentAnalysis == null) {
            return "No analysis available";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("Content Analysis Summary:\n");
        summary.append("Status: ").append(this.contentAnalysis.getStatus()).append("\n");
        summary.append("Overall Score: ").append(String.format("%.1f", this.contentAnalysis.getOverallScore() != null ?
                this.contentAnalysis.getOverallScore() : 0.0)).append("/100\n");
        summary.append("Quality Grade: ").append(this.contentAnalysis.getQualityGrade()).append("\n");
        summary.append("Topics Extracted: ").append(this.topics.size()).append("\n");

        if (this.contentAnalysis.getSentiment() != null) {
            summary.append("Sentiment: ").append(this.contentAnalysis.getSentiment().getSentimentType()).append("\n");
        }

        return summary.toString();
    }

    /**
     * Business logic: Calculate word count from content body
     */
    public void calculateContentStatistics() {
        if (this.contentAnalysis == null || this.contentAnalysis.getContentBody() == null) {
            return;
        }

        String content = this.contentAnalysis.getContentBody();

        // Word count
        String[] words = content.split("\\s+");
        this.contentAnalysis.setWordCount(words.length);

        // Character count
        this.contentAnalysis.setCharacterCount(content.length());

        // Sentence count (approximate)
        String[] sentences = content.split("[.!?]+");
        this.contentAnalysis.setSentenceCount(sentences.length);

        // Paragraph count
        String[] paragraphs = content.split("\\n\\n+");
        this.contentAnalysis.setParagraphCount(paragraphs.length);
    }

    /**
     * Business logic: Update analysis progress
     */
    public void updateProgress(Integer percentage, String step) {
        if (this.analysisRequest != null) {
            this.analysisRequest.updateProgress(percentage, step);
        }
    }
}
