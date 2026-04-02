package com.gogidix.rapidassist.ai.summarization.domain.policy;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationRequest;
import com.gogidix.rapidassist.ai.summarization.domain.model.Summary;

public class SummarizationPolicy {

    public static boolean isValidRequest(SummarizationRequest request) {
        if (request == null) {
            return false;
        }

        if (request.getSummarizationType() == null) {
            return false;
        }

        if (request.getSummaryLength() == null) {
            return false;
        }

        if (request.getSourceTexts() == null || request.getSourceTexts().isEmpty()) {
            return false;
        }

        for (String text : request.getSourceTexts()) {
            if (text == null || text.trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static boolean canProcessRequest(SummarizationRequest request) {
        return request.getStatus().name().equals("PENDING");
    }

    public static double calculateCompressionRatio(int originalWordCount, int summaryWordCount) {
        if (originalWordCount == 0) {
            return 0.0;
        }
        return (double) summaryWordCount / originalWordCount;
    }

    public static boolean isAcceptableQualityScore(double score) {
        return score >= 0.6;
    }

    public static int calculateMaxSentences(int originalWordCount) {
        int maxSentences = originalWordCount / 20;
        return Math.max(3, Math.min(maxSentences, 10));
    }
}
