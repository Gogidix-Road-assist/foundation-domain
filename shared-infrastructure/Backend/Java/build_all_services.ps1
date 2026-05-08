# Build All 41 Shared-Infrastructure Services
# ULTRATHINK MODE - Production Build Script

param(
    [switch]$SkipTests = $true,
    [switch]$Parallel = $false,
    [string]$ServiceFilter = ""
)

$ErrorActionPreference = "Continue"
$BASE_DIR = "C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\shared-infrastructure\Backend\Java"
$LOG_FILE = Join-Path $BASE_DIR "build_$(Get-Date -Format 'yyyyMMdd_HHmmss').log"

# All 41 services
$ALL_SERVICES = @(
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

Write-Host "="*80 -ForegroundColor Cyan
Write-Host "BUILDING ALL 41 SHARED-INFRASTRUCTURE SERVICES" -ForegroundColor Cyan
Write-Host "v1.0.0 Gold Standard" -ForegroundColor Cyan
Write-Host "="*80 -ForegroundColor Cyan
Write-Host ""

# Filter services if specified
if ($ServiceFilter) {
    $services = $ALL_SERVICES | Where-Object { $_ -like "*$ServiceFilter*" }
    Write-Host "Filtering for: $ServiceFilter" -ForegroundColor Yellow
    Write-Host "Matched services: $($services.Count)" -ForegroundColor Yellow
    Write-Host ""
} else {
    $services = $ALL_SERVICES
}

Write-Host "Build Configuration:" -ForegroundColor White
Write-Host "  Skip Tests: $SkipTests" -ForegroundColor White
Write-Host "  Parallel: $Parallel" -ForegroundColor White
Write-Host "  Total Services: $($services.Count)" -ForegroundColor White
Write-Host "  Log File: $LOG_FILE" -ForegroundColor White
Write-Host ""

# Create log file
Start-Transcript -Path $LOG_FILE -Append

# Results tracking
$results = @{
    Total = 0
    Success = 0
    Failed = 0
    Skipped = 0
    StartTime = Get-Date
}

Write-Host "Starting build process..." -ForegroundColor Green
Write-Host ""

if ($Parallel) {
    Write-Host "PARALLEL BUILD MODE - Launching all builds simultaneously" -ForegroundColor Yellow
    Write-Host "Note: This will use significant CPU and memory" -ForegroundColor Yellow
    Write-Host ""

    $jobs = @()

    foreach ($service in $services) {
        $servicePath = Join-Path $BASE_DIR $service

        if (-not (Test-Path $servicePath)) {
            Write-Host "[$service] NOT FOUND - Skipping" -ForegroundColor Red
            $results.Skipped++
            continue
        }

        $results.Total++

        $scriptBlock = {
            param($servicePath, $serviceName, $skipTests)

            try {
                Push-Location $servicePath

                $args = @("clean", "package")
                if ($skipTests) {
                    $args += "-DskipTests"
                }

                $buildResult = mvn $args 2>&1
                $exitCode = $LASTEXITCODE

                Pop-Location

                return @{
                    Service = $serviceName
                    Success = ($exitCode -eq 0)
                    ExitCode = $exitCode
                    Output = $buildResult -join "`n"
                }
            } catch {
                Pop-Location
                return @{
                    Service = $serviceName
                    Success = $false
                    ExitCode = -1
                    Output = $_.Exception.Message
                }
            }
        }

        $job = Start-Job -ScriptBlock $scriptBlock -ArgumentList $servicePath, $service, $SkipTests
        $jobs += $job

        Write-Host "[$service] Build job started..." -ForegroundColor Cyan
    }

    Write-Host ""
    Write-Host "Waiting for builds to complete..." -ForegroundColor Yellow
    Write-Host ""

    # Wait for all jobs and collect results
    foreach ($job in $jobs) {
        $result = Receive-Job -Job $job -Wait
        Remove-Job -Job $job

        if ($result.Success) {
            Write-Host "[$($result.Service)] ✓ BUILD SUCCESS" -ForegroundColor Green
            $results.Success++
        } else {
            Write-Host "[$($result.Service)] ✗ BUILD FAILED (exit code: $($result.ExitCode))" -ForegroundColor Red
            $results.Failed++
        }
    }

} else {
    Write-Host "SEQUENTIAL BUILD MODE" -ForegroundColor Yellow
    Write-Host ""

    foreach ($service in $services) {
        $servicePath = Join-Path $BASE_DIR $service

        if (-not (Test-Path $servicePath)) {
            Write-Host "[$service] NOT FOUND - Skipping" -ForegroundColor Red
            $results.Skipped++
            continue
        }

        $results.Total++

        Write-Host "[$service] Building..." -ForegroundColor Cyan

        try {
            Push-Location $servicePath

            $args = @("clean", "package")
            if ($SkipTests) {
                $args += "-DskipTests"
            }

            $buildOutput = mvn $args 2>&1
            $exitCode = $LASTEXITCODE

            Pop-Location

            if ($exitCode -eq 0) {
                Write-Host "  ✓ BUILD SUCCESS" -ForegroundColor Green

                # Check if JAR was created
                $jarPath = Join-Path $servicePath "target\$service-1.0.0.jar"
                if (Test-Path $jarPath) {
                    $jarSize = (Get-Item $jarPath).Length / 1MB
                    Write-Host "  JAR: {0:N2} MB" -f $jarSize -ForegroundColor Green
                }

                $results.Success++
            } else {
                Write-Host "  ✗ BUILD FAILED (exit code: $exitCode)" -ForegroundColor Red
                $results.Failed++
            }

        } catch {
            Pop-Location
            Write-Host "  ✗ BUILD ERROR: $($_.Exception.Message)" -ForegroundColor Red
            $results.Failed++
        }

        Write-Host ""
    }
}

# Calculate duration
$duration = (Get-Date) - $results.StartTime

# Stop transcript
Stop-Transcript

# Final Report
Write-Host "="*80 -ForegroundColor Cyan
Write-Host "BUILD SUMMARY" -ForegroundColor Cyan
Write-Host "="*80 -ForegroundColor Cyan
Write-Host ""

Write-Host "Total Services: $($results.Total)" -ForegroundColor White
Write-Host "Successful: $($results.Success)" -ForegroundColor Green
Write-Host "Failed: $($results.Failed)" -ForegroundColor $(if ($results.Failed -gt 0) { "Red" } else { "Green" })
Write-Host "Skipped: $($results.Skipped)" -ForegroundColor Yellow
Write-Host ""

Write-Host "Duration: $($duration.ToString('mm\:ss'))" -ForegroundColor White
Write-Host ""

if ($results.Failed -eq 0) {
    Write-Host "="*80 -ForegroundColor Green
    Write-Host "✓ ALL BUILDS SUCCESSFUL!" -ForegroundColor Green
    Write-Host "="*80 -ForegroundColor Green
    Write-Host ""
    Write-Host "All 41 services are ready for deployment!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next Steps:" -ForegroundColor Cyan
    Write-Host "  1. Review JAR files in service target/ directories" -ForegroundColor White
    Write-Host "  2. Run: powershell -File verify_deployment.ps1" -ForegroundColor White
    Write-Host "  3. Begin deployment process" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host "="*80 -ForegroundColor Red
    Write-Host "✗ SOME BUILDS FAILED" -ForegroundColor Red
    Write-Host "="*80 -ForegroundColor Red
    Write-Host ""
    Write-Host "Check the log file for details: $LOG_FILE" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Failed services may have:" -ForegroundColor Yellow
    Write-Host "  - Compilation errors" -ForegroundColor White
    Write-Host "  - Missing dependencies" -ForegroundColor White
    Write-Host "  - Configuration issues" -ForegroundColor White
    Write-Host ""
}

Write-Host "Log saved to: $LOG_FILE" -ForegroundColor Cyan
Write-Host ""
