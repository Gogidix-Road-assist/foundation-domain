/**
 * Foundation Domain MongoDB Setup Script
 *
 * Creates databases and collections following the Foundation Domain structure:
 * - ai-services
 * - central-configuration
 * - centralized-dashboard
 * - shared-libraries
 * - shared-infrastructure
 * - orchestration-services
 *
 * Usage: mongosh "mongodb://localhost:27017" setup-foundation-mongodb-clean.js
 * Or load in MongoDB Compass: Mongosh → load("setup-foundation-mongodb-clean.js")
 */

// MongoDB connection
const conn = new Mongo();
const db = conn.getDB("admin");

// Switch to admin for database creation
print("════════════════════════════════════════════════════════════════════");
print("       FOUNDATION DOMAIN MONGODB DATABASE SETUP                        ");
print("════════════════════════════════════════════════════════════════════");
print("");

// ============================================================================
// DATABASE 1: ai-services
// ============================================================================
print("📁 Creating database: ai-services");
const aiServicesDb = conn.getDB("ai-services");

const aiCollections = [
    // AI Services collections
    "ai_chatbot_conversations",
    "ai_chatbot_knowledge_base",
    "ai_bi_analytics_reports",
    "ai_bi_analytics_dashboards",
    "ai_computer_vision_images",
    "ai_computer_vision_analysis",
    "ai_anomaly_detection_logs",
    "ai_anomaly_detection_alerts",
    "ai_automated_tagging_tags",
    "ai_automated_tagging_mappings",
    "ai_content_analysis_results",
    "ai_content_analysis_queue",
    "ai_data_quality_reports",
    "ai_data_quality_metrics",
    "ai_fraud_detection_cases",
    "ai_fraud_detection_rules",
    "ai_forecasting_predictions",
    "ai_forecasting_models",
    "ai_content_moderation_queue",
    "ai_content_moderation_results",
    "ai_inference_requests",
    "ai_inference_models",
    "ai_gateway_routes",
    "ai_gateway_metrics",
    "ai_image_recognition_images",
    "ai_image_recognition_labels",
    "ai_matching_algorithm_profiles",
    "ai_matching_algorithm_scores",
    "ai_categorization_items",
    "ai_categorization_categories",
    "ai_model_management_models",
    "ai_model_management_versions",
    "ai_nlp_processing_documents",
    "ai_nlp_processing_entities",
    "ai_predictive_analytics_predictions",
    "ai_predictive_analytics_models",
    "ai_optimization_parameters",
    "ai_optimization_results",
    "ai_personalization_profiles",
    "ai_personalization_preferences",
    "ai_search_optimization_index",
    "ai_search_optimization_queries",
    "ai_pricing_engine_rates",
    "ai_pricing_engine_rules",
    "ai_risk_assessment_scores",
    "ai_risk_assessment_factors",
    "ai_recommendation_items",
    "ai_recommendation_history",
    "ai_report_generation_templates",
    "ai_report_generation_queue",
    "ai_summarization_documents",
    "ai_summarization_results",
    "ai_sentiment_analysis_results",
    "ai_sentiment_analysis_feedback",
    "ai_translation_requests",
    "ai_translation_cache",
    "ai_speech_recognition_audio",
    "ai_speech_recognition_transcripts",
    "analytics_events",
    "analytics_aggregations"
];

aiCollections.forEach(collection => {
    aiServicesDb.createCollection(collection);
    print("  ✅ Created collection: " + collection);
});

// Create indexes for ai-services
print("  🔑 Creating indexes for ai-services...");
aiServicesDb.ai_chatbot_conversations.createIndex({ sessionId: 1, createdAt: -1 });
aiServicesDb.ai_chatbot_conversations.createIndex({ userId: 1 });
aiServicesDb.ai_fraud_detection_cases.createIndex({ status: 1, createdAt: -1 });
aiServicesDb.ai_model_management_models.createIndex({ name: 1, version: -1 }, { unique: true });
aiServicesDb.analytics_events.createIndex({ eventType: 1, timestamp: -1 });
print("  ✅ Indexes created for ai-services");
print("");

// ============================================================================
// DATABASE 2: central-configuration
// ============================================================================
print("📁 Creating database: central-configuration");
const centralConfigDb = conn.getDB("central-configuration");

const centralConfigCollections = [
    "tenancy_configuration",
    "tenancy_settings",
    "feature_flags",
    "feature_flag_audits",
    "dynamic_routing_config",
    "dynamic_routing_rules",
    "country_localization_config",
    "country_localization_strings",
    "release_rollout_config",
    "release_rollout_history",
    "policy_configuration",
    "policy_rules",
    "rate_limit_policy",
    "rate_limit_rules",
    "config_service",
    "config_service_versions"
];

centralConfigCollections.forEach(collection => {
    centralConfigDb.createCollection(collection);
    print("  ✅ Created collection: " + collection);
});

// Create indexes for central-configuration
print("  🔑 Creating indexes for central-configuration...");
centralConfigDb.tenancy_configuration.createIndex({ tenantId: 1 }, { unique: true });
centralConfigDb.feature_flags.createIndex({ name: 1, enabled: 1 });
centralConfigDb.feature_flags.createIndex({ tenantId: 1 });
centralConfigDb.dynamic_routing_config.createIndex({ service: 1, version: -1 });
centralConfigDb.country_localization_config.createIndex({ countryCode: 1 }, { unique: true });
centralConfigDb.policy_configuration.createIndex({ policyType: 1, active: 1 });
centralConfigDb.rate_limit_policy.createIndex({ identifier: 1 }, { unique: true });
centralConfigDb.config_service.createIndex({ key: 1, environment: 1 }, { unique: true });
print("  ✅ Indexes created for central-configuration");
print("");

// ============================================================================
// DATABASE 3: centralized-dashboard
// ============================================================================
print("📁 Creating database: centralized-dashboard");
const dashboardDb = conn.getDB("centralized-dashboard");

const dashboardCollections = [
    "dashboard_analytics_widgets",
    "dashboard_analytics_layouts",
    "dashboard_analytics_data",
    "dashboard_reporting_templates",
    "dashboard_reporting_schedules",
    "dashboard_reporting_history",
    "dashboard_configuration_users",
    "dashboard_configuration_permissions",
    "dashboard_configuration_widgets"
];

dashboardCollections.forEach(collection => {
    dashboardDb.createCollection(collection);
    print("  ✅ Created collection: " + collection);
});

// Create indexes for centralized-dashboard
print("  🔑 Creating indexes for centralized-dashboard...");
dashboardDb.dashboard_analytics_widgets.createIndex({ userId: 1, name: 1 });
dashboardDb.dashboard_reporting_templates.createIndex({ name: 1, createdBy: 1 });
dashboardDb.dashboard_configuration_users.createIndex({ userId: 1 }, { unique: true });
print("  ✅ Indexes created for centralized-dashboard");
print("");

// ============================================================================
// DATABASE 4: shared-libraries
// ============================================================================
print("📁 Creating database: shared-libraries");
const sharedLibsDb = conn.getDB("shared-libraries");

const sharedLibsCollections = [
    "event_schemas",
    "event_schema_versions",
    "shared_ai_contracts",
    "shared_audit_logs",
    "shared_audit_trail",
    "shared_cors_config",
    "shared_dto_schemas",
    "shared_exception_definitions",
    "shared_idempotency_keys",
    "shared_idempotency_records",
    "shared_mapper_configurations",
    "shared_observability_metrics",
    "shared_observability_logs",
    "shared_persistence_entities",
    "shared_request_context",
    "shared_security_policies",
    "shared_validation_rules",
    "common_domain_models",
    "common_entities"
];

sharedLibsCollections.forEach(collection => {
    sharedLibsDb.createCollection(collection);
    print("  ✅ Created collection: " + collection);
});

// Create indexes for shared-libraries
print("  🔑 Creating indexes for shared-libraries...");
sharedLibsDb.event_schemas.createIndex({ eventType: 1, version: -1 });
sharedLibsDb.shared_audit_logs.createIndex({ timestamp: -1, entityType: 1 });
sharedLibsDb.shared_audit_logs.createIndex({ userId: 1, timestamp: -1 });
sharedLibsDb.shared_idempotency_keys.createIndex({ key: 1 }, { unique: true });
sharedLibsDb.shared_observability_metrics.createIndex({ service: 1, timestamp: -1 });
sharedLibsDb.shared_request_context.createIndex({ requestId: 1 }, { unique: true });
sharedLibsDb.common_domain_models.createIndex({ entityType: 1, id: 1 });
print("  ✅ Indexes created for shared-libraries");
print("");

// ============================================================================
// DATABASE 5: shared-infrastructure
// ============================================================================
print("📁 Creating database: shared-infrastructure");
const sharedInfraDb = conn.getDB("shared-infrastructure");

const sharedInfraCollections = [
    "event_audit_logs",
    "logging_aggregation_logs",
    "idempotency_records",
    "rate_limiting_counters",
    "rate_limiting_rules",
    "billing_invoices",
    "billing_payments",
    "billing_subscriptions",
    "courier_adapter_shipments",
    "notification_queue",
    "notification_history",
    "payment_transactions",
    "payment_methods",
    "geo_location_cache",
    "geo_location_history",
    "identity_users",
    "identity_roles",
    "identity_permissions",
    "insurer_adapter_claims",
    "insurer_adapter_policies",
    "integration_adapters_config",
    "metrics_telemetry_data",
    "metrics_telemetry_aggregations",
    "payments_adapter_transactions",
    "policy_engine_rules",
    "policy_engine_evaluations",
    "pricing_rules",
    "pricing_history",
    "request_routing_rules",
    "request_routing_logs",
    "service_health_monitor_status",
    "service_health_monitor_alerts",
    "service_registry_services",
    "service_registry_instances",
    "tenant_org_tenants",
    "tenant_org_organizations",
    "user_profile_profiles",
    "user_profile_preferences",
    "database_management_backups",
    "database_management_maintenance",
    "template_messaging_templates",
    "template_messaging_campaigns",
    "waf_policy_rules",
    "waf_policy_logs",
    "webhook_delivery_queue",
    "webhook_delivery_logs",
    "alerting_alerts",
    "alerting_rules",
    "alerting_escalations",
    "anti_fraud_rules",
    "anti_fraud_signals",
    "anti_fraud_cases",
    "api_keys",
    "api_keys_usage",
    "audit_correlation_mappings",
    "session_token_sessions",
    "session_token_blacklist",
    "onboarding_workflows",
    "onboarding_tasks",
    "currency_converter_rates",
    "currency_converter_cache",
    "identity_access_users",
    "identity_access_roles",
    "identity_access_permissions",
    "maps_geocoding_cache",
    "mfa_codes",
    "mfa_settings",
    "access_control_policies",
    "access_control_roles",
    "api_gateway_routes",
    "api_gateway_config",
    "data_privacy_consent_records",
    "data_privacy_requests",
    "reporting_read_model_views",
    "reporting_read_model_snapshots",
    "database_indexing_jobs",
    "database_indexing_status"
];

sharedInfraCollections.forEach(collection => {
    sharedInfraDb.createCollection(collection);
    print("  ✅ Created collection: " + collection);
});

// Create indexes for shared-infrastructure
print("  🔑 Creating indexes for shared-infrastructure...");
sharedInfraDb.event_audit_logs.createIndex({ timestamp: -1, eventType: 1 });
sharedInfraDb.logging_aggregation_logs.createIndex({ service: 1, timestamp: -1 });
sharedInfraDb.logging_aggregation_logs.createIndex({ level: 1, timestamp: -1 });
sharedInfraDb.idempotency_records.createIndex({ idempotencyKey: 1 }, { unique: true });
sharedInfraDb.rate_limiting_counters.createIndex({ identifier: 1, window: 1 }, { unique: true });
sharedInfraDb.billing_invoices.createIndex({ tenantId: 1, status: 1 });
sharedInfraDb.notification_queue.createIndex({ status: 1, scheduledAt: 1 });
sharedInfraDb.payment_transactions.createIndex({ transactionId: 1 }, { unique: true });
sharedInfraDb.identity_users.createIndex({ email: 1 }, { unique: true });
sharedInfraDb.identity_users.createIndex({ username: 1 }, { unique: true });
sharedInfraDb.service_registry_services.createIndex({ serviceName: 1 }, { unique: true });
sharedInfraDb.service_registry_instances.createIndex({ serviceId: 1, status: 1 });
sharedInfraDb.tenant_org_tenants.createIndex({ tenantId: 1 }, { unique: true });
sharedInfraDb.webhook_delivery_queue.createIndex({ status: 1, retryCount: 1 });
sharedInfraDb.alerting_alerts.createIndex({ status: 1, severity: 1, createdAt: -1 });
sharedInfraDb.api_keys.createIndex({ keyHash: 1 }, { unique: true });
sharedInfraDb.session_token_sessions.createIndex({ token: 1 }, { unique: true });
sharedInfraDb.api_gateway_routes.createIndex({ path: 1, method: 1 }, { unique: true });
print("  ✅ Indexes created for shared-infrastructure");
print("");

// ============================================================================
// DATABASE 6: orchestration-services
// ============================================================================
print("📁 Creating database: orchestration-services");
const orchestrationDb = conn.getDB("orchestration-services");

const orchestrationCollections = [
    "fleet_assistance_requests",
    "fleet_assistance_assignments",
    "fleet_assistance_status",
    "fleet_policy_policies",
    "fleet_policy_rules",
    "fleet_policy_claims",
    "fleet_organization_hierarchy",
    "fleet_organization_units",
    "fleet_organization_members",
    "reporting_reports",
    "reporting_schedules",
    "reporting_templates",
    "fleet_vehicles_vehicles",
    "fleet_vehicles_maintenance",
    "fleet_vehicles_locations",
    "alerting_alerts",
    "alerting_rules",
    "alerting_notifications",
    "dispatching_jobs",
    "dispatching_assignments",
    "dispatching_routes",
    "location_service_locations",
    "location_service_geofences",
    "location_service_history",
    "matching_algorithm_profiles",
    "matching_algorithm_scores",
    "matching_algorithm_preferences",
    "monitoring_service_metrics",
    "monitoring_service_alerts",
    "monitoring_service_dashboards",
    "transaction_orchestration_transactions",
    "transaction_orchestration_sagas",
    "transaction_orchestration_compensations"
];

orchestrationCollections.forEach(collection => {
    orchestrationDb.createCollection(collection);
    print("  ✅ Created collection: " + collection);
});

// Create indexes for orchestration-services
print("  🔑 Creating indexes for orchestration-services...");
orchestrationDb.fleet_assistance_requests.createIndex({ requestId: 1 }, { unique: true });
orchestrationDb.fleet_assistance_requests.createIndex({ status: 1, createdAt: -1 });
orchestrationDb.fleet_policy_policies.createIndex({ policyNumber: 1 }, { unique: true });
orchestrationDb.fleet_organization_hierarchy.createIndex({ tenantId: 1, parentId: 1 });
orchestrationDb.fleet_vehicles_vehicles.createIndex({ vehicleId: 1 }, { unique: true });
orchestrationDb.fleet_vehicles_vehicles.createIndex({ fleetId: 1 });
orchestrationDb.alerting_alerts.createIndex({ status: 1, priority: 1, createdAt: -1 });
orchestrationDb.dispatching_jobs.createIndex({ status: 1, scheduledAt: 1 });
orchestrationDb.location_service_locations.createIndex({ vehicleId: 1, timestamp: -1 });
orchestrationDb.matching_algorithm_scores.createIndex({ requestId: 1, score: -1 });
orchestrationDb.monitoring_service_metrics.createIndex({ service: 1, timestamp: -1 });
orchestrationDb.transaction_orchestration_transactions.createIndex({ transactionId: 1 }, { unique: true });
orchestrationDb.transaction_orchestration_transactions.createIndex({ status: 1, createdAt: -1 });
print("  ✅ Indexes created for orchestration-services");
print("");

// ============================================================================
// SUMMARY
// ============================================================================
print("════════════════════════════════════════════════════════════════════");
print("                    SETUP COMPLETED SUCCESSFULLY                      ");
print("════════════════════════════════════════════════════════════════════");
print("");
print("📊 DATABASES CREATED: 6");
print("   ✅ ai-services");
print("   ✅ central-configuration");
print("   ✅ centralized-dashboard");
print("   ✅ shared-libraries");
print("   ✅ shared-infrastructure");
print("   ✅ orchestration-services");
print("");
print("📋 TOTAL COLLECTIONS: ~170");
print("");
print("🔍 VERIFY IN MONGODB COMPASS:");
print("   mongodb://localhost:27017");
print("");
print("════════════════════════════════════════════════════════════════════");
