# MongoDB Database Initialization Script
# This script creates all 27 databases for Rapid-Assist services

$MongoDBPort = 27017
$MongoDBHost = "localhost"
$Databases = @(
    "rapid_assist_gateway_dev",
    "rapid_assist_registry_dev",
    "rapidassist_dev",
    "rapid_assist_identity_dev",
    "rapid_assist_identity_access_dev",
    "rapid_assist_mfa_dev",
    "rapid_assist_privacy_dev",
    "rapid_assist_waf_dev",
    "rapid_assist_audit_dev",
    "rapid_assist_billing_dev",
    "rapid_assist_payment_dev",
    "rapid_assist_payments_adapter_dev",
    "rapid_assist_pricing_dev",
    "rapid_assist_policy_dev",
    "rapid_assit_insurer_adapter_dev",
    "rapid_assist_notification_dev",
    "rapid_assist_reporting_dev",
    "rapid_assist_tenant_dev",
    "rapid_assist_user_profile_dev",
    "rapid_assist_geo_dev",
    "rapid_assist_db_indexing_dev",
    "rapid_assist_db_management_dev",
    "rapid_assist_idempotency_dev",
    "rapid_assist_integration_dev",
    "rapid_assist_logging_dev",
    "rapid_assist_metrics_dev",
    "rapid_assist_routing_dev",
    "rapid_assist_health_dev"
)

Write-Host "=================================================="
Write-Host "MongoDB Database Initialization"
Write-Host "=================================================="
Write-Host ""
Write-Host "Connecting to MongoDB at $MongoDBHost`:$MongoDBPort..."
Write-Host ""

# Try using MongoDB Command Line Tools
$MongoExe = "C:\Program Files\MongoDB\Server\8.2\bin\mongo.exe"

if (Test-Path $MongoExe) {
    Write-Host "Found MongoDB shell: $MongoExe"
    Write-Host ""

    $CreatedCount = 0
    $FailedCount = 0

    foreach ($Database in $Databases) {
        Write-Host "Creating database: $Database"

        try {
            # Create a test collection to initialize the database
            $Command = "$MongoExe $MongoDBHost`:$MongoDBPort/$Database --quiet --eval `"db.createCollection('system_info'); db.system_info.insertOne({database: '$Database', created_at: new Date(), environment: 'development', version: '1.0.0', status: 'initialized'}); print('SUCCESS: ' + '$Database');`""

            $Result = Invoke-Expression $Command 2>&1

            if ($Result -match "SUCCESS") {
                $CreatedCount++
                Write-Host "  Created successfully" -ForegroundColor Green
            } else {
                $FailedCount++
                Write-Host "  Failed to create" -ForegroundColor Red
            }
        } catch {
            $FailedCount++
            Write-Host "  Error: $_" -ForegroundColor Red
        }
    }

    Write-Host ""
    Write-Host "=================================================="
    Write-Host "Initialization Summary"
    Write-Host "=================================================="
    Write-Host "Successfully Created: $CreatedCount databases"
    Write-Host "Failed: $FailedCount databases"
    Write-Host ""

    if ($CreatedCount -gt 0) {
        Write-Host "Databases are now visible in MongoDB Compass!" -ForegroundColor Green
    }

} else {
    Write-Host "ERROR: MongoDB shell not found at: $MongoExe" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please verify MongoDB installation path or create databases manually in MongoDB Compass."
}

Write-Host ""
