-- ============================================================================
-- Mechanics-Insurance Bridge Service Database Migration
-- Creates tables for claim validation records
-- Version: V1
-- Author: Claude (Gogidix Technology)
-- ============================================================================

-- ============================================================================
-- TABLE: claim_validations
-- Stores validation records for insurance claims
-- ============================================================================
CREATE TABLE IF NOT EXISTS claim_validations (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Claim Information
    claim_id VARCHAR(100) NOT NULL,
    policy_number VARCHAR(100) NOT NULL,
    customer_id VARCHAR(100),
    vehicle_registration VARCHAR(50),

    -- Service Information
    workshop_id VARCHAR(100),
    mechanic_id VARCHAR(100),

    -- Financial Information
    estimated_cost NUMERIC(12, 2) NOT NULL,
    approved_amount NUMERIC(12, 2),

    -- Validation Results
    valid BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    rejection_reason VARCHAR(500),

    -- Policy Validation Details
    policy_active BOOLEAN,
    coverage_valid BOOLEAN,
    within_limit BOOLEAN,
    workshop_approved BOOLEAN,
    mechanic_certified BOOLEAN,

    -- Coverage Details
    coverage_type VARCHAR(50),
    coverage_limit NUMERIC(12, 2),
    remaining_limit NUMERIC(12, 2),
    deductible NUMERIC(12, 2),

    -- Validation Metadata
    validated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    validated_by VARCHAR(100),
    validation_reference VARCHAR(100),
    booking_id VARCHAR(100),

    -- Audit Fields (from BaseEntity)
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    tenant_id UUID,

    -- Constraints
    CONSTRAINT chk_claim_validations_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'REQUIRES_INFO', 'CANCELLED')),
    CONSTRAINT chk_claim_validations_costs CHECK (estimated_cost >= 0 AND (approved_amount IS NULL OR approved_amount >= 0))
);

-- ============================================================================
-- INDEXES: claim_validations
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_claim_id ON claim_validations(claim_id, deleted) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_policy_number ON claim_validations(policy_number, deleted) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_workshop_id ON claim_validations(workshop_id, deleted) WHERE deleted = FALSE AND workshop_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_mechanic_id ON claim_validations(mechanic_id, deleted) WHERE deleted = FALSE AND mechanic_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_status ON claim_validations(status, deleted) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_validated_at ON claim_validations(validated_at DESC, deleted) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_created_at ON claim_validations(created_at DESC, deleted) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_validation_reference ON claim_validations(validation_reference, deleted) WHERE deleted = FALSE AND validation_reference IS NOT NULL;

-- Composite index for workshop analytics
CREATE INDEX IF NOT EXISTS idx_workshop_stats ON claim_validations(workshop_id, validated_at, valid, deleted)
WHERE deleted = FALSE AND workshop_id IS NOT NULL;

-- ============================================================================
-- VIEWS: Validation Summary
-- ============================================================================
CREATE OR REPLACE VIEW v_validation_summary AS
SELECT
    DATE(cv.validated_at) as validation_date,
    COUNT(*) as total_validations,
    COUNT(*) FILTER (WHERE cv.valid = TRUE) as approved_count,
    COUNT(*) FILTER (WHERE cv.valid = FALSE) as rejected_count,
    COUNT(*) FILTER (WHERE cv.status = 'PENDING') as pending_count,
    COALESCE(SUM(cv.approved_amount), 0) as total_approved_amount,
    COALESCE(AVG(cv.approved_amount), 0) as average_approved_amount
FROM claim_validations cv
WHERE cv.deleted = FALSE
GROUP BY DATE(cv.validated_at)
ORDER BY validation_date DESC;

-- ============================================================================
-- VIEWS: Workshop Performance
-- ============================================================================
CREATE OR REPLACE VIEW v_workshop_performance AS
SELECT
    cv.workshop_id,
    COUNT(*) as total_validations,
    COUNT(*) FILTER (WHERE cv.valid = TRUE) as approved_count,
    COUNT(*) FILTER (WHERE cv.valid = FALSE) as rejected_count,
    ROUND(100.0 * COUNT(*) FILTER (WHERE cv.valid = TRUE) / NULLIF(COUNT(*), 0), 2) as approval_rate_pct,
    COALESCE(SUM(cv.approved_amount), 0) as total_approved_amount
FROM claim_validations cv
WHERE cv.deleted = FALSE
  AND cv.workshop_id IS NOT NULL
  AND cv.validated_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY cv.workshop_id
ORDER BY approval_rate_pct DESC;

-- ============================================================================
-- FUNCTIONS: Get validation statistics for a workshop
-- ============================================================================
CREATE OR REPLACE FUNCTION get_workshop_validation_stats(
    p_workshop_id VARCHAR,
    p_days INTEGER DEFAULT 30
) RETURNS TABLE (
    total_validations BIGINT,
    approved_count BIGINT,
    rejected_count BIGINT,
    pending_count BIGINT,
    approval_rate NUMERIC,
    total_approved_amount NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(*)::BIGINT as total_validations,
        COUNT(*) FILTER (WHERE cv.valid = TRUE)::BIGINT as approved_count,
        COUNT(*) FILTER (WHERE cv.valid = FALSE)::BIGINT as rejected_count,
        COUNT(*) FILTER (WHERE cv.status = 'PENDING')::BIGINT as pending_count,
        ROUND(100.0 * COUNT(*) FILTER (WHERE cv.valid = TRUE) / NULLIF(COUNT(*), 0), 2) as approval_rate,
        COALESCE(SUM(cv.approved_amount), 0) as total_approved_amount
    FROM claim_validations cv
    WHERE cv.deleted = FALSE
      AND cv.workshop_id = p_workshop_id
      AND cv.validated_at >= CURRENT_TIMESTAMP - (p_days || ' days')::INTERVAL;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- TRIGGER: Auto-update updated_at
-- ============================================================================
CREATE OR REPLACE FUNCTION update_claim_validations_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_claim_validations_updated_at
    BEFORE UPDATE ON claim_validations
    FOR EACH ROW
    EXECUTE FUNCTION update_claim_validations_updated_at();

-- ============================================================================
-- TABLE COMMENTS
-- ============================================================================
COMMENT ON TABLE claim_validations IS 'Claim validation records for Mechanics-Insurance bridge';
COMMENT ON COLUMN claim_validations.claim_id IS 'Insurance claim ID';
COMMENT ON COLUMN claim_validations.policy_number IS 'Insurance policy number';
COMMENT ON COLUMN claim_validations.workshop_id IS 'Workshop performing the service';
COMMENT ON COLUMN claim_validations.mechanic_id IS 'Mechanic performing the service';
COMMENT ON COLUMN claim_validations.estimated_cost IS 'Estimated repair cost';
COMMENT ON COLUMN claim_validations.approved_amount IS 'Approved claim amount after validation';
COMMENT ON COLUMN claim_validations.valid IS 'Whether the claim passed validation';
COMMENT ON COLUMN claim_validations.status IS 'Validation status: PENDING, APPROVED, REJECTED, REQUIRES_INFO, CANCELLED';
COMMENT ON COLUMN claim_validations.workshop_approved IS 'Whether the workshop is approved for insurance work';
COMMENT ON COLUMN claim_validations.mechanic_certified IS 'Whether the mechanic is certified';

-- ============================================================================
-- VACUUM: Analyze tables for query optimization
-- ============================================================================
ANALYZE claim_validations;
