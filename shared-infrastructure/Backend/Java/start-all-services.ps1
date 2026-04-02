# =============================================================================
# Rapid-Assist Services Startup Script
# =============================================================================

$ErrorActionPreference = "Continue"

$BASE_DIR = "C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\shared-infrastructure\Backend\Java"

$SERVICES = @(
    @{Name="service-registry-discovery"; Port=8761; Order=1},
    @{Name="api-gateway"; Port=8304; Order=2},
    @{Name="access-control-service"; Port=8080; Order=3},
    @{Name="identity-service"; Port=8081; Order=4},
    @{Name="identity-access-service"; Port=8082; Order=5},
    @{Name="mfa-service"; Port=8083; Order=6},
    @{Name="data-privacy-consent-service"; Port=8084; Order=7},
    @{Name="waf-policy-service"; Port=8085; Order=8},
    @{Name="audit-correlation-service"; Port=8086; Order=9},
    @{Name="billing-service"; Port=8087; Order=10},
    @{Name="payment-service"; Port=8088; Order=11},
    @{Name="payments-adapter-service"; Port=8089; Order=12},
    @{Name="pricing-service"; Port=8090; Order=13},
    @{Name="policy-engine-service"; Port=8091; Order=14},
    @{Name="insurer-adapter-service"; Port=8092; Order=15},
    @{Name="notification-service"; Port=8093; Order=16},
    @{Name="reporting-read-model-service"; Port=8094; Order=17},
    @{Name="tenant-org-service"; Port=8095; Order=18},
    @{Name="user-profile-service"; Port=8096; Order=19},
    @{Name="geo-location-service"; Port=8097; Order=20},
    @{Name="database-indexing-service"; Port=8098; Order=21},
    @{Name="database-management-service"; Port=8099; Order=22},
    @{Name="idempotency-service"; Port=8100; Order=23},
    @{Name="integration-adapters-service"; Port=8101; Order=24},
    @{Name="logging-aggregation-service"; Port=8102; Order=25},
    @{Name="metrics-telemetry-service"; Port=8103; Order=26},
    @{Name="request-routing-service"; Port=8104; Order=27},
    @{Name="service-health-monitor-service"; Port=8105; Order=28}
)

Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host "Rapid-Assist Services - Starting All Services" -ForegroundColor Cyan
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host ""

$SORTED_SERVICES = $SERVICES | Sort-Object { $_.Order }

foreach ($SERVICE in $SORTED_SERVICES) {
    $SERVICE_PATH = Join-Path $BASE_DIR $SERVICE.Name
    $LOG_FILE = "$BASE_DIR\logs\$($SERVICE.Name).log"

    Write-Host "[$($SERVICE.Order)/28] Starting $($SERVICE.Name) on port $($SERVICE.Port)..." -ForegroundColor Yellow

    if (Test-Path $SERVICE_PATH) {
        Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run", "-Dspring-boot.run.profiles=dev", "-DskipTests=true" -WorkingDirectory $SERVICE_PATH -NoNewWindow -RedirectStandardOutput "$LOG_FILE" -RedirectStandardError "$LOG_FILE.err"

        Write-Host "  ✓ Started $($SERVICE.Name) - PID: $($PID)" -ForegroundColor Green
        Start-Sleep -Seconds 5
    } else {
        Write-Host "  ✗ Path not found: $SERVICE_PATH" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host "All Services Started! Check logs in: $BASE_DIR\logs\" -ForegroundColor Cyan
Write-Host "============================================================================" -ForegroundColor Cyan
