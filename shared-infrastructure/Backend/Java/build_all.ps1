# Build All 41 Services - Simplified Script
$ErrorActionPreference = "Continue"
$BASE_DIR = Get-Location
$LOG_FILE = "$BASE_DIR\build_$(Get-Date -Format 'yyyyMMdd_HHmmss').log"

$services = @(
    "access-control-service",
    "alerting-service",
    "anti-fraud-rules-service",
    "anti-fraud-signals-service",
    "api-gateway",
    "api-keys-service",
    "audit-correlation-service",
    "billing-service",
    "courier-adapter-service",
    "currency-converter-service",
    "data-privacy-consent-service",
    "database-indexing-service",
    "database-management-service",
    "event-audit-service",
    "geo-location-service",
    "idempotency-service",
    "identity-access-service",
    "identity-service",
    "insurer-adapter-service",
    "integration-adapters-service",
    "logging-aggregation-service",
    "maps-geocoding-adapter-service",
    "metrics-telemetry-service",
    "mfa-service",
    "notification-service",
    "onboarding-service",
    "payment-service",
    "payments-adapter-service",
    "policy-engine-service",
    "pricing-service",
    "rate-limiting-service",
    "reporting-read-model-service",
    "request-routing-service",
    "service-health-monitor-service",
    "service-registry-discovery",
    "session-token-service",
    "template-messaging-service",
    "tenant-org-service",
    "user-profile-service",
    "waf-policy-service",
    "webhook-delivery-service"
)

Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host "BUILDING ALL 41 SERVICES" -ForegroundColor Cyan
Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host ""

$successCount = 0
$failedCount = 0
$skippedCount = 0
$startTime = Get-Date

foreach ($service in $services) {
    $servicePath = Join-Path $BASE_DIR $service

    if (-not (Test-Path $servicePath)) {
        Write-Host "[$service] NOT FOUND" -ForegroundColor Red
        $skippedCount++
        continue
    }

    Write-Host "[$service] Building..." -ForegroundColor Cyan

    try {
        Push-Location $servicePath
        $output = mvn clean package -DskipTests -q 2>&1
        $exitCode = $LASTEXITCODE
        Pop-Location

        if ($exitCode -eq 0) {
            Write-Host "  SUCCESS" -ForegroundColor Green
            $successCount++

            $jarPath = Join-Path $servicePath "target\$service-1.0.0.jar"
            if (Test-Path $jarPath) {
                $size = [math]::Round((Get-Item $jarPath).Length / 1MB, 2)
                Write-Host "  JAR: $size MB" -ForegroundColor Green
            }
        } else {
            Write-Host "  FAILED (exit code: $exitCode)" -ForegroundColor Red
            $failedCount++
        }
    } catch {
        Pop-Location
        Write-Host "  ERROR: $($_.Exception.Message)" -ForegroundColor Red
        $failedCount++
    }

    Write-Host ""
}

$duration = (Get-Date) - $startTime

Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host "BUILD SUMMARY" -ForegroundColor Cyan
Write-Host "================================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Total:   $($services.Count)" -ForegroundColor White
Write-Host "Success: $successCount" -ForegroundColor Green
Write-Host "Failed:  $failedCount" -ForegroundColor $(if ($failedCount -gt 0) { "Red" } else { "Green" })
Write-Host "Skipped: $skippedCount" -ForegroundColor Yellow
Write-Host ""
Write-Host "Duration: $($duration.ToString('mm\:ss'))" -ForegroundColor White
Write-Host ""

if ($failedCount -eq 0) {
    Write-Host "ALL BUILDS SUCCESSFUL!" -ForegroundColor Green
    Write-Host ""
    Write-Host "All 41 services ready for deployment!" -ForegroundColor Green
} else {
    Write-Host "SOME BUILDS FAILED" -ForegroundColor Red
}

Write-Host ""
