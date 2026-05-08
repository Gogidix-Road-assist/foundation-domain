/**
 * MongoDB Setup Script for Shared Infrastructure Services (41 Services)
 *
 * This script creates all databases, collections, and indexes for the 41
 * shared-infrastructure domain services using Node.js.
 *
 * INSTRUCTIONS:
 * 1. Ensure MongoDB is running on localhost:27017
 * 2. Run: node setup-shared-infrastructure-mongodb.js
 * 3. Open MongoDB Compass to verify databases created
 *
 * Prerequisites: npm install mongodb uuid
 */

const { MongoClient } = require('mongodb');
const { v4: uuidv4 } = require('uuid');

// MongoDB Connection Configuration
const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017';

// Database Names for all 41 services
const DATABASES = {
    ACCESS_CONTROL: 'rapid_assist_access_control_dev',
    ALERTING: 'rapid_assist_alerting_dev',
    ANTI_FRAUD_RULES: 'rapid_assist_anti_fraud_rules_dev',
    ANTI_FRAUD_SIGNALS: 'rapid_assist_anti_fraud_signals_dev',
    API_GATEWAY: 'rapid_assist_gateway_dev',
    API_KEYS: 'rapid_assist_api_keys_dev',
    AUDIT_CORRELATION: 'rapid_assist_audit_correlation_dev',
    BILLING: 'rapid_assist_billing_dev',
    COURIER_ADAPTER: 'rapid_assist_courier_adapter_dev',
    CURRENCY_CONVERTER: 'rapid_assist_currency_converter_dev',
    DATABASE_INDEXING: 'rapid_assist_database_indexing_dev',
    DATABASE_MANAGEMENT: 'rapid_assist_database_management_dev',
    DATA_PRIVACY_CONSENT: 'rapid_assist_data_privacy_consent_dev',
    EVENT_AUDIT: 'rapid_assist_event_audit_dev',
    GEO_LOCATION: 'rapid_assist_geo_location_dev',
    IDEMPOTENCY: 'rapid_assist_idempotency_dev',
    IDENTITY_ACCESS: 'rapid_assist_identity_access_dev',
    IDENTITY: 'rapid_assist_identity_dev',
    INSURER_ADAPTER: 'rapid_assist_insurer_adapter_dev',
    INTEGRATION_ADAPTERS: 'rapid_assist_integration_adapters_dev',
    LOGGING_AGGREGATION: 'rapid_assist_logging_aggregation_dev',
    MFA: 'rapid_assist_mfa_dev',
    NOTIFICATION: 'rapid_assist_notification_dev',
    ONBOARDING: 'rapid_assist_onboarding_dev',
    PAYMENTS_ADAPTER: 'rapid_assist_payments_adapter_dev',
    PAYMENT: 'rapid_assist_payment_dev',
    POLICY_ENGINE: 'rapid_assist_policy_engine_dev',
    PRICING: 'rapid_assist_pricing_dev',
    RATE_LIMITING: 'rapid_assist_rate_limiting_dev',
    REPORTING_READ_MODEL: 'rapid_assist_reporting_read_model_dev',
    REQUEST_ROUTING: 'rapid_assist_request_routing_dev',
    RAPIDASSIST: 'rapidassist',
    SERVICE_HEALTH_MONITOR: 'rapid_assist_service_health_monitor_dev',
    SERVICE_REGISTRY: 'rapid_assist_registry_dev',
    SESSION_TOKEN: 'rapid_assist_session_token_dev',
    TEMPLATE_MESSAGING: 'rapid_assist_template_messaging_dev',
    TENANT_ORG: 'rapid_assist_tenant_org_dev',
    USER_PROFILE: 'rapid_assist_user_profile_dev',
    WAF_POLICY: 'rapid_assist_waf_policy_dev',
    WEBHOOK_DELIVERY: 'rapid_assist_webhook_delivery_dev'
};

// Helper function for UUID
const uuid = () => uuidv4();
const now = new Date();

// ============================================================
// SERVICE DATABASE SETUP FUNCTIONS
// ============================================================

async function setupAccessControlDatabase(client) {
    console.log('Setting up ACCESS_CONTROL service database...');
    const db = client.db(DATABASES.ACCESS_CONTROL);

    await db.createCollection('access_policies');
    await db.collection('access_policies').createIndex({ tenantId: 1 });
    await db.collection('access_policies').createIndex({ resource: 1 });
    await db.collection('access_policies').createIndex({ action: 1 });

    await db.createCollection('role_permissions');
    await db.collection('role_permissions').createIndex({ tenantId: 1 });
    await db.collection('role_permissions').createIndex({ roleId: 1 });

    await db.createCollection('access_logs');
    await db.collection('access_logs').createIndex({ tenantId: 1 });
    await db.collection('access_logs').createIndex({ userId: 1 });
    await db.collection('access_logs').createIndex({ timestamp: -1 });

    console.log('✓ Access Control database setup complete');
}

async function setupAlertingDatabase(client) {
    console.log('Setting up ALERTING service database...');
    const db = client.db(DATABASES.ALERTING);

    await db.createCollection('alerts');
    await db.collection('alerts').createIndex({ tenantId: 1 });
    await db.collection('alerts').createIndex({ severity: 1 });
    await db.collection('alerts').createIndex({ status: 1 });
    await db.collection('alerts').createIndex({ createdAt: -1 });

    await db.createCollection('alert_rules');
    await db.collection('alert_rules').createIndex({ tenantId: 1 });
    await db.collection('alert_rules').createIndex({ isActive: 1 });

    await db.createCollection('alert_subscriptions');
    await db.collection('alert_subscriptions').createIndex({ tenantId: 1 });
    await db.collection('alert_subscriptions').createIndex({ userId: 1 });

    console.log('✓ Alerting database setup complete');
}

async function setupAntiFraudRulesDatabase(client) {
    console.log('Setting up ANTI_FRAUD_RULES service database...');
    const db = client.db(DATABASES.ANTI_FRAUD_RULES);

    await db.createCollection('fraud_rules');
    await db.collection('fraud_rules').createIndex({ tenantId: 1 });
    await db.collection('fraud_rules').createIndex({ ruleType: 1 });
    await db.collection('fraud_rules').createIndex({ isActive: 1 });
    await db.collection('fraud_rules').createIndex({ priority: -1 });

    await db.createCollection('rule_conditions');
    await db.collection('rule_conditions').createIndex({ ruleId: 1 });

    await db.createCollection('rule_actions');
    await db.collection('rule_actions').createIndex({ ruleId: 1 });

    console.log('✓ Anti-Fraud Rules database setup complete');
}

async function setupAntiFraudSignalsDatabase(client) {
    console.log('Setting up ANTI_FRAUD_SIGNALS service database...');
    const db = client.db(DATABASES.ANTI_FRAUD_SIGNALS);

    await db.createCollection('fraud_signals');
    await db.collection('fraud_signals').createIndex({ tenantId: 1 });
    await db.collection('fraud_signals').createIndex({ signalType: 1 });
    await db.collection('fraud_signals').createIndex({ severity: 1 });
    await db.collection('fraud_signals').createIndex({ timestamp: -1 });

    await db.createCollection('signal_aggregations');
    await db.collection('signal_aggregations').createIndex({ tenantId: 1 });
    await db.collection('signal_aggregations').createIndex({ timeWindow: 1 });

    console.log('✓ Anti-Fraud Signals database setup complete');
}

async function setupAPIGatewayDatabase(client) {
    console.log('Setting up API_GATEWAY service database...');
    const db = client.db(DATABASES.API_GATEWAY);

    await db.createCollection('routes');
    await db.collection('routes').createIndex({ path: 1 });
    await db.collection('routes').createIndex({ isActive: 1 });

    await db.createCollection('api_configs');
    await db.collection('api_configs').createIndex({ serviceId: 1 });

    await db.createCollection('rate_limit_rules');
    await db.collection('rate_limit_rules').createIndex({ routeId: 1 });

    await db.createCollection('gateway_logs');
    await db.collection('gateway_logs').createIndex({ timestamp: -1 });
    await db.collection('gateway_logs').createIndex({ routeId: 1 });

    console.log('✓ API Gateway database setup complete');
}

async function setupAPIKeysDatabase(client) {
    console.log('Setting up API_KEYS service database...');
    const db = client.db(DATABASES.API_KEYS);

    await db.createCollection('api_keys');
    await db.collection('api_keys').createIndex({ tenantId: 1 });
    await db.collection('api_keys').createIndex({ keyId: 1 }, { unique: true });
    await db.collection('api_keys').createIndex({ apiKey: 1 }, { unique: true });
    await db.collection('api_keys').createIndex({ isActive: 1 });
    await db.collection('api_keys').createIndex({ expiresAt: 1 });

    await db.createCollection('key_usage_logs');
    await db.collection('key_usage_logs').createIndex({ keyId: 1 });
    await db.collection('key_usage_logs').createIndex({ timestamp: -1 });

    await db.createCollection('key_scopes');
    await db.collection('key_scopes').createIndex({ keyId: 1 });

    console.log('✓ API Keys database setup complete');
}

async function setupAuditCorrelationDatabase(client) {
    console.log('Setting up AUDIT_CORRELATION service database...');
    const db = client.db(DATABASES.AUDIT_CORRELATION);

    await db.createCollection('audit_trails');
    await db.collection('audit_trails').createIndex({ tenantId: 1 });
    await db.collection('audit_trails').createIndex({ correlationId: 1 });
    await db.collection('audit_trails').createIndex({ timestamp: -1 });
    await db.collection('audit_trails').createIndex({ eventType: 1 });

    await db.createCollection('correlation_maps');
    await db.collection('correlation_maps').createIndex({ correlationId: 1 }, { unique: true });

    await db.createCollection('audit_events');
    await db.collection('audit_events').createIndex({ tenantId: 1 });
    await db.collection('audit_events').createIndex({ timestamp: -1 });

    console.log('✓ Audit Correlation database setup complete');
}

async function setupBillingDatabase(client) {
    console.log('Setting up BILLING service database...');
    const db = client.db(DATABASES.BILLING);

    await db.createCollection('invoices');
    await db.collection('invoices').createIndex({ tenantId: 1 });
    await db.collection('invoices').createIndex({ invoiceNumber: 1 }, { unique: true });
    await db.collection('invoices').createIndex({ status: 1 });
    await db.collection('invoices').createIndex({ dueDate: 1 });

    await db.createCollection('billing_accounts');
    await db.collection('billing_accounts').createIndex({ tenantId: 1 }, { unique: true });
    await db.collection('billing_accounts').createIndex({ accountId: 1 });

    await db.createCollection('payment_methods');
    await db.collection('payment_methods').createIndex({ tenantId: 1 });
    await db.collection('payment_methods').createIndex({ isDefault: 1 });

    await db.createCollection('billing_records');
    await db.collection('billing_records').createIndex({ tenantId: 1 });
    await db.collection('billing_records').createIndex({ period: 1 });

    console.log('✓ Billing database setup complete');
}

async function setupCourierAdapterDatabase(client) {
    console.log('Setting up COURIER_ADAPTER service database...');
    const db = client.db(DATABASES.COURIER_ADAPTER);

    await db.createCollection('courier_configs');
    await db.collection('courier_configs').createIndex({ tenantId: 1 });
    await db.collection('courier_configs').createIndex({ courierName: 1 });

    await db.createCollection('shipments');
    await db.collection('shipments').createIndex({ tenantId: 1 });
    await db.collection('shipments').createIndex({ trackingNumber: 1 });
    await db.collection('shipments').createIndex({ status: 1 });

    await db.createCollection('delivery_events');
    await db.collection('delivery_events').createIndex({ shipmentId: 1 });
    await db.collection('delivery_events').createIndex({ timestamp: -1 });

    console.log('✓ Courier Adapter database setup complete');
}

async function setupCurrencyConverterDatabase(client) {
    console.log('Setting up CURRENCY_CONVERTER service database...');
    const db = client.db(DATABASES.CURRENCY_CONVERTER);

    await db.createCollection('exchange_rates');
    await db.collection('exchange_rates').createIndex({ fromCurrency: 1, toCurrency: 1 });
    await db.collection('exchange_rates').createIndex({ effectiveDate: -1 });

    await db.createCollection('currency_configs');
    await db.collection('currency_configs').createIndex({ currencyCode: 1 }, { unique: true });

    await db.createCollection('conversion_history');
    await db.collection('conversion_history').createIndex({ tenantId: 1 });
    await db.collection('conversion_history').createIndex({ timestamp: -1 });

    console.log('✓ Currency Converter database setup complete');
}

async function setupDatabaseIndexingDatabase(client) {
    console.log('Setting up DATABASE_INDEXING service database...');
    const db = client.db(DATABASES.DATABASE_INDEXING);

    await db.createCollection('index_definitions');
    await db.collection('index_definitions').createIndex({ databaseName: 1 });
    await db.collection('index_definitions').createIndex({ collectionName: 1 });

    await db.createCollection('index_jobs');
    await db.collection('index_jobs').createIndex({ status: 1 });
    await db.collection('index_jobs').createIndex({ createdAt: -1 });

    console.log('✓ Database Indexing database setup complete');
}

async function setupDatabaseManagementDatabase(client) {
    console.log('Setting up DATABASE_MANAGEMENT service database...');
    const db = client.db(DATABASES.DATABASE_MANAGEMENT);

    await db.createCollection('database_registrations');
    await db.collection('database_registrations').createIndex({ databaseName: 1 }, { unique: true });

    await db.createCollection('backup_jobs');
    await db.collection('backup_jobs').createIndex({ status: 1 });
    await db.collection('backup_jobs').createIndex({ scheduledAt: 1 });

    await db.createCollection('migration_logs');
    await db.collection('migration_logs').createIndex({ timestamp: -1 });

    console.log('✓ Database Management database setup complete');
}

async function setupDataPrivacyConsentDatabase(client) {
    console.log('Setting up DATA_PRIVACY_CONSENT service database...');
    const db = client.db(DATABASES.DATA_PRIVACY_CONSENT);

    await db.createCollection('consent_records');
    await db.collection('consent_records').createIndex({ tenantId: 1 });
    await db.collection('consent_records').createIndex({ userId: 1 });
    await db.collection('consent_records').createIndex({ consentType: 1 });
    await db.collection('consent_records').createIndex({ isActive: 1 });

    await db.createCollection('consent_policies');
    await db.collection('consent_policies').createIndex({ tenantId: 1 });
    await db.collection('consent_policies').createIndex({ policyType: 1 });

    await db.createCollection('gdpr_requests');
    await db.collection('gdpr_requests').createIndex({ tenantId: 1 });
    await db.collection('gdpr_requests').createIndex({ requestType: 1 });
    await db.collection('gdpr_requests').createIndex({ status: 1 });

    console.log('✓ Data Privacy Consent database setup complete');
}

async function setupEventAuditDatabase(client) {
    console.log('Setting up EVENT_AUDIT service database...');
    const db = client.db(DATABASES.EVENT_AUDIT);

    await db.createCollection('audit_events');
    await db.collection('audit_events').createIndex({ tenantId: 1 });
    await db.collection('audit_events').createIndex({ eventType: 1 });
    await db.collection('audit_events').createIndex({ timestamp: -1 });
    await db.collection('audit_events').createIndex({ userId: 1 });

    await db.createCollection('event_snapshots');
    await db.collection('event_snapshots').createIndex({ eventId: 1 });

    await db.createCollection('audit_queries');
    await db.collection('audit_queries').createIndex({ tenantId: 1 });
    await db.collection('audit_queries').createIndex({ executedAt: -1 });

    console.log('✓ Event Audit database setup complete');
}

async function setupGeoLocationDatabase(client) {
    console.log('Setting up GEO_LOCATION service database...');
    const db = client.db(DATABASES.GEO_LOCATION);

    await db.createCollection('locations');
    await db.collection('locations').createIndex({ tenantId: 1 });
    await db.collection('locations').createIndex({ latitude: 1, longitude: 1 });

    await db.createCollection('geo_fences');
    await db.collection('geo_fences').createIndex({ tenantId: 1 });
    await db.collection('geo_fences').createIndex({ isActive: 1 });

    await db.createCollection('location_history');
    await db.collection('location_history').createIndex({ userId: 1 });
    await db.collection('location_history').createIndex({ timestamp: -1 });

    console.log('✓ Geo Location database setup complete');
}

async function setupIdempotencyDatabase(client) {
    console.log('Setting up IDEMPOTENCY service database...');
    const db = client.db(DATABASES.IDEMPOTENCY);

    await db.createCollection('idempotency_keys');
    await db.collection('idempotency_keys').createIndex({ key: 1 }, { unique: true });
    await db.collection('idempotency_keys').createIndex({ expiryAt: 1 });

    await db.createCollection('request_responses');
    await db.collection('request_responses').createIndex({ idempotencyKey: 1 });

    console.log('✓ Idempotency database setup complete');
}

async function setupIdentityAccessDatabase(client) {
    console.log('Setting up IDENTITY_ACCESS service database...');
    const db = client.db(DATABASES.IDENTITY_ACCESS);

    await db.createCollection('access_tokens');
    await db.collection('access_tokens').createIndex({ userId: 1 });
    await db.collection('access_tokens').createIndex({ token: 1 }, { unique: true });
    await db.collection('access_tokens').createIndex({ expiresAt: 1 });

    await db.createCollection('refresh_tokens');
    await db.collection('refresh_tokens').createIndex({ userId: 1 });
    await db.collection('refresh_tokens').createIndex({ token: 1 }, { unique: true });

    await db.createCollection('user_sessions');
    await db.collection('user_sessions').createIndex({ userId: 1 });
    await db.collection('user_sessions').createIndex({ sessionId: 1 }, { unique: true });

    await db.createCollection('role_assignments');
    await db.collection('role_assignments').createIndex({ userId: 1 });
    await db.collection('role_assignments').createIndex({ roleId: 1 });

    console.log('✓ Identity Access database setup complete');
}

async function setupIdentityDatabase(client) {
    console.log('Setting up IDENTITY service database...');
    const db = client.db(DATABASES.IDENTITY);

    await db.createCollection('users');
    await db.collection('users').createIndex({ tenantId: 1 });
    await db.collection('users').createIndex({ email: 1 }, { unique: true });
    await db.collection('users').createIndex({ username: 1 });
    await db.collection('users').createIndex({ status: 1 });

    await db.createCollection('user_profiles');
    await db.collection('user_profiles').createIndex({ userId: 1 }, { unique: true });

    await db.createCollection('password_reset_tokens');
    await db.collection('password_reset_tokens').createIndex({ token: 1 }, { unique: true });
    await db.collection('password_reset_tokens').createIndex({ expiresAt: 1 });

    console.log('✓ Identity database setup complete');
}

async function setupInsurerAdapterDatabase(client) {
    console.log('Setting up INSURER_ADAPTER service database...');
    const db = client.db(DATABASES.INSURER_ADAPTER);

    await db.createCollection('insurer_configs');
    await db.collection('insurer_configs').createIndex({ tenantId: 1 });
    await db.collection('insurer_configs').createIndex({ insurerCode: 1 });

    await db.createCollection('policy_syncs');
    await db.collection('policy_syncs').createIndex({ tenantId: 1 });
    await db.collection('policy_syncs').createIndex({ policyNumber: 1 });

    await db.createCollection('claims_syncs');
    await db.collection('claims_syncs').createIndex({ tenantId: 1 });
    await db.collection('claims_syncs').createIndex({ claimNumber: 1 });

    console.log('✓ Insurer Adapter database setup complete');
}

async function setupIntegrationAdaptersDatabase(client) {
    console.log('Setting up INTEGRATION_ADAPTERS service database...');
    const db = client.db(DATABASES.INTEGRATION_ADAPTERS);

    await db.createCollection('adapter_configs');
    await db.collection('adapter_configs').createIndex({ tenantId: 1 });
    await db.collection('adapter_configs').createIndex({ adapterType: 1 });
    await db.collection('adapter_configs').createIndex({ isActive: 1 });

    await db.createCollection('integration_logs');
    await db.collection('integration_logs').createIndex({ adapterId: 1 });
    await db.collection('integration_logs').createIndex({ timestamp: -1 });

    await db.createCollection('webhook_configs');
    await db.collection('webhook_configs').createIndex({ tenantId: 1 });

    console.log('✓ Integration Adapters database setup complete');
}

async function setupLoggingAggregationDatabase(client) {
    console.log('Setting up LOGGING_AGGREGATION service database...');
    const db = client.db(DATABASES.LOGGING_AGGREGATION);

    await db.createCollection('log_entries');
    await db.collection('log_entries').createIndex({ tenantId: 1 });
    await db.collection('log_entries').createIndex({ level: 1 });
    await db.collection('log_entries').createIndex({ timestamp: -1 });
    await db.collection('log_entries').createIndex({ service: 1 });

    await db.createCollection('log_aggregations');
    await db.collection('log_aggregations').createIndex({ timeWindow: 1 });

    await db.createCollection('log_filters');
    await db.collection('log_filters').createIndex({ tenantId: 1 });

    console.log('✓ Logging Aggregation database setup complete');
}

async function setupMFADatabase(client) {
    console.log('Setting up MFA service database...');
    const db = client.db(DATABASES.MFA);

    await db.createCollection('mfa_configs');
    await db.collection('mfa_configs').createIndex({ tenantId: 1 });
    await db.collection('mfa_configs').createIndex({ userId: 1 });

    await db.createCollection('mfa_secrets');
    await db.collection('mfa_secrets').createIndex({ userId: 1 }, { unique: true });

    await db.createCollection('mfa_tokens');
    await db.collection('mfa_tokens').createIndex({ userId: 1 });
    await db.collection('mfa_tokens').createIndex({ token: 1 });
    await db.collection('mfa_tokens').createIndex({ expiresAt: 1 });

    await db.createCollection('mfa_backup_codes');
    await db.collection('mfa_backup_codes').createIndex({ userId: 1 });

    console.log('✓ MFA database setup complete');
}

async function setupNotificationDatabase(client) {
    console.log('Setting up NOTIFICATION service database...');
    const db = client.db(DATABASES.NOTIFICATION);

    await db.createCollection('notifications');
    await db.collection('notifications').createIndex({ tenantId: 1 });
    await db.collection('notifications').createIndex({ userId: 1 });
    await db.collection('notifications').createIndex({ status: 1 });
    await db.collection('notifications').createIndex({ createdAt: -1 });

    await db.createCollection('notification_templates');
    await db.collection('notification_templates').createIndex({ tenantId: 1 });
    await db.collection('notification_templates').createIndex({ templateType: 1 });

    await db.createCollection('notification_preferences');
    await db.collection('notification_preferences').createIndex({ userId: 1 }, { unique: true });

    await db.createCollection('delivery_logs');
    await db.collection('delivery_logs').createIndex({ notificationId: 1 });
    await db.collection('delivery_logs').createIndex({ timestamp: -1 });

    console.log('✓ Notification database setup complete');
}

async function setupOnboardingDatabase(client) {
    console.log('Setting up ONBOARDING service database...');
    const db = client.db(DATABASES.ONBOARDING);

    await db.createCollection('onboarding_flows');
    await db.collection('onboarding_flows').createIndex({ tenantId: 1 });
    await db.collection('onboarding_flows').createIndex({ flowType: 1 });

    await db.createCollection('onboarding_steps');
    await db.collection('onboarding_steps').createIndex({ flowId: 1 });

    await db.createCollection('user_onboarding_state');
    await db.collection('user_onboarding_state').createIndex({ userId: 1 }, { unique: true });

    await db.createCollection('onboarding_checklists');
    await db.collection('onboarding_checklists').createIndex({ tenantId: 1 });

    console.log('✓ Onboarding database setup complete');
}

async function setupPaymentsAdapterDatabase(client) {
    console.log('Setting up PAYMENTS_ADAPTER service database...');
    const db = client.db(DATABASES.PAYMENTS_ADAPTER);

    await db.createCollection('payment_gateway_configs');
    await db.collection('payment_gateway_configs').createIndex({ tenantId: 1 });
    await db.collection('payment_gateway_configs').createIndex({ gatewayName: 1 });

    await db.createCollection('payment_transactions');
    await db.collection('payment_transactions').createIndex({ tenantId: 1 });
    await db.collection('payment_transactions').createIndex({ transactionId: 1 }, { unique: true });
    await db.collection('payment_transactions').createIndex({ status: 1 });

    await db.createCollection('payment_mappings');
    await db.collection('payment_mappings').createIndex({ tenantId: 1 });

    console.log('✓ Payments Adapter database setup complete');
}

async function setupPaymentDatabase(client) {
    console.log('Setting up PAYMENT service database...');
    const db = client.db(DATABASES.PAYMENT);

    await db.createCollection('payments');
    await db.collection('payments').createIndex({ tenantId: 1 });
    await db.collection('payments').createIndex({ paymentReference: 1 }, { unique: true });
    await db.collection('payments').createIndex({ status: 1 });
    await db.collection('payments').createIndex({ createdAt: -1 });

    await db.createCollection('payment_methods');
    await db.collection('payment_methods').createIndex({ tenantId: 1 });
    await db.collection('payment_methods').createIndex({ userId: 1 });
    await db.collection('payment_methods').createIndex({ isDefault: 1 });

    await db.createCollection('payment_schedules');
    await db.collection('payment_schedules').createIndex({ tenantId: 1 });
    await db.collection('payment_schedules').createIndex({ nextPaymentDate: 1 });

    console.log('✓ Payment database setup complete');
}

async function setupPolicyEngineDatabase(client) {
    console.log('Setting up POLICY_ENGINE service database...');
    const db = client.db(DATABASES.POLICY_ENGINE);

    await db.createCollection('policy_rules');
    await db.collection('policy_rules').createIndex({ tenantId: 1 });
    await db.collection('policy_rules').createIndex({ ruleType: 1 });
    await db.collection('policy_rules').createIndex({ isActive: 1 });
    await db.collection('policy_rules').createIndex({ priority: -1 });

    await db.createCollection('policy_evaluations');
    await db.collection('policy_evaluations').createIndex({ tenantId: 1 });
    await db.collection('policy_evaluations').createIndex({ timestamp: -1 });

    await db.createCollection('policy_variables');
    await db.collection('policy_variables').createIndex({ tenantId: 1 });

    console.log('✓ Policy Engine database setup complete');
}

async function setupPricingDatabase(client) {
    console.log('Setting up PRICING service database...');
    const db = client.db(DATABASES.PRICING);

    await db.createCollection('price_plans');
    await db.collection('price_plans').createIndex({ tenantId: 1 });
    await db.collection('price_plans').createIndex({ planCode: 1 });

    await db.createCollection('pricing_rules');
    await db.collection('pricing_rules').createIndex({ tenantId: 1 });
    await db.collection('pricing_rules').createIndex({ productId: 1 });

    await db.createCollection('price_calculations');
    await db.collection('price_calculations').createIndex({ tenantId: 1 });
    await db.collection('price_calculations').createIndex({ timestamp: -1 });

    await db.createCollection('discount_rules');
    await db.collection('discount_rules').createIndex({ tenantId: 1 });
    await db.collection('discount_rules').createIndex({ isActive: 1 });

    console.log('✓ Pricing database setup complete');
}

async function setupRateLimitingDatabase(client) {
    console.log('Setting up RATE_LIMITING service database...');
    const db = client.db(DATABASES.RATE_LIMITING);

    await db.createCollection('rate_limits');
    await db.collection('rate_limits').createIndex({ key: 1 }, { unique: true });
    await db.collection('rate_limits').createIndex({ expiryAt: 1 });

    await db.createCollection('rate_limit_rules');
    await db.collection('rate_limit_rules').createIndex({ tenantId: 1 });
    await db.collection('rate_limit_rules').createIndex({ endpoint: 1 });

    await db.createCollection('usage_counters');
    await db.collection('usage_counters').createIndex({ key: 1 }, { unique: true });

    console.log('✓ Rate Limiting database setup complete');
}

async function setupReportingReadModelDatabase(client) {
    console.log('Setting up REPORTING_READ_MODEL service database...');
    const db = client.db(DATABASES.REPORTING_READ_MODEL);

    await db.createCollection('report_views');
    await db.collection('report_views').createIndex({ tenantId: 1 });
    await db.collection('report_views').createIndex({ reportType: 1 });
    await db.collection('report_views').createIndex({ generatedAt: -1 });

    await db.createCollection('report_snapshots');
    await db.collection('report_snapshots').createIndex({ reportId: 1 });
    await db.collection('report_snapshots').createIndex({ timestamp: -1 });

    await db.createCollection('aggregate_views');
    await db.collection('aggregate_views').createIndex({ tenantId: 1 });
    await db.collection('aggregate_views').createIndex({ dimension: 1 });

    console.log('✓ Reporting Read Model database setup complete');
}

async function setupRequestRoutingDatabase(client) {
    console.log('Setting up REQUEST_ROUTING service database...');
    const db = client.db(DATABASES.REQUEST_ROUTING);

    await db.createCollection('routing_rules');
    await db.collection('routing_rules').createIndex({ tenantId: 1 });
    await db.collection('routing_rules').createIndex({ priority: -1 });
    await db.collection('routing_rules').createIndex({ isActive: 1 });

    await db.createCollection('routing_logs');
    await db.collection('routing_logs').createIndex({ timestamp: -1 });
    await db.collection('routing_logs').createIndex({ routeKey: 1 });

    await db.createCollection('service_endpoints');
    await db.collection('service_endpoints').createIndex({ serviceName: 1 });
    await db.collection('service_endpoints').createIndex({ isActive: 1 });

    console.log('✓ Request Routing database setup complete');
}

async function setupRapidassistDatabase(client) {
    console.log('Setting up RAPIDASSIST service database...');
    const db = client.db(DATABASES.RAPIDASSIST);

    await db.createCollection('assist_requests');
    await db.collection('assist_requests').createIndex({ tenantId: 1 });
    await db.collection('assist_requests').createIndex({ requestId: 1 }, { unique: true });
    await db.collection('assist_requests').createIndex({ status: 1 });
    await db.collection('assist_requests').createIndex({ createdAt: -1 });

    await db.createCollection('assist_providers');
    await db.collection('assist_providers').createIndex({ tenantId: 1 });
    await db.collection('assist_providers').createIndex({ isActive: 1 });

    await db.createCollection('service_types');
    await db.collection('service_types').createIndex({ category: 1 });

    console.log('✓ RapidAssist database setup complete');
}

async function setupServiceHealthMonitorDatabase(client) {
    console.log('Setting up SERVICE_HEALTH_MONITOR service database...');
    const db = client.db(DATABASES.SERVICE_HEALTH_MONITOR);

    await db.createCollection('health_checks');
    await db.collection('health_checks').createIndex({ serviceName: 1 });
    await db.collection('health_checks').createIndex({ timestamp: -1 });
    await db.collection('health_checks').createIndex({ status: 1 });

    await db.createCollection('health_metrics');
    await db.collection('health_metrics').createIndex({ serviceName: 1 });
    await db.collection('health_metrics').createIndex({ timestamp: -1 });

    await db.createCollection('alert_thresholds');
    await db.collection('alert_thresholds').createIndex({ serviceName: 1 });

    console.log('✓ Service Health Monitor database setup complete');
}

async function setupServiceRegistryDatabase(client) {
    console.log('Setting up SERVICE_REGISTRY service database...');
    const db = client.db(DATABASES.SERVICE_REGISTRY);

    await db.createCollection('services');
    await db.collection('services').createIndex({ serviceName: 1 }, { unique: true });
    await db.collection('services').createIndex({ instanceId: 1 });
    await db.collection('services').createIndex({ status: 1 });

    await db.createCollection('service_instances');
    await db.collection('service_instances').createIndex({ serviceName: 1 });
    await db.collection('service_instances').createIndex({ host: 1, port: 1 });
    await db.collection('service_instances').createIndex({ lastHeartbeat: -1 });

    await db.createCollection('heartbeat_logs');
    await db.collection('heartbeat_logs').createIndex({ serviceName: 1 });
    await db.collection('heartbeat_logs').createIndex({ timestamp: -1 });

    console.log('✓ Service Registry database setup complete');
}

async function setupSessionTokenDatabase(client) {
    console.log('Setting up SESSION_TOKEN service database...');
    const db = client.db(DATABASES.SESSION_TOKEN);

    await db.createCollection('sessions');
    await db.collection('sessions').createIndex({ sessionId: 1 }, { unique: true });
    await db.collection('sessions').createIndex({ userId: 1 });
    await db.collection('sessions').createIndex({ expiresAt: 1 });

    await db.createCollection('session_activities');
    await db.collection('session_activities').createIndex({ sessionId: 1 });
    await db.collection('session_activities').createIndex({ timestamp: -1 });

    console.log('✓ Session Token database setup complete');
}

async function setupTemplateMessagingDatabase(client) {
    console.log('Setting up TEMPLATE_MESSAGING service database...');
    const db = client.db(DATABASES.TEMPLATE_MESSAGING);

    await db.createCollection('message_templates');
    await db.collection('message_templates').createIndex({ tenantId: 1 });
    await db.collection('message_templates').createIndex({ templateCode: 1 });
    await db.collection('message_templates').createIndex({ category: 1 });

    await db.createCollection('template_variables');
    await db.collection('template_variables').createIndex({ templateId: 1 });

    await db.createCollection('rendered_messages');
    await db.collection('rendered_messages').createIndex({ templateId: 1 });
    await db.collection('rendered_messages').createIndex({ createdAt: -1 });

    console.log('✓ Template Messaging database setup complete');
}

async function setupTenantOrgDatabase(client) {
    console.log('Setting up TENANT_ORG service database...');
    const db = client.db(DATABASES.TENANT_ORG);

    await db.createCollection('tenants');
    await db.collection('tenants').createIndex({ tenantId: 1 }, { unique: true });
    await db.collection('tenants').createIndex({ tenantCode: 1 }, { unique: true });
    await db.collection('tenants').createIndex({ status: 1 });

    await db.createCollection('organizations');
    await db.collection('organizations').createIndex({ tenantId: 1 });
    await db.collection('organizations').createIndex({ orgId: 1 });

    await db.createCollection('tenant_configs');
    await db.collection('tenant_configs').createIndex({ tenantId: 1 }, { unique: true });

    await db.createCollection('tenant_subscriptions');
    await db.collection('tenant_subscriptions').createIndex({ tenantId: 1 });
    await db.collection('tenant_subscriptions').createIndex({ status: 1 });

    console.log('✓ Tenant Org database setup complete');
}

async function setupUserProfileDatabase(client) {
    console.log('Setting up USER_PROFILE service database...');
    const db = client.db(DATABASES.USER_PROFILE);

    await db.createCollection('user_profiles');
    await db.collection('user_profiles').createIndex({ tenantId: 1 });
    await db.collection('user_profiles').createIndex({ userId: 1 }, { unique: true });
    await db.collection('user_profiles').createIndex({ email: 1 });

    await db.createCollection('profile_preferences');
    await db.collection('profile_preferences').createIndex({ userId: 1 }, { unique: true });

    await db.createCollection('profile_activities');
    await db.collection('profile_activities').createIndex({ userId: 1 });
    await db.collection('profile_activities').createIndex({ timestamp: -1 });

    console.log('✓ User Profile database setup complete');
}

async function setupWAFPolicyDatabase(client) {
    console.log('Setting up WAF_POLICY service database...');
    const db = client.db(DATABASES.WAF_POLICY);

    await db.createCollection('waf_rules');
    await db.collection('waf_rules').createIndex({ tenantId: 1 });
    await db.collection('waf_rules').createIndex({ ruleType: 1 });
    await db.collection('waf_rules').createIndex({ isActive: 1 });
    await db.collection('waf_rules').createIndex({ priority: -1 });

    await db.createCollection('waf_policies');
    await db.collection('waf_policies').createIndex({ tenantId: 1 });
    await db.collection('waf_policies').createIndex({ policyName: 1 });

    await db.createCollection('waf_logs');
    await db.collection('waf_logs').createIndex({ tenantId: 1 });
    await db.collection('waf_logs').createIndex({ timestamp: -1 });
    await db.collection('waf_logs').createIndex({ action: 1 });

    console.log('✓ WAF Policy database setup complete');
}

async function setupWebhookDeliveryDatabase(client) {
    console.log('Setting up WEBHOOK_DELIVERY service database...');
    const db = client.db(DATABASES.WEBHOOK_DELIVERY);

    await db.createCollection('webhooks');
    await db.collection('webhooks').createIndex({ tenantId: 1 });
    await db.collection('webhooks').createIndex({ webhookUrl: 1 });
    await db.collection('webhooks').createIndex({ isActive: 1 });

    await db.createCollection('webhook_events');
    await db.collection('webhook_events').createIndex({ webhookId: 1 });
    await db.collection('webhook_events').createIndex({ eventType: 1 });

    await db.createCollection('delivery_attempts');
    await db.collection('delivery_attempts').createIndex({ eventId: 1 });
    await db.collection('delivery_attempts').createIndex({ timestamp: -1 });
    await db.collection('delivery_attempts').createIndex({ status: 1 });

    await db.createCollection('dead_letter_queue');
    await db.collection('dead_letter_queue').createIndex({ webhookId: 1 });
    await db.collection('dead_letter_queue').createIndex({ retryAfter: 1 });

    console.log('✓ Webhook Delivery database setup complete');
}

// ============================================================
// MAIN EXECUTION FUNCTION
// ============================================================

async function main() {
    console.log('\n');
    console.log('╔' + '═'.repeat(78) + '╗');
    console.log('║' + ' '.repeat(15) + 'MONGODB SETUP FOR SHARED INFRASTRUCTURE' + ' '.repeat(20) + '║');
    console.log('║' + '              41 Services - Complete Database Initialization'.repeat(1) + '        ' + '║');
    console.log('╚' + '═'.repeat(78) + '╝');
    console.log('\n');

    console.log(`MongoDB Connection: ${MONGODB_URI}`);
    console.log(`Timestamp: ${new Date().toISOString()}`);
    console.log('\n');

    const client = new MongoClient(MONGODB_URI);

    try {
        await client.connect();
        console.log('✓ Connected to MongoDB successfully');
        console.log('\n');

        // Setup all 41 service databases
        await setupAccessControlDatabase(client);
        await setupAlertingDatabase(client);
        await setupAntiFraudRulesDatabase(client);
        await setupAntiFraudSignalsDatabase(client);
        await setupAPIGatewayDatabase(client);
        await setupAPIKeysDatabase(client);
        await setupAuditCorrelationDatabase(client);
        await setupBillingDatabase(client);
        await setupCourierAdapterDatabase(client);
        await setupCurrencyConverterDatabase(client);
        await setupDatabaseIndexingDatabase(client);
        await setupDatabaseManagementDatabase(client);
        await setupDataPrivacyConsentDatabase(client);
        await setupEventAuditDatabase(client);
        await setupGeoLocationDatabase(client);
        await setupIdempotencyDatabase(client);
        await setupIdentityAccessDatabase(client);
        await setupIdentityDatabase(client);
        await setupInsurerAdapterDatabase(client);
        await setupIntegrationAdaptersDatabase(client);
        await setupLoggingAggregationDatabase(client);
        await setupMFADatabase(client);
        await setupNotificationDatabase(client);
        await setupOnboardingDatabase(client);
        await setupPaymentsAdapterDatabase(client);
        await setupPaymentDatabase(client);
        await setupPolicyEngineDatabase(client);
        await setupPricingDatabase(client);
        await setupRateLimitingDatabase(client);
        await setupReportingReadModelDatabase(client);
        await setupRequestRoutingDatabase(client);
        await setupRapidassistDatabase(client);
        await setupServiceHealthMonitorDatabase(client);
        await setupServiceRegistryDatabase(client);
        await setupSessionTokenDatabase(client);
        await setupTemplateMessagingDatabase(client);
        await setupTenantOrgDatabase(client);
        await setupUserProfileDatabase(client);
        await setupWAFPolicyDatabase(client);
        await setupWebhookDeliveryDatabase(client);

        console.log('\n');
        console.log('╔' + '═'.repeat(78) + '╗');
        console.log('║' + ' '.repeat(25) + 'SETUP COMPLETED SUCCESSFULLY!' + ' '.repeat(26) + '║');
        console.log('╚' + '═'.repeat(78) + '╝');
        console.log('\n');
        console.log('Summary - All 41 Service Databases Created:');
        console.log('  ✓ Access Control Service: ' + DATABASES.ACCESS_CONTROL);
        console.log('  ✓ Alerting Service: ' + DATABASES.ALERTING);
        console.log('  ✓ Anti-Fraud Rules Service: ' + DATABASES.ANTI_FRAUD_RULES);
        console.log('  ✓ Anti-Fraud Signals Service: ' + DATABASES.ANTI_FRAUD_SIGNALS);
        console.log('  ✓ API Gateway: ' + DATABASES.API_GATEWAY);
        console.log('  ✓ API Keys Service: ' + DATABASES.API_KEYS);
        console.log('  ✓ Audit Correlation Service: ' + DATABASES.AUDIT_CORRELATION);
        console.log('  ✓ Billing Service: ' + DATABASES.BILLING);
        console.log('  ✓ Courier Adapter Service: ' + DATABASES.COURIER_ADAPTER);
        console.log('  ✓ Currency Converter Service: ' + DATABASES.CURRENCY_CONVERTER);
        console.log('  ✓ Database Indexing Service: ' + DATABASES.DATABASE_INDEXING);
        console.log('  ✓ Database Management Service: ' + DATABASES.DATABASE_MANAGEMENT);
        console.log('  ✓ Data Privacy Consent Service: ' + DATABASES.DATA_PRIVACY_CONSENT);
        console.log('  ✓ Event Audit Service: ' + DATABASES.EVENT_AUDIT);
        console.log('  ✓ Geo Location Service: ' + DATABASES.GEO_LOCATION);
        console.log('  ✓ Idempotency Service: ' + DATABASES.IDEMPOTENCY);
        console.log('  ✓ Identity Access Service: ' + DATABASES.IDENTITY_ACCESS);
        console.log('  ✓ Identity Service: ' + DATABASES.IDENTITY);
        console.log('  ✓ Insurer Adapter Service: ' + DATABASES.INSURER_ADAPTER);
        console.log('  ✓ Integration Adapters Service: ' + DATABASES.INTEGRATION_ADAPTERS);
        console.log('  ✓ Logging Aggregation Service: ' + DATABASES.LOGGING_AGGREGATION);
        console.log('  ✓ MFA Service: ' + DATABASES.MFA);
        console.log('  ✓ Notification Service: ' + DATABASES.NOTIFICATION);
        console.log('  ✓ Onboarding Service: ' + DATABASES.ONBOARDING);
        console.log('  ✓ Payments Adapter Service: ' + DATABASES.PAYMENTS_ADAPTER);
        console.log('  ✓ Payment Service: ' + DATABASES.PAYMENT);
        console.log('  ✓ Policy Engine Service: ' + DATABASES.POLICY_ENGINE);
        console.log('  ✓ Pricing Service: ' + DATABASES.PRICING);
        console.log('  ✓ Rate Limiting Service: ' + DATABASES.RATE_LIMITING);
        console.log('  ✓ Reporting Read Model Service: ' + DATABASES.REPORTING_READ_MODEL);
        console.log('  ✓ Request Routing Service: ' + DATABASES.REQUEST_ROUTING);
        console.log('  ✓ RapidAssist Service: ' + DATABASES.RAPIDASSIST);
        console.log('  ✓ Service Health Monitor: ' + DATABASES.SERVICE_HEALTH_MONITOR);
        console.log('  ✓ Service Registry: ' + DATABASES.SERVICE_REGISTRY);
        console.log('  ✓ Session Token Service: ' + DATABASES.SESSION_TOKEN);
        console.log('  ✓ Template Messaging Service: ' + DATABASES.TEMPLATE_MESSAGING);
        console.log('  ✓ Tenant Org Service: ' + DATABASES.TENANT_ORG);
        console.log('  ✓ User Profile Service: ' + DATABASES.USER_PROFILE);
        console.log('  ✓ WAF Policy Service: ' + DATABASES.WAF_POLICY);
        console.log('  ✓ Webhook Delivery Service: ' + DATABASES.WEBHOOK_DELIVERY);
        console.log('\n');
        console.log('You can now connect MongoDB Compass to view the databases:');
        console.log(`  ${MONGODB_URI}`);
        console.log('\n');

    } catch (error) {
        console.error('\n');
        console.error('ERROR during setup:');
        console.error(error);
        console.error('\n');
        process.exit(1);
    } finally {
        await client.close();
        console.log('MongoDB connection closed.');
    }
}

// Execute main function
main();
