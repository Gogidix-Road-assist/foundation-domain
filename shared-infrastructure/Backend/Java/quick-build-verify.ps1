# Quick Build Verification for all services
$ErrorActionPreference = "Continue"
$BASE_DIR = "C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\shared-infrastructure\Backend\Java"

$services = @("access-control-service","alerting-service","anti-fraud-rules-service","anti-fraud-signals-service","api-gateway","api-keys-service","audit-correlation-service","billing-service","courier-adapter-service","currency-converter-service","data-privacy-consent-service","database-indexing-service","database-management-service","event-audit-service","geo-location-service","idempotency-service","identity-access-service","identity-service","insurer-adapter-service","integration-adapters-service","logging-aggregation-service","maps-geocoding-adapter-service","metrics-telemetry-service","mfa-service","notification-service","onboarding-service","payment-service","payments-adapter-service","policy-engine-service","pricing-service","rate-limiting-service","reporting-read-model-service","request-routing-service","service-health-monitor-service","service-registry-discovery","session-token-service","template-messaging-service","tenant-org-service","user-profile-service","waf-policy-service","webhook-delivery-service")

Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "QUICK BUILD VERIFICATION" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "Services: $($services.Count)" -ForegroundColor White
Write-Host ""

$compilePass = 0
$compileFail = 0
$jarPass = 0
$jarFail = 0
$failedServices = @()
$startTime = Get-Date

foreach ($service in $services) {
    $servicePath = Join-Path $BASE_DIR $service

    Write-Host "[$($services.IndexOf($service) + 1)/$($services.Count)] $service" -ForegroundColor Cyan

    if (-not (Test-Path $servicePath)) {
        Write-Host "  SKIP Directory not found" -ForegroundColor Yellow
        continue
    }

    Push-Location $servicePath

    # Step 1: Compile
    Write-Host "  [1/2] Compile..." -ForegroundColor Gray
    $null = mvn clean compile -DskipTests -q 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  OK Compile" -ForegroundColor Green
        $compilePass++
    } else {
        Write-Host "  FAIL Compile" -ForegroundColor Red
        $compileFail++
        $failedServices += $service + "(compile)"
        Pop-Location
        continue
    }

    # Step 2: Build JAR
    Write-Host "  [2/2] JAR..." -ForegroundColor Gray
    $null = mvn package -DskipTests -q 2>&1
    $jarFiles = Get-ChildItem -Path "target" -Filter "*.jar" -ErrorAction SilentlyContinue | Where-Object { $_.Name -notmatch "sources|javadoc" }

    if ($jarFiles) {
        $sizeMb = [math]::Round($jarFiles[0].Length / 1MB, 1)
        Write-Host "  OK JAR ${sizeMb}MB" -ForegroundColor Green
        $jarPass++
    } else {
        Write-Host "  FAIL JAR" -ForegroundColor Red
        $jarFail++
        $failedServices += $service + "(jar)"
    }

    Pop-Location
    Write-Host ""
}

$duration = [math]::Round(((Get-Date) - $startTime).TotalMinutes, 1)

Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "VERIFICATION COMPLETE" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Duration: ${duration} minutes" -ForegroundColor White
Write-Host "Compile: $compilePass PASS, $compileFail FAIL" -ForegroundColor $(if ($compileFail -eq 0) { "Green" } else { "Red" })
Write-Host "JAR: $jarPass PASS, $jarFail FAIL" -ForegroundColor $(if ($jarFail -eq 0) { "Green" } else { "Red" })
Write-Host ""

if ($failedServices.Count -eq 0) {
    Write-Host "SUCCESS ALL SERVICES!" -ForegroundColor Green
    Write-Host "All $($services.Count) services compiled and built JARs successfully" -ForegroundColor Green
} else {
    Write-Host "FAILED SERVICES:" -ForegroundColor Red
    foreach ($svc in $failedServices) { Write-Host "  - $svc" -ForegroundColor Red }
}
Write-Host ""
