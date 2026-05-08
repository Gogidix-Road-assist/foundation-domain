const { MongoClient } = require('mongodb');

async function dropOldDatabases() {
    const client = new MongoClient('mongodb://localhost:27017');

    try {
        await client.connect();
        const db = client.db();
        const databases = await db.admin().listDatabases();

        // Filter out the new shared_infrastructure_dev and system databases
        const oldDbs = databases.databases.filter(d =>
            (d.name.startsWith('rapid_assist_') || d.name === 'rapidassist') &&
            d.name !== 'shared_infrastructure_dev' &&
            !['admin', 'config', 'local'].includes(d.name)
        );

        console.log(`\nDropping ${oldDbs.length} old individual service databases...\n`);

        for (const dbInfo of oldDbs) {
            await client.db(dbInfo.name).dropDatabase();
            console.log(`✓ Dropped: ${dbInfo.name}`);
        }

        console.log('\n✓ All old databases dropped successfully!');
        console.log('\nRemaining databases:');
        const remaining = (await db.admin().listDatabases()).databases;
        remaining.forEach(d => console.log(`  - ${d.name}`));

    } catch (error) {
        console.error('Error:', error);
    } finally {
        await client.close();
    }
}

dropOldDatabases();
