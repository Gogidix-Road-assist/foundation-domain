#!/usr/bin/env python3
"""
Audit script to check v1.0.0 gold standard compliance across all 41 services
"""
import os
import json
from pathlib import Path

BASE_PATH = Path(r"C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\shared-infrastructure\Backend\Java")

services = [
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
]

results = {}

for service in services:
    service_path = BASE_PATH / service
    if not service_path.exists():
        results[service] = {"status": "NOT FOUND", "compliance": 0}
        continue

    checks = {
        "has_pom": (service_path / "pom.xml").exists(),
        "has_openapi": False,
        "has_global_exception": False,
        "has_cors": False,
        "has_dockerfile": (service_path / "Dockerfile").exists(),
        "v1_0_0": False
    }

    # Check pom.xml for version 1.0.0
    pom_path = service_path / "pom.xml"
    if pom_path.exists():
        try:
            with open(pom_path, 'r', encoding='utf-8') as f:
                pom_content = f.read()
                checks["v1_0_0"] = "<version>1.0.0</version>" in pom_content
        except:
            pass

    # Check for config files
    src_path = service_path / "src" / "main" / "java"
    if src_path.exists():
        # Find OpenApiConfiguration
        for file in src_path.rglob("OpenApiConfiguration.java"):
            checks["has_openapi"] = True
            break

        # Find GlobalExceptionHandler
        for file in src_path.rglob("GlobalExceptionHandler.java"):
            checks["has_global_exception"] = True
            break

        # Find CORS configuration (multiple naming patterns)
        cors_found = False
        for pattern in ["CorsConfiguration.java", "CorsConfig.java", "WebCorsConfiguration.java"]:
            if list(src_path.rglob(pattern)):
                cors_found = True
                break
        checks["has_cors"] = cors_found

    # Calculate compliance percentage
    compliance = sum(checks.values()) / len(checks) * 100

    results[service] = {
        "checks": checks,
        "compliance": round(compliance, 1)
    }

# Generate report
print("=" * 80)
print("SHARED-INFRASTRUCTURE SERVICES AUDIT - v1.0.0 GOLD STANDARD")
print("=" * 80)
print()

complete = []
in_progress = []
not_started = []

for service, data in sorted(results.items(), key=lambda x: x[1]["compliance"], reverse=True):
    checks = data["checks"]
    compliance = data["compliance"]

    status_icon = "[COMPLETE]" if compliance >= 85 else "[PROGRESS]" if compliance >= 50 else "[PENDING]"

    print(f"{status_icon} {service:<40} {compliance:>6}%")
    if not all(checks.values()):
        missing = [k for k, v in checks.items() if not v]
        print(f"   Missing: {', '.join(missing)}")

    if compliance >= 85:
        complete.append(service)
    elif compliance >= 50:
        in_progress.append(service)
    else:
        not_started.append(service)

print()
print("=" * 80)
print("SUMMARY")
print("=" * 80)
print(f"[COMPLETE] Complete (>=85%): {len(complete)}/41")
print(f"[PROGRESS] In Progress (50-84%): {len(in_progress)}/41")
print(f"[PENDING] Not Started (<50%): {len(not_started)}/41")
print()

# Save to JSON
output_path = BASE_PATH / "audit_results.json"
with open(output_path, 'w', encoding='utf-8') as f:
    json.dump(results, f, indent=2)

print(f"\nDetailed results saved to: {output_path}")
