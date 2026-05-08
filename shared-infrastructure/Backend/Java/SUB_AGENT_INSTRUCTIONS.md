# Sub-Agent Instructions: Shared-Infrastructure Services Gold Standard Update

**Mission**: Update all shared-infrastructure services to v1.0.0 gold standard following the access-control-service blueprint.

**Context**: You are one of 8 parallel sub-agents (Opus 4.5, ultrathink mode) working simultaneously.

---

## Completed Services (DO NOT TOUCH)

| Service | Status |
|---------|--------|
| access-control-service | ✅ COMPLETE - Gold Standard Blueprint |
| event-audit-service | 🔄 CODE UPDATED - Pending build verification |

---

## Your Assignment: Agent {N}

### Services to Process:
{SERVICE_LIST}

### Base Path:
```
/mnt/c/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/
```

---

## Gold Standard Checklist (Apply to Each Service)

For each service, ensure the following:

### 1. pom.xml Updates
- [ ] Change version from `0.0.1-SNAPSHOT` to `1.0.0`
- [ ] Update shared library versions to `1.0.0`:
  - `shared-security-library`
  - `shared-request-context-library`
  - `shared-exception-library`
- [ ] Add Testcontainers dependencies (if missing):
  ```xml
  <dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mongodb</artifactId>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
  </dependency>
  ```
- [ ] Add spring-security-test (if service uses Security):
  ```xml
  <dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
  </dependency>
  ```
- [ ] Add JaCoCo plugin:
  ```xml
  <plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
      <execution>
        <goals><goal>prepare-agent</goal></goals>
      </execution>
      <execution>
        <id>report</id>
        <phase>test</phase>
        <goals><goal>report</goal></goals>
      </execution>
    </executions>
  </plugin>
  ```
- [ ] Add Surefire plugin:
  ```xml
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
  </plugin>
  ```

### 2. Test Files
- [ ] Update `HexArchitectureTest.java` to use `@AnalyzeClasses` with `@ArchTest` pattern
- [ ] Update `ContextLoadsTest.java` to use `@Testcontainers` with MongoDB
- [ ] Fix any test compilation errors (RequestContext constructor, AuditEvent types, etc.)
- [ ] Ensure tests reference correct package structure

### 3. Build & Verify
- [ ] Run `mvn clean compile` - verify no compilation errors
- [ ] Run `mvn test` - verify tests pass
- [ ] Run `mvn package` - verify JAR is created
- [ ] Document any issues in service-specific notes

---

## Package Structure Reference

The gold standard uses Maven standard directory layout:
```
src/main/java/com/gogidix/rapidassist/{service-name}/
├── domain/
│   ├── model/
│   ├── aggregate/
│   ├── event/
│   ├── policy/
│   ├── repository/
│   └── port/
│       ├── in/
│       └── out/
├── application/
│   ├── command/
│   ├── query/
│   ├── service/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   └── mapper/
├── infrastructure/
│   ├── persistence/
│   │   ├── mongo/
│   │   └── redis/
│   ├── security/
│   ├── adapter/
│   │   ├── rest/
│   │   └── storage/
│   ├── messaging/
│   │   ├── events/
│   │   └── kafka/
│   └── config/
├── interfaces/
│   └── rest/
└── shared/
    ├── exception/
    └── util/
```

**Note**: Some services may use `adapters/` instead of `interfaces/`. This is acceptable - maintain existing structure but ensure hexagonal principles are followed.

---

## Critical Fix Patterns

### RequestContext Constructor
```java
// OLD (4 params) - WRONG
new RequestContext("tenant", "country", "correlation", "user")

// NEW (5 params) - CORRECT
new RequestContext(correlationId, country, tenantId, userId, requestId)
// Example:
new RequestContext("corr-456", "US", "tenant-123", "user-789", null)
```

### AuditEvent Payload
```java
// OLD - WRONG
new AuditEvent(..., "{}")  // String

// NEW - CORRECT
new AuditEvent(..., Map.of("key", "value"))  // Map<String, Object>
```

### HexArchitectureTest Pattern
```java
@AnalyzeClasses(packages = "com.gogidix.rapidassist.{service-package}",
        importOptions = {ImportOption.DoNotIncludeTests.class})
@DisplayName("Hexagonal Architecture Tests")
class HexArchitectureTest {
    @ArchTest
    static final ArchRule domain_layer_should_not_depend_on_other_layers = ...
    // Use static final fields with @ArchTest, NOT @DisplayName on fields
}
```

---

## Progress Tracking

After completing each service:
1. Update the `AGENT_{N}_PROGRESS.md` file with:
   - Service name
   - Status: ✅ COMPLETE / 🔄 PARTIAL / ❌ FAILED
   - Issues encountered
   - Test results summary
   - JAR size and location

---

## Shared Libraries Location
```
/mnt/c/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/shared-libraries/Backend/Java/
```

Available at version `1.0.0` in local Maven repo:
- shared-security-library
- shared-request-context-library
- shared-exception-library
- shared-audit-library
- shared-dto-library
- shared-observability-library
- shared-persistence-library

---

## Communication Protocol

1. **Start immediately** - Do not wait for other agents
2. **Work independently** - Each agent has unique services
3. **Document everything** - Leave detailed progress notes
4. **Handle failures gracefully** - If a service has issues, document and move to next
5. **No shared resources** - Each agent works on separate services

---

## Emergency Contact

If you encounter a service with:
- No source code (empty directory)
- Corrupted pom.xml
- Unresolvable dependencies
- Architecture that doesn't match hexagonal pattern

Document the issue in your progress report and continue to the next service.

---

**Remember**: We cannot skip any service. Every service must reach v1.0.0 gold standard.

**Target**: All 41 services at production-ready state by end of parallel execution.

---

*Generated by: Lead Agent (Opus 4.5)*
*Date: 2026-01-23*
*Session: Foundation-Domain Shared-Infrastructure Standardization*
