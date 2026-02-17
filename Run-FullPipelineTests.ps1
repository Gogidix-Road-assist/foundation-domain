# ============================================================================
# Foundation-Domain: Full Pipeline Validation Script
# ============================================================================
# Runs complete production pipeline: Compile → Test → Package → Smoke Test
# ============================================================================

$ErrorActionPreference = "Continue"
$FoundationDomain = "C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain"
$LogFile = "$FoundationDomain\PipelineValidation_$(Get-Date -Format 'yyyyMMdd_HHmmss').log"
$ResultsFile = "$FoundationDomain\PipelineValidation_Results.csv"

# Initialize results CSV
"Service,Subdomain,Compile_Status,Compile_Time,Test_Status,Test_Time,JAR_Status,JAR_Size,SmokeTest_Status,Overall_Status" | Out-File -FilePath $ResultsFile -Encoding UTF8

# Counters
$TotalServices = 0
$CompilePass = 0
$TestPass = 0
$JARPass = 0
$SmokePass = 0
$OverallPass = 0

# Hash tables for tracking
$ServicesByStatus = @{
    "All_Pass" = @()
    "Compile_Fail" = @()
    "Test_Fail" = @()
    "JAR_Fail" = @()
    "Smoke_Fail" = @()
    "Skipped" = @()
}

Function Log-Message {
    param([string]$Message)
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    "[$timestamp] $Message" | Out-File -FilePath $LogFile -Append -Encoding UTF8
    Write-Host "[$timestamp] $Message" -ForegroundColor Cyan
}

Function Invoke-MavenCommand {
    param(
        [string]$ServicePath,
        [string]$ServiceName,
        [string]$Command
    )

    Log-Message "Running: $Command for $ServiceName"
    $startTime = Get-Date

    Push-Location $ServicePath
    try {
        $output = mvn $Command -q 2>&1
        $exitCode = $LASTEXITCODE
        $duration = ((Get-Date) - $startTime).TotalSeconds

        if ($exitCode -eq 0) {
            Log-Message "  ✓ SUCCESS ($duration seconds)"
            return @{ "Success" = $true; "Time" = $duration; "Output" = $output }
        } else {
            Log-Message "  ✗ FAILED ($duration seconds)"
            return @{ "Success" = $false; "Time" = $duration; "Output" = $output }
        }
    }
    catch {
        $duration = ((Get-Date) - $startTime).TotalSeconds
        Log-Message "  ✗ ERROR: $_ ($duration seconds)"
        return @{ "Success" = $false; "Time" = $duration; "Output" = $_ }
    }
    finally {
        Pop-Location
    }
}

Function Test-Compilation {
    param([string]$Path, [string]$Name)
    $result = Invoke-MavenCommand $Path $Name "clean compile"
    return $result
}

Function Test-UnitTests {
    param([string]$Path, [string]$Name)
    $result = Invoke-MavenCommand $Path $Name "test"
    return $result
}

Function Test-JARBuild {
    param([string]$Path, [string]$Name)
    $result = Invoke-MavenCommand $Path $Name "package -DskipTests=false"

    # Check if JAR was created
    $jarPath = Join-Path $Path "target\$Name.jar"
    if (Test-Path $jarPath) {
        $jarSize = (Get-Item $jarPath).Length / 1MB
        Log-Message "  JAR Size: $([math]::Round($jarSize, 2)) MB"
        $result["JARSize"] = $jarSize
        $result["JARExists"] = $true
    } else {
        # Try to find any JAR in target
        $jars = Get-ChildItem (Join-Path $Path "target") -Filter "*.jar" -ErrorAction SilentlyContinue
        if ($jars) {
            $jarSize = ($jars[0].Length) / 1MB
            Log-Message "  JAR Size: $([math]::Round($jarSize, 2)) MB"
            $result["JARSize"] = $jarSize
            $result["JARExists"] = $true
        } else {
            Log-Message "  ✗ No JAR file found"
            $result["JARExists"] = $false
            $result["Success"] = $false
        }
    }
    return $result
}

Function Test-SmokeTests {
    param([string]$Path, [string]$Name)
    $result = Invoke-MavenCommand $Path $Name "test -Dtest=*SmokeTest"
    return $result
}

Function Invoke-ServiceValidation {
    param(
        [string]$ServicePath,
        [string]$ServiceName,
        [string]$Subdomain
    )

    $TotalServices++
    Log-Message ""
    Log-Message "============================================================================"
    Log-Message "VALIDATING: $ServiceName ($Subdomain)"
    Log-Message "Path: $ServicePath"
    Log-Message "============================================================================"

    # Skip known problematic service
    if ($ServiceName -eq "ai-search-optimization-service") {
        Log-Message "⚠ SKIPPED: Known malformed pom.xml"
        "$ServiceName,$Subdomain,SKIPPED,Malformed pom.xml,SKIPPED,0,SKIPPED,0,SKIPPED,SKIPPED" | Out-File -FilePath $ResultsFile -Append -Encoding UTF8
        $ServicesByStatus["Skipped"] += $ServiceName
        return
    }

    # Check if pom.xml exists
    $pomPath = Join-Path $ServicePath "pom.xml"
    if (-not (Test-Path $pomPath)) {
        Log-Message "✗ SKIPPED: No pom.xml found"
        "$ServiceName,$Subdomain,SKIPPED,No pom.xml,SKIPPED,0,SKIPPED,0,SKIPPED,SKIPPED" | Out-File -FilePath $ResultsFile -Append -Encoding UTF8
        $ServicesByStatus["Skipped"] += $ServiceName
        return
    }

    # Step 1: Compilation
    $compileResult = Test-Compilation $ServicePath $ServiceName
    $compileStatus = if ($compileResult.Success) { "PASS" } else { "FAIL" }
    $compileTime = if ($compileResult.Time) { $compileResult.Time } else { 0 }

    if (-not $compileResult.Success) {
        Log-Message "✗ COMPILATION FAILED - Skipping remaining tests"
        "$ServiceName,$Subdomain,$compileStatus,$compileTime,SKIPPED,0,SKIPPED,0,SKIPPED,FAIL" | Out-File -FilePath $ResultsFile -Append -Encoding UTF8
        $ServicesByStatus["Compile_Fail"] += $ServiceName
        return
    }
    $CompilePass++

    # Step 2: Unit Tests
    $testResult = Test-UnitTests $ServicePath $ServiceName
    $testStatus = if ($testResult.Success) { "PASS" } else { "FAIL" }
    $testTime = if ($testResult.Time) { $testResult.Time } else { 0 }

    if ($testResult.Success) { $TestPass++ }
    else { $ServicesByStatus["Test_Fail"] += $ServiceName }

    # Step 3: JAR Build
    $jarResult = Test-JARBuild $ServicePath $ServiceName
    $jarStatus = if ($jarResult.Success -and $jarResult.JARExists) { "PASS" } else { "FAIL" }
    $jarSize = if ($jarResult.JARSize) { $jarResult.JARSize } else { 0 }

    if ($jarResult.Success -and $jarResult.JARExists) { $JARPass++ }
    else { $ServicesByStatus["JAR_Fail"] += $ServiceName }

    # Step 4: Smoke Tests
    $smokeResult = Test-SmokeTests $ServicePath $ServiceName
    $smokeStatus = if ($smokeResult.Success) { "PASS" } else { "FAIL" }

    if ($smokeResult.Success) { $SmokePass++ }
    else { $ServicesByStatus["Smoke_Fail"] += $ServiceName }

    # Overall Status
    $overallStatus = if ($compileResult.Success -and $testResult.Success -and $jarResult.Success -and $jarResult.JARExists -and $smokeResult.Success) { "PASS" } else { "FAIL" }

    if ($overallStatus -eq "PASS") {
        $OverallPass++
        $ServicesByStatus["All_Pass"] += $ServiceName
        Log-Message "✓ $ServiceName - ALL TESTS PASSED"
    } else {
        Log-Message "✗ $ServiceName - SOME TESTS FAILED"
    }

    # Write to CSV
    "$ServiceName,$Subdomain,$compileStatus,$compileTime,$testStatus,$testTime,$jarStatus,$jarSize,$smokeStatus,$overallStatus" | Out-File -FilePath $ResultsFile -Append -Encoding UTF8
}

# ============================================================================
# MAIN EXECUTION
# ============================================================================

Log-Message "============================================================================"
Log-Message "    FOUNDATION-DOMAIN: FULL PIPELINE VALIDATION"
Log-Message "============================================================================"
Log-Message "Foundation Domain: $FoundationDomain"
Log-Message "Log File: $LogFile"
Log-Message "Results File: $ResultsFile"
Log-Message "Start Time: $(Get-Date)"
Log-Message ""

# Subdomains to process
$subdomains = @(
    "shared-infrastructure\Backend\Java",
    "orchestration-services\Backend\Java",
    "ai-services\Backend\Java",
    "central-configuration\Backend\Java",
    "shared-libraries\Backend\Java"
)

Log-Message "Processing subdomains..."
foreach ($sub in $subdomains) {
    $subPath = Join-Path $FoundationDomain $sub
    if (Test-Path $subPath) {
        Log-Message "Found: $sub"
    } else {
        Log-Message "Not found: $sub"
    }
}

Log-Message ""
Log-Message "============================================================================"
Log-Message "BEGINNING PIPELINE VALIDATION"
Log-Message "============================================================================"

# Process each subdomain
foreach ($subdomainPath in $subdomains) {
    $fullSubPath = Join-Path $FoundationDomain $subdomainPath

    if (-not (Test-Path $fullSubPath)) {
        Log-Message "Skipping missing path: $fullSubPath"
        continue
    }

    $subdomainName = ($subdomainPath -split "\\")[0]
    Log-Message ""
    Log-Message "----------------------------------------------------------------------------------------"
    Log-Message "SUBDOMAIN: $subdomainName"
    Log-Message "----------------------------------------------------------------------------------------"

    # Get all service directories
    $services = Get-ChildItem $fullSubPath -Directory | Where-Object {
        $_.Name -match "-service$" -or $_.Name -match "-library$"
    }

    Log-Message "Found $($services.Count) services to validate"

    foreach ($service in $services) {
        Invoke-ServiceValidation -ServicePath $service.FullName -ServiceName $service.Name -Subdomain $subdomainName
    }
}

# ============================================================================
# FINAL REPORT
# ============================================================================

Log-Message ""
Log-Message "============================================================================"
Log-Message "                           VALIDATION COMPLETE"
Log-Message "============================================================================"
Log-Message "End Time: $(Get-Date)"
Log-Message ""

# Calculate percentages
$compileRate = if ($TotalServices -gt 0) { [math]::Round(($CompilePass / $TotalServices) * 100, 1) } else { 0 }
$testRate = if ($TotalServices -gt 0) { [math]::Round(($TestPass / $TotalServices) * 100, 1) } else { 0 }
$jarRate = if ($TotalServices -gt 0) { [math]::Round(($JARPass / $TotalServices) * 100, 1) } else { 0 }
$smokeRate = if ($TotalServices -gt 0) { [math]::Round(($SmokePass / $TotalServices) * 100, 1) } else { 0 }
$overallRate = if ($TotalServices -gt 0) { [math]::Round(($OverallPass / $TotalServices) * 100, 1) } else { 0 }

Log-Message "┌────────────────────────────────────────────────────────────────────────────┐"
Log-Message "│                           SUMMARY STATISTICS                             │"
Log-Message "├────────────────────────────────────────────────────────────────────────────┤"
Log-Message "│  Total Services Processed:    $TotalServices"
Log-Message "│                                                                                        │"
Log-Message "│  Stage 1: Compilation         │  Pass: $CompilePass  │  Rate: $compileRate%"
Log-Message "│  Stage 2: Unit Tests          │  Pass: $TestPass     │  Rate: $testRate%"
Log-Message "│  Stage 3: JAR Build           │  Pass: $JARPass      │  Rate: $jarRate%"
Log-Message "│  Stage 4: Smoke Tests         │  Pass: $SmokePass   │  Rate: $smokeRate%"
Log-Message "│                                                                                        │"
Log-Message "│  OVERALL PRODUCTION READY:   │  Pass: $OverallPass  │  Rate: $overallRate%"
Log-Message "└────────────────────────────────────────────────────────────────────────────┘"
Log-Message ""

# Services by status
if ($ServicesByStatus["All_Pass"].Count -gt 0) {
    Log-Message "✓ FULLY PASSED ($($ServicesByStatus['All_Pass'].Count) services):"
    foreach ($s in $ServicesByStatus["All_Pass"]) {
        Log-Message "    • $s"
    }
}

if ($ServicesByStatus["Compile_Fail"].Count -gt 0) {
    Log-Message ""
    Log-Message "✗ COMPILATION FAILED ($($ServicesByStatus['Compile_Fail'].Count) services):"
    foreach ($s in $ServicesByStatus["Compile_Fail"]) {
        Log-Message "    • $s"
    }
}

if ($ServicesByStatus["Test_Fail"].Count -gt 0) {
    Log-Message ""
    Log-Message "⚠ TEST FAILED ($($ServicesByStatus['Test_Fail'].Count) services):"
    foreach ($s in $ServicesByStatus["Test_Fail"]) {
        Log-Message "    • $s"
    }
}

if ($ServicesByStatus["JAR_Fail"].Count -gt 0) {
    Log-Message ""
    Log-Message "✗ JAR BUILD FAILED ($($ServicesByStatus['JAR_Fail'].Count) services):"
    foreach ($s in $ServicesByStatus["JAR_Fail"]) {
        Log-Message "    • $s"
    }
}

if ($ServicesByStatus["Smoke_Fail"].Count -gt 0) {
    Log-Message ""
    Log-Message "⚠ SMOKE TEST FAILED ($($ServicesByStatus['Smoke_Fail'].Count) services):"
    foreach ($s in $ServicesByStatus["Smoke_Fail"]) {
        Log-Message "    • $s"
    }
}

if ($ServicesByStatus["Skipped"].Count -gt 0) {
    Log-Message ""
    Log-Message "⏭ SKIPPED ($($ServicesByStatus['Skipped'].Count) services):"
    foreach ($s in $ServicesByStatus["Skipped"]) {
        Log-Message "    • $s"
    }
}

Log-Message ""
Log-Message "============================================================================"
Log-Message "Results saved to: $ResultsFile"
Log-Message "Log saved to: $LogFile"
Log-Message "============================================================================"

# Display final verdict
if ($overallRate -ge 90) {
    Write-Host ""
    Write-Host "╔═══════════════════════════════════════════════════════════════════════════╗" -ForegroundColor Green
    Write-Host "║                   PRODUCTION READY: $overallRate%                                      ║" -ForegroundColor Green
    Write-Host "╚═══════════════════════════════════════════════════════════════════════════╝" -ForegroundColor Green
} elseif ($overallRate -ge 70) {
    Write-Host ""
    Write-Host "╔═══════════════════════════════════════════════════════════════════════════╗" -ForegroundColor Yellow
    Write-Host "║              CONDITIONAL PRODUCTION READY: $overallRate%                              ║" -ForegroundColor Yellow
    Write-Host "║              Some services require fixes before production                    ║" -ForegroundColor Yellow
    Write-Host "╚═══════════════════════════════════════════════════════════════════════════╝" -ForegroundColor Yellow
} else {
    Write-Host ""
    Write-Host "╔═══════════════════════════════════════════════════════════════════════════╗" -ForegroundColor Red
    Write-Host "║                     NOT PRODUCTION READY: $overallRate%                                 ║" -ForegroundColor Red
    Write-Host "║              Significant work required before production                          ║" -ForegroundColor Red
    Write-Host "╚═══════════════════════════════════════════════════════════════════════════╝" -ForegroundColor Red
}
