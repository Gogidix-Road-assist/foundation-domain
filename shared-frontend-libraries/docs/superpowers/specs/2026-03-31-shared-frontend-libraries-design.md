# Foundation-Domain\shared-frontend-libraries Design Document

**Date:** 2026-03-31
**Version:** 1.0
**Status:** Approved

---

## Executive Summary

Enterprise-grade shared component library serving 15+ Business Domain frontends with multi-country, multi-tenant, multi-role architecture support. This library provides the foundation for all Rapid Assist frontend applications.

---

## Architecture Overview

### Monorepo Structure

```
shared-frontend-libraries/
├── packages/
│   ├── design-system/              # Core design tokens, theme, colors, typography
│   │   ├── src/
│   │   │   ├── theme/
│   │   │   │   ├── index.ts              # Theme exports
│   │   │   │   ├── colors.ts             # Color palettes
│   │   │   │   ├── typography.ts         # Font configurations
│   │   │   │   ├── spacing.ts            # Spacing scale
│   │   │   │   ├── breakpoints.ts        # Responsive breakpoints
│   │   │   │   └── shadows.ts           # Shadow utilities
│   │   │   ├── modes/
│   │   │   │   ├── light.ts             # Light mode theme
│   │   │   │   ├── dark.ts              # Dark mode theme
│   │   │   │   └── critical.ts          # Emergency/critical mode
│   │   │   ├── brand/
│   │   │   │   ├── management.ts         # Management domain colors
│   │   │   │   └── business-domains.ts   # 7 domain palettes
│   │   │   └── personalization/
│   │   │       ├── presets.ts            # Pre-built themes
│   │   │       └── custom-picker.ts      # Custom color picker
│   │   └── package.json
│   │
│   ├── components/                   # Enterprise UI components
│   │   ├── src/
│   │   │   ├── Button/
│   │   │   ├── Card/
│   │   │   ├── Input/
│   │   │   ├── Select/
│   │   │   ├── Checkbox/
│   │   │   ├── Radio/
│   │   │   ├── Switch/
│   │   │   ├── Slider/
│   │   │   ├── Badge/
│   │   │   ├── Avatar/
│   │   │   ├── Chip/
│   │   │   ├── Tabs/
│   │   │   ├── Accordion/
│   │   │   ├── Alert/
│   │   │   ├── Dialog/
│   │   │   ├── Drawer/
│   │   │   ├── Menu/
│   │   │   ├── Progress/
│   │   │   ├── Skeleton/
│   │   │   ├── Tooltip/
│   │   │   └── index.ts
│   │   └── package.json
│   │
│   ├── layouts/                     # Layout components
│   │   ├── src/
│   │   │   ├── AppLayout/
│   │   │   ├── AuthLayout/
│   │   │   ├── DashboardLayout/
│   │   │   ├── PublicLayout/
│   │   │   ├── Header/
│   │   │   ├── Sidebar/
│   │   │   ├── Footer/
│   │   │   └── index.ts
│   │   └── package.json
│   │
│   ├── forms/                       # Form components with validation
│   │   ├── src/
│   │   │   ├── FormBuilder/
│   │   │   ├── ValidatedInput/
│   │   │   ├── FormField/
│   │   │   ├── FormLabel/
│   │   │   ├── FormError/
│   │   │   ├── DatePickerField/
│   │   │   ├── SelectField/
│   │   │   ├── TextField/
│   │   │   └── index.ts
│   │   └── package.json
│   │
│   ├── data-display/                # Data visualization components
│   │   ├── src/
│   │   │   ├── DataTable/
│   │   │   ├── DataGrid/
│   │   │   ├── VirtualizedTable/
│   │   │   ├── Chart/
│   │   │   ├── LineChart/
│   │   │   ├── BarChart/
│   │   │   ├── PieChart/
│   │   │   ├── Map/
│   │   │   ├── StatCard/
│   │   │   ├── TrendIndicator/
│   │   │   └── index.ts
│   │   └── package.json
│   │
│   ├── real-time/                   # Real-time communication
│   │   ├── src/
│   │   │   ├── hooks/
│   │   │   │   ├── useWebSocket.ts
│   │   │   │   ├── usePolling.ts
│   │   │   │   ├── useRealTimeData.ts
│   │   │   │   └── useEventBus.ts
│   │   │   ├── providers/
│   │   │   │   ├── WebSocketProvider.tsx
│   │   │   │   └── RealTimeProvider.tsx
│   │   │   ├── services/
│   │   │   │   ├── ConnectionManager.ts
│   │   │   │   ├── ReconnectionStrategy.ts
│   │   │   │   └── EventPublisher.ts
│   │   │   └── index.ts
│   │   └── package.json
│   │
│   ├── dashboards/                  # Dashboard components
│   │   ├── src/
│   │   │   ├── GlobalHQDashboard/
│   │   │   ├── CountryDashboard/
│   │   │   ├── RealTimeTracker/
│   │   │   ├── AnalyticsDashboard/
│   │   │   ├── ResourceAllocation/
│   │   │   ├── StrategicDashboard/
│   │   │   ├── widgets/
│   │   │   │   ├── KPICard/
│   │   │   │   ├── LiveMap/
│   │   │   │   ├── ActivityFeed/
│   │   │   │   ├── AlertPanel/
│   │   │   │   └── StatusPanel/
│   │   │   └── index.ts
│   │   └── package.json
│   │
│   ├── brand-assets/                # Brand assets and logos
│   │   ├── src/
│   │   │   ├── Logo/
│   │   │   │   ├── BrandLogo.tsx
│   │   │   │   ├── IconLogo.tsx
│   │   │   │   ├── variants/
│   │   │   │   │   ├── primary.svg
│   │   │   │   │   ├── primary-light.svg
│   │   │   │   │   ├── icon.svg
│   │   │   │   │   ├── icon-light.svg
│   │   │   │   │   └── favicon/
│   │   │   │   └── index.ts
│   │   │   ├── icons/
│   │   │   │   ├── # 500+ SVG icons
│   │   │   │   └── index.ts
│   │   │   └── index.ts
│   │   └── package.json
│   │
│   └── utils/                       # Utility functions
│       ├── src/
│       │   ├── formatters/
│       │   │   ├── currency.ts
│       │   │   ├── date.ts
│       │   │   ├── number.ts
│       │   │   ├── phone.ts
│       │   │   └── index.ts
│       │   ├── validators/
│       │   │   ├── email.ts
│       │   │   ├── phone.ts
│       │   │   ├── postalCode.ts
│       │   │   └── index.ts
│       │   ├── helpers/
│       │   │   ├── api.ts
│       │   │   ├── storage.ts
│       │   │   ├── theme.ts
│       │   │   └── index.ts
│       │   └── index.ts
│       └── package.json
│
├── apps/
│   ├── storybook/                   # Component documentation
│   │   ├── .storybook/
│   │   ├── src/
│   │   │   └── stories/
│   │   └── package.json
│   └── demo/                        # Demo application
│       ├── src/
│       └── package.json
│
├── docs/
│   ├── getting-started.md
│   ├── components/
│   ├── themes/
│   ├── api/
│   └── examples/
│
├── tests/
│   ├── unit/
│   ├── integration/
│   └── e2e/
│
├── infrastructure/
│   ├── docker/
│   │   ├── Dockerfile
│   │   ├── Dockerfile.prod
│   │   └── docker-compose.yml
│   ├── k8s/
│   │   ├── deployment.yaml
│   │   ├── service.yaml
│   │   └── helm/
│   └── ci/
│       ├── github-actions/
│       ├── azure-pipelines/
│       └── jenkins/
│
├── package.json                     # Root package.json
├── pnpm-workspace.yaml             # Workspace config
├── tsconfig.json                   # Root TypeScript config
├── tsconfig.base.json              # Base TS config
├── vite.config.ts                  # Vite config
├── .eslintrc.js                  # ESLint config
├── .prettierrc.js                # Prettier config
├── .env.example                  # Environment variables template
├── vitest.config.ts              # Vitest config
├── playwright.config.ts           # Playwright config
├── lighthouserc.js              # Lighthouse CI config
└── README.md
```

---

## Technology Stack

### Core Dependencies
| Package | Version | Purpose |
|---------|---------|---------|
| React | 18.3.1 | UI Framework |
| React DOM | 18.3.1 | DOM rendering |
| TypeScript | 5.3+ | Type safety |
| Material UI | 6.2.0 | Base component library |
| Redux Toolkit | 2.0.1 | State management |
| TanStack Query | 5.62.11 | Server state |
| React Router | 6.x | Routing |
| Axios | 1.7.9 | HTTP client |
| Framer Motion | 12.2.0 | Animations |
| Recharts | 2.10.0 | Charts |
| Leaflet | Latest | Maps |
| Zod | 3.22.0 | Validation |

### Dev Dependencies
| Package | Version | Purpose |
|---------|---------|---------|
| Vite | 5.2.0 | Build tool |
| Vitest | Latest | Unit testing |
| Testing Library | Latest | Component testing |
| Playwright | Latest | E2E testing |
| ESLint | Latest | Linting |
| Prettier | Latest | Formatting |
| Storybook | Latest | Documentation |
| Lighthouse CI | Latest | Performance testing |
| axe-core | Latest | Accessibility testing |

---

## Module Specifications

### 1. Design System (`design-system/`)

#### Color System

**Management Domain Colors (Amazon/Stripe Standards):**
```typescript
export const MANAGEMENT_COLORS = {
  primary: '#0066CC',        // Deep Blue - Primary Action
  primaryHover: '#0052A3',   // Darker Blue - Hover State
  primaryLight: '#E6F2FF',   // Light Blue - Backgrounds
  secondary: '#1A1A1A',       // Dark Gray - Text/Headings
  secondaryLight: '#F5F5F5',  // Light Gray - Backgrounds
  accent: '#00D4AA',          // Teal - Success/Confirmation
  accentHover: '#00A888',     // Darker Teal - Hover

  // Functional colors
  success: '#10B981',
  warning: '#F59E0B',
  error: '#EF4444',
  info: '#3B82F6',
};
```

**Business Domain Color Palettes:**

| Domain | Primary | Secondary | Accent | Warmth |
|--------|---------|-----------|---------|---------|
| Individual Insurance | #6366F1 (Indigo) | #4F46E5 | #8B5CF6 | #F59E0B |
| Corporate Insurance | #0D9488 (Teal) | #0F766E | #14B8A6 | #F59E0B |
| Insurance Core | #4338CA (Deep Indigo) | #3730A3 | #6366F1 | #10B981 |
| Claims Automation | #DC2626 (Deep Red) | #B91C1C | #EF4444 | #F59E0B |
| Mechanics | #059669 (Emerald) | #047857 | #10B981 | #F59E0B |
| Partners Towing | #D97706 (Amber) | #B45309 | #FBBF24 | #3B82F6 |
| Vendors Ecommerce | #7C3AED (Purple) | #6D28D9 | #8B5CF6 | #F59E0B |

#### Theme Modes

**Light Mode:**
```typescript
export const LIGHT_THEME = {
  background: {
    default: '#FFFFFF',
    paper: '#F9FAFB',
    elevated: '#FFFFFF',
  },
  text: {
    primary: '#1F2937',
    secondary: '#374151',
    disabled: '#9CA3AF',
  },
  border: '#E5E7EB',
};
```

**Dark Mode:**
```typescript
export const DARK_THEME = {
  background: {
    default: '#111827',
    paper: '#1F2937',
    elevated: '#1F2937',
  },
  text: {
    primary: '#F9FAFB',
    secondary: '#E5E7EB',
    disabled: '#6B7280',
  },
  border: '#374151',
};
```

**Critical Mode (Emergency):**
```typescript
export const CRITICAL_THEME = {
  background: {
    default: '#FEF2F2',
    paper: '#FFFFFF',
  },
  criticalAction: '#DC2626',
  mutedOpacity: 0.5,
};
```

#### Typography

**Fonts:**
- Inter (UI, headings, body text)
- JetBrains Mono (data displays, code, numbers)

**Scale:**
```typescript
export const TYPOGRAPHY_SCALE = {
  h1: { fontSize: '2.5rem', fontWeight: 700, lineHeight: 1.2 },
  h2: { fontSize: '2rem', fontWeight: 600, lineHeight: 1.3 },
  h3: { fontSize: '1.75rem', fontWeight: 600, lineHeight: 1.3 },
  h4: { fontSize: '1.5rem', fontWeight: 600, lineHeight: 1.4 },
  h5: { fontSize: '1.25rem', fontWeight: 500, lineHeight: 1.4 },
  h6: { fontSize: '1rem', fontWeight: 500, lineHeight: 1.5 },
  body1: { fontSize: '1rem', fontWeight: 400, lineHeight: 1.5 },
  body2: { fontSize: '0.875rem', fontWeight: 400, lineHeight: 1.5 },
  button: { fontSize: '0.875rem', fontWeight: 500, lineHeight: 1.4 },
  caption: { fontSize: '0.75rem', fontWeight: 400, lineHeight: 1.4 },
  data: { fontSize: '1rem', fontWeight: 600, lineHeight: 1.2 }, // JetBrains Mono
};
```

#### User Personalization

**Preset Themes:** 4-6 per domain
**Custom Picker:** Constrained to contrast guidelines (4.5:1 minimum)

---

### 2. Components (`components/`)

**Core Components List:**
- Button (Primary, Secondary, Ghost, Destructive, Link variants)
- Card (Elevation variants, hover states)
- Input (Text, Number, Search, Password)
- Select (Single, Multi, Searchable)
- Checkbox (Regular, Indeterminate)
- Radio (Button group variant)
- Switch (Toggle)
- Slider (Range, value display)
- Badge (Status, count, notification)
- Avatar (Image, initials, fallback)
- Chip (Deletable, avatar)
- Tabs (Scrollable, full-width)
- Accordion (Collapsible panels)
- Alert (Success, warning, error, info variants)
- Dialog (Modal, confirmation, full-screen)
- Drawer (Side panel)
- Menu (Dropdown, context)
- Progress (Linear, circular, indeterminate)
- Skeleton (Loading placeholder)
- Tooltip (Position variants)

**Animation Standards:**
- Duration: 100-300ms for micro-interactions
- Easing: cubic-bezier(0.4, 0, 0.2, 1)
- Target: 60fps

---

### 3. Layouts (`layouts/`)

**Layout Components:**
- **AppLayout**: Main application shell with header, sidebar, footer
- **AuthLayout**: Login/register pages (centered content)
- **DashboardLayout**: Dashboard-specific layout with sidebar navigation
- **PublicLayout**: Public pages with minimal branding

**Header:**
- Logo (responsive: full on desktop, icon on mobile)
- Navigation menu
- User dropdown (profile, settings, logout)
- Notifications indicator
- Theme toggle

**Sidebar:**
- Collapsible navigation
- Domain-specific menu items
- Active state indicators
- Breadcrumb integration

**Footer:**
- Copyright
- Links (Privacy, Terms, Support)
- Social media links
- Monochrome logo

---

### 4. Data Display (`data-display/`)

**DataTable Features:**
- Virtual scrolling (large datasets)
- Column sorting
- Row filtering
- Pagination
- Export (CSV, Excel, PDF)
- Row selection
- Expandable rows
- Responsive columns

**DataGrid Features:**
- MUI X Data Grid
- Custom cell renderers
- Column pinning
- Column grouping
- Editing mode

**Charts:**
- Line Chart (trends, time series)
- Bar Chart (comparisons)
- Pie Chart (distributions)
- Area Chart (cumulative data)
- Custom themes per domain

**Map:**
- Leaflet integration
- Custom markers (dispatch locations, incidents)
- Real-time updates
- Clustering (multiple points)
- Heat map option

**StatCard:**
- KPI display
- Trend indicator (up/down arrow, percentage)
- Sparkline (optional)
- Click-to-drill-down

---

### 5. Real-Time (`real-time/`)

**WebSocket Hook:**
```typescript
interface WebSocketOptions {
  url: string;
  onMessage: (data: any) => void;
  onError?: (error: Event) => void;
  reconnectInterval?: number; // Default: 5000ms
  maxRetries?: number; // Default: Infinity
}
```

**Polling Hook:**
```typescript
interface PollingOptions {
  endpoint: string;
  interval: number; // 10-30s for standard data
  enabled: boolean;
  onSuccess?: (data: any) => void;
  onError?: (error: Error) => void;
}
```

**Connection Manager:**
- Connection status tracking
- Auto-reconnect with exponential backoff
- Fallback to polling on WebSocket failure
- Heartbeat detection
- Queue messages during disconnection

**Event Bus:**
```typescript
// Publish/subscribe pattern
eventBus.publish('dispatch:created', data);
eventBus.subscribe('dispatch:created', handler);
eventBus.unsubscribe('dispatch:created', handler);
```

---

### 6. Dashboards (`dashboards/`)

**Global HQ Dashboard:**
- Overview cards (total dispatches, active incidents, online partners)
- World map with country overlays
- Country drill-down links
- Recent alerts feed
- System health indicators

**Country Dashboard:**
- Local KPIs
- Regional map
- Active dispatches table
- Partner availability
- Incident history

**Real-Time Tracker:**
- Live map with moving markers
- Dispatch cards (real-time status)
- ETA calculations
- Partner status indicators

**Analytics Dashboard:**
- Trend charts (dispatches, claims, incidents)
- Performance metrics
- Resource utilization
- Comparison views (period-over-period)

**Widgets:**
- KPICard (metric, value, trend)
- LiveMap (real-time tracking)
- ActivityFeed (recent events)
- AlertPanel (critical alerts)
- StatusPanel (system health)

---

### 7. Brand Assets (`brand-assets/`)

**Logo Components:**
```typescript
interface BrandLogoProps {
  variant?: 'primary' | 'icon' | 'monochrome';
  size?: 'small' | 'medium' | 'large';
  mode?: 'light' | 'dark';
  onClick?: () => void;
}
```

**Logo Placement Rules:**
| Location | Variant | Size |
|----------|----------|------|
| Header/Navbar | Primary (desktop) / Icon (mobile) | 32-40px height |
| Login/Splash | Primary + Tagline | 120-160px width |
| Footer | Monochrome | 24px height |
| Favicon | Icon | 16x16px, 32x32px |
| Email | Monochrome | 120px width |

**Icon Library:**
- 500+ SVG icons
- Categories: Navigation, Actions, Status, Business, Maps, Vehicles, Insurance, etc.
- Size variants: 16px, 20px, 24px, 32px
- Theme-aware coloring

---

## Integration Points

### Foundation Services

| Service | Port | Purpose |
|---------|------|---------|
| Identity-Access-Service | 8081 | Auth, user profiles, sessions |
| Config-Service | 8888 | App settings, global configs |
| Monitoring-Service | 8091 | Metrics, dashboards, alerts |
| Notification-Service | 8010 | Push notifications, alerts |
| Policy-Engine-Service | 8020 | Business rules, fraud detection |
| Event-Stream-Service | 8010 | Kafka events, messaging |

### API Gateway

```typescript
export const API_GATEWAY = {
  baseUrl: process.env.VITE_API_GATEWAY_URL || 'https://api.gogidix.com',
  routes: {
    foundation: '/api/v1/foundation',
    management: '/api/v1/management',
    business: '/api/v1/business',
    insurance: '/api/v1/insurance',
  },
};
```

---

## Performance Requirements

| Metric | Target | Measurement |
|--------|--------|-------------|
| Lighthouse Performance | > 90 | Automated audit |
| Lighthouse Accessibility | > 95 | Automated audit |
| Lighthouse Best Practices | > 90 | Automated audit |
| First Contentful Paint | < 1.5s | Lighthouse |
| Time to Interactive | < 3s | Lighthouse |
| Largest Contentful Paint | < 2.5s | Lighthouse |
| Cumulative Layout Shift | < 0.1 | Lighthouse |
| Critical Data Latency | < 1s | WebSocket timing |
| Standard Data Latency | < 2s | Polling timing |
| Animation FPS | 60 | Framer Motion |
| Bundle Size | < 200KB gzipped | Build output |
| Build Time | < 5 min | CI/CD |

---

## Accessibility Standards

### WCAG AA Compliance

- **Contrast:** Minimum 4.5:1 for normal text, 3:1 for large text
- **Keyboard:** All interactive elements keyboard accessible
- **Screen Reader:** ARIA labels, live regions, announcements
- **Focus:** Visible focus indicators (outline: 2px solid)
- **Motion:** Respect prefers-reduced-motion
- **Touch:** Minimum 44x44px tap targets

### Testing Tools
- axe-core (automated)
- Lighthouse (automated)
- Manual keyboard testing
- Screen reader testing (NVDA, JAWS)

---

## Testing Strategy

### Unit Tests (Vitest)
- Component unit tests
- Utility function tests
- Custom hook tests
- Target: > 80% coverage

### Integration Tests (Testing Library)
- Component interaction tests
- Form validation tests
- State management tests

### E2E Tests (Playwright)
- Critical user flows
- Cross-browser (Chrome, Firefox, Safari, Edge)
- Target: > 80% coverage

### Visual Regression (Chromatic - optional)
- Component screenshots
- Theme variants
- Responsive views

### Performance Testing (Lighthouse CI)
- Every PR
- All pages
- Performance budget enforcement

---

## Deployment Architecture

### Docker
- Multi-stage builds
- Alpine base images for production
- Layer caching optimization

### Kubernetes
- Helm charts for easy deployment
- Horizontal pod autoscaling
- Resource limits and requests
- Health checks and probes

### CI/CD Pipeline
- GitHub Actions for PR validation
- Azure Pipelines for production deployment
- Jenkins for legacy support

### Deployment Strategy
- Blue-green deployments (zero downtime)
- Automated rollback on failure
- Canary releases for critical changes

---

## Security Considerations

- All external dependencies vetted
- No npm scripts with `pre` or `post` hooks
- Content Security Policy headers
- XSS protection in all inputs
- CSRF tokens for mutations
- Rate limiting on API calls

---

## Monitoring & Observability

### Metrics (Prometheus)
- Component render times
- API call latency
- Error rates
- Bundle sizes

### Logs (ELK Stack)
- Structured JSON logs
- Correlation IDs
- User context

### Dashboards (Grafana)
- Real-time performance
- Error tracking
- Usage analytics

---

## Success Metrics

### Code Quality
- TypeScript strict mode: 100%
- ESLint warnings: 0
- Test coverage: > 80%
- Bundle size: < 200KB gzipped

### Performance
- Lighthouse score: > 90
- FCP: < 1.5s
- TTI: < 3s
- API latency: < 1s (critical), < 2s (standard)

### User Experience
- WCAG AA: Full compliance
- Animations: 60fps
- Load time: < 3s

### Business Impact
- 15+ domains using the library
- Consistent brand across all apps
- 90%+ user satisfaction

---

## Appendix: Domain Configurations

### Department Ports
```typescript
export const DEPARTMENT_PORTS = {
  // Management Domain
  'country-admin': 8501,
  'customer-support': 8502,
  'executive-command': 8503,
  'finance-settlement': 8504,
  'global-admin': 8505,
  'hr': 8506,
  'pricing-policy': 8507,
  'sales': 8508,
  'shared-services': 8509,
  'digital-marketing': 8510,
  'compliance-risk': 8511,

  // Business Domain
  'mechanics': 8520,
  'partners-towing': 8521,
  'vendors-ecommerce': 8522,
  'central-monitoring': 8523,
  'individual-insurance-customer': 8524,
  'corporate-insurance-customer': 8525,

  // Insurance Domain
  'business-applications': 8601,
  'claims-assistance': 8602,
  'compliance-risk-insurance': 8603,
  'customer-distribution': 8604,
  'finance-settlement-insurance': 8605,
  'insurance-core': 8606,
  'operations-insurance': 8607,
  'pricing-underwriting': 8608,
};
```

---

**Document Version:** 1.0
**Last Updated:** 2026-03-31
**Status:** Ready for Implementation
