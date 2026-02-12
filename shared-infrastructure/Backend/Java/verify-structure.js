const { MongoClient } = require('mongodb');

async function verifyStructure() {
    const client = new MongoClient('mongodb://localhost:27017');

    try {
        await client.connect();
        const db = client.db('shared_infrastructure_dev');
        const collections = await db.listCollections().toArray();

        console.log('\n╔══════════════════════════════════════════════════════════════════════════════╗');
        console.log('║          SHARED INFRASTRUCTURE DATABASE - VERIFICATION                  ║');
        console.log('╚══════════════════════════════════════════════════════════════════════════════╝\n');

        console.log('Database: shared_infrastructure_dev');
        console.log('Total Collections:', collections.length);
        console.log('\nCollections (sorted):');

        const sorted = collections.map(c => c.name).sort();
        sorted.forEach((name, i) => {
            console.log(`  ${(i+1).toString().padStart(3)}. ${name}`);
        });

        console.log('\n╔══════════════════════════════════════════════════════════════════════════════╗');
        console.log('║                    VERIFICATION COMPLETED                                ║');
        console.log('╚══════════════════════════════════════════════════════════════════════════════╝\n');

        console.log('MongoDB Compass Connection:');
        console.log('  mongodb://localhost:27017/shared_infrastructure_dev\n');

    } catch (error) {
        console.error('Error:', error);
    } finally {
        await client.close();
    }
}

verifyStructure();
