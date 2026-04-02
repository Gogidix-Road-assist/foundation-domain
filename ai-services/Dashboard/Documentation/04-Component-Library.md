# AI Services Monitoring Dashboard - Component Library

**Version:** 2.0
**Date:** March 7, 2026
**Product:** AI Services Monitoring Dashboard
**Domain:** Foundation Domain - AI Services
**Port:** 3000 (Dashboard)
**Status:** Component Specification

---

## Design Tokens

```css
:root {
  /* Primary Colors */
  --dashboard-blue: #2563EB;
  --dashboard-dark: #1E40AF;
  --dashboard-light: #DBEAFE;

  /* Status Colors */
  --status-operational: #10B981;
  --status-degraded: #F59E0B;
  --status-down: #DC2626;
  --status-unknown: #64748B;

  /* Alert Severity */
  --alert-critical: #DC2626;
  --alert-warning: #F59E0B;
  --alert-info: #3B82F6;

  /* Metric Colors */
  --metric-cpu-high: #EF4444;
  --metric-cpu-warning: #F59E0B;
  --metric-memory-high: #8B5CF6;
  --metric-memory-warning: #A78BFA;
  --metric-response-slow: #EC4899;

  /* Category Colors */
  --category-core-ai: #8B5CF6;
  --category-bi: #3B82F6;
  --category-ops: #10B981;
}
```

---

## Component Catalog

### 1. ServiceStatusCard

**Purpose:** Display individual service status with metrics

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  <ServiceStatusCard />                                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│  PROPS:                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ prop          │ type      │ default    │ description               │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ service       │ object    │ required   │ Service metadata           │   │
│  │ status        │ enum     │ required   │ operational, degraded,    │   │
│  │               │          │            │ down, unknown             │   │
│  │ metrics       │ object[]  │ required   │ CPU, memory, response,    │   │
│  │               │          │            │ error rate                │   │
│  │ lastCheck     │ date      │ required   │ Last health check          │   │
│  │ onClick       │ function  │ undefined  │ Card click callback       │   │
│  │ showAlerts    │ boolean   │ true       │ Show alert badge           │   │
│  │ compact       │ boolean   │ false      │ Compact layout            │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  DISPLAYS: Service name, status badge, metrics with trends, alert count    │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2. AlertCard

**Purpose:** Display alert with severity and actions

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  <AlertCard />                                                                │
├─────────────────────────────────────────────────────────────────────────────┤
│  PROPS:                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ prop          │ type      │ default    │ description               │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ alert         │ object    │ required   │ Alert data                 │   │
│  │ severity      │ enum     │ required   │ critical, warning, info    │   │
│  │ onAcknowledge │ function  │ required   │ Acknowledge callback       │   │
│  │ onDismiss     │ function  │ undefined  │ Dismiss callback          │   │
│  │ onInvestigate │ function  │ undefined  │ Investigate callback       │   │
│  │ onRestart     │ function  │ undefined  │ Restart service callback   │   │
│  │ showDetails   │ boolean   │ true       │ Show detailed metrics      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  SEVERITY COLORS: Critical (Red), Warning (Orange), Info (Blue)              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3. MetricChart

**Purpose:** Display metric trends over time

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  <MetricChart />                                                              │
├─────────────────────────────────────────────────────────────────────────────┤
│  PROPS:                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ prop          │ type      │ default    │ description               │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ metric        │ enum     │ required   │ cpu, memory, response,    │   │
│  │               │          │            │ errorRate, requestRate    │   │
│  │ service       │ object    │ required   │ Service data               │   │
│  │ timeRange     │ enum     │ 24h       │ 24h, 7d, 30d              │   │
│  │ showAverage   │ boolean   │ true       │ Show average line          │   │
│  │ showThresholds│ boolean   │ true       │ Show warning/critical     │   │
│  │ onTimeRangeChange│ function │ undefined│ Time range change callback │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  CHART TYPE: Line chart with area fill, dual Y-axis optional               │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4. ServiceGrid

**Purpose:** Display all services in grid layout

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  <ServiceGrid />                                                              │
├─────────────────────────────────────────────────────────────────────────────┤
│  PROPS:                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ prop          │ type      │ default    │ description               │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ services      │ object[]  │ required   │ All services data          │   │
│  │ filter        │ object    │ undefined  │ {status, category, search}│   │
│  │ groupBy       │ enum     │ category   │ category, status, none     │   │
│  │ onServiceClick│ function  │ required   │ Service click callback     │   │
│  │ compact       │ boolean   │ false      │ Compact card layout        │   │
│  │ autoRefresh   │ boolean   │ true       │ Auto-refresh every 10s     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  LAYOUT: Responsive grid (3 columns desktop, 2 tablet, 1 mobile)              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5. AIPredictionCard

**Purpose:** Display AI-generated predictions and recommendations

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  <AIPredictionCard />                                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│  PROPS:                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ prop          │ type      │ default    │ description               │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ prediction    │ object    │ required   │ AI prediction data         │   │
│  │ confidence    │ number    │ required   │ Confidence score (0-100)   │   │
│  │ timeHorizon   │ string    │ required   │ "2-4 hours", "1-2 days"   │   │
│  │ actions       │ object[]  │ required   │ Recommended actions         │   │
│  │ onApply       │ function  │ undefined  │ Apply action callback      │   │
│  │ onDismiss     │ function  │ undefined  │ Dismiss callback          │   │
│  │ compact       │ boolean   │ false      │ Compact view              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  VISUAL: Confidence meter, severity indicator, action buttons               │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6. ServiceDetailPanel

**Purpose:** Comprehensive service detail view

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  <ServiceDetailPanel />                                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│  PROPS:                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ prop          │ type      │ default    │ description               │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ service       │ object    │ required   │ Complete service data      │   │
│  │ activeTab     │ enum     │ overview   │ overview, metrics, alerts, │   │
│  │               │          │            │ settings                   │   │
│  │ onRestart     │ function  │ undefined  │ Restart service callback  │   │
│  │ onScale       │ function  │ undefined  │ Scale service callback     │   │
│  │ onConfigure   │ function  │ undefined  │ Configure callback        │   │
│  │ showAI        │ boolean   │ true       │ Show AI insights           │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  TABS: Overview (metadata, status), Metrics (charts), Alerts (history),   │
│        Settings (config, actions)                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 7. PerformanceOverview

**Purpose:** Aggregate platform performance metrics

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  <PerformanceOverview />                                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│  PROPS:                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ prop          │ type      │ default    │ description               │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ metrics       │ object    │ required   │ Aggregate platform data    │   │
│  │ timeRange     │ enum     │ 24h       │ 24h, 7d, 30d              │   │
│  │ breakdown     │ boolean   │ true       │ Show by category           │   │
│  │ onDrillDown   │ function  │ undefined  │ Drill-down callback       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  METRICS: Avg CPU, Avg Memory, Avg Response, Error Rate, Total Requests   │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 8. AlertManagementPanel

**Purpose:** Alert list with filtering and actions

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  <AlertManagementPanel />                                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│  PROPS:                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ prop          │ type      │ default    │ description               │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ alerts        │ object[]  │ required   │ Alert list                 │   │
│  │ filter        │ object    │ undefined  │ {severity, status, service}│ │
│  │ onAcknowledge │ function  │ required   │ Acknowledge callback       │   │
│  │ onResolve     │ function  │ required   │ Resolve callback           │   │
│  │ onEscalate    │ function  │ undefined  │ Escalate callback          │   │
│  │ autoRefresh   │ boolean   │ true       │ Auto-refresh alerts         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  FEATURES: Sort by severity, filter by service, bulk actions, export           │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 9. AIRootCauseAnalysis

**Purpose:** Display AI-generated root cause analysis

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  <AIRootCauseAnalysis />                                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│  PROPS:                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ prop          │ type      │ default    │ description               │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ analysis      │ object    │ required   │ AI analysis data           │   │
│  │ confidence    │ number    │ required   │ Confidence score (0-100)   │   │
│  │ rootCauses    │ object[]  │ required   │ Identified root causes     │   │
│  │ recommendations│ object[]  │ required   │ Recommended fixes          │   │
│  │ onApply       │ function  │ undefined  │ Apply fix callback         │   │
│  │ onDismiss     │ function  │ undefined  │ Dismiss callback          │   │
│  │ showDetails   │ boolean   │ true       │ Show detailed analysis      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  VISUAL: Confidence meter, cause prioritization, one-click fix actions      │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 10. WebSocketIndicator

**Purpose:** Display real-time connection status

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  <WebSocketIndicator />                                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│  PROPS:                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ prop          │ type      │ default    │ description               │   │
│  ├─────────────────────────────────────────────────────────────────────┤   │
│  │ connected     │ boolean   │ required   │ WebSocket connection status│   │
│  │ latency       │ number    │ undefined  │ Latency in ms             │   │
│  │ lastUpdate    │ date      │ required   │ Last update timestamp     │   │
│  │ onReconnect   │ function  │ undefined  │ Reconnect callback        │   │
│  │ showLatency   │ boolean   │ true       │ Show latency indicator     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  STATUS: Connected (Green pulsing), Disconnected (Red), Latency (ms)         │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Component Dependencies

```
Foundation Layer:
- Design Tokens (dashboard-blue, status colors, metric colors)
- Status Indicators (operational, degraded, down, unknown)
- Metric Formatters (percentage, milliseconds, bytes)

Base Components:
- Card (extends to ServiceStatusCard, AlertCard)
- Badge (extends to StatusBadge, SeverityBadge)
- Chart (extends to MetricChart)
- Table (extends to ServiceTable)
- Modal (extends to ServiceDetailModal)

AI Components:
- AIPredictionCard
- AIRootCauseAnalysis
- AIAnomalyDetector
- AIModelPerformanceChart

Composite Components:
- DashboardOverview (uses all above)
- ServicesPage (uses ServiceGrid, filters)
- AnalyticsPage (uses MetricChart, PerformanceOverview)
- AlertsPage (uses AlertManagementPanel)
- ServiceDetailPage (uses ServiceDetailPanel, AIRootCauseAnalysis)
```

---

## Responsive Breakpoints

```css
/* Dashboard Responsive Behavior */
ServiceGrid: {
  mobile: 1 column,
  tablet: 2 columns,
  desktop: 3 columns,
  large: 4 columns
}

ServiceStatusCard: {
  mobile: 100% width,
  tablet: 50% width,
  desktop: 33.33% width
}

AlertManagementPanel: {
  mobile: 100% width,
  tablet: 100% width,
  desktop: 50% width
}
```

---

**Document Status:** Component Library - Complete
**Owner:** Design System Team
