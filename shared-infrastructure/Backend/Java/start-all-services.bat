@echo off
REM =============================================================================
REM Rapid-Assist Services Startup Script (Windows Batch)
REM =============================================================================

setlocal enabledelayedexpansion

set BASE_DIR=C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\shared-infrastructure\Backend\Java
set LOG_DIR=%BASE_DIR%\logs

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

echo =============================================================================
echo Rapid-Assist Services - Starting All Services
echo =============================================================================
echo.

start "Service Registry" cmd /k "cd %BASE_DIR%\service-registry-discovery && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\service-registry.log 2>&1"
timeout /t 8 /nobreak >nul

start "API Gateway" cmd /k "cd %BASE_DIR%\api-gateway && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\api-gateway.log 2>&1"
timeout /t 5 /nobreak >nul

start "Access Control" cmd /k "cd %BASE_DIR%\access-control-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\access-control.log 2>&1"
timeout /t 5 /nobreak >nul

start "Identity Service" cmd /k "cd %BASE_DIR%\identity-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\identity.log 2>&1"
timeout /t 5 /nobreak >nul

start "Identity Access" cmd /k "cd %BASE_DIR%\identity-access-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\identity-access.log 2>&1"
timeout /t 5 /nobreak >nul

start "MFA Service" cmd /k "cd %BASE_DIR%\mfa-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\mfa.log 2>&1"
timeout /t 5 /nobreak >nul

start "Data Privacy" cmd /k "cd %BASE_DIR%\data-privacy-consent-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\data-privacy.log 2>&1"
timeout /t 5 /nobreak >nul

start "WAF Policy" cmd /k "cd %BASE_DIR%\waf-policy-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\waf-policy.log 2>&1"
timeout /t 5 /nobreak >nul

start "Audit Correlation" cmd /k "cd %BASE_DIR%\audit-correlation-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\audit.log 2>&1"
timeout /t 5 /nobreak >nul

start "Billing Service" cmd /k "cd %BASE_DIR%\billing-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\billing.log 2>&1"
timeout /t 5 /nobreak >nul

start "Payment Service" cmd /k "cd %BASE_DIR%\payment-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\payment.log 2>&1"
timeout /t 5 /nobreak >nul

start "Payments Adapter" cmd /k "cd %BASE_DIR%\payments-adapter-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\payments-adapter.log 2>&1"
timeout /t 5 /nobreak >nul

start "Pricing Service" cmd /k "cd %BASE_DIR%\pricing-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\pricing.log 2>&1"
timeout /t 5 /nobreak >nul

start "Policy Engine" cmd /k "cd %BASE_DIR%\policy-engine-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\policy-engine.log 2>&1"
timeout /t 5 /nobreak >nul

start "Insurer Adapter" cmd /k "cd %BASE_DIR%\insurer-adapter-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\insurer-adapter.log 2>&1"
timeout /t 5 /nobreak >nul

start "Notification Service" cmd /k "cd %BASE_DIR%\notification-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\notification.log 2>&1"
timeout /t 5 /nobreak >nul

start "Reporting Service" cmd /k "cd %BASE_DIR%\reporting-read-model-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\reporting.log 2>&1"
timeout /t 5 /nobreak >nul

start "Tenant Service" cmd /k "cd %BASE_DIR%\tenant-org-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\tenant.log 2>&1"
timeout /t 5 /nobreak >nul

start "User Profile" cmd /k "cd %BASE_DIR%\user-profile-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\user-profile.log 2>&1"
timeout /t 5 /nobreak >nul

start "Geo Location" cmd /k "cd %BASE_DIR%\geo-location-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\geo-location.log 2>&1"
timeout /t 5 /nobreak >nul

start "Database Indexing" cmd /k "cd %BASE_DIR%\database-indexing-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\database-indexing.log 2>&1"
timeout /t 5 /nobreak >nul

start "Database Management" cmd /k "cd %BASE_DIR%\database-management-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\database-management.log 2>&1"
timeout /t 5 /nobreak >nul

start "Idempotency" cmd /k "cd %BASE_DIR%\idempotency-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\idempotency.log 2>&1"
timeout /t 5 /nobreak >nul

start "Integration Adapters" cmd /k "cd %BASE_DIR%\integration-adapters-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\integration.log 2>&1"
timeout /t 5 /nobreak >nul

start "Logging Aggregation" cmd /k "cd %BASE_DIR%\logging-aggregation-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\logging.log 2>&1"
timeout /t 5 /nobreak >nul

start "Metrics Telemetry" cmd /k "cd %BASE_DIR%\metrics-telemetry-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\metrics.log 2>&1"
timeout /t 5 /nobreak >nul

start "Request Routing" cmd /k "cd %BASE_DIR%\request-routing-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\request-routing.log 2>&1"
timeout /t 5 /nobreak >nul

start "Health Monitor" cmd /k "cd %BASE_DIR%\service-health-monitor-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests=true > %LOG_DIR%\health-monitor.log 2>&1"

echo.
echo =============================================================================
echo All Services Started!
echo Logs available in: %LOG_DIR%
echo =============================================================================
echo.
echo Press any key to exit...
pause >nul
