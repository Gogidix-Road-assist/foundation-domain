-- ===================================================================
-- Flyway Migration Script - Translation Service Tables
-- ===================================================================
-- Description: Creates tables specific to Translation Service
-- Service: ai-translation-service
-- Version: V5
-- ===================================================================

-- ===================================================================
-- Translation result table
-- ===================================================================

CREATE TABLE IF NOT EXISTS translation (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    translation_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    source_text TEXT NOT NULL,
    translated_text TEXT NOT NULL,
    source_language VARCHAR(10) NOT NULL,
    target_language VARCHAR(10) NOT NULL,
    translation_type VARCHAR(50) DEFAULT 'text',
    provider VARCHAR(50) DEFAULT 'openai',
    model VARCHAR(100),
    quality_score DECIMAL(5,4),
    confidence DECIMAL(5,4),
    character_count INTEGER,
    word_count INTEGER,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_translation_user (user_id),
    INDEX idx_translation_tenant (tenant_id),
    INDEX idx_translation_languages (source_language, target_language),
    INDEX idx_translation_created (created_at)
);

-- ===================================================================
-- Translation memory table
-- ===================================================================

CREATE TABLE IF NOT EXISTS translation_memory (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id VARCHAR(100) NOT NULL,
    source_language VARCHAR(10) NOT NULL,
    target_language VARCHAR(10) NOT NULL,
    source_text TEXT NOT NULL,
    translated_text TEXT NOT NULL,
    category VARCHAR(100),
    usage_count INTEGER DEFAULT 0,
    last_used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, source_language, target_language, source_text),
    INDEX idx_memory_tenant (tenant_id),
    INDEX idx_memory_languages (source_language, target_language),
    INDEX idx_memory_category (category)
);

-- ===================================================================
-- Real-time translation session table
-- ===================================================================

CREATE TABLE IF NOT EXISTS translation_session (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    source_language VARCHAR(10) NOT NULL,
    target_language VARCHAR(10) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    total_translations INTEGER DEFAULT 0,
    total_characters INTEGER DEFAULT 0,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP,
    INDEX idx_session_user (user_id),
    INDEX idx_session_tenant (tenant_id),
    INDEX idx_session_status (status)
);

-- ===================================================================
-- Translation feedback table
-- ===================================================================

CREATE TABLE IF NOT EXISTS translation_feedback (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    translation_id VARCHAR(100) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    is_correct BOOLEAN,
    suggested_translation TEXT,
    feedback_text TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (translation_id) REFERENCES translation(translation_id) ON DELETE CASCADE,
    INDEX idx_feedback_translation (translation_id),
    INDEX idx_feedback_user (user_id)
);

-- ===================================================================
-- Comments
-- ===================================================================

COMMENT ON TABLE translation IS 'Stores translation results';
COMMENT ON TABLE translation_memory IS 'Translation memory for reuse';
COMMENT ON TABLE translation_session IS 'Real-time translation sessions';
COMMENT ON TABLE translation_feedback IS 'User feedback on translations';
