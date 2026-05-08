# Central Monitoring Mobile Dashboard - UI Design Documentation

**Version:** 1.0
**Last Updated:** January 10, 2026
**Product:** Gogidix Road Assist Services
**Domain:** Central Monitoring
**Platform:** Mobile Dashboard (React Native / TypeScript)

---

## Table of Contents

1. [Overview](#overview)
2. [Mobile-First Design Philosophy](#mobile-first-design-philosophy)
3. [User Context & Use Cases](#user-context--use-cases)
4. [Navigation Architecture](#navigation-architecture)
5. [Screen Specifications](#screen-specifications)
6. [Component Library (Mobile)](#component-library-mobile)
7. [Gesture & Interaction Design](#gesture--interaction-design)
8. [Push Notification Design](#push-notification-design)
9. [Offline & Connectivity](#offline--connectivity)
10. [Accessibility (Mobile)](#accessibility-mobile)
11. [Responsive Design](#responsive-design)

---

## Overview

The Central Monitoring Mobile Dashboard enables **on-call operations specialists** to monitor and respond to incidents from anywhere. Unlike the web dashboard's comprehensive view, the mobile app focuses on **immediate action needs** with optimized interactions for one-handed use.

### Key Design Principles

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    MOBILE DESIGN PRINCIPLES                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  📱 THUMB-DRIVEN                                                        │
│  All critical actions within thumb reach (bottom 1/3 of screen)         │
│                                                                         │
│  ⚡ INSTANT GRATIFICATION                                                │
│  < 3 taps to any critical action, < 1 second to open app                │
│                                                                         │
│  👁️ GLANCEABLE                                                          │
│  Key info readable without unlocking (notification preview)             │
│                                                                         │
│  🎯 FOCUS MODE                                                          │
│  One task at a time - no complex multitasking                           │
│                                                                         │
│  🔔 INTERRUPT-AWARE                                                     │
│  Critical alerts break through, non-urgent wait                         │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Mobile-First Design Philosophy

### Core UX Principles

| Principle | Mobile Implementation | Rationale |
|-----------|----------------------|-----------|
| **Bottom-Up Layout** | Primary actions at bottom, navigation at bottom | Thumb reachability |
| **Single-Column Flow** | Vertical scrolling, no side-by-side content | Narrow screen width |
| **Progressive Disclosure** | Show essentials first, details on tap | Reduce cognitive load |
| **Large Touch Targets** | Minimum 44x44px (iOS), 48x48px (Android) | Accurate touch |
| **Swipe Gestures** | Quick actions without navigating | Efficient interaction |
| **Haptic Feedback** | Confirm critical actions | Non-visual feedback |

### Screen Real Estate Allocation

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    MOBILE SCREEN LAYOUT                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  🔙 BACK    [PAGE TITLE]                     [⋮] MORE           │   │
│  │  (20px)      (24px bold)                    (24px)           │   │
│  │  ←──────────────── TOP BAR (56px) ──────────────────→           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                                                                  │   │
│  │                                                                 │   │
│  │                     SCROLLABLE CONTENT                           │   │
│  │                   (Variable height)                              │   │
│  │                                                                 │   │
│  │                   Focus: ONE thing at a time                     │   │
│  │                                                                 │   │
│  │                                                                 │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  [PRIMARY ACTION] ──────────────── [SECONDARY]                  │   │
│  │  (48px tall)                                                   │   │
│  │  ←─────────── BOTTOM ACTION BAR (64px) ─────────────────→       │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  🏠  🗺️  🚗  🚙  🔔  👤                                          │   │
│  │  ←──────────── BOTTOM NAVIGATION (56px) ─────────────────→        │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## User Context & Use Cases

### Primary Use Cases

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    MOBILE USE CASE SCENARIOS                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  SCENARIO 1: ON-CALL EMERGENCY (Middle of night)                        │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Context: 2:30 AM, sleeping, phone on nightstand                  │   │
│  │ Trigger: Critical SLA breach alert                                │   │
│  │ Flow:                                                            │   │
│  │   1. Loud alarm + vibration wakes user                          │   │
│  │   2. Lock screen shows "SLA BREACH" preview                     │   │
│  │   3. Face ID/Fingerprint unlock                                 │   │
│  │   4. App opens directly to incident detail                      │   │
│  │   5. One tap to reassign to nearest partner                     │   │
│  │   6. Confirm with fingerprint                                   │   │
│  │ Total time: < 30 seconds from sleep to resolution               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  SCENARIO 2: COMMUTING MONITORING                                       │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Context: 08:15, on train to work, one-handed use                │   │
│  │ Trigger: User opens app to check status                         │   │
│  │ Flow:                                                            │   │
│  │   1. App opens to dashboard (cached data)                       │   │
│  │   2. Quick scan of 3 key metrics (cards at top)                 │   │
│  │   3. Scroll through active requests (5 items)                   │   │
│  │   4. Tap one urgent request for details                         │   │
│  │   5. Swipe right to assign partner                               │   │
│  │ Total time: 45 seconds for full status check                    │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  SCENARIO 3: IN-FIELD COORDINATION                                       │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Context: 14:00, at accident site, coordinating with partners    │   │
│  │ Trigger: Partner calls for guidance                              │   │
│  │ Flow:                                                            │   │
│  │   1. Quick switch to map view                                   │   │
│  │   2. Pinch to zoom to incident location                         │   │
│  │   3. Tap partner marker to see availability                     │   │
│  │   4. Tap "Call" button to dial partner directly                 │   │
│  │   5. Switch between app and call seamlessly                     │   │
│  │ Total time: Ongoing - app stays in background                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  SCENARIO 4: SHIFT HANDOFF                                             │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Context: 06:55, arriving at control center, handoff needed     │   │
│  │ Trigger: Opening app on office Wi-Fi                            │   │
│  │ Flow:                                                            │   │
│  │   1. App detects location change, suggests handoff            │   │
│  │   2. Show handoff summary (active requests, issues)            │   │
│  │   3. Add quick voice note for incoming team                    │   │
│  │   4. Tap "Complete Handoff"                                    │   │
│  │ Total time: 2 minutes                                           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Navigation Architecture

### Bottom Navigation Pattern

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    BOTTOM NAVIGATION BAR                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌───────────┬───────────┬───────────┬───────────┬───────────┐          │
│  │           │           │           │           │           │          │
│  │    🏠     │    🗺️     │    🚗     │    🔔     │    👤     │          │
│  │           │           │           │           │           │          │
│  │ Dashboard │    Map    │ Requests  │  Alerts   │  Profile  │          │
│  │           │           │           │           │           │          │
│  │  [Active  │           │           │    (3)    │           │          │
│  │   dot]    │           │           │  Badge    │           │          │
│  │           │           │           │           │           │          │
│  └───────────┴───────────┴───────────┴───────────┴───────────┘          │
│      56px height (safe area + 16px on iPhone X+)                       │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Navigation Stack Pattern

```
Mobile Navigation Flow:

App Launch
    │
    ├─► Onboarding (if first time)
    │    └─► Login
    │         └─► Dashboard (Main)
    │
    └─► Dashboard (Main)
         │
         ├─► [Map Tab] ──────► Map View
         │                        ├─► [Tap marker] ──► Partner Detail
         │                        └─► [Tap request] ──► Request Detail
         │
         ├─► [Requests Tab] ──► Requests List
         │                        ├─► [Tap request] ──► Request Detail
         │                        │                      ├─► [Assign] ──► Partner Select
         │                        │                      └─► [Contact] ──► Action Sheet
         │                        └─► [Filter] ────────► Filter Modal
         │
         ├─► [Alerts Tab] ────► Alerts List
         │                        ├─► [Tap alert] ────► Alert Detail
         │                        └─► [Acknowledge] ──► Confirmation
         │
         └─► [Profile Tab] ────► Profile Menu
                                  ├─► Settings
                                  ├─► Notifications
                                  ├─► Help
                                  └─► Logout
```

---

## Screen Specifications

### Screen 1: Dashboard (Home)

**Purpose:** Quick status overview with immediate access to critical items

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  ← Back                Dashboard                      Country: 🇬🇧         [⋮]  │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  ⚠️ CRITICAL ALERT: SLA Breach - #REQ-5872                [VIEW] │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│  (Only shows when active - auto-dismisses after action)                     │
│                                                                              │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐    │
│  │     47      │ │     12      │ │      8      │ │     15min    │    │
│  │  Active     │ │  Partners   │ │  Waiting    │ │   Avg Resp   │    │
│  │  Requests   │ │  Available  │ │   Users     │ │     Time     │    │
│  │  ━━━━━━━━   │ │  ━━━━━━━━   │ │  ━━━━━━━━   │ │  ━━━━━━━━    │    │
│  │  △ 3 from   │ │  ▽ 2 from    │ │  △ 1 from   │ │  ▽ 2min from  │    │
│  │  last hour  │ │  last hour  │ │  last hour  │ │  yesterday    │    │
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  🚨 URGENT REQUESTS                                               │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │ #REQ-5892 🔴        URGENT                         8min    │  │    │
│  │  │ Towing • M4 J8 • Engine Failure                              │  │    │
│  │  │ John D. • AXA Insurance • ⏱️ 8 min waiting                    │  │    │
│  │  │                                                    [→ Swipe] │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │ #REQ-5891 🟡        HIGH                           12min    │  │    │
│  │  │ Lockout • Downtown Mall                                      │  │    │
│  │  │ Mary S. • Verified • ⏱️ 12 min waiting                       │  │    │
│  │  │                                                    [→ Swipe] │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │ #REQ-5890 🟢        NORMAL                         5min     │  │    │
│  │  │ Tire Change • A1 Rd Jn15                                    │  │    │
│  │  │ Fleet Corp • Subscription • ⏱️ 5 min on site                │  │    │
│  │  │                                                    [→ Swipe] │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  │                                                                     │    │
│  │                             View all 47 requests →  │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  PARTNER AVAILABILITY                                               │    │
│  │  ┌──────────────────────┐  ┌──────────────────────┐              │    │
│  │  │     🚛 TOWING        │  │     🔧 MECHANICS     │              │    │
│  │  │                      │  │                      │              │    │
│  │  │     18 / 45         │  │      6 / 15         │              │    │
│  │  │   Available          │  │    Available        │              │    │
│  │  │                      │  │                      │              │    │
│  │  │  [View Map]         │  │   [View Map]        │              │    │
│  │  └──────────────────────┘  └──────────────────────┘              │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  📊 ACTIVITY (last hour)                                            │    │
│  │  • 14:32  Highway Heroes assigned to #REQ-5890                     │    │
│  │  • 14:31  New request: #REQ-5892 - Engine failure - M4 J8         │    │
│  │  • 14:28  #REQ-5888 marked complete by Express Towing              │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                     REFRESHED 14:35                                │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│  [📍 VIEW MAP]                   [🔄 REFRESH]                             │
└──────────────────────────────────────────────────────────────────────────────┘
```

**Swipe Actions on Request Cards:**
- **Swipe Right:** Quick assign to best available partner
- **Swipe Left:** More options menu
- **Long Press:** Preview details without opening

### Screen 2: Map View

**Purpose:** Geographic view of all entities with touch interaction

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  ← Back                Live Map                         Filter: [All ▼]    [⋮]  │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  🔍 Search location...                                [Clear]      │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                                                                      │    │
│  │                                                                      │    │
│  │         🗺️                   INTERACTIVE MAP                          │    │
│  │                                                                      │    │
│  │              🚗            🚙                   🚙                 │    │
│  │                                                                      │    │
│  │                  🚙        🚗        🚙                            │    │
│  │                                                                      │    │
│  │         🚛                   🚛                   🚛                 │    │
│  │                                                                      │    │
│  │                        🚗         🚙                               │    │
│  │                                                                      │    │
│  │   [Double-tap to zoom | Pinch to zoom | Drag to pan]               │    │
│  │                                                                      │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  LAYERS:                    FILTER:                                 │    │
│  │  ☑️ Users (47)               ☑️ All Types                            │    │
│  │  ☑️ Partners (23)            ☐ Towing Only                          │    │
│  │  ☑️ Available only           ☐ Mechanics Only                       │    │
│  │  ☐ Traffic                  ☐ Urgent Only                          │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  Selected: User #1234 - Waiting for assignment             [×]     │    │
│  │  Location: M4 J8 • Service: Towing • Waiting: 8min                 │    │
│  │                      [ASSIGN] [DETAILS]                             │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│  [📍 CENTER ON ME]              [📱 RE-CENTER MAP]                          │
└──────────────────────────────────────────────────────────────────────────────┘
```

**Map Interactions:**
- **Tap marker:** Show quick info popover
- **Double-tap marker:** Open detail screen
- **Long-press map:** Add note to location
- **Two-finger drag:** Tilt map (3D view)

### Screen 3: Request List

**Purpose:** Browse and manage all requests

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  ← Back                Requests                    Sort: [Urgency ▼]    [⋮]  │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  🔍 Search requests...                              [Filter]       │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  Filter chips: [All] [Urgent] [Unassigned] [In Progress] [Near Me]         │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  #REQ-5892 🔴                          URGENT               8min    │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Towing - Engine Failure                                           │    │
│  │  M4 Motorway, Junction 8 (eastbound)                               │    │
│  │  John Doe • Individual                                             │    │
│  │  ⏱️ Waiting 8 min    🛡️ AXA: Verified                              │    │
│  │                                                [→ Swipe to assign]│    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  #REQ-5891 🟡                          HIGH                12min    │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Lockout - Keys locked in car                                      │    │
│  │  Downtown Shopping Center, Level 2, Space B45                      │    │
│  │  Mary Smith • Individual                                           │    │
│  │  ⏱️ Waiting 12 min   🛡️ Aviva: Verified                            │    │
│  │                                                [→ Swipe to assign]│    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  #REQ-5890 🟢                         NORMAL                5min     │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Tire Change - Flat tire                                           │    │
│  │  A1 Road, Junction 15, Northbound                                 │    │
│  │  Fleet Corp • Corporate                                           │    │
│  │  🚙 On Site: David M. (Highway Heroes)  ⏱️ 5 min                  │    │
│  │                                                [→ Swipe details ]│    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  #REQ-5889 🟢                         NORMAL               15min    │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Fuel Delivery - Out of fuel                                       │    │
│  │  M25 Motorway, Junction 10                                        │    │
│  │  Sarah Jones • Individual                                         │    │
│  │  🚙 En Route: Mike R. (Express Towing) ETA 8min                   │    │
│  │                                                [→ Swipe details ]│    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                       Showing 4 of 47 requests                     │    │
│  │                      [Scroll to load more]                         │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│  [📍 VIEW MAP]                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

**Swipe Actions:**
- **Swipe right:** Quick action (Assign/Acknowledge)
- **Swipe left:** Secondary actions
- **Tap:** Open details

### Screen 4: Request Detail

**Purpose:** Full request information with actions

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  ← Back           #REQ-5892                                                       │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  🔴 URGENT                                                           │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Towing - Engine Failure                                            │    │
│  │  M4 Motorway, Junction 8 (eastbound)                               │    │
│  │                                                                  │    │
│  │  👤 John Doe                                  ⏱️ 8 min waiting    │    │
│  │     +44 7700 900123                           📱 [Call]           │    │
│  │                                                                  │    │
│  │  🚗 2022 Ford Fiesta                               🛡️ AXA Insurance │    │
│  │     BG23 XYZ                                     ✅ Verified        │    │
│  │                                                                  │    │
│  │  Created: 14:25                                           [Map] │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  AVAILABLE PARTNERS (nearest first)                                │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │ 🚛 Highway Heroes • David M.                                │  │    │
│  │  │    2.3 mi away • ⭐ 4.9 • ETA 8 min                        │  │    │
│  │  │    [ASSIGN NOW]                                            │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │ 🚛 Express Towing • Tom K.                                  │  │    │
│  │  │    3.1 mi away • ⭐ 4.7 • ETA 12 min                       │  │    │
│  │  │    [ASSIGN NOW]                                            │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │ 🚗 Independent • John D.                                    │  │    │
│  │  │    4.5 mi away • ⭐ 4.5 • ETA 15 min                       │  │    │
│  │  │    [ASSIGN NOW]                                            │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  │                                                                  │    │
│  │                                  [View all 8 partners →]        │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  TIMELINE                                                          │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │ 14:25  Request created                                        │  │    │
│  │  │ 14:26  Location captured: M4 J8                              │  │    │
│  │  │ 14:27  Insurance verified: AXA - Pre-approved                │  │    │
│  │  │ 14:28  Request queued for dispatch                           │  │    │
│  │  │        ⏳ Awaiting partner assignment...                     │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  ACTIONS                                                           │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  [📍 View on Map]  [📞 Call User]  [💬 Message User]               │    │
│  │  [📢 Broadcast to Partners]  [⚠️ Escalate]                         │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│                            [ASSIGN TO BEST PARTNER]                          │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Screen 5: Alerts

**Purpose:** Critical alert management

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  ← Back                Alerts                       3 critical         [⋮]  │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  🔴 CRITICAL (3)    🟠 URGENT (0)    🟡 WARNING (5)    🔵 INFO (2)   │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  ⚠️ SLA BREACH                                       2 min ago    │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Request #REQ-5872 has exceeded 30-minute response threshold       │    │
│  │  Location: M25 Junction 10 • Service: Towing                      │    │
│  │                                                                  │    │
│  │  [VIEW REQUEST]  [REASSIGN]  [CONTACT USER]  [ACKNOWLEDGE]       │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  🔴 NO PARTNERS AVAILABLE                                5 min ago │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Zero partners available in North District                       │    │
│  │  3 requests waiting for assignment                               │    │
│  │                                                                  │    │
│  │  [BROADCAST]  [EXPAND SEARCH]  [ESCALATE]  [ACKNOWLEDGE]          │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  🟠 PARTNER OFFLINE                                    12 min ago │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Express Towing - 3 trucks out for maintenance                    │    │
│  │  Reduced capacity in West District for 2 hours                    │    │
│  │                                                                  │    │
│  │  [VIEW PARTNER]  [SEND MESSAGE]  [ACKNOWLEDGE]                   │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  🟡 HIGH DEMAND EXPECTED                                25 min ago│    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Event at Wembley Stadium expected to increase demand             │    │
│  │  Time: Today 18:00-22:00 • Region: North District                  │    │
│  │                                                                  │    │
│  │  [VIEW REGION]  [PREPARE BROADCAST]  [DISMISS]                    │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  🔵 SYSTEM MAINTENANCE                                  1 hour ago│    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  Scheduled maintenance tonight 02:00-02:30 UTC                     │    │
│  │  Expected downtime: 30 minutes                                    │    │
│  │                                                                  │    │
│  │  [VIEW DETAILS]  [SCHEDULE REMINDER]  [DISMISS]                   │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                       Show earlier alerts                          │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│  [MARK ALL READ]                                                            │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Screen 6: Profile & Settings

**Purpose:** User account and app configuration

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  ← Back                Profile                                                      │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                        ┌──────────────────────────────────────┐       │    │
│  │                        │                                        │       │    │
│  │                        │           [Photo]                     │       │    │
│  │                        │                                        │       │    │
│  │                        │      Maria Rodriguez                 │       │    │
│  │                        │   Incident Response Specialist       │       │    │
│  │                        │                                        │       │    │
│  │                        │      [EDIT PROFILE]                  │       │    │
│  │                        └──────────────────────────────────────┘       │    │
│  │                                                                  │    │
│  │  Status:          🟢 On Call                                        │    │
│  │  Shift:           Night (23:00 - 07:00)                             │    │
│  │  Country:         🇬🇧 United Kingdom                                │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  SETTINGS                                                          │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │                                                                  │    │
│  │  🔔 Notifications                                                  │    │
│  │     Critical alerts    🔊 Always on                               │    │
│  │     Urgent alerts      🔊 On (sound)                              │    │
│  │     Warning alerts     🔊 Vibrate only                            │    │
│  │     Information       🔊 Off                                     │    │
│  │                                                    [Configure →]  │    │
│  │                                                                  │    │
│  │  🌙 Appearance                                                     │    │
│  │     Theme               ◉ Dark  ○ Light  ○ System                │    │
│  │                                                    [Configure →]  │    │
│  │                                                                  │    │
│  │  📍 Location                                                       │    │
│  │     Background location  ◉ On (required for monitoring)          │    │
│  │     High accuracy mode  ◉ On                                      │    │
│  │                                                                  │    │
│  │  🔐 Security                                                       │    │
│  │     Biometric login      ◉ On (Face ID)                          │    │
│  │     Auto-lock timer     5 minutes                                │    │
│  │                                                                  │    │
│  │  📊 Data                                                           │    │
│  │     Offline mode         ◉ On (cache for offline)                 │    │
│  │     Data saver mode      ○ Off                                     │    │
│  │                                                                  │    │
│  │  💬 Language                                                       │    │
│  │     App language         English (UK)                              │    │
│  │                                                                  │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  SUPPORT                                                          │    │
│  │  ─────────────────────────────────────────────────────────────────  │    │
│  │  [Help Center]  [Training Videos]  [Contact Support]              │    │
│  │                                                                  │    │
│  │  App Version 2.1.0 (Build 487)                                    │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                                                                  │    │
│  │                            [LOG OUT]                             │    │
│  │                                                                  │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## Component Library (Mobile)

### Mobile Button Component

```typescript
interface MobileButtonProps {
  // Variants
  variant: 'primary' | 'secondary' | 'danger' | 'text';

  // Sizes (mobile-specific)
  size: 'small' | 'medium' | 'large' | 'extra-large';

  // Full width on mobile
  fullWidth?: boolean;

  // Loading state
  loading?: boolean;

  // Icon
  icon?: ReactNode;

  // Touch feedback
  hapticFeedback?: boolean;

  // Accessibility
  ariaLabel?: string;
}

// Size specifications
const sizes = {
  small: { height: 36, paddingHorizontal: 16, fontSize: 14 },
  medium: { height: 44, paddingHorizontal: 20, fontSize: 16 },
  large: { height: 48, paddingHorizontal: 24, fontSize: 16 },
  extraLarge: { height: 56, paddingHorizontal: 32, fontSize: 18 }
};
```

### Mobile Card Component

```typescript
interface MobileCardProps {
  // Card type
  type: 'request' | 'partner' | 'alert' | 'metric';

  // Priority indicator
  priority?: 'critical' | 'urgent' | 'normal' | 'low';

  // Expandable
  expandable?: boolean;
  initiallyExpanded?: boolean;

  // Swipe actions
  swipeActions?: {
    left?: SwipeAction;
    right?: SwipeAction;
  };

  // Tap action
  onTap?: () => void;

  // Long press action
  onLongPress?: () => void;

  // Children
  children: ReactNode;
}

interface SwipeAction {
  icon: string;
  color: string;
  label: string;
  action: () => void;
}
```

### Bottom Sheet Component

```typescript
interface BottomSheetProps {
  // Snap points (percentage of screen)
  snapPoints: number[];

  // Initial snap point
  initialSnapIndex: number;

  // Enable backdrop dismiss
  enableBackdropDismiss?: boolean;

  // Header component
  header?: ReactNode;

  // Content
  children: ReactNode;

  // Close handler
  onClose?: () => void;
}

// Usage example
<BottomSheet snapPoints={[0.5, 0.9]} initialSnapIndex={0}>
  <BottomSheetHeader>Assign Partner</BottomSheetHeader>
  <PartnerList />
</BottomSheet>
```

### Action Sheet Component

```typescript
interface ActionSheetProps {
  // Title
  title?: string;

  // Message
  message?: string;

  // Actions
  actions: ActionSheetAction[];

  // Cancel button
  showCancelButton?: boolean;
  cancelButtonLabel?: string;
}

interface ActionSheetAction {
  label: string;
  icon?: string;
  type?: 'default' | 'destructive' | 'primary';
  onTap: () => void;
}
```

---

## Gesture & Interaction Design

### Swipe Action States

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SWIPE GESTURE STATES                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  STATE 1: REST (Initial)                                                │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ #REQ-5892 🔴 URGENT • Towing • M4 J8 • 8min                    │   │
│  │ ← Swipe to assign →                                             │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  STATE 2: SWIPE START (User begins drag)                                 │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ ━━━━━━━━━━ #REQ-5892 🔴 URGENT • Towing • M4 J8 • 8min         │   │
│  │ ✓ Assign to Highway Heroes (2.3mi, 8min)                        │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  STATE 3: SWIPE COMPLETE (Action triggered)                              │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ ✓ Assigned!                                                      │   │
│  │ Highway Heroes - David M. will arrive in ~8 min                  │   │
│  │ [UNDO]                                                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Long Press Actions

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    LONG PRESS INTERACTION                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Duration: 500ms (recognizable but not too slow)                        │
│  Haptic: Medium vibration when triggered                                 │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  HOLD & DRAG MODE                                               │   │
│  │  ─────────────────────────────────────────────────────────────  │   │
│  │  Long press any request card to enter multi-select mode          │   │
│  │                                                                  │   │
│  │  [✓] #REQ-5892                                              [×]   │   │
│  │  [✓] #REQ-5891                                                   │   │
│  │  [  ] #REQ-5890                                                   │   │
│  │                                                                  │   │
│  │  Selected: 2 requests                                           │   │
│  │  [BROADCAST TO ALL PARTNERS]  [ASSIGN TO SAME PARTNER]          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Pull to Refresh

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    PULL TO REFRESH STATES                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  STATE 1: PULLING (User drags down)                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                                                                  │   │
│  │                     ↕ Pull to refresh...                         │   │
│  │                                                                  │   │
│  │  [Content shifts down, revealing indicator]                     │   │
│  │                                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  STATE 2: READY (Release threshold reached)                              │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                                                                  │   │
│  │                   ↕ Release to refresh...                        │   │
│  │                                                                  │   │
│  │  [Indicator animates, showing readiness]                         │   │
│  │                                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  STATE 3: REFRESHING (Loading)                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                                                                  │   │
│  │                      ⏳ Refreshing...                            │   │
│  │                                                                  │   │
│  │  [Spinner shows, content remains visible]                       │   │
│  │                                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  STATE 4: COMPLETE (Success)                                            │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                                                                  │   │
│  │                      ✓ Updated just now                          │   │
│  │                                                                  │   │
│  │  [Success message, indicator fades out]                          │   │
│  │                                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Push Notification Design

### Notification Types

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    PUSH NOTIFICATION TYPES                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  🔴 CRITICAL (Interruption mode)                                        │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ ⚠️ SLA BREACH                                                     │   │
│  │ Request #REQ-5872 exceeded 30min threshold                      │   │
│  │ M25 J10 • Towing                                                  │   │
│  │                                                                  │   │
│  │ [ESCALATE]  [VIEW]                                               │   │
│  │                                                                  │   │
│  │ • Sound: Critical alarm tone                                      │   │
│  │ • Vibration: Strong, repeating                                   │   │
│  │ • Bypasses: Do Not Disturb, Silent mode                         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  🟠 URGENT (Immediate attention)                                        │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ ⚠️ New Urgent Request                                            │   │
│  │ Engine failure - M4 J8                                          │   │
│  │ Awaiting partner assignment                                     │   │
│  │                                                                  │   │
│  │ [ASSIGN]  [VIEW]                                                │   │
│  │                                                                  │   │
│  │ • Sound: Default notification sound                              │   │
│  │ • Vibration: Standard                                           │   │
│  │ • Respects: Do Not Disturb                                      │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  🟡 WARNING (Awareness)                                                 │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ 📊 3 Approaching SLA                                             │   │
│  │ Requests in North District need attention                      │   │
│  │                                                                  │   │
│  │ [VIEW]                                                           │   │
│  │                                                                  │   │
│  │ • Sound: None                                                    │   │
│  │ • Vibration: None (unless DND off)                               │   │
│  │ • Banners in Notification Center only                            │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  🔵 INFORMATION (FYI)                                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ 📋 Shift Handoff Complete                                        │   │
│  │ Your shift has been handed off to incoming team               │   │
│  │                                                                  │   │
│  │ • Sound: None                                                    │   │
│  │ • Vibration: None                                                │   │
│  │ • Notification Center only, no banner                           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Notification Actions (iOS / Android)

```
iOS (Rich Notifications):
┌─────────────────────────────────────────────────────────────────────────┐
│ ⚠️ SLA BREACH                                        2 min ago          │
│─────────────────────────────────────────────────────────────────────────│
│ Request #REQ-5872 exceeded 30min threshold                          │
│                                                                  │
│ ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │
│ │  ESCCALATE   │  │   VIEW       │  │   DISMISS    │              │
│ └──────────────┘  └──────────────┘  └──────────────┘              │
└─────────────────────────────────────────────────────────────────────────┘

Android (Actions):
┌─────────────────────────────────────────────────────────────────────────┐
│ ⚠️ SLA BREACH                                        2 min ago          │
│─────────────────────────────────────────────────────────────────────────│
│ Request #REQ-5872 exceeded 30min threshold                          │
│                                                                  │
│ ┌───────────────────────────────────────────────────────────────────┐ │
│ │ ESCCALATE  │  VIEW  │  DISMISS                                 │ │
│ └───────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Offline & Connectivity

### Offline Mode UI

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    OFFLINE MODE INDICATORS                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  DISCONNECTED (No connection)                                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ ⚠️ You're offline                                              │   │
│  │ ───────────────────────────────────────────────────────────────  │   │
│  │ No internet connection. Some features may be limited.           │   │
│  │                                                                  │   │
│  │ Available:                                                        │   │
│  │ • View cached requests (last updated: 14:32)                    │   │
│  │ • View partner availability (cached)                             │   │
│  │                                                                  │   │
│  │ Unavailable:                                                      │   │
│  │ • Assigning partners                                             │   │
│  │ • Real-time updates                                              │   │
│  │ • Sending broadcasts                                            │   │
│  │                                                                  │   │
│  │ [RETRY]                                    [USE OFFLINE MODE]    │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  RECONNECTING (Attempting connection)                                   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ 🔄 Reconnecting...                                              │   │
│  │ ───────────────────────────────────────────────────────────────  │   │
│  │ Attempting to reconnect...                                      │   │
│  │                                                                  │   │
│  │ Last successful sync: 14:32                                     │   │
│  │ Queued actions: 2                                               │   │
│  │                                                                  │   │
│  │ ⏳ Waiting for connection...                                     │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  CONNECTED (Restored)                                                   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ ✓ Back online                                                  │   │
│  │ ───────────────────────────────────────────────────────────────  │   │
│  │ Connected! Syncing 2 queued actions...                          │   │
│  │                                                                  │   │
│  │ • Request #REQ-5892 assigned ✓                                  │   │
│  │ • Broadcast message sent ✓                                      │   │
│  │                                                                  │   │
│  │ 3 new updates received                                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Data Caching Strategy

```typescript
interface CacheConfig {
  // Cache duration for different data types
  requests: {
    active: 1 * 60 * 1000,      // 1 minute
    completed: 5 * 60 * 1000,   // 5 minutes
    historical: 60 * 60 * 1000   // 1 hour
  };
  partners: {
    available: 30 * 1000,       // 30 seconds
    details: 5 * 60 * 1000       // 5 minutes
  };
  map: {
    tiles: 24 * 60 * 60 * 1000   // 24 hours
  };
}

// Offline storage strategy
const offlineStorage = {
  // IndexedDB for structured data
  requests: 'requests_store',
  partners: 'partners_store',
  alerts: 'alerts_store',

  // AsyncStorage for simple key-value
  userPreferences: 'user_prefs',
  cachedMetrics: 'cached_metrics',

  // File system for map tiles
  mapTiles: 'map_tiles_cache'
};
```

---

## Accessibility (Mobile)

### Mobile Accessibility Features

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    MOBILE ACCESSIBILITY FEATURES                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  VISUAL:                                                                │
│  ├─ Dynamic Type: Support iOS text size scaling (up to 200%)          │   │
│  ├─ High Contrast: Mode for enhanced visibility                        │   │
│  ├─ Color Blind: Dual-coding (icons + colors for status)               │   │
│  ├─ Reduce Motion: Disable animations when requested                   │   │
│  └─ Dark Mode: System preference respected                             │   │
│                                                                         │
│  MOTOR:                                                                 │   │
│  ├─ Voice Control: Support Siri/Google Assistant commands              │   │
│  ├─ Switch Control: Full navigation with external switches            │   │
│  ├─ Touch Accommodations: Hold duration, swipe customization           │   │
│  └─ Larger Touch Targets: Minimum 44x48px targets                      │   │
│                                                                         │
│  COGNITIVE:                                                             │   │
│  ├─ Simple Navigation: Clear, predictable back button                 │   │
│  ├─ Consistent Layout: Similar functions in same positions            │   │
│  ├─ Error Prevention: Confirmation for destructive actions             │   │
│  └─ Clear Feedback: Haptic, visual, audio confirmation                 │   │
│                                                                         │
│  HEARING:                                                               │   │
│  ├─ Visual Alerts: Flash screen for critical alerts (if enabled)      │   │
│  ├─ Haptic Feedback: Vibration patterns for different event types     │   │
│  └─ Caption Support: Video content includes captions                   │   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Screen Reader Support

```typescript
// Accessibility labels for key components
<AccessibilityInfo accessibilityLabel="Urgent request number 5892, towing,
  M4 Junction 8, waiting 8 minutes, swipe right to assign to partner">

// Accessibility hints for gestures
<AccessibilityInfo accessibilityHint="Swipe right to assign to nearest partner,
  double tap to view details">

// Live region announcements
<AccessibilityInfo liveRegion="assertive">
  Request {requestId} has been assigned to {partnerName}
</AccessibilityInfo>
```

---

## Responsive Design

### Screen Size Adaptations

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SCREEN SIZE ADAPTATIONS                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  SMALL (< 375px - iPhone SE, iPhone 13 Mini)                            │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • Single column layout                                           │   │
│  │ • Hide less critical metrics                                     │   │
│  │ • Larger touch targets (minimum 44px)                            │   │
│  │ • Stacked navigation (bottom only)                               │   │
│  │ • Simplified cards (essential info only)                         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  MEDIUM (375px - 414px - iPhone 13, Pixel 5)                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • Single column with occasional side-by-side                      │   │
│  │ • All metrics visible                                            │   │
│  │ • Standard touch targets (44-48px)                               │   │
│  │ • Full bottom navigation                                         │   │
│  │ • Standard card layout                                           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  LARGE (> 414px - iPhone 13 Pro Max, Pixel 6 Pro)                      │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • Some two-column layouts (metrics)                              │   │
│  │ • All metrics visible with trends                                │   │
│  │ • Enhanced card layouts with more detail                         │   │
│  │ • Larger text by default                                         │   │
│  │ • Optional tablet-style sidebar (landscape)                      │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  TABLET (> 768px - iPad, Android tablets)                               │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • Two-column main layout                                         │   │
│  │ • Side navigation + bottom navigation (optional)                │   │
│  │ • Full-featured cards with all details                           │   │
│  │ • Floating action buttons for primary actions                    │   │
│  │ • Split view support (request list + detail)                    │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Orientation Adaptations

```
LANDSCAPE MODE:
┌──────────────────────────────────────────────────────────────────────────────┐
│  ← Back           Dashboard                         Country: 🇬🇧         [⋮]       │
├───────────────────────────────────────┬────────────────────────────────────────┤
│                                      │                                    │
│  ┌─────────────┐ ┌─────────────┐     │  ACTIVE REQUESTS (scrolling)     │
│  │     47      │ │     12      │     │  ───────────────────────────────  │
│  │  Active     │ │  Partners   │     │  #REQ-5892 🔴 URGENT 8min       │
│  │  Requests   │ │  Available  │     │  #REQ-5891 🟡 HIGH 12min        │
│  │             │ │             │     │  #REQ-5890 🟢 NORMAL 5min       │
│  └─────────────┘ └─────────────┘     │  #REQ-5889 🟢 NORMAL 15min      │
│                                      │  #REQ-5888 🟢 NORMAL 2min       │
│  PARTNER STATUS                      │  #REQ-5887 🟢 NORMAL 8min       │
│  ┌─────────────────────────────┐      │                                   │
│  │ 🚛 Towing    18/45          │      │                                   │
│  │ 🔧 Mechanics  6/15          │      │                                   │
│  │ 🚗 Independent 5/5           │      │                                   │
│  └─────────────────────────────┘      │                                   │
└───────────────────────────────────────┴────────────────────────────────────────┘
```

---

*Document Version: 1.0*
*Last Updated: January 10, 2026*
*Author: Central Monitoring Domain Team*
