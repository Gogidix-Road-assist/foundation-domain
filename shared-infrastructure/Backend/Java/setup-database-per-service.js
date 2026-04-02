/**
 * MongoDB Database Per Service Setup Script
 * Following 2024-2025 Microservices Best Practices
 *
 * Each service gets its own dedicated database with clear ownership
 */

const { MongoClient } = require('mongodb');

const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017';

// Service database definitions with their collections
const SERVICES = {
    'access_control_service_db': {
        collections: {
            policies: { indexes: [{ tenantId: 1 }, { resource: 1 }, { action: 1 }, { createdAt: -1 }] },
            roles: { indexes: [{ tenantId: 1 }, { roleId: 1 }, { name: 1 }] },
            permissions: { indexes: [{ tenantId: 1 }, { roleId: 1 }, { permission: 1 }] },
            access_logs: { indexes: [{ tenantId: 1 }, { userId: 1 }, { timestamp: -1 }, { action: 1 }] }
        }
    },

    'alerting_service_db': {
        collections: {
            alerts: { indexes: [{ tenantId: 1 }, { severity: 1 }, { status: 1 }, { createdAt: -1 }] },
            rules: { indexes: [{ tenantId: 1 }, { isActive: 1 }, { priority: -1 }] },
            subscriptions: { indexes: [{ tenantId: 1 }, { userId: 1 }, { alertType: 1 }] },
            alert_history: { indexes: [{ alertId: 1 }, { timestamp: -1 }] }
        }
    },

    'anti_fraud_rules_service_db': {
        collections: {
            rules: { indexes: [{ tenantId: 1 }, { ruleType: 1 }, { isActive: 1 }, { priority: -1 }] },
            conditions: { indexes: [{ ruleId: 1 }, { conditionType: 1 }] },
            actions: { indexes: [{ ruleId: 1 }, { actionType: 1 }] },
            rule_versions: { indexes: [{ ruleId: 1 }, { version: -1 }] }
        }
    },

    'anti_fraud_signals_service_db': {
        collections: {
            signals: { indexes: [{ tenantId: 1 }, { signalType: 1 }, { severity: 1 }, { timestamp: -1 }] },
            aggregations: { indexes: [{ tenantId: 1 }, { timeWindow: 1 }, { signalType: 1 }] },
            signal_history: { indexes: [{ entityId: 1 }, { timestamp: -1 }] }
        }
    },

    'api_gateway_service_db': {
        collections: {
            routes: { indexes: [{ path: 1 }, { isActive: 1 }, { priority: -1 }] },
            route_configs: { indexes: [{ serviceId: 1 }, { environment: 1 }] },
            rate_limits: { indexes: [{ routeId: 1 }, { clientId: 1 }] },
            gateway_logs: { indexes: [{ timestamp: -1 }, { routeId: 1 }, { statusCode: 1 }] }
        }
    },

    'api_keys_service_db': {
        collections: {
            keys: { indexes: [{ tenantId: 1 }, { keyId: 1 }, { apiKey: 1 }, { isActive: 1 }, { expiresAt: 1 }] },
            key_usage: { indexes: [{ keyId: 1 }, { timestamp: -1 }] },
            key_scopes: { indexes: [{ keyId: 1 }, { scope: 1 }] },
            key_audit: { indexes: [{ keyId: 1 }, { action: 1 }, { timestamp: -1 }] }
        }
    },

    'audit_correlation_service_db': {
        collections: {
            trails: { indexes: [{ tenantId: 1 }, { correlationId: 1 }, { timestamp: -1 }, { eventType: 1 }] },
            correlations: { indexes: [{ correlationId: 1 }, { timestamp: -1 }] },
            snapshots: { indexes: [{ eventId: 1 }, { entityType: 1 }] }
        }
    },

    'billing_service_db': {
        collections: {
            invoices: { indexes: [{ tenantId: 1 }, { invoiceNumber: 1 }, { status: 1 }, { dueDate: 1 }] },
            accounts: { indexes: [{ tenantId: 1 }, { accountId: 1 }] },
            payment_methods: { indexes: [{ tenantId: 1 }, { isDefault: 1 }, { isActive: 1 }] },
            billing_records: { indexes: [{ tenantId: 1 }, { period: 1 }] },
            invoice_items: { indexes: [{ invoiceId: 1 }] }
        }
    },

    'courier_adapter_service_db': {
        collections: {
            configs: { indexes: [{ tenantId: 1 }, { courierName: 1 }, { isActive: 1 }] },
            shipments: { indexes: [{ tenantId: 1 }, { trackingNumber: 1 }, { status: 1 }] },
            delivery_events: { indexes: [{ shipmentId: 1 }, { timestamp: -1 }] },
            courier_rates: { indexes: [{ courierId: 1 }, { route: 1 }] }
        }
    },

    'currency_converter_service_db': {
        collections: {
            exchange_rates: { indexes: [{ fromCurrency: 1, toCurrency: 1 }, { effectiveDate: -1 }] },
            currency_configs: { indexes: [{ currencyCode: 1 }, { isActive: 1 }] },
            conversion_history: { indexes: [{ tenantId: 1 }, { timestamp: -1 }] },
            rate_snapshots: { indexes: [{ currencyPair: 1 }, { timestamp: -1 }] }
        }
    },

    'database_indexing_service_db': {
        collections: {
            index_definitions: { indexes: [{ databaseName: 1 }, { collectionName: 1 }, { isActive: 1 }] },
            index_jobs: { indexes: [{ status: 1 }, { createdAt: -1 }] },
            job_history: { indexes: [{ jobType: 1 }, { timestamp: -1 }] }
        }
    },

    'database_management_service_db': {
        collections: {
            database_registrations: { indexes: [{ databaseName: 1 }, { status: 1 }] },
            backup_jobs: { indexes: [{ status: 1 }, { scheduledAt: 1 }] },
            migration_logs: { indexes: [{ timestamp: -1 }, { status: 1 }] },
            maintenance_windows: { indexes: [{ databaseName: 1 }, { scheduledAt: 1 }] }
        }
    },

    'data_privacy_consent_service_db': {
        collections: {
            consent_records: { indexes: [{ tenantId: 1 }, { userId: 1 }, { consentType: 1 }, { isActive: 1 }] },
            consent_policies: { indexes: [{ tenantId: 1 }, { policyType: 1 }, { version: -1 }] },
            gdpr_requests: { indexes: [{ tenantId: 1 }, { requestType: 1 }, { status: 1 }] },
            consent_audit: { indexes: [{ userId: 1 }, { consentType: 1 }, { timestamp: -1 }] }
        }
    },

    'event_audit_service_db': {
        collections: {
            audit_events: { indexes: [{ tenantId: 1 }, { eventType: 1 }, { timestamp: -1 }, { userId: 1 }] },
            event_snapshots: { indexes: [{ eventId: 1 }, { entityType: 1 }] },
            audit_queries: { indexes: [{ tenantId: 1 }, { executedAt: -1 }] }
        }
    },

    'geo_location_service_db': {
        collections: {
            locations: { indexes: [{ tenantId: 1 }, { entityType: 1 }, { entityId: 1 }, { latitude: 1, longitude: 1 }] },
            fences: { indexes: [{ tenantId: 1 }, { fenceType: 1 }, { isActive: 1 }] },
            location_history: { indexes: [{ entityId: 1 }, { timestamp: -1 }] },
            geo_coordinates: { indexes: [{ locationId: 1 }, { timestamp: -1 }] }
        }
    },

    'idempotency_service_db': {
        collections: {
            keys: { indexes: [{ key: 1 }, { expiryAt: 1 }] },
            responses: { indexes: [{ idempotencyKey: 1 }, { createdAt: -1 }] },
            key_metadata: { indexes: [{ keyHash: 1 }, { status: 1 }] }
        }
    },

    'identity_access_service_db': {
        collections: {
            access_tokens: { indexes: [{ userId: 1 }, { token: 1 }, { expiresAt: 1 }] },
            refresh_tokens: { indexes: [{ userId: 1 }, { token: 1 }, { expiresAt: 1 }] },
            sessions: { indexes: [{ userId: 1 }, { sessionId: 1 }, { expiresAt: 1 }] },
            role_assignments: { indexes: [{ userId: 1 }, { roleId: 1 }] }
        }
    },

    'identity_service_db': {
        collections: {
            users: { indexes: [{ tenantId: 1 }, { email: 1 }, { username: 1 }, { status: 1 }] },
            profiles: { indexes: [{ userId: 1 }, { tenantId: 1 }] },
            reset_tokens: { indexes: [{ token: 1 }, { expiresAt: 1 }] },
            user_preferences: { indexes: [{ userId: 1 }, { preferenceType: 1 }] }
        }
    },

    'insurer_adapter_service_db': {
        collections: {
            configs: { indexes: [{ tenantId: 1 }, { insurerCode: 1 }, { isActive: 1 }] },
            policy_syncs: { indexes: [{ tenantId: 1 }, { policyNumber: 1 }, { syncStatus: 1 }] },
            claims_syncs: { indexes: [{ tenantId: 1 }, { claimNumber: 1 }, { syncStatus: 1 }] },
            sync_history: { indexes: [{ syncId: 1 }, { timestamp: -1 }] }
        }
    },

    'integration_adapters_service_db': {
        collections: {
            configs: { indexes: [{ tenantId: 1 }, { adapterType: 1 }, { isActive: 1 }] },
            logs: { indexes: [{ adapterId: 1 }, { timestamp: -1 }] },
            webhooks: { indexes: [{ tenantId: 1 }, { webhookUrl: 1 }] },
            adapter_metrics: { indexes: [{ adapterId: 1 }, { timestamp: -1 }] }
        }
    },

    'logging_aggregation_service_db': {
        collections: {
            log_entries: { indexes: [{ tenantId: 1 }, { level: 1 }, { timestamp: -1 }, { service: 1 }] },
            aggregations: { indexes: [{ timeWindow: 1 }, { service: 1 }] },
            filters: { indexes: [{ tenantId: 1 }, { filterName: 1 }] },
            log_stats: { indexes: [{ service: 1 }, { timestamp: -1 }] }
        }
    },

    'mfa_service_db': {
        collections: {
            configs: { indexes: [{ tenantId: 1 }, { userId: 1 }, { mfaType: 1 }] },
            secrets: { indexes: [{ userId: 1 }, { isActive: 1 }] },
            tokens: { indexes: [{ userId: 1 }, { token: 1 }, { expiresAt: 1 }] },
            backup_codes: { indexes: [{ userId: 1 }, { isUsed: 1 }] }
        }
    },

    'notification_service_db': {
        collections: {
            notifications: { indexes: [{ tenantId: 1 }, { userId: 1 }, { status: 1 }, { createdAt: -1 }] },
            templates: { indexes: [{ tenantId: 1 }, { templateType: 1 }, { isActive: 1 }] },
            preferences: { indexes: [{ userId: 1 }, { notificationType: 1 }] },
            delivery_logs: { indexes: [{ notificationId: 1 }, { timestamp: -1 }] }
        }
    },

    'onboarding_service_db': {
        collections: {
            flows: { indexes: [{ tenantId: 1 }, { flowType: 1 }, { version: 1 }] },
            steps: { indexes: [{ flowId: 1 }, { stepOrder: 1 }] },
            user_states: { indexes: [{ userId: 1 }, { flowId: 1 }] },
            checklists: { indexes: [{ tenantId: 1 }, { checklistType: 1 }] }
        }
    },

    'payments_adapter_service_db': {
        collections: {
            gateway_configs: { indexes: [{ tenantId: 1 }, { gatewayName: 1 }, { isActive: 1 }] },
            transactions: { indexes: [{ tenantId: 1 }, { transactionId: 1 }, { status: 1 }] },
            payment_mappings: { indexes: [{ tenantId: 1 }, { mappingType: 1 }] },
            adapter_logs: { indexes: [{ transactionId: 1 }, { timestamp: -1 }] }
        }
    },

    'payment_service_db': {
        collections: {
            payments: { indexes: [{ tenantId: 1 }, { paymentReference: 1 }, { status: 1 }, { createdAt: -1 }] },
            payment_methods: { indexes: [{ tenantId: 1 }, { userId: 1 }, { isDefault: 1 }] },
            schedules: { indexes: [{ tenantId: 1 }, { nextPaymentDate: 1 }] },
            refunds: { indexes: [{ paymentId: 1 }, { status: 1 }] }
        }
    },

    'policy_engine_service_db': {
        collections: {
            rules: { indexes: [{ tenantId: 1 }, { ruleType: 1 }, { isActive: 1 }, { priority: -1 }] },
            evaluations: { indexes: [{ tenantId: 1 }, { timestamp: -1 }] },
            variables: { indexes: [{ tenantId: 1 }, { variableName: 1 }] },
            rule_versions: { indexes: [{ ruleId: 1 }, { version: -1 }] }
        }
    },

    'pricing_service_db': {
        collections: {
            plans: { indexes: [{ tenantId: 1 }, { planCode: 1 }, { isActive: 1 }] },
            rules: { indexes: [{ tenantId: 1 }, { productId: 1 }] },
            calculations: { indexes: [{ tenantId: 1 }, { timestamp: -1 }] },
            discounts: { indexes: [{ tenantId: 1 }, { discountCode: 1 }, { isActive: 1 }] }
        }
    },

    'rate_limiting_service_db': {
        collections: {
            limits: { indexes: [{ key: 1 }, { expiryAt: 1 }] },
            rules: { indexes: [{ tenantId: 1 }, { endpoint: 1 }] },
            counters: { indexes: [{ key: 1 }, { windowStart: 1 }] },
            limit_history: { indexes: [{ key: 1 }, { timestamp: -1 }] }
        }
    },

    'reporting_read_model_service_db': {
        collections: {
            views: { indexes: [{ tenantId: 1 }, { reportType: 1 }, { generatedAt: -1 }] },
            snapshots: { indexes: [{ reportId: 1 }, { timestamp: -1 }] },
            aggregates: { indexes: [{ tenantId: 1 }, { dimension: 1 }, { timeWindow: -1 }] }
        }
    },

    'request_routing_service_db': {
        collections: {
            rules: { indexes: [{ tenantId: 1 }, { priority: -1 }, { isActive: 1 }] },
            logs: { indexes: [{ timestamp: -1 }, { routeKey: 1 }] },
            endpoints: { indexes: [{ serviceName: 1 }, { isActive: 1 }] },
            routing_metrics: { indexes: [{ endpointId: 1 }, { timestamp: -1 }] }
        }
    },

    'rapidassist_service_db': {
        collections: {
            requests: { indexes: [{ tenantId: 1 }, { requestId: 1 }, { status: 1 }, { createdAt: -1 }] },
            providers: { indexes: [{ tenantId: 1 }, { isActive: 1 }, { serviceType: 1 }] },
            service_types: { indexes: [{ category: 1 }, { isActive: 1 }] },
            request_history: { indexes: [{ userId: 1 }, { timestamp: -1 }] }
        }
    },

    'service_health_monitor_service_db': {
        collections: {
            health_checks: { indexes: [{ serviceName: 1 }, { timestamp: -1 }, { status: 1 }] },
            metrics: { indexes: [{ serviceName: 1 }, { timestamp: -1 }] },
            thresholds: { indexes: [{ serviceName: 1 }, { metricType: 1 }] },
            alerts: { indexes: [{ serviceName: 1 }, { severity: 1 }, { isActive: 1 }] }
        }
    },

    'service_registry_service_db': {
        collections: {
            services: { indexes: [{ serviceName: 1 }, { status: 1 }] },
            instances: { indexes: [{ serviceName: 1 }, { host: 1, port: 1 }, { lastHeartbeat: -1 }] },
            heartbeats: { indexes: [{ serviceName: 1 }, { instanceId: 1 }, { timestamp: -1 }] }
        }
    },

    'session_token_service_db': {
        collections: {
            sessions: { indexes: [{ sessionId: 1 }, { userId: 1 }, { expiresAt: 1 }] },
            activities: { indexes: [{ sessionId: 1 }, { timestamp: -1 }] },
            session_metadata: { indexes: [{ userId: 1 }, { createdAt: -1 }] }
        }
    },

    'template_messaging_service_db': {
        collections: {
            templates: { indexes: [{ tenantId: 1 }, { templateCode: 1 }, { category: 1 }] },
            variables: { indexes: [{ templateId: 1 }, { variableName: 1 }] },
            rendered_messages: { indexes: [{ templateId: 1 }, { createdAt: -1 }] },
            template_versions: { indexes: [{ templateCode: 1 }, { version: -1 }] }
        }
    },

    'tenant_org_service_db': {
        collections: {
            tenants: { indexes: [{ tenantId: 1 }, { tenantCode: 1 }, { status: 1 }] },
            organizations: { indexes: [{ tenantId: 1 }, { orgId: 1 }] },
            configs: { indexes: [{ tenantId: 1 }, { configType: 1 }] },
            subscriptions: { indexes: [{ tenantId: 1 }, { status: 1 }, { planType: 1 }] }
        }
    },

    'user_profile_service_db': {
        collections: {
            profiles: { indexes: [{ tenantId: 1 }, { userId: 1 }, { email: 1 }] },
            preferences: { indexes: [{ userId: 1 }, { preferenceType: 1 }] },
            activities: { indexes: [{ userId: 1 }, { activityType: 1 }, { timestamp: -1 }] },
            profile_attributes: { indexes: [{ userId: 1 }, { attributeName: 1 }] }
        }
    },

    'waf_policy_service_db': {
        collections: {
            rules: { indexes: [{ tenantId: 1 }, { ruleType: 1 }, { isActive: 1 }, { priority: -1 }] },
            policies: { indexes: [{ tenantId: 1 }, { policyName: 1 }] },
            logs: { indexes: [{ tenantId: 1 }, { timestamp: -1 }, { action: 1 }] },
            rule_versions: { indexes: [{ ruleId: 1 }, { version: -1 }] }
        }
    },

    'webhook_delivery_service_db': {
        collections: {
            configs: { indexes: [{ tenantId: 1 }, { webhookUrl: 1 }, { isActive: 1 }] },
            events: { indexes: [{ webhookId: 1 }, { eventType: 1 }] },
            deliveries: { indexes: [{ eventId: 1 }, { timestamp: -1 }, { status: 1 }] },
            dead_letters: { indexes: [{ webhookId: 1 }, { retryAfter: 1 }] }
        }
    }
};

async function setupDatabasePerService() {
    console.log('\n╔══════════════════════════════════════════════════════════════════════════════╗');
    console.log('║          DATABASE PER SERVICE SETUP - 41 SERVICES                       ║');
    console.log('║          Following 2024-2025 Microservices Best Practices                ║');
    console.log('╚══════════════════════════════════════════════════════════════════════════════╝\n');

    const client = new MongoClient(MONGODB_URI);

    try {
        await client.connect();
        console.log('✓ Connected to MongoDB successfully\n');

        // Drop the old shared database
        console.log('🗑️  Dropping old shared_infrastructure_dev database...');
        await client.db('shared_infrastructure_dev').dropDatabase().catch(() => {});
        console.log('✓ Old database dropped\n');

        let totalDatabases = 0;
        let totalCollections = 0;
        let totalIndexes = 0;

        console.log('📊 Creating databases and collections:\n');

        for (const [dbName, service] of Object.entries(SERVICES)) {
            const db = client.db(dbName);

            console.log(`\n📦 ${dbName.replace('_db', '').toUpperCase()}`);
            console.log(`   Collections:`);

            let dbIndexCount = 0;

            for (const [collName, config] of Object.entries(service.collections)) {
                try {
                    // Create collection
                    await db.createCollection(collName);
                    totalCollections++;

                    // Create indexes
                    for (const indexSpec of config.indexes) {
                        await db.collection(collName).createIndex(indexSpec);
                        dbIndexCount++;
                        totalIndexes++;
                    }

                    const indexCount = config.indexes.length;
                    console.log(`     ✓ ${collName} (${indexCount} indexes)`);
                } catch (error) {
                    console.log(`     ⚠ ${collName}: ${error.message}`);
                }
            }

            totalDatabases++;
        }

        console.log(`\n\n╔══════════════════════════════════════════════════════════════════════════════╗`);
        console.log(`║                    SETUP COMPLETED SUCCESSFULLY!                           ║`);
        console.log(`╚══════════════════════════════════════════════════════════════════════════════╝\n`);

        console.log('Summary:');
        console.log(`  Total Databases: ${totalDatabases}`);
        console.log(`  Total Collections: ${totalCollections}`);
        console.log(`  Total Indexes: ${totalIndexes}`);
        console.log(`  Connection: ${MONGODB_URI}\n`);

        console.log('Architecture Pattern: Database Per Service');
        console.log('Each service owns its dedicated database with clear boundaries.\n');

        console.log('Service List:');
        Object.keys(SERVICES).forEach((db, i) => {
            console.log(`  ${(i+1).toString().padStart(2)}. ${db.replace('_db', '')}`);
        });

        console.log('\nMongoDB Compass Connection:');
        console.log(`  ${MONGODB_URI}\n`);

    } catch (error) {
        console.error('\n❌ ERROR during setup:', error.message);
        process.exit(1);
    } finally {
        await client.close();
        console.log('MongoDB connection closed.\n');
    }
}

// Execute
setupDatabasePerService();
