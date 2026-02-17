const { MongoClient } = require('mongodb');

// Foundation Domain Complete Database Structure
// Following Database Per Service Pattern (2024-2025 Best Practices)

const FOUNDATION_DOMAIN_DATABASES = {
    // ==========================================
    // ORCHESTRATION SERVICES (12 services)
    // ==========================================
    'orchestration_api_gateway_service_db': {
        subdomain: 'orchestration',
        port: 8081,
        collections: {
            routes: { indexes: [{ tenantId: 1 }, { path: 1 }, { isActive: 1 }, { priority: -1 }] },
            route_configs: { indexes: [{ routeId: 1 }, { version: 1 }] },
            gateway_logs: { indexes: [{ timestamp: -1 }, { routeId: 1 }, { statusCode: 1 }] },
            rate_limits: { indexes: [{ clientId: 1 }, { endpoint: 1 }] }
        }
    },
    'orchestration_alerting_service_db': {
        subdomain: 'orchestration',
        port: 8083,
        collections: {
            alerts: { indexes: [{ tenantId: 1 }, { severity: 1 }, { status: 1 }, { createdAt: -1 }] },
            rules: { indexes: [{ tenantId: 1 }, { isActive: 1 }] },
            subscriptions: { indexes: [{ userId: 1 }, { alertType: 1 }] },
            alert_history: { indexes: [{ alertId: 1 }, { timestamp: -1 }] }
        }
    },
    'orchestration_dispatching_service_db': {
        subdomain: 'orchestration',
        port: 8084,
        collections: {
            dispatches: { indexes: [{ tenantId: 1 }, { status: 1 }, { createdAt: -1 }] },
            dispatch_assignments: { indexes: [{ dispatchId: 1 }, { providerId: 1 }] },
            dispatch_tracking: { indexes: [{ dispatchId: 1 }, { timestamp: -1 }] },
            dispatch_metrics: { indexes: [{ providerId: 1 }, { date: -1 }] }
        }
    },
    'orchestration_fleet_assistance_service_db': {
        subdomain: 'orchestration',
        port: 8085,
        collections: {
            fleet_requests: { indexes: [{ tenantId: 1 }, { fleetId: 1 }, { status: 1 }] },
            assistance_history: { indexes: [{ fleetId: 1 }, { timestamp: -1 }] },
            fleet_providers: { indexes: [{ tenantId: 1 }, { isActive: 1 }] },
            service_types: { indexes: [{ category: 1 }] }
        }
    },
    'orchestration_fleet_organization_service_db': {
        subdomain: 'orchestration',
        port: 8086,
        collections: {
            organizations: { indexes: [{ tenantId: 1 }, { parentId: 1 }] },
            fleet_units: { indexes: [{ organizationId: 1 }, { isActive: 1 }] },
            organization_hierarchy: { indexes: [{ ancestorId: 1 }, { descendantId: 1 }] },
            fleet_policies: { indexes: [{ organizationId: 1 }] }
        }
    },
    'orchestration_fleet_policy_service_db': {
        subdomain: 'orchestration',
        port: 8087,
        collections: {
            policies: { indexes: [{ tenantId: 1 }, { fleetId: 1 }, { isActive: 1 }] },
            policy_rules: { indexes: [{ policyId: 1 }, { ruleType: 1 }] },
            policy_compliance: { indexes: [{ fleetId: 1 }, { complianceDate: -1 }] },
            policy_violations: { indexes: [{ fleetId: 1 }, { severity: 1 }] }
        }
    },
    'orchestration_fleet_vehicles_service_db': {
        subdomain: 'orchestration',
        port: 8088,
        collections: {
            vehicles: { indexes: [{ tenantId: 1 }, { fleetId: 1 }, { vin: 1 }] },
            vehicle_maintenance: { indexes: [{ vehicleId: 1 }, { maintenanceDate: -1 }] },
            vehicle_telemetry: { indexes: [{ vehicleId: 1 }, { timestamp: -1 }] },
            vehicle_locations: { indexes: [{ vehicleId: 1 }, { timestamp: -1 }] }
        }
    },
    'orchestration_location_service_db': {
        subdomain: 'orchestration',
        port: 8089,
        collections: {
            locations: { indexes: [{ entityType: 1 }, { entityId: 1 }, { timestamp: -1 }] },
            location_history: { indexes: [{ entityId: 1 }, { timestamp: -1 }] },
            geofences: { indexes: [{ tenantId: 1 }, { isActive: 1 }] },
            location_alerts: { indexes: [{ geofenceId: 1 }, { timestamp: -1 }] }
        }
    },
    'orchestration_matching_service_db': {
        subdomain: 'orchestration',
        port: 8090,
        collections: {
            matching_requests: { indexes: [{ tenantId: 1 }, { status: 1 }, { createdAt: -1 }] },
            matching_results: { indexes: [{ requestId: 1 }, { score: -1 }] },
            matching_criteria: { indexes: [{ serviceType: 1 }] },
            provider_profiles: { indexes: [{ providerId: 1 }, { isActive: 1 }] }
        }
    },
    'orchestration_monitoring_service_db': {
        subdomain: 'orchestration',
        port: 8091,
        collections: {
            monitors: { indexes: [{ tenantId: 1 }, { targetService: 1 }, { isActive: 1 }] },
            monitoring_results: { indexes: [{ monitorId: 1 }, { timestamp: -1 }] },
            alerts: { indexes: [{ monitorId: 1 }, { severity: 1 }] },
            monitoring_configs: { indexes: [{ metricType: 1 }] }
        }
    },
    'orchestration_reporting_service_db': {
        subdomain: 'orchestration',
        port: 8092,
        collections: {
            reports: { indexes: [{ tenantId: 1 }, { reportType: 1 }, { generatedAt: -1 }] },
            report_templates: { indexes: [{ tenantId: 1 }, { category: 1 }] },
            report_schedules: { indexes: [{ templateId: 1 }, { nextRun: 1 }] },
            report_history: { indexes: [{ reportId: 1 }, { timestamp: -1 }] }
        }
    },
    'orchestration_transaction_orchestration_service_db': {
        subdomain: 'orchestration',
        port: 8093,
        collections: {
            transactions: { indexes: [{ tenantId: 1 }, { transactionId: 1 }, { status: 1 }] },
            transaction_steps: { indexes: [{ transactionId: 1 }, { stepOrder: 1 }] },
            transaction_state: { indexes: [{ transactionId: 1 }, { state: 1 }] },
            compensations: { indexes: [{ transactionId: 1 }, { status: 1 }] }
        }
    },

    // ==========================================
    // AI SERVICES (30+ services)
    // ==========================================
    'ai_customer_behaviour_analytics_service_db': {
        subdomain: 'ai',
        port: 8201,
        collections: {
            behaviour_profiles: { indexes: [{ tenantId: 1 }, { customerId: 1 }] },
            behaviour_events: { indexes: [{ customerId: 1 }, { timestamp: -1 }] },
            analytics_results: { indexes: [{ customerId: 1 }, { analysisDate: -1 }] },
            behaviour_segments: { indexes: [{ tenantId: 1 }, { segmentId: 1 }] }
        }
    },
    'ai_customer_support_chatbot_service_db': {
        subdomain: 'ai',
        port: 8202,
        collections: {
            conversations: { indexes: [{ tenantId: 1 }, { customerId: 1 }, { createdAt: -1 }] },
            messages: { indexes: [{ conversationId: 1 }, { timestamp: -1 }] },
            chatbot_configs: { indexes: [{ tenantId: 1 }, { language: 1 }] },
            intents: { indexes: [{ tenantId: 1 }, { intent: 1 }] }
        }
    },
    'ai_document_intelligence_service_db': {
        subdomain: 'ai',
        port: 8203,
        collections: {
            documents: { indexes: [{ tenantId: 1 }, { documentType: 1 }, { uploadedAt: -1 }] },
            document_analysis: { indexes: [{ documentId: 1 }, { analysisType: 1 }] },
            extraction_results: { indexes: [{ documentId: 1 }, { extractedAt: -1 }] },
            document_classifications: { indexes: [{ category: 1 }, { confidence: -1 }] }
        }
    },
    'ai_dynamic_pricing_service_db': {
        subdomain: 'ai',
        port: 8204,
        collections: {
            pricing_models: { indexes: [{ tenantId: 1 }, { serviceType: 1 }, { isActive: 1 }] },
            price_recommendations: { indexes: [{ serviceId: 1 }, { calculatedAt: -1 }] },
            pricing_factors: { indexes: [{ modelId: 1 }, { factorName: 1 }] },
            pricing_history: { indexes: [{ serviceId: 1 }, { effectiveDate: -1 }] }
        }
    },
    'ai_fraud_detection_service_db': {
        subdomain: 'ai',
        port: 8205,
        collections: {
            fraud_models: { indexes: [{ tenantId: 1 }, { modelType: 1 }, { isActive: 1 }] },
            fraud_assessments: { indexes: [{ transactionId: 1 }, { assessedAt: -1 }] },
            fraud_signals: { indexes: [{ assessmentId: 1 }, { signalType: 1 }] },
            fraud_patterns: { indexes: [{ patternType: 1 }, { confidence: -1 }] }
        }
    },
    'ai_intelligent_dispatch_service_db': {
        subdomain: 'ai',
        port: 8206,
        collections: {
            dispatch_optimizations: { indexes: [{ tenantId: 1 }, { optimizationDate: -1 }] },
            dispatch_recommendations: { indexes: [{ requestId: 1 }, { score: -1 }] },
            route_optimizations: { indexes: [{ dispatchId: 1 }, { optimizedAt: -1 }] },
            provider_rankings: { indexes: [{ serviceType: 1 }, { score: -1 }] }
        }
    },
    'ai_recommendation_engine_service_db': {
        subdomain: 'ai',
        port: 8207,
        collections: {
            recommendation_models: { indexes: [{ tenantId: 1 }, { modelType: 1 }, { isActive: 1 }] },
            recommendations: { indexes: [{ userId: 1 }, { generatedAt: -1 }] },
            user_preferences: { indexes: [{ userId: 1 }, { preferenceType: 1 }] },
            item_features: { indexes: [{ itemId: 1 }, { featureType: 1 }] }
        }
    },
    'ai_route_optimization_service_db': {
        subdomain: 'ai',
        port: 8208,
        collections: {
            routes: { indexes: [{ tenantId: 1 }, { optimizedAt: -1 }] },
            route_segments: { indexes: [{ routeId: 1 }, { segmentOrder: 1 }] },
            traffic_data: { indexes: [{ location: '2dsphere' }, { timestamp: -1 }] },
            optimization_history: { indexes: [{ requestId: 1 }, { optimizedAt: -1 }] }
        }
    },
    'ai_vendors_product_listing_service_db': {
        subdomain: 'ai',
        port: 8209,
        collections: {
            vendor_products: { indexes: [{ vendorId: 1 }, { category: 1 }, { isActive: 1 }] },
            product_analytics: { indexes: [{ productId: 1 }, { metricDate: -1 }] },
            search_history: { indexes: [{ userId: 1 }, { searchedAt: -1 }] },
            recommendation_cache: { indexes: [{ queryHash: 1 }, { expiresAt: 1 }] }
        }
    },

    // ==========================================
    // CENTRAL CONFIGURATION (8 services)
    // ==========================================
    'central_config_service_db': {
        subdomain: 'central-config',
        port: 8301,
        collections: {
            config_properties: { indexes: [{ key: 1 }, { environment: 1 }] },
            config_versions: { indexes: [{ key: 1 }, { version: -1 }] },
            config_audits: { indexes: [{ key: 1 }, { changedAt: -1 }] },
            config_environments: { indexes: [{ environment: 1 }] }
        }
    },
    'central_country_localization_config_service_db': {
        subdomain: 'central-config',
        port: 8302,
        collections: {
            country_configs: { indexes: [{ countryCode: 1 }] },
            localized_strings: { indexes: [{ locale: 1 }, { key: 1 }] },
            currency_formats: { indexes: [{ countryCode: 1 }] },
            date_formats: { indexes: [{ countryCode: 1 }] }
        }
    },
    'central_dynamic_routing_config_service_db': {
        subdomain: 'central-config',
        port: 8303,
        collections: {
            routing_rules: { indexes: [{ priority: -1 }, { isActive: 1 }] },
            route_targets: { indexes: [{ ruleId: 1 }, { targetId: 1 }] },
            routing_metrics: { indexes: [{ ruleId: 1 }, { timestamp: -1 }] },
            traffic_distributions: { indexes: [{ targetId: 1 }, { timestamp: -1 }] }
        }
    },
    'central_feature_flags_service_db': {
        subdomain: 'central-config',
        port: 8304,
        collections: {
            feature_flags: { indexes: [{ flagKey: 1 }, { environment: 1 }] },
            flag_overrides: { indexes: [{ flagKey: 1 }, { entityType: 1 }, { entityId: 1 }] },
            flag_audits: { indexes: [{ flagKey: 1 }, { changedAt: -1 }] },
            flag_rollouts: { indexes: [{ flagKey: 1 }, { percentage: 1 }] }
        }
    },
    'central_policy_configuration_service_db': {
        subdomain: 'central-config',
        port: 8305,
        collections: {
            policy_definitions: { indexes: [{ policyType: 1 }, { version: -1 }] },
            policy_rules: { indexes: [{ policyId: 1 }, { ruleOrder: 1 }] },
            policy_variables: { indexes: [{ policyId: 1 }, { variableName: 1 }] },
            policy_evaluations: { indexes: [{ policyId: 1 }, { evaluatedAt: -1 }] }
        }
    },
    'central_rate_limit_policy_service_db': {
        subdomain: 'central-config',
        port: 8306,
        collections: {
            rate_limit_policies: { indexes: [{ policyKey: 1 }, { environment: 1 }] },
            rate_limit_rules: { indexes: [{ policyId: 1 }, { resourceType: 1 }] },
            usage_counters: { indexes: [{ policyId: 1 }, { clientId: 1 }, { windowStart: 1 }] },
            limit_violations: { indexes: [{ policyId: 1 }, { timestamp: -1 }] }
        }
    },
    'central_release_rollout_config_service_db': {
        subdomain: 'central-config',
        port: 8307,
        collections: {
            releases: { indexes: [{ releaseKey: 1 }, { version: -1 }] },
            rollout_phases: { indexes: [{ releaseId: 1 }, { phaseOrder: 1 }] },
            rollout_status: { indexes: [{ releaseId: 1 }, { environment: 1 }] },
            feature_mappings: { indexes: [{ releaseId: 1 }, { featureKey: 1 }] }
        }
    },
    'central_tenancy_configuration_service_db': {
        subdomain: 'central-config',
        port: 8308,
        collections: {
            tenant_configs: { indexes: [{ tenantId: 1 }] },
            tenant_features: { indexes: [{ tenantId: 1 }, { featureKey: 1 }] },
            tenant_overrides: { indexes: [{ tenantId: 1 }, { configKey: 1 }] },
            tenant_tiers: { indexes: [{ tierLevel: 1 }] }
        }
    },

    // ==========================================
    // CENTRALIZED DASHBOARD (3 services)
    // ==========================================
    'dashboard_analytics_service_db': {
        subdomain: 'dashboard',
        port: 8401,
        collections: {
            analytics_widgets: { indexes: [{ dashboardId: 1 }, { widgetOrder: 1 }] },
            analytics_data: { indexes: [{ widgetId: 1 }, { timestamp: -1 }] },
            data_sources: { indexes: [{ sourceType: 1 }, { isActive: 1 }] },
            refresh_schedules: { indexes: [{ widgetId: 1 }, { nextRefresh: 1 }] }
        }
    },
    'dashboard_configuration_service_db': {
        subdomain: 'dashboard',
        port: 8402,
        collections: {
            dashboards: { indexes: [{ tenantId: 1 }, { ownerId: 1 }] },
            dashboard_layouts: { indexes: [{ dashboardId: 1 }, { version: -1 }] },
            dashboard_permissions: { indexes: [{ dashboardId: 1 }, { userId: 1 }] },
            dashboard_templates: { indexes: [{ category: 1 }, { isPublic: 1 }] }
        }
    },
    'dashboard_reporting_service_db': {
        subdomain: 'dashboard',
        port: 8403,
        collections: {
            reports: { indexes: [{ tenantId: 1 }, { reportType: 1 }, { createdAt: -1 }] },
            report_subscriptions: { indexes: [{ reportId: 1 }, { userId: 1 }] },
            report_executions: { indexes: [{ reportId: 1 }, { executedAt: -1 }] },
            export_formats: { indexes: [{ reportId: 1 }, { format: 1 }] }
        }
    }
};

async function setupFoundationDomainDatabases() {
    const client = new MongoClient('mongodb://localhost:27017');

    try {
        console.log('\n╔══════════════════════════════════════════════════════════════════════════════╗');
        console.log('║          FOUNDATION DOMAIN - DATABASE PER SERVICE SETUP                      ║');
        console.log('║                 Following 2024-2025 Microservices Best Practices             ║');
        console.log('╚══════════════════════════════════════════════════════════════════════════════╝\n');

        await client.connect();
        console.log('✓ Connected to MongoDB successfully\n');

        let totalDatabases = 0;
        let totalCollections = 0;
        let totalIndexes = 0;

        // Group by subdomain for better reporting
        const bySubdomain = {};
        for (const [dbName, config] of Object.entries(FOUNDATION_DOMAIN_DATABASES)) {
            if (!bySubdomain[config.subdomain]) {
                bySubdomain[config.subdomain] = [];
            }
            bySubdomain[config.subdomain].push({ dbName, ...config });
        }

        // Process each subdomain
        for (const [subdomain, services] of Object.entries(bySubdomain)) {
            console.log(`\n▶ ${subdomain.toUpperCase()} SUBDOMAIN`);
            console.log('━'.repeat(80));

            for (const service of services) {
                const db = client.db(service.dbName);

                console.log(`\n  📦 Database: ${service.dbName}`);
                console.log(`     Port: ${service.port}`);

                let collectionCount = 0;
                let indexCount = 0;

                for (const [collectionName, indexConfig] of Object.entries(service.collections)) {
                    // Create collection with validation
                    const collection = db.collection(collectionName);

                    // Create indexes
                    const indexSpecs = indexConfig.indexes;
                    for (const spec of indexSpecs) {
                        await collection.createIndex(spec);
                        indexCount++;
                    }

                    collectionCount++;
                    console.log(`     ✓ Collection: ${collectionName} (${indexConfig.indexes.length} indexes)`);
                }

                totalDatabases++;
                totalCollections += collectionCount;
                totalIndexes += indexCount;
            }
        }

        console.log('\n\n╔══════════════════════════════════════════════════════════════════════════════╗');
        console.log('║                    FOUNDATION DOMAIN SETUP COMPLETE                        ║');
        console.log('╚══════════════════════════════════════════════════════════════════════════════╝\n');

        console.log('Summary:');
        console.log(`  Total Databases: ${totalDatabases}`);
        console.log(`  Total Collections: ${totalCollections}`);
        console.log(`  Total Indexes: ${totalIndexes}`);
        console.log(`  Connection: mongodb://localhost:27017\n`);

        console.log('Database Architecture:');
        console.log(`  ✓ Orchestration Services: 12 databases`);
        console.log(`  ✓ AI Services: 9 databases`);
        console.log(`  ✓ Central Configuration: 8 databases`);
        console.log(`  ✓ Centralized Dashboard: 3 databases\n`);

        console.log('✓ Foundation Domain now follows Database Per Service pattern!');
        console.log('✓ Each service has its own dedicated database with clear ownership boundaries.\n');

    } catch (error) {
        console.error('\n❌ Error:', error.message);
        throw error;
    } finally {
        await client.close();
    }
}

// Run the setup
setupFoundationDomainDatabases().catch(console.error);
