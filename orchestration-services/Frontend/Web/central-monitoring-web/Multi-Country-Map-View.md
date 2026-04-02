# Real-Time Multi-Country Map View - UI Documentation

**Version:** 1.0
**Last Updated:** January 10, 2026
**Product:** Gogidix Road Assist Services
**Domain:** Central Monitoring - Global Operations

---

## Table of Contents

1. [Overview](#overview)
2. [Multi-Country Architecture](#multi-country-architecture)
3. [Map Design Specifications](#map-design-specifications)
4. [Country Switching](#country-switching)
5. [Regional Operations](#regional-operations)
6. [Performance Optimization](#performance-optimization)
7. [Data Synchronization](#data-synchronization)

---

## Overview

The Multi-Country Map View provides **operational visibility across all Gogidix service regions**. It enables central operators to monitor, coordinate, and optimize service delivery across multiple countries from a single unified interface.

### Global Operations Philosophy

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    GLOBAL OPERATIONS PHILOSOPHY                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  🌍 THINK GLOBAL, ACT LOCAL                                            │
│  Unified view with regional customization                               │
│                                                                         │
│  ⚡ REAL-TIME ACROSS BORDERS                                           │
│  Same data freshness regardless of location                            │
│                                                                         │
│  🔄 SEAMLESS HANDOFFS                                                  │
│  Cross-border support without context switching                        │
│                                                                         │
│  📊 NORMALIZED METRICS                                                 │
│  Consistent KPIs across all regions for comparison                    │
│                                                                         │
│  🎨 REGIONAL AESTHETICS                                                │
│  Local context (language, currency, units) preserved                  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Multi-Country Architecture

### Supported Countries & Regions

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SUPPORTED COUNTRIES & REGIONS                         │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  🇬🇧 UNITED KINGDOM                             Primary Market             │
│  ├─ Regions: England, Scotland, Wales, Northern Ireland               │
│  ├─ Cities: London, Manchester, Birmingham, Glasgow, Belfast         │
│  ├─ Currency: GBP (£)                                                  │
│  ├─ Emergency: 999                                                     │
│  └─ Partners: 45 companies, 120+ drivers                              │
│                                                                         │
│  🇮🇪 IRELAND                                   Expansion Market           │
│  ├─ Regions: Leinster, Munster, Connacht, Ulster                      │
│  ├─ Cities: Dublin, Cork, Limerick, Galway                           │
│  ├─ Currency: EUR (€)                                                  │
│  ├─ Emergency: 112                                                     │
│  └─ Partners: 12 companies, 30+ drivers                               │
│                                                                         │
│  🇫🇷 FRANCE                                    Planned Expansion Q2     │
│  ├─ Regions: Île-de-France, Provence, Normandie, Brittany             │
│  ├─ Cities: Paris, Marseille, Lyon, Nice                             │
│  ├─ Currency: EUR (€)                                                  │
│  ├─ Emergency: 112                                                     │
│  └─ Partners: Planning - 20+ target companies                         │
│                                                                         │
│  🇩🇪 GERMANY                                  Planned Expansion Q3     │
│  ├─ Regions: Bavaria, Berlin, Hamburg, North Rhine-Westphalia         │
│  ├─ Cities: Berlin, Munich, Hamburg, Cologne                         │
│  ├─ Currency: EUR (€)                                                  │
│  ├─ Emergency: 112                                                     │
│  └─ Partners: Planning - 15+ target companies                         │
│                                                                         │
│  🇳🇱 NETHERLANDS                               Planned Expansion Q3     │
│  ├─ Regions: North Holland, South Holland, Utrecht                    │
│  ├─ Cities: Amsterdam, Rotterdam, The Hague, Utrecht                 │
│  ├─ Currency: EUR (€)                                                  │
│  ├─ Emergency: 112                                                     │
│  └─ Partners: Planning - 10+ target companies                         │
│                                                                         │
│  🇧🇪 BELGIUM                                  Planned Expansion Q4     │
│  ├─ Regions: Flanders, Wallonia, Brussels                             │
│  ├─ Cities: Brussels, Antwerp, Ghent, Liège                          │
│  ├─ Currency: EUR (€)                                                  │
│  ├─ Emergency: 112                                                     │
│  └─ Partners: Planning - 8+ target companies                          │
│                                                                         │
│  🇪🇸 SPAIN                                    Future Market             │
│  ├─ Regions: Madrid, Barcelona, Valencia, Seville                     │
│  ├─ Cities: Madrid, Barcelona, Valencia, Seville                     │
│  ├─ Currency: EUR (€)                                                  │
│  ├─ Emergency: 112                                                     │
│  └─ Partners: Exploratory phase                                       │
│                                                                         │
│  🇮🇹 ITALY                                    Future Market             │
│  ├─ Regions: Lombardy, Lazio, Campania, Sicily                        │
│  ├─ Cities: Milan, Rome, Naples, Palermo                             │
│  ├─ Currency: EUR (€)                                                  │
│  ├─ Emergency: 112                                                     │
│  └─ Partners: Exploratory phase                                       │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Data Center Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    MULTI-REGION DATA ARCHITECTURE                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    CENTRAL MONITORING                            │   │
│  │                    (Any Region)                                   │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              │                                          │
│                  ┌─────────┴─────────┐                                   │
│                  │   Global Router    │                                   │
│                  │   (Edge Network)    │                                   │
│                  └─────────┬─────────┘                                   │
│                              │                                          │
│      ┌───────────────┼───────────────┼───────────────┐                   │
│      ▼               ▼               ▼               ▼                   │
│ ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐                │
│ │         │   │         │   │         │   │         │                │
│ │   UK    │   │ Ireland │   │ France  │   │ Germany │                │
│ │Region   │   │ Region  │   │ Region  │   │ Region  │                │
│ │         │   │         │   │         │   │         │                │
│ │London DC│   │Dublin DC│   │Paris DC │   │Frankfurt│                │
│ └─────────┘   └─────────┘   └─────────┘   └─────────┘                │
│      │               │               │               │                   │
│      └───────────────┴───────────────┴───────────────┴──────────────┘    │
│                              │                                          │
│                              ▼                                          │
│                 ┌────────────────────────────────┐                       │
│                 │   Global Data Sync Service      │                       │
│                 │   (Real-time replication)      │                       │
│                 └────────────────────────────────┘                       │
│                                                                         │
│  LATENCY TARGETS (from operator to data):                             │
│  • Same region: < 50ms                                               │
│  • Adjacent region: < 100ms                                           │
│  • Cross-continent: < 200ms                                           │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Map Design Specifications

### Global Overview Map

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  ← Back           Global Operations Overview               [🌍 View All]   [⋮]  │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  COUNTRY SUMMARY                                                    │    │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐        │    │
│  │  │  🇬🇧 UK     │ │  🇮🇪 Ireland│ │  🇫🇷 France │ │  🇩🇪 Germany│        │    │
│  │  │           │ │           │ │           │ │           │        │    │
│  │  │ 23 Active │ │  8 Active │ │   Coming   │ │   Coming   │        │    │
│  │  │   5 Pending│ │   2 Pending│ │    Q2     │ │    Q3     │        │    │
│  │  │   18 Avail │ │   3 Avail │ │            │ │            │        │    │
│  │  │  12min ETA│ │  16min ETA│ │            │ │            │        │    │
│  │  │  96.5% SLA│ │  94.2% SLA│ │            │ │            │        │    │
│  │  │           │ │           │ │  [View]    │ │  [View]    │        │    │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘        │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                                                                  │    │
│  │         🗺️                   INTERACTIVE GLOBAL MAP                 │    │
│  │                                                                  │    │
│  │              🇮🇪(8)     🇬🇧(47)                                  │    │
│  │                                                                 │    │
│  │                          🇫🇷                      🇩🇪          │    │
│  │                                                                  │    │
│  │                                                                  │    │
│  │              🇧🇪                                      🇳🇱          │    │
│  │                                                          🇪🇸🇮🇹       │    │
│  │                                                                  │    │
│  │  [Click country to view regional operations]                       │    │
│  │  [Hover for summary]                                            │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  LEGEND                                                            │    │
│  │  🇬🇧 Active (23+ requests)    🟠 Elevated (10-20)    🔴 Critical (20+)   │    │
│  │  🟢 Normal (<10 requests)    ⚪ Coming Soon                  📊 Planned        │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│  [SWITCH TO UNITED KINGDOM ↓]           [COMPARE REGIONS]                   │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Regional Map View (Country-Specific)

```
┌──────────────────────────────────────────────────────────────────────────────┐
│  ← Back     United Kingdom Operations              [🇬🇧]           [Switch ▼]  [⋮]  │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Region: [All Regions ▼]    City: [All Cities ▼]    Timezone: GMT          │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  REGIONAL SUMMARY                                                  │    │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐     │    │
│  │  │  London    │ │  Manchester │ │  Scotland   │ │  N. Ireland │     │    │
│  │  │    15      │ │      8      │ │      6      │ │      3      │     │    │
│  │  │  Active     │ │  Active     │ │  Active     │ │  Active     │     │    │
│  │  │   4 Pending│ │   2 Pending│ │   1 Pending│ │   0 Pending│     │    │
│  │  │  12 Avail  │ │   5 Avail  │ │   4 Avail  │ │   2 Avail  │     │    │
│  │  │  10min ETA │ │  14min ETA │ │  18min ETA │ │  22min ETA │     │    │
│  │  │  97.2% SLA │ │  95.1% SLA │ │  93.8% SLA │ │  92.0% SLA │     │    │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘     │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                                                                  │    │
│  │         🗺️                    UNITED KINGDOM MAP                   │    │
│  │                                                                  │    │
│  │              🏴󠁧󠁢�������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������󴃁��������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������󴃂����������������������������������������������������������������������������󴃁���������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������│    │
│  │                                                                  │    │
│  │  [Click region to zoom]  [Click marker for details]               │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  ACTIVE REQUESTS IN UNITED KINGDOM (47 total)                      │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │ London (15)  │ Manchester (8) │ Scotland (6) │ N.Ireland (3) │  │    │
│  │  │ 3 Urgent      │ 2 Urgent        │ 1 Urgent       │ 0 Urgent      │  │    │
│  │  │ 4 Pending     │ 2 Pending       │ 1 Pending      │ 0 Pending      │  │    │
│  │  │ [View All]    │ [View All]      │ [View All]     │ [View All]     │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │  PARTNER COVERAGE BY REGION                                         │    │
│  │  ┌──────────────────────────────────────────────────────────────┐  │    │
│  │  │ Region         │ Partners │ Drivers │ Avail │ On Job │ Utilization│  │    │
│  │  ├──────────────────────────────────────────────────────────────┤  │    │
│  │  │ London         │    15    │   60    │   12   │   48    │    80%     │  │    │
│  │  │ Manchester     │     8    │   32    │    5   │   27    │    84%     │  │    │
│  │  │ Scotland       │     6    │   18    │    4   │   14    │    78%     │  │    │
│  │  │ N. Ireland     │     3    │   10    │    2   │    8    │    80%     │  │    │
│  │  └──────────────────────────────────────────────────────────────┘  │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│  [📍 MY LOCATION]              [⚙️ MAP SETTINGS]                   [EXPAND FULLSCREEN]   │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## Country Switching

### Country Selector Component

```typescript
interface CountrySelector {
  currentCountry: CountryCode;
  availableCountries: CountryInfo[];
  onCountryChange: (country: CountryCode) => void;
}

interface CountryInfo {
  code: string;           // 'GB', 'IE', 'FR', etc.
  name: string;          // 'United Kingdom'
  flag: string;          // Emoji flag
  active: boolean;       // Is country operational
  coming: boolean;       // Is country launching soon
  requestCount: number;  // Active requests
  partnerCount: number;  // Available partners
  timeZone: string;      // Local timezone
}

// Usage
<CountrySelector
  currentCountry="GB"
  availableCountries={[
    { code: 'GB', name: 'United Kingdom', flag: '🇬🇧', active: true, requestCount: 47, partnerCount: 18, timeZone: 'GMT' },
    { code: 'IE', name: 'Ireland', flag: '🇮🇪', active: true, requestCount: 8, partnerCount: 3, timeZone: 'GMT' },
    { code: 'FR', name: 'France', flag: '🇫🇷', active: false, coming: true, timeZone: 'CET' }
  ]}
  onCountryChange={(code) => switchToCountry(code)}
/>
```

### Country Switch Animation

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    COUNTRY SWITCH TRANSITION                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  CURRENT STATE (United Kingdom)                                       │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  🇬🇧 United Kingdom                                           │   │
│  │  47 requests • 18 partners available                            │   │
│  │  £ GBP • GMT timezone                                        │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓ (User selects Ireland)                   │
│                                                                         │
│  TRANSITION (300ms slide + fade)                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  ┌──────────────┐                                              │   │
│  │  │ 🇬🇧 UK       │  ▶▶▶  (slides out left)                       │   │
│  │  └──────────────┘                                              │   │
│  │                    ◀◀◀  (slides in from right)                 │   │
│  │                       ┌──────────────┐                            │   │
│  │                       │ 🇮🇪 Ireland   │                            │   │
│  │                       └──────────────┘                            │   │
│  │                                                                  │   │
│  │  Loading Ireland data... (skeleton briefly)                     │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓                                       │
│  NEW STATE (Ireland)                                                │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  🇮🇪 Ireland                                                   │   │
│  │  8 requests • 3 partners available                              │   │
│  │  € EUR • GMT timezone                                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  Simultaneously:                                                      │
│  • Currency formats update (GBP → EUR)                              │
│  • Timezone display updates                                          │
│  • Region names localize                                           │
│  • Partner list refreshes                                           │
│  • Map bounds adjust to Ireland                                     │
│  • Language updates (if applicable)                                  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Quick Switch Keyboard Shortcuts

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    QUICK SWITCH SHORTCUTS                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  SHORTCUT               ACTION                                       │
│  ───────────────────────────────────────────────────────────────────  │
│  Cmd/Ctrl + 1           Switch to United Kingdom                       │
│  Cmd/Ctrl + 2           Switch to Ireland                              │
│  Cmd/Ctrl + 3           Switch to France (when available)               │
│  Cmd/Ctrl + 4           Switch to Germany (when available)              │
│  Cmd/Ctrl + 0           Switch to Global View                          │
│  Cmd/Ctrl + Shift + C   Open country selector                         │
│                                                                         │
│  For international keyboards:                                         │
│  Cmd/Ctrl + G + B       Go back to Global view                        │
│  Cmd/Ctrl + G + H       Go to home country                            │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Regional Operations

### Cross-Border Support

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    CROSS-BORDER OPERATIONS                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  SCENARIO 1: User Near Border                                       │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ User Location: Newry, Northern Ireland (near ROI border)        │   │
│  │ Request: Towing service                                         │   │
│  │ Analysis:                                                       │   │
│  │  • Nearest partner: Derry/Londonderry, NI (5 partners)        │   │
│  │  • Next nearest: Donegal, Ireland (2 partners, 15min away)      │   │
│  │  • Decision: Check both regions for availability               │   │
│  │  Options:                                                        │   │
│  │  1. Assign NI partner (5min ETA) ✅ Preferred                  │   │
│  │  2. Assign Ireland partner (15min ETA) if NI unavailable      │   │
│  │  3. Broadcast to both regions if no local partners             │   │
│  │ Impact: Wider partner pool, faster service                     │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  SCENARIO 2: Partner Working Across Border                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Partner: Express Towing (registered in Belfast, NI)            │   │
│  │ Driver Location: Drogheda, Ireland (moved across for job)      │   │
│  │ Current Status: Available, willing to take NI jobs            │   │
│  │ System Handling:                                                │   │
│  │  • Driver marked as "Cross-border available"                  │   │
│  │  • Shows in both NI and Ireland partner pools                   │   │
│  │  • ETA calculated with border crossing time added             │   │
│  │  • Currency conversion handled automatically                      │   │
│  │ Pricing:                                                       │   │
│  │  • NI job: Charged in GBP, paid in GBP                         │   │
│  │  │ ROI job: Charged in EUR, paid in EUR                         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  SCENARIO 3: Cross-Border Escalation                                  │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Situation: No partners available in any single region           │   │
│  │ Location: Dundalk, Ireland (near NI border)                    │   │
│  │ Action:                                                         │   │
│  │  • System automatically expands search radius across border     │   │
│  │  • Includes partners from Northern Ireland                       │   │
│  │  • Presents unified list with location indicator                 │   │
│  │  User sees: "Searching Ireland + Northern Ireland..."             │   │
│  │ Result: Faster dispatch, better service                         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Regional Customization

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    REGIONAL CUSTOMIZATION SETTINGS                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  COUNTRY: United Kingdom (GB)                                        │
│  ├─ Currency: GBP (£)                                                 │
│  ├─ Distance Unit: Miles                                              │
│  ├─ Speed Unit: MPH                                                   │
│  ├─ Date Format: DD/MM/YYYY                                           │
│  ├─ Time Format: 24-hour (14:30)                                      │
│  ├─ Language: English (UK)                                           │
│  ├─ Emergency: 999                                                    │
│  └─ Map Provider: Google Maps                                         │
│                                                                         │
│  COUNTRY: Ireland (IE)                                              │
│  ├─ Currency: EUR (€)                                                 │
│  ├─ Distance Unit: Kilometers                                        │
│  ├─ Speed Unit: KM/H                                                 │
│  ├─ Date Format: DD/MM/YYYY                                           │
│  ├─ Time Format: 24-hour (14:30)                                      │
│  ├─ Language: English (Ireland)                                     │
│  ├─ Emergency: 112                                                    │
│  └─ Map Provider: Google Maps                                         │
│                                                                         │
│  COUNTRY: France (FR) - Launching Q2                                 │
│  ├─ Currency: EUR (€)                                                 │
│  ├─ Distance Unit: Kilometers                                        │
│  ├─ Speed Unit: KM/H                                                 │
│  ├─ Date Format: DD/MM/YYYY                                           │
│  ├─ Time Format: 24-hour (14h30)                                     │
│  ├─ Language: Français (French)                                     │
│  ├─ Emergency: 112                                                    │
│  └─ Map Provider: Mapbox (local requirement)                          │
│                                                                         │
│  COUNTRY: Germany (DE) - Launching Q3                                │
│  ├─ Currency: EUR (€)                                                 │
│  ├─ Distance Unit: Kilometers                                        │
│  ├─ Speed Unit: KM/H                                                 │
│  ├─ Date Format: DD.MM.YYYY                                           │
│  ├─ Time Format: 24-hour (14:30)                                     │
│  ├─ Language: Deutsch (German)                                       │
│  ├─ Emergency: 112                                                    │
│  └─ Map Provider: Google Maps                                         │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Performance Optimization

### Map Tile Caching Strategy

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    MAP TILE CACHING STRATEGY                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  PRE-CACHING (App Launch)                                              │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Priority regions (based on user activity):                       │   │
│  │  • Current country: Level 4-6 tiles around major cities        │   │
│  │  • Adjacent regions: Level 2-4 tiles around borders            │   │
│  │  • All regions: Level 1-2 tiles (country overview)             │   │
│  │ Storage: IndexedDB (persistent across sessions)                 │   │
│  │ Expiry: 24 hours                                                │   │
│  │ Size: ~50MB per country                                         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  ON-DEMAND CACHING                                                   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ When user pans/zooms map:                                     │   │
│  │  • Fetch tiles for visible area                               │   │
│  │  • Cache adjacent tiles (anticipate movement)                 │   │
│  │  • Background prefetch for next zoom level                      │   │
│  │ Storage: IndexedDB + Memory (fast access)                      │   │
│  │ Expiry: 7 days (static map tiles)                              │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  OFFLINE SUPPORT                                                      │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ When device offline:                                          │   │
│  │  • Show cached tiles (indicate with "Offline" banner)          │   │
│  │  • Show entity markers (cached locations)                      │   │
│  │  • Disable tile refresh attempts                                │   │
│  │  • Allow basic operations with cached data                      │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Entity Marker Optimization

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ENTITY MARKER OPTIMIZATION                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  MARKER CLUSTERING (for high-density areas)                              │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Zoom Level 1-3 (Country view):                                 │   │
│  │  • Show region markers with count                               │   │
│  │  🇬🇧 London (47) 🏴�󠁢������������������������������������������������������������������������������������������│   │
│  │                                                                 │   │
│  │ Zoom Level 4-6 (Region view):                                 │   │
│  │  • Show city-level clusters                                     │   │
│  │  📍 London Central (23)  📍 West London (12)  📍 North (8)   │   │
│  │                                                                 │   │
│  │ Zoom Level 7-10 (City view):                                   │   │
│  │  • Show individual entity markers                                 │   │
│  │  • Enable marker clustering if > 50 in view                      │   │
│  │                                                                 │   │
│  │ Zoom Level 11+ (Street view):                                   │   │
│  │  • Show all individual markers                                   │   │
│  │  • Enable precise location display                               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  MARKER UPDATE THROTTLING                                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Zoom Level    Update Frequency    Reason                       │   │
│  │ ───────────────────────────────────────────────────────       │   │
│  │ 1-3 (Country)  Every 30s        Low detail, big area           │   │
│  │ 4-6 (Region)   Every 20s        Medium detail                   │   │
│  │ 7-10 (City)    Every 10s        High detail                    │   │
│  │ 11+ (Street)   Every 5s         Precise tracking               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  PARTNER MARKER VISUAL VARIATIONS                                   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Provider Type         Marker Color      Shape                │   │
│  │ ───────────────────────────────────────────────────────       │   │
│  │  Towing Company       🚛 Blue          Shield icon          │   │
│  │  Mobile Mechanic     🔧 Orange         Wrench icon          │   │
│  │  Independent Driver  🚗 Green          Car icon             │   │
│  │  Multi-service        🚙 Purple         Star icon            │   │
│  │  Offline/Maintenance  ⚪ Gray           Strikethrough         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Data Synchronization

### Cross-Region Data Sync

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    CROSS-REGION DATA SYNCHRONIZATION                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  SYNC ARCHITECTURE:                                                   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                                                                  │   │
│  │   UK Region ─────┐                                            │   │
│  │        │         │                                            │   │
│  │        └────┬────┘                                            │   │
│  │             │                                                   │   │
│  │             ▼                                                   │   │
│  │      ┌─────────────┐                                           │   │
│  │      │  Global     │                                            │   │
│  │      │  State      │◄──── Sync to all regions               │   │
│  │      │  Store      │     (event-driven)                   │   │
│  │      └─────────────┘                                           │   │
│  │             │                                                   │   │
│  │             ├────────────┬────────────┐                        │   │
│  │             ▼            ▼            ▼                        │   │
│  │       Ireland     France      Germany                       │   │
│  │      Region      Region      Region                        │   │
│  │        │           │           │                           │   │
│  │        └───────────┴───────────┘                           │   │
│  │                   ▼                                           │   │
│  │           Local State Store                                 │   │
│  │           (per-country context)                             │   │
│  │                                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  SYNC EVENTS:                                                        │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ Event                  Sync Strategy        Target              │   │
│  │ ───────────────────────────────────────────────────────────     │   │
│  │ Request created       Broadcast to all     All regions         │   │
│  │ Request assigned     Broadcast to all     All regions         │   │
│  │ Request completed    Broadcast to all     All regions         │   │
│  │ Partner location     Region-specific     Home region only    │   │
│  │ Partner status       Broadcast to all     All regions         │   │
│  │ SLA breach            Broadcast to all     All regions         │   │
│  │ System alert          Broadcast to all     All regions         │   │
│  │ User location        Region-specific     Home region only    │   │
│  │ Partner sign-on       Region-specific     Home region only    │   │
│  │ Configuration change  Broadcast to all     All regions         │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  DATA FRESHNESS:                                                     │
│  • Requests: Real-time (WebSocket)                                  │
│  • Partners: Every 10s (optimized by region)                        │
│  • Users: Every 30s (optimized by region)                            │
│  • System status: Every 30s                                          │
│  • Metrics: Aggregated every minute (real-time updates)              │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Global Event Bus

```typescript
// Global event types for multi-country operations
interface GlobalEvent {
  // Request events (broadcast globally)
  'request:created': GlobalRequestEvent;
  'request:assigned': GlobalRequestEvent;
  'request:completed': GlobalRequestEvent;
  'request:escalated': GlobalRequestEvent;

  // Partner events (broadcast globally for status changes)
  'partner:available': PartnerStatusEvent;
  'partner:busy': PartnerStatusEvent;
  'partner:offline': PartnerStatusEvent;

  // Alert events (broadcast globally for critical alerts)
  'alert:sla-breach': GlobalAlertEvent;
  'alert:emergency': GlobalAlertEvent;
  'alert:system': GlobalAlertEvent;

  // Configuration events (broadcast globally)
  'config:changed': ConfigEvent;
  'config:maintenance-scheduled': MaintenanceEvent;
}

interface GlobalRequestEvent {
  requestId: string;
  region: string;           // Originating region
  country: string;         // Originating country
  timestamp: Date;
  data: RequestData;
}

// Example: Request created in Ireland broadcast to UK
{
  eventType: 'request:created',
  requestId: 'REQ-IE-1234',
  region: 'leinster',
  country: 'IE',
  timestamp: '2026-01-10T14:30:00Z',
  data: {
    serviceType: 'towing',
    location: { lat: 53.3498, lng: -6.2603 },
    priority: 'urgent'
  }
}
```

---

*Document Version: 1.0*
*Last Updated: January 10, 2026*
*Author: Central Monitoring Domain Team*
