/**
 * Foundation Domain MongoDB Setup Script (CORRECTED)
 *
 * Creates ONE database: Foundation-Domain
 * Each domain is a collection prefix for clean navigation
 *
 * Run: node setup-foundation-domain-correct.js
 */

const { MongoClient } = require('mongodb');

const CONNECTION_URL = 'mongodb://localhost:27017';
const DATABASE_NAME = 'Foundation-Domain';

// Collection structure with prefixes for clear navigation
const COLLECTIONS = {
    // AI Services - prefix: ai_
    'ai_ai_chatbot_conversations': 'ai-services',
    'ai_ai_chatbot_knowledge_base': 'ai-services',
    'ai_ai_bi_analytics_reports': 'ai-services',
    'ai_ai_bi_analytics_dashboards': 'ai-services',
    'ai_ai_computer_vision_images': 'ai-services',
    'ai_ai_computer_vision_analysis': 'ai-services',
    'ai_ai_anomaly_detection_logs': 'ai-services',
    'ai_ai_anomaly_detection_alerts': 'ai-services',
    'ai_ai_automated_tagging_tags': 'ai-services',
    'ai_ai_automated_tagging_mappings': 'ai-services',
    'ai_ai_content_analysis_results': 'ai-services',
    'ai_ai_content_analysis_queue': 'ai-services',
    'ai_ai_data_quality_reports': 'ai-services',
    'ai_ai_data_quality_metrics': 'ai-services',
    'ai_ai_fraud_detection_cases': 'ai-services',
    'ai_ai_fraud_detection_rules': 'ai-services',
    'ai_ai_forecasting_predictions': 'ai-services',
    'ai_ai_forecasting_models': 'ai-services',
    'ai_ai_content_moderation_queue': 'ai-services',
    'ai_ai_content_moderation_results': 'ai-services',
    'ai_ai_inference_requests': 'ai-services',
    'ai_ai_inference_models': 'ai-services',
    'ai_ai_gateway_routes': 'ai-services',
    'ai_ai_gateway_metrics': 'ai-services',
    'ai_ai_image_recognition_images': 'ai-services',
    'ai_ai_image_recognition_labels': 'ai-services',
    'ai_ai_matching_algorithm_profiles': 'ai-services',
    'ai_ai_matching_algorithm_scores': 'ai-services',
    'ai_ai_categorization_items': 'ai-services',
    'ai_ai_categorization_categories': 'ai-services',
    'ai_ai_model_management_models': 'ai-services',
    'ai_ai_model_management_versions': 'ai-services',
    'ai_ai_nlp_processing_documents': 'ai-services',
    'ai_ai_nlp_processing_entities': 'ai-services',
    'ai_ai_predictive_analytics_predictions': 'ai-services',
    'ai_ai_predictive_analytics_models': 'ai-services',
    'ai_ai_optimization_parameters': 'ai-services',
    'ai_ai_optimization_results': 'ai-services',
    'ai_ai_personalization_profiles': 'ai-services',
    'ai_ai_personalization_preferences': 'ai-services',
    'ai_ai_search_optimization_index': 'ai-services',
    'ai_ai_search_optimization_queries': 'ai-services',
    'ai_ai_pricing_engine_rates': 'ai-services',
    'ai_ai_pricing_engine_rules': 'ai-services',
    'ai_ai_risk_assessment_scores': 'ai-services',
    'ai_ai_risk_assessment_factors': 'ai-services',
    'ai_ai_recommendation_items': 'ai-services',
    'ai_ai_recommendation_history': 'ai-services',
    'ai_ai_report_generation_templates': 'ai-services',
    'ai_ai_report_generation_queue': 'ai-services',
    'ai_ai_summarization_documents': 'ai-services',
    'ai_ai_summarization_results': 'ai-services',
    'ai_ai_sentiment_analysis_results': 'ai-services',
    'ai_ai_sentiment_analysis_feedback': 'ai-services',
    'ai_ai_translation_requests': 'ai-services',
    'ai_ai_translation_cache': 'ai-services',
    'ai_ai_speech_recognition_audio': 'ai-services',
    'ai_ai_speech_recognition_transcripts': 'ai-services',
    'ai_analytics_events': 'ai-services',
    'ai_analytics_aggregations': 'ai-services',

    // Central Configuration - prefix: config_
    'config_tenancy_configuration': 'central-configuration',
    'config_tenancy_settings': 'central-configuration',
    'config_feature_flags': 'central-configuration',
    'config_feature_flag_audits': 'central-configuration',
    'config_dynamic_routing_config': 'central-configuration',
    'config_dynamic_routing_rules': 'central-configuration',
    'config_country_localization_config': 'central-configuration',
    'config_country_localization_strings': 'central-configuration',
    'config_release_rollout_config': 'central-configuration',
    'config_release_rollout_history': 'central-configuration',
    'config_policy_configuration': 'central-configuration',
    'config_policy_rules': 'central-configuration',
    'config_rate_limit_policy': 'central-configuration',
    'config_rate_limit_rules': 'central-configuration',
    'config_config_service': 'central-configuration',
    'config_config_service_versions': 'central-configuration',

    // Centralized Dashboard - prefix: dash_
    'dash_dashboard_analytics_widgets': 'centralized-dashboard',
    'dash_dashboard_analytics_layouts': 'centralized-dashboard',
    'dash_dashboard_analytics_data': 'centralized-dashboard',
    'dash_dashboard_reporting_templates': 'centralized-dashboard',
    'dash_dashboard_reporting_schedules': 'centralized-dashboard',
    'dash_dashboard_reporting_history': 'centralized-dashboard',
    'dash_dashboard_configuration_users': 'centralized-dashboard',
    'dash_dashboard_configuration_permissions': 'centralized-dashboard',
    'dash_dashboard_configuration_widgets': 'centralized-dashboard',

    // Shared Libraries - prefix: lib_
    'lib_event_schemas': 'shared-libraries',
    'lib_event_schema_versions': 'shared-libraries',
    'lib_shared_ai_contracts': 'shared-libraries',
    'lib_shared_audit_logs': 'shared-libraries',
    'lib_shared_audit_trail': 'shared-libraries',
    'lib_shared_cors_config': 'shared-libraries',
    'lib_shared_dto_schemas': 'shared-libraries',
    'lib_shared_exception_definitions': 'shared-libraries',
    'lib_shared_idempotency_keys': 'shared-libraries',
    'lib_shared_idempotency_records': 'shared-libraries',
    'lib_shared_mapper_configurations': 'shared-libraries',
    'lib_shared_observability_metrics': 'shared-libraries',
    'lib_shared_observability_logs': 'shared-libraries',
    'lib_shared_persistence_entities': 'shared-libraries',
    'lib_shared_request_context': 'shared-libraries',
    'lib_shared_security_policies': 'shared-libraries',
    'lib_shared_validation_rules': 'shared-libraries',
    'lib_common_domain_models': 'shared-libraries',
    'lib_common_entities': 'shared-libraries',

    // Shared Infrastructure - prefix: infra_
    'infra_event_audit_logs': 'shared-infrastructure',
    'infra_logging_aggregation_logs': 'shared-infrastructure',
    'infra_idempotency_records': 'shared-infrastructure',
    'infra_rate_limiting_counters': 'shared-infrastructure',
    'infra_rate_limiting_rules': 'shared-infrastructure',
    'infra_billing_invoices': 'shared-infrastructure',
    'infra_billing_payments': 'shared-infrastructure',
    'infra_billing_subscriptions': 'shared-infrastructure',
    'infra_courier_adapter_shipments': 'shared-infrastructure',
    'infra_notification_queue': 'shared-infrastructure',
    'infra_notification_history': 'shared-infrastructure',
    'infra_payment_transactions': 'shared-infrastructure',
    'infra_payment_methods': 'shared-infrastructure',
    'infra_geo_location_cache': 'shared-infrastructure',
    'infra_geo_location_history': 'shared-infrastructure',
    'infra_identity_users': 'shared-infrastructure',
    'infra_identity_roles': 'shared-infrastructure',
    'infra_identity_permissions': 'shared-infrastructure',
    'infra_insurer_adapter_claims': 'shared-infrastructure',
    'infra_insurer_adapter_policies': 'shared-infrastructure',
    'infra_integration_adapters_config': 'shared-infrastructure',
    'infra_metrics_telemetry_data': 'shared-infrastructure',
    'infra_metrics_telemetry_aggregations': 'shared-infrastructure',
    'infra_payments_adapter_transactions': 'shared-infrastructure',
    'infra_policy_engine_rules': 'shared-infrastructure',
    'infra_policy_engine_evaluations': 'shared-infrastructure',
    'infra_pricing_rules': 'shared-infrastructure',
    'infra_pricing_history': 'shared-infrastructure',
    'infra_request_routing_rules': 'shared-infrastructure',
    'infra_request_routing_logs': 'shared-infrastructure',
    'infra_service_health_monitor_status': 'shared-infrastructure',
    'infra_service_health_monitor_alerts': 'shared-infrastructure',
    'infra_service_registry_services': 'shared-infrastructure',
    'infra_service_registry_instances': 'shared-infrastructure',
    'infra_tenant_org_tenants': 'shared-infrastructure',
    'infra_tenant_org_organizations': 'shared-infrastructure',
    'infra_user_profile_profiles': 'shared-infrastructure',
    'infra_user_profile_preferences': 'shared-infrastructure',
    'infra_database_management_backups': 'shared-infrastructure',
    'infra_database_management_maintenance': 'shared-infrastructure',
    'infra_template_messaging_templates': 'shared-infrastructure',
    'infra_template_messaging_campaigns': 'shared-infrastructure',
    'infra_waf_policy_rules': 'shared-infrastructure',
    'infra_waf_policy_logs': 'shared-infrastructure',
    'infra_webhook_delivery_queue': 'shared-infrastructure',
    'infra_webhook_delivery_logs': 'shared-infrastructure',
    'infra_alerting_alerts': 'shared-infrastructure',
    'infra_alerting_rules': 'shared-infrastructure',
    'infra_alerting_escalations': 'shared-infrastructure',
    'infra_anti_fraud_rules': 'shared-infrastructure',
    'infra_anti_fraud_signals': 'shared-infrastructure',
    'infra_anti_fraud_cases': 'shared-infrastructure',
    'infra_api_keys': 'shared-infrastructure',
    'infra_api_keys_usage': 'shared-infrastructure',
    'infra_audit_correlation_mappings': 'shared-infrastructure',
    'infra_session_token_sessions': 'shared-infrastructure',
    'infra_session_token_blacklist': 'shared-infrastructure',
    'infra_onboarding_workflows': 'shared-infrastructure',
    'infra_onboarding_tasks': 'shared-infrastructure',
    'infra_currency_converter_rates': 'shared-infrastructure',
    'infra_currency_converter_cache': 'shared-infrastructure',
    'infra_identity_access_users': 'shared-infrastructure',
    'infra_identity_access_roles': 'shared-infrastructure',
    'infra_identity_access_permissions': 'shared-infrastructure',
    'infra_maps_geocoding_cache': 'shared-infrastructure',
    'infra_mfa_codes': 'shared-infrastructure',
    'infra_mfa_settings': 'shared-infrastructure',
    'infra_access_control_policies': 'shared-infrastructure',
    'infra_access_control_roles': 'shared-infrastructure',
    'infra_api_gateway_routes': 'shared-infrastructure',
    'infra_api_gateway_config': 'shared-infrastructure',
    'infra_data_privacy_consent_records': 'shared-infrastructure',
    'infra_data_privacy_requests': 'shared-infrastructure',
    'infra_reporting_read_model_views': 'shared-infrastructure',
    'infra_reporting_read_model_snapshots': 'shared-infrastructure',
    'infra_database_indexing_jobs': 'shared-infrastructure',
    'infra_database_indexing_status': 'shared-infrastructure',

    // Orchestration Services - prefix: orch_
    'orch_fleet_assistance_requests': 'orchestration-services',
    'orch_fleet_assistance_assignments': 'orchestration-services',
    'orch_fleet_assistance_status': 'orchestration-services',
    'orch_fleet_policy_policies': 'orchestration-services',
    'orch_fleet_policy_rules': 'orchestration-services',
    'orch_fleet_policy_claims': 'orchestration-services',
    'orch_fleet_organization_hierarchy': 'orchestration-services',
    'orch_fleet_organization_units': 'orchestration-services',
    'orch_fleet_organization_members': 'orchestration-services',
    'orch_reporting_reports': 'orchestration-services',
    'orch_reporting_schedules': 'orchestration-services',
    'orch_reporting_templates': 'orchestration-services',
    'orch_fleet_vehicles_vehicles': 'orchestration-services',
    'orch_fleet_vehicles_maintenance': 'orchestration-services',
    'orch_fleet_vehicles_locations': 'orchestration-services',
    'orch_alerting_alerts': 'orchestration-services',
    'orch_alerting_rules': 'orchestration-services',
    'orch_alerting_notifications': 'orchestration-services',
    'orch_dispatching_jobs': 'orchestration-services',
    'orch_dispatching_assignments': 'orchestration-services',
    'orch_dispatching_routes': 'orchestration-services',
    'orch_location_service_locations': 'orchestration-services',
    'orch_location_service_geofences': 'orchestration-services',
    'orch_location_service_history': 'orchestration-services',
    'orch_matching_algorithm_profiles': 'orchestration-services',
    'orch_matching_algorithm_scores': 'orchestration-services',
    'orch_matching_algorithm_preferences': 'orchestration-services',
    'orch_monitoring_service_metrics': 'orchestration-services',
    'orch_monitoring_service_alerts': 'orchestration-services',
    'orch_monitoring_service_dashboards': 'orchestration-services',
    'orch_transaction_orchestration_transactions': 'orchestration-services',
    'orch_transaction_orchestration_sagas': 'orchestration-services',
    'orch_transaction_orchestration_compensations': 'orchestration-services'
};

// Old databases to delete
const OLD_DATABASES = [
    'ai-services',
    'central-configuration',
    'centralized-dashboard',
    'shared-libraries',
    'shared-infrastructure',
    'orchestration-services'
];

async function setupDatabase() {
    const client = new MongoClient(CONNECTION_URL);

    console.log('════════════════════════════════════════════════════════════════════');
    console.log('     FOUNDATION DOMAIN MONGODB - CORRECTED STRUCTURE                 ');
    console.log('════════════════════════════════════════════════════════════════════');
    console.log('');

    try {
        await client.connect();
        console.log('✅ Connected to MongoDB');
        console.log('');

        // Step 1: Delete old databases
        console.log('🗑️  STEP 1: Deleting old incorrect databases...');
        for (const dbName of OLD_DATABASES) {
            try {
                const db = client.db(dbName);
                await db.dropDatabase();
                console.log(`  ✅ Dropped: ${dbName}`);
            } catch (err) {
                console.log(`  ⚠️  ${dbName} not found or error: ${err.message}`);
            }
        }
        console.log('');

        // Step 2: Create new Foundation-Domain database with prefixed collections
        console.log(`📁 STEP 2: Creating database: ${DATABASE_NAME}`);
        const db = client.db(DATABASE_NAME);

        let totalCollections = 0;
        const domainStats = {};

        for (const [collectionName, domain] of Object.entries(COLLECTIONS)) {
            try {
                const collection = db.collection(collectionName);
                await collection.insertOne({ _init: true });
                await collection.deleteOne({ _init: true });

                if (!domainStats[domain]) {
                    domainStats[domain] = 0;
                }
                domainStats[domain]++;
                totalCollections++;

                console.log(`  ✅ [${domain}] ${collectionName}`);
            } catch (err) {
                if (err.code === 48) {
                    console.log(`  ⚠️  [${domain}] ${collectionName} (already exists)`);
                    totalCollections++;
                } else {
                    console.log(`  ❌ Error creating ${collectionName}: ${err.message}`);
                }
            }
        }

        // Step 3: Create indexes
        console.log('');
        console.log('🔑 STEP 3: Creating indexes...');
        await createIndexes(db);
        console.log('  ✅ Indexes created');
        console.log('');

        // Summary
        console.log('════════════════════════════════════════════════════════════════════');
        console.log('                    SETUP COMPLETED SUCCESSFULLY                      ');
        console.log('════════════════════════════════════════════════════════════════════');
        console.log('');
        console.log(`🗄️  DATABASE: ${DATABASE_NAME}`);
        console.log(`📋 TOTAL COLLECTIONS: ${totalCollections}`);
        console.log('');
        console.log('📊 BY DOMAIN:');
        for (const [domain, count] of Object.entries(domainStats)) {
            console.log(`   ${domain}: ${count} collections`);
        }
        console.log('');
        console.log('🔍 COLLECTION PREFIXES:');
        console.log('   ai_      → ai-services');
        console.log('   config_  → central-configuration');
        console.log('   dash_    → centralized-dashboard');
        console.log('   lib_     → shared-libraries');
        console.log('   infra_   → shared-infrastructure');
        console.log('   orch_    → orchestration-services');
        console.log('');
        console.log('🔍 VERIFY IN MONGODB COMPASS:');
        console.log(`   mongodb://localhost:27017`);
        console.log(`   Database: ${DATABASE_NAME}`);
        console.log('');
        console.log('════════════════════════════════════════════════════════════════════');

    } catch (err) {
        console.error('❌ Error:', err);
    } finally {
        await client.close();
    }
}

async function createIndexes(db) {
    try {
        // ai-services indexes
        await db.collection('ai_ai_chatbot_conversations').createIndex({ sessionId: 1, createdAt: -1 });
        await db.collection('ai_ai_chatbot_conversations').createIndex({ userId: 1 });
        await db.collection('ai_ai_fraud_detection_cases').createIndex({ status: 1, createdAt: -1 });
        await db.collection('ai_ai_model_management_models').createIndex({ name: 1, version: -1 }, { unique: true });
        await db.collection('ai_analytics_events').createIndex({ eventType: 1, timestamp: -1 });

        // central-configuration indexes
        await db.collection('config_tenancy_configuration').createIndex({ tenantId: 1 }, { unique: true });
        await db.collection('config_feature_flags').createIndex({ name: 1, enabled: 1 });
        await db.collection('config_feature_flags').createIndex({ tenantId: 1 });
        await db.collection('config_dynamic_routing_config').createIndex({ service: 1, version: -1 });
        await db.collection('config_country_localization_config').createIndex({ countryCode: 1 }, { unique: true });
        await db.collection('config_policy_configuration').createIndex({ policyType: 1, active: 1 });
        await db.collection('config_rate_limit_policy').createIndex({ identifier: 1 }, { unique: true });
        await db.collection('config_config_service').createIndex({ key: 1, environment: 1 }, { unique: true });

        // centralized-dashboard indexes
        await db.collection('dash_dashboard_analytics_widgets').createIndex({ userId: 1, name: 1 });
        await db.collection('dash_dashboard_reporting_templates').createIndex({ name: 1, createdBy: 1 });
        await db.collection('dash_dashboard_configuration_users').createIndex({ userId: 1 }, { unique: true });

        // shared-libraries indexes
        await db.collection('lib_event_schemas').createIndex({ eventType: 1, version: -1 });
        await db.collection('lib_shared_audit_logs').createIndex({ timestamp: -1, entityType: 1 });
        await db.collection('lib_shared_audit_logs').createIndex({ userId: 1, timestamp: -1 });
        await db.collection('lib_shared_idempotency_keys').createIndex({ key: 1 }, { unique: true });
        await db.collection('lib_shared_observability_metrics').createIndex({ service: 1, timestamp: -1 });
        await db.collection('lib_shared_request_context').createIndex({ requestId: 1 }, { unique: true });
        await db.collection('lib_common_domain_models').createIndex({ entityType: 1, id: 1 });

        // shared-infrastructure indexes
        await db.collection('infra_event_audit_logs').createIndex({ timestamp: -1, eventType: 1 });
        await db.collection('infra_logging_aggregation_logs').createIndex({ service: 1, timestamp: -1 });
        await db.collection('infra_logging_aggregation_logs').createIndex({ level: 1, timestamp: -1 });
        await db.collection('infra_idempotency_records').createIndex({ idempotencyKey: 1 }, { unique: true });
        await db.collection('infra_rate_limiting_counters').createIndex({ identifier: 1, window: 1 }, { unique: true });
        await db.collection('infra_billing_invoices').createIndex({ tenantId: 1, status: 1 });
        await db.collection('infra_notification_queue').createIndex({ status: 1, scheduledAt: 1 });
        await db.collection('infra_payment_transactions').createIndex({ transactionId: 1 }, { unique: true });
        await db.collection('infra_identity_users').createIndex({ email: 1 }, { unique: true });
        await db.collection('infra_identity_users').createIndex({ username: 1 }, { unique: true });
        await db.collection('infra_service_registry_services').createIndex({ serviceName: 1 }, { unique: true });
        await db.collection('infra_service_registry_instances').createIndex({ serviceId: 1, status: 1 });
        await db.collection('infra_tenant_org_tenants').createIndex({ tenantId: 1 }, { unique: true });
        await db.collection('infra_webhook_delivery_queue').createIndex({ status: 1, retryCount: 1 });
        await db.collection('infra_alerting_alerts').createIndex({ status: 1, severity: 1, createdAt: -1 });
        await db.collection('infra_api_keys').createIndex({ keyHash: 1 }, { unique: true });
        await db.collection('infra_session_token_sessions').createIndex({ token: 1 }, { unique: true });
        await db.collection('infra_api_gateway_routes').createIndex({ path: 1, method: 1 }, { unique: true });

        // orchestration-services indexes
        await db.collection('orch_fleet_assistance_requests').createIndex({ requestId: 1 }, { unique: true });
        await db.collection('orch_fleet_assistance_requests').createIndex({ status: 1, createdAt: -1 });
        await db.collection('orch_fleet_policy_policies').createIndex({ policyNumber: 1 }, { unique: true });
        await db.collection('orch_fleet_organization_hierarchy').createIndex({ tenantId: 1, parentId: 1 });
        await db.collection('orch_fleet_vehicles_vehicles').createIndex({ vehicleId: 1 }, { unique: true });
        await db.collection('orch_fleet_vehicles_vehicles').createIndex({ fleetId: 1 });
        await db.collection('orch_alerting_alerts').createIndex({ status: 1, priority: 1, createdAt: -1 });
        await db.collection('orch_dispatching_jobs').createIndex({ status: 1, scheduledAt: 1 });
        await db.collection('orch_location_service_locations').createIndex({ vehicleId: 1, timestamp: -1 });
        await db.collection('orch_matching_algorithm_scores').createIndex({ requestId: 1, score: -1 });
        await db.collection('orch_monitoring_service_metrics').createIndex({ service: 1, timestamp: -1 });
        await db.collection('orch_transaction_orchestration_transactions').createIndex({ transactionId: 1 }, { unique: true });
        await db.collection('orch_transaction_orchestration_transactions').createIndex({ status: 1, createdAt: -1 });

    } catch (err) {
        console.log(`  ⚠️  Index creation warning: ${err.message}`);
    }
}

setupDatabase().catch(console.error);
