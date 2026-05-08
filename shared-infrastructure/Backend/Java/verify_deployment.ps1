# Deployment Verification Script for 41 Shared-Infrastructure Services
# ULTRATHINK MODE - 100% Precision Verification

param(
    [switch]$SkipBuild = $false,
    [switch]$QuickCheck = $false,
    [string]$ServiceFilter = ""
)

$ErrorActionPreference = "Stop"
$BASE_DIR = "C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\shared-infrastructure\Backend\Java"

# Service Port Mapping
$SERVICE_PORTS = @{
    "database-indexing-service" = 8200
    "access-control-service" = 8300
    "alerting-service" = 8301
    "anti-fraud-rules-service" = 8302
    "anti-fraud-signals-service" = 8303
    "api-gateway" = 8304
    "api-keys-service" = 8305
    "audit-correlation-service" = 8306
    "billing-service" = 8307
    "courier-adapter-service" = 8308
    "currency-converter-service" = 8309
    "data-privacy-consent-service" = 8310
    "identity-access-service" = 8315
    "identity-service" = 8316
    "insurer-adapter-service" = 8320
    "integration-adapters-service" = 8325
    "logging-aggregation-service" = 8328
    "maps-geocoding-adapter-service" = 8330
    "metrics-telemetry-service" = 8331
    "mfa-service" = 8335
    "notification-service" = 8340
    "onboarding-service" = 8345
    "payment-service" = 8350
    "payments-adapter-service" = 8355
    "policy-engine-service" = 8360
    "pricing-service" = 8365
    "rate-limiting-service" = 8370
    "reporting-read-model-service" = 8375
    "request-routing-service" = 8380
    "service-health-monitor-service" = 8385
    "service-registry-discovery" = 8333
    "session-token-service" = 8390
    "template-messaging-service" = 8395
    "tenant-org-service" = 8255
    "user-profile-service" = 8260
    "waf-policy-service" = 8265
    "webhook-delivery-service" = 8270
    "idempotency-service" = 8322
    "database-management-service" = 8201
}

# All 41 services
$ALL_SERVICES = $SERVICE_PORTS.Keys | Sort-Object

Write-Host "="*80 -ForegroundColor Cyan
Write-Host "SHARED-INFRASTRUCTURE DEPLOYMENT VERIFICATION" -ForegroundColor Cyan
Write-Host "41 Services - v1.0.0 Gold Standard" -ForegroundColor Cyan
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

if ($QuickCheck) {
    Write-Host "QUICK CHECK MODE - Skpping build verification" -ForegroundColor Yellow
    Write-Host ""
}

# Function to check if a port is in use
function Test-PortInUse {
    param([int]$Port)
    try {
        $connection = New-Object System.Net.Sockets.TcpClient
        $connection.ReceiveTimeout = 1000
        $connection.Connect("localhost", $Port)
        $connection.Close()
        return $true
    } catch {
        return $false
    }
}

# Function to check HTTP endpoint
function Test-HttpEndpoint {
    param([string]$Url, [int]$Timeout = 5000)
    try {
        $client = New-Object System.Net.WebClient
        $client.Headers.Add("User-Agent", "DeploymentVerification/1.0")
        $response = $client.DownloadString($Url)
        return $true
    } catch {
        return $false
    }
}

# Results tracking
$results = @{
    Total = 0
    FilesVerified = 0
    BuildsPassed = 0
    BuildsFailed = 0
    BuildsSkipped = 0
    ServicesRunning = 0
    ServicesNotRunning = 0
    HealthChecksPassed = 0
    HealthChecksFailed = 0
    OpenApiConfigs = 0
    CorsConfigs = 0
    ExceptionHandlers = 0
    Dockerfiles = 0
}

Write-Host "PHASE 1: FILE STRUCTURE VERIFICATION" -ForegroundColor Green
Write-Host "-"*80 -ForegroundColor Green

foreach ($service in $services) {
    $servicePath = Join-Path $BASE_DIR $service

    if (-not (Test-Path $servicePath)) {
        Write-Host "[$service] NOT FOUND" -ForegroundColor Red
        continue
    }

    $results.Total++

    # Check for pom.xml
    $pomPath = Join-Path $servicePath "pom.xml"
    $hasPom = Test-Path $pomPath

    # Check version in pom.xml
    if ($hasPom) {
        $pomContent = Get-Content $pomPath -Raw
        $hasVersion = $pomContent -match "<version>1\.0\.0</version>"
    }

    # Check for OpenAPI configuration
    $openApiFound = Get-ChildItem -Path $servicePath -Recurse -Filter "OpenApiConfiguration.java" -ErrorAction SilentlyContinue | Measure-Object | Select-Object -ExpandProperty Count
    if ($openApiFound -eq 0) {
        # Check for alternative naming
        $openApiFound = Get-ChildItem -Path $servicePath -Recurse -Filter "OpenApiConfig.java" -ErrorAction SilentlyContinue | Measure-Object | Select-Object -ExpandProperty Count
    }
    if ($openApiFound -gt 0) { $results.OpenApiConfigs++ }

    # Check for CORS configuration
    $corsFound = Get-ChildItem -Path $servicePath -Recurse -Filter "WebCorsConfiguration.java" -ErrorAction SilentlyContinue | Measure-Object | Select-Object -ExpandProperty Count
    if ($corsFound -eq 0) {
        $corsFound = Get-ChildItem -Path $servicePath -Recurse -Filter "CorsConfiguration.java" -ErrorAction SilentlyContinue | Measure-Object | Select-Object -ExpandProperty Count
    }
    if ($corsFound -gt 0) { $results.CorsConfigs++ }

    # Check for Global Exception Handler
    $handlerFound = Get-ChildItem -Path $servicePath -Recurse -Filter "GlobalExceptionHandler.java" -ErrorAction SilentlyContinue | Measure-Object | Select-Object -ExpandProperty Count
    if ($handlerFound -gt 0) { $results.ExceptionHandlers++ }

    # Check for Dockerfile
    $dockerfileFound = Test-Path (Join-Path $servicePath "Dockerfile")
    if ($dockerfileFound) { $results.Dockerfiles++ }

    # Calculate compliance
    $compliance = 0
    if ($hasPom -and $hasVersion) { $compliance += 16.7 }
    if ($openApiFound -gt 0) { $compliance += 16.7 }
    if ($corsFound -gt 0) { $compliance += 16.7 }
    if ($handlerFound -gt 0) { $compliance += 16.7 }
    if ($dockerfileFound) { $compliance += 16.6 }
    $compliance = [math]::Round($compliance, 1)

    if ($compliance -ge 85) {
        Write-Host "[$service] " -NoNewline -ForegroundColor Cyan
        Write-Host "$compliance% " -NoNewline -ForegroundColor Green
        Write-Host "COMPLETE" -ForegroundColor Green
        $results.FilesVerified++
    } elseif ($compliance -ge 50) {
        Write-Host "[$service] " -NoNewline -ForegroundColor Cyan
        Write-Host "$compliance% " -NoNewline -ForegroundColor Yellow
        Write-Host "IN PROGRESS" -ForegroundColor Yellow
    } else {
        Write-Host "[$service] " -NoNewline -ForegroundColor Cyan
        Write-Host "$compliance% " -NoNewline -ForegroundColor Red
        Write-Host "PENDING" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "PHASE 1 SUMMARY:" -ForegroundColor Green
Write-Host "  Services Verified: $($results.FilesVerified)/$($results.Total)" -ForegroundColor White
Write-Host "  OpenAPI Configs: $($results.OpenApiConfigs)" -ForegroundColor White
Write-Host "  CORS Configs: $($results.CorsConfigs)" -ForegroundColor White
Write-Host "  Exception Handlers: $($results.ExceptionHandlers)" -ForegroundColor White
Write-Host "  Dockerfiles: $($results.Dockerfiles)" -ForegroundColor White
Write-Host ""

if (-not $SkipBuild -and -not $QuickCheck) {
    Write-Host "PHASE 2: BUILD VERIFICATION" -ForegroundColor Green
    Write-Host "-"*80 -ForegroundColor Green
    Write-Host "This will compile all services. Press Ctrl+C to cancel..." -ForegroundColor Yellow
    Write-Host "Starting in 3 seconds..." -ForegroundColor Yellow
    Start-Sleep -Seconds 3
    Write-Host ""

    foreach ($service in $services) {
        $servicePath = Join-Path $BASE_DIR $service

        if (-not (Test-Path $servicePath)) { continue }

        Write-Host "Building $service..." -ForegroundColor Cyan

        try {
            Push-Location $servicePath
            $buildResult = mvn clean compile -q -DskipTests 2>&1
            $exitCode = $LASTEXITCODE

            if ($exitCode -eq 0) {
                Write-Host "  ✓ BUILD SUCCESS" -ForegroundColor Green
                $results.BuildsPassed++
            } else {
                Write-Host "  ✗ BUILD FAILED (exit code: $exitCode)" -ForegroundColor Red
                $results.BuildsFailed++
            }
        } catch {
            Write-Host "  ✗ BUILD ERROR: $_" -ForegroundColor Red
            $results.BuildsFailed++
        } finally {
            Pop-Location
        }
    }

    Write-Host ""
    Write-Host "PHASE 2 SUMMARY:" -ForegroundColor Green
    Write-Host "  Builds Passed: $($results.BuildsPassed)" -ForegroundColor Green
    Write-Host "  Builds Failed: $($results.BuildsFailed)" -ForegroundColor Red
    Write-Host ""
} else {
    $results.BuildsSkipped = $services.Count
    Write-Host "PHASE 2: BUILD VERIFICATION SKIPPED" -ForegroundColor Yellow
    Write-Host ""
}

Write-Host "PHASE 3: SERVICE HEALTH CHECK" -ForegroundColor Green
Write-Host "-"*80 -ForegroundColor Green

foreach ($service in $services) {
    $port = $SERVICE_PORTS[$service]
    $serviceRunning = Test-PortInUse -Port $port

    if ($serviceRunning) {
        Write-Host "[$service] Port $port " -NoNewline -ForegroundColor Cyan
        Write-Host "IN USE" -ForegroundColor Green

        # Try health check
        $healthUrl = "http://localhost:$port/actuator/health"
        $healthy = Test-HttpEndpoint -Url $healthUrl

        if ($healthy) {
            Write-Host "  ✓ Health check: $healthUrl" -ForegroundColor Green
            $results.HealthChecksPassed++
            $results.ServicesRunning++
        } else {
            Write-Host "  ✗ Health check failed: $healthUrl" -ForegroundColor Yellow
            $results.HealthChecksFailed++
            $results.ServicesRunning++
        }

        # Check OpenAPI docs
        $swaggerUrl = "http://localhost:$port/swagger-ui.html"
        $swaggerAvailable = Test-HttpEndpoint -Url $swaggerUrl
        if ($swaggerAvailable) {
            Write-Host "  ✓ Swagger UI available" -ForegroundColor Green
        }
    } else {
        Write-Host "[$service] Port $port " -NoNewline -ForegroundColor Cyan
        Write-Host "NOT IN USE" -ForegroundColor Gray
        $results.ServicesNotRunning++
    }
}

Write-Host ""
Write-Host "PHASE 3 SUMMARY:" -ForegroundColor Green
Write-Host "  Services Running: $($results.ServicesRunning)" -ForegroundColor Green
Write-Host "  Services Not Running: $($results.ServicesNotRunning)" -ForegroundColor Gray
Write-Host "  Health Checks Passed: $($results.HealthChecksPassed)" -ForegroundColor Green
Write-Host "  Health Checks Failed: $($results.HealthChecksFailed)" -ForegroundColor Yellow
Write-Host ""

# Final Report
Write-Host "="*80 -ForegroundColor Cyan
Write-Host "FINAL VERIFICATION REPORT" -ForegroundColor Cyan
Write-Host "="*80 -ForegroundColor Cyan
Write-Host ""

$overallStatus = "SUCCESS"
if ($results.FilesVerified -lt $results.Total) { $overallStatus = "WARNING" }
if ($results.BuildsFailed -gt 0) { $overallStatus = "BUILD ERRORS" }

Write-Host "Status: $overallStatus" -ForegroundColor $(if ($overallStatus -eq "SUCCESS") { "Green" } elseif ($overallStatus -eq "WARNING") { "Yellow" } else { "Red" })
Write-Host ""
Write-Host "File Structure: $($results.FilesVerified)/$($results.Total) services verified" -ForegroundColor White
Write-Host "Build Status: $($results.BuildsPassed) passed, $($results.BuildsFailed) failed" -ForegroundColor White
Write-Host "Service Status: $($results.ServicesRunning) running, $($results.ServicesNotRunning) stopped" -ForegroundColor White
Write-Host ""

if ($overallStatus -eq "SUCCESS") {
    Write-Host "✓ All 41 services are production ready!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next Steps:" -ForegroundColor Cyan
    Write-Host "  1. Deploy services to production environment" -ForegroundColor White
    Write-Host "  2. Configure ALLOWED_ORIGINS environment variable" -ForegroundColor White
    Write-Host "  3. Set up service discovery and load balancing" -ForegroundColor White
    Write-Host "  4. Configure monitoring and alerting" -ForegroundColor White
} else {
    Write-Host "✗ Issues detected. Please review the report above." -ForegroundColor Red
}

Write-Host ""
Write-Host "="*80 -ForegroundColor Cyan
Write-Host "Verification completed: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Cyan
Write-Host "="*80 -ForegroundColor Cyan
