-- ===================================================================
-- Flyway Migration Script - Base Tables
-- ===================================================================
-- Description: Creates base tables for AI Service
-- Service: Template for all AI services
-- Version: V1
-- ===================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ===================================================================
-- Audit and Metadata Tables
-- ===================================================================

-- Service health check table
CREATE TABLE IF NOT EXISTS service_health (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    service_name VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    last_check TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB
);

-- Request tracking table for audit
CREATE TABLE IF NOT EXISTS request_audit (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    request_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    method VARCHAR(10) NOT NULL,
    request_body JSONB,
    response_status INTEGER,
    response_time_ms INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_request_audit_user (user_id),
    INDEX idx_request_audit_tenant (tenant_id),
    INDEX idx_request_audit_created (created_at)
);

-- API usage tracking table
CREATE TABLE IF NOT EXISTS api_usage (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    request_count INTEGER NOT NULL DEFAULT 0,
    last_used TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date DATE NOT NULL DEFAULT CURRENT_DATE,
    UNIQUE(user_id, tenant_id, endpoint, date),
    INDEX idx_api_usage_user_tenant (user_id, tenant_id),
    INDEX idx_api_usage_date (date)
);

-- ===================================================================
-- Error tracking table
-- ===================================================================

CREATE TABLE IF NOT EXISTS error_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    error_code VARCHAR(50) NOT NULL,
    error_message TEXT NOT NULL,
    stack_trace TEXT,
    user_id VARCHAR(100),
    tenant_id VARCHAR(100),
    endpoint VARCHAR(255),
    request_params JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_error_log_code (error_code),
    INDEX idx_error_log_user (user_id),
    INDEX idx_error_log_tenant (tenant_id),
    INDEX idx_error_log_created (created_at)
);

-- ===================================================================
-- User preferences table
-- ===================================================================

CREATE TABLE IF NOT EXISTS user_preferences (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(100) NOT NULL UNIQUE,
    tenant_id VARCHAR(100) NOT NULL,
    preferences JSONB NOT NULL DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_preferences_tenant (tenant_id)
);

-- ===================================================================
-- Rate limiting table
-- ===================================================================

CREATE TABLE IF NOT EXISTS rate_limit (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    request_count INTEGER NOT NULL DEFAULT 0,
    window_start TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    window_duration_seconds INTEGER NOT NULL DEFAULT 60,
    INDEX idx_rate_limit_user (user_id),
    INDEX idx_rate_limit_tenant (tenant_id),
    INDEX idx_rate_limit_window (window_start)
);

-- ===================================================================
-- Comments
-- ===================================================================

COMMENT ON TABLE service_health IS 'Tracks service health status';
COMMENT ON TABLE request_audit IS 'Audit log for all API requests';
COMMENT ON TABLE api_usage IS 'Tracks API usage per user/tenant/endpoint';
COMMENT ON TABLE error_log IS 'Centralized error logging';
COMMENT ON TABLE user_preferences IS 'User-specific preferences';
COMMENT ON TABLE rate_limit IS 'Rate limiting data';

-- ===================================================================
-- Triggers for updated_at
-- ===================================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_user_preferences_updated_at
    BEFORE UPDATE ON user_preferences
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
