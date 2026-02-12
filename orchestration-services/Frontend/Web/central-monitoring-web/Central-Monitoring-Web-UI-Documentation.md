# Central Monitoring Web Dashboard - UI Design Documentation

**Version:** 1.0
**Last Updated:** January 10, 2026
**Product:** Gogidix Road Assist Services
**Domain:** Central Monitoring
**Platform:** Web Dashboard (React/TypeScript)

---

## Table of Contents

1. [Overview](#overview)
2. [User Personas & Access Levels](#user-personas--access-levels)
3. [Dashboard Architecture](#dashboard-architecture)
4. [Navigation Structure](#navigation-structure)
5. [Page Specifications](#page-specifications)
6. [Component Library](#component-library)
7. [Real-Time Features](#real-time-features)
8. [Multi-Country Operations](#multi-country-operations)
9. [Insurance Claim Integration](#insurance-claim-integration)
10. [Alerting System](#alerting-system)
11. [Design System](#design-system)
12. [Accessibility](#accessibility)

---

## Overview

The Central Monitoring Web Dashboard serves as the **command and control center** for Gogidix Road Assist operations. It provides real-time visibility into all service activities across the ecosystem, supporting operations across multiple countries and regions.

### Key Capabilities

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    CENTRAL MONITORING DASHBOARD                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐        │
│  │   LIVE          │  │   PARTNER       │  │   SERVICE       │        │
│  │   OPERATIONS    │  │   MONITORING    │  │   COORDINATION  │        │
│  │                 │  │                 │  │                 │        │
│  │ • Real-time Map │  │ • Towing Cos    │  │ • Auto Dispatch │        │
│  │ • User Tracking │  │ • Mechanics     │  │ • Manual Assign │        │
│  │ • GPS Updates   │  │ • Independent   │  │ • Queue Mgmt    │        │
│  │ • Status Monitor│  │ • Availability  │  │ • ETA Tracking  │        │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘        │
│                                                                         │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐        │
│  │   INSURANCE     │  │   ALERTING      │  │   REPORTING     │        │
│  │   CLAIMS        │  │                 │  │                 │        │
│  │                 │  │ • SLA Breaches  │  │ • Performance   │        │
│  │ • Claim Status  │  │ • Emergencies   │  │ • Utilization   │        │
│  │ • Approval      │  │ • Delays        │  │ • Incident      │        │
│  │ • Verification  │  │ • System Issues │  │ • Historical    │        │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘        │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## User Personas & Access Levels

### 1. Operations Manager (Full Access)

| Attribute | Details |
|-----------|---------|
| **Role** | Senior Operations Controller |
| **Access** | All dashboard features, full dispatch control |
| **Devices** | Desktop (primary), Large Monitor Displays |
| **Key Needs** | Real-time overview, quick incident response, partner coordination |
| **Session Timeout** | 4 hours |

### 2. Duty Supervisor (Standard Access)

| Attribute | Details |
|-----------|---------|
| **Role** | Shift Supervisor |
| **Access** | Monitoring, dispatch, reporting (no settings) |
| **Devices** | Desktop, Tablet |
| **Key Needs** | Team coordination, escalation handling, performance monitoring |
| **Session Timeout** | 2 hours |

### 3. Incident Response Specialist (Focused Access)

| Attribute | Details |
|-----------|---------|
| **Role** | Emergency Coordinator |
| **Access** | Live map, alerts, dispatch (emergency mode) |
| **Devices** | Desktop, Mobile (on-call) |
| **Key Needs** | Quick response, partner activation, user safety |
| **Session Timeout** | 1 hour |

### Access Control Matrix

```
┌────────────────────────────────────────────────────────────────────────────┐
│                           ACCESS CONTROL MATRIX                            │
├────────────────────────────────────────────────────────────────────────────┤
│ Feature                    │ Ops Mgr │ Supervisor │ Specialist │ Read-Only │
├────────────────────────────┼─────────┼────────────┼────────────┼───────────┤
│ Live Dashboard             │    ✅   │     ✅     │     ✅     │    ✅     │
│ Real-time Map              │    ✅   │     ✅     │     ✅     │    ✅     │
│ Partner Monitoring         │    ✅   │     ✅     │     ✅     │    ✅     │
│ Service Requests           │    ✅   │     ✅     │     ✅     │    ✅     │
│ Manual Dispatch            │    ✅   │     ✅     │     ✅     │    ❌     │
│ Auto-Assign Configuration  │    ✅   │     ❌     │     ❌     │    ❌     │
│ Insurance Claims           │    ✅   │     ✅     │     ✅     │    ✅     │
│ Claim Approval             │    ✅   │     ✅     │     ❌     │    ❌     │
│ Alert Management           │    ✅   │     ✅     │     ✅     │    ✅     │
│ Broadcast Messages         │    ✅   │     ✅     │     ❌     │    ❌     │
│ Reports & Analytics        │    ✅   │     ✅     │     ✅     │    ✅     │
│ System Settings            │    ✅   │     ❌     │     ❌     │    ❌     │
│ User Management            │    ✅   │     ❌     │     ❌     │    ❌     │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## Dashboard Architecture

### Application Structure

```
src/
├── App.tsx                          # Main application with routing
├── index.tsx                        # Application entry point
│
├── pages/
│   ├── Dashboard.tsx                # Main operations overview
│   ├── LiveMap.tsx                  # Real-time map view
│   ├── Partners.tsx                 # Partner monitoring
│   ├── Requests.tsx                 # Service request monitoring
│   ├── Dispatch.tsx                 # Dispatch coordination
│   ├── InsuranceClaims.tsx          # Insurance claim workflow
│   ├── Alerts.tsx                   # Alert management
│   ├── Reports.tsx                  # Performance reports
│   ├── Broadcasts.tsx               # Partner communication
│   ├── Settings.tsx                 # System configuration
│   └── Profile.tsx                  # User profile
│
├── components/
│   ├── layout/
│   │   ├── Sidebar.tsx              # Navigation sidebar
│   │   ├── Header.tsx               # Top header with alerts
│   │   └── MainLayout.tsx           # Layout wrapper
│   │
│   ├── dashboard/
│   │   ├── MetricsCards.tsx         # Key metrics widgets
│   │   ├── QuickStats.tsx           # Statistics overview
│   │   └── ActivityFeed.tsx         # Recent activity stream
│   │
│   ├── map/
│   │   ├── MapView.tsx              # Main map component
│   │   ├── EntityMarkers.tsx        # User/partner markers
│   │   ├── MapControls.tsx          # Zoom, filters, layers
│   │   ├── MapLegend.tsx            # Entity type legend
│   │   └── EntityInfoPanel.tsx      # Selected entity details
│   │
│   ├── partners/
│   │   ├── PartnerList.tsx          # Partner grid/list
│   │   ├── PartnerCard.tsx          # Individual partner card
│   │   ├── DriverStatus.tsx         # Driver availability
│   │   └── PartnerDetails.tsx       # Detailed partner view
│   │
│   ├── requests/
│   │   ├── RequestList.tsx          # Active requests table
│   │   ├── RequestCard.tsx          # Request summary card
│   │   ├── RequestDetails.tsx       # Full request details
│   │   ├── RequestFilters.tsx       # Filter controls
│   │   └── RequestTimeline.tsx      # Request status timeline
│   │
│   ├── dispatch/
│   │   ├── DispatchPanel.tsx        # Dispatch control panel
│   │   ├── PartnerSelector.tsx      # Available partners list
│   │   ├── AssignmentConfirmation.tsx
│   │   └── QueueManager.tsx         # Request queue
│   │
│   ├── insurance/
│   │   ├── ClaimList.tsx            # Claims needing review
│   │   ├── ClaimDetails.tsx         # Claim information
│   │   ├── ClaimApproval.tsx        # Approval workflow
│   │   └── ClaimVerification.tsx    # Verification status
│   │
│   ├── alerts/
│   │   ├── AlertPanel.tsx           # Active alerts sidebar
│   │   ├── AlertCard.tsx            # Individual alert
│   │   ├── AlertHistory.tsx         # Historical alerts
│   │   └── SLABreachBanner.tsx      # SLA breach warning
│   │
│   ├── broadcasts/
│   │   ├── BroadcastComposer.tsx    # Message composition
│   │   ├── RecipientSelector.tsx    # Target selection
│   │   ├── BroadcastHistory.tsx     # Sent messages
│   │   └── AcknowledgementTracker.tsx
│   │
│   └── common/
│       ├── Button.tsx
│       ├── Modal.tsx
│       ├── Table.tsx
│       ├── Badge.tsx
│       ├── StatusIndicator.tsx
│       ├── FilterBar.tsx
│       └── SearchInput.tsx
│
├── services/
│   ├── api.ts                       # API client configuration
│   ├── locationService.ts           # Location tracking API
│   ├── partnerService.ts            # Partner monitoring API
│   ├── requestService.ts            # Service request API
│   ├── dispatchService.ts           # Dispatch coordination API
│   ├── insuranceService.ts          # Insurance claim API
│   ├── alertService.ts              # Alerting API
│   ├── reportService.ts             # Reporting API
│   └── websocketService.ts          # Real-time WebSocket connection
│
├── hooks/
│   ├── useLocations.ts              # Real-time location updates
│   ├── usePartners.ts               # Partner data
│   ├── useRequests.ts               # Service request data
│   ├── useAlerts.ts                 # Alert notifications
│   ├── useWebSocket.ts              # WebSocket connection
│   └── useDebounce.ts               # Utility hook
│
├── contexts/
│   ├── AuthContext.tsx              # Authentication state
│   ├── LocationContext.tsx          # Real-time location state
│   ├── AlertContext.tsx             # Alert state
│   └── CountryContext.tsx           # Multi-country state
│
├── types/
│   ├── location.ts                  # Location types
│   ├── partner.ts                   # Partner types
│   ├── request.ts                   # Request types
│   ├── insurance.ts                 # Insurance types
│   ├── alert.ts                     # Alert types
│   └── user.ts                      # User types
│
└── utils/
    ├── formatters.ts                # Data formatting utilities
    ├── calculations.ts              # Distance, ETA calculations
    ├── constants.ts                 # App constants
    └── validators.ts                # Input validators
```

---

## Navigation Structure

### Sidebar Navigation

```
┌────────────────────────────────────┐
│         [Gogidix Logo]             │
├────────────────────────────────────┤
│                                    │
│  📊 Dashboard                      │
│    ├── Operations Overview         │
│    ├── Performance Metrics         │
│                                    │
│  🗺️ Live Map                       │
│    ├── Multi-Country View          │
│    ├── Regional View               │
│    ├── Traffic Overlay             │
│    └── Heatmap View                │
│                                    │
│  🚗 Partners                       │
│    ├── Towing Companies            │
│    ├── Mechanics                   │
│    ├── Independent Drivers         │
│    └── Availability Report         │
│                                    │
│  📋 Service Requests               │
│    ├── Active Requests             │
│    ├── Pending Assignment          │
│    ├── In Progress                 │
│    └── Request History             │
│                                    │
│  🚀 Dispatch                       │
│    ├── Auto-Assign Queue           │
│    ├── Manual Dispatch             │
│    └── Assignment History          │
│                                    │
│  🛡️ Insurance Claims               │
│    ├── Pending Approval            │
│    ├── Verified Claims             │
│    ├── Claim History               │
│    └── Verification Queue          │
│                                    │
│  🔔 Alerts                         │
│    ├── Active Alerts               │
│    ├── SLA Breaches                │
│    ├── System Alerts               │
│    └── Alert History               │
│                                    │
│  📢 Broadcasts                     │
│    ├── Compose Message             │
│    ├── Sent Broadcasts             │
│    └── Acknowledgements            │
│                                    │
│  📈 Reports                        │
│    ├── Performance Report          │
│    ├── Partner Utilization         │
│    ├── Response Time Analysis      │
│    └── Incident Reports            │
│                                    │
│  ⚙️ Settings                      │
│    ├── System Configuration        │
│    ├── Auto-Assign Rules           │
│    ├── Alert Thresholds            │
│    └── User Management             │
│                                    │
├────────────────────────────────────┤
│  [Operator Profile]                │
│  Country: 🇬🇧 United Kingdom       │
│  [Switch Country]                  │
│  [Logout]                          │
└────────────────────────────────────┘
```

### Header Elements

```
┌──────────────────────────────────────────────────────────────────────────────┐
│ [Logo] Central Monitoring    [🇬🇧 UK ▼]    [Search: Requests, Partners...] │
│                                                                              │
│ [Dashboard] [Map] [Partners] [Requests] [Dispatch] [Claims] [Alerts]       │
│                                                                              │
│ John Smith • Operations Manager    🔔 3 new alerts    📊 12 pending         │
│ ─────────────────────────────────────────────────────────────────────────   │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## Page Specifications

### 1. Dashboard (Main Operations Overview)

**Route:** `/dashboard`
**Purpose:** Primary command center view with real-time metrics

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                         OPERATIONS DASHBOARD                                 │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────────┐  │
│  │  KEY PERFORMANCE METRICS - Real-time                                   │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐    │  │
│  │  │   47     │ │   23     │ │   8      │ │  12min   │ │  94.2%   │    │  │
│  │  │ Active   │ │ Partners │ │ Waiting  │ │ Avg      │ │ SLA      │    │  │
│  │  │ Requests │ │ Available│ │ Users    │ │ Response │ │ Achieved │    │  │
│  │  │ ⬆️ +3    │ │ ⬇️ -2    │ │ ⬆️ +1    │ │ ⬇️ -1min │ │          │    │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘    │  │
│  └────────────────────────────────────────────────────────────────────────┘  │
│                                                                              │
│  ┌──────────────────────────────────────┐  ┌──────────────────────────────┐ │
│  │                                      │  │                              │ │
│  │        LIVE OPERATIONS MAP           │  │    ACTIVE REQUESTS QUEUE     │ │
│  │                                      │  │                              │ │
│  │   [Interactive Map Display]          │  │  ┌────────────────────────┐ │ │
│  │                                      │  │  │ 🔴 URGENT              │ │ │
│  │     🚗 47 Active Users               │  │  │ #REQ-5892 • Towing     │ │ │
│  │     🚙 23 Available Partners        │  │  │ M4 J8 • 8min waiting   │ │ │
│  │     ⚠️ 3 SLA Breaches               │  │  │ [Assign] [Details]     │ │ │
│  │                                      │  │  └────────────────────────┘ │ │
│  │   [Zoom] [Traffic] [Heatmap]        │  │                              │ │
│  │                                      │  │  ┌────────────────────────┐ │ │
│  │                                      │  │  │ 🟡 HIGH                │ │ │
│  │                                      │  │  │ #REQ-5891 • Lockout    │ │ │
│  │                                      │  │  │ Downtown • 5min        │ │ │
│  │                                      │  │  │ [Assign] [Details]     │ │ │
│  │                                      │  │  └────────────────────────┘ │ │
│  └──────────────────────────────────────┘  └──────────────────────────────┘ │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────────┐  │
│  │  PARTNER AVAILABILITY SUMMARY                                          │  │
│  │  ┌────────────────────┐ ┌────────────────────┐ ┌────────────────────┐  │  │
│  │  │ 🚛 TOWING          │ │ �� MECHANICS        │ │ 🚗 INDEPENDENT     │  │  │
│  │  │                    │ │                    │ │                    │  │  │
│  │  │ 12 Companies       │ │ 8 Workshops        │ │ 5 Drivers          │  │  │
│  │  │ 45 Total Drivers   │ │ 15 Total Mechanics │ │                    │  │  │
│  │  │ 18 Available       │ │ 6 Available        │ │ 5 Available        │  │  │
│  │  │ 27 On Job          │ │ 9 On Job           │ │ 0 On Job           │  │  │
│  │  │                    │ │                    │ │                    │  │  │
│  │  │ Avg: 12min ETA     │ │ Avg: 18min ETA     │ │ Avg: 15min ETA     │  │  │
│  │  │ [View Details]     │ │ [View Details]     │ │ [View Details]     │  │  │
│  │  └────────────────────┘ └────────────────────┘ └────────────────────┘  │  │
│  └────────────────────────────────────────────────────────────────────────┘  │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────────┐  │
│  │  ACTIVITY FEED - Last 10 minutes                                        │  │
│  │  ┌──────────────────────────────────────────────────────────────────┐  │  │
│  │  │ 14:32  Highway Heroes assigned to #REQ-5890                      │  │  │
│  │  │ 14:31  New request: #REQ-5892 - Engine failure - M4 J8          │  │  │
│  │  │ 14:30  Partner broadcast sent: Downtown high demand             │  │  │
│  │  │ 14:28  #REQ-5888 marked complete by Express Towing              │  │  │
│  │  │ 14:25  SLA breach alert: #REQ-5885 exceeded 30min wait          │  │  │
│  │  └──────────────────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────────────────────────────────────────────┘  │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 2. Live Map Page

**Route:** `/map`
**Purpose:** Real-time geographic view of all entities

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                            LIVE MAP VIEW                                     │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Country: [🇬🇧 United Kingdom ▼]    Region: [All Regions ▼]                 │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                                                                      │   │
│  │                                                                      │   │
│  │     🗺️                   INTERACTIVE MAP                            │   │
│  │                                                                      │   │
│  │                      🚙 🚗 🚗 🚙 🚙                                │   │
│  │                                                                      │   │
│  │                  🚗              🚙                                  │   │
│  │                                                                      │   │
│  │              🚙       🚙       🚙       🚗                           │   │
│  │                                                                      │   │
│  │                                                                      │   │
│  │                   🚗                    🚙                           │   │
│  │                                                                      │   │
│  │                                                                      │   │
│  │                        🚗        🚙                                  │   │
│  │                                                                      │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  MAP CONTROLS                                                        │   │
│  │  [🔍+] [🔍-] [📍 My Location] [🔄 Refresh]                           │   │
│  │                                                                      │   │
│  │  LAYERS:                                                              │   │
│  │  [✅] Users Waiting            [ ] Users In Service                  │   │
│  │  [✅] Available Partners       [ ] Partners On Job                   │   │
│  │  [ ] Traffic Conditions        [ ] Service Area Boundaries           │   │
│  │  [ ] Heatmap                   [ ] Weather Overlay                  │   │
│  │                                                                      │   │
│  │  FILTERS:                                                             │   │
│  │  Partner Type: [All ▼]                                               │   │
│  │  Service Type: [All ▼]                                               │   │
│  │  Priority: [All ▼]                                                   │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  MAP LEGEND                                                          │   │
│  │  🚗 User - Waiting for assistance                                     │   │
│  │  🚙 User - Service in progress                                        │   │
│  │  🚛 Towing Company - Available                                        │   │
│  │  🔧 Mechanic - Available                                             │   │
│  │  🚐 Independent Driver - Available                                    │   │
│  │  ⚠️ SLA Breach / Emergency                                           │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  SELECTED ENTITY                                                     │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │ User #1234 - Individual                                         │  │   │
│  │  ├────────────────────────────────────────────────────────────────┤  │   │
│  │  │ Status:         Waiting for assignment                         │  │   │
│  │  │ Location:       M4 Highway, Junction 8                         │  │   │
│  │  │ Service:        Towing - Engine Failure                         │  │   │
│  │  │ Request Time:   14:25 (8 min ago)                              │  │   │
│  │  │ Priority:       HIGH                                            │  │   │
│  │  │ Insurance:      Pending verification                           │  │   │
│  │  │                                                                │  │   │
│  │  │ [View Full Details] [Assign Partner] [Escalate]                │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 3. Partners Monitoring Page

**Route:** `/partners`
**Purpose:** Monitor all partner fleets and availability

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                          PARTNER MONITORING                                  │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Partner Type: [All ▼]    Status: [All ▼]    Region: [All ▼]                │
│  Search: [________________]                                                 │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  PARTNER OVERVIEW                                                    │   │
│  │  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐        │   │
│  │  │  🚛 TOWING      │ │  🔧 MECHANICS   │ │  🚗 INDEPENDENT │        │   │
│  │  │                 │ │                 │ │                 │        │   │
│  │  │ 12 Companies    │ │ 8 Workshops     │ │ 5 Drivers       │        │   │
│  │  │ 45 Drivers      │ │ 15 Mechanics    │ │                 │        │   │
│  │  │ 18 Available    │ │ 6 Available     │ │ 5 Available     │        │   │
│  │  │ 27 On Job       │ │ 9 On Job        │ │ 0 On Job        │        │   │
│  │  │                 │ │                 │ │                 │        │   │
│  │  │ Avg: 12min      │ │ Avg: 18min      │ │ Avg: 15min      │        │   │
│  │  │ [View All]      │ │ [View All]      │ │ [View All]      │        │   │
│  │  └─────────────────┘ └─────────────────┘ └─────────────────┘        │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  TOWING COMPANIES                                                    │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │ Company           │ Total │ On Job │ Avail │ Rating │ Avg    │  │   │
│  │  │                   │ Drivers│        │       │        │ Response│  │   │
│  │  ├───────────────────┼────────┼────────┼────────┼────────┼────────┤  │   │
│  │  │ Highway Heroes   │   20   │   14   │   6    │  4.9⭐ │  10min │  │   │
│  │  │ [View] [Message] │        │        │        │        │        │  │   │
│  │  ├───────────────────┼────────┼────────┼────────┼────────┼────────┤  │   │
│  │  │ Express Towing   │   12   │   8    │   4    │  4.7⭐ │  12min │  │   │
│  │  │ [View] [Message] │        │        │        │        │        │  │   │
│  │  ├───────────────────┼────────┼────────┼────────┼────────┼────────┤  │   │
│  │  │ QuickTow Services│    8   │   5    │   3    │  4.5⭐ │  15min │  │   │
│  │  │ [View] [Message] │        │        │        │        │        │  │   │
│  │  ├───────────────────┼────────┼────────┼────────┼────────┼────────┤  │   │
│  │  │ City Roadside    │   15   │   12   │   3    │  4.3⭐ │  18min │  │   │
│  │  │ [View] [Message] │        │        │        │        │        │  │   │
│  │  └───────────────────┴────────┴────────┴────────┴────────┴────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  MECHANIC WORKSHOPS                                                  │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │ Workshop           │ Mobile │ On Job │ Avail │ Rating │ Avg    │  │   │
│  │  │                   │ Staff  │        │       │        │ Response│  │   │
│  │  ├───────────────────┼────────┼────────┼────────┼────────┼────────┤  │   │
│  │  │ Roadside Repairs  │    8   │   6    │   2    │  4.9⭐ │  15min │  │   │
│  │  │ [View] [Message]  │        │        │        │        │        │  │   │
│  │  ├───────────────────┼────────┼────────┼────────┼────────┼────────┤  │   │
│  │  │ AutoFix Centre    │    5   │   3    │   2    │  4.8⭐ │  18min │  │   │
│  │  │ [View] [Message]  │        │        │        │        │        │  │   │
│  │  ├───────────────────┼────────┼────────┼────────┼────────┼────────┤  │   │
│  │  │ Quick Mechanics   │    3   │   2    │   1    │  4.5⭐ │  20min │  │   │
│  │  │ [View] [Message]  │        │        │        │        │        │  │   │
│  │  └───────────────────┴────────┴────────┴────────┴────────┴────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Partner Detail Modal

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  Highway Heroes - Partner Details                                    [X]     │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Company Information                                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Partner ID:        TH-001                                             │   │
│  │ Company Type:      Towing Company                                     │   │
│  │ Region:            Greater London                                     │   │
│  │ Status:            ✅ Active                                           │   │
│  │ Rating:            4.9/5.0 (127 reviews)                              │   │
│  │ Total Drivers:     20                                                 │   │
│  │ Active Since:      Jan 2023                                           │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Current Status                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Available:    6 drivers                                               │   │
│  │ On Job:       14 drivers                                              │   │
│  │ Offline:      0 drivers                                               │   │
│  │                                                                      │   │
│  │ ████████████████░░░░░░░░ 70% Utilization                             │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Active Drivers                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Driver          │ Status    │ Location        │ Job      │ ETA     │   │
│  ├─────────────────┼───────────┼─────────────────┼──────────┼─────────│   │
│  │ David M. (HH-01)│ On Job    │ M4 J8           │ #REQ-123 │ 5min    │   │
│  │ Tom K. (HH-02)  │ On Job    │ Downtown        │ #REQ-124 │ 2min    │   │
│  │ Sarah L. (HH-03)│ Available │ North District  │ -        │ -       │   │
│  │ Mike R. (HH-04) │ On Job    │ West Side       │ #REQ-125 │ 8min    │   │
│  │ ... (view all 20)                                                      │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Performance This Month                                                     │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Total Jobs:         347                                               │   │
│  │ Completed:          342 (98.6%)                                       │   │
│  │ Cancelled:          5 (1.4%)                                          │   │
│  │ Avg Response Time:  10.2 minutes                                      │   │
│  │ SLA Achievement:    97.5%                                             │   │
│  │ Customer Rating:    4.8/5.0                                           │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  [Send Broadcast] [View History] [Edit Partner] [Suspend Partner]            │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 4. Service Requests Page

**Route:** `/requests`
**Purpose:** Monitor and manage all service requests

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                        SERVICE REQUEST MONITORING                            │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  REQUEST SUMMARY                                                     │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐    │   │
│  │  │   47     │ │   12     │ │   23     │ │    8     │ │    4     │    │   │
│  │  │ Total    │ │ Pending  │ │ Assigned │ │ On Route │ │ On Site  │    │   │
│  │  │ Active   │ │ Assign   │ │         │ │         │ │         │    │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘    │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Filters:                                                                     │
│  Status: [All ▼]  Service Type: [All ▼]  Priority: [All ▼]                 │
│  User Type: [All ▼]  Country: [All ▼]  Time Range: [Today ▼]                │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  ACTIVE REQUESTS                                                     │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │ ID       │ User │Service │Location │Partner │Status  │Priority│  │   │
│  │  ├──────────┼──────┼─────────┼─────────┼────────┼────────┼────────┤  │   │
│  │  │#REQ-5892 │ John │ Towing  │ M4 J8   │Searchin│ Pending│  URGENT│  │   │
│  │  │          │ Doe  │         │         │   g    │        │        │  │   │
│  │  │[View]    │      │         │         │        │[Assign]│        │  │   │
│  │  ├──────────┼──────┼─────────┼─────────┼────────┼────────┼────────┤  │   │
│  │  │#REQ-5891 │ Mary │ Lockout │ Downtown│Express │ En     │  HIGH  │  │   │
│  │  │          │ Smith│         │ Mall    │ Towing │ Route  │        │  │   │
│  │  │[View]    │      │         │         │        │        │  3min  │  │   │
│  │  ├──────────┼──────┼─────────┼─────────┼────────┼────────┼────────┤  │   │
│  │  │#REQ-5890 │ Fleet│ Tire    │ A1 Rd   │Highway │ On     │  NORMAL│  │   │
│  │  │          │ Corp │ Change  │ Jn15    │ Heroes │ Site   │        │  │   │
│  │  │[View]    │      │         │         │        │        │  2min  │  │   │
│  │  ├──────────┼──────┼─────────┼─────────┼────────┼────────┼────────┤  │   │
│  │  │#REQ-5889 │ Sara │ Fuel    │ M25     │QuickTow│Progress│  NORMAL│  │   │
│  │  │          │ Jones│ Delivery│ Jn10    │        │        │        │  │   │
│  │  │[View]    │      │         │         │        │        │  5min  │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Service Type Distribution                                                   │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Towing         ████████████████ 28                                    │   │
│  │ Tire Change    ████████ 8                                            │   │
│  │ Lockout        ██████ 6                                              │   │
│  │ Fuel Delivery  ████ 4                                                │   │
│  │ Jump-start     ████ 4                                                │   │
│  │ Mechanic       ██████████ 10                                          │   │
│  │ Other          ██ 2                                                  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Request Detail Panel

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  Request #REQ-5892 - Details                                          [X]     │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Request Information                                                         │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Request ID:        #REQ-5892                                          │   │
│  │ Created:           14:25:32 (8 minutes ago)                           │   │
│  │ Status:            🔴 Pending Assignment                              │   │
│  │ Priority:          URGENT                                            │   │
│  │ Service Type:      Towing                                            │   │
│  │ Sub-type:          Engine Failure                                    │   │
│  │ Vehicle:           2022 Ford Fiesta - BG23 XYZ                        │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  User Information                                                            │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ User Type:          Individual                                        │   │
│  │ Name:               John Doe                                          │   │
│  │ Phone:              +44 7700 900123                                   │   │
│  │ Member Since:       March 2024                                        │   │
│  │ Total Requests:     3                                                │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Location Information                                                        │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Location:           M4 Motorway, Junction 8                           │   │
│  │ Coordinates:        51.5123° N, 2.1845° W                             │   │
│  │ GPS Accuracy:       5 meters                                          │   │
│  │ Landmark:           Near services, eastbound                          │   │
│  │ Weather:            🌧️ Light rain, 12°C                               │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Insurance Information                                                       │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Coverage:          ✅ Verified                                        │   │
│  │ Provider:          AXA Insurance                                     │   │
│  │ Policy:            POL-AXA-456789                                    │   │
│  │ Claim Status:      ✅ Pre-approved for towing                        │   │
│  │ Claim Reference:   CLM-2024-01234                                    │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Timeline                                                                    │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ 14:25  Request created by user via mobile app                         │   │
│  │ 14:25  Location captured: M4 J8 (accuracy: 5m)                        │   │
│  │ 14:26  Insurance verification initiated                               │   │
│  │ 14:27  Insurance verified: Pre-approved for towing                    │   │
│  │ 14:28  Request queued for dispatch                                     │   │
│  │       ⏳ Awaiting partner assignment...                                │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Available Partners (Ranked by proximity)                                    │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ #1 Highway Heroes - Driver HH-07    • 2.3 miles  • ETA 8min          │   │
│  │    [ASSIGN]                                                         │   │
│  │                                                                      │   │
│  │ #2 Express Towing - Driver ET-12    • 3.1 miles  • ETA 12min         │   │
│  │    [ASSIGN]                                                         │   │
│  │                                                                      │   │
│  │ #3 Independent - John D.            • 4.5 miles  • ETA 15min         │   │
│  │    [ASSIGN]                                                         │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  [Assign Partner] [Escalate] [Contact User] [View on Map]                    │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 5. Dispatch Coordination Page

**Route:** `/dispatch`
**Purpose:** Manual and automatic dispatch management

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                         DISPATCH COORDINATION                                │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  DISPATCH MODE                                                       │   │
│  │  ⚪ Auto-Assign (AI-powered)    ⚪ Manual Review    ⚫ Emergency     │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌─────────────────────────────────────────────┐  ┌──────────────────────┐ │
│  │  PENDING ASSIGNMENT QUEUE                   │  │  AUTO-ASSIGN CONFIG  │ │
│  │  ┌───────────────────────────────────────┐  │  │                      │ │
│  │  │ 🔴 #REQ-5892 - URGENT                  │  │  │ Auto-Assign:  ✅ ON │ │
│  │  │    M4 J8 • Towing • 8min waiting      │  │  │                      │ │
│  │  │    [Assign Now] [Hold]                │  │  │ Priority:           │ │
│  │  ├───────────────────────────────────────┤  │  │  ☑️ Emergency        │ │
│  │  │ 🟡 #REQ-5891 - HIGH                    │  │  │  ☑️ High             │ │
│  │  │    Downtown • Lockout • 5min          │  │  │  ☑️ Normal           │ │
│  │  │    [Assign Now] [Hold]                │  │  │  ☐ Low               │ │
│  │  ├───────────────────────────────────────┤  │  │                      │ │
│  │  │ 🟢 #REQ-5890 - NORMAL                 │  │  │ Max Distance:       │ │
│  │  │    A1 Rd • Tire Change • 2min        │  │  │  [10] miles         │ │
│  │  │    [Assign Now] [Hold]                │  │  │                      │ │
│  │  └───────────────────────────────────────┘  │  │ Max ETA:            │ │
│  └─────────────────────────────────────────────┘  │  [20] minutes        │ │
│                                                     │                      │ │
│                                                     │  Partner Rotation:   │ │
│  ┌─────────────────────────────────────────────┐  │  ☑️ Enable           │ │
│  │  ACTIVE ASSIGNMENTS                         │  │  └───────────────────┘ │
│  │  ┌───────────────────────────────────────┐  │                          │
│  │  │ #REQ-5889 → Highway Heroes (HH-03)    │  │                          │
│  │  │    ETA: 5min • Distance: 2.3mi       │  │                          │
│  │  │    [Reassign] [Track] [Contact]      │  │                          │
│  │  ├───────────────────────────────────────┤  │                          │
│  │  │ #REQ-5888 → Express Towing (ET-08)    │  │                          │
│  │  │    ETA: 12min • Distance: 3.8mi      │  │                          │
│  │  │    [Reassign] [Track] [Contact]      │  │                          │
│  │  └───────────────────────────────────────┘  │                          │
│  └─────────────────────────────────────────────┘                          │
│                                                                              │
│  Manual Dispatch Interface                                                   │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  ASSIGN PARTNER TO REQUEST                                           │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │ Request: #REQ-5892 - Towing - M4 J8                            │  │   │
│  │  ├────────────────────────────────────────────────────────────────┤  │   │
│  │  │                                                                │  │   │
│  │  │ Available Partners (by distance):                              │  │   │
│  │  │ ◉ Highway Heroes - Driver HH-07 • 2.3mi • ETA 8min            │  │   │
│  │  │ ○ Express Towing - Driver ET-12 • 3.1mi • ETA 12min           │  │   │
│  │  │ ○ QuickTow - Driver QT-05 • 4.2mi • ETA 14min                 │  │   │
│  │  │ ○ Independent - John D. • 4.5mi • ETA 15min                   │  │   │
│  │  │                                                                │  │   │
│  │  │ Assignment Notes:                                             │  │   │
│  │  │ ┌────────────────────────────────────────────────────────────┐│  │   │
│  │  │ │                                                            ││  │   │
│  │  │ └────────────────────────────────────────────────────────────┘│  │   │
│  │  │                                                                │  │   │
│  │  │ [CONFIRM ASSIGNMENT] [CANCEL]                                 │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 6. Insurance Claims Page

**Route:** `/insurance`
**Purpose:** Review and approve insurance claims before dispatch

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                       INSURANCE CLAIM WORKFLOW                               │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  CLAIMS SUMMARY                                                      │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐    │   │
│  │  │    8     │ │    5     │ │    2     │ │    1     │ │   100%   │    │   │
│  │  │ Pending  │ │ Verified │ │ Approved │ │ Rejected │ │ Auto-App │    │   │
│  │  │ Review   │ │          │ │          │ │          │  (This Mo)│    │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘    │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Filters:                                                                     │
│  Status: [All ▼]  Provider: [All ▼]  Country: [All ▼]  Urgency: [All ▼]     │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  CLAIMS REQUIRING REVIEW                                             │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │ Req ID   │User │Provider  │Policy    │Service │Status  │Action │  │   │
│  │  ├──────────┼─────┼───────────┼──────────┼────────┼────────┼────────┤  │   │
│  │  │#REQ-5892 │John │ AXA       │POL-456789│ Towing │Pending │[Review]│  │   │
│  │  │          │Doe  │           │          │        │        │        │  │   │
│  │  ├──────────┼─────┼───────────┼──────────┼────────┼────────┼────────┤  │   │
│  │  │#REQ-5891 │Mary │ Aviva     │POL-123456│ Lockout│Verified│[Approve]│  │   │
│  │  │          │Smith│           │          │        │        │        │  │   │
│  │  ├──────────┼─────┼───────────┼──────────┼────────┼────────┼────────┤  │   │
│  │  │#REQ-5890 │Fleet│ Zurich    │POL-COR-01│ Tire   │Verified│[Approve]│  │   │
│  │  │          │Corp │           │          │ Change │        │        │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Insurance Provider Distribution                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ AXA UK         ████████████ 32%                                       │   │
│  │ Aviva          ████████ 26%                                           │   │
│  │ Zurich         █████ 19%                                              │   │
│  │ Admiral        ███ 13%                                                │   │
│  │ Direct Line    ██ 10%                                                 │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Claim Review Modal

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  Claim Review - #REQ-5892                                              [X]   │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Claim Information                                                           │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Claim Reference:    CLM-2024-01234                                   │   │
│  │ Request ID:         #REQ-5892                                        │   │
│  │ Status:             Pending Review                                   │   │
│  │ Submitted:          14:26 (7 minutes ago)                            │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Policy Information                                                          │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Provider:           AXA Insurance UK                                 │   │
│  │ Policy Number:      POL-AXA-456789                                   │   │
│  │ Policy Type:        Comprehensive Roadside Assistance                │   │
│  │ Policy Status:      ✅ Active (expires: Dec 2025)                    │   │
│  │ Coverage Level:     Full Coverage (Towing, Lockout, Tire, Fuel)     │   │
│  │ Annual Limit:       £500 (used: £120, remaining: £380)               │   │
│  │ Claims This Year:   2 (max: 6)                                       │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Service Details                                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Service Type:       Towing                                           │   │
│  │ Incident:           Engine Failure - Vehicle not starting           │   │
│  │ Location:           M4 Motorway, Junction 8                          │   │
│  │ Vehicle:            2022 Ford Fiesta - BG23 XYZ                      │   │
│  │ Estimated Cost:     £150 (subject to actual service)                 │   │
│  │ Coverage:           ✅ Covered under policy                          │   │
│  │ Pre-approval:       ⏳ Awaiting review                                │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Verification Status                                                         │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Policy Status:          ✅ Verified - Active                         │   │
│  │ Coverage Check:         ✅ Confirmed - Service covered               │   │
│  │ Limit Check:            ✅ Within annual limit                       │   │
│  │ Driver Verification:    ✅ Policy holder confirmed                  │   │
│  │ Location Verification:  ✅ Matches GPS location                      │   │
│  │ Previous Claims:        ✅ Within allowed frequency                  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Risk Assessment                                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Fraud Risk:           LOW                                            │   │
│  │ Duplicate Claim:      None detected                                 │   │
│  │ Pattern Anomaly:      None detected                                 │   │
│  │ Recommendation:       ✅ APPROVE - All checks passed                 │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Decision Options                                                            │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ [✅ APPROVE CLAIM] - Dispatch can proceed                            │   │
│  │ [❌ REJECT CLAIM] - Service requires direct payment                  │   │
│  │ [🔍 REQUEST INFO] - Additional verification needed                   │   │
│  │ [⚠️ FLAG FOR REVIEW] - Escalate to supervisor                       │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Notes:                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ ┌──────────────────────────────────────────────────────────────────┐ │   │
│  │ │                                                                  │ │   │
│  │ └──────────────────────────────────────────────────────────────────┘ │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│                           [CANCEL] [SUBMIT DECISION]                         │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 7. Alerts Page

**Route:** `/alerts`
**Purpose:** Monitor and manage system alerts

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                           ALERT MANAGEMENT                                   │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  ALERT SUMMARY                                                       │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐    │   │
│  │  │   3      │ │   1      │ │   2      │ │   15     │ │    47     │    │   │
│  │  │ Active   │ │Critical  │ │ SLA      │ │ Today   │ │ This Week │    │   │
│  │  │ Alerts   │ │          │ │ Breaches │         │          │    │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘    │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Filter: [All Types ▼]  Severity: [All ▼]  Status: [Active ▼]               │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  ACTIVE ALERTS                                                       │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │ ⚠️ SLA BREACH - #REQ-5885                               2min ago│  │   │
│  │  │ Request exceeded 30-minute response threshold                  │  │   │
│  │  │ Location: M25 J10 • Service: Towing                            │  │   │
│  │  │ [View Request] [Escalate] [Acknowledge]                        │  │   │
│  │  ├────────────────────────────────────────────────────────────────┤  │   │
│  │  │ 🚨 CRITICAL - No partners available                    5min ago│  │   │
│  │  │ Zero available partners in North District                     │  │   │
│  │  │ 3 requests waiting for assignment                             │  │   │
│  │  │ [View Region] [Broadcast Partners] [Escalate]                 │  │   │
│  │  ├────────────────────────────────────────────────────────────────┤  │   │
│  │  │ 📉 PARTNER AVAILABILITY LOW - Express Towing          12min ago│  │   │
│  │  │ Only 2 drivers available (normal: 6+)                          │  │   │
│  │  │ Reason: 3 trucks out for maintenance                          │  │   │
│  │  │ [View Partner] [Send Message] [Acknowledge]                    │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Alert Configuration                                                          │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  SLA Alert Thresholds                                                 │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │ Response Time Warning:  [20] minutes                           │  │   │
│  │  │ Response Time Critical: [30] minutes                           │  │   │
│  │  │ On-site Time Warning:   [45] minutes                           │  │   │
│  │  │ Resolution Time Warning: [90] minutes                          │  │   │
│  │  │ [Save Thresholds]                                              │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 8. Broadcasts Page

**Route:** `/broadcasts`
**Purpose:** Send messages to partners

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                        PARTNER BROADCASTS                                    │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  COMPOSE BROADCAST                                                   │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │ Recipients:                                                     │  │   │
│  │  │ ◉ All Partners                                                 │  │   │
│  │  │ ○ Towing Companies Only                                        │  │   │
│  │  │ ○ Mechanics Only                                               │  │   │
│  │  │ ○ Independent Drivers Only                                     │  │   │
│  │  │ ○ Specific Partner/Company: [Select...]                       │  │   │
│  │  │ ○ Specific Region: [Select...]                                │  │   │
│  │  │                                                                │  │   │
│  │  │ Priority:                                                      │  │   │
│  │  │ ◉ Information  ○ Advisory  ○ Urgent  ○ Emergency              │  │   │
│  │  │                                                                │  │   │
│  │  │ Message:                                                       │  │   │
│  │  │ ┌────────────────────────────────────────────────────────────┐ │  │   │
│  │  │ │                                                            │ │  │   │
│  │  │ └────────────────────────────────────────────────────────────┘ │  │   │
│  │  │                                                                │  │   │
│  │  │ Character count: 0/500                                        │  │   │
│  │  │                                                                │  │   │
│  │  │ ☑️ Require acknowledgement                                     │  │   │
│  │  │ ☑️ Send push notification                                      │  │   │
│  │  │                                                                │  │   │
│  │  │              [PREVIEW] [SEND BROADCAST]                        │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  Recent Broadcasts                                                           │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │ 14:32  📢 High demand - Downtown area                  Priority: │  │   │
│  │  │        Advisory                                    [View Details]│  │   │
│  │  │        Recipients: All towing companies                        │  │   │
│  │  │        Acknowledged: 8/12                                      │  │   │
│  │  ├────────────────────────────────────────────────────────────────┤  │   │
│  │  │ 13:15  📢 Maintenance notice - West District           Priority: │  │   │
│  │  │        Information                                  [View Details]│  │   │
│  │  │        Recipients: All partners                               │  │   │
│  │  │        Acknowledged: 23/23                                    │  │   │
│  │  ├────────────────────────────────────────────────────────────────┤  │   │
│  │  │ 11:00  📢 System maintenance - Tonight 2AM              Priority: │  │   │
│  │  │        Advisory                                    [View Details]│  │   │
│  │  │        Recipients: All partners                               │  │   │
│  │  │        Acknowledged: 23/23                                    │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## Component Library

### Status Indicators

```typescript
// Status Badge Component
interface StatusBadgeProps {
  status: 'pending' | 'assigned' | 'en-route' | 'on-site' | 'completed' | 'cancelled';
  size?: 'small' | 'medium' | 'large';
}

// Priority Badge Component
interface PriorityBadgeProps {
  priority: 'low' | 'normal' | 'high' | 'urgent' | 'emergency';
}

// Availability Indicator
interface AvailabilityIndicatorProps {
  available: number;
  total: number;
  type: 'towing' | 'mechanic' | 'independent';
}
```

### Map Markers

```
USER MARKERS:
┌────────────────────────────────────┐
│            🚗                      │  User - Waiting
│        ╔═════════╗                 │
│        ║  8 min  ║                 │  Shows wait time
│        ╚═════════╝                 │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│            🚙                      │  User - In Service
│        ╔═════════╗                 │
│        ║ ETA 5min║                 │  Shows partner ETA
│        ╚═════════╝                 │
└────────────────────────────────────┘

PARTNER MARKERS:
┌────────────────────────────────────┐
│            🚛                      │  Towing Company - Available
│       ┌─────────┐                  │
│       │  AVAIL  │                  │  Green border
│       └─────────┘                  │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│            🚛                      │  Towing Company - On Job
│       ┌─────────┐                  │
│       │ ON JOB  │                  │  Yellow border
│       └─────────┘                  │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│            🔧                      │  Mechanic - Available
│       ┌─────────┐                  │
│       │  AVAIL  │                  │
│       └─────────┘                  │
└────────────────────────────────────┘

ALERT MARKERS:
┌────────────────────────────────────┐
│            ⚠️                      │  SLA Breach Warning
│       ╔══════════════╗             │  Red pulsing marker
│       ║  SLA BREACH  ║             │
│       ╚══════════════╝             │
└────────────────────────────────────┘
```

### Data Tables

```typescript
// Sortable Table Component
interface TableProps<T> {
  data: T[];
  columns: Column<T>[];
  sortable?: boolean;
  filterable?: boolean;
  onRowClick?: (item: T) => void;
  rowActions?: RowAction<T>[];
}

interface Column<T> {
  key: keyof T;
  label: string;
  sortable?: boolean;
  filterable?: boolean;
  render?: (value: any, item: T) => React.ReactNode;
}

interface RowAction<T> {
  label: string;
  icon?: string;
  onClick: (item: T) => void;
  variant?: 'primary' | 'secondary' | 'danger';
}
```

### Metrics Cards

```
┌────────────────────────────────────┐
│                                    │
│         47                         │
│     Active Requests                │
│         ⬆️ +3                      │
│     vs last hour                   │
│                                    │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│                                    │
│         12min                      │
│     Avg Response Time              │
│         ⬇️ -1min                   │
│     improvement                    │
│                                    │
└────────────────────────────────────┘
```

---

## Real-Time Features

### WebSocket Integration

```typescript
// WebSocket Event Types
type WebSocketEvent =
  | { type: 'location:update'; data: LocationUpdate }
  | { type: 'request:created'; data: ServiceRequest }
  | { type: 'request:updated'; data: ServiceRequest }
  | { type: 'partner:status:change'; data: PartnerStatus }
  | { type: 'alert:created'; data: Alert }
  | { type: 'sla:breach'; data: SLABreachEvent }
  | { type: 'assignment:created'; data: DispatchAssignment };

// Real-time Location Hook
function useRealTimeLocations() {
  const [locations, setLocations] = useState<Map<string, LocationUpdate>>(new Map());

  useEffect(() => {
    const ws = connectToWebSocket();

    ws.on('location:update', (update: LocationUpdate) => {
      setLocations(prev => new Map(prev).set(update.entityId, update));
    });

    return () => ws.disconnect();
  }, []);

  return locations;
}
```

### Update Frequencies

| Entity Type | Update Frequency | Data Points |
|-------------|-----------------|-------------|
| User Location | Every 30 seconds | Latitude, Longitude, Accuracy |
| Partner Driver | Every 10 seconds | Latitude, Longitude, Status, Heading |
| Service Request | Event-driven | Status changes, ETA updates |
| Partner Availability | Every 30 seconds | Available count, On-job count |
| Alert Notifications | Instant | New alerts, SLA breaches |

---

## Multi-Country Operations

### Country Selector

```
┌────────────────────────────────────┐
│  Country: [🇬🇧 United Kingdom ▼]   │
│                                    │
│  ┌──────────────────────────────┐  │
│  │ 🇬🇧 United Kingdom           │  │
│  │ 🇮🇪 Ireland                  │  │
│  │ 🇫🇷 France                   │  │
│  │ 🇩🇪 Germany                  │  │
│  │ 🇳🇱 Netherlands              │  │
│  │ 🇧🇪 Belgium                  │  │
│  │ 🇪🇸 Spain                    │  │
│  │ 🇮🇹 Italy                    │  │
│  └──────────────────────────────┘  │
└────────────────────────────────────┘
```

### Multi-Country Dashboard

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                       MULTI-COUNTRY OPERATIONS VIEW                           │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  View: [🌍 Global] [📊 Comparison] [🗺️ Map]                                  │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │  COUNTRY OVERVIEW                                                    │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │   │
│  │  │  🇬🇧 UK      │ │  🇮🇪 Ireland │ │  🇫🇷 France  │ │  🇩🇪 Germany │ │   │
│  │  │             │ │             │ │             │ │             │ │   │
│  │  │  23 Active  │ │   8 Active  │ │  15 Active  │ │  12 Active  │ │   │
│  │  │   5 Partners│ │   3 Partners│ │   6 Partners│ │   4 Partners│ │   │
│  │  │  11min Avg  │ │  14min Avg  │ │  13min Avg  │ │  16min Avg  │ │   │
│  │  │  96.5% SLA  │ │  94.2% SLA  │ │  95.1% SLA  │ │  93.8% SLA  │ │   │
│  │  │ [View]      │ │ [View]      │ │ [View]      │ │ [View]      │ │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘ │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                           GLOBAL MAP VIEW                             │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │                                                                │  │   │
│  │  │         🇮🇪           🇬🇧                    🇳🇱 🇧🇪          │  │   │
│  │  │        (8)           (23)                    (5)  (3)          │  │   │
│  │  │                                                                │  │   │
│  │  │                                                            🇩🇪  │   │
│  │  │           🇫🇷        (12)                                  (12) │   │
│  │  │          (15)                                                  │   │
│  │  │                                                                │  │   │
│  │  │                   🇪🇸                                          │   │
│  │  │                  (9)                                           │   │
│  │  │                                                                │  │   │
│  │  │        Click country to view detailed operations              │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Region-Specific Configuration

```typescript
interface CountryConfig {
  code: string;              // ISO country code (GB, IE, FR, etc.)
  name: string;
  flag: string;             // Emoji flag
  currency: string;
  language: string;
  emergencyNumber: string;
  defaultResponseTime: number;  // minutes
  slaThresholds: {
    warning: number;
    critical: number;
  };
  supportedProviders: string[];
}

const countryConfigs: Record<string, CountryConfig> = {
  GB: {
    code: 'GB',
    name: 'United Kingdom',
    flag: '🇬🇧',
    currency: 'GBP',
    language: 'en-GB',
    emergencyNumber: '999',
    defaultResponseTime: 30,
    slaThresholds: { warning: 20, critical: 30 },
    supportedProviders: ['AXA', 'Aviva', 'Zurich', 'Admiral', 'Direct Line']
  },
  IE: {
    code: 'IE',
    name: 'Ireland',
    flag: '🇮🇪',
    currency: 'EUR',
    language: 'en-IE',
    emergencyNumber: '112',
    defaultResponseTime: 35,
    slaThresholds: { warning: 25, critical: 35 },
    supportedProviders: ['AXA', 'Aviva', 'FBD', 'Allianz']
  },
  // ... more countries
};
```

---

## Insurance Claim Integration

### Claim Verification Flow

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                       INSURANCE CLAIM VERIFICATION FLOW                      │
└──────────────────────────────────────────────────────────────────────────────┘

1. REQUEST CREATED
       │
       ▼
2. USER PROVIDES INSURANCE DETAILS
       │
       ├─► Individual: Policy Number + Provider
       └─► Corporate: Fleet Policy Number
       │
       ▼
3. CENTRAL MONITORING RECEIVES REQUEST
       │
       ▼
4. AUTOMATIC VERIFICATION INITIATED
       │
       ├─► Check Policy Status (Active/Expired)
       ├─► Check Coverage (Service Type Covered)
       ├─► Check Limits (Annual/Per-incident)
       ├─► Check Driver/Policy Holder Match
       └─► Check Claim Frequency
       │
       ▼
5. VERIFICATION RESULT
       │
       ├─► ✅ FULLY VERIFIED → Auto-approve if < £100 and < 3 claims this year
       ├─► ⚠️ NEEDS REVIEW → Requires manual review
       └─► ❌ NOT COVERED → Reject claim, offer direct payment
       │
       ▼
6. DISPATCH DECISION
       │
       ├─► Approved → Proceed with dispatch
       └─► Rejected → Require user confirmation for direct payment
       │
       ▼
7. SERVICE COMPLETION
       │
       ▼
8. CLAIM SETTLEMENT
       │
       └─► Automatic claim filing with provider
```

### Claim Status Display

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  INSURANCE CLAIM STATUS PANEL                                               │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Claim ID: CLM-2024-01234                                              │   │
│  ├──────────────────────────────────────────────────────────────────────┤   │
│  │                                                                        │   │
│  │  Verification Progress:                                                │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │ Policy Status    ████████████████████  ✅ Verified              │  │   │
│  │  │ Coverage Check   ████████████████████  ✅ Confirmed             │  │   │
│  │  │ Limit Check      ████████████████████  ✅ Within Limits         │  │   │
│  │  │ Driver Match     ████████████████████  ✅ Confirmed             │  │   │
│  │  │ Location Check   ████████████████████  ✅ Verified              │  │   │
│  │  │ Frequency Check  ████████████████████  ✅ OK                    │  │   │
│  │  │ Final Decision   ████████████████████  ⏳ Pending               │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  │                                                                        │   │
│  │  Status: ⏳ Awaiting Manual Review                                     │   │
│  │                                                                        │   │
│  │  Recommendation: ✅ APPROVE - All checks passed successfully           │   │
│  │                                                                        │   │
│  │  [Review Details] [Approve] [Reject] [Request Info]                   │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## Alerting System

### Alert Types

| Alert Type | Severity | Trigger | Action Required |
|------------|----------|---------|-----------------|
| SLA Breach | Critical | Response time exceeded | Immediate escalation |
| No Partners Available | Critical | Zero partners in region | Broadcast to adjacent regions |
| Partner Offline | Warning | Partner unresponsive > 15min | Contact partner |
| High Request Volume | Warning | Requests > capacity threshold | Notify partners |
| System Degradation | Critical | API response > 5s | Technical team |
| Insurance Verification Failed | Warning | Policy not verified | Manual review required |

### Alert Display Priority

```
CRITICAL (🔴):
┌──────────────────────────────────────────────────────────────────────────────┐
│ 🔴 CRITICAL ALERT - SLA BREACH                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│ Request #REQ-5885 has exceeded 30-minute response threshold                  │
│ Location: M25 J10 • Waiting: 32 minutes                                      │
│ Action: Immediate escalation required                                        │
│ [ESCALATE NOW] [VIEW REQUEST] [ACKNOWLEDGE]                                 │
└──────────────────────────────────────────────────────────────────────────────┘

WARNING (⚠️):
┌──────────────────────────────────────────────────────────────────────────────┐
│ ⚠️ WARNING - Partner Availability Low                                        │
├──────────────────────────────────────────────────────────────────────────────┤
│ Express Towing: Only 2 drivers available (normal: 6+)                        │
│ Impact: Potential delays in West District                                    │
│ [VIEW PARTNER] [SEND BROADCAST] [ACKNOWLEDGE]                                │
└──────────────────────────────────────────────────────────────────────────────┘

INFO (ℹ️):
┌──────────────────────────────────────────────────────────────────────────────┐
│ ℹ️ INFO - High Demand Expected                                               │
├──────────────────────────────────────────────────────────────────────────────┤
│ Event at Wembley Stadium expected to increase demand in North District       │
│ Time: Today 18:00-22:00                                                       │
│ [VIEW REGION] [PREPARE BROADCAST] [DISMISS]                                   │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## Design System

### Color Palette

```css
/* Primary Colors */
--color-primary: #0052CC;      /* Gogidix Blue */
--color-primary-light: #0065FF;
--color-primary-dark: #0747A6;

/* Status Colors */
--color-success: #36B37E;      /* Green - Available, Complete */
--color-warning: #FFAB00;      /* Orange - Waiting, Review */
--color-danger: #FF5630;       /* Red - Urgent, Breach, Offline */
--color-info: #00B8D9;         /* Cyan - Information */

/* Neutral Colors */
--color-text-primary: #172B4D;
--color-text-secondary: #5E6C84;
--color-bg-primary: #FFFFFF;
--color-bg-secondary: #F4F5F7;
--color-bg-tertiary: #EBECF0;
--color-border: #DFE1E6;

/* Entity Colors */
--color-user-waiting: #FF5630;
--color-user-service: #36B37E;
--color-partner-available: #36B37E;
--color-partner-busy: #FFAB00;
--color-partner-offline: #6B778C;
```

### Typography

```css
/* Font Families */
--font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
--font-family-mono: 'SF Mono', Monaco, 'Cascadia Code', monospace;

/* Font Sizes */
--font-size-xs: 11px;
--font-size-sm: 12px;
--font-size-md: 14px;
--font-size-lg: 16px;
--font-size-xl: 20px;
--font-size-2xl: 24px;
--font-size-3xl: 32px;

/* Font Weights */
--font-weight-normal: 400;
--font-weight-medium: 500;
--font-weight-semibold: 600;
--font-weight-bold: 700;
```

### Spacing

```css
--space-1: 4px;
--space-2: 8px;
--space-3: 12px;
--space-4: 16px;
--space-5: 20px;
--space-6: 24px;
--space-8: 32px;
--space-10: 40px;
--space-12: 48px;
```

### Border Radius

```css
--radius-sm: 4px;
--radius-md: 6px;
--radius-lg: 8px;
--radius-xl: 12px;
--radius-full: 9999px;
```

### Shadows

```css
--shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.1);
--shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1);
--shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.1);
--shadow-xl: 0 20px 25px rgba(0, 0, 0, 0.1);
```

---

## Accessibility

### WCAG 2.1 Compliance

- **Level AA** compliance target
- Keyboard navigation for all interactive elements
- Screen reader support with ARIA labels
- Color contrast ratio > 4.5:1
- Focus indicators on all focusable elements
- Error messages with clear guidance

### Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl/Cmd + K` | Open search |
| `Ctrl/Cmd + D` | Go to Dashboard |
| `Ctrl/Cmd + M` | Go to Map |
| `Ctrl/Cmd + R` | Go to Requests |
| `Ctrl/Cmd + A` | Go to Alerts |
| `Escape` | Close modal/drawer |
| `Space` | Pause/Resume real-time updates |
| `F5` | Refresh data |

---

## Performance Requirements

### Load Targets

| Metric | Target | Maximum |
|--------|--------|----------|
| Initial Page Load | < 2s | < 3s |
| Dashboard Render | < 1s | < 2s |
| Map Tile Load | < 500ms | < 1s |
| API Response | < 200ms | < 500ms |
| WebSocket Latency | < 100ms | < 200ms |

### Optimization Strategies

- Code splitting by route
- Lazy loading for map components
- Virtual scrolling for large lists
- Debounced search inputs
- Connection pooling for API calls
- Incremental rendering for map markers

---

*Document Version: 1.0*
*Last Updated: January 10, 2026*
*Author: Central Monitoring Domain Team*
