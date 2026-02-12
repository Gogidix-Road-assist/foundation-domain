-- ===================================================================
-- Flyway Migration Script - Image Recognition Service Tables
-- ===================================================================
-- Description: Creates tables specific to Image Recognition Service
-- Service: ai-image-recognition-service
-- Version: V6
-- ===================================================================

-- ===================================================================
-- Image recognition result table
-- ===================================================================

CREATE TABLE IF NOT EXISTS image_recognition (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recognition_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    image_id VARCHAR(100) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    image_url TEXT,
    analysis_types JSONB NOT NULL DEFAULT '[]',
    objects_detected JSONB DEFAULT '[]',
    labels JSONB DEFAULT '[]',
    text_detected JSONB DEFAULT '[]',
    faces_detected JSONB DEFAULT '[]',
    colors_dominant JSONB DEFAULT '[]',
    confidence DECIMAL(5,4),
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_recognition_user (user_id),
    INDEX idx_recognition_tenant (tenant_id),
    INDEX idx_recognition_image (image_id),
    INDEX idx_recognition_created (created_at)
);

-- ===================================================================
-- Detected objects table (for detailed object storage)
-- ===================================================================

CREATE TABLE IF NOT EXISTS detected_object (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recognition_id VARCHAR(100) NOT NULL,
    object_name VARCHAR(100) NOT NULL,
    confidence DECIMAL(5,4) NOT NULL,
    bounding_box JSONB NOT NULL,
    area INTEGER,
    FOREIGN KEY (recognition_id) REFERENCES image_recognition(recognition_id) ON DELETE CASCADE,
    INDEX idx_object_recognition (recognition_id),
    INDEX idx_object_name (object_name)
);

-- ===================================================================
-- Image analysis job table
-- ===================================================================

CREATE TABLE IF NOT EXISTS image_analysis_job (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id VARCHAR(100) UNIQUE NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    image_url TEXT NOT NULL,
    analysis_types JSONB NOT NULL DEFAULT '[]',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    progress INTEGER DEFAULT 0 CHECK (progress >= 0 AND progress <= 100),
    result_data JSONB,
    error_message TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_job_user (user_id),
    INDEX idx_job_tenant (tenant_id),
    INDEX idx_job_status (status)
);

-- ===================================================================
-- Image metadata cache table
-- ===================================================================

CREATE TABLE IF NOT EXISTS image_metadata (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    image_url TEXT UNIQUE NOT NULL,
    file_size BIGINT,
    width INTEGER,
    height INTEGER,
    format VARCHAR(20),
    color_space VARCHAR(20),
    has_transparency BOOLEAN DEFAULT FALSE,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_metadata_url (image_url)
);

-- ===================================================================
-- Comments
-- ===================================================================

COMMENT ON TABLE image_recognition IS 'Stores image recognition results';
COMMENT ON TABLE detected_object IS 'Detailed detected object information';
COMMENT ON TABLE image_analysis_job IS 'Image analysis job tracking';
COMMENT ON TABLE image_metadata IS 'Cached image metadata';
