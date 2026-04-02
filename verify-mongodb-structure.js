/**
 * MongoDB Structure Verification Script
 *
 * Verifies all databases and collections exist and shows document counts
 */

const { MongoClient } = require('mongodb');

const CONNECTION_URL = 'mongodb://localhost:27017';

// Expected databases
const FOUNDATION_DATABASES = [
    'Foundation-Domain-AI-Services',
    'Foundation-Domain-Central-Configuration',
    'Foundation-Domain-Centralized-Dashboard',
    'Foundation-Domain-Shared-Libraries',
    'Foundation-Domain-Shared-Infrastructure',
    'Foundation-Domain-Orchestration-Services'
];

const MANAGEMENT_DATABASES = [
    'Management-Domain-Bridge-Services',
    'Management-Domain-Central-Monitoring',
    'Management-Domain-Compliance-Risk',
    'Management-Domain-Country-Admin',
    'Management-Domain-Customer-Support',
    'Management-Domain-Digital-Marketing',
    'Management-Domain-Executive-Command',
    'Management-Domain-Finance-Settlement',
    'Management-Domain-Global-Admin',
    'Management-Domain-HR',
    'Management-Domain-Pricing-Policy',
    'Management-Domain-Sales',
    'Management-Domain-Shared-Services',
    'Management-Domain-Shared-Libraries'
];

async function verifyStructure() {
    const client = new MongoClient(CONNECTION_URL);

    console.log('════════════════════════════════════════════════════════════════════');
    console.log('              MONGODB STRUCTURE VERIFICATION                        ');
    console.log('════════════════════════════════════════════════════════════════════');
    console.log('');

    try {
        await client.connect();
        console.log('✅ Connected to MongoDB at mongodb://localhost:27017');
        console.log('');

        const adminDb = client.db().admin();
        const result = await adminDb.listDatabases();
        const existingDbs = result.databases.map(d => d.name);

        // Check Foundation-Domain databases
        console.log('════════════════════════════════════════════════════════════════════');
        console.log('                    FOUNDATION DOMAIN                              ');
        console.log('════════════════════════════════════════════════════════════════════');

        let foundationTotal = 0;
        let foundationDocCount = 0;

        for (const dbName of FOUNDATION_DATABASES) {
            if (existingDbs.includes(dbName)) {
                const db = client.db(dbName);
                const collections = await db.listCollections().toArray();
                let dbDocCount = 0;

                console.log(`\n📁 ${dbName}`);
                console.log(`   Collections: ${collections.length}`);

                for (const col of collections) {
                    const count = await db.collection(col.name).countDocuments();
                    dbDocCount += count;
                    foundationTotal++;
                    const status = count > 0 ? `📊 ${count} docs` : '🔴 EMPTY';
                    console.log(`   ${status} - ${col.name}`);
                }
                foundationDocCount += dbDocCount;
                console.log(`   └── Total documents: ${dbDocCount}`);
            } else {
                console.log(`❌ MISSING: ${dbName}`);
            }
        }

        console.log('\n════════════════════════════════════════════════════════════════════');
        console.log('                    MANAGEMENT DOMAIN                              ');
        console.log('════════════════════════════════════════════════════════════════════');

        let managementTotal = 0;
        let managementDocCount = 0;

        for (const dbName of MANAGEMENT_DATABASES) {
            if (existingDbs.includes(dbName)) {
                const db = client.db(dbName);
                const collections = await db.listCollections().toArray();
                let dbDocCount = 0;

                console.log(`\n📁 ${dbName}`);
                console.log(`   Collections: ${collections.length}`);

                // Show first 3 collections as sample (to avoid too much output)
                for (let i = 0; i < collections.length; i++) {
                    const col = collections[i];
                    const count = await db.collection(col.name).countDocuments();
                    dbDocCount += count;
                    managementTotal++;
                    const status = count > 0 ? `📊 ${count} docs` : '🔴 EMPTY';
                    console.log(`   ${status} - ${col.name}`);
                }
                if (collections.length > 3) {
                    console.log(`   ... and ${collections.length - 3} more collections`);
                }
                managementDocCount += dbDocCount;
                console.log(`   └── Total documents: ${dbDocCount}`);
            } else {
                console.log(`❌ MISSING: ${dbName}`);
            }
        }

        console.log('\n════════════════════════════════════════════════════════════════════');
        console.log('                        SUMMARY                                    ');
        console.log('════════════════════════════════════════════════════════════════════');
        console.log('');
        console.log(`Foundation-Domain: ${foundationTotal} collections, ${foundationDocCount} documents`);
        console.log(`Management-Domain:  ${managementTotal} collections, ${managementDocCount} documents`);
        console.log(`TOTAL: ${foundationTotal + managementTotal} collections, ${foundationDocCount + managementDocCount} documents`);
        console.log('');

        if (foundationDocCount + managementDocCount === 0) {
            console.log('⚠️  ALL COLLECTIONS ARE EMPTY!');
            console.log('');
            console.log('Next steps:');
            console.log('  1. Insert sample/test data for development?');
            console.log('  2. Connect your services to populate real data?');
        } else {
            console.log('✅ Collections contain data!');
        }

        console.log('');
        console.log('════════════════════════════════════════════════════════════════════');

    } catch (err) {
        console.error('❌ Error:', err);
    } finally {
        await client.close();
    }
}

verifyStructure().catch(console.error);
