/**
 * Foundation Domain MongoDB Setup Script (Node.js)
 *
 * Creates SEPARATE DATABASE for each domain
 *
 * Run: node setup-foundation-domain-separate-databases.js
 * Requirements: npm install mongodb
 */

const { MongoClient } = require('mongodb');

const CONNECTION_URL = 'mongodb://localhost:27017';

// Database structure: each domain gets its own database
const DOMAINS = {
    'Foundation-Domain-AI-Services': [
        'ai_chatbot_conversations',
        'ai_chatbot_knowledge_base',
        'ai_bi_analytics_reports',
        'ai_bi_analytics_dashboards',
        'ai_computer_vision_images',
        'ai_computer_vision_analysis',
        'ai_anomaly_detection_logs',
        'ai_anomaly_detection_alerts',
        'ai_automated_tagging_tags',
        'ai_automated_tagging_mappings',
        'ai_content_analysis_results',
        'ai_content_analysis_queue',
        'ai_data_quality_reports',
        'ai_data_quality_metrics',
        'ai_fraud_detection_cases',
        'ai_fraud_detection_rules',
        'ai_forecasting_predictions',
        'ai_forecasting_models',
        'ai_content_moderation_queue',
        'ai_content_moderation_results',
        'ai_inference_requests',
        'ai_inference_models',
        'ai_gateway_routes',
        'ai_gateway_metrics',
        'ai_image_recognition_images',
        'ai_image_recognition_labels',
        'ai_matching_algorithm_profiles',
        'ai_matching_algorithm_scores',
        'ai_categorization_items',
        'ai_categorization_categories',
        'ai_model_management_models',
        'ai_model_management_versions',
        'ai_nlp_processing_documents',
        'ai_nlp_processing_entities',
        'ai_predictive_analytics_predictions',
        'ai_predictive_analytics_models',
        'ai_optimization_parameters',
        'ai_optimization_results',
        'ai_personalization_profiles',
        'ai_personalization_preferences',
        'ai_search_optimization_index',
        'ai_search_optimization_queries',
        'ai_pricing_engine_rates',
        'ai_pricing_engine_rules',
        'ai_risk_assessment_scores',
        'ai_risk_assessment_factors',
        'ai_recommendation_items',
        'ai_recommendation_history',
        'ai_report_generation_templates',
        'ai_report_generation_queue',
        'ai_summarization_documents',
        'ai_summarization_results',
        'ai_sentiment_analysis_results',
        'ai_sentiment_analysis_feedback',
        'ai_translation_requests',
        'ai_translation_cache',
        'ai_speech_recognition_audio',
        'ai_speech_recognition_transcripts',
        'analytics_events',
        'analytics_aggregations'
    ],
    'Foundation-Domain-Central-Configuration': [
        'tenancy_configuration',
        'tenancy_settings',
        'feature_flags',
        'feature_flag_audits',
        'dynamic_routing_config',
        'dynamic_routing_rules',
        'country_localization_config',
        'country_localization_strings',
        'release_rollout_config',
        'release_rollout_history',
        'policy_configuration',
        'policy_rules',
        'rate_limit_policy',
        'rate_limit_rules',
        'config_service',
        'config_service_versions'
    ],
    'Foundation-Domain-Centralized-Dashboard': [
        'dashboard_analytics_widgets',
        'dashboard_analytics_layouts',
        'dashboard_analytics_data',
        'dashboard_reporting_templates',
        'dashboard_reporting_schedules',
        'dashboard_reporting_history',
        'dashboard_configuration_users',
        'dashboard_configuration_permissions',
        'dashboard_configuration_widgets'
    ],
    'Foundation-Domain-Shared-Libraries': [
        'event_schemas',
        'event_schema_versions',
        'shared_ai_contracts',
        'shared_audit_logs',
        'shared_audit_trail',
        'shared_cors_config',
        'shared_dto_schemas',
        'shared_exception_definitions',
        'shared_idempotency_keys',
        'shared_idempotency_records',
        'shared_mapper_configurations',
        'shared_observability_metrics',
        'shared_observability_logs',
        'shared_persistence_entities',
        'shared_request_context',
        'shared_security_policies',
        'shared_validation_rules',
        'common_domain_models',
        'common_entities'
    ],
    'Foundation-Domain-Shared-Infrastructure': [
        'event_audit_logs',
        'logging_aggregation_logs',
        'idempotency_records',
        'rate_limiting_counters',
        'rate_limiting_rules',
        'billing_invoices',
        'billing_payments',
        'billing_subscriptions',
        'courier_adapter_shipments',
        'notification_queue',
        'notification_history',
        'payment_transactions',
        'payment_methods',
        'geo_location_cache',
        'geo_location_history',
        'identity_users',
        'identity_roles',
        'identity_permissions',
        'insurer_adapter_claims',
        'insurer_adapter_policies',
        'integration_adapters_config',
        'metrics_telemetry_data',
        'metrics_telemetry_aggregations',
        'payments_adapter_transactions',
        'policy_engine_rules',
        'policy_engine_evaluations',
        'pricing_rules',
        'pricing_history',
        'request_routing_rules',
        'request_routing_logs',
        'service_health_monitor_status',
        'service_health_monitor_alerts',
        'service_registry_services',
        'service_registry_instances',
        'tenant_org_tenants',
        'tenant_org_organizations',
        'user_profile_profiles',
        'user_profile_preferences',
        'database_management_backups',
        'database_management_maintenance',
        'template_messaging_templates',
        'template_messaging_campaigns',
        'waf_policy_rules',
        'waf_policy_logs',
        'webhook_delivery_queue',
        'webhook_delivery_logs',
        'alerting_alerts',
        'alerting_rules',
        'alerting_escalations',
        'anti_fraud_rules',
        'anti_fraud_signals',
        'anti_fraud_cases',
        'api_keys',
        'api_keys_usage',
        'audit_correlation_mappings',
        'session_token_sessions',
        'session_token_blacklist',
        'onboarding_workflows',
        'onboarding_tasks',
        'currency_converter_rates',
        'currency_converter_cache',
        'identity_access_users',
        'identity_access_roles',
        'identity_access_permissions',
        'maps_geocoding_cache',
        'mfa_codes',
        'mfa_settings',
        'access_control_policies',
        'access_control_roles',
        'api_gateway_routes',
        'api_gateway_config',
        'data_privacy_consent_records',
        'data_privacy_requests',
        'reporting_read_model_views',
        'reporting_read_model_snapshots',
        'database_indexing_jobs',
        'database_indexing_status'
    ],
    'Foundation-Domain-Orchestration-Services': [
        'fleet_assistance_requests',
        'fleet_assistance_assignments',
        'fleet_assistance_status',
        'fleet_policy_policies',
        'fleet_policy_rules',
        'fleet_policy_claims',
        'fleet_organization_hierarchy',
        'fleet_organization_units',
        'fleet_organization_members',
        'reporting_reports',
        'reporting_schedules',
        'reporting_templates',
        'fleet_vehicles_vehicles',
        'fleet_vehicles_maintenance',
        'fleet_vehicles_locations',
        'alerting_alerts',
        'alerting_rules',
        'alerting_notifications',
        'dispatching_jobs',
        'dispatching_assignments',
        'dispatching_routes',
        'location_service_locations',
        'location_service_geofences',
        'location_service_history',
        'matching_algorithm_profiles',
        'matching_algorithm_scores',
        'matching_algorithm_preferences',
        'monitoring_service_metrics',
        'monitoring_service_alerts',
        'monitoring_service_dashboards',
        'transaction_orchestration_transactions',
        'transaction_orchestration_sagas',
        'transaction_orchestration_compensations'
    ]
};

async function setupDatabases() {
    const client = new MongoClient(CONNECTION_URL);

    console.log('════════════════════════════════════════════════════════════════════');
    console.log('       FOUNDATION DOMAIN MONGODB - SEPARATE DATABASES                ');
    console.log('════════════════════════════════════════════════════════════════════');
    console.log('');

    try {
        await client.connect();
        console.log('✅ Connected to MongoDB at mongodb://localhost:27017');
        console.log('');

        // Delete old incorrect database first
        console.log('🗑️  Deleting old incorrect database: Foundation-Domain');
        try {
            await client.db('Foundation-Domain').dropDatabase();
            console.log('  ✅ Dropped: Foundation-Domain');
        } catch (err) {
            console.log('  ⚠️  Foundation-Domain does not exist or already dropped');
        }
        console.log('');

        let totalDatabases = 0;
        let totalCollections = 0;

        for (const [dbName, collections] of Object.entries(DOMAINS)) {
            console.log(`📁 Creating database: ${dbName}`);
            const db = client.db(dbName);
            totalDatabases++;

            for (const collectionName of collections) {
                try {
                    const collection = db.collection(collectionName);
                    await collection.insertOne({ _init: true });
                    await collection.deleteOne({ _init: true });
                    console.log(`  ✅ ${collectionName}`);
                    totalCollections++;
                } catch (err) {
                    if (err.code === 48) {
                        console.log(`  ⚠️  Collection already exists: ${collectionName}`);
                        totalCollections++;
                    } else {
                        console.log(`  ❌ Error creating ${collectionName}: ${err.message}`);
                    }
                }
            }

            // Create indexes
            console.log(`  🔑 Creating indexes...`);
            await createIndexes(db, dbName);
            console.log('');
        }

        console.log('════════════════════════════════════════════════════════════════════');
        console.log('                    SETUP COMPLETED SUCCESSFULLY                      ');
        console.log('════════════════════════════════════════════════════════════════════');
        console.log('');
        console.log(`🗄️  TOTAL DATABASES: ${totalDatabases}`);
        console.log(`📋 TOTAL COLLECTIONS: ${totalCollections}`);
        console.log('');
        console.log('📊 DATABASE STRUCTURE:');
        for (const [dbName, collections] of Object.entries(DOMAINS)) {
            console.log(`   ${dbName}`);
            console.log(`   └── ${collections.length} collections`);
        }
        console.log('');
        console.log('🔍 VERIFY IN MONGODB COMPASS:');
        console.log('   mongodb://localhost:27017');
        console.log('   You will see separate databases for each domain');
        console.log('');
        console.log('════════════════════════════════════════════════════════════════════');

    } catch (err) {
        console.error('❌ Error:', err);
    } finally {
        await client.close();
    }
}

async function createIndexes(db, dbName) {
    try {
        // AI-Services indexes
        if (dbName.includes('AI-Services')) {
            await db.collection('ai_chatbot_conversations').createIndex({ sessionId: 1, createdAt: -1 });
            await db.collection('ai_chatbot_conversations').createIndex({ userId: 1 });
            await db.collection('ai_fraud_detection_cases').createIndex({ status: 1, createdAt: -1 });
            await db.collection('ai_model_management_models').createIndex({ name: 1, version: -1 }, { unique: true });
            await db.collection('analytics_events').createIndex({ eventType: 1, timestamp: -1 });
        }
        // Central-Configuration indexes
        else if (dbName.includes('Central-Configuration')) {
            await db.collection('tenancy_configuration').createIndex({ tenantId: 1 }, { unique: true });
            await db.collection('feature_flags').createIndex({ name: 1, enabled: 1 });
            await db.collection('feature_flags').createIndex({ tenantId: 1 });
            await db.collection('dynamic_routing_config').createIndex({ service: 1, version: -1 });
            await db.collection('country_localization_config').createIndex({ countryCode: 1 }, { unique: true });
        }
        // Centralized-Dashboard indexes
        else if (dbName.includes('Centralized-Dashboard')) {
            await db.collection('dashboard_analytics_widgets').createIndex({ userId: 1, name: 1 });
            await db.collection('dashboard_configuration_users').createIndex({ userId: 1 }, { unique: true });
        }
        // Shared-Libraries indexes
        else if (dbName.includes('Shared-Libraries')) {
            await db.collection('event_schemas').createIndex({ eventType: 1, version: -1 });
            await db.collection('shared_audit_logs').createIndex({ timestamp: -1, entityType: 1 });
            await db.collection('shared_audit_logs').createIndex({ userId: 1, timestamp: -1 });
            await db.collection('shared_idempotency_keys').createIndex({ key: 1 }, { unique: true });
        }
        // Shared-Infrastructure indexes
        else if (dbName.includes('Shared-Infrastructure')) {
            await db.collection('event_audit_logs').createIndex({ timestamp: -1, eventType: 1 });
            await db.collection('logging_aggregation_logs').createIndex({ service: 1, timestamp: -1 });
            await db.collection('idempotency_records').createIndex({ idempotencyKey: 1 }, { unique: true });
            await db.collection('rate_limiting_counters').createIndex({ identifier: 1, window: 1 }, { unique: true });
            await db.collection('billing_invoices').createIndex({ tenantId: 1, status: 1 });
            await db.collection('payment_transactions').createIndex({ transactionId: 1 }, { unique: true });
            await db.collection('identity_users').createIndex({ email: 1 }, { unique: true });
            await db.collection('service_registry_services').createIndex({ serviceName: 1 }, { unique: true });
        }
        // Orchestration-Services indexes
        else if (dbName.includes('Orchestration-Services')) {
            await db.collection('fleet_assistance_requests').createIndex({ requestId: 1 }, { unique: true });
            await db.collection('fleet_assistance_requests').createIndex({ status: 1, createdAt: -1 });
            await db.collection('fleet_policy_policies').createIndex({ policyNumber: 1 }, { unique: true });
            await db.collection('fleet_vehicles_vehicles').createIndex({ vehicleId: 1 }, { unique: true });
            await db.collection('alerting_alerts').createIndex({ status: 1, priority: 1, createdAt: -1 });
            await db.collection('dispatching_jobs').createIndex({ status: 1, scheduledAt: 1 });
            await db.collection('transaction_orchestration_transactions').createIndex({ transactionId: 1 }, { unique: true });
        }
        console.log('  ✅ Indexes created');
    } catch (err) {
        console.log(`  ⚠️  Index creation warning: ${err.message}`);
    }
}

setupDatabases().catch(console.error);
