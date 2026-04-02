# AI Services Monitoring Dashboard - User Journeys

**Version:** 2.0
**Date:** March 7, 2026
**Product:** AI Services Monitoring Dashboard
**Domain:** Foundation Domain - AI Services
**Port:** 3000 (Dashboard)
**Status:** User Journey Definition

---

## User Personas

### Persona 1: DevOps Engineer - Alex Martinez

| Attribute | Details |
|-----------|---------|
| **Role** | Senior DevOps Engineer |
| **Age** | 34 years old |
| **Location** | Dublin HQ (Remote monitoring) |
| **Goals** | Maintain 99.9% uptime, respond to alerts quickly, optimize performance |
| **Pain Points** | Too many monitoring tools, fragmented alerting, manual service restarts |
| **Tech Savviness** | Very high - Infrastructure expert |
| **Time Pressure** | High - Manages critical infrastructure |
| **Key KPIs** | MTTD, MTTR, Uptime, Alert response time |

### Persona 2: Platform Engineer - Sarah Kim

| Attribute | Details |
|-----------|---------|
| **Role** | Platform Engineer |
| **Age** | 29 years old |
| **Location** | Dublin HQ |
| **Goals** | Capacity planning, performance optimization, service integration |
| **Pain Points** | No centralized metrics, difficult to track service dependencies |
| **Tech Savviness** | High - Full-stack engineer |
| **Time Pressure** | Medium-high - Balances development and operations |
| **Key KPIs** | Service performance, resource utilization, capacity planning |

### Persona 3: AI Researcher - Dr. James Wilson

| Attribute | Details |
|-----------|---------|
| **Role** | AI Research Scientist |
| **Age** | 41 years old |
| **Location** | Remote (London) |
| **Goals** | Monitor model performance, track accuracy metrics, identify model drift |
| **Pain Points** | Hard to correlate model performance with business metrics |
| **Tech Savviness** | High - Data scientist and ML engineer |
| **Time Pressure** | Medium - Research-focused with monitoring needs |
| **Key KPIs** | Model accuracy, prediction quality, training metrics |

---

## Primary User Journeys

### Journey 1: DevOps Engineer Morning Health Check

**Persona:** Alex Martinez (DevOps Engineer)
**Duration:** 5-10 minutes
**Frequency:** Daily (8:00 AM)

#### Step-by-Step Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  DEVOPS ENGINEER MORNING HEALTH CHECK JOURNEY                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  STEP 1: DASHBOARD OVERVIEW                                    [1 minute]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  • Opens AI Services Dashboard: http://ai-dashboard.gogidix.ie    │   │
│  │  • Authentication via SSO (Okta)                                   │   │
│  │  • Dashboard loads with last update: 8:00:05 AM                   │   │
│  │                                                                       │   │
│  │  Services Summary:                                                   │   │
│  │    Total: 30 │ Operational: 27 │ Degraded: 2 │ Down: 1             │   │
│  │    Visual: Green bar mostly filled, orange/red segments visible     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STEP 2: CHECK ALERTS (3 Active)                                [2 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Active Alerts Panel:                                                │   │
│  │    🔴 Critical: AI Fraud Detection Service - CPU 95%, Memory 88%      │   │
│  │       Age: 2 minutes • Impact: High - Transaction delays            │   │
│  │                                                                       │   │
│  │    🟡 Warning: AI Chatbot Service - Response time 1.2s              │   │
│  │       Age: 5 minutes • Impact: Medium - Customer experience         │   │
│  │                                                                       │   │
│  │    🟡 Warning: AI Recommendation Engine - Memory 78%                │   │
│  │       Age: 8 minutes • Impact: Low - Monitor                        │   │
│  │                                                                       │   │
│  │  Alex prioritizes: Critical alert first → Clicks [Investigate]       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STEP 3: INVESTIGATE CRITICAL ALERT                             [3 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Service Detail: AI Fraud Detection Service                           │   │
│  │                                                                       │   │
│  │  Current Status:                                                      │   │
│  │    • CPU: 95% (▲ trending up)                                      │   │
│  │    • Memory: 88% (→ stable)                                        │   │
│  │    • Response Time: 1.8s (▲ 6x vs normal 300ms)                    │   │
│  │    • Error Rate: 2.5% (▲ 5x vs normal 0.5%)                       │   │
│  │                                                                       │   │
│  │  🤖 AI Root Cause Analysis displays:                                  │   │
│  │    "Unusual transaction volume (+45% vs normal)                     │   │
│  │     Complex fraud patterns requiring more CPU                      │   │
│  │     Recommendation: Scale service capacity"                        │   │
│  │                                                                       │   │
│  │  Alex actions:                                                        │   │
│  │    1. Clicks [Scale Service] → Adds 2 instances                     │   │
│  │    2. Monitors dashboard: CPU dropping to 65% in 2 minutes           │   │
│  │    3. Acknowledges alert → Moves to resolved state                  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STEP 4: CHECK DEGRADED SERVICES                               [2 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Scans service grid for orange (degraded) services:                  │   │
│  │    • AI Chatbot Service: Response time 1.2s → Scheduled for review   │   │
│  │    • AI Recommendation Engine: Memory 78% → No immediate action      │   │
│  │                                                                       │   │
│  │  Notes: Both services within acceptable limits, monitoring            │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STEP 5: REVIEW PERFORMANCE TRENDS                              [2 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Clicks [Analytics] → Performance Trends                            │   │
│  │                                                                       │   │
│  │  Observations:                                                        │   │
│  │    • Overall platform CPU: 45% (stable ✓)                          │   │
│  │    • Overall platform memory: 62% (stable ✓)                        │   │
│  │    • Average response time: 245ms (▲ 12ms vs yesterday - OK)         │   │
│  │    • Error rate: 0.8% (▼ 0.2% vs yesterday ✓)                       │   │
│  │                                                                       │   │
│  │  🤖 AI Prediction: "All services stable. No issues predicted         │   │
│  │   for next 8 hours. Confidence: 94%"                                │   │
│  │                                                                       │   │
│  │  Alex notes: System healthy, no immediate concerns                  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  TOTAL JOURNEY TIME: 10 minutes                                               │
│  ALERTS RESOLVED: 1 critical, 2 warnings monitored                          │
│  ACTIONS TAKEN: Scaled fraud detection, acknowledged alerts                  │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

### Journey 2: Platform Engineer Capacity Planning

**Persona:** Sarah Kim (Platform Engineer)
**Duration:** 20-25 minutes
**Frequency:** Weekly (Friday afternoons)

#### Step-by-Step Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  PLATFORM ENGINEER CAPACITY PLANNING JOURNEY                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  STEP 1: ACCESS ANALYTICS DASHBOARD                           [2 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  • Logs into AI Services Dashboard                                    │   │
│  │  • Clicks [Analytics] → Opens analytics overview                    │   │
│  │  • Selects time range: "Last 7 Days"                                 │   │
│  │                                                                       │   │
│  │  Analytics Dashboard loads with weekly summary                       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STEP 2: REVIEW PERFORMANCE TRENDS                              [5 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Performance Trends (7-Day):                                          │   │
│  │    • CPU Usage: Average 42% (▼ 3% vs previous week) ✓               │   │
│  │    • Memory Usage: Average 58% (▲ 5% vs previous week) ⚠️           │   │
│  │    • Response Time: Average 235ms (stable ✓)                        │   │
│  │    • Error Rate: Average 0.6% (▼ 0.2% ✓)                          │   │
│  │                                                                       │   │
│  │  Sarah notes: Memory usage trending up - investigate                 │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STEP 3: CATEGORY PERFORMANCE BREAKDOWN                      [5 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Category Comparison:                                                  │   │
│  │    • Core AI Services: Memory 65% (highest) 🔴                     │   │
│  │    • Business Intelligence: Memory 52% (medium) 🟡                 │   │
│  │    • Business Operations: Memory 48% (good) 🟢                     │   │
│  │                                                                       │   │
│  │  Top memory consumers (Core AI):                                      │   │
│  │    1. AI Fraud Detection: 88% memory usage                          │   │
│  │    2. AI Recommendation Engine: 78%                                 │   │
│  │    3. AI Document Analyzer: 72%                                    │   │
│  │                                                                       │   │
│  │  Sarah identifies: Memory optimization needed for top 3 services    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STEP 4: AI CAPACITY FORECASTING                                [4 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  🤖 AI Capacity Forecasting:                                          │   │
│  │    "Based on current trends, memory usage will reach 80% in        │   │
│  │     2-3 weeks. At current growth rate (+15% weekly), capacity      │   │
│  │     exhaustion expected in 5-6 weeks."                             │   │
│  │                                                                       │   │
│  │    Recommendations:                                                  │   │
│  │    1. Increase memory allocation for top 3 services (+4GB each)    │   │
│  │    2. Optimize AI Fraud Detection model (reduce memory by 30%)     │   │
│  │    3. Implement memory caching layer (-20% memory usage)          │   │
│  │                                                                       │   │
│  │    Confidence: 89%"                                                  │   │
│  │                                                                       │   │
│  │  Sarah reviews recommendations and creates action plan               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STEP 5: CREATE CAPACITY PLAN                                   [4 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Capacity Plan Created:                                                │   │
│  │    Q2 2026 - AI Services Capacity Enhancement                        │   │
│  │    ────────────────────────────────────────                          │   │
│  │    Immediate (This Week):                                            │   │
│  │    • Increase memory: AI Fraud Detection +4GB                        │   │
│  │    • Increase memory: AI Recommendation Engine +4GB                 │   │
│  │                                                                       │   │
│  │    Short-term (Next 2 Weeks):                                        │   │
│  │    • Optimize AI Fraud Detection model                              │   │
│  │    • Implement memory caching layer                                 │   │
│  │                                                                       │   │
│  │    Medium-term (Next Month):                                         │   │
│  │    • Review all Core AI services for memory optimization            │   │
│  │    • Plan capacity expansion if growth continues                    │   │
│  │                                                                       │   │
│  │  Submitted for review and implementation                             │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  TOTAL JOURNEY TIME: 20 minutes                                               │
│  INSIGHTS GAINED: Memory trend identified, proactive capacity plan         │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

### Journey 3: AI Researcher Model Performance Review

**Persona:** Dr. James Wilson (AI Researcher)
**Duration:** 15-20 minutes
**Frequency:** Weekly (Monday mornings)

#### Step-by-Step Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  AI RESEARCHER MODEL PERFORMANCE REVIEW JOURNEY                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  STEP 1: ACCESS MODEL PERFORMANCE METRICS                      [3 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  • Logs into AI Services Dashboard (read-only access)               │   │
│  │  • Navigates to Analytics → Model Performance                        │   │
│  │  • Selects "AI Fraud Detection" for detailed review                  │   │
│  │                                                                       │   │
│  │  Service Detail: AI Fraud Detection                                   │   │
│  │    • Model Version: v3.2.1                                          │   │
│  │    • Last Training: 7 days ago                                       │   │
│  │    • Accuracy: 94.2% (▼ 0.3% vs last week) ⚠️                       │   │
│  │    • Precision: 92.8% (→ stable)                                    │   │
│  │    • Recall: 91.5% (▲ 0.2%)                                        │   │
│  │    • F1 Score: 92.1% (▼ 0.1%)                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STEP 2: ANALYZE ACCURACY TREND                                 [5 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Dr. Wilson reviews accuracy trend (30-day chart):                   │   │
│  │    • Current: 94.2%                                                 │   │
│  │    • 30-day average: 94.8%                                         │   │
│  │    • Peak: 95.5% (2 weeks ago)                                     │   │
│  │    • Trend: Slight decline (-0.6% over 30 days)                     │   │
│  │                                                                       │   │
│  │  🤖 AI Model Drift Analysis:                                          │   │
│  │    "Model drift detected: Accuracy declining 0.6% in 30 days.      │   │
│  │     Contributing factors:                                            │   │
│  │     • New fraud patterns not in training data                       │   │
│  │     • Seasonal transaction pattern changes                          │   │
│  │     • Model age: 7 weeks since last retraining                     │   │
│  │                                                                       │   │
│  │     Recommendation: Retrain model with latest data"                 │   │
│  │                                                                       │   │
│  │  Confidence: 87%                                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STEP 3: COMPARE WITH OTHER MODELS                               [3 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Model Performance Comparison (Fraud Detection Models):              │   │
│  │    • AI Fraud Detection (v3.2.1): 94.2% accuracy                    │   │
│  │    • Legacy Model (v2.8): 91.5% accuracy (baseline)                │   │
│  │    • Experimental Model (v4.0-beta): 96.1% accuracy (testing)      │   │
│  │                                                                       │   │
│  │  Dr. Wilson notes: v4.0-beta shows 1.9% improvement                 │   │
│  │  Decision: Accelerate v4.0 testing and deployment                   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STEP 4: RETRAINING DECISION                                     [3 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Dr. Wilson actions:                                                  │   │
│  │    1. Initiates model retraining with latest 7 days of data        │   │
│  │    2. Schedules v4.0-beta deployment for next week                   │   │
│  │    3. Creates monitoring plan: Track accuracy for 14 days          │   │
│  │                                                                       │   │
│  │  Expected outcome: Accuracy back to 95%+ within 2 weeks              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STEP 5: DOCUMENT FINDINGS                                     [3 minutes]     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Research Notes Created:                                              │   │
│  │    • Model drift detected: -0.6% accuracy over 30 days               │   │
│  │    • Root cause: New fraud patterns, seasonal changes                │   │
│  │    • Action: Retrain v3.2.1, deploy v4.0-beta                       │   │
│  │    • Expected improvement: +0.8% accuracy (94.2% → 95.0%)           │   │
│  │                                                                       │   │
│  │  Shared with ML engineering team for implementation                  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  TOTAL JOURNEY TIME: 17 minutes                                               │
│  MODEL INSIGHTS: Drift identified, retraining initiated, v4.0 accelerated  │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## AI-Enhanced Journeys

### Journey 4: AI-Powered Anomaly Detection Workflow

**Persona:** DevOps Engineer
**Duration:** 10-15 minutes
**Context:** AI detects unusual service behavior

#### Flow Summary

1. **AI Anomaly Alert**: "Unusual memory pattern detected in AI Recommendation Engine"
2. **Dashboard Investigation**: Navigate to service detail page
3. **AI Analysis Display**:
   - Memory spike from 45% to 92% in 5 minutes
   - Correlation with: New feature deployment, traffic spike (+30%)
   - Root cause: Memory leak in new recommendation algorithm
4. **AI Recommendations**:
   - Rollback to previous version
   - Fix memory leak in new algorithm
   - Increase monitoring frequency
5. **Execution**: One-click rollback, create JIRA ticket for fix
6. **Verification**: Memory back to normal, monitor for 2 hours

---

## Journey Metrics Summary

| Journey | Primary Persona | Duration | Frequency | AI Impact |
|---------|-----------------|----------|-----------|-----------|
| Morning Health Check | DevOps Engineer | 10 min | Daily | 60% faster alert response |
| Capacity Planning | Platform Engineer | 20 min | Weekly | 89% prediction confidence |
| Model Performance Review | AI Researcher | 17 min | Weekly | 87% drift detection accuracy |
| Anomaly Detection | DevOps Engineer | 12 min | As needed | 90% anomaly detection rate |

---

**Document Status:** User Journeys - Complete
**Owner:** Platform Engineering Team
