package com.gogidix.rapidassist.ai.summization.domain.model;

/**
 * Enumeration representing the type of document being summarized.
 */
public enum DocumentType {
    /**
     * Plain text document
     */
    TEXT,

    /**
     * PDF document
     */
    PDF,

    /**
     * Microsoft Word document
     */
    WORD,

    /**
     * HTML document
     */
    HTML,

    /**
     * Markdown document
     */
    MARKDOWN,

    /**
     * JSON document
     */
    JSON,

    /**
     * XML document
     */
    XML,

    /**
     * Email message
     */
    EMAIL,

    /**
     * Article or blog post
     */
    ARTICLE,

    /**
     * Report or whitepaper
     */
    REPORT,

    /**
     * Academic paper
     */
    ACADEMIC,

    /**
     * Legal document
     */
    LEGAL,

    /**
     * Other document type
     */
    OTHER
}
