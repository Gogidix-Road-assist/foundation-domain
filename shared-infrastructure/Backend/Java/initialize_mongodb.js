// MongoDB initialization script for Rapid-Assist services

// List of databases to create
const databases = [
  'rapid_assist_gateway_dev',
  'rapid_assist_registry_dev',
  'rapidassist_dev',
  'rapid_assist_identity_dev',
  'rapid_assist_identity_access_dev',
  'rapid_assist_mfa_dev',
  'rapid_assist_privacy_dev',
  'rapid_assist_waf_dev',
  'rapid_assist_audit_dev',
  'rapid_assist_billing_dev',
  'rapid_assist_payment_dev',
  'rapid_assist_payments_adapter_dev',
  'rapid_assist_pricing_dev',
  'rapid_assist_policy_dev',
  'rapid_assit_insurer_adapter_dev',
  'rapid_assist_notification_dev',
  'rapid_assist_reporting_dev',
  'rapid_assist_tenant_dev',
  'rapid_assist_user_profile_dev',
  'rapid_assist_geo_dev',
  'rapid_assist_db_indexing_dev',
  'rapid_assist_db_management_dev',
  'rapid_assist_idempotency_dev',
  'rapid_assist_integration_dev',
  'rapid_assist_logging_dev',
  'rapid_assist_metrics_dev',
  'rapid_assist_routing_dev',
  'rapid_assist_health_dev'
];

// Create each database and add initial collections
databases.forEach(dbName => {
  const db = db.getSiblingDB(dbName);
  
  // Create a test collection to initialize the database
  db.createCollection('system_info');
  
  // Insert system information
  db.system_info.insertOne({
    database: dbName,
    created_at: new Date(),
    environment: 'development',
    version: '1.0.0',
    status: 'initialized'
  });
  
  // Create indexes for common fields
  db.system_info.createIndex({ created_at: 1 });
  db.system_info.createIndex({ environment: 1 });
  
  print('Database initialized: ' + dbName);
});

print('All databases initialized successfully!');
