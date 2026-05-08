/**
 * MongoDB Setup Script for Shared Infrastructure Domain
 * Clean Architecture - Single Database with Namespaced Collections
 *
 * Database: shared_infrastructure_dev
 * Collections: Organized with service prefixes
 */

const { MongoClient } = require('mongodb');

const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017';
const DATABASE_NAME = 'shared_infrastructure_dev';

// Collection definitions with service prefixes
const COLLECTIONS = {
    // Access Control Service
    access_control_policies: {
        indexes: [{ tenantId: 1 }, { resource: 1 }, { action: 1 }]
    },
    access_control_roles: {
        indexes: [{ tenantId: 1 }, { roleId: 1 }]
    },
    access_control_logs: {
        indexes: [{ tenantId: 1 }, { userId: 1 }, { timestamp: -1 }]
    },

    // Alerting Service
    alerting_alerts: {
        indexes: [{ tenantId: 1 }, { severity: 1 }, { status: 1 }, { createdAt: -1 }]
    },
    alerting_rules: {
        indexes: [{ tenantId: 1 }, { isActive: 1 }]
    },
    alerting_subscriptions: {
        indexes: [{ tenantId: 1 }, { userId: 1 }]
    },

    // Anti-Fraud Rules Service
    anti_fraud_rules: {
        indexes: [{ tenantId: 1 }, { ruleType: 1 }, { isActive: 1 }, { priority: -1 }]
    },
    anti_fraud_conditions: {
        indexes: [{ ruleId: 1 }]
    },
    anti_fraud_actions: {
        indexes: [{ ruleId: 1 }]
    },

    // Anti-Fraud Signals Service
    anti_fraud_signals: {
        indexes: [{ tenantId: 1 }, { signalType: 1 }, { severity: 1 }, { timestamp: -1 }]
    },
    anti_fraud_aggregations: {
        indexes: [{ tenantId: 1 }, { timeWindow: 1 }]
    },

    // API Gateway
    api_gateway_routes: {
        indexes: [{ path: 1 }, { isActive: 1 }]
    },
    api_gateway_configs: {
        indexes: [{ serviceId: 1 }]
    },
    api_gateway_rate_limits: {
        indexes: [{ routeId: 1 }]
    },
    api_gateway_logs: {
        indexes: [{ timestamp: -1 }, { routeId: 1 }]
    },

    // API Keys Service
    api_keys: {
        indexes: [{ tenantId: 1 }, { keyId: 1 }, { apiKey: 1 }, { isActive: 1 }, { expiresAt: 1 }]
    },
    api_keys_usage: {
        indexes: [{ keyId: 1 }, { timestamp: -1 }]
    },
    api_keys_scopes: {
        indexes: [{ keyId: 1 }]
    },

    // Audit Correlation Service
    audit_correlation_trails: {
        indexes: [{ tenantId: 1 }, { correlationId: 1 }, { timestamp: -1 }, { eventType: 1 }]
    },
    audit_correlation_maps: {
        indexes: [{ correlationId: 1 }]
    },
    audit_correlation_events: {
        indexes: [{ tenantId: 1 }, { timestamp: -1 }]
    },

    // Billing Service
    billing_invoices: {
        indexes: [{ tenantId: 1 }, { invoiceNumber: 1 }, { status: 1 }, { dueDate: 1 }]
    },
    billing_accounts: {
        indexes: [{ tenantId: 1 }, { accountId: 1 }]
    },
    billing_payment_methods: {
        indexes: [{ tenantId: 1 }, { isDefault: 1 }]
    },
    billing_records: {
        indexes: [{ tenantId: 1 }, { period: 1 }]
    },

    // Courier Adapter Service
    courier_configs: {
        indexes: [{ tenantId: 1 }, { courierName: 1 }]
    },
    courier_shipments: {
        indexes: [{ tenantId: 1 }, { trackingNumber: 1 }, { status: 1 }]
    },
    courier_delivery_events: {
        indexes: [{ shipmentId: 1 }, { timestamp: -1 }]
    },

    // Currency Converter Service
    currency_rates: {
        indexes: [{ fromCurrency: 1, toCurrency: 1 }, { effectiveDate: -1 }]
    },
    currency_configs: {
        indexes: [{ currencyCode: 1 }]
    },
    currency_conversions: {
        indexes: [{ tenantId: 1 }, { timestamp: -1 }]
    },

    // Database Indexing Service
    db_indexing_definitions: {
        indexes: [{ databaseName: 1 }, { collectionName: 1 }]
    },
    db_indexing_jobs: {
        indexes: [{ status: 1 }, { createdAt: -1 }]
    },

    // Database Management Service
    db_management_registrations: {
        indexes: [{ databaseName: 1 }]
    },
    db_management_backups: {
        indexes: [{ status: 1 }, { scheduledAt: 1 }]
    },
    db_management_migrations: {
        indexes: [{ timestamp: -1 }]
    },

    // Data Privacy Consent Service
    privacy_consent_records: {
        indexes: [{ tenantId: 1 }, { userId: 1 }, { consentType: 1 }, { isActive: 1 }]
    },
    privacy_consent_policies: {
        indexes: [{ tenantId: 1 }, { policyType: 1 }]
    },
    privacy_gdpr_requests: {
        indexes: [{ tenantId: 1 }, { requestType: 1 }, { status: 1 }]
    },

    // Event Audit Service
    event_audit_logs: {
        indexes: [{ tenantId: 1 }, { eventType: 1 }, { timestamp: -1 }, { userId: 1 }]
    },
    event_audit_snapshots: {
        indexes: [{ eventId: 1 }]
    },
    event_audit_queries: {
        indexes: [{ tenantId: 1 }, { executedAt: -1 }]
    },

    // Geo Location Service
    geo_locations: {
        indexes: [{ tenantId: 1 }, { latitude: 1, longitude: 1 }]
    },
    geo_fences: {
        indexes: [{ tenantId: 1 }, { isActive: 1 }]
    },
    geo_location_history: {
        indexes: [{ userId: 1 }, { timestamp: -1 }]
    },

    // Idempotency Service
    idempotency_keys: {
        indexes: [{ key: 1 }, { expiryAt: 1 }]
    },
    idempotency_responses: {
        indexes: [{ idempotencyKey: 1 }]
    },

    // Identity Access Service
    identity_access_tokens: {
        indexes: [{ userId: 1 }, { token: 1 }, { expiresAt: 1 }]
    },
    identity_refresh_tokens: {
        indexes: [{ userId: 1 }, { token: 1 }]
    },
    identity_sessions: {
        indexes: [{ userId: 1 }, { sessionId: 1 }]
    },
    identity_role_assignments: {
        indexes: [{ userId: 1 }, { roleId: 1 }]
    },

    // Identity Service
    identity_users: {
        indexes: [{ tenantId: 1 }, { email: 1 }, { username: 1 }, { status: 1 }]
    },
    identity_profiles: {
        indexes: [{ userId: 1 }]
    },
    identity_reset_tokens: {
        indexes: [{ token: 1 }, { expiresAt: 1 }]
    },

    // Insurer Adapter Service
    insurer_configs: {
        indexes: [{ tenantId: 1 }, { insurerCode: 1 }]
    },
    insurer_policy_syncs: {
        indexes: [{ tenantId: 1 }, { policyNumber: 1 }]
    },
    insurer_claims_syncs: {
        indexes: [{ tenantId: 1 }, { claimNumber: 1 }]
    },

    // Integration Adapters Service
    integration_configs: {
        indexes: [{ tenantId: 1 }, { adapterType: 1 }, { isActive: 1 }]
    },
    integration_logs: {
        indexes: [{ adapterId: 1 }, { timestamp: -1 }]
    },
    integration_webhooks: {
        indexes: [{ tenantId: 1 }]
    },

    // Logging Aggregation Service
    logging_entries: {
        indexes: [{ tenantId: 1 }, { level: 1 }, { timestamp: -1 }, { service: 1 }]
    },
    logging_aggregations: {
        indexes: [{ timeWindow: 1 }]
    },
    logging_filters: {
        indexes: [{ tenantId: 1 }]
    },

    // MFA Service
    mfa_configs: {
        indexes: [{ tenantId: 1 }, { userId: 1 }]
    },
    mfa_secrets: {
        indexes: [{ userId: 1 }]
    },
    mfa_tokens: {
        indexes: [{ userId: 1 }, { token: 1 }, { expiresAt: 1 }]
    },
    mfa_backup_codes: {
        indexes: [{ userId: 1 }]
    },

    // Notification Service
    notification_messages: {
        indexes: [{ tenantId: 1 }, { userId: 1 }, { status: 1 }, { createdAt: -1 }]
    },
    notification_templates: {
        indexes: [{ tenantId: 1 }, { templateType: 1 }]
    },
    notification_preferences: {
        indexes: [{ userId: 1 }]
    },
    notification_delivery_logs: {
        indexes: [{ notificationId: 1 }, { timestamp: -1 }]
    },

    // Onboarding Service
    onboarding_flows: {
        indexes: [{ tenantId: 1 }, { flowType: 1 }]
    },
    onboarding_steps: {
        indexes: [{ flowId: 1 }]
    },
    onboarding_user_states: {
        indexes: [{ userId: 1 }]
    },
    onboarding_checklists: {
        indexes: [{ tenantId: 1 }]
    },

    // Payments Adapter Service
    payments_gateway_configs: {
        indexes: [{ tenantId: 1 }, { gatewayName: 1 }]
    },
    payments_transactions: {
        indexes: [{ tenantId: 1 }, { transactionId: 1 }, { status: 1 }]
    },
    payments_mappings: {
        indexes: [{ tenantId: 1 }]
    },

    // Payment Service
    payment_records: {
        indexes: [{ tenantId: 1 }, { paymentReference: 1 }, { status: 1 }, { createdAt: -1 }]
    },
    payment_methods: {
        indexes: [{ tenantId: 1 }, { userId: 1 }, { isDefault: 1 }]
    },
    payment_schedules: {
        indexes: [{ tenantId: 1 }, { nextPaymentDate: 1 }]
    },

    // Policy Engine Service
    policy_rules: {
        indexes: [{ tenantId: 1 }, { ruleType: 1 }, { isActive: 1 }, { priority: -1 }]
    },
    policy_evaluations: {
        indexes: [{ tenantId: 1 }, { timestamp: -1 }]
    },
    policy_variables: {
        indexes: [{ tenantId: 1 }]
    },

    // Pricing Service
    pricing_plans: {
        indexes: [{ tenantId: 1 }, { planCode: 1 }]
    },
    pricing_rules: {
        indexes: [{ tenantId: 1 }, { productId: 1 }]
    },
    pricing_calculations: {
        indexes: [{ tenantId: 1 }, { timestamp: -1 }]
    },
    pricing_discounts: {
        indexes: [{ tenantId: 1 }, { isActive: 1 }]
    },

    // Rate Limiting Service
    rate_limits: {
        indexes: [{ key: 1 }, { expiryAt: 1 }]
    },
    rate_limit_rules: {
        indexes: [{ tenantId: 1 }, { endpoint: 1 }]
    },
    rate_usage_counters: {
        indexes: [{ key: 1 }]
    },

    // Reporting Read Model Service
    report_views: {
        indexes: [{ tenantId: 1 }, { reportType: 1 }, { generatedAt: -1 }]
    },
    report_snapshots: {
        indexes: [{ reportId: 1 }, { timestamp: -1 }]
    },
    report_aggregates: {
        indexes: [{ tenantId: 1 }, { dimension: 1 }]
    },

    // Request Routing Service
    routing_rules: {
        indexes: [{ tenantId: 1 }, { priority: -1 }, { isActive: 1 }]
    },
    routing_logs: {
        indexes: [{ timestamp: -1 }, { routeKey: 1 }]
    },
    routing_endpoints: {
        indexes: [{ serviceName: 1 }, { isActive: 1 }]
    },

    // RapidAssist Service
    rapidassist_requests: {
        indexes: [{ tenantId: 1 }, { requestId: 1 }, { status: 1 }, { createdAt: -1 }]
    },
    rapidassist_providers: {
        indexes: [{ tenantId: 1 }, { isActive: 1 }]
    },
    rapidassist_service_types: {
        indexes: [{ category: 1 }]
    },

    // Service Health Monitor
    health_checks: {
        indexes: [{ serviceName: 1 }, { timestamp: -1 }, { status: 1 }]
    },
    health_metrics: {
        indexes: [{ serviceName: 1 }, { timestamp: -1 }]
    },
    health_thresholds: {
        indexes: [{ serviceName: 1 }]
    },

    // Service Registry
    registry_services: {
        indexes: [{ serviceName: 1 }, { status: 1 }]
    },
    registry_instances: {
        indexes: [{ serviceName: 1 }, { host: 1, port: 1 }, { lastHeartbeat: -1 }]
    },
    registry_heartbeats: {
        indexes: [{ serviceName: 1 }, { timestamp: -1 }]
    },

    // Session Token Service
    session_tokens: {
        indexes: [{ sessionId: 1 }, { userId: 1 }, { expiresAt: 1 }]
    },
    session_activities: {
        indexes: [{ sessionId: 1 }, { timestamp: -1 }]
    },

    // Template Messaging Service
    messaging_templates: {
        indexes: [{ tenantId: 1 }, { templateCode: 1 }, { category: 1 }]
    },
    messaging_variables: {
        indexes: [{ templateId: 1 }]
    },
    messaging_rendered: {
        indexes: [{ templateId: 1 }, { createdAt: -1 }]
    },

    // Tenant Org Service
    tenant_orgs: {
        indexes: [{ tenantId: 1 }, { tenantCode: 1 }, { status: 1 }]
    },
    tenant_organizations: {
        indexes: [{ tenantId: 1 }, { orgId: 1 }]
    },
    tenant_configs: {
        indexes: [{ tenantId: 1 }]
    },
    tenant_subscriptions: {
        indexes: [{ tenantId: 1 }, { status: 1 }]
    },

    // User Profile Service
    user_profiles: {
        indexes: [{ tenantId: 1 }, { userId: 1 }, { email: 1 }]
    },
    user_preferences: {
        indexes: [{ userId: 1 }]
    },
    user_activities: {
        indexes: [{ userId: 1 }, { timestamp: -1 }]
    },

    // WAF Policy Service
    waf_rules: {
        indexes: [{ tenantId: 1 }, { ruleType: 1 }, { isActive: 1 }, { priority: -1 }]
    },
    waf_policies: {
        indexes: [{ tenantId: 1 }, { policyName: 1 }]
    },
    waf_logs: {
        indexes: [{ tenantId: 1 }, { timestamp: -1 }, { action: 1 }]
    },

    // Webhook Delivery Service
    webhook_configs: {
        indexes: [{ tenantId: 1 }, { webhookUrl: 1 }, { isActive: 1 }]
    },
    webhook_events: {
        indexes: [{ webhookId: 1 }, { eventType: 1 }]
    },
    webhook_deliveries: {
        indexes: [{ eventId: 1 }, { timestamp: -1 }, { status: 1 }]
    },
    webhook_dead_letters: {
        indexes: [{ webhookId: 1 }, { retryAfter: 1 }]
    }
};

async function setupCleanDatabase() {
    console.log('\n╔══════════════════════════════════════════════════════════════════════════════╗');
    console.log('║          MONGODB CLEAN SETUP - SHARED INFRASTRUCTURE DOMAIN                ║');
    console.log('╚══════════════════════════════════════════════════════════════════════════════╝\n');

    const client = new MongoClient(MONGODB_URI);

    try {
        await client.connect();
        console.log('✓ Connected to MongoDB successfully\n');

        const db = client.db(DATABASE_NAME);

        // Drop existing database if it exists
        console.log('⚠ Dropping existing database if exists...');
        await db.dropDatabase().catch(() => {});
        console.log('✓ Database cleaned\n');

        // Create collections with indexes
        console.log('Creating collections with service prefixes:\n');

        let collectionCount = 0;
        let indexCount = 0;

        for (const [collectionName, config] of Object.entries(COLLECTIONS)) {
            try {
                // Create collection
                await db.createCollection(collectionName);
                collectionCount++;

                // Create indexes
                if (config.indexes && config.indexes.length > 0) {
                    for (const indexSpec of config.indexes) {
                        await db.collection(collectionName).createIndex(indexSpec);
                        indexCount++;
                    }
                }

                console.log(`  ✓ ${collectionName} (${config.indexes.length} indexes)`);
            } catch (error) {
                console.log(`  ⚠ ${collectionName}: ${error.message}`);
            }
        }

        console.log(`\n╔══════════════════════════════════════════════════════════════════════════════╗`);
        console.log(`║                    SETUP COMPLETED SUCCESSFULLY!                            ║`);
        console.log(`╚══════════════════════════════════════════════════════════════════════════════╝\n`);

        console.log('Summary:');
        console.log(`  Database: ${DATABASE_NAME}`);
        console.log(`  Collections Created: ${collectionCount}`);
        console.log(`  Indexes Created: ${indexCount}`);
        console.log(`  Connection: ${MONGODB_URI}\n`);

        console.log('Collection Structure:');
        console.log('  ├── Access Control (3 collections)');
        console.log('  ├── Alerting (3 collections)');
        console.log('  ├── Anti-Fraud (5 collections)');
        console.log('  ├── API Gateway (4 collections)');
        console.log('  ├── API Keys (3 collections)');
        console.log('  ├── Audit (3 collections)');
        console.log('  ├── Billing (4 collections)');
        console.log('  ├── Courier (3 collections)');
        console.log('  ├── Currency (3 collections)');
        console.log('  ├── Database Mgmt (5 collections)');
        console.log('  ├── Privacy (3 collections)');
        console.log('  ├── Event Audit (3 collections)');
        console.log('  ├── Geo Location (3 collections)');
        console.log('  ├── Idempotency (2 collections)');
        console.log('  ├── Identity (6 collections)');
        console.log('  ├── Insurer (3 collections)');
        console.log('  ├── Integration (3 collections)');
        console.log('  ├── Logging (3 collections)');
        console.log('  ├── MFA (4 collections)');
        console.log('  ├── Notification (4 collections)');
        console.log('  ├── Onboarding (4 collections)');
        console.log('  ├── Payments (6 collections)');
        console.log('  ├── Policy Engine (3 collections)');
        console.log('  ├── Pricing (4 collections)');
        console.log('  ├── Rate Limiting (3 collections)');
        console.log('  ├── Reporting (3 collections)');
        console.log('  ├── Routing (3 collections)');
        console.log('  ├── RapidAssist (3 collections)');
        console.log('  ├── Health Monitor (3 collections)');
        console.log('  ├── Service Registry (3 collections)');
        console.log('  ├── Session (2 collections)');
        console.log('  ├── Messaging (3 collections)');
        console.log('  ├── Tenant (4 collections)');
        console.log('  ├── User Profile (3 collections)');
        console.log('  ├── WAF (3 collections)');
        console.log('  └── Webhook (4 collections)');
        console.log('\nTotal: 41 Services → 1 Database → 118 Collections\n');

        console.log('You can now connect MongoDB Compass to view:');
        console.log(`  Database: ${DATABASE_NAME}`);
        console.log(`  Connection: ${MONGODB_URI}\n`);

    } catch (error) {
        console.error('\n❌ ERROR during setup:', error.message);
        process.exit(1);
    } finally {
        await client.close();
        console.log('MongoDB connection closed.\n');
    }
}

// Execute
setupCleanDatabase();
