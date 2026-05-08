-- Multi-Tenancy Index Enhancement
-- Version: 2.0
-- Description: Enhance tenant_id indexes for optimal query performance
-- This script creates composite indexes for common tenant-based queries

-- Note: This is a reference migration that should be adapted per service
-- Individual services should create their own V2__add_tenant_indexes.sql
-- based on their specific table structures

-- Template for composite indexes (tenant_id + commonly queried columns)
-- Uncomment and modify as needed for each service:

-- Example for policy-service:
-- CREATE INDEX IF NOT EXISTS idx_policies_tenant_status ON policies(tenant_id, policy_status);
-- CREATE INDEX IF NOT EXISTS idx_policies_tenant_customer ON policies(tenant_id, customer_id);
-- CREATE INDEX IF NOT EXISTS idx_policies_tenant_effective ON policies(tenant_id, effective_date);
-- CREATE INDEX IF NOT EXISTS idx_policy_versions_tenant_policy ON policy_versions(tenant_id, policy_id);
-- CREATE INDEX IF NOT EXISTS idx_insured_items_tenant_policy ON insured_items(tenant_id, policy_id);
-- CREATE INDEX IF NOT EXISTS idx_insured_items_tenant_type ON insured_items(tenant_id, item_type);
-- CREATE INDEX IF NOT EXISTS idx_policy_coverages_tenant_policy ON policy_coverages(tenant_id, policy_id);

-- Example for coverage-service:
-- CREATE INDEX IF NOT EXISTS idx_coverage_definitions_tenant_code ON coverage_definitions(tenant_id, coverage_code);
-- CREATE INDEX IF NOT EXISTS idx_coverage_definitions_tenant_active ON coverage_definitions(tenant_id, is_active);
-- CREATE INDEX IF NOT EXISTS idx_coverage_limits_tenant_coverage ON coverage_limits(tenant_id, coverage_id);

-- Example for product-catalog-service:
-- CREATE INDEX IF NOT EXISTS idx_products_tenant_code ON products(tenant_id, product_code);
-- CREATE INDEX IF NOT EXISTS idx_products_tenant_active ON products(tenant_id, is_active);
-- CREATE INDEX IF NOT EXISTS idx_product_variants_tenant_product ON product_variants(tenant_id, product_id);
