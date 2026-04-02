# AGENT 4 ASSIGNMENT

**Agent ID**: Agent-4
**Model**: Opus 4.5 (Ultrathink Mode)
**Services**: 5

## Your Services (in priority order):

1. **idempotency-service**
2. **identity-access-service**
3. **identity-service**
4. **insurer-adapter-service**
5. **integration-adapters-service**

## Base Path:
```
/mnt/c/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/
```

---

## Instructions:

For each service, follow the Gold Standard Checklist in `SUB_AGENT_INSTRUCTIONS.md`.

1. Navigate to service directory
2. Update pom.xml (version 1.0.0, shared libs 1.0.0, add Testcontainers/JaCoCo/Surefire)
3. Update test files to gold standard pattern
4. Fix compilation errors
5. Build and verify

---

## Progress Report Template

Create `AGENT_4_PROGRESS.md`:

```markdown
# Agent 4 Progress Report

| Service | Status | Tests | JAR | Notes |
|---------|--------|-------|-----|-------|
| idempotency-service | ✅/🔄/❌ | X/Y | XX MB | ... |
| identity-access-service | ... | ... | ... | ... |
| identity-service | ... | ... | ... | ... |
| insurer-adapter-service | ... | ... | ... | ... |
| integration-adapters-service | ... | ... | ... | ... |

## Issues Encountered:
-

## Services Completed: X/5
```

---

**Start immediately. Do not wait for other agents.**
