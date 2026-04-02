const { MongoClient } = require('mongodb');

async function showCollectionDetails() {
    const client = new MongoClient('mongodb://localhost:27017');

    try {
        await client.connect();
        const db = client.db('shared_infrastructure_dev');
        const collections = await db.listCollections().toArray();

        console.log('\n╔══════════════════════════════════════════════════════════════════════════════╗');
        console.log('║          MONGODB COLLECTIONS - DETAILED STRUCTURE                        ║');
        console.log('╚══════════════════════════════════════════════════════════════════════════════╝\n');

        console.log('Database: shared_infrastructure_dev');
        console.log('Total Collections:', collections.length);
        console.log('\nCollections with Tables (Collections) and Indexes:\n');

        // Group by service prefix
        const grouped = {};
        collections.forEach(coll => {
            const prefix = coll.name.split('_')[0] + '_' + coll.name.split('_')[1];
            if (!grouped[prefix]) grouped[prefix] = [];
            grouped[prefix].push(coll.name);
        });

        for (const [service, collNames] of Object.entries(grouped)) {
            console.log(`\n═══ ${service.toUpperCase()} ═══`);
            for (const collName of collNames.sort()) {
                const indexes = await db.collection(collName).indexes();
                const count = await db.collection(collName).countDocuments();

                console.log(`\n📋 Table/Collection: ${collName}`);
                console.log(`   Documents: ${count}`);
                console.log(`   Indexes (${indexes.length}):`);

                indexes.forEach(idx => {
                    const keyStr = Object.entries(idx.key).map(([k, v]) => `${k}: ${v}`).join(', ');
                    const name = idx.name !== '_id_' ? ` [${idx.name}]` : ' [_id_]';
                    console.log(`     • ${keyStr}${name}`);
                });
            }
        }

        console.log('\n\n╔══════════════════════════════════════════════════════════════════════════════╗');
        console.log('║                    COLLECTION DETAILS COMPLETED                           ║');
        console.log('╚══════════════════════════════════════════════════════════════════════════════╝\n');

    } catch (error) {
        console.error('Error:', error);
    } finally {
        await client.close();
    }
}

showCollectionDetails();
