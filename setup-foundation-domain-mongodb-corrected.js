const { MongoClient } = require('mongodb');

// Foundation Domain Complete Database Structure - CORRECTED
// Following Database Per Service Pattern (2024-2025 Best Practices)
// This script creates databases for ACTUAL services found in the codebase

const FOUNDATION_DOMAIN_DATABASES = {
    // ==========================================
    // SHARED INFRASTRUCTURE - MISSING DATABASES (2)
    // ==========================================
    'maps_geocoding_adapter_service_db': {
        subdomain: 'shared-infrastructure',
        port: 8120,
        collections: {
            maps_configs: { indexes: [{ provider: 1 }, { isActive: 1 }] },
            geocoding_cache: { indexes: [{ queryHash: 1 }, { expiresAt: 1 }] },
            geocoding_history: { indexes: [{ timestamp: -1 }, { queryType: 1 }] }
        }
    },
    'metrics_telemetry_service_db': {
        subdomain: 'shared-infrastructure',
        port: 8121,
        collections: {
            metrics: { indexes: [{ tenantId: 1 }, { metricName: 1 }, { timestamp: -1 }] },
            telemetry_data: { indexes: [{ sourceService: 1 }, { timestamp: -1 }] },
            metric_aggregations: { indexes: [{ aggregationWindow: 1 }, { metricName: 1 }] }
        }
    },

    // ==========================================
    // ORCHESTRATION SERVICES (10 databases)
    // Note: These already exist but included for completeness
    // ==========================================
    'orchestration_alerting_service_db': {
        subdomain: 'orchestration',
        port: 8083,
        collections: {
            alerts: { indexes: [{ tenantId: 1 }, { severity: 1 }, { status: 1 }] },
            rules: { indexes: [{ tenantId: 1 }, { isActive: 1 }] },
            subscriptions: { indexes: [{ userId: 1 }, { alertType: 1 }] }
        }
    },
    'orchestration_dispatching_service_db': {
        subdomain: 'orchestration',
        port: 8084,
        collections: {
            dispatches: { indexes: [{ tenantId: 1 }, { status: 1 }] },
            assignments: { indexes: [{ dispatchId: 1 }, { providerId: 1 }] },
            tracking: { indexes: [{ dispatchId: 1 }, { timestamp: -1 }] }
        }
    },
    'orchestration_fleet_assistance_service_db': {
        subdomain: 'orchestration',
        port: 8085,
        collections: {
            requests: { indexes: [{ tenantId: 1 }, { fleetId: 1 }, { status: 1 }] },
            history: { indexes: [{ fleetId: 1 }, { timestamp: -1 }] },
            providers: { indexes: [{ tenantId: 1 }, { isActive: 1 }] }
        }
    },
    'orchestration_fleet_organization_service_db': {
        subdomain: 'orchestration',
        port: 8086,
        collections: {
            organizations: { indexes: [{ tenantId: 1 }, { parentId: 1 }] },
            units: { indexes: [{ organizationId: 1 }, { isActive: 1 }] },
            hierarchy: { indexes: [{ ancestorId: 1 }, { descendantId: 1 }] }
        }
    },
    'orchestration_fleet_policy_service_db': {
        subdomain: 'orchestration',
        port: 8087,
        collections: {
            policies: { indexes: [{ tenantId: 1 }, { fleetId: 1 }, { isActive: 1 }] },
            rules: { indexes: [{ policyId: 1 }, { ruleType: 1 }] },
            compliance: { indexes: [{ fleetId: 1 }, { complianceDate: -1 }] }
        }
    },
    'orchestration_fleet_vehicles_service_db': {
        subdomain: 'orchestration',
        port: 8088,
        collections: {
            vehicles: { indexes: [{ tenantId: 1 }, { fleetId: 1 }, { vin: 1 }] },
            maintenance: { indexes: [{ vehicleId: 1 }, { maintenanceDate: -1 }] },
            telemetry: { indexes: [{ vehicleId: 1 }, { timestamp: -1 }] }
        }
    },
    'orchestration_location_service_db': {
        subdomain: 'orchestration',
        port: 8089,
        collections: {
            locations: { indexes: [{ entityType: 1 }, { entityId: 1 }, { timestamp: -1 }] },
            history: { indexes: [{ entityId: 1 }, { timestamp: -1 }] },
            geofences: { indexes: [{ tenantId: 1 }, { isActive: 1 }] }
        }
    },
    'orchestration_matching_service_db': {
        subdomain: 'orchestration',
        port: 8090,
        collections: {
            requests: { indexes: [{ tenantId: 1 }, { status: 1 }, { createdAt: -1 }] },
            results: { indexes: [{ requestId: 1 }, { score: -1 }] },
            profiles: { indexes: [{ providerId: 1 }, { isActive: 1 }] }
        }
    },
    'orchestration_monitoring_service_db': {
        subdomain: 'orchestration',
        port: 8091,
        collections: {
            monitors: { indexes: [{ tenantId: 1 }, { targetService: 1 }, { isActive: 1 }] },
            results: { indexes: [{ monitorId: 1 }, { timestamp: -1 }] },
            alerts: { indexes: [{ monitorId: 1 }, { severity: 1 }] }
        }
    },
    'orchestration_reporting_service_db': {
        subdomain: 'orchestration',
        port: 8092,
        collections: {
            reports: { indexes: [{ tenantId: 1 }, { reportType: 1 }, { generatedAt: -1 }] },
            templates: { indexes: [{ tenantId: 1 }, { category: 1 }] },
            schedules: { indexes: [{ templateId: 1 }, { nextRun: 1 }] }
        }
    },
    'orchestration_transaction_orchestration_service_db': {
        subdomain: 'orchestration',
        port: 8093,
        collections: {
            transactions: { indexes: [{ tenantId: 1 }, { transactionId: 1 }, { status: 1 }] },
            steps: { indexes: [{ transactionId: 1 }, { stepOrder: 1 }] },
            state: { indexes: [{ transactionId: 1 }, { state: 1 }] }
        }
    },

    // ==========================================
    // AI SERVICES - CORRECTED (30 databases)
    // These match the ACTUAL service names in the codebase
    // ==========================================
    'ai_anomaly_detection_service_db': {
        subdomain: 'ai',
        port: 8201,
        collections: {
            anomaly_models: { indexes: [{ tenantId: 1 }, { modelType: 1 }, { isActive: 1 }] },
            detections: { indexes: [{ tenantId: 1 }, { severity: 1 }, { detectedAt: -1 }] },
            patterns: { indexes: [{ patternType: 1 }, { confidence: -1 }] }
        }
    },
    'ai_automated_tagging_service_db': {
        subdomain: 'ai',
        port: 8202,
        collections: {
            tagging_models: { indexes: [{ tenantId: 1 }, { modelType: 1 }, { isActive: 1 }] },
            tags: { indexes: [{ entityType: 1 }, { entityId: 1 }] },
            tagging_history: { indexes: [{ entityId: 1 }, { taggedAt: -1 }] }
        }
    },
    'ai_bi_analytics_service_db': {
        subdomain: 'ai',
        port: 8203,
        collections: {
            analytics_queries: { indexes: [{ tenantId: 1 }, { queryType: 1 }] },
            dashboards: { indexes: [{ tenantId: 1 }, { dashboardType: 1 }] },
            analytics_results: { indexes: [{ queryId: 1 }, { generatedAt: -1 }] }
        }
    },
    'ai_categorization_service_db': {
        subdomain: 'ai',
        port: 8204,
        collections: {
            categories: { indexes: [{ tenantId: 1 }, { categoryType: 1 }] },
            classification_models: { indexes: [{ modelType: 1 }, { isActive: 1 }] },
            classifications: { indexes: [{ entityId: 1 }, { classifiedAt: -1 }] }
        }
    },
    'ai_chatbot_service_db': {
        subdomain: 'ai',
        port: 8205,
        collections: {
            conversations: { indexes: [{ tenantId: 1 }, { userId: 1 }, { createdAt: -1 }] },
            messages: { indexes: [{ conversationId: 1 }, { timestamp: -1 }] },
            intents: { indexes: [{ intent: 1 }, { confidence: -1 }] }
        }
    },
    'ai_computer_vision_service_db': {
        subdomain: 'ai',
        port: 8206,
        collections: {
            vision_models: { indexes: [{ modelType: 1 }, { isActive: 1 }] },
            detections: { indexes: [{ imageId: 1 }, { detectionType: 1 }] },
            analysis_results: { indexes: [{ imageId: 1 }, { analyzedAt: -1 }] }
        }
    },
    'ai_content_moderation_service_db': {
        subdomain: 'ai',
        port: 8207,
        collections: {
            moderation_rules: { indexes: [{ tenantId: 1 }, { ruleType: 1 }, { isActive: 1 }] },
            moderations: { indexes: [{ contentId: 1 }, { moderationResult: 1 }] },
            moderation_history: { indexes: [{ contentId: 1 }, { moderatedAt: -1 }] }
        }
    },
    'ai_data_quality_service_db': {
        subdomain: 'ai',
        port: 8208,
        collections: {
            quality_rules: { indexes: [{ tenantId: 1 }, { ruleType: 1 }, { isActive: 1 }] },
            quality_checks: { indexes: [{ dataSource: 1 }, { qualityScore: -1 }] },
            issues: { indexes: [{ severity: 1 }, { detectedAt: -1 }] }
        }
    },
    'ai_forecasting_service_db': {
        subdomain: 'ai',
        port: 8209,
        collections: {
            forecasting_models: { indexes: [{ tenantId: 1 }, { modelType: 1 }, { isActive: 1 }] },
            forecasts: { indexes: [{ targetMetric: 1 }, { forecastDate: -1 }] },
            forecast_accuracy: { indexes: [{ modelId: 1 }, { accuracyDate: -1 }] }
        }
    },
    'ai_fraud_detection_service_db': {
        subdomain: 'ai',
        port: 8210,
        collections: {
            fraud_models: { indexes: [{ tenantId: 1 }, { modelType: 1 }, { isActive: 1 }] },
            assessments: { indexes: [{ transactionId: 1 }, { assessedAt: -1 }] },
            fraud_signals: { indexes: [{ assessmentId: 1 }, { signalType: 1 }] }
        }
    },
    'ai_gateway_service_db': {
        subdomain: 'ai',
        port: 8211,
        collections: {
            ai_services: { indexes: [{ serviceName: 1 }, { isActive: 1 }] },
            requests: { indexes: [{ serviceId: 1 }, { timestamp: -1 }] },
            rate_limits: { indexes: [{ serviceId: 1 }, { clientId: 1 }] }
        }
    },
    'ai_image_recognition_service_db': {
        subdomain: 'ai',
        port: 8212,
        collections: {
            recognition_models: { indexes: [{ modelType: 1 }, { isActive: 1 }] },
            recognitions: { indexes: [{ imageId: 1 }, { recognizedAt: -1 }] },
            labels: { indexes: [{ label: 1 }, { confidence: -1 }] }
        }
    },
    'ai_inference_service_db': {
        subdomain: 'ai',
        port: 8213,
        collections: {
            models: { indexes: [{ modelType: 1 }, { isActive: 1 }] },
            inferences: { indexes: [{ modelId: 1 }, { inferredAt: -1 }] },
            inference_logs: { indexes: [{ modelId: 1 }, { timestamp: -1 }] }
        }
    },
    'ai_matching_algorithm_service_db': {
        subdomain: 'ai',
        port: 8214,
        collections: {
            matching_models: { indexes: [{ algorithmType: 1 }, { isActive: 1 }] },
            matches: { indexes: [{ requestId: 1 }, { score: -1 }] },
            match_history: { indexes: [{ requestId: 1 }, { matchedAt: -1 }] }
        }
    },
    'ai_model_management_service_db': {
        subdomain: 'ai',
        port: 8215,
        collections: {
            models: { indexes: [{ tenantId: 1 }, { modelType: 1 }, { version: -1 }] },
            model_versions: { indexes: [{ modelId: 1 }, { version: -1 }] },
            deployments: { indexes: [{ modelId: 1 }, { deploymentStatus: 1 }] }
        }
    },
    'ai_nlp_processing_service_db': {
        subdomain: 'ai',
        port: 8216,
        collections: {
            nlp_models: { indexes: [{ modelType: 1 }, { language: 1 }] },
            processing_jobs: { indexes: [{ status: 1 }, { createdAt: -1 }] },
            extracted_entities: { indexes: [{ entityType: 1 }, { entityId: 1 }] }
        }
    },
    'ai_optimization_service_db': {
        subdomain: 'ai',
        port: 8217,
        collections: {
            optimization_models: { indexes: [{ algorithmType: 1 }, { isActive: 1 }] },
            optimizations: { indexes: [{ targetId: 1 }, { optimizedAt: -1 }] },
            optimization_results: { indexes: [{ optimizationId: 1 }, { score: -1 }] }
        }
    },
    'ai_personalization_service_db': {
        subdomain: 'ai',
        port: 8218,
        collections: {
            user_profiles: { indexes: [{ tenantId: 1 }, { userId: 1 }] },
            personalization_rules: { indexes: [{ ruleType: 1 }, { isActive: 1 }] },
            recommendations: { indexes: [{ userId: 1 }, { generatedAt: -1 }] }
        }
    },
    'ai_predictive_analytics_service_db': {
        subdomain: 'ai',
        port: 8219,
        collections: {
            prediction_models: { indexes: [{ tenantId: 1 }, { modelType: 1 }, { isActive: 1 }] },
            predictions: { indexes: [{ targetId: 1 }, { predictionDate: -1 }] },
            prediction_accuracy: { indexes: [{ modelId: 1 }, { accuracy: -1 }] }
        }
    },
    'ai_pricing_engine_service_db': {
        subdomain: 'ai',
        port: 8220,
        collections: {
            pricing_models: { indexes: [{ tenantId: 1 }, { serviceType: 1 }, { isActive: 1 }] },
            price_recommendations: { indexes: [{ serviceId: 1 }, { calculatedAt: -1 }] },
            pricing_factors: { indexes: [{ modelId: 1 }, { factorName: 1 }] }
        }
    },
    'ai_recommendation_service_db': {
        subdomain: 'ai',
        port: 8221,
        collections: {
            recommendation_models: { indexes: [{ tenantId: 1 }, { modelType: 1 }, { isActive: 1 }] },
            recommendations: { indexes: [{ userId: 1 }, { generatedAt: -1 }] },
            user_preferences: { indexes: [{ userId: 1 }, { preferenceType: 1 }] }
        }
    },
    'ai_report_generation_service_db': {
        subdomain: 'ai',
        port: 8222,
        collections: {
            report_templates: { indexes: [{ tenantId: 1 }, { reportType: 1 }] },
            generated_reports: { indexes: [{ templateId: 1 }, { generatedAt: -1 }] },
            report_schedules: { indexes: [{ templateId: 1 }, { nextRun: 1 }] }
        }
    },
    'ai_risk_assessment_service_db': {
        subdomain: 'ai',
        port: 8223,
        collections: {
            risk_models: { indexes: [{ tenantId: 1 }, { riskType: 1 }, { isActive: 1 }] },
            assessments: { indexes: [{ targetId: 1 }, { assessedAt: -1 }] },
            risk_factors: { indexes: [{ assessmentId: 1 }, { factorType: 1 }] }
        }
    },
    'ai_search_optimization_service_db': {
        subdomain: 'ai',
        port: 8224,
        collections: {
            search_analytics: { indexes: [{ queryHash: 1 }, { timestamp: -1 }] },
            optimization_rules: { indexes: [{ ruleType: 1 }, { isActive: 1 }] },
            search_results: { indexes: [{ queryId: 1 }, { relevance: -1 }] }
        }
    },
    'ai_sentiment_analysis_service_db': {
        subdomain: 'ai',
        port: 8225,
        collections: {
            sentiment_models: { indexes: [{ modelType: 1 }, { language: 1 }] },
            analyses: { indexes: [{ contentId: 1 }, { analyzedAt: -1 }] },
            sentiment_scores: { indexes: [{ contentId: 1 }, { sentiment: 1 }] }
        }
    },
    'ai_speech_recognition_service_db': {
        subdomain: 'ai',
        port: 8226,
        collections: {
            recognition_models: { indexes: [{ modelType: 1 }, { language: 1 }] },
            transcriptions: { indexes: [{ audioId: 1 }, { transcribedAt: -1 }] },
            recognition_logs: { indexes: [{ audioId: 1 }, { accuracy: -1 }] }
        }
    },
    'ai_summarization_service_db': {
        subdomain: 'ai',
        port: 8227,
        collections: {
            summarization_models: { indexes: [{ modelType: 1 }, { language: 1 }] },
            summaries: { indexes: [{ contentId: 1 }, { summarizedAt: -1 }] },
            summary_templates: { indexes: [{ templateType: 1 }, { isActive: 1 }] }
        }
    },
    'ai_summization_service_db': {
        subdomain: 'ai',
        port: 8228,
        collections: {
            summization_models: { indexes: [{ modelType: 1 }, { language: 1 }] },
            summizations: { indexes: [{ contentId: 1 }, { summizedAt: -1 }] },
            summization_templates: { indexes: [{ templateType: 1 }, { isActive: 1 }] }
        }
    },
    'ai_translation_service_db': {
        subdomain: 'ai',
        port: 8229,
        collections: {
            translation_models: { indexes: [{ sourceLanguage: 1 }, { targetLanguage: 1 }] },
            translations: { indexes: [{ contentId: 1 }, { translatedAt: -1 }] },
            translation_cache: { indexes: [{ sourceHash: 1 }, { expiresAt: 1 }] }
        }
    },
    'analytics_service_db': {
        subdomain: 'ai',
        port: 8230,
        collections: {
            analytics_events: { indexes: [{ tenantId: 1 }, { eventType: 1 }, { timestamp: -1 }] },
            aggregations: { indexes: [{ aggregationType: 1 }, { timestamp: -1 }] },
            analytics_configs: { indexes: [{ configType: 1 }, { isActive: 1 }] }
        }
    }
};

async function setupFoundationDomainDatabases() {
    const client = new MongoClient('mongodb://localhost:27017');

    try {
        console.log('\n╔══════════════════════════════════════════════════════════════════════════════╗');
        console.log('║    FOUNDATION DOMAIN - CORRECTED DATABASE PER SERVICE SETUP                  ║');
        console.log('║           Following 2024-2025 Microservices Best Practices                     ║');
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
                    const collection = db.collection(collectionName);

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
        console.log(`  ✓ Shared Infrastructure (Missing): 2 databases`);
        console.log(`  ✓ Orchestration Services: 11 databases`);
        console.log(`  ✓ AI Services (Corrected): 30 databases`);
        console.log(`  ✓ Central Configuration: 8 databases (already exist)`);
        console.log(`  ✓ Centralized Dashboard: 3 databases (already exist)`);
        console.log(`  ✓ Total Foundation Domain: 54 databases\n`);

        console.log('✓ Foundation Domain now follows Database Per Service pattern!');
        console.log('✓ AI services databases now match actual service names!');
        console.log('✓ Each service has its own dedicated database with clear ownership boundaries.\n');

    } catch (error) {
        console.error('\n❌ Error:', error.message);
        throw error;
    } finally {
        await client.close();
    }
}

setupFoundationDomainDatabases().catch(console.error);
