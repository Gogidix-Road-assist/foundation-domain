-- ===================================================================
-- Flyway Migration Script - Data Prediction Service Tables
-- ===================================================================
-- Description: Creates tables specific to Data Prediction Service
-- Service: ai-data-prediction-service
-- Version: V7
-- ===================================================================

-- ===================================================================
-- Prediction result table
-- ===================================================================

CREATE TABLE IF NOT EXISTS data_prediction (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    prediction_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    prediction_type VARCHAR(50) NOT NULL,
    model_id VARCHAR(100),
    model_version VARCHAR(50),
    input_data JSONB NOT NULL,
    prediction_result JSONB NOT NULL,
    confidence DECIMAL(5,4),
    probability JSONB,
    features_importance JSONB,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_prediction_user (user_id),
    INDEX idx_prediction_tenant (tenant_id),
    INDEX idx_prediction_type (prediction_type),
    INDEX idx_prediction_created (created_at)
);

-- ===================================================================
-- ML Model table
-- ===================================================================

CREATE TABLE IF NOT EXISTS ml_model (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    model_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    model_name VARCHAR(255) NOT NULL,
    model_type VARCHAR(50) NOT NULL,
    algorithm VARCHAR(100),
    version VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'TRAINING',
    training_data_size INTEGER,
    feature_count INTEGER,
    hyperparameters JSONB DEFAULT '{}',
    performance_metrics JSONB,
    training_started_at TIMESTAMP,
    training_completed_at TIMESTAMP,
    deployed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_model_user (user_id),
    INDEX idx_model_tenant (tenant_id),
    INDEX idx_model_type (model_type),
    INDEX idx_model_status (status)
);

-- ===================================================================
-- Model training job table
-- ===================================================================

CREATE TABLE IF NOT EXISTS model_training_job (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    model_id VARCHAR(100),
    training_data_source JSONB NOT NULL,
    algorithm VARCHAR(100),
    hyperparameters JSONB DEFAULT '{}',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    progress INTEGER DEFAULT 0 CHECK (progress >= 0 AND progress <= 100),
    epochs_completed INTEGER DEFAULT 0,
    total_epochs INTEGER,
    loss DECIMAL(10,6),
    accuracy DECIMAL(5,4),
    validation_metrics JSONB,
    error_message TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_training_user (user_id),
    INDEX idx_training_tenant (tenant_id),
    INDEX idx_training_status (status)
);

-- ===================================================================
-- Anomaly detection result table
-- ===================================================================

CREATE TABLE IF NOT EXISTS anomaly_detection (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    detection_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    dataset_id VARCHAR(100) NOT NULL,
    model_id VARCHAR(100),
    algorithm VARCHAR(100),
    anomalies_detected JSONB NOT NULL DEFAULT '[]',
    anomaly_count INTEGER DEFAULT 0,
    contamination_rate DECIMAL(5,4),
    confidence_threshold DECIMAL(5,4),
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_anomaly_user (user_id),
    INDEX idx_anomaly_tenant (tenant_id),
    INDEX idx_anomaly_dataset (dataset_id),
    INDEX idx_anomaly_created (created_at)
);

-- ===================================================================
-- Forecasting result table
-- ===================================================================

CREATE TABLE IF NOT EXISTS forecast_result (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    forecast_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    target_column VARCHAR(100) NOT NULL,
    forecast_horizon INTEGER NOT NULL,
    forecast_method VARCHAR(50),
    forecast_values JSONB NOT NULL,
    confidence_intervals JSONB,
    seasonality_components JSONB,
    trend_components JSONB,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_forecast_user (user_id),
    INDEX idx_forecast_tenant (tenant_id),
    INDEX idx_forecast_target (target_column)
);

-- ===================================================================
-- Comments
-- ===================================================================

COMMENT ON TABLE data_prediction IS 'Stores prediction results';
COMMENT ON TABLE ml_model IS 'Stores trained ML models';
COMMENT ON TABLE model_training_job IS 'Tracks model training jobs';
COMMENT ON TABLE anomaly_detection IS 'Stores anomaly detection results';
COMMENT ON TABLE forecast_result IS 'Stores forecasting results';
