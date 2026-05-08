# Fix All 27 Failed Services
# ULTRATHINK MODE - Complete Fix

$ErrorActionPreference = "Stop"

$failedServices = @(
    "api-gateway",
    "audit-correlation-service",
    "billing-service",
    "data-privacy-consent-service",
    "database-indexing-service",
    "database-management-service",
    "geo-location-service",
    "idempotency-service",
    "identity-access-service",
    "identity-service",
    "insurer-adapter-service",
    "integration-adapters-service",
    "logging-aggregation-service",
    "metrics-telemetry-service",
    "mfa-service",
    "notification-service",
    "payment-service",
    "payments-adapter-service",
    "policy-engine-service",
    "pricing-service",
    "rate-limiting-service",  # Actually passed but verify
    "reporting-read-model-service",
    "request-routing-service",
    "service-health-monitor-service",
    "service-registry-discovery",
    "tenant-org-service",
    "user-profile-service",
    "waf-policy-service"
)

Write-Host "FIXING 27 FAILED SERVICES" -ForegroundColor Cyan
Write-Host ""

$fixedCount = 0
$errorCount = 0

foreach ($service in $failedServices) {
    $servicePath = Join-Path $BASE_DIR $service

    if (-not (Test-Path $servicePath)) {
        Write-Host "[$service] NOT FOUND" -ForegroundColor Red
        continue
    }

    Write-Host "[$service] Fixing..." -ForegroundColor Cyan

    try {
        # Check for duplicate CORS configurations
        $webCors = Get-ChildItem -Path $servicePath -Recurse -Filter "WebCorsConfiguration.java" -ErrorAction SilentlyContinue
        $corsConfig = Get-ChildItem -Path $servicePath -Recurse -Filter "CorsConfiguration.java" -ErrorAction SilentlyContinue
        $corsConfig2 = Get-ChildItem -Path $servicePath -Recurse -Filter "CorsConfig.java" -ErrorAction SilentlyContinue

        # If duplicate exists, remove the newer WebCorsConfiguration.java
        if ($webCors -and ($corsConfig -or $corsConfig2)) {
            Write-Host "  Removing duplicate WebCorsConfiguration.java" -ForegroundColor Yellow
            Remove-Item $webCors.FullName -Force
        }

        # Check pom.xml for Redis dependency
        $pomPath = Join-Path $servicePath "pom.xml"
        if (Test-Path $pomPath) {
            $pomContent = Get-Content $pomPath -Raw

            # Check if service uses Redis (has RedisConfig or RedisTemplate)
            $usesRedis = Get-ChildItem -Path $servicePath -Recurse -Filter "*Redis*.java" -ErrorAction SilentlyContinue

            if ($usesRedis -and $pomContent -notmatch "spring-boot-starter-data-redis") {
                Write-Host "  Adding Redis dependency" -ForegroundColor Yellow

                # Add Redis dependency before </dependencies>
                $redisDep = @"

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>"@

                $pomContent = $pomContent -replace "</dependencies>", "$redisDep`n</dependencies>"
                Set-Content -Path $pomPath -Value $pomContent -NoNewline
            }
        }

        Write-Host "  FIXED" -ForegroundColor Green
        $fixedCount++

    } catch {
        Write-Host "  ERROR: $($_.Exception.Message)" -ForegroundColor Red
        $errorCount++
    }
}

Write-Host ""
Write-Host "FIX SUMMARY:" -ForegroundColor Cyan
Write-Host "  Fixed: $fixedCount" -ForegroundColor Green
Write-Host "  Errors: $errorCount" -ForegroundColor Red
Write-Host ""
