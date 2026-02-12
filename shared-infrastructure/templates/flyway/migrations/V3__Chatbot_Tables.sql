-- ===================================================================
-- Flyway Migration Script - Chatbot Service Tables
-- ===================================================================
-- Description: Creates tables specific to AI Chatbot Service
-- Service: ai-chatbot-service
-- Version: V3
-- ===================================================================

-- ===================================================================
-- Chat session table
-- ===================================================================

CREATE TABLE IF NOT EXISTS chatbot_session (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    title VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    provider VARCHAR(50) NOT NULL DEFAULT 'deepseek',
    context JSONB DEFAULT '{}',
    metadata JSONB DEFAULT '{}',
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP,
    INDEX idx_session_user (user_id),
    INDEX idx_session_tenant (tenant_id),
    INDEX idx_session_status (status)
);

-- ===================================================================
-- Chat message table
-- ===================================================================

CREATE TABLE IF NOT EXISTS chatbot_message (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    message_id VARCHAR(100) UNIQUE NOT NULL,
    session_id VARCHAR(100) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('user', 'assistant', 'system')),
    content TEXT NOT NULL,
    tokens_used INTEGER DEFAULT 0,
    model VARCHAR(100),
    provider VARCHAR(50) DEFAULT 'deepseek',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES chatbot_session(session_id) ON DELETE CASCADE,
    INDEX idx_message_session (session_id),
    INDEX idx_message_user (user_id),
    INDEX idx_message_created (created_at)
);

-- ===================================================================
-- Rate limiting for chatbot
-- ===================================================================

CREATE TABLE IF NOT EXISTS chatbot_rate_limit (
    user_id VARCHAR(100) PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    requests_count INTEGER NOT NULL DEFAULT 0,
    window_start TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_rate_limit_tenant (tenant_id)
);

-- ===================================================================
-- Comments
-- ===================================================================

COMMENT ON TABLE chatbot_session IS 'Stores chatbot session information';
COMMENT ON TABLE chatbot_message IS 'Stores individual chat messages';
COMMENT ON TABLE chatbot_rate_limit IS 'Rate limiting for chatbot API';
