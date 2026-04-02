# =====================================================================
# Test Central-Configuration Services (8 services)
# =====================================================================
# Run from PowerShell on Windows
# Tests: compile, build, test, verify JAR
# =====================================================================

$ErrorActionPreference = "Continue"

# Convert Windows path to WSL path format
$WindowsBaseDir = "C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\central-configuration\Backend\Java"
$WslBaseDir = "/mnt/c/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/central-configuration/Backend/Java"

$LOG_DIR = "C:\Users\HP\Desktop\central-config-test-logs"
$Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"

# Create log directory
New-Item -ItemType Directory -Force -Path $LOG_DIR | Out-Null
$SummaryFile = "$LOG_DIR\summary_$Timestamp.txt"

Write-Host "============================================" -ForegroundColor Cyan
Write-Host " Central-Configuration Domain - Test Suite" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Base Directory: $WindowsBaseDir"
Write-Host "Log Directory: $LOG_DIR"
Write-Host ""

$Services = @(
    "config-service",
    "country-localization-config-service",
    "dynamic-routing-config-service",
    "feature-flags-service",
    "policy-configuration-service",
    "rate-limit-policy-service",
    "release-rollout-config-service",
    "tenancy-configuration-service"
)

$Results = @()

foreach ($Service in $Services) {
    $ServicePath = Join-Path $WindowsBaseDir $Service
    $WslServicePath = "$WslBaseDir/$Service"
    $LogFile = "$LOG_DIR\$($Service)_$Timestamp.log"

    Write-Host "----------------------------------------" -ForegroundColor Yellow
    Write-Host "Testing: $Service" -ForegroundColor Yellow
    Write-Host "----------------------------------------" -ForegroundColor Yellow

    $Result = [PSCustomObject]@{
        Service = $Service
        Exists = "NO"
        Compile = "FAIL"
        Build = "FAIL"
        Test = "FAIL"
        JAR = "NOT_FOUND"
        Error = ""
    }

    # Check if service directory exists
    if (-not (Test-Path $ServicePath)) {
        Write-Host "  ❌ Directory not found: $ServicePath" -ForegroundColor Red
        $Result.Error = "Directory not found"
        $Results += $Result
        continue
    }
    $Result.Exists = "YES"
    Write-Host "  ✅ Directory found" -ForegroundColor Green

    # Step 1: Compile
    Write-Host "  [1/4] Compiling..." -ForegroundColor Gray
    $BashCommand = "cd '$WslServicePath' && mvn clean compile -q 2>&1"
    $CompileOutput = wsl bash -c $BashCommand 2>&1
    $CompileOutput | Out-File $LogFile
    if ($LASTEXITCODE -eq 0) {
        Write-Host "       ✅ Compile SUCCESS" -ForegroundColor Green
        $Result.Compile = "PASS"
    } else {
        Write-Host "       ❌ Compile FAIL" -ForegroundColor Red
        $Result.Error = "Compile failed"
    }

    # Step 2: Build (package)
    Write-Host "  [2/4] Building (mvn package)..." -ForegroundColor Gray
    $BashCommand = "cd '$WslServicePath' && mvn clean package -DskipTests -q 2>&1"
    $BuildOutput = wsl bash -c $BashCommand 2>&1
    $BuildOutput | Out-File "$LogFile.build" -Append
    if ($LASTEXITCODE -eq 0) {
        Write-Host "       ✅ Build SUCCESS" -ForegroundColor Green
        $Result.Build = "PASS"
    } else {
        Write-Host "       ❌ Build FAIL" -ForegroundColor Red
        if ($Result.Error -eq "") { $Result.Error = "Build failed" }
    }

    # Step 3: Test
    Write-Host "  [3/4] Running Tests..." -ForegroundColor Gray
    $BashCommand = "cd '$WslServicePath' && mvn test -q 2>&1"
    $TestOutput = wsl bash -c $BashCommand 2>&1
    $TestOutput | Out-File "$LogFile.test" -Append

    # Parse test results
    if ($LASTEXITCODE -eq 0) {
        $TestOutput | Select-String "Tests run:" | ForEach-Object {
            Write-Host "       ✅ $_" -ForegroundColor Green
        }
        $Result.Test = "PASS"
    } else {
        $TestOutput | Select-String "Tests run:" | ForEach-Object {
            Write-Host "       ❌ $_" -ForegroundColor Red
        }
        $Result.Test = "FAIL"
        if ($Result.Error -eq "") { $Result.Error = "Tests failed" }
    }

    # Step 4: Verify JAR
    Write-Host "  [4/4] Checking JAR..." -ForegroundColor Gray
    $TargetPath = Join-Path $ServicePath "target"
    $JarFiles = Get-ChildItem -Path $TargetPath -Filter "*.jar" -ErrorAction SilentlyContinue | Where-Object { $_.Name -notlike "*sources.jar" -and $_.Name -notlike "*javadoc.jar" }

    if ($JarFiles) {
        foreach ($Jar in $JarFiles) {
            Write-Host "       ✅ JAR: $($Jar.Name)" -ForegroundColor Green
        }
        $Result.JAR = "FOUND"
    } else {
        Write-Host "       ❌ No JAR found in target/" -ForegroundColor Red
    }

    $Results += $Result
    Write-Host ""
}

# Print Summary
Write-Host "============================================" -ForegroundColor Cyan
Write-Host " SUMMARY - Central-Configuration Domain" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

$Results | Format-Table -AutoSize Service, Exists, Compile, Build, Test, JAR, Error

# Save summary to file
$Results | Format-Table -AutoSize Service, Exists, Compile, Build, Test, JAR, Error | Out-File $SummaryFile

Write-Host ""
Write-Host "Full logs saved to: $LOG_DIR" -ForegroundColor Cyan
Write-Host "Summary saved to: $SummaryFile" -ForegroundColor Cyan

# Count statistics
$TotalServices = $Results.Count
$CompilePass = ($Results | Where-Object { $_.Compile -eq "PASS" }).Count
$BuildPass = ($Results | Where-Object { $_.Build -eq "PASS" }).Count
$TestPass = ($Results | Where-Object { $_.Test -eq "PASS" }).Count
$JarFound = ($Results | Where-Object { $_.JAR -eq "FOUND" }).Count

Write-Host ""
Write-Host "=== STATISTICS ===" -ForegroundColor Yellow
Write-Host "Total Services: $TotalServices"
Write-Host "Compile Pass:   $CompilePass / $TotalServices"
Write-Host "Build Pass:     $BuildPass / $TotalServices"
Write-Host "Test Pass:      $TestPass / $TotalServices"
Write-Host "JAR Found:      $JarFound / $TotalServices"

Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
