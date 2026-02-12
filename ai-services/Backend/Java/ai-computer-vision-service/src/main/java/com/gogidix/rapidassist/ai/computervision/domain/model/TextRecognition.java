package com.gogidix.rapidassist.ai.computervision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing OCR text extraction results
 * Pure domain model without MongoDB annotations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextRecognition {

    private UUID id;
    private String tenantId;
    private UUID imageAnalysisId;
    private String fullText;
    @Builder.Default
    private List<TextLine> textLines = new ArrayList<>();
    @Builder.Default
    private List<TextWord> textWords = new ArrayList<>();
    private String language;
    private Double confidenceScore;
    private Integer totalCharacters;
    private Integer totalWords;
    private Integer totalLines;

    /**
     * Business logic: Get confidence level
     */
    public ConfidenceLevel getConfidenceLevel() {
        if (confidenceScore == null) {
            return ConfidenceLevel.VERY_LOW;
        }
        return ConfidenceLevel.fromScore(confidenceScore);
    }

    /**
     * Business logic: Check if OCR is confident
     */
    public boolean isConfident(double threshold) {
        return confidenceScore != null && confidenceScore >= threshold;
    }

    /**
     * Business logic: Check if text was extracted
     */
    public boolean hasText() {
        return fullText != null && !fullText.trim().isEmpty();
    }

    /**
     * Business logic: Get text lines sorted by Y position (top to bottom)
     */
    public List<TextLine> getTextLinesSorted() {
        return textLines.stream()
                .filter(line -> line.getBoundingBox() != null && line.getBoundingBox().getY() != null)
                .sorted(Comparator.comparing(line -> line.getBoundingBox().getY()))
                .toList();
    }

    /**
     * Business logic: Get text by region
     */
    public List<TextLine> getTextByRegion(double x, double y, double width, double height) {
        return textLines.stream()
                .filter(line -> line.isInRegion(x, y, width, height))
                .toList();
    }

    /**
     * Business logic: Get average line height
     */
    public Double getAverageLineHeight() {
        return textLines.stream()
                .filter(line -> line.getBoundingBox() != null && line.getBoundingBox().getHeight() != null)
                .mapToInt(line -> line.getBoundingBox().getHeight().intValue())
                .average()
                .orElse(0.0);
    }

    /**
     * Business logic: Get longest line
     */
    public TextLine getLongestLine() {
        return textLines.stream()
                .max(Comparator.comparingInt(line -> line.getText() != null ? line.getText().length() : 0))
                .orElse(null);
    }

    /**
     * Business logic: Extract only numbers from text
     */
    public String extractNumbers() {
        if (fullText == null) {
            return "";
        }
        return fullText.replaceAll("[^0-9]", " ").trim().replaceAll("\\s+", " ");
    }

    /**
     * Business logic: Extract only alphanumeric text
     */
    public String extractAlphanumeric() {
        if (fullText == null) {
            return "";
        }
        return fullText.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
    }

    /**
     * Inner class representing a line of text
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextLine {
        private String text;
        private Double confidence;
        private ObjectDetection.BoundingBox boundingBox;

        public boolean isInRegion(double x, double y, double width, double height) {
            if (boundingBox == null || boundingBox.getX() == null || boundingBox.getY() == null) {
                return false;
            }
            return boundingBox.getX() >= x &&
                   boundingBox.getY() >= y &&
                   boundingBox.getX() + (boundingBox.getWidth() != null ? boundingBox.getWidth() : 0) <= x + width &&
                   boundingBox.getY() + (boundingBox.getHeight() != null ? boundingBox.getHeight() : 0) <= y + height;
        }
    }

    /**
     * Inner class representing a word of text
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextWord {
        private String text;
        private Double confidence;
        private ObjectDetection.BoundingBox boundingBox;
        private Integer lineNumber;
    }
}
