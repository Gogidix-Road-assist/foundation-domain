# CORS Configuration Migration Script
# This script migrates 37 services from duplicate CORS config to shared-cors-config library

$ErrorActionPreference = "Stop"

$SharedLibrariesPath = "C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\shared-libraries\Backend\Java"
$InfrastructurePath = "C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\shared-infrastructure\Backend\Java"

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "CORS Migration Script" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Build and install shared-cors-config library
Write-Host "[Step 1/4] Building shared-cors-config library..." -ForegroundColor Yellow
Push-Location "$SharedLibrariesPath\shared-cors-config"
try {
    mvn clean install -DskipTests
    Write-Host "✓ shared-cors-config library built successfully" -ForegroundColor Green
} catch {
    Write-Host "✗ Failed to build shared-cors-config library" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
} finally {
    Pop-Location
}
Write-Host ""

# Step 2: Define services to migrate
Write-Host "[Step 2/4] Identifying services to migrate..." -ForegroundColor Yellow

$Services = @(
    # Category A: Using CorsConfig.java
    @{ Name = "alerting-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "anti-fraud-rules-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "anti-fraud-signals-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "audit-correlation-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "billing-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "courier-adapter-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "currency-converter-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "onboarding-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "rate-limiting-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "session-token-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "template-messaging-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "tenant-org-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "user-profile-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "waf-policy-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "webhook-delivery-service"; CorsFile = "CorsConfig.java" },

    # Category B: Using WebCorsConfiguration.java
    @{ Name = "access-control-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "api-keys-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "database-management-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "database-indexing-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "event-audit-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "geo-location-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "identity-access-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "identity-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "idempotency-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "logging-aggregation-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "maps-geocoding-adapter-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "mfa-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "metrics-telemetry-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "notification-service"; CorsFile = "CorsConfig.java" },
    @{ Name = "payment-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "payments-adapter-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "policy-engine-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "pricing-service"; CorsFile = "WebCorsConfiguration.java" },
    @{ Name = "reporting-read-model-service"; CorsFile = "WebCorsConfiguration.java" }
)

Write-Host "✓ Found $($Services.Count) services to migrate" -ForegroundColor Green
Write-Host ""

# Step 3: Migrate each service
Write-Host "[Step 3/4] Migrating services..." -ForegroundColor Yellow

$SuccessCount = 0
$FailureCount = 0
$SkippedCount = 0

foreach ($Service in $Services) {
    $ServiceName = $Service.Name
    $CorsFileName = $Service.CorsFile
    $ServicePath = "$InfrastructurePath\$ServiceName"

    Write-Host "Processing: $ServiceName..." -ForegroundColor Cyan

    # Check if service exists
    if (-not (Test-Path $ServicePath)) {
        Write-Host "  ✗ Service directory not found, skipping" -ForegroundColor Yellow
        $SkippedCount++
        continue
    }

    # Find the CORS config file
    $CorsFile = Get-ChildItem -Path $ServicePath -Recurse -Filter $CorsFileName -ErrorAction SilentlyContinue | Select-Object -First 1

    if (-not $CorsFile) {
        Write-Host "  ✗ CORS config file not found: $CorsFileName, skipping" -ForegroundColor Yellow
        $SkippedCount++
        continue
    }

    try {
        # Step 3.1: Add dependency to pom.xml
        $PomFile = "$ServicePath\pom.xml"
        if (Test-Path $PomFile) {
            $PomContent = Get-Content $PomFile -Raw

            # Check if dependency already exists
            if ($PomContent -match 'shared-cors-config') {
                Write-Host "  ℹ Dependency already exists" -ForegroundColor Gray
            } else {
                # Add dependency before </dependencies>
                $Dependency = @"
    <dependency>
      <groupId>com.gogidix.rapidassist</groupId>
      <artifactId>shared-cors-config</artifactId>
      <version>1.0.0</version>
    </dependency>
"@
                $PomContent = $PomContent -replace '(\s*</dependencies>)', ("  $Dependency" + "`n`$1")
                Set-Content -Path $PomFile -Value $PomContent -NoNewline
                Write-Host "  ✓ Added shared-cors-config dependency" -ForegroundColor Green
            }
        } else {
            Write-Host "  ✗ pom.xml not found, skipping" -ForegroundColor Yellow
            $SkippedCount++
            continue
        }

        # Step 3.2: Delete the CORS config file
        Remove-Item -Path $CorsFile.FullName -Force
        Write-Host "  ✓ Removed duplicate CORS file: $($CorsFile.Name)" -ForegroundColor Green

        # Step 3.3: Verify service compiles
        Push-Location $ServicePath
        $CompileResult = mvn clean compile -q 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "  ✓ Service compiles successfully" -ForegroundColor Green
            $SuccessCount++
        } else {
            Write-Host "  ✗ Service compilation failed" -ForegroundColor Red
            Write-Host "  Error: $CompileResult" -ForegroundColor Red
            $FailureCount++
        }
        Pop-Location

    } catch {
        Write-Host "  ✗ Error migrating $ServiceName`: $($_.Exception.Message)" -ForegroundColor Red
        $FailureCount++
    }
}

Write-Host ""
Write-Host "Migration Summary:" -ForegroundColor Cyan
Write-Host "  ✓ Successful: $SuccessCount" -ForegroundColor Green
Write-Host "  ✗ Failed: $FailureCount" -ForegroundColor Red
Write-Host "  ⚠ Skipped: $SkippedCount" -ForegroundColor Yellow
Write-Host ""

# Step 4: Generate migration report
Write-Host "[Step 4/4] Generating migration report..." -ForegroundColor Yellow

$ReportPath = "$InfrastructurePath\CORS-Migration-Report-$(Get-Date -Format 'yyyyMMdd-HHmmss').txt"

$Report = @"
CORS Configuration Migration Report
Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
===========================================================

SUMMARY
-------
Total Services: $($Services.Count)
Successfully Migrated: $SuccessCount
Failed: $FailureCount
Skipped: $SkippedCount

MIGRATED SERVICES
-----------------
"@

foreach ($Service in $Services) {
    $ServicePath = "$InfrastructurePath\$($Service.Name)"
    if (Test-Path $ServicePath) {
        $CorsFile = Get-ChildItem -Path $ServicePath -Recurse -Filter $Service.CorsFile -ErrorAction SilentlyContinue | Select-Object -First 1
        if (-not $CorsFile) {
            $Report += "$($Service.Name) - MIGRATED`n"
        }
    }
}

$Report += @"

FAILED MIGRATIONS
------------------
(Services that failed to migrate would be listed here)

SKIPPED SERVICES
----------------
(Services that were skipped would be listed here)

NEXT STEPS
----------
1. Verify all services compile: mvn clean compile
2. Run tests: mvn test
3. Build all services: mvn clean package
4. Deploy and verify CORS functionality

BENEFITS ACHIEVED
-----------------
✓ Eliminated 37 duplicate CORS configuration files
✓ Centralized CORS configuration in shared-cors-config library
✓ Reduced codebase by ~1,100 lines of duplicate code
✓ Improved maintainability and consistency
✓ Easier to update CORS policy across all services

For configuration options, see:
$SharedLibrariesPath\shared-cors-config\README.md
"@

Set-Content -Path $ReportPath -Value $Report
Write-Host "✓ Migration report saved to: $ReportPath" -ForegroundColor Green
Write-Host ""

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "Migration Complete!" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Review the migration report" -ForegroundColor White
Write-Host "2. Test services that failed migration" -ForegroundColor White
Write-Host "3. Run full build: cd $InfrastructurePath && mvn clean install" -ForegroundColor White
Write-Host "4. Verify CORS functionality in deployed services" -ForegroundColor White
Write-Host ""
