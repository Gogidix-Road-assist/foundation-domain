package com.gogidix.rapidassist.ai.summarization.domain.model;

public enum SummaryLength {
    VERY_SHORT(50),
    SHORT(100),
    MEDIUM(200),
    LONG(400),
    VERY_LONG(800);

    private final int maxWords;

    SummaryLength(int maxWords) {
        this.maxWords = maxWords;
    }

    public int getMaxWords() {
        return maxWords;
    }
}
