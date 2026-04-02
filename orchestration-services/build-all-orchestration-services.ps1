# Master Build Script for All 11 Orchestration Services
# Build Date: February 6, 2026
# Status: Production Ready

$ErrorActionPreference = "Continue"
$BaseDir = "C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\orchestration-services\Backend\Java"
$MavenCmd = "C:\maven-3.9.9\bin\mvn.cmd"
$LogFile = Join-Path $BaseDir "..\..\build-all-orchestration-$(Get-Date -Format 'yyyyMMdd-HHmmss').log"

# Create logs directory if not exists
$LogsDir = Join-Path $BaseDir "..\..\logs"
if (-not (Test-Path $LogsDir)) {
    New-Item -ItemType Directory -Path $LogsDir -Force | Out-Null
}

# All 11 services with their ports
$Services = @(
    @{ Name = "alerting-service"; Port = 8083; Order = 1 },
    @{ Name = "dispatching-service"; Port = 8084; Order = 2 },
    @{ Name = "fleet-assistance-service"; Port = 8085; Order = 3 },
    @{ Name = "fleet-organization-service"; Port = 8086; Order = 4 },
    @{ Name = "fleet-policy-service"; Port = 8087; Order = 5 },
    @{ Name = "fleet-vehicles-service"; Port = 8088; Order = 6 },
    @{ Name = "location-service"; Port = 8089; Order = 7 },
    @{ Name = "matching-service"; Port = 8090; Order = 8 },
    @{ Name = "monitoring-service"; Port = 8091; Order = 9 },
    @{ Name = "reporting-service"; Port = 8092; Order = 10 },
    @{ Name = "transaction-orchestration-service"; Port = 8093; Order = 11 }
)

Write-Host "=" * 100 -ForegroundColor Cyan
Write-Host "ORCHESTRATION SERVICES - MASTER BUILD SCRIPT" -ForegroundColor Cyan
Write-Host "Total Services: $($Services.Count)" -ForegroundColor Cyan
Write-Host "Build Date: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Cyan
Write-Host "=" * 100 -ForegroundColor Cyan
Write-Host ""

$BuildResults = @()

foreach ($Service in $Services) {
    $ServiceName = $Service.Name
    $ServicePort = $Service.Port
    $ServiceDir = Join-Path $BaseDir $ServiceName
    $ServiceLog = Join-Path $LogsDir "$ServiceName-build.log"

    Write-Host "[$($Service.Order)/$($Services.Count)] Building: $ServiceName (Port: $ServicePort)" -ForegroundColor Yellow

    # Check if service directory exists
    if (-not (Test-Path $ServiceDir)) {
        Write-Host "  ✗ FAIL: Service directory not found: $ServiceDir" -ForegroundColor Red
        $BuildResults += @{
            Service = $ServiceName
            Port = $ServicePort
            Status = "FAIL"
            Reason = "Directory not found"
            BuildTime = 0
        }
        continue
    }

    # Check for pom.xml
    $PomPath = Join-Path $ServiceDir "pom.xml"
    if (-not (Test-Path $PomPath)) {
        Write-Host "  ✗ FAIL: pom.xml not found" -ForegroundColor Red
        $BuildResults += @{
            Service = $ServiceName
            Port = $ServicePort
            Status = "FAIL"
            Reason = "pom.xml missing"
            BuildTime = 0
        }
        continue
    }

    # Run Maven build
    $StartTime = Get-Date
    try {
        Write-Host "  → Running: mvn clean package -DskipTests" -ForegroundColor Gray

        & $MavenCmd -f $PomPath clean package -DskipTests -q *> $ServiceLog 2>&1
        $ExitCode = $LASTEXITCODE

        $TimeDiff = (Get-Date) - $StartTime
        $BuildTime = [math]::Round($TimeDiff.TotalSeconds, 2)

        if ($ExitCode -eq 0) {
            # Check for JAR file
            $TargetDir = Join-Path $ServiceDir "target"
            $JarFiles = Get-ChildItem -Path $TargetDir -Filter "*.jar" -ErrorAction SilentlyContinue | Where-Object { $_.Name -notlike "*.sources.jar" -and $_.Name -notlike "*.javadoc.jar" }

            if ($JarFiles) {
                $JarSize = ($JarFiles[0] | Measure-Object -Property Length -Sum).Sum / 1MB
                $JarSizeFormatted = [math]::Round($JarSize, 2)
                $SuccessMsg = "  SUCCESS: JAR created ($JarSizeFormatted MB) in ${BuildTime}s"
                Write-Host $SuccessMsg -ForegroundColor Green

                $BuildResults += @{
                    Service = $ServiceName
                    Port = $ServicePort
                    Status = "SUCCESS"
                    Reason = "JAR: $($JarFiles[0].Name)"
                    BuildTime = $BuildTime
                    JarSize = $JarSizeFormatted
                }
            } else {
                Write-Host "  ⚠ WARNING: Build succeeded but no JAR found" -ForegroundColor Yellow
                $BuildResults += @{
                    Service = $ServiceName
                    Port = $ServicePort
                    Status = "WARNING"
                    Reason = "No JAR file"
                    BuildTime = $BuildTime
                }
            }
        } else {
            Write-Host "  ✗ FAIL: Maven exit code $ExitCode (see: $ServiceLog)" -ForegroundColor Red
            $BuildResults += @{
                Service = $ServiceName
                Port = $ServicePort
                Status = "FAIL"
                Reason = "Maven error (exit: $ExitCode)"
                BuildTime = $BuildTime
            }
        }
    } catch {
        Write-Host "  ✗ FAIL: $($_.Exception.Message)" -ForegroundColor Red
        $ErrorTime = (Get-Date) - $StartTime
        $BuildResults += @{
            Service = $ServiceName
            Port = $ServicePort
            Status = "FAIL"
            Reason = $_.Exception.Message
            BuildTime = [math]::Round($ErrorTime.TotalSeconds, 2)
        }
    }

    Write-Host ""
}

# Summary Report
Write-Host "=" * 100 -ForegroundColor Cyan
Write-Host "BUILD SUMMARY REPORT" -ForegroundColor Cyan
Write-Host "=" * 100 -ForegroundColor Cyan
Write-Host ""

$SuccessCount = ($BuildResults | Where-Object { $_.Status -eq "SUCCESS" }).Count
$FailCount = ($BuildResults | Where-Object { $_.Status -eq "FAIL" }).Count
$WarningCount = ($BuildResults | Where-Object { $_.Status -eq "WARNING" }).Count
$TotalTime = ($BuildResults | Measure-Object -Property BuildTime -Sum).Sum

Write-Host "Total Services: $($Services.Count)" -ForegroundColor White
Write-Host "Successful: $SuccessCount" -ForegroundColor Green
Write-Host "Failed: $FailCount" -ForegroundColor Red
Write-Host "Warnings: $WarningCount" -ForegroundColor Yellow
Write-Host "Total Build Time: $([math]::Round($TotalTime, 2)) seconds" -ForegroundColor White
Write-Host ""

# Detailed Results Table
Write-Host "DETAILED RESULTS:" -ForegroundColor Cyan
Write-Host ("{0,-35} {1,-6} {2,-10} {3,-50} {4,-10}" -f "Service", "Port", "Status", "Details", "Time(s)") -ForegroundColor White
Write-Host ("-" * 115) -ForegroundColor Gray

foreach ($Result in $BuildResults | Sort-Object { $_.Service }) {
    $StatusColor = switch ($Result.Status) {
        "SUCCESS" { "Green" }
        "FAIL" { "Red" }
        "WARNING" { "Yellow" }
        default { "Gray" }
    }

    if ($Result.JarSize) {
        $Details = "$($Result.Reason) ($($Result.JarSize) MB)"
    } else {
        $Details = $Result.Reason
    }

    Write-Host ("{0,-35} {1,-6} {2,-10} {3,-50} {4,-10}" -f
        $Result.Service,
        $Result.Port,
        $Result.Status,
        $Details,
        $Result.BuildTime
    ) -ForegroundColor $StatusColor
}

Write-Host ""
Write-Host "=" * 100 -ForegroundColor Cyan

# Save results to JSON
$ResultsJson = $BuildResults | ConvertTo-Json -Depth 3
$ResultsFile = Join-Path $BaseDir "..\..\build-results-$(Get-Date -Format 'yyyyMMdd-HHmmss').json"
$ResultsJson | Out-File -FilePath $ResultsFile -Encoding UTF8
Write-Host "Results saved to: $ResultsFile" -ForegroundColor Gray

# Exit with appropriate code
if ($FailCount -gt 0) {
    Write-Host ""
    Write-Host "BUILD FAILED: $FailCount service(s) failed to build" -ForegroundColor Red
    exit 1
} else {
    Write-Host ""
    Write-Host "BUILD SUCCESSFUL: All services built successfully!" -ForegroundColor Green
    exit 0
}
