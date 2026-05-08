-- ============================================================================
-- Mechanics-Vendors Bridge Service Database Migration
-- Creates tables for parts order tracking
-- Version: V1
-- Author: Claude (Gogidix Technology)
-- ============================================================================

-- ============================================================================
-- TABLE: parts_orders
-- Stores parts order records between Mechanics and Vendors domains
-- ============================================================================
CREATE TABLE IF NOT EXISTS parts_orders (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Order Information
    order_id VARCHAR(100) NOT NULL UNIQUE,
    workshop_id VARCHAR(100) NOT NULL,
    vendor_id VARCHAR(100) NOT NULL,
    vendor_name VARCHAR(255),

    -- Status and Priority
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(20),
    order_type VARCHAR(20),

    -- Financial Information
    total_amount NUMERIC(12, 2) NOT NULL,

    -- Shipping Information
    shipping_address VARCHAR(500),
    tracking_number VARCHAR(100),
    expected_delivery TIMESTAMP WITH TIME ZONE,

    -- Timestamps
    requested_at TIMESTAMP WITH TIME ZONE NOT NULL,
    confirmed_at TIMESTAMP WITH TIME ZONE,
    shipped_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,

    -- Additional Information
    internal_reference VARCHAR(100),
    special_instructions VARCHAR(1000),

    -- Auto-reorder flags
    auto_order BOOLEAN NOT NULL DEFAULT FALSE,
    reorder_reason VARCHAR(255),

    -- Audit Fields (from BaseEntity)
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    tenant_id UUID,

    -- Constraints
    CONSTRAINT chk_parts_orders_status CHECK (status IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'FAILED')),
    CONSTRAINT chk_parts_orders_type CHECK (order_type IN ('AUTO', 'MANUAL', 'URGENT')),
    CONSTRAINT chk_parts_orders_total CHECK (total_amount >= 0)
);

-- ============================================================================
-- INDEXES: parts_orders
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_order_id ON parts_orders(order_id, deleted) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_workshop_id ON parts_orders(workshop_id, deleted) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_vendor_id ON parts_orders(vendor_id, deleted) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_status ON parts_orders(status, deleted) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_requested_at ON parts_orders(requested_at DESC, deleted) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_created_at ON parts_orders(created_at DESC, deleted) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_auto_order ON parts_orders(auto_order, deleted) WHERE deleted = FALSE AND auto_order = TRUE;

-- Composite index for workshop pending orders
CREATE INDEX IF NOT EXISTS idx_workshop_pending ON parts_orders(workshop_id, status, requested_at, deleted)
WHERE deleted = FALSE AND status = 'PENDING';

-- ============================================================================
-- VIEWS: Order Summary
-- ============================================================================
CREATE OR REPLACE VIEW v_order_summary AS
SELECT
    DATE(po.requested_at) as order_date,
    COUNT(*) as total_orders,
    COUNT(*) FILTER (WHERE po.auto_order = TRUE) as auto_orders,
    COUNT(*) FILTER (WHERE po.auto_order = FALSE) as manual_orders,
    COALESCE(SUM(po.total_amount), 0) as total_amount,
    AVG(po.total_amount) as average_order_value,
    COUNT(*) FILTER (WHERE po.status = 'DELIVERED') as delivered_count,
    COUNT(*) FILTER (WHERE po.status = 'PENDING') as pending_count
FROM parts_orders po
WHERE po.deleted = FALSE
GROUP BY DATE(po.requested_at)
ORDER BY order_date DESC;

-- ============================================================================
-- VIEWS: Vendor Performance
-- ============================================================================
CREATE OR REPLACE VIEW v_vendor_performance AS
SELECT
    po.vendor_id,
    po.vendor_name,
    COUNT(*) as total_orders,
    COUNT(*) FILTER (WHERE po.status = 'DELIVERED') as delivered_count,
    ROUND(100.0 * COUNT(*) FILTER (WHERE po.status = 'DELIVERED') / NULLIF(COUNT(*), 0), 2) as delivery_success_rate,
    COALESCE(SUM(po.total_amount), 0) as total_revenue,
    AVG(po.total_amount) as average_order_value
FROM parts_orders po
WHERE po.deleted = FALSE
  AND po.requested_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY po.vendor_id, po.vendor_name
ORDER BY total_revenue DESC;

-- ============================================================================
-- FUNCTIONS: Get workshop order statistics
-- ============================================================================
CREATE OR REPLACE FUNCTION get_workshop_order_stats(
    p_workshop_id VARCHAR,
    p_days INTEGER DEFAULT 30
) RETURNS TABLE (
    total_orders BIGINT,
    pending_orders BIGINT,
    delivered_orders BIGINT,
    auto_orders BIGINT,
    total_spent NUMERIC,
    average_order_value NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(*)::BIGINT as total_orders,
        COUNT(*) FILTER (WHERE po.status = 'PENDING')::BIGINT as pending_orders,
        COUNT(*) FILTER (WHERE po.status = 'DELIVERED')::BIGINT as delivered_orders,
        COUNT(*) FILTER (WHERE po.auto_order = TRUE)::BIGINT as auto_orders,
        COALESCE(SUM(po.total_amount), 0) as total_spent,
        AVG(po.total_amount) as average_order_value
    FROM parts_orders po
    WHERE po.deleted = FALSE
      AND po.workshop_id = p_workshop_id
      AND po.requested_at >= CURRENT_TIMESTAMP - (p_days || ' days')::INTERVAL;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- TRIGGER: Auto-update updated_at
-- ============================================================================
CREATE OR REPLACE FUNCTION update_parts_orders_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_parts_orders_updated_at
    BEFORE UPDATE ON parts_orders
    FOR EACH ROW
    EXECUTE FUNCTION update_parts_orders_updated_at();

-- ============================================================================
-- TABLE COMMENTS
-- ============================================================================
COMMENT ON TABLE parts_orders IS 'Parts order records for Mechanics-Vendors bridge';
COMMENT ON COLUMN parts_orders.order_id IS 'Unique order identifier';
COMMENT ON COLUMN parts_orders.workshop_id IS 'Workshop placing the order';
COMMENT ON COLUMN parts_orders.vendor_id IS 'Vendor fulfilling the order';
COMMENT ON COLUMN parts_orders.auto_order IS 'Whether this was an auto-reorder';
COMMENT ON COLUMN parts_orders.reorder_reason IS 'Reason for auto-reorder: LOW_STOCK, EMERGENCY, STOCKOUT';

-- ============================================================================
-- VACUUM: Analyze tables for query optimization
-- ============================================================================
ANALYZE parts_orders;
