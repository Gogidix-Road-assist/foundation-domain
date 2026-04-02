@echo off
REM =============================================================================
REM MongoDB Database Initialization Script for Rapid-Assist Services
REM =============================================================================

echo Connecting to MongoDB...
echo.

REM List of databases to create
set DATABASES=rapid_assist_gateway_dev rapid_assist_registry_dev rapidassist_dev rapid_assist_identity_dev rapid_assist_identity_access_dev rapid_assist_mfa_dev rapid_assist_privacy_dev rapid_assist_waf_dev rapid_assist_audit_dev rapid_assist_billing_dev rapid_assist_payment_dev rapid_assist_payments_adapter_dev rapid_assist_pricing_dev rapid_assist_policy_dev rapid_assit_insurer_adapter_dev rapid_assist_notification_dev rapid_assist_reporting_dev rapid_assist_tenant_dev rapid_assist_user_profile_dev rapid_assist_geo_dev rapid_assist_db_indexing_dev rapid_assist_db_management_dev rapid_assist_idempotency_dev rapid_assist_integration_dev rapid_assist_logging_dev rapid_assist_metrics_dev rapid_assist_routing_dev rapid_assist_health_dev

for %%D in (%DATABASES%) do (
    echo Creating database: %%D
    mongo --quiet localhost:27017/%%D --eval "db.createCollection('system_info'); db.system_info.insertOne({database: '%%D', created_at: new Date(), environment: 'development', version: '1.0.0', status: 'initialized'}); print('Database initialized: %%D');"
    timeout /t 1 /nobreak >nul
)

echo.
echo All databases initialized successfully!
echo.
echo You can now see these databases in MongoDB Compass.
pause
