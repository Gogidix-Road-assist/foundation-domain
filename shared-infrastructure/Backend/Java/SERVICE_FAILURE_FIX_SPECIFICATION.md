# SERVICE FAILURE FIX SPECIFICATION
## Phase 2: Documentation of Failures and Fixes

**Generated**: 2026-02-02
**Scope**: 27 failing services out of 41 total
**Approach**: Surgical, 100% precision fixes applied one-by-one

---

## EXECUTIVE SUMMARY

**Total Failing Services**: 27 (65.9%)
- **Compilation Failures**: 17 services (pom.xml issues)
- **JAR Build Failures**: 10 services (test compilation errors)

**Root Cause Categories**:
1. Malformed pom.xml files (missing versions, duplicates, malformed XML)
2. Test code incompatibility with shared library APIs

---

## CATEGORY 1: COMPILATION FAILURES (17 services)

### PATTERN 1.1: MISSING DEPENDENCY VERSION

#### 1. api-gateway
**File**: `pom.xml`
**Error**: `'dependencies.dependency.version' for org.springframework.boot:spring-boot-starter-data-redis:jar is missing. @ line 162, column 17`
**Root Cause**: Duplicate `spring-boot-starter-data-redis` in dependencyManagement without version
**Location**: Lines 33-36 (dependencyManagement), 163-165 (dependencies)
**Fix**:
```xml
<!-- REMOVE from dependencyManagement (lines 33-36) -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- KEEP in dependencies (lines 163-165) - will inherit version from parent -->
```

---

### PATTERN 1.2: SEVERELY MALFORMED POM.XML

#### 2. data-privacy-consent-service
**File**: `pom.xml`
**Errors**:
- Massive duplicate dependencies
- Malformed XML structure
- Duplicate plugins

**Specific Issues**:
- Line 21: `<dependencies><dependency>` - missing newline
- `spring-security-test` duplicated 10+ times (lines 48, 57, 66, 76, 86, 96, 106, 114, 123, 132, 142, 153, 164)
- Line 167: `</dependencies><build>` - missing newline
- Lines 175-178, 196-200: Duplicate `maven-surefire-plugin`
- Lines 186-187, 191-192: Malformed `<goals>` tags

**Fix**: Complete pom.xml reconstruction using passing service template (access-control-service)

**Reference Template**: `access-control-service/pom.xml`

---

### PATTERN 1.3: DUPLICATE/MISPLACED DEPENDENCIES

#### 3-17. Remaining Compilation Failures (Need Individual Analysis)
- database-indexing-service
- database-management-service
- geo-location-service
- identity-access-service
- identity-service
- insurer-adapter-service
- integration-adapters-service
- payment-service
- payments-adapter-service
- policy-engine-service
- pricing-service
- reporting-read-model-service
- request-routing-service
- service-health-monitor-service
- service-registry-discovery

**Action Required**: Individual pom.xml analysis for each service

---

## CATEGORY 2: JAR BUILD FAILURES (10 services)

### PATTERN 2.1: TEST COMPILATION ERRORS - OUTDATED API USAGE

#### 1. audit-correlation-service
**File**: `src/test/java/com/gogidix/rapidassist/audit/correlation/service/adapters/in/web/CorrelationControllerTest.java`
**Error**: `constructor RequestContext cannot be applied to given types`
**Details**:
- Line 42: `RequestContext` expects 5 parameters, given 3
- Line 89: Type mismatch `List<E>` vs `Map<String, String>`

**Root Cause**: Test code uses old `RequestContext` API from shared-request-context-library

**Fix**: Update test code to use current `RequestContext` record signature
```java
// OLD (incorrect):
new RequestContext("val1", "val2", "val3")

// NEW (correct):
new RequestContext("correlationId", "userId", "sessionId", "requestId", "timestamp")
```

---

#### 2-10. Remaining JAR Build Failures (Need Individual Analysis)
- billing-service
- idempotency-service
- logging-aggregation-service
- metrics-telemetry-service
- mfa-service
- notification-service
- tenant-org-service
- user-profile-service
- waf-policy-service

**Action Required**: Individual test code analysis and API fix for each service

---

## FIX STRATEGY

### For Compilation Failures (pom.xml issues):
1. Read existing pom.xml
2. Identify specific issue (missing version, duplicate, malformed XML)
3. Apply surgical fix to ONLY the problematic section
4. Verify XML structure is valid
5. Run `mvn clean compile` to verify

### For JAR Build Failures (test compilation errors):
1. Identify the failing test file
2. Read the error message carefully
3. Identify the API mismatch
4. Update test code to use correct API signature
5. Run `mvn clean package -DskipTests` to verify JAR builds

---

## PHASE 3 EXECUTION PLAN

Each service will be fixed INDIVIDUALLY with:
1. ✅ Compile verification
2. ✅ Build verification
3. ✅ Test execution (if applicable)
4. ✅ JAR build verification

**NO BATCHING. ONE SERVICE AT A TIME.**

---

## NEXT ACTIONS

1. ✅ **COMPLETED**: Phase 1 - Comparative Analysis
2. ✅ **IN PROGRESS**: Phase 2 - Documentation
3. ⏳ **PENDING**: Phase 3 - Surgical Fixes (one by one)

**READY TO PROCEED TO PHASE 3?**

---

**Document Version**: 1.0
**Last Updated**: 2026-02-02 03:37:00
**Status**: Phase 2 Complete, Ready for Phase 3
