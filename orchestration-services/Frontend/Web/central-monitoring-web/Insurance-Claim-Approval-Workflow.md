# Insurance Claim Approval Workflow - UI Documentation

**Version:** 1.0
**Last Updated:** January 10, 2026
**Product:** Gogidix Road Assist Services
**Domain:** Central Monitoring - Insurance Integration

---

## Table of Contents

1. [Overview](#overview)
2. [Workflow States](#workflow-states)
3. [Verification Process](#verification-process)
4. [UI Specifications](#ui-specifications)
5. [Decision Trees](#decision-trees)
6. [Integration Points](#integration-points)
7. [Error Handling](#error-handling)

---

## Overview

The Insurance Claim Approval Workflow is a **critical pre-dispatch process** that verifies insurance coverage before service providers are dispatched. This ensures valid claims are approved quickly while preventing fraud and protecting partners from non-paying requests.

### Key Principles

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    INSURANCE WORKFLOW PRINCIPLES                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ⚡ SPEED TO DISPATCH                                                  │
│  Auto-approve when confidence > 90%                                    │
│  Manual review only when needed                                        │
│  Target: < 30 seconds from request to approval                          │
│                                                                         │
│  🛡️ FRAUD PREVENTION                                                   │
│  Multi-factor verification before approval                              │
│  Risk scoring on every claim                                           │
│  Pattern detection for suspicious activity                             │
│                                                                         │
│  👤 PARTNER PROTECTION                                                  │
│  Partners only receive pre-approved requests                           │
│  Payment guarantee before dispatch                                     │
│  Clear claim status on every job                                      │
│                                                                         │
│  🔄 TRANSPARENT COMMUNICATION                                            │
│  Real-time status updates to all stakeholders                           │
│  Clear rejection reasons with next steps                               │
│  Audit trail for every decision                                       │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Workflow States

### Claim State Machine

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    INSURANCE CLAIM STATE MACHINE                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│                           ┌─────────────────┐                            │
│                           │   REQUEST       │                            │
│                           │   CREATED       │                            │
│                           │   (User         │                            │
│                           │    submits)     │                            │
│                           └────────┬────────┘                            │
│                                    │                                     │
│                                    ▼                                     │
│                           ┌─────────────────┐                            │
│      ┌───────────────────│  INSURANCE INFO  │───────────────────┐      │
│      │                   │   COLLECTED      │                   │      │
│      │                   │ (from user/DB)   │                   │      │
│      │                   └────────┬────────┘                   │      │
│      │                            │                            │      │
│      │                            ▼                            │      │
│      │                   ┌─────────────────┐                            │
│      │      ┌────────────│   VERIFICATION   │────────────┐        │      │
│      │      │            │     IN PROGRESS   │            │        │      │
│      │      │            └────────┬────────┘            │        │      │
│      │      │                     │                     │        │      │
│      │      │    ┌────────────────┴────────────────┐       │        │      │
│      │      │    │                                 │       │        │      │
│      │      │    ▼                                 ▼       │        │      │
│      │      │ ┌──────────┐                    ┌──────────┐ │        │      │
│      │      │ │ VERIFIED │                    │  FAILED   │ │        │      │
│      │      │ │          │                    │          │ │        │      │
│      │      │ └────┬─────┘                    └────┬─────┘ │        │      │
│      │      │      │                               │        │        │      │
│      │      │      ▼                               ▼        │        │      │
│      │      │ ┌────────────────────────────────────────┐ │        │      │
│      │      │ │          DECISION POINT              │ │        │      │
│      │      │ │    (Auto-approve or Manual Review?)   │ │        │      │
│      │      │ └─────────────────┬──────────────────────┘ │        │      │
│      │      │                   │                       │        │      │
│      │      │    ┌──────────────┴──────────────┐       │        │      │
│      │      │    │                             │       │        │      │
│      │      │    ▼                             ▼       │        │      │
│      │      │ ┌──────────┐                ┌──────────┐ │        │      │
│      │      │ │ APPROVED │                │  REVIEW  │ │        │      │
│      │      │ │(Auto/Man)│                │REQUIRED  │ │        │      │
│      │      │ └─────┬────┘                └────┬─────┘ │        │      │
│      │      │       │                          │        │        │      │
│      │      │       └──────────┬───────────────┘        │        │      │
│      │      │                  │                        │        │      │
│      │      │                  ▼                        │        │      │
│      │      │         ┌─────────────────┐                  │        │      │
│      │      │         │  READY FOR      │                  │        │      │
│      │      │         │   DISPATCH      │◄─────────────────┘        │      │
│      │      │         └─────────────────┘                  │        │      │
│      │      │                                        │        │      │
│      └──────┴───────┌────────────────────────────────┐       │        │
│                    │                                │       │        │      │
│                    ▼                                ▼       │        │      │
│             ┌──────────┐                      ┌──────────┐ │        │      │
│             │ REJECTED │                      │ PENDING  │ │        │      │
│             │  (Manual) │                      │  (Wait)  │ │        │      │
│             └──────────┘                      └──────────┘ │        │      │
│                                                           └─────────┘      │
│                                                                     │        │
│  END STATES: APPROVED, REJECTED, CANCELLED                           │        │
│                                                                     │        │
└─────────────────────────────────────────────────────────────────────┘
```

### State Definitions

| State | Description | Duration Target | Next States |
|-------|-------------|------------------|-------------|
| `INFO_COLLECTED` | Insurance details received | < 2s | `VERIFICATION_IN_PROGRESS` |
| `VERIFICATION_IN_PROGRESS` | Checking policy status | < 10s | `VERIFIED`, `FAILED` |
| `VERIFIED` | Policy confirmed active | Immediate | `APPROVED` (if auto), `REVIEW_REQUIRED` (if manual) |
| `FAILED` | Policy invalid/expired | Immediate | `REJECTED` |
| `REVIEW_REQUIRED` | Needs human review | < 60s | `APPROVED`, `REJECTED` |
| `APPROVED` | Cleared for dispatch | N/A | `READY_FOR_DISPATCH` |
| `REJECTED` | Claim denied, payment required | N/A | `PENDING_PAYMENT` |
| `PENDING_PAYMENT` | User must pay directly | N/A | `READY_FOR_DISPATCH` (after payment) |

---

## Verification Process

### Automatic Verification Checks

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    AUTOMATIC VERIFICATION CHECKS                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  CHECK 1: POLICY STATUS                                                 │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Input: Policy Number + Provider ID                              │   │
│  │ API: Provider verification service                               │   │
│  │ Validate:                                                      │   │
│  │  ✓ Policy exists                                               │   │
│  │  ✓ Policy is active (not expired, not cancelled)              │   │
│  │  ✓ Policy is in good standing (no lapsed payments)             │   │
│  │ Return: { valid: boolean, expiryDate: date, holder: string }    │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓                                          │
│  CHECK 2: COVERAGE VALIDATION                                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Input: Policy ID + Service Type                                 │   │
│  │ API: Coverage terms service                                    │   │
│  │ Validate:                                                      │   │
│  │  ✓ Service type is covered                                    │   │
│  │  ✓ Location is within coverage area                           │   │
│  │  ✓ Vehicle type is covered (if applicable)                     │   │
│  │  ✓ Incident type is covered (accident vs. breakdown)          │   │
│  │ Return: { covered: boolean, limits: object, exclusions: [] }   │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓                                          │
│  CHECK 3: LIMIT VERIFICATION                                            │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Input: Policy ID + Claim amount                                │   │
│  │ API: Claim history service                                      │   │
│  │ Validate:                                                      │   │
│  │  ✓ Within annual limit                                         │   │
│  │  ✓ Within per-incident limit                                   │   │
│  │  ✓ Within claim frequency limit (e.g., max 6 per year)         │   │
│  │  ✓ No duplicate claim for same incident                         │   │
│  │ Return: { withinLimits: boolean, remaining: number }           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓                                          │
│  CHECK 4: DRIVER/HOLDER MATCH                                            │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Input: Policy holder name + Requestor name                     │   │
│  │ API: User verification service                                  │   │
│  │ Validate:                                                      │   │
│  │  ✓ Requestor is policy holder OR authorized driver             │   │
│  │  ✓ Vehicle registration matches policy (if applicable)          │   │
│  │  ✓ No suspicious patterns (multiple claims, different drivers)  │   │
│  │ Return: { authorized: boolean, riskScore: number }             │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓                                          │
│  CHECK 5: RISK ASSESSMENT                                                │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Input: All previous check results                               │   │
│  │ Algorithm: Risk scoring model                                   │   │
│  │ Factors:                                                       │   │
│  │  • Claim frequency (last 90 days)                              │   │
│  │  • Amount patterns (round numbers = suspicious)                 │   │
│  │  • Time patterns (claims at similar times/locations)            │   │
│  │  • Driver history                                               │   │
│  │  • Location patterns                                            │   │
│  │ Return: { riskScore: 0-100, recommendation: string }            │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓                                          │
│                         FINAL DECISION                                  │
│                                                                         │
│  ✅ AUTO-APPROVE IF:                                                   │
│     • All checks pass                                                  │
│     • Risk score < 20                                                  │
│     • Claim amount < £100 (individual) or < £500 (corporate)         │
│                                                                         │
│  ⚠️ MANUAL REVIEW IF:                                                 │
│     • All checks pass BUT risk score 20-50                           │
│     • Claim amount > £100 (individual) or > £500 (corporate)         │
│     • First-time policy holder                                       │
│                                                                         │
│  ❌ AUTO-REJECT IF:                                                    │
│     • Any verification check fails                                   │
│     • Risk score > 50                                                 │
│     • Clear fraud indicators                                        │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Risk Scoring Model

```typescript
interface RiskAssessment {
  // Claim frequency (0-30 points)
  frequencyScore: number;  // Claims in last 90 days: 0=0, 1=5, 2=15, 3+=30

  // Amount patterns (0-20 points)
  amountScore: number;     // Round numbers, maxed out limits

  // Time patterns (0-15 points)
  timeScore: number;       // Claims at suspicious times/locations

  // Driver history (0-20 points)
  driverScore: number;     // New driver, multiple drivers on policy

  // Location patterns (0-15 points)
  locationScore: number;   // High-risk areas, repeated locations

  // Total score (0-100)
  totalScore: number;

  // Recommendation
  recommendation: 'auto-approve' | 'manual-review' | 'auto-reject';
}

// Scoring thresholds
const RISK_THRESHOLDS = {
  AUTO_APPROVE: 20,   // Score 0-20: Auto-approve
  MANUAL_REVIEW: 50,  // Score 21-50: Manual review required
  AUTO_REJECT: 51     // Score 51+: Auto-reject
};
```

---

## UI Specifications

### Claims Queue Dashboard

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  ← Back              Insurance Claims Queue                  5 pending       │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Filters: [All] [Pending] [Verified] [Review Required] [Rejected]           │
│  Provider: [All ▼]  Country: [All ▼]  Sort: [Urgency ▼]                      │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  SUMMARY CARDS                                                       │    │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ │    │
│  │  │    8     │ │    5     │ │    2     │ │    1     │ │  100%    │ │    │
│  │  │ Pending  │ │ Verified │ │ Approved │ │ Rejected │ Auto-Appr │    │
│  │  │ Review   │ │          │ │          │ │          │ (Today)   │    │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  PENDING REVIEW (Requires attention)                                 │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │ #REQ-5892 ⚠️ HIGH RISK                                  8min   │  │    │
│  │  │ ───────────────────────────────────────────────────────────  │  │    │
│  │  │ Policy: AXA-456789 • Holder: John Doe                       │  │    │
│  │  │ Service: Towing - Engine Failure                              │  │    │
│  │  │ Claim: £150 (within £500 annual limit: £380 remaining)        │  │    │
│  │  │ Risk Score: 35/100 • Reason: High claim frequency (3 in 90d)│  │    │
│  │  │                                                              │  │    │
│  │  │ Checks: ✓ Policy ✗ Frequency ⚠️ Near limit                  │  │    │
│  │  │                                                              │  │    │
│  │  │ Recommendation: ⚠️ MANUAL REVIEW - Verify claim necessity    │  │    │
│  │  │                                                              │  │    │
│  │  │ [APPROVE] [REJECT] [REQUEST INFO] [VIEW DETAILS]             │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │ #REQ-5891 ⚠️ FIRST TIME                                  12min   │  │    │
│  │  │ ───────────────────────────────────────────────────────────  │  │    │
│  │  │ Policy: Aviva-123456 • Holder: Mary Smith                  │  │    │
│  │  │ Service: Lockout • Downtown Mall                             │  │    │
│  │  │ Claim: £75 (within annual limit: £500 remaining)            │  │    │
│  │  │ Risk Score: 25/100 • Reason: First-time claimer            │  │    │
│  │  │                                                              │  │    │
│  │  │ Checks: ✓ Policy ✓ Coverage ✓ Limits ✓ Driver              │  │    │
│  │  │                                                              │  │    │
│  │  │ Recommendation: ⚠️ MANUAL REVIEW - First claim verification  │  │    │
│  │  │                                                              │  │    │
│  │  │ [APPROVE] [REJECT] [VIEW DETAILS]                            │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  VERIFIED - READY FOR DECISION                                      │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │ #REQ-5890 ✅ LOW RISK                                   5min    │  │    │
│  │  │ Policy: Zurich-CORP-01 • Fleet: ABC Transport              │  │    │
│  │  │ Service: Tire Change • A1 Rd Jn15                           │  │    │
│  │  │ Claim: £80 (within £2000 monthly limit: £1920 remaining)      │  │    │
│  │  │ Risk Score: 5/100 • Reason: Low risk, good history          │  │    │
│  │  │                                                              │  │    │
│  │  │ Recommendation: ✅ CAN AUTO-APPROVE                          │  │    │
│  │  │                                                              │  │    │
│  │  │ [APPROVE NOW] [VIEW DETAILS]                                 │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  View all pending claims [→]                                          │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│  [APPROVE ALL LOW-RISK]              [EXPORT REPORT]                         │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Claim Detail Review Screen

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  ← Back              Claim Review: #REQ-5892                                   │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  CLAIM STATUS                                                       │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Status: ⚠️ Pending Review                                          │    │
│  │  Created: 14:25 (8 minutes ago)                                    │    │
│  │  Priority: 🔴 Urgent                                               │    │
│  │  Request: #REQ-5892                                                │    │
│  │                                                                  │    │
│  │  Progress: ████████████░░░░░░░░░ 60%                              │    │
│  │  Step 4/7: Risk Assessment Complete                               │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  POLICY INFORMATION                                                 │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Provider:           AXA Insurance UK                            │    │
│  │  Policy Number:      POL-AXA-456789                              │    │
│  │  Policy Type:        Comprehensive Roadside Assistance             │    │
│  │  Policy Status:      ✅ Active (expires: December 2025)            │    │
│  │  Policy Holder:      John Doe                                     │    │
│  │  Coverage Level:     Full Coverage                                │    │
│  │  Annual Limit:       £500 (used: £320, remaining: £180)           │    │
│  │  Claims This Year:   3 (max: 6)                                   │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  SERVICE DETAILS                                                    │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Service Type:       Towing                                      │    │
│  │  Incident:           Engine Failure - Vehicle not starting        │    │
│  │  Location:           M4 Motorway, Junction 8                       │    │
│  │  Vehicle:            2022 Ford Fiesta - BG23 XYZ                  │    │
│  │  Estimated Cost:     £150 (subject to actual service)             │    │
│  │  Coverage:           ✅ Covered under policy                      │    │
│  │  Pre-approval:       ⏳ Pending manual review                     │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  VERIFICATION RESULTS                                               │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  ┌────────────────────────────────────────────────────────────┐  │    │
│  │  │ Policy Status          ████████████████████  ✅ VERIFIED     │  │    │
│  │  │ Coverage Check         ████████████████████  ✅ CONFIRMED    │  │    │
│  │  │ Limit Check            ████████████████████  ✅ WITHIN LIMIT │  │    │
│  │  │ Driver Verification    ████████████████████  ✅ AUTHORIZED   │  │    │
│  │  │ Location Verification   ████████████████████  ✅ MATCHES     │  │    │
│  │  │ Frequency Check        ████████████░░░░░░░░░  ⚠️ 3 CLAIMS/90D │  │    │
│  │  └────────────────────────────────────────────────────────────┘  │    │
│  │                                                                  │    │
│  │  Overall Risk Score: 35/100                                      │    │
│  │  ┌────────────────────────────────────────────────────────────┐  │    │
│  │  │ Risk:     ████████████░░░░░░░ 35/100                         │  │    │
│  │  │ Claim Freq:  ████████████████░░ 30 points (3 claims, 90d)   │  │    │
│  │  │ Amount:      ████░░░░░░░░░░░░░░░ 5 points (within limits)   │  │    │
│  │  │ Time:        ██░░░░░░░░░░░░░░░░░ 0 points (normal)         │  │    │
│  │  │ Driver:      ████░░░░░░░░░░░░░░░░ 0 points (authorized)     │  │    │
│  │  │ Location:    ██░░░░░░░░░░░░░░░░░ 0 points (normal area)     │  │    │
│  │  └────────────────────────────────────────────────────────────┘  │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  CLAIM HISTORY (This Policy)                                         │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  ┌────────────────────────────────────────────────────────────┐  │    │
│  │  │ Date       │ Service   │ Location   │ Amount   │ Status    │  │    │
│  │  ├────────────────────────────────────────────────────────────┤  │    │
│  │  │ 15 Dec     │ Towing    │ M25 J10   │ £120     │ Approved  │  │    │
│  │  │ 03 Dec     │ Tire Chg  │ A1 Rd     │ £75      │ Approved  │  │    │
│  │  │ 20 Nov     │ Lockout   │ Downtown  │ £60      │ Approved  │  │    │
│  │  └────────────────────────────────────────────────────────────┘  │    │
│  │                                                                  │    │
│  │  Pattern: ⚠️ Increasing frequency (3 claims in 6 weeks)           │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  DECISION                                                          │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Based on verification results, please select an action:          │    │
│  │                                                                  │    │
│  │  ┌────────────────────────────────────────────────────────────┐  │    │
│  │  │ ✅ APPROVE CLAIM                                            │  │    │
│  │  │ This claim passes verification and can be approved.         │  │    │
│  │  │                                                            │  │    │
│  │  │ Approval notes (optional):                               │  │    │
│  │  │ ┌────────────────────────────────────────────────────────┐ │  │    │
│  │  │ │                                                        │ │  │    │
│  │  │ └────────────────────────────────────────────────────────┘ │  │    │
│  │  │                                                            │  │    │
│  │  │ [CONFIRM APPROVAL]                                       │  │    │
│  │  └────────────────────────────────────────────────────────────┘  │    │
│  │                                                                  │    │
│  │  ┌────────────────────────────────────────────────────────────┐  │    │
│  │  │ ❌ REJECT CLAIM                                             │  │    │
│  │  │ This claim cannot be approved at this time.                 │  │    │
│  │  │                                                            │  │    │
│  │  │ Rejection reason (required):                              │  │    │
│  │  │ ○ Policy invalid/expired                                   │  │    │
│  │  │ ○ Service not covered                                      │  │    │
│  │  │ ○ Exceeds limits                                            │  │    │
│  │  │ ○ Suspicious activity                                      │  │    │
│  │  │ ○ Requires additional verification                          │  │    │
│  │  │ ○ Other: __________________                                 │  │    │
│  │  │                                                            │  │    │
│  │  │ Additional notes:                                         │  │    │
│  │  │ ┌────────────────────────────────────────────────────────┐ │  │    │
│  │  │ │                                                        │ │  │    │
│  │  │ └────────────────────────────────────────────────────────┘ │  │    │
│  │  │                                                            │  │    │
│  │  │ [CONFIRM REJECTION]                                      │  │    │
│  │  └────────────────────────────────────────────────────────────┘  │    │
│  │                                                                  │    │
│  │  ┌────────────────────────────────────────────────────────────┐  │    │
│  │  │ 📞 REQUEST ADDITIONAL INFO                                   │  │    │
│  │  │ Need more information from user or provider                   │  │    │
│  │  │                                                            │  │    │
│  │  │ [CONTACT USER]  [CONTACT PROVIDER]                           │  │    │
│  │  └────────────────────────────────────────────────────────────┘  │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  ACTIONS                                                           │    │
│  │  [← PREVIOUS]                     [SAVE DRAFT]  [NEXT CLAIM →]        │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Approval Confirmation Modal

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              ⚠️ Confirm Approval                             │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  You are about to approve this insurance claim:                           │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  Claim: #REQ-5892                                                  │    │
│  │  Policy: AXA-456789                                               │    │
│  │  Amount: £150                                                      │    │
│  │  Remaining limit: £330                                            │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  By approving this claim:                                                │
│  • The request will be cleared for partner dispatch                       │
│  • AXA Insurance will be notified of the claim                          │
│  • £150 will be reserved from the policy's annual limit                   │
│  • The partner will be guaranteed payment for this service                 │
│                                                                              │
│  This action cannot be undone. The request will proceed to dispatch.       │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  Require user confirmation?                                          │    │
│  │  ◉ Yes, notify user of approval                                    │    │
│  │  ○ No, silent approval                                           │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│                              [CANCEL]  [CONFIRM APPROVAL]                  │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## Decision Trees

### Auto-Approval Decision Tree

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    AUTO-APPROVAL DECISION TREE                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  START: Claim verification complete                                    │
│       │                                                               │
│       ▼                                                               │
│  Is policy valid and active?                                          │
│       │                                                               │
│       ├─ NO ───────────────────────────► REJECT (Policy invalid)     │
│       │                                                               │
│       ├─ YES ────────────────────────►                               │
│       │                                                               │
│       ▼                                                               │
│  Is service type covered?                                            │
│       │                                                               │
│       ├─ NO ───────────────────────────► MANUAL REVIEW (Check exclusion)│
│       │                                                               │
│       ├─ YES ────────────────────────►                               │
│       │                                                               │
│       ▼                                                               │
│  Is within annual and per-incident limits?                           │
│       │                                                               │
│       ├─ NO ───────────────────────────► REJECT (Exceeds limits)       │
│       │                                                               │
│       ├─ YES ────────────────────────►                               │
│       │                                                               │
│       ▼                                                               │
│  Is driver/vehicle authorized?                                       │
│       │                                                               │
│       ├─ NO ───────────────────────────► MANUAL REVIEW (Verify driver)  │
│       │                                                               │
│       ├─ YES ────────────────────────►                               │
│       │                                                               │
│       ▼                                                               │
│  Is risk score < 20?                                                 │
│       │                                                               │
│       ├─ NO ──────────────────────────► MANUAL REVIEW (Assess risk)     │
│       │                                                               │
│       ├─ YES ────────────────────────►                               │
│       │                                                               │
│       ▼                                                               │
│  Is claim amount < threshold?                                       │
│       │  (Individual: <£100, Corporate: <£500)                       │
│       │                                                               │
│       ├─ NO ──────────────────────────► MANUAL REVIEW (High amount)      │
│       │                                                               │
│       ├─ YES ────────────────────────►                               │
│       │                                                               │
│       ▼                                                               │
│  AUTO-APPROVE ✅                                                       │
│       │                                                               │
│       ▼                                                               │
│  Claim approved, proceed to dispatch                                  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Rejection Decision Tree

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    REJECTION DECISION TREE                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  START: Claim verification complete                                    │
│       │                                                               │
│       ▼                                                               │
│  Is policy valid?                                                      │
│       │                                                               │
│       ├─ NO ───────────────────────────► REJECT                       │
│       │   Reason: Policy not valid/expired                             │
│       │   Action: Notify user, offer direct payment                    │
│       │                                                               │
│       ├─ YES ────────────────────────►                               │
│       │                                                               │
│       ▼                                                               │
│  Is service type covered?                                            │
│       │                                                               │
│       ├─ NO ───────────────────────────► REJECT                       │
│       │   Reason: Service not covered by policy                        │
│       │   Action: Notify user of exclusion, offer direct payment       │
│       │                                                               │
│       ├─ YES ────────────────────────►                               │
│       │                                                               │
│       ▼                                                               │
│  Is within limits?                                                    │
│       │                                                               │
│       ├─ NO ───────────────────────────► REJECT                       │
│       │   Reason: Exceeds annual/per-incident limit                    │
│       │   Action: Notify user of limit, offer direct payment           │
│       │                                                               │
│       ├─ YES ────────────────────────►                               │
│       │                                                               │
│       ▼                                                               │
│  Is driver/vehicle authorized?                                       │
│       │                                                               │
│       ├─ NO ───────────────────────────► REJECT                       │
│       │   Reason: Driver/vehicle not authorized                       │
│       │   Action: Notify user, verify authorized driver               │
│       │                                                               │
│       ├─ YES ────────────────────────►                               │
│       │                                                               │
│       ▼                                                               │
│  Is risk score > 50?                                                 │
│       │                                                               │
│       ├─ NO ───────────────────────────► MANUAL REVIEW (Not auto-reject) │
│       │                                                               │
│       ├─ YES ────────────────────────►                               │
│       │                                                               │
│       ▼                                                               │
│  REJECT                                                               │
│  Reason: High fraud risk detected                                    │
│  Action: Escalate to supervisor, request additional verification       │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Integration Points

### External Insurance Provider APIs

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    INSURANCE API INTEGRATION                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  AXA Insurance API                                                      │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Endpoint: https://api.axa.co.uk/roadside/v1/verify             │   │
│  │ Method: POST                                                      │   │
│  │ Authentication: Bearer token (client credentials)               │   │
│  │ Request: { policyNumber, holderName, vehicleReg, serviceType }  │   │
│  │ Response: { valid, coverage, limits, holder, expiry }            │   │
│  │ Timeout: 5 seconds                                              │   │
│  │ Fallback: Manual review                                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  Aviva Insurance API                                                   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Endpoint: https://api.aviva.co.uk/assist/verify                │   │
│  │ Method: POST                                                      │   │
│  │ Authentication: API Key + HMAC signature                          │   │
│  │ Request: { policyNumber, claimDetails }                          │   │
│  │ Response: { valid, coverage, remainingClaims, riskScore }        │   │
│  │ Timeout: 5 seconds                                              │   │
│  │ Fallback: Manual review                                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  Zurich Insurance API                                                  │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Endpoint: https://api.zurich.co.uk/fleet/verify                │   │
│  │ Method: POST                                                      │   │
│  │ Authentication: Mutual TLS                                        │   │
│  │ Request: { fleetId, vehicleId, driverId, serviceType }          │   │
│  │ Response: { valid, coverage, monthlyLimit, remaining }           │   │
│  │ Timeout: 3 seconds                                              │   │
│  │ Fallback: Use cached policy data                                │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  Common Response Format (normalized)                                  │
│  {                                                                     │
│    "providerId": "axa",                                               │
│    "policyNumber": "POL-AXA-456789",                                  │
│    "valid": true,                                                    │
│    "holder": "John Doe",                                             │
│    "coverage": {                                                      │
│      "serviceType": "towing",                                        │
│      "covered": true,                                                │
│      "exclusions": []                                                │
│    },                                                               │
│    "limits": {                                                       │
│      "annual": 500,                                                 │
│      "perIncident": 150,                                           │
│      "remainingAnnual": 320,                                        │
│      "claimsThisYear": 3                                           │
│    },                                                               │
│    "riskScore": 15,                                                 │
│    "recommendation": "auto-approve"                                  │
│  }                                                                   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Error Handling

### API Failure Scenarios

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    API FAILURE HANDLING                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  SCENARIO 1: Provider API Timeout                                      │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Error: API request timed out after 5 seconds                     │   │
│  │ Status: Service unavailable                                       │   │
│  │ Action:                                                          │   │
│  │  • Check if policy exists in local cache                          │   │
│  │  • If cached < 24 hours: Use cached data (with warning)          │   │
│  │  • If no cache or > 24 hours: Route to manual review              │   │
│  │  • Notify IT team of API issue                                    │   │
│  │ User sees:                                                       │   │
│  │  ⚠️ Unable to verify with AXA. Using cached data from 2 hours ago. │   │
│  │  Manual review recommended.                                       │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  SCENARIO 2: Invalid Policy Number                                     │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Error: Policy not found in provider system                        │   │
│  │ Status: Invalid policy                                           │   │
│  │ Action:                                                          │   │
│  │  • Auto-reject claim                                              │   │
│  │  • Prompt user to verify policy number                           │   │
│  │  • Suggest common corrections (typo check)                        │   │
│  │ User sees:                                                       │   │
│  │  ❌ Policy not found. Please check the number and try again.    │   │
│  │  [RE-ENTER POLICY]                                               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  SCENARIO 3: Partial Data Response                                     │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Error: API returned partial data (missing limits)                  │   │
│  │ Status: Degraded response                                        │   │
│  │ Action:                                                          │   │
│  │  • Use available data                                             │   │
│  │  • Flag for manual verification of missing data                   │   │
│  │  • Proceed with caution warning                                   │   │
│  │ User sees:                                                       │   │
│  │  ⚠️ Coverage confirmed, but unable to verify limits.              │   │
│  │  Manual review recommended for approval.                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  SCENARIO 4: Rate Limit Exceeded                                       │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Error: 429 Too Many Requests                                     │   │
│  │ Status: Rate limited                                             │   │
│  │ Action:                                                          │   │
│  │  • Queue request for retry after delay                            │   │
│  │  • Use exponential backoff (5s, 10s, 20s)                         │   │
│  │  • Show countdown to user                                         │   │
│  │ User sees:                                                       │   │
│  │  ⏳ Too many requests to insurer. Retrying in 5...4...3...         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

*Document Version: 1.0*
*Last Updated: January 10, 2026*
*Author: Central Monitoring Domain Team*
