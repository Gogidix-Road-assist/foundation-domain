-- Chatbot Sessions Table
CREATE TABLE IF NOT EXISTS chatbot_sessions (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    tenant_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    channel VARCHAR(100),
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_activity_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

-- Indexes for chatbot_sessions
CREATE INDEX IF NOT EXISTS idx_sessions_tenant_id ON chatbot_sessions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON chatbot_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_sessions_session_id ON chatbot_sessions(session_id);
CREATE INDEX IF NOT EXISTS idx_sessions_status ON chatbot_sessions(status);
CREATE INDEX IF NOT EXISTS idx_sessions_created_at ON chatbot_sessions(created_at);

-- Chat Messages Table
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    tenant_id VARCHAR(255) NOT NULL,
    session_id UUID NOT NULL,
    direction VARCHAR(50) NOT NULL,
    content VARCHAR(4000) NOT NULL,
    message_type VARCHAR(100),
    metadata JSONB,
    timestamp TIMESTAMP NOT NULL,
    sequence_number INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_messages_session FOREIGN KEY (session_id) REFERENCES chatbot_sessions(uuid) ON DELETE CASCADE
);

-- Indexes for chat_messages
CREATE INDEX IF NOT EXISTS idx_messages_tenant_id ON chat_messages(tenant_id);
CREATE INDEX IF NOT EXISTS idx_messages_session_id ON chat_messages(session_id);
CREATE INDEX IF NOT EXISTS idx_messages_direction ON chat_messages(direction);
CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON chat_messages(timestamp);

-- Chatbot Contexts Table
CREATE TABLE IF NOT EXISTS chatbot_contexts (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    tenant_id VARCHAR(255) NOT NULL,
    session_id UUID NOT NULL,
    context_key VARCHAR(255) NOT NULL,
    context_value TEXT,
    context_type VARCHAR(100),
    additional_context JSONB,
    ttl INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_contexts_session FOREIGN KEY (session_id) REFERENCES chatbot_sessions(uuid) ON DELETE CASCADE
);

-- Indexes for chatbot_contexts
CREATE INDEX IF NOT EXISTS idx_contexts_tenant_id ON chatbot_contexts(tenant_id);
CREATE INDEX IF NOT EXISTS idx_contexts_session_id ON chatbot_contexts(session_id);
CREATE INDEX IF NOT EXISTS idx_contexts_context_key ON chatbot_contexts(context_key);
CREATE INDEX IF NOT EXISTS idx_contexts_updated_at ON chatbot_contexts(updated_at);

-- Conversation Flows Table
CREATE TABLE IF NOT EXISTS conversation_flows (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    tenant_id VARCHAR(255) NOT NULL,
    session_id UUID NOT NULL,
    current_state VARCHAR(255),
    previous_state VARCHAR(255),
    next_state VARCHAR(255),
    flow_type VARCHAR(100),
    flow_parameters JSONB,
    step_number INTEGER,
    state_entered_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_flows_session FOREIGN KEY (session_id) REFERENCES chatbot_sessions(uuid) ON DELETE CASCADE
);

-- Indexes for conversation_flows
CREATE INDEX IF NOT EXISTS idx_flows_tenant_id ON conversation_flows(tenant_id);
CREATE INDEX IF NOT EXISTS idx_flows_session_id ON conversation_flows(session_id);
CREATE INDEX IF NOT EXISTS idx_flows_current_state ON conversation_flows(current_state);

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers for updated_at
CREATE TRIGGER update_chatbot_sessions_updated_at
    BEFORE UPDATE ON chatbot_sessions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_chatbot_contexts_updated_at
    BEFORE UPDATE ON chatbot_contexts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_conversation_flows_updated_at
    BEFORE UPDATE ON conversation_flows
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
