-- ===================================================================
-- Flyway Migration Script - AI Service Specific Tables
-- ===================================================================
-- Description: Creates core tables for AI processing results
-- Service: Template for all AI services
-- Version: V2
-- ===================================================================

-- ===================================================================
-- AI Job/Task tracking table
-- ===================================================================

CREATE TABLE IF NOT EXISTS ai_job (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    job_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    input_data JSONB,
    output_data JSONB,
    error_message TEXT,
    progress INTEGER DEFAULT 0 CHECK (progress >= 0 AND progress <= 100),
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ai_job_user (user_id),
    INDEX idx_ai_job_tenant (tenant_id),
    INDEX idx_ai_job_status (status),
    INDEX idx_ai_job_type (job_type),
    INDEX idx_ai_job_created (created_at)
);

-- ===================================================================
-- AI Model tracking table
-- ===================================================================

CREATE TABLE IF NOT EXISTS ai_model (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    model_id VARCHAR(100) UNIQUE NOT NULL,
    model_name VARCHAR(255) NOT NULL,
    model_type VARCHAR(100) NOT NULL,
    version VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    config JSONB NOT NULL DEFAULT '{}',
    performance_metrics JSONB,
    deployed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ai_model_type (model_type),
    INDEX idx_ai_model_status (status)
);

-- ===================================================================
-- AI result caching table
-- ===================================================================

CREATE TABLE IF NOT EXISTS ai_result_cache (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cache_key VARCHAR(500) UNIQUE NOT NULL,
    user_id VARCHAR(100),
    tenant_id VARCHAR(100),
    job_type VARCHAR(50) NOT NULL,
    result_data JSONB NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    hit_count INTEGER DEFAULT 0,
    INDEX idx_cache_key (cache_key),
    INDEX idx_cache_expires (expires_at),
    INDEX idx_cache_user (user_id),
    INDEX idx_cache_tenant (tenant_id)
);

-- ===================================================================
-- AI feedback table
-- ===================================================================

CREATE TABLE IF NOT EXISTS ai_feedback (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id VARCHAR(100) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    feedback_text TEXT,
    is_positive BOOLEAN,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES ai_job(job_id) ON DELETE CASCADE,
    INDEX idx_feedback_job (job_id),
    INDEX idx_feedback_user (user_id),
    INDEX idx_feedback_tenant (tenant_id),
    INDEX idx_feedback_created (created_at)
);

-- ===================================================================
-- Comments
-- ===================================================================

COMMENT ON TABLE ai_job IS 'Tracks AI processing jobs/tasks';
COMMENT ON TABLE ai_model IS 'Stores AI model information and versions';
COMMENT ON TABLE ai_result_cache IS 'Caches AI results for performance';
COMMENT ON TABLE ai_feedback IS 'User feedback on AI results';

-- ===================================================================
-- Triggers
-- ===================================================================

CREATE TRIGGER update_ai_job_updated_at
    BEFORE UPDATE ON ai_job
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_ai_model_updated_at
    BEFORE UPDATE ON ai_model
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ===================================================================
-- Index for cache cleanup
-- ===================================================================

CREATE OR REPLACE FUNCTION cleanup_expired_cache()
RETURNS void AS $$
BEGIN
    DELETE FROM ai_result_cache
    WHERE expires_at < CURRENT_TIMESTAMP;
END;
$$ LANGUAGE plpgsql;
