# STREAMLINED BUILD VERIFICATION - Simple version
$ErrorActionPreference = "Continue"
$BASE_DIR = "C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\shared-infrastructure\Backend\Java"
$services = Get-ChildItem -Path $BASE_DIR -Directory | Select-Object -ExpandProperty Name | Where-Object { $_ -match "service|gateway|registry|discovery" } | Sort-Object

Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "BUILD VERIFICATION - Simple Version" -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "Services: $($services.Count)" -ForegroundColor White
Write-Host ""

$compilePass = 0
$compileFail = 0
$jarPass = 0
$jarFail = 0
$failedList = @()

foreach ($service in $services) {
    $servicePath = Join-Path $BASE_DIR $service
    Write-Host "[$($services.IndexOf($service) + 1)/$($services.Count)] $service" -ForegroundColor Cyan
    
    if (-not (Test-Path $servicePath)) {
        Write-Host "  SKIP: Directory not found" -ForegroundColor Yellow
        continue
    }
    
    Push-Location $servicePath
    
    # Compile
    Write-Host "  Compiling..." -ForegroundColor Gray
    $compileResult = mvn clean compile -DskipTests -q 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  PASS: Compile success" -ForegroundColor Green
        $compilePass++
    } else {
        Write-Host "  FAIL: Compile failed" -ForegroundColor Red
        $compileFail++
        $failedList += $service
        Pop-Location
        continue
    }
    
    # Build JAR
    Write-Host "  Building JAR..." -ForegroundColor Gray
    $jarResult = mvn package -DskipTests -q 2>&1
    $jarFiles = Get-ChildItem -Path "target" -Filter "*.jar" -ErrorAction SilentlyContinue | Where-Object { $_.Name -notlike "*-sources.jar" -and $_.Name -notlike "*-javadoc.jar" }
    
    if ($jarFiles) {
        $jarSize = [math]::Round(($jarFiles[0].Length / 1MB), 2)
        Write-Host "  PASS: JAR created (${jarSize}MB)" -ForegroundColor Green
        $jarPass++
    } else {
        Write-Host "  FAIL: JAR not found" -ForegroundColor Red
        $jarFail++
        $failedList += $service
    }
    
    Pop-Location
    Write-Host ""
}

Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "BUILD VERIFICATION COMPLETE" -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Compile: $compilePass pass, $compileFail fail" -ForegroundColor $(if ($compileFail -eq 0) { "Green" } else { "Yellow" })
Write-Host "JAR Build: $jarPass pass, $jarFail fail" -ForegroundColor $(if ($jarFail -eq 0) { "Green" } else { "Yellow" })
Write-Host ""

if ($failedList.Count -gt 0) {
    Write-Host "Failed services:" -ForegroundColor Red
    foreach ($svc in $failedList) { Write-Host "  - $svc" -ForegroundColor Red }
} else {
    Write-Host "SUCCESS: All services compiled and built JARs!" -ForegroundColor Green
}
Write-Host ""
