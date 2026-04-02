-- ===================================================================
-- Flyway Migration Script - Sentiment Analysis Service Tables
-- ===================================================================
-- Description: Creates tables specific to Sentiment Analysis Service
-- Service: ai-sentiment-analysis-service
-- Version: V4
-- ===================================================================

-- ===================================================================
-- Sentiment analysis result table
-- ===================================================================

CREATE TABLE IF NOT EXISTS sentiment_analysis (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    analysis_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    source_id VARCHAR(100),
    content TEXT NOT NULL,
    language VARCHAR(10) DEFAULT 'en',
    sentiment VARCHAR(20) NOT NULL CHECK (sentiment IN ('positive', 'negative', 'neutral')),
    confidence DECIMAL(5,4),
    sentiment_score DECIMAL(5,4) CHECK (sentiment_score BETWEEN -1 AND 1),
    emotions JSONB,
    aspects JSONB,
    keywords JSONB,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sentiment_user (user_id),
    INDEX idx_sentiment_tenant (tenant_id),
    INDEX idx_sentiment_result (sentiment),
    INDEX idx_sentiment_created (created_at)
);

-- ===================================================================
-- Sentiment analysis batch table
-- ===================================================================

CREATE TABLE IF NOT EXISTS sentiment_batch (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    batch_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PROCESSING',
    total_items INTEGER NOT NULL,
    processed_items INTEGER DEFAULT 0,
    results JSONB DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    INDEX idx_batch_user (user_id),
    INDEX idx_batch_tenant (tenant_id),
    INDEX idx_batch_status (status)
);

-- ===================================================================
-- Aspect-based sentiment table
-- ===================================================================

CREATE TABLE IF NOT EXISTS sentiment_aspect (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    analysis_id VARCHAR(100) NOT NULL,
    aspect_name VARCHAR(100) NOT NULL,
    aspect_sentiment VARCHAR(20) NOT NULL,
    confidence DECIMAL(5,4),
    FOREIGN KEY (analysis_id) REFERENCES sentiment_analysis(analysis_id) ON DELETE CASCADE,
    INDEX idx_aspect_analysis (analysis_id),
    INDEX idx_aspect_name (aspect_name)
);

-- ===================================================================
-- Comments
-- ===================================================================

COMMENT ON TABLE sentiment_analysis IS 'Stores sentiment analysis results';
COMMENT ON TABLE sentiment_batch IS 'Batch sentiment analysis jobs';
COMMENT ON TABLE sentiment_aspect IS 'Aspect-based sentiment analysis details';
