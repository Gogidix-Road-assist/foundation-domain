const { MongoClient } = require('mongodb');

// Databases to drop - these were created with wrong service names
const INCORRECT_AI_DATABASES = [
    'ai_customer_behaviour_analytics_service_db',
    'ai_customer_support_chatbot_service_db',
    'ai_document_intelligence_service_db',
    'ai_dynamic_pricing_service_db',
    'ai_fraud_detection_service_db', // This will be recreated with correct name
    'ai_intelligent_dispatch_service_db',
    'ai_recommendation_engine_service_db',
    'ai_route_optimization_service_db',
    'ai_vendors_product_listing_service_db'
];

async function dropIncorrectAIDatabases() {
    const client = new MongoClient('mongodb://localhost:27017');

    try {
        console.log('\n╔══════════════════════════════════════════════════════════════════════════════╗');
        console.log('║              DROPPING INCORRECT AI SERVICE DATABASES                        ║');
        console.log('╚══════════════════════════════════════════════════════════════════════════════╝\n');

        await client.connect();
        console.log('✓ Connected to MongoDB successfully\n');

        let droppedCount = 0;
        let notFoundCount = 0;

        for (const dbName of INCORRECT_AI_DATABASES) {
            try {
                const db = client.db(dbName);

                // Check if database exists by listing collections
                const collections = await db.listCollections().toArray();

                if (collections.length > 0) {
                    // Drop the database by dropping all collections
                    for (const coll of collections) {
                        await db.collection(coll.name).drop();
                    }
                    console.log(`  ✓ Dropped: ${dbName}`);
                    droppedCount++;
                } else {
                    console.log(`  ⚠ Skipped (not found): ${dbName}`);
                    notFoundCount++;
                }
            } catch (error) {
                if (error.message.includes('ns not found')) {
                    console.log(`  ⚠ Skipped (not found): ${dbName}`);
                    notFoundCount++;
                } else {
                    console.log(`  ❌ Error dropping ${dbName}:`, error.message);
                }
            }
        }

        console.log('\n\n╔══════════════════════════════════════════════════════════════════════════════╗');
        console.log('║                    CLEANUP COMPLETE                                         ║');
        console.log('╚══════════════════════════════════════════════════════════════════════════════╝\n');

        console.log('Summary:');
        console.log(`  Databases Dropped: ${droppedCount}`);
        console.log(`  Databases Not Found: ${notFoundCount}`);
        console.log(`  Total Processed: ${INCORRECT_AI_DATABASES.length}\n`);

        console.log('✓ Incorrect AI service databases have been removed!');
        console.log('✓ Ready to create databases with correct service names!\n');

    } catch (error) {
        console.error('\n❌ Error:', error.message);
        throw error;
    } finally {
        await client.close();
    }
}

dropIncorrectAIDatabases().catch(console.error);
