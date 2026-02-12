-- Schema for ai-chatbot-service

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS chatbot_entity (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(500) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_chatbot_tenant_id ON chatbot_entity(tenant_id);
CREATE INDEX idx_chatbot_status ON chatbot_entity(status);

CREATE OR REPLACE FUNCTION update_chatbot_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_chatbot_updated_at
BEFORE UPDATE ON chatbot_entity
FOR EACH ROW
EXECUTE FUNCTION update_chatbot_updated_at();

COMMENT ON TABLE chatbot_entity IS 'Main entity table for chatbot';
COMMENT ON COLUMN chatbot_entity.tenant_id IS 'Tenant identifier for multi-tenancy';
