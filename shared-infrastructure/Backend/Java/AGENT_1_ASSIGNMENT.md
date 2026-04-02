# AGENT 1 ASSIGNMENT

**Agent ID**: Agent-1
**Model**: Opus 4.5 (Ultrathink Mode)
**Services**: 4

## Your Services (in priority order):

1. **alerting-service**
2. **anti-fraud-rules-service**
3. **anti-fraud-signals-service**
4. **api-gateway**

## Base Path:
```
/mnt/c/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/
```

---

## Step 1: Process alerting-service

1. Navigate to: `alerting-service/`
2. Read `pom.xml` - update version to `1.0.0`
3. Update shared library versions to `1.0.0`
4. Add Testcontainers, JaCoCo, Surefire dependencies/plugins
5. Check test files - update to gold standard pattern
6. Fix any compilation errors
7. Run `mvn clean compile` - verify
8. Run `mvn test` - verify
9. Run `mvn package` - verify
10. Document results

## Step 2: Process anti-fraud-rules-service

[Repeat same steps]

## Step 3: Process anti-fraud-signals-service

[Repeat same steps]

## Step 4: Process api-gateway

[Repeat same steps]

---

## Progress Report Template

Create `AGENT_1_PROGRESS.md`:

```markdown
# Agent 1 Progress Report

| Service | Status | Tests | JAR | Notes |
|---------|--------|-------|-----|-------|
| alerting-service | ✅/🔄/❌ | X/Y | XX MB | ... |
| anti-fraud-rules-service | ... | ... | ... | ... |
| anti-fraud-signals-service | ... | ... | ... | ... |
| api-gateway | ... | ... | ... | ... |

## Issues Encountered:
-

## Services Completed: X/4
```

---

**Start immediately. Do not wait for other agents.**
