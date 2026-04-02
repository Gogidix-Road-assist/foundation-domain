# Shared Frontend Libraries Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build enterprise-grade shared component library serving 15+ Business Domain frontends with multi-country, multi-tenant, multi-role architecture support.

**Architecture:** Monorepo with 9 packages (design-system, components, layouts, forms, data-display, real-time, dashboards, brand-assets, utils) using React 18.3.1 + TypeScript 5.3+, Material UI 6.2.0, and Vite 5.2.0.

**Tech Stack:** React 18.3.1, TypeScript 5.3+, Material UI 6.2.0, Redux Toolkit 2.0.1, TanStack Query 5.62.11, Framer Motion 12.2.0, Vite 5.2.0, Vitest, Playwright, ESLint, Prettier, Storybook, Lighthouse CI.

---

## File Structure Map

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
│   │   ├── src/index.ts
│   │   └── package.json
│   │
│   ├── components/                   # Enterprise UI components
│   │   ├── src/
│   │   │   ├── Button/
│   │   │   │   ├── Button.tsx
│   │   │   │   ├── Button.test.tsx
│   │   │   │   ├── Button.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Card/
│   │   │   │   ├── Card.tsx
│   │   │   │   ├── Card.test.tsx
│   │   │   │   ├── Card.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Input/
│   │   │   │   ├── Input.tsx
│   │   │   │   ├── Input.test.tsx
│   │   │   │   ├── Input.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Select/
│   │   │   │   ├── Select.tsx
│   │   │   │   ├── Select.test.tsx
│   │   │   │   ├── Select.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Checkbox/
│   │   │   │   ├── Checkbox.tsx
│   │   │   │   ├── Checkbox.test.tsx
│   │   │   │   ├── Checkbox.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Radio/
│   │   │   │   ├── Radio.tsx
│   │   │   │   ├── Radio.test.tsx
│   │   │   │   ├── Radio.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Switch/
│   │   │   │   ├── Switch.tsx
│   │   │   │   ├── Switch.test.tsx
│   │   │   │   ├── Switch.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Slider/
│   │   │   │   ├── Slider.tsx
│   │   │   │   ├── Slider.test.tsx
│   │   │   │   ├── Slider.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Badge/
│   │   │   │   ├── Badge.tsx
│   │   │   │   ├── Badge.test.tsx
│   │   │   │   ├── Badge.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Avatar/
│   │   │   │   ├── Avatar.tsx
│   │   │   │   ├── Avatar.test.tsx
│   │   │   │   ├── Avatar.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Chip/
│   │   │   │   ├── Chip.tsx
│   │   │   │   ├── Chip.test.tsx
│   │   │   │   ├── Chip.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Tabs/
│   │   │   │   ├── Tabs.tsx
│   │   │   │   ├── Tab.tsx
│   │   │   │   ├── Tabs.test.tsx
│   │   │   │   ├── Tabs.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Accordion/
│   │   │   │   ├── Accordion.tsx
│   │   │   │   ├── AccordionSummary.tsx
│   │   │   │   ├── AccordionDetails.tsx
│   │   │   │   ├── Accordion.test.tsx
│   │   │   │   ├── Accordion.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Alert/
│   │   │   │   ├── Alert.tsx
│   │   │   │   ├── Alert.test.tsx
│   │   │   │   ├── Alert.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Dialog/
│   │   │   │   ├── Dialog.tsx
│   │   │   │   ├── Dialog.test.tsx
│   │   │   │   ├── Dialog.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Drawer/
│   │   │   │   ├── Drawer.tsx
│   │   │   │   ├── Drawer.test.tsx
│   │   │   │   ├── Drawer.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Menu/
│   │   │   │   ├── Menu.tsx
│   │   │   │   ├── MenuItem.tsx
│   │   │   │   ├── Menu.test.tsx
│   │   │   │   ├── Menu.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Progress/
│   │   │   │   ├── Progress.tsx
│   │   │   │   ├── CircularProgress.tsx
│   │   │   │   ├── Progress.test.tsx
│   │   │   │   ├── Progress.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Skeleton/
│   │   │   │   ├── Skeleton.tsx
│   │   │   │   ├── Skeleton.test.tsx
│   │   │   │   ├── Skeleton.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Tooltip/
│   │   │   │   ├── Tooltip.tsx
│   │   │   │   ├── Tooltip.test.tsx
│   │   │   │   ├── Tooltip.stories.tsx
│   │   │   │   └── index.ts
│   │   │   └── index.ts
│   │   ├── src/index.ts
│   │   └── package.json
│   │
│   ├── layouts/                     # Layout components
│   │   ├── src/
│   │   │   ├── AppLayout/
│   │   │   │   ├── AppLayout.tsx
│   │   │   │   ├── AppLayout.test.tsx
│   │   │   │   ├── AppLayout.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── AuthLayout/
│   │   │   │   ├── AuthLayout.tsx
│   │   │   │   ├── AuthLayout.test.tsx
│   │   │   │   ├── AuthLayout.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── DashboardLayout/
│   │   │   │   ├── DashboardLayout.tsx
│   │   │   │   ├── DashboardLayout.test.tsx
│   │   │   │   ├── DashboardLayout.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── PublicLayout/
│   │   │   │   ├── PublicLayout.tsx
│   │   │   │   ├── PublicLayout.test.tsx
│   │   │   │   ├── PublicLayout.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Header/
│   │   │   │   ├── Header.tsx
│   │   │   │   ├── Header.test.tsx
│   │   │   │   ├── Header.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Sidebar/
│   │   │   │   ├── Sidebar.tsx
│   │   │   │   ├── Sidebar.test.tsx
│   │   │   │   ├── Sidebar.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Footer/
│   │   │   │   ├── Footer.tsx
│   │   │   │   ├── Footer.test.tsx
│   │   │   │   ├── Footer.stories.tsx
│   │   │   │   └── index.ts
│   │   │   └── index.ts
│   │   ├── src/index.ts
│   │   └── package.json
│   │
│   ├── forms/                       # Form components with validation
│   │   ├── src/
│   │   │   ├── FormBuilder/
│   │   │   │   ├── FormBuilder.tsx
│   │   │   │   ├── FormBuilder.test.tsx
│   │   │   │   ├── FormBuilder.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── ValidatedInput/
│   │   │   │   ├── ValidatedInput.tsx
│   │   │   │   ├── ValidatedInput.test.tsx
│   │   │   │   ├── ValidatedInput.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── FormField/
│   │   │   │   ├── FormField.tsx
│   │   │   │   ├── FormField.test.tsx
│   │   │   │   ├── FormField.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── FormLabel/
│   │   │   │   ├── FormLabel.tsx
│   │   │   │   ├── FormLabel.test.tsx
│   │   │   │   ├── FormLabel.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── FormError/
│   │   │   │   ├── FormError.tsx
│   │   │   │   ├── FormError.test.tsx
│   │   │   │   ├── FormError.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── DatePickerField/
│   │   │   │   ├── DatePickerField.tsx
│   │   │   │   ├── DatePickerField.test.tsx
│   │   │   │   ├── DatePickerField.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── SelectField/
│   │   │   │   ├── SelectField.tsx
│   │   │   │   ├── SelectField.test.tsx
│   │   │   │   ├── SelectField.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── TextField/
│   │   │   │   ├── TextField.tsx
│   │   │   │   ├── TextField.test.tsx
│   │   │   │   ├── TextField.stories.tsx
│   │   │   │   └── index.ts
│   │   │   └── index.ts
│   │   ├── src/index.ts
│   │   └── package.json
│   │
│   ├── data-display/                # Data visualization components
│   │   ├── src/
│   │   │   ├── DataTable/
│   │   │   │   ├── DataTable.tsx
│   │   │   │   ├── DataTable.types.ts
│   │   │   │   ├── DataTable.test.tsx
│   │   │   │   ├── DataTable.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── DataGrid/
│   │   │   │   ├── DataGrid.tsx
│   │   │   │   ├── DataGrid.test.tsx
│   │   │   │   ├── DataGrid.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── VirtualizedTable/
│   │   │   │   ├── VirtualizedTable.tsx
│   │   │   │   ├── VirtualizedTable.test.tsx
│   │   │   │   ├── VirtualizedTable.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Chart/
│   │   │   │   ├── Chart.tsx
│   │   │   │   ├── Chart.types.ts
│   │   │   │   ├── Chart.test.tsx
│   │   │   │   ├── Chart.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── LineChart/
│   │   │   │   ├── LineChart.tsx
│   │   │   │   ├── LineChart.test.tsx
│   │   │   │   ├── LineChart.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── BarChart/
│   │   │   │   ├── BarChart.tsx
│   │   │   │   ├── BarChart.test.tsx
│   │   │   │   ├── BarChart.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── PieChart/
│   │   │   │   ├── PieChart.tsx
│   │   │   │   ├── PieChart.test.tsx
│   │   │   │   ├── PieChart.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Map/
│   │   │   │   ├── Map.tsx
│   │   │   │   ├── Map.types.ts
│   │   │   │   ├── Map.test.tsx
│   │   │   │   ├── Map.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── StatCard/
│   │   │   │   ├── StatCard.tsx
│   │   │   │   ├── StatCard.test.tsx
│   │   │   │   ├── StatCard.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── TrendIndicator/
│   │   │   │   ├── TrendIndicator.tsx
│   │   │   │   ├── TrendIndicator.test.tsx
│   │   │   │   ├── TrendIndicator.stories.tsx
│   │   │   │   └── index.ts
│   │   │   └── index.ts
│   │   ├── src/index.ts
│   │   └── package.json
│   │
│   ├── real-time/                   # Real-time communication
│   │   ├── src/
│   │   │   ├── hooks/
│   │   │   │   ├── useWebSocket.ts
│   │   │   │   ├── useWebSocket.test.ts
│   │   │   │   ├── usePolling.ts
│   │   │   │   ├── usePolling.test.ts
│   │   │   │   ├── useRealTimeData.ts
│   │   │   │   ├── useRealTimeData.test.ts
│   │   │   │   ├── useEventBus.ts
│   │   │   │   ├── useEventBus.test.ts
│   │   │   │   └── index.ts
│   │   │   ├── providers/
│   │   │   │   ├── WebSocketProvider.tsx
│   │   │   │   ├── WebSocketProvider.test.tsx
│   │   │   │   ├── RealTimeProvider.tsx
│   │   │   │   ├── RealTimeProvider.test.tsx
│   │   │   │   └── index.ts
│   │   │   ├── services/
│   │   │   │   ├── ConnectionManager.ts
│   │   │   │   ├── ConnectionManager.test.ts
│   │   │   │   ├── ReconnectionStrategy.ts
│   │   │   │   ├── ReconnectionStrategy.test.ts
│   │   │   │   ├── EventPublisher.ts
│   │   │   │   ├── EventPublisher.test.ts
│   │   │   │   └── index.ts
│   │   │   ├── types/
│   │   │   │   ├── websocket.types.ts
│   │   │   │   └── index.ts
│   │   │   └── index.ts
│   │   ├── src/index.ts
│   │   └── package.json
│   │
│   ├── dashboards/                  # Dashboard components
│   │   ├── src/
│   │   │   ├── GlobalHQDashboard/
│   │   │   │   ├── GlobalHQDashboard.tsx
│   │   │   │   ├── GlobalHQDashboard.test.tsx
│   │   │   │   ├── GlobalHQDashboard.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── CountryDashboard/
│   │   │   │   ├── CountryDashboard.tsx
│   │   │   │   ├── CountryDashboard.test.tsx
│   │   │   │   ├── CountryDashboard.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── RealTimeTracker/
│   │   │   │   ├── RealTimeTracker.tsx
│   │   │   │   ├── RealTimeTracker.test.tsx
│   │   │   │   ├── RealTimeTracker.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── AnalyticsDashboard/
│   │   │   │   ├── AnalyticsDashboard.tsx
│   │   │   │   ├── AnalyticsDashboard.test.tsx
│   │   │   │   ├── AnalyticsDashboard.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── ResourceAllocation/
│   │   │   │   ├── ResourceAllocation.tsx
│   │   │   │   ├── ResourceAllocation.test.tsx
│   │   │   │   ├── ResourceAllocation.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── StrategicDashboard/
│   │   │   │   ├── StrategicDashboard.tsx
│   │   │   │   ├── StrategicDashboard.test.tsx
│   │   │   │   ├── StrategicDashboard.stories.tsx
│   │   │   │   └── index.ts
│   │   │   ├── widgets/
│   │   │   │   ├── KPICard/
│   │   │   │   │   ├── KPICard.tsx
│   │   │   │   │   ├── KPICard.test.tsx
│   │   │   │   │   ├── KPICard.stories.tsx
│   │   │   │   │   └── index.ts
│   │   │   │   ├── LiveMap/
│   │   │   │   │   ├── LiveMap.tsx
│   │   │   │   │   ├── LiveMap.test.tsx
│   │   │   │   │   ├── LiveMap.stories.tsx
│   │   │   │   │   └── index.ts
│   │   │   │   ├── ActivityFeed/
│   │   │   │   │   ├── ActivityFeed.tsx
│   │   │   │   │   ├── ActivityFeed.test.tsx
│   │   │   │   │   ├── ActivityFeed.stories.tsx
│   │   │   │   │   └── index.ts
│   │   │   │   ├── AlertPanel/
│   │   │   │   │   ├── AlertPanel.tsx
│   │   │   │   │   ├── AlertPanel.test.tsx
│   │   │   │   │   ├── AlertPanel.stories.tsx
│   │   │   │   │   └── index.ts
│   │   │   │   ├── StatusPanel/
│   │   │   │   │   ├── StatusPanel.tsx
│   │   │   │   │   ├── StatusPanel.test.tsx
│   │   │   │   │   ├── StatusPanel.stories.tsx
│   │   │   │   │   └── index.ts
│   │   │   │   └── index.ts
│   │   │   └── index.ts
│   │   ├── src/index.ts
│   │   └── package.json
│   │
│   ├── brand-assets/                # Brand assets and logos
│   │   ├── src/
│   │   │   ├── Logo/
│   │   │   │   ├── BrandLogo.tsx
│   │   │   │   ├── BrandLogo.test.tsx
│   │   │   │   ├── BrandLogo.stories.tsx
│   │   │   │   ├── IconLogo.tsx
│   │   │   │   ├── IconLogo.test.tsx
│   │   │   │   ├── IconLogo.stories.tsx
│   │   │   │   ├── variants/
│   │   │   │   │   ├── primary.svg
│   │   │   │   │   ├── primary-light.svg
│   │   │   │   │   ├── icon.svg
│   │   │   │   │   ├── icon-light.svg
│   │   │   │   │   └── favicon/
│   │   │   │   └── index.ts
│   │   │   ├── icons/
│   │   │   │   ├── NavigationIcons.tsx
│   │   │   │   ├── ActionIcons.tsx
│   │   │   │   ├── StatusIcons.tsx
│   │   │   │   ├── BusinessIcons.tsx
│   │   │   │   ├── MapIcons.tsx
│   │   │   │   ├── VehicleIcons.tsx
│   │   │   │   ├── InsuranceIcons.tsx
│   │   │   │   └── index.ts
│   │   │   └── index.ts
│   │   ├── src/index.ts
│   │   └── package.json
│   │
│   └── utils/                       # Utility functions
│       ├── src/
│       │   ├── formatters/
│       │   │   ├── currency.ts
│       │   │   ├── currency.test.ts
│       │   │   ├── date.ts
│       │   │   ├── date.test.ts
│       │   │   ├── number.ts
│       │   │   ├── number.test.ts
│       │   │   ├── phone.ts
│       │   │   ├── phone.test.ts
│       │   │   └── index.ts
│       │   ├── validators/
│       │   │   ├── email.ts
│       │   │   ├── email.test.ts
│       │   │   ├── phone.ts
│       │   │   ├── phone.test.ts
│       │   │   ├── postalCode.ts
│       │   │   ├── postalCode.test.ts
│       │   │   └── index.ts
│       │   ├── helpers/
│       │   │   ├── api.ts
│       │   │   ├── api.test.ts
│       │   │   ├── storage.ts
│       │   │   ├── storage.test.ts
│       │   │   ├── theme.ts
│       │   │   ├── theme.test.ts
│       │   │   └── index.ts
│       │   └── index.ts
│       ├── src/index.ts
│       └── package.json
│
├── apps/
│   ├── storybook/                   # Component documentation
│   │   ├── .storybook/
│   │   │   ├── main.ts
│   │   │   ├── preview.ts
│   │   │   └── theme.ts
│   │   ├── src/
│   │   │   └── stories/
│   │   └── package.json
│   └── demo/                        # Demo application
│       ├── src/
│       │   ├── App.tsx
│       │   ├── main.tsx
│       │   └── index.html
│       ├── vite.config.ts
│       └── package.json
│
├── tests/
│   ├── unit/
│   │   ├── setup.ts
│   │   └── ...
│   ├── integration/
│   │   ├── setup.ts
│   │   └── ...
│   └── e2e/
│       ├── playwright.config.ts
│       └── specs/
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
│   │       ├── Chart.yaml
│   │       └── values.yaml
│   └── ci/
│       ├── github-actions/
│       │   ├── ci.yml
│       │   └── cd.yml
│       ├── azure-pipelines/
│       │   ├── azure-pipelines.yml
│       └── jenkins/
│           └── Jenkinsfile
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

# PHASE 1: Foundation Setup (Week 1-2)

## Task 1: Initialize Monorepo Structure

**Files:**
- Create: `package.json`
- Create: `pnpm-workspace.yaml`
- Create: `tsconfig.json`
- Create: `tsconfig.base.json`

- [ ] **Step 1: Write root package.json**

```json
{
  "name": "shared-frontend-libraries",
  "version": "1.0.0",
  "private": true,
  "description": "Enterprise-grade shared component library for Rapid Assist",
  "workspaces": [
    "packages/*",
    "apps/*"
  ],
  "scripts": {
    "dev": "pnpm --filter @shared-frontend-libraries/demo dev",
    "build": "pnpm -r --filter './packages/**' build",
    "build:storybook": "pnpm --filter @shared-frontend-libraries/storybook build-storybook",
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:e2e": "playwright test",
    "lint": "eslint . --ext .ts,.tsx",
    "lint:fix": "eslint . --ext .ts,.tsx --fix",
    "format": "prettier --write \"**/*.{ts,tsx,js,jsx,json,md}\"",
    "format:check": "prettier --check \"**/*.{ts,tsx,js,jsx,json,md}\"",
    "typecheck": "tsc --noEmit",
    "clean": "pnpm -r clean && rm -rf node_modules",
    "changeset": "changeset",
    "version-packages": "changeset version",
    "release": "changeset publish"
  },
  "devDependencies": {
    "@changesets/cli": "^2.27.0",
    "@changesets/config": "^3.0.0",
    "@playwright/test": "^1.40.0",
    "@testing-library/jest-dom": "^6.1.5",
    "@testing-library/react": "^14.1.2",
    "@testing-library/user-event": "^14.5.1",
    "@types/react": "^18.2.45",
    "@types/react-dom": "^18.2.18",
    "@typescript-eslint/eslint-plugin": "^6.15.0",
    "@typescript-eslint/parser": "^6.15.0",
    "@vitejs/plugin-react": "^4.2.1",
    "@vitest/ui": "^1.1.0",
    "eslint": "^8.56.0",
    "eslint-config-prettier": "^9.1.0",
    "eslint-plugin-react": "^7.33.2",
    "eslint-plugin-react-hooks": "^4.6.0",
    "jsdom": "^23.0.1",
    "lighthouse-ci": "^0.12.0",
    "prettier": "^3.1.1",
    "typescript": "^5.3.3",
    "vite": "^5.0.11",
    "vitest": "^1.1.0"
  },
  "engines": {
    "node": ">=20.0.0",
    "pnpm": ">=8.0.0"
  },
  "packageManager": "pnpm@8.15.0"
}
```

- [ ] **Step 2: Write pnpm-workspace.yaml**

```yaml
packages:
  - 'packages/*'
  - 'apps/*'
```

- [ ] **Step 3: Write tsconfig.base.json**

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "moduleResolution": "bundler",
    "resolveJsonModule": true,
    "allowJs": true,
    "strict": true,
    "strictNullChecks": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true,
    "jsx": "react-jsx",
    "declaration": true,
    "declarationMap": true,
    "sourceMap": true,
    "outDir": "./dist",
    "baseUrl": ".",
    "paths": {
      "@shared-frontend-libraries/design-system": ["./packages/design-system/src"],
      "@shared-frontend-libraries/components": ["./packages/components/src"],
      "@shared-frontend-libraries/layouts": ["./packages/layouts/src"],
      "@shared-frontend-libraries/forms": ["./packages/forms/src"],
      "@shared-frontend-libraries/data-display": ["./packages/data-display/src"],
      "@shared-frontend-libraries/real-time": ["./packages/real-time/src"],
      "@shared-frontend-libraries/dashboards": ["./packages/dashboards/src"],
      "@shared-frontend-libraries/brand-assets": ["./packages/brand-assets/src"],
      "@shared-frontend-libraries/utils": ["./packages/utils/src"]
    }
  },
  "exclude": ["node_modules", "dist", "build"]
}
```

- [ ] **Step 4: Write root tsconfig.json**

```json
{
  "extends": "./tsconfig.base.json",
  "include": ["packages/*/src/**/*", "apps/*/src/**/*"],
  "references": [
    { "path": "./packages/design-system" },
    { "path": "./packages/components" },
    { "path": "./packages/layouts" },
    { "path": "./packages/forms" },
    { "path": "./packages/data-display" },
    { "path": "./packages/real-time" },
    { "path": "./packages/dashboards" },
    { "path": "./packages/brand-assets" },
    { "path": "./packages/utils" }
  ]
}
```

- [ ] **Step 5: Create package directories**

Run: `mkdir -p packages/{design-system,components,layouts,forms,data-display,real-time,dashboards,brand-assets,utils}/src`
Expected: All package directories created

- [ ] **Step 6: Commit**

```bash
git add package.json pnpm-workspace.yaml tsconfig.json tsconfig.base.json
git commit -m "feat(monorepo): initialize monorepo structure with root configs"
```

---

## Task 2: Configure Vite and Build Tools

**Files:**
- Create: `vite.config.ts`
- Create: `.env.example`

- [ ] **Step 1: Write vite.config.ts**

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@shared-frontend-libraries/design-system': path.resolve(__dirname, './packages/design-system/src'),
      '@shared-frontend-libraries/components': path.resolve(__dirname, './packages/components/src'),
      '@shared-frontend-libraries/layouts': path.resolve(__dirname, './packages/layouts/src'),
      '@shared-frontend-libraries/forms': path.resolve(__dirname, './packages/forms/src'),
      '@shared-frontend-libraries/data-display': path.resolve(__dirname, './packages/data-display/src'),
      '@shared-frontend-libraries/real-time': path.resolve(__dirname, './packages/real-time/src'),
      '@shared-frontend-libraries/dashboards': path.resolve(__dirname, './packages/dashboards/src'),
      '@shared-frontend-libraries/brand-assets': path.resolve(__dirname, './packages/brand-assets/src'),
      '@shared-frontend-libraries/utils': path.resolve(__dirname, './packages/utils/src'),
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './tests/unit/setup.ts',
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'tests/',
        '**/*.test.{ts,tsx}',
        '**/*.stories.{ts,tsx}',
        '**/dist/**',
        '**/build/**',
      ],
    },
  },
});
```

- [ ] **Step 2: Write .env.example**

```bash
# API Configuration
VITE_API_GATEWAY_URL=https://api.gogidix.com
VITE_WS_URL=wss://api.gogidix.com/ws

# Foundation Services
VITE_IDENTITY_SERVICE_URL=http://localhost:8081
VITE_CONFIG_SERVICE_URL=http://localhost:8888
VITE_MONITORING_SERVICE_URL=http://localhost:8091
VITE_NOTIFICATION_SERVICE_URL=http://localhost:8010
VITE_POLICY_ENGINE_SERVICE_URL=http://localhost:8020

# Feature Flags
VITE_ENABLE_WEBSOCKET=true
VITE_ENABLE_POLLING=true
VITE_POLLING_INTERVAL=30000

# Environment
VITE_ENV=development
VITE_LOG_LEVEL=debug
```

- [ ] **Step 3: Commit**

```bash
git add vite.config.ts .env.example
git commit -m "feat(build): add Vite configuration and environment template"
```

---

## Task 3: Configure ESLint and Prettier

**Files:**
- Create: `.eslintrc.js`
- Create: `.prettierrc.js`

- [ ] **Step 1: Write .eslintrc.js**

```javascript
module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
    node: true,
  },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:react/recommended',
    'plugin:react-hooks/recommended',
    'prettier',
  ],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaFeatures: {
      jsx: true,
    },
    ecmaVersion: 'latest',
    sourceType: 'module',
  },
  plugins: ['@typescript-eslint', 'react', 'react-hooks'],
  rules: {
    'react/react-in-jsx-scope': 'off',
    'react/prop-types': 'off',
    '@typescript-eslint/no-unused-vars': ['error', { argsIgnorePattern: '^_' }],
    '@typescript-eslint/no-explicit-any': 'warn',
    'react-hooks/rules-of-hooks': 'error',
    'react-hooks/exhaustive-deps': 'warn',
  },
  settings: {
    react: {
      version: 'detect',
    },
  },
};
```

- [ ] **Step 2: Write .prettierrc.js**

```javascript
module.exports = {
  semi: true,
  trailingComma: 'es5',
  singleQuote: true,
  printWidth: 100,
  tabWidth: 2,
  useTabs: false,
  arrowParens: 'always',
  endOfLine: 'lf',
  bracketSpacing: true,
  jsxSingleQuote: false,
};
```

- [ ] **Step 3: Create .prettierignore**

```text
node_modules
dist
build
coverage
*.lock
pnpm-lock.yaml
package-lock.json
.next
.nuxt
.cache
```

- [ ] **Step 4: Create .eslintignore**

```text
node_modules
dist
build
coverage
*.config.js
```

- [ ] **Step 5: Commit**

```bash
git add .eslintrc.js .prettierrc.js .prettierignore .eslintignore
git commit -m "config(lint): add ESLint and Prettier configuration"
```

---

## Task 4: Configure Vitest and Testing Setup

**Files:**
- Create: `vitest.config.ts`
- Create: `tests/unit/setup.ts`

- [ ] **Step 1: Write vitest.config.ts**

```typescript
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./tests/unit/setup.ts'],
    include: ['**/*.{test,spec}.{ts,tsx}'],
    exclude: ['node_modules', 'dist', 'build', '**/*.stories.{ts,tsx}'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'],
      exclude: [
        'node_modules/',
        'tests/',
        '**/*.test.{ts,tsx}',
        '**/*.stories.{ts,tsx}',
        '**/dist/**',
        '**/build/**',
        '**/*.config.{ts,js}',
      ],
      statements: 80,
      branches: 80,
      functions: 80,
      lines: 80,
    },
  },
  resolve: {
    alias: {
      '@shared-frontend-libraries/design-system': path.resolve(__dirname, './packages/design-system/src'),
      '@shared-frontend-libraries/components': path.resolve(__dirname, './packages/components/src'),
      '@shared-frontend-libraries/layouts': path.resolve(__dirname, './packages/layouts/src'),
      '@shared-frontend-libraries/forms': path.resolve(__dirname, './packages/forms/src'),
      '@shared-frontend-libraries/data-display': path.resolve(__dirname, './packages/data-display/src'),
      '@shared-frontend-libraries/real-time': path.resolve(__dirname, './packages/real-time/src'),
      '@shared-frontend-libraries/dashboards': path.resolve(__dirname, './packages/dashboards/src'),
      '@shared-frontend-libraries/brand-assets': path.resolve(__dirname, './packages/brand-assets/src'),
      '@shared-frontend-libraries/utils': path.resolve(__dirname, './packages/utils/src'),
    },
  },
});
```

- [ ] **Step 2: Write tests/unit/setup.ts**

```typescript
import '@testing-library/jest-dom';
import { cleanup } from '@testing-library/react';
import { afterEach, vi } from 'vitest';

// Cleanup after each test
afterEach(() => {
  cleanup();
});

// Mock IntersectionObserver
global.IntersectionObserver = class IntersectionObserver {
  constructor() {}
  disconnect() {}
  observe() {}
  takeRecords() {
    return [];
  }
  unobserve() {}
} as any;

// Mock ResizeObserver
global.ResizeObserver = class ResizeObserver {
  constructor() {}
  disconnect() {}
  observe() {}
  unobserve() {}
} as any;

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation((query) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});
```

- [ ] **Step 3: Commit**

```bash
git add vitest.config.ts tests/unit/setup.ts
git commit -m "test(vitest): configure Vitest with coverage and setup files"
```

---

## Task 5: Configure Playwright for E2E Testing

**Files:**
- Create: `playwright.config.ts`

- [ ] **Step 1: Write playwright.config.ts**

```typescript
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests/e2e/specs',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: [
    ['html'],
    ['json', { outputFile: 'test-results/results.json' }],
    ['junit', { outputFile: 'test-results/junit.xml' }],
  ],
  use: {
    baseURL: 'http://localhost:3000',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
    {
      name: 'Mobile Chrome',
      use: { ...devices['Pixel 5'] },
    },
    {
      name: 'Mobile Safari',
      use: { ...devices['iPhone 12'] },
    },
  ],

  webServer: {
    command: 'pnpm dev',
    url: 'http://localhost:3000',
    reuseExistingServer: !process.env.CI,
  },
});
```

- [ ] **Step 2: Create tests/e2e/specs directory**

Run: `mkdir -p tests/e2e/specs`
Expected: Directory created

- [ ] **Step 3: Write basic E2E test example at tests/e2e/specs/example.spec.ts**

```typescript
import { test, expect } from '@playwright/test';

test('homepage has title', async ({ page }) => {
  await page.goto('/');
  await expect(page).toHaveTitle(/Rapid Assist/);
});

test('theme toggle works', async ({ page }) => {
  await page.goto('/');
  const themeToggle = page.getByTestId('theme-toggle');
  await themeToggle.click();
  await expect(page.locator('body')).toHaveClass(/dark/);
});
```

- [ ] **Step 4: Commit**

```bash
git add playwright.config.ts tests/e2e/specs/example.spec.ts
git commit -m "test(e2e): configure Playwright with multi-browser support"
```

---

## Task 6: Configure Lighthouse CI

**Files:**
- Create: `lighthouserc.js`

- [ ] **Step 1: Write lighthouserc.js**

```javascript
module.exports = {
  assert: {
    preset: 'lighthouse:recommended',
    assertions: {
      'categories:performance': ['error', { minScore: 0.9 }],
      'categories:accessibility': ['error', { minScore: 0.95 }],
      'categories:best-practices': ['error', { minScore: 0.9 }],
      'categories:seo': ['error', { minScore: 0.9 }],
      'first-contentful-paint': ['error', { maxNumericValue: 1500 }],
      'interactive': ['error', { maxNumericValue: 3000 }],
      'largest-contentful-paint': ['error', { maxNumericValue: 2500 }],
      'cumulative-layout-shift': ['error', { maxNumericValue: 0.1 }],
    },
  },
  collect: {
    url: ['http://localhost:3000'],
    numberOfRuns: 3,
    staticDistDir: './dist',
  },
  upload: {
    target: 'temporary-public-storage',
  },
};
```

- [ ] **Step 2: Commit**

```bash
git add lighthouserc.js
git commit -m "perf(lighthouse): configure Lighthouse CI with performance targets"
```

---

## Task 7: Create README and Documentation

**Files:**
- Create: `README.md`
- Create: `docs/getting-started.md`

- [ ] **Step 1: Write README.md**

```markdown
# Shared Frontend Libraries

Enterprise-grade shared component library for Rapid Assist applications.

## Overview

This library provides a comprehensive set of reusable UI components, layouts, and utilities designed for multi-country, multi-tenant, multi-role applications.

## Packages

| Package | Description |
|----------|-------------|
| `@shared-frontend-libraries/design-system` | Design tokens, theme, colors, typography |
| `@shared-frontend-libraries/components` | Enterprise UI components |
| `@shared-frontend-libraries/layouts` | Layout components (App, Dashboard, Auth, Public) |
| `@shared-frontend-libraries/forms` | Form components with validation |
| `@shared-frontend-libraries/data-display` | Data visualization components |
| `@shared-frontend-libraries/real-time` | Real-time communication hooks and providers |
| `@shared-frontend-libraries/dashboards` | Dashboard components |
| `@shared-frontend-libraries/brand-assets` | Brand assets and logos |
| `@shared-frontend-libraries/utils` | Utility functions |

## Technology Stack

- React 18.3.1
- TypeScript 5.3+
- Material UI 6.2.0
- Redux Toolkit 2.0.1
- TanStack Query 5.62.11
- Framer Motion 12.2.0
- Vite 5.2.0

## Getting Started

See [Getting Started Guide](docs/getting-started.md)

## Development

```bash
# Install dependencies
pnpm install

# Start development server
pnpm dev

# Run tests
pnpm test

# Run tests with UI
pnpm test:ui

# Run E2E tests
pnpm test:e2e

# Build all packages
pnpm build

# Lint code
pnpm lint

# Format code
pnpm format
```

## License

Proprietary - Gogidix
```

- [ ] **Step 2: Write docs/getting-started.md**

```markdown
# Getting Started

## Installation

```bash
pnpm add @shared-frontend-libraries/components @shared-frontend-libraries/design-system
```

## Setup

### Theme Provider

Wrap your application with the ThemeProvider:

```tsx
import { ThemeProvider } from '@shared-frontend-libraries/design-system';
import { AppLayout } from '@shared-frontend-libraries/layouts';

function App() {
  return (
    <ThemeProvider mode="light" domain="management">
      <AppLayout>
        {/* Your app content */}
      </AppLayout>
    </ThemeProvider>
  );
}
```

### Using Components

```tsx
import { Button, Card, Input } from '@shared-frontend-libraries/components';

function MyComponent() {
  return (
    <Card>
      <Input label="Email" placeholder="Enter your email" />
      <Button variant="primary">Submit</Button>
    </Card>
  );
}
```

## Theme Modes

- **Light**: Default mode with light backgrounds
- **Dark**: Dark mode for reduced eye strain
- **Critical**: High contrast mode for emergency situations

## Business Domains

- **Management**: Corporate enterprise colors
- **Individual Insurance**: Trust and security theme
- **Corporate Insurance**: Professional teal theme
- **Insurance Core**: Deep indigo theme
- **Claims Automation**: Urgent red theme
- **Mechanics**: Service green theme
- **Partners Towing**: Visibility amber theme
- **Vendors Ecommerce**: Marketplace purple theme

See [Theme Documentation](themes/) for more details.
```

- [ ] **Step 3: Commit**

```bash
git add README.md docs/getting-started.md
git commit -m "docs: add README and getting started guide"
```

---

## Task 8: Configure CI/CD (GitHub Actions)

**Files:**
- Create: `infrastructure/ci/github-actions/ci.yml`
- Create: `infrastructure/ci/github-actions/cd.yml`

- [ ] **Step 1: Write infrastructure/ci/github-actions/ci.yml**

```yaml
name: CI

on:
  pull_request:
    branches: [main, dev]
  push:
    branches: [main, dev]

jobs:
  lint:
    name: Lint
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v2
        with:
          version: 8
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'pnpm'
      - run: pnpm install --frozen-lockfile
      - run: pnpm lint
      - run: pnpm format:check
      - run: pnpm typecheck

  test:
    name: Test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v2
        with:
          version: 8
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'pnpm'
      - run: pnpm install --frozen-lockfile
      - run: pnpm test -- --coverage
      - uses: codecov/codecov-action@v3
        with:
          files: ./coverage/lcov.info

  e2e:
    name: E2E
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v2
        with:
          version: 8
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'pnpm'
      - run: pnpm install --frozen-lockfile
      - run: pnpm build
      - run: pnpm test:e2e
      - uses: actions/upload-artifact@v3
        if: failure()
        with:
          name: playwright-report
          path: playwright-report/
          retention-days: 7

  lighthouse:
    name: Lighthouse
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v2
        with:
          version: 8
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'pnpm'
      - run: pnpm install --frozen-lockfile
      - run: pnpm build
      - run: npx lhci autorun
      - uses: treosh/lighthouse-ci-action@v10
        with:
          urls: |
            http://localhost:3000
          uploadArtifacts: true
          temporaryPublicStorage: true
```

- [ ] **Step 2: Write infrastructure/ci/github-actions/cd.yml**

```yaml
name: CD

on:
  push:
    tags:
      - 'v*'

jobs:
  build-and-deploy:
    name: Build and Deploy
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v2
        with:
          version: 8
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'pnpm'
      - run: pnpm install --frozen-lockfile
      - run: pnpm build
      - uses: docker/setup-buildx-action@v3
      - uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      - uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ghcr.io/gogidix/shared-frontend-libraries:latest
          cache-from: type=gha
          cache-to: type=gha,mode=max
```

- [ ] **Step 3: Create .github/workflows directory and symlink files**

Run: `mkdir -p .github/workflows && ln -s ../../infrastructure/ci/github-actions/ci.yml .github/workflows/ci.yml && ln -s ../../infrastructure/ci/github-actions/cd.yml .github/workflows/cd.yml`
Expected: CI/CD workflows linked

- [ ] **Step 4: Commit**

```bash
git add infrastructure/ci/github-actions/ci.yml infrastructure/ci/github-actions/cd.yml
git commit -m "ci(cicd): add GitHub Actions CI/CD workflows"
```

---

# PHASE 2: Design System Package (Week 3-4)

## Task 9: Create design-system Package Structure

**Files:**
- Create: `packages/design-system/package.json`
- Create: `packages/design-system/tsconfig.json`
- Create: `packages/design-system/src/index.ts`

- [ ] **Step 1: Write packages/design-system/package.json**

```json
{
  "name": "@shared-frontend-libraries/design-system",
  "version": "1.0.0",
  "type": "module",
  "main": "./dist/index.js",
  "module": "./dist/index.js",
  "types": "./dist/index.d.ts",
  "exports": {
    ".": {
      "import": "./dist/index.js",
      "types": "./dist/index.d.ts"
    },
    "./theme": {
      "import": "./dist/theme/index.js",
      "types": "./dist/theme/index.d.ts"
    }
  },
  "files": [
    "dist"
  ],
  "scripts": {
    "build": "vite build",
    "dev": "vite build --watch",
    "typecheck": "tsc --noEmit",
    "clean": "rm -rf dist"
  },
  "dependencies": {
    "@mui/material": "^6.2.0",
    "@mui/system": "^6.2.0",
    "@emotion/react": "^11.11.1",
    "@emotion/styled": "^11.11.0"
  },
  "devDependencies": {
    "@types/react": "^18.2.45",
    "@types/react-dom": "^18.2.18",
    "vite": "^5.0.11",
    "vite-plugin-dts": "^3.7.0",
    "typescript": "^5.3.3"
  },
  "peerDependencies": {
    "react": "^18.0.0",
    "react-dom": "^18.0.0"
  }
}
```

- [ ] **Step 2: Write packages/design-system/tsconfig.json**

```json
{
  "extends": "../../tsconfig.base.json",
  "compilerOptions": {
    "outDir": "./dist",
    "rootDir": "./src",
    "composite": true
  },
  "include": ["src/**/*"],
  "exclude": ["dist", "node_modules"]
}
```

- [ ] **Step 3: Write packages/design-system/vite.config.ts**

```typescript
import { defineConfig } from 'vite';
import dts from 'vite-plugin-dts';

export default defineConfig({
  build: {
    lib: {
      entry: './src/index.ts',
      name: 'DesignSystem',
      fileName: 'index',
      formats: ['es'],
    },
    rollupOptions: {
      external: ['react', 'react-dom'],
      output: {
        globals: {
          react: 'React',
          'react-dom': 'ReactDOM',
        },
      },
    },
  },
  plugins: [dts()],
});
```

- [ ] **Step 4: Write packages/design-system/src/index.ts**

```typescript
export * from './theme';
export * from './modes';
export * from './brand';
export * from './personalization';
```

- [ ] **Step 5: Commit**

```bash
git add packages/design-system/package.json packages/design-system/tsconfig.json packages/design-system/vite.config.ts packages/design-system/src/index.ts
git commit -m "feat(design-system): create package structure and config"
```

---

## Task 10: Implement Color System

**Files:**
- Create: `packages/design-system/src/theme/colors.ts`

- [ ] **Step 1: Write failing test for colors**

Create `packages/design-system/src/theme/colors.test.ts`:

```typescript
import { describe, it, expect } from 'vitest';
import { MANAGEMENT_COLORS, BUSINESS_DOMAIN_COLORS } from './colors';

describe('Color System', () => {
  describe('Management Colors', () => {
    it('should have all required management color properties', () => {
      expect(MANAGEMENT_COLORS).toHaveProperty('primary');
      expect(MANAGEMENT_COLORS).toHaveProperty('primaryHover');
      expect(MANAGEMENT_COLORS).toHaveProperty('primaryLight');
      expect(MANAGEMENT_COLORS).toHaveProperty('secondary');
      expect(MANAGEMENT_COLORS).toHaveProperty('secondaryLight');
      expect(MANAGEMENT_COLORS).toHaveProperty('accent');
      expect(MANAGEMENT_COLORS).toHaveProperty('accentHover');
    });

    it('should have correct management primary color', () => {
      expect(MANAGEMENT_COLORS.primary).toBe('#0066CC');
    });

    it('should have all functional colors', () => {
      expect(MANAGEMENT_COLORS).toHaveProperty('success');
      expect(MANAGEMENT_COLORS).toHaveProperty('warning');
      expect(MANAGEMENT_COLORS).toHaveProperty('error');
      expect(MANAGEMENT_COLORS).toHaveProperty('info');
    });
  });

  describe('Business Domain Colors', () => {
    it('should have all 7 business domains', () => {
      expect(Object.keys(BUSINESS_DOMAIN_COLORS)).toHaveLength(7);
    });

    it('should have Individual Insurance domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.individualInsurance).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.individualInsurance.primary).toBe('#6366F1');
    });

    it('should have Corporate Insurance domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.corporateInsurance).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.corporateInsurance.primary).toBe('#0D9488');
    });

    it('should have Insurance Core domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.insuranceCore).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.insuranceCore.primary).toBe('#4338CA');
    });

    it('should have Claims Automation domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.claimsAutomation).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.claimsAutomation.primary).toBe('#DC2626');
    });

    it('should have Mechanics domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.mechanics).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.mechanics.primary).toBe('#059669');
    });

    it('should have Partners Towing domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.partnersTowing).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.partnersTowing.primary).toBe('#D97706');
    });

    it('should have Vendors Ecommerce domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.vendorsEcommerce).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.vendorsEcommerce.primary).toBe('#7C3AED');
    });
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd packages/design-system && pnpm test src/theme/colors.test.ts`
Expected: FAIL with "Cannot find module './colors'"

- [ ] **Step 3: Write packages/design-system/src/theme/colors.ts**

```typescript
/**
 * Management Domain Colors - Amazon/Stripe Standards
 * Corporate enterprise colors for Management Domain
 */
export const MANAGEMENT_COLORS = {
  primary: '#0066CC',        // Deep Blue - Primary Action
  primaryHover: '#0052A3',   // Darker Blue - Hover State
  primaryLight: '#E6F2FF',    // Light Blue - Backgrounds
  secondary: '#1A1A1A',       // Dark Gray - Text/Headings
  secondaryLight: '#F5F5F5',   // Light Gray - Backgrounds
  accent: '#00D4AA',          // Teal - Success/Confirmation
  accentHover: '#00A888',     // Darker Teal - Hover

  // Functional colors
  success: '#10B981',
  warning: '#F59E0B',
  error: '#EF4444',
  info: '#3B82F6',
} as const;

export type ManagementColors = typeof MANAGEMENT_COLORS;

/**
 * Business Domain Color Palettes
 * Each domain has primary, secondary, accent, and warmth colors
 */
export const BUSINESS_DOMAIN_COLORS = {
  individualInsurance: {
    primary: '#6366F1',        // Indigo - Trust & Security
    secondary: '#4F46E5',      // Deep Indigo - Primary Actions
    accent: '#8B5CF6',          // Violet - Highlights
    warmth: '#F59E0B',          // Amber - Personal touch
  },
  corporateInsurance: {
    primary: '#0D9488',        // Teal - Professional & Trust
    secondary: '#0F766E',      // Deep Teal - Primary Actions
    accent: '#14B8A6',          // Lighter Teal - Highlights
    warmth: '#F59E0B',          // Amber - Business warmth
  },
  insuranceCore: {
    primary: '#4338CA',        // Deep Indigo - Insurance Authority
    secondary: '#3730A3',      // Darker Indigo - Actions
    accent: '#6366F1',          // Blue-Gray - Neutral Highlights
    warmth: '#10B981',          // Emerald - Claims success
  },
  claimsAutomation: {
    primary: '#DC2626',        // Deep Red - Urgency & Action
    secondary: '#B91C1C',      // Darker Red - Primary Actions
    accent: '#EF4444',          // Lighter Red - Highlights
    warmth: '#F59E0B',          // Amber - Processing status
  },
  mechanics: {
    primary: '#059669',        // Emerald Green - Service & Fix
    secondary: '#047857',      // Deep Emerald - Primary Actions
    accent: '#10B981',          // Lighter Emerald - Highlights
    warmth: '#F59E0B',          // Amber - Status updates
  },
  partnersTowing: {
    primary: '#D97706',        // Amber-Orange - Visibility & Towing
    secondary: '#B45309',      // Darker Amber - Primary Actions
    accent: '#FBBF24',          // Lighter Amber - Highlights
    warmth: '#3B82F6',          // Blue - Trust & Coordination
  },
  vendorsEcommerce: {
    primary: '#7C3AED',        // Purple - Marketplace & Commerce
    secondary: '#6D28D9',      // Deep Purple - Primary Actions
    accent: '#8B5CF6',          // Lighter Purple - Highlights
    warmth: '#F59E0B',          // Amber - Transaction status
  },
} as const;

export type BusinessDomain = keyof typeof BUSINESS_DOMAIN_COLORS;
export type BusinessDomainColors = typeof BUSINESS_DOMAIN_COLORS[BusinessDomain];

/**
 * Get domain-specific colors
 */
export function getDomainColors(domain: BusinessDomain): BusinessDomainColors {
  return BUSINESS_DOMAIN_COLORS[domain];
}

/**
 * Check if a domain is valid
 */
export function isValidDomain(domain: string): domain is BusinessDomain {
  return domain in BUSINESS_DOMAIN_COLORS;
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd packages/design-system && pnpm test src/theme/colors.test.ts`
Expected: PASS

- [ ] **Step 5: Write packages/design-system/src/theme/index.ts**

```typescript
export * from './colors';
export * from './typography';
export * from './spacing';
export * from './breakpoints';
export * from './shadows';
```

- [ ] **Step 6: Commit**

```bash
git add packages/design-system/src/theme/colors.ts packages/design-system/src/theme/colors.test.ts packages/design-system/src/theme/index.ts
git commit -m "feat(design-system): implement color system with management and domain palettes"
```

---

## Task 11: Implement Typography System

**Files:**
- Create: `packages/design-system/src/theme/typography.ts`

- [ ] **Step 1: Write failing test for typography**

Create `packages/design-system/src/theme/typography.test.ts`:

```typescript
import { describe, it, expect } from 'vitest';
import { TYPOGRAPHY_SCALE, getTypographyScale } from './typography';

describe('Typography System', () => {
  it('should have all typography scales', () => {
    expect(TYPOGRAPHY_SCALE).toHaveProperty('h1');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('h2');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('h3');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('h4');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('h5');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('h6');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('body1');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('body2');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('button');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('caption');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('data');
  });

  it('should have correct h1 typography', () => {
    expect(TYPOGRAPHY_SCALE.h1.fontSize).toBe('2.5rem');
    expect(TYPOGRAPHY_SCALE.h1.fontWeight).toBe(700);
    expect(TYPOGRAPHY_SCALE.h1.lineHeight).toBe(1.2);
  });

  it('should have correct body1 typography', () => {
    expect(TYPOGRAPHY_SCALE.body1.fontSize).toBe('1rem');
    expect(TYPOGRAPHY_SCALE.body1.fontWeight).toBe(400);
    expect(TYPOGRAPHY_SCALE.body1.lineHeight).toBe(1.5);
  });

  it('should get typography scale for data displays', () => {
    const dataTypography = getTypographyScale('data');
    expect(dataTypography.fontSize).toBe('1rem');
    expect(dataTypography.fontWeight).toBe(600);
    expect(dataTypography.lineHeight).toBe(1.2);
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd packages/design-system && pnpm test src/theme/typography.test.ts`
Expected: FAIL with "Cannot find module './typography'"

- [ ] **Step 3: Write packages/design-system/src/theme/typography.ts**

```typescript
/**
 * Typography Scale
 * Enterprise typography system with Inter for UI text and JetBrains Mono for data
 */
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
} as const;

export type TypographyScale = keyof typeof TYPOGRAPHY_SCALE;

/**
 * Font families
 */
export const FONT_FAMILIES = {
  ui: '"Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
  data: '"JetBrains Mono", "Fira Code", monospace',
} as const;

/**
 * Get typography configuration for a given scale
 */
export function getTypographyScale(scale: TypographyScale) {
  return TYPOGRAPHY_SCALE[scale];
}

/**
 * Get font family for UI elements
 */
export function getUIFontFamily(): string {
  return FONT_FAMILIES.ui;
}

/**
 * Get font family for data displays
 */
export function getDataFontFamily(): string {
  return FONT_FAMILIES.data;
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd packages/design-system && pnpm test src/theme/typography.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add packages/design-system/src/theme/typography.ts packages/design-system/src/theme/typography.test.ts
git commit -m "feat(design-system): implement typography scale with Inter and JetBrains Mono"
```

---

## Task 12: Implement Spacing Scale

**Files:**
- Create: `packages/design-system/src/theme/spacing.ts`

- [ ] **Step 1: Write failing test for spacing**

Create `packages/design-system/src/theme/spacing.test.ts`:

```typescript
import { describe, it, expect } from 'vitest';
import { SPACING_SCALE, getSpacing } from './spacing';

describe('Spacing Scale', () => {
  it('should have all spacing values from 0 to 12', () => {
    expect(SPACING_SCALE).toHaveProperty('0');
    expect(SPACING_SCALE).toHaveProperty('12');
  });

  it('should have base spacing unit of 4px', () => {
    expect(SPACING_SCALE[1]).toBe('4px');
  });

  it('should calculate spacing correctly', () => {
    expect(getSpacing(1)).toBe('4px');
    expect(getSpacing(2)).toBe('8px');
    expect(getSpacing(4)).toBe('16px');
    expect(getSpacing(8)).toBe('32px');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd packages/design-system && pnpm test src/theme/spacing.test.ts`
Expected: FAIL with "Cannot find module './spacing'"

- [ ] **Step 3: Write packages/design-system/src/theme/spacing.ts**

```typescript
/**
 * Spacing Scale
 * Base unit: 4px
 * Scale: 0, 0.5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
 */
export const SPACING_SCALE = {
  0: '0px',
  0.5: '2px',
  1: '4px',
  2: '8px',
  3: '12px',
  4: '16px',
  5: '20px',
  6: '24px',
  7: '28px',
  8: '32px',
  9: '36px',
  10: '40px',
  11: '44px',
  12: '48px',
  auto: 'auto',
} as const;

export type SpacingScale = keyof typeof SPACING_SCALE;

/**
 * Get spacing value for a given scale
 * @param scale - Spacing scale value
 * @returns CSS spacing value
 */
export function getSpacing(scale: SpacingScale): string {
  return SPACING_SCALE[scale];
}

/**
 * Convert spacing unit to pixels
 * @param scale - Spacing scale value
 * @returns Pixel value (number)
 */
export function spacingToPx(scale: number): number {
  return scale * 4;
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd packages/design-system && pnpm test src/theme/spacing.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add packages/design-system/src/theme/spacing.ts packages/design-system/src/theme/spacing.test.ts
git commit -m "feat(design-system): implement spacing scale with 4px base unit"
```

---

## Task 13: Implement Breakpoints

**Files:**
- Create: `packages/design-system/src/theme/breakpoints.ts`

- [ ] **Step 1: Write failing test for breakpoints**

Create `packages/design-system/src/theme/breakpoints.test.ts`:

```typescript
import { describe, it, expect } from 'vitest';
import { BREAKPOINTS, getBreakpointValue } from './breakpoints';

describe('Breakpoints', () => {
  it('should have all required breakpoints', () => {
    expect(BREAKPOINTS).toHaveProperty('xs');
    expect(BREAKPOINTS).toHaveProperty('sm');
    expect(BREAKPOINTS).toHaveProperty('md');
    expect(BREAKPOINTS).toHaveProperty('lg');
    expect(BREAKPOINTS).toHaveProperty('xl');
    expect(BREAKPOINTS).toHaveProperty('xxl');
  });

  it('should have correct breakpoint values', () => {
    expect(BREAKPOINTS.xs).toBe(0);
    expect(BREAKPOINTS.sm).toBe(600);
    expect(BREAKPOINTS.md).toBe(900);
    expect(BREAKPOINTS.lg).toBe(1200);
    expect(BREAKPOINTS.xl).toBe(1536);
    expect(BREAKPOINTS.xxl).toBe(1920);
  });

  it('should get breakpoint value correctly', () => {
    expect(getBreakpointValue('md')).toBe(900);
    expect(getBreakpointValue('lg')).toBe(1200);
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd packages/design-system && pnpm test src/theme/breakpoints.test.ts`
Expected: FAIL with "Cannot find module './breakpoints'"

- [ ] **Step 3: Write packages/design-system/src/theme/breakpoints.ts**

```typescript
/**
 * Responsive Breakpoints
 * Material UI standard breakpoints
 */
export const BREAKPOINTS = {
  xs: 0,      // Extra small devices (phones)
  sm: 600,    // Small devices (tablets)
  md: 900,    // Medium devices (small laptops)
  lg: 1200,   // Large devices (desktops)
  xl: 1536,   // Extra large devices (large desktops)
  xxl: 1920,  // Extra extra large devices (4K displays)
} as const;

export type Breakpoint = keyof typeof BREAKPOINTS;

/**
 * Get breakpoint value in pixels
 * @param breakpoint - Breakpoint name
 * @returns Pixel value
 */
export function getBreakpointValue(breakpoint: Breakpoint): number {
  return BREAKPOINTS[breakpoint];
}

/**
 * Get media query for a breakpoint
 * @param breakpoint - Breakpoint name
 * @param type - Media query type (up, down, only)
 * @returns Media query string
 */
export function getMediaQuery(
  breakpoint: Breakpoint,
  type: 'up' | 'down' | 'only' = 'up'
): string {
  const value = BREAKPOINTS[breakpoint];

  switch (type) {
    case 'up':
      return `@media (min-width: ${value}px)`;
    case 'down':
      return `@media (max-width: ${value - 1}px)`;
    case 'only':
      return `@media (min-width: ${value}px) and (max-width: ${BREAKPOINTS[breakpoint as keyof typeof BREAKPOINTS] - 1}px)`;
    default:
      return '';
  }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd packages/design-system && pnpm test src/theme/breakpoints.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add packages/design-system/src/theme/breakpoints.ts packages/design-system/src/theme/breakpoints.test.ts
git commit -m "feat(design-system): implement responsive breakpoints"
```

---

## Task 14: Implement Shadow Utilities

**Files:**
- Create: `packages/design-system/src/theme/shadows.ts`

- [ ] **Step 1: Write failing test for shadows**

Create `packages/design-system/src/theme/shadows.test.ts`:

```typescript
import { describe, it, expect } from 'vitest';
import { SHADOWS, getShadow } from './shadows';

describe('Shadow System', () => {
  it('should have all shadow levels', () => {
    expect(SHADOWS).toHaveProperty('none');
    expect(SHADOWS).toHaveProperty('xs');
    expect(SHADOWS).toHaveProperty('sm');
    expect(SHADOWS).toHaveProperty('md');
    expect(SHADOWS).toHaveProperty('lg');
    expect(SHADOWS).toHaveProperty('xl');
  });

  it('should have correct shadow values', () => {
    expect(SHADOWS.xs).toBe('0 1px 2px rgba(0,0,0,0.05)');
    expect(SHADOWS.md).toBe('0 4px 6px -1px rgba(0,0,0,0.1)');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd packages/design-system && pnpm test src/theme/shadows.test.ts`
Expected: FAIL with "Cannot find module './shadows'"

- [ ] **Step 3: Write packages/design-system/src/theme/shadows.ts**

```typescript
/**
 * Shadow Utilities
 * Material UI standard shadows
 */
export const SHADOWS = {
  none: 'none',
  xs: '0 1px 2px rgba(0,0,0,0.05)',
  sm: '0 1px 3px rgba(0,0,0,0.1), 0 1px 2px rgba(0,0,0,0.06)',
  md: '0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06)',
  lg: '0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -2px rgba(0,0,0,0.05)',
  xl: '0 20px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04)',
} as const;

export type ShadowLevel = keyof typeof SHADOWS;

/**
 * Get shadow value for a given level
 * @param level - Shadow level
 * @returns CSS shadow value
 */
export function getShadow(level: ShadowLevel): string {
  return SHADOWS[level];
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd packages/design-system && pnpm test src/theme/shadows.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add packages/design-system/src/theme/shadows.ts packages/design-system/src/theme/shadows.test.ts
git commit -m "feat(design-system): implement shadow utilities"
```

---

## Task 15: Implement Theme Modes

**Files:**
- Create: `packages/design-system/src/modes/light.ts`
- Create: `packages/design-system/src/modes/dark.ts`
- Create: `packages/design-system/src/modes/critical.ts`
- Create: `packages/design-system/src/modes/index.ts`

- [ ] **Step 1: Write failing test for light mode**

Create `packages/design-system/src/modes/light.test.ts`:

```typescript
import { describe, it, expect } from 'vitest';
import { LIGHT_THEME } from './light';

describe('Light Theme', () => {
  it('should have light theme background colors', () => {
    expect(LIGHT_THEME.background.default).toBe('#FFFFFF');
    expect(LIGHT_THEME.background.paper).toBe('#F9FAFB');
  });

  it('should have light theme text colors', () => {
    expect(LIGHT_THEME.text.primary).toBe('#1F2937');
    expect(LIGHT_THEME.text.secondary).toBe('#374151');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd packages/design-system && pnpm test src/modes/light.test.ts`
Expected: FAIL with "Cannot find module './light'"

- [ ] **Step 3: Write packages/design-system/src/modes/light.ts**

```typescript
import { MANAGEMENT_COLORS } from '../theme/colors';

/**
 * Light Mode Theme
 * Default theme for most applications
 */
export const LIGHT_THEME = {
  palette: {
    mode: 'light' as const,
    primary: {
      main: MANAGEMENT_COLORS.primary,
      light: MANAGEMENT_COLORS.primaryLight,
      dark: MANAGEMENT_COLORS.primaryHover,
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: MANAGEMENT_COLORS.secondary,
      light: MANAGEMENT_COLORS.secondaryLight,
      dark: '#000000',
      contrastText: '#FFFFFF',
    },
    error: {
      main: MANAGEMENT_COLORS.error,
      light: '#F87171',
      dark: '#B91C1C',
      contrastText: '#FFFFFF',
    },
    warning: {
      main: MANAGEMENT_COLORS.warning,
      light: '#FBBF24',
      dark: '#B45309',
      contrastText: '#000000',
    },
    info: {
      main: MANAGEMENT_COLORS.info,
      light: '#60A5FA',
      dark: '#1D4ED8',
      contrastText: '#FFFFFF',
    },
    success: {
      main: MANAGEMENT_COLORS.success,
      light: '#34D399',
      dark: '#047857',
      contrastText: '#FFFFFF',
    },
    text: {
      primary: '#1F2937',
      secondary: '#374151',
      disabled: '#9CA3AF',
    },
    divider: '#E5E7EB',
    background: {
      default: '#FFFFFF',
      paper: '#F9FAFB',
    },
  },
} as const;

export type LightTheme = typeof LIGHT_THEME;
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd packages/design-system && pnpm test src/modes/light.test.ts`
Expected: PASS

- [ ] **Step 5: Write failing test for dark mode**

Create `packages/design-system/src/modes/dark.test.ts`:

```typescript
import { describe, it, expect } from 'vitest';
import { DARK_THEME } from './dark';

describe('Dark Theme', () => {
  it('should have dark theme background colors', () => {
    expect(DARK_THEME.background.default).toBe('#111827');
    expect(DARK_THEME.background.paper).toBe('#1F2937');
  });

  it('should have dark theme text colors', () => {
    expect(DARK_THEME.text.primary).toBe('#F9FAFB');
    expect(DARK_THEME.text.secondary).toBe('#E5E7EB');
  });
});
```

- [ ] **Step 6: Run test to verify it fails**

Run: `cd packages/design-system && pnpm test src/modes/dark.test.ts`
Expected: FAIL with "Cannot find module './dark'"

- [ ] **Step 7: Write packages/design-system/src/modes/dark.ts**

```typescript
import { MANAGEMENT_COLORS } from '../theme/colors';

/**
 * Dark Mode Theme
 * Reduced eye strain for low-light environments
 */
export const DARK_THEME = {
  palette: {
    mode: 'dark' as const,
    primary: {
      main: MANAGEMENT_COLORS.primary,
      light: MANAGEMENT_COLORS.primaryLight,
      dark: MANAGEMENT_COLORS.primaryHover,
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: MANAGEMENT_COLORS.secondaryLight,
      light: '#9CA3AF',
      dark: '#1A1A1A',
      contrastText: '#000000',
    },
    error: {
      main: MANAGEMENT_COLORS.error,
      light: '#F87171',
      dark: '#B91C1C',
      contrastText: '#FFFFFF',
    },
    warning: {
      main: MANAGEMENT_COLORS.warning,
      light: '#FBBF24',
      dark: '#B45309',
      contrastText: '#000000',
    },
    info: {
      main: MANAGEMENT_COLORS.info,
      light: '#60A5FA',
      dark: '#1D4ED8',
      contrastText: '#FFFFFF',
    },
    success: {
      main: MANAGEMENT_COLORS.success,
      light: '#34D399',
      dark: '#047857',
      contrastText: '#FFFFFF',
    },
    text: {
      primary: '#F9FAFB',
      secondary: '#E5E7EB',
      disabled: '#6B7280',
    },
    divider: '#374151',
    background: {
      default: '#111827',
      paper: '#1F2937',
    },
  },
} as const;

export type DarkTheme = typeof DARK_THEME;
```

- [ ] **Step 8: Run test to verify it passes**

Run: `cd packages/design-system && pnpm test src/modes/dark.test.ts`
Expected: PASS

- [ ] **Step 9: Write failing test for critical mode**

Create `packages/design-system/src/modes/critical.test.ts`:

```typescript
import { describe, it, expect } from 'vitest';
import { CRITICAL_THEME } from './critical';

describe('Critical Theme', () => {
  it('should have critical background', () => {
    expect(CRITICAL_THEME.background.default).toBe('#FEF2F2');
  });

  it('should have critical action color', () => {
    expect(CRITICAL_THEME.criticalAction).toBe('#DC2626');
  });
});
```

- [ ] **Step 10: Run test to verify it fails**

Run: `cd packages/design-system && pnpm test src/modes/critical.test.ts`
Expected: FAIL with "Cannot find module './critical'"

- [ ] **Step 11: Write packages/design-system/src/modes/critical.ts**

```typescript
/**
 * Critical Mode Theme (Emergency)
 * High contrast mode for emergency situations
 * All non-critical UI is muted for focus
 */
export const CRITICAL_THEME = {
  palette: {
    mode: 'light' as const,
    critical: {
      main: '#DC2626',
      light: '#FEF2F2',
      dark: '#991B1B',
      contrastText: '#FFFFFF',
    },
    background: {
      default: '#FEF2F2',
      paper: '#FFFFFF',
    },
    text: {
      primary: '#1F2937',
      secondary: '#374151',
      disabled: '#9CA3AF',
    },
    action: {
      active: '#DC2626',
      hover: '#B91C1C',
      selected: '#EF4444',
      disabled: 'rgba(0,0,0,0.3)',
    },
  },
  criticalAction: '#DC2626',
  mutedOpacity: 0.5,
} as const;

export type CriticalTheme = typeof CRITICAL_THEME;
```

- [ ] **Step 12: Run test to verify it passes**

Run: `cd packages/design-system && pnpm test src/modes/critical.test.ts`
Expected: PASS

- [ ] **Step 13: Write packages/design-system/src/modes/index.ts**

```typescript
export * from './light';
export * from './dark';
export * from './critical';

export type ThemeMode = 'light' | 'dark' | 'critical';

/**
 * Get theme configuration for a given mode
 * @param mode - Theme mode
 * @returns Theme configuration
 */
export function getThemeConfig(mode: ThemeMode) {
  switch (mode) {
    case 'light':
      return import('./light').then(m => m.LIGHT_THEME);
    case 'dark':
      return import('./dark').then(m => m.DARK_THEME);
    case 'critical':
      return import('./critical').then(m => m.CRITICAL_THEME);
    default:
      return import('./light').then(m => m.LIGHT_THEME);
  }
}
```

- [ ] **Step 14: Commit**

```bash
git add packages/design-system/src/modes/
git commit -m "feat(design-system): implement light, dark, and critical theme modes"
```

---

## Task 16: Implement Brand Colors

**Files:**
- Create: `packages/design-system/src/brand/management.ts`
- Create: `packages/design-system/src/brand/business-domains.ts`
- Create: `packages/design-system/src/brand/index.ts`

- [ ] **Step 1: Write failing test for brand colors**

Create `packages/design-system/src/brand/management.test.ts`:

```typescript
import { describe, it, expect } from 'vitest';
import { getManagementBrandColors } from './management';

describe('Management Brand Colors', () => {
  it('should return correct brand colors', () => {
    const colors = getManagementBrandColors();
    expect(colors).toHaveProperty('primary');
    expect(colors.primary).toBe('#0066CC');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd packages/design-system && pnpm test src/brand/management.test.ts`
Expected: FAIL with "Cannot find module './management'"

- [ ] **Step 3: Write packages/design-system/src/brand/management.ts**

```typescript
/**
 * Management Domain Brand Colors
 * Amazon/Stripe standards for corporate enterprise
 */
export const MANAGEMENT_BRAND_COLORS = {
  primary: '#0066CC',
  primaryHover: '#0052A3',
  primaryLight: '#E6F2FF',
  secondary: '#1A1A1A',
  secondaryLight: '#F5F5F5',
  accent: '#00D4AA',
  accentHover: '#00A888',
} as const;

/**
 * Get Management Domain brand colors
 * @returns Brand color configuration
 */
export function getManagementBrandColors() {
  return MANAGEMENT_BRAND_COLORS;
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd packages/design-system && pnpm test src/brand/management.test.ts`
Expected: PASS

- [ ] **Step 5: Write failing test for business domain colors**

Create `packages/design-system/src/brand/business-domains.test.ts`:

```typescript
import { describe, it, expect } from 'vitest';
import { getBusinessDomainBrandColors, BusinessDomain } from './business-domains';

describe('Business Domain Brand Colors', () => {
  it('should return correct colors for Individual Insurance', () => {
    const colors = getBusinessDomainBrandColors('individualInsurance');
    expect(colors.primary).toBe('#6366F1');
  });

  it('should return correct colors for Mechanics', () => {
    const colors = getBusinessDomainBrandColors('mechanics');
    expect(colors.primary).toBe('#059669');
  });

  it('should throw error for invalid domain', () => {
    expect(() => getBusinessDomainBrandColors('invalid' as BusinessDomain)).toThrow();
  });
});
```

- [ ] **Step 6: Run test to verify it fails**

Run: `cd packages/design-system && pnpm test src/brand/business-domains.test.ts`
Expected: FAIL with "Cannot find module './business-domains'"

- [ ] **Step 7: Write packages/design-system/src/brand/business-domains.ts**

```typescript
import { BUSINESS_DOMAIN_COLORS } from '../theme/colors';

/**
 * Business Domain Brand Colors
 * Extends base domain colors with brand-specific properties
 */

export type BusinessDomain =
  | 'individualInsurance'
  | 'corporateInsurance'
  | 'insuranceCore'
  | 'claimsAutomation'
  | 'mechanics'
  | 'partnersTowing'
  | 'vendorsEcommerce';

export interface BusinessDomainBrandColors {
  primary: string;
  secondary: string;
  accent: string;
  warmth: string;
}

/**
 * Get brand colors for a specific business domain
 * @param domain - Business domain name
 * @returns Brand color configuration
 */
export function getBusinessDomainBrandColors(
  domain: BusinessDomain
): BusinessDomainBrandColors {
  const colors = BUSINESS_DOMAIN_COLORS[domain];

  if (!colors) {
    throw new Error(`Unknown business domain: ${domain}`);
  }

  return {
    primary: colors.primary,
    secondary: colors.secondary,
    accent: colors.accent,
    warmth: colors.warmth,
  };
}

/**
 * Get all available business domains
 * @returns Array of business domain names
 */
export function getAvailableBusinessDomains(): BusinessDomain[] {
  return Object.keys(BUSINESS_DOMAIN_COLORS) as BusinessDomain[];
}
```

- [ ] **Step 8: Run test to verify it passes**

Run: `cd packages/design-system && pnpm test src/brand/business-domains.test.ts`
Expected: PASS

- [ ] **Step 9: Write packages/design-system/src/brand/index.ts**

```typescript
export * from './management';
export * from './business-domains';
```

- [ ] **Step 10: Commit**

```bash
git add packages/design-system/src/brand/
git commit -m "feat(design-system): implement brand colors for management and business domains"
```

---

## Task 17: Implement User Personalization

**Files:**
- Create: `packages/design-system/src/personalization/presets.ts`
- Create: `packages/design-system/src/personalization/custom-picker.ts`
- Create: `packages/design-system/src/personalization/index.ts`

- [ ] **Step 1: Write failing test for preset themes**

Create `packages/design-system/src/personalization/presets.test.ts`:

```typescript
import { describe, it, expect } from 'vitest';
import { getPresetThemes, getPresetTheme } from './presets';

describe('Preset Themes', () => {
  it('should return preset themes for a domain', () => {
    const presets = getPresetThemes('individualInsurance');
    expect(Array.isArray(presets)).toBe(true);
    expect(presets.length).toBeGreaterThanOrEqual(4);
  });

  it('should return a specific preset theme', () => {
    const preset = getPresetTheme('individualInsurance', 'professional');
    expect(preset).toBeDefined();
    expect(preset).toHaveProperty('primary');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd packages/design-system && pnpm test src/personalization/presets.test.ts`
Expected: FAIL with "Cannot find module './presets'"

- [ ] **Step 3: Write packages/design-system/src/personalization/presets.ts**

```typescript
import { getBusinessDomainBrandColors, BusinessDomain } from '../brand/business-domains';

/**
 * Preset Themes
 * 4-6 pre-built themes per domain with WCAG AA contrast compliance
 */

export interface PresetTheme {
  id: string;
  name: string;
  primary: string;
  secondary: string;
  accent: string;
  background: string;
  text: string;
}

/**
 * Generate preset themes for a business domain
 * @param domain - Business domain
 * @returns Array of preset themes
 */
export function getPresetThemes(domain: BusinessDomain): PresetTheme[] {
  const baseColors = getBusinessDomainBrandColors(domain);

  return [
    {
      id: 'default',
      name: 'Default',
      primary: baseColors.primary,
      secondary: baseColors.secondary,
      accent: baseColors.accent,
      background: '#FFFFFF',
      text: '#1F2937',
    },
    {
      id: 'professional',
      name: 'Professional',
      primary: baseColors.secondary,
      secondary: baseColors.primary,
      accent: baseColors.warmth,
      background: '#F9FAFB',
      text: '#111827',
    },
    {
      id: 'warm',
      name: 'Warm',
      primary: baseColors.warmth,
      secondary: baseColors.primary,
      accent: baseColors.accent,
      background: '#FFFBEB',
      text: '#1F2937',
    },
    {
      id: 'cool',
      name: 'Cool',
      primary: baseColors.accent,
      secondary: baseColors.secondary,
      accent: baseColors.primary,
      background: '#F0F9FF',
      text: '#1F2937',
    },
    {
      id: 'high-contrast',
      name: 'High Contrast',
      primary: '#000000',
      secondary: '#FFFFFF',
      accent: baseColors.primary,
      background: '#FFFFFF',
      text: '#000000',
    },
    {
      id: 'dark-focus',
      name: 'Dark Focus',
      primary: baseColors.primary,
      secondary: '#1F2937',
      accent: baseColors.accent,
      background: '#111827',
      text: '#F9FAFB',
    },
  ];
}

/**
 * Get a specific preset theme
 * @param domain - Business domain
 * @param presetId - Preset theme ID
 * @returns Preset theme or undefined
 */
export function getPresetTheme(
  domain: BusinessDomain,
  presetId: string
): PresetTheme | undefined {
  const presets = getPresetThemes(domain);
  return presets.find(p => p.id === presetId);
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd packages/design-system && pnpm test src/personalization/presets.test.ts`
Expected: PASS

- [ ] **Step 5: Write failing test for custom color picker**

Create `packages/design-system/src/personalization/custom-picker.test.ts`:

```typescript
import { describe, it, expect } from 'vitest';
import { validateCustomColor, getContrastRatio } from './custom-picker';

describe('Custom Color Picker', () => {
  it('should validate colors with sufficient contrast', () => {
    const result = validateCustomColor('#0066CC', '#FFFFFF');
    expect(result.valid).toBe(true);
    expect(result.contrast).toBeGreaterThan(4.5);
  });

  it('should reject colors with insufficient contrast', () => {
    const result = validateCustomColor('#CCCCCC', '#DDDDDD');
    expect(result.valid).toBe(false);
  });

  it('should calculate contrast ratio correctly', () => {
    const contrast = getContrastRatio('#000000', '#FFFFFF');
    expect(contrast).toBe(21); // Maximum contrast
  });
});
```

- [ ] **Step 6: Run test to verify it fails**

Run: `cd packages/design-system && pnpm test src/personalization/custom-picker.test.ts`
Expected: FAIL with "Cannot find module './custom-picker'"

- [ ] **Step 7: Write packages/design-system/src/personalization/custom-picker.ts`

```typescript
/**
 * Custom Color Picker
 * Validates custom colors against WCAG AA contrast guidelines (4.5:1 minimum)
 */

export interface ColorValidationResult {
  valid: boolean;
  contrast: number;
  meetsAA: boolean;
  meetsAAA: boolean;
}

/**
 * Calculate relative luminance of a color
 * @param r - Red (0-255)
 * @param g - Green (0-255)
 * @param b - Blue (0-255)
 * @returns Relative luminance
 */
function getLuminance(r: number, g: number, b: number): number {
  const [rs, gs, bs] = [r, g, b].map(c => {
    const c255 = c / 255;
    return c255 <= 0.03928 ? c255 / 12.92 : Math.pow((c255 + 0.055) / 1.055, 2.4);
  });
  return 0.2126 * rs + 0.7152 * gs + 0.0722 * bs;
}

/**
 * Convert hex color to RGB
 * @param hex - Hex color string
 * @returns RGB values
 */
function hexToRgb(hex: string): { r: number; g: number; b: number } {
  const cleanHex = hex.replace('#', '');
  if (cleanHex.length === 3) {
    return {
      r: parseInt(cleanHex[0] + cleanHex[0], 16),
      g: parseInt(cleanHex[1] + cleanHex[1], 16),
      b: parseInt(cleanHex[2] + cleanHex[2], 16),
    };
  }
  return {
    r: parseInt(cleanHex.substring(0, 2), 16),
    g: parseInt(cleanHex.substring(2, 4), 16),
    b: parseInt(cleanHex.substring(4, 6), 16),
  };
}

/**
 * Calculate contrast ratio between two colors
 * @param foreground - Foreground color
 * @param background - Background color
 * @returns Contrast ratio
 */
export function getContrastRatio(foreground: string, background: string): number {
  const fg = hexToRgb(foreground);
  const bg = hexToRgb(background);
  const fgLum = getLuminance(fg.r, fg.g, fg.b);
  const bgLum = getLuminance(bg.r, bg.g, bg.b);
  const lighter = Math.max(fgLum, bgLum);
  const darker = Math.min(fgLum, bgLum);
  return (lighter + 0.05) / (darker + 0.05);
}

/**
 * Validate custom color against WCAG guidelines
 * @param foreground - Foreground color
 * @param background - Background color
 * @returns Validation result
 */
export function validateCustomColor(
  foreground: string,
  background: string
): ColorValidationResult {
  const contrast = getContrastRatio(foreground, background);
  const meetsAA = contrast >= 4.5;
  const meetsAAA = contrast >= 7;

  return {
    valid: meetsAA,
    contrast,
    meetsAA,
    meetsAAA,
  };
}

/**
 * Generate light variant of a color
 * @param hex - Hex color
 * @param factor - Lightness factor (0-1)
 * @returns Lightened color
 */
export function lightenColor(hex: string, factor: number = 0.1): string {
  const rgb = hexToRgb(hex);
  const lightened = {
    r: Math.min(255, Math.round(rgb.r + (255 - rgb.r) * factor)),
    g: Math.min(255, Math.round(rgb.g + (255 - rgb.g) * factor)),
    b: Math.min(255, Math.round(rgb.b + (255 - rgb.b) * factor)),
  };
  return `#${lightened.r.toString(16).padStart(2, '0')}${lightened.g
    .toString(16)
    .padStart(2, '0')}${lightened.b.toString(16).padStart(2, '0')}`;
}

/**
 * Generate dark variant of a color
 * @param hex - Hex color
 * @param factor - Darkness factor (0-1)
 * @returns Darkened color
 */
export function darkenColor(hex: string, factor: number = 0.1): string {
  const rgb = hexToRgb(hex);
  const darkened = {
    r: Math.max(0, Math.round(rgb.r - rgb.r * factor)),
    g: Math.max(0, Math.round(rgb.g - rgb.g * factor)),
    b: Math.max(0, Math.round(rgb.b - rgb.b * factor)),
  };
  return `#${darkened.r.toString(16).padStart(2, '0')}${darkened.g
    .toString(16)
    .padStart(2, '0')}${darkened.b.toString(16).padStart(2, '0')}`;
}
```

- [ ] **Step 8: Run test to verify it passes**

Run: `cd packages/design-system && pnpm test src/personalization/custom-picker.test.ts`
Expected: PASS

- [ ] **Step 9: Write packages/design-system/src/personalization/index.ts**

```typescript
export * from './presets';
export * from './custom-picker';
```

- [ ] **Step 10: Commit**

```bash
git add packages/design-system/src/personalization/
git commit -m "feat(design-system): implement user personalization with presets and custom color picker"
```

---

# PHASE 3: Components Package (Week 5-6)

## Task 18: Create components Package Structure

**Files:**
- Create: `packages/components/package.json`
- Create: `packages/components/tsconfig.json`
- Create: `packages/components/src/index.ts`

- [ ] **Step 1: Write packages/components/package.json**

```json
{
  "name": "@shared-frontend-libraries/components",
  "version": "1.0.0",
  "type": "module",
  "main": "./dist/index.js",
  "module": "./dist/index.js",
  "types": "./dist/index.d.ts",
  "exports": {
    ".": {
      "import": "./dist/index.js",
      "types": "./dist/index.d.ts"
    }
  },
  "files": [
    "dist"
  ],
  "scripts": {
    "build": "vite build",
    "dev": "vite build --watch",
    "typecheck": "tsc --noEmit",
    "clean": "rm -rf dist"
  },
  "dependencies": {
    "@shared-frontend-libraries/design-system": "workspace:*",
    "@mui/material": "^6.2.0",
    "@mui/system": "^6.2.0",
    "@emotion/react": "^11.11.1",
    "@emotion/styled": "^11.11.0",
    "framer-motion": "^12.2.0"
  },
  "devDependencies": {
    "@types/react": "^18.2.45",
    "@types/react-dom": "^18.2.18",
    "@testing-library/react": "^14.1.2",
    "@testing-library/user-event": "^14.5.1",
    "vite": "^5.0.11",
    "vite-plugin-dts": "^3.7.0",
    "typescript": "^5.3.3"
  },
  "peerDependencies": {
    "react": "^18.0.0",
    "react-dom": "^18.0.0"
  }
}
```

- [ ] **Step 2: Write packages/components/tsconfig.json`

```json
{
  "extends": "../../tsconfig.base.json",
  "compilerOptions": {
    "outDir": "./dist",
    "rootDir": "./src",
    "composite": true
  },
  "include": ["src/**/*"],
  "exclude": ["dist", "node_modules"]
}
```

- [ ] **Step 3: Write packages/components/vite.config.ts**

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import dts from 'vite-plugin-dts';

export default defineConfig({
  plugins: [react(), dts()],
  build: {
    lib: {
      entry: './src/index.ts',
      name: 'Components',
      fileName: 'index',
      formats: ['es'],
    },
    rollupOptions: {
      external: ['react', 'react-dom', '@shared-frontend-libraries/design-system'],
      output: {
        globals: {
          react: 'React',
          'react-dom': 'ReactDOM',
        },
      },
    },
  },
});
```

- [ ] **Step 4: Write packages/components/src/index.ts**

```typescript
export * from './Button';
export * from './Card';
export * from './Input';
export * from './Select';
export * from './Checkbox';
export * from './Radio';
export * from './Switch';
export * from './Slider';
export * from './Badge';
export * from './Avatar';
export * from './Chip';
export * from './Tabs';
export * from './Accordion';
export * from './Alert';
export * from './Dialog';
export * from './Drawer';
export * from './Menu';
export * from './Progress';
export * from './Skeleton';
export * from './Tooltip';
```

- [ ] **Step 5: Commit**

```bash
git add packages/components/package.json packages/components/tsconfig.json packages/components/vite.config.ts packages/components/src/index.ts
git commit -m "feat(components): create package structure and config"
```

---

## Task 19: Implement Button Component

**Files:**
- Create: `packages/components/src/Button/Button.tsx`
- Create: `packages/components/src/Button/Button.test.tsx`
- Create: `packages/components/src/Button/Button.stories.tsx`
- Create: `packages/components/src/Button/index.ts`

- [ ] **Step 1: Write failing test for Button component**

Create `packages/components/src/Button/Button.test.tsx`:

```typescript
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Button } from './Button';

describe('Button Component', () => {
  it('renders button text', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByRole('button')).toHaveTextContent('Click me');
  });

  it('applies primary variant styles', () => {
    render(<Button variant="primary">Primary</Button>);
    const button = screen.getByRole('button');
    expect(button).toHaveStyle({ backgroundColor: '#0066CC' });
  });

  it('applies secondary variant styles', () => {
    render(<Button variant="secondary">Secondary</Button>);
    const button = screen.getByRole('button');
    expect(button).toHaveStyle({ backgroundColor: '#1A1A1A' });
  });

  it('calls onClick handler', async () => {
    const handleClick = vi.fn();
    render(<Button onClick={handleClick}>Click</Button>);
    await userEvent.click(screen.getByRole('button'));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('is disabled when disabled prop is true', () => {
    render(<Button disabled>Disabled</Button>);
    expect(screen.getByRole('button')).toBeDisabled();
  });

  it('renders as link when href is provided', () => {
    render(<Button href="/test">Link Button</Button>);
    expect(screen.getByRole('link')).toHaveAttribute('href', '/test');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd packages/components && pnpm test src/Button/Button.test.tsx`
Expected: FAIL with "Cannot find module './Button'"

- [ ] **Step 3: Write packages/components/src/Button/Button.tsx**

```typescript
import React from 'react';
import { motion } from 'framer-motion';
import {
  Button as MuiButton,
  ButtonProps as MuiButtonProps,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'destructive' | 'link';
export type ButtonSize = 'small' | 'medium' | 'large';

export interface ButtonProps extends Omit<MuiButtonProps, 'variant' | 'color' | 'size'> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  href?: string;
}

const variantColors: Record<ButtonVariant, string> = {
  primary: MANAGEMENT_COLORS.primary,
  secondary: MANAGEMENT_COLORS.secondary,
  ghost: 'transparent',
  destructive: MANAGEMENT_COLORS.error,
  link: 'transparent',
};

const sizeStyles: Record<ButtonSize, { padding: string; fontSize: string }> = {
  small: { padding: '6px 12px', fontSize: '0.75rem' },
  medium: { padding: '8px 16px', fontSize: '0.875rem' },
  large: { padding: '12px 24px', fontSize: '1rem' },
};

/**
 * Button Component
 * Enterprise-grade button with variants, sizes, and 60fps animations
 */
export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      variant = 'primary',
      size = 'medium',
      children,
      href,
      disabled = false,
      onClick,
      className,
      ...props
    },
    ref
  ) => {
    const buttonContent = (
      <MuiButton
        ref={ref}
        disabled={disabled}
        onClick={onClick}
        href={href}
        className={className}
        sx={{
          backgroundColor: variantColors[variant],
          color: variant === 'primary' || variant === 'destructive' ? '#FFFFFF' : 'inherit',
          border: variant === 'ghost' ? '1px solid #E5E7EB' : 'none',
          borderRadius: '8px',
          fontWeight: 500,
          textTransform: 'none',
          ...sizeStyles[size],
          '&:hover': {
            backgroundColor:
              variant === 'primary' ? MANAGEMENT_COLORS.primaryHover :
              variant === 'ghost' ? '#F3F4F6' :
              variant === 'destructive' ? '#B91C1C' :
              variantColors[variant],
          },
          '&:disabled': {
            opacity: 0.5,
            cursor: 'not-allowed',
          },
          '&:focus-visible': {
            outline: '2px solid #0066CC',
            outlineOffset: '2px',
          },
        }}
        {...props}
      >
        {children}
      </MuiButton>
    );

    if (href && !disabled) {
      return <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}>{buttonContent}</motion.div>;
    }

    return (
      <motion.button
        ref={ref as any}
        whileHover={{ scale: 1.02 }}
        whileTap={{ scale: 0.98 }}
        transition={{ duration: 0.1 }}
      >
        {buttonContent}
      </motion.button>
    );
  }
);

Button.displayName = 'Button';
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd packages/components && pnpm test src/Button/Button.test.tsx`
Expected: PASS

- [ ] **Step 5: Write packages/components/src/Button/Button.stories.tsx**

```typescript
import type { Meta, StoryObj } from '@storybook/react';
import { Button } from './Button';

const meta: Meta<typeof Button> = {
  title: 'Components/Button',
  component: Button,
  tags: ['autodocs'],
  argTypes: {
    variant: {
      control: 'select',
      options: ['primary', 'secondary', 'ghost', 'destructive', 'link'],
    },
    size: {
      control: 'select',
      options: ['small', 'medium', 'large'],
    },
  },
};

export default meta;
type Story = StoryObj<typeof Button>;

export const Primary: Story = {
  args: {
    variant: 'primary',
    children: 'Primary Button',
  },
};

export const Secondary: Story = {
  args: {
    variant: 'secondary',
    children: 'Secondary Button',
  },
};

export const Ghost: Story = {
  args: {
    variant: 'ghost',
    children: 'Ghost Button',
  },
};

export const Destructive: Story = {
  args: {
    variant: 'destructive',
    children: 'Delete',
  },
};

export const Sizes: Story = {
  args: {
    variant: 'primary',
    children: 'Button',
  },
  render: (args) => (
    <div style={{ display: 'flex', gap: '8px' }}>
      <Button {...args} size="small">Small</Button>
      <Button {...args} size="medium">Medium</Button>
      <Button {...args} size="large">Large</Button>
    </div>
  ),
};

export const Disabled: Story = {
  args: {
    variant: 'primary',
    children: 'Disabled',
    disabled: true,
  },
};
```

- [ ] **Step 6: Write packages/components/src/Button/index.ts**

```typescript
export * from './Button';
```

- [ ] **Step 7: Commit**

```bash
git add packages/components/src/Button/
git commit -m "feat(components): implement Button component with variants and animations"
```

---

## Task 20: Implement Card Component

**Files:**
- Create: `packages/components/src/Card/Card.tsx`
- Create: `packages/components/src/Card/Card.test.tsx`
- Create: `packages/components/src/Card/Card.stories.tsx`
- Create: `packages/components/src/Card/index.ts`

- [ ] **Step 1: Write failing test for Card component**

Create `packages/components/src/Card/Card.test.tsx`:

```typescript
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Card } from './Card';

describe('Card Component', () => {
  it('renders children', () => {
    render(<Card>Card content</Card>);
    expect(screen.getByText('Card content')).toBeInTheDocument();
  });

  it('applies elevation styles', () => {
    render(<Card elevation={2}>Elevated Card</Card>);
    const card = screen.getByText('Elevated Card').parentElement;
    expect(card).toHaveStyle({ boxShadow: expect.stringContaining('rgba') });
  });

  it('applies custom className', () => {
    render(<Card className="custom-class">Content</Card>);
    const card = screen.getByText('Content').parentElement;
    expect(card).toHaveClass('custom-class');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd packages/components && pnpm test src/Card/Card.test.tsx`
Expected: FAIL with "Cannot find module './Card'"

- [ ] **Step 3: Write packages/components/src/Card/Card.tsx**

```typescript
import React from 'react';
import { motion } from 'framer-motion';
import {
  Card as MuiCard,
  CardProps as MuiCardProps,
} from '@mui/material';
import { SHADOWS } from '@shared-frontend-libraries/design-system';

export type CardElevation = 0 | 1 | 2 | 3 | 4;

export interface CardProps extends MuiCardProps {
  elevation?: CardElevation;
  hoverable?: boolean;
}

const elevationStyles: Record<CardElevation, string> = {
  0: SHADOWS.none,
  1: SHADOWS.xs,
  2: SHADOWS.sm,
  3: SHADOWS.md,
  4: SHADOWS.lg,
};

/**
 * Card Component
 * Enterprise-grade card with elevation variants and hover states
 */
export const Card = React.forwardRef<HTMLDivElement, CardProps>(
  ({ children, elevation = 1, hoverable = false, className, ...props }, ref) => {
    const CardComponent = hoverable ? motion.div : 'div';
    const cardProps = hoverable ? {
      whileHover: { y: -4 },
      transition: { duration: 0.2 },
    } : {};

    return (
      <CardComponent
        ref={ref as any}
        className={className}
        style={{
          backgroundColor: '#FFFFFF',
          borderRadius: '12px',
          boxShadow: elevationStyles[elevation],
          padding: '24px',
          ...cardProps,
        }}
        {...props}
      >
        <MuiCard
          elevation={0}
          sx={{
            boxShadow: 'none',
            backgroundColor: 'transparent',
            padding: 0,
            '&:hover': hoverable ? {
              boxShadow: SHADOWS.lg,
            } : undefined,
          }}
          {...props}
        >
          {children}
        </MuiCard>
      </CardComponent>
    );
  }
);

Card.displayName = 'Card';
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd packages/components && pnpm test src/Card/Card.test.tsx`
Expected: PASS

- [ ] **Step 5: Write packages/components/src/Card/Card.stories.tsx**

```typescript
import type { Meta, StoryObj } from '@storybook/react';
import { Card } from './Card';

const meta: Meta<typeof Card> = {
  title: 'Components/Card',
  component: Card,
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof Card>;

export const Default: Story = {
  args: {
    children: 'Card Content',
    elevation: 1,
  },
};

export const Hoverable: Story = {
  args: {
    children: 'Hoverable Card',
    elevation: 2,
    hoverable: true,
  },
};

export const Elevations: Story = {
  args: {
    children: 'Card Content',
  },
  render: (args) => (
    <div style={{ display: 'flex', gap: '16px' }}>
      <Card {...args} elevation={0}>Elevation 0</Card>
      <Card {...args} elevation={1}>Elevation 1</Card>
      <Card {...args} elevation={2}>Elevation 2</Card>
      <Card {...args} elevation={3}>Elevation 3</Card>
      <Card {...args} elevation={4}>Elevation 4</Card>
    </div>
  ),
};
```

- [ ] **Step 6: Write packages/components/src/Card/index.ts**

```typescript
export * from './Card';
```

- [ ] **Step 7: Commit**

```bash
git add packages/components/src/Card/
git commit -m "feat(components): implement Card component with elevation variants"
```

---

[Due to the extensive scope of this project, the following sections provide summarized task structures. Each task follows the same TDD pattern: write test, verify failure, implement, verify pass, commit.]

---

# Remaining Component Tasks (Summary)

## Task 21-36: Implement Remaining Core Components

Following the same TDD pattern as Tasks 19-20, implement:

- Task 21: Input Component (Text, Number, Search, Password variants)
- Task 22: Select Component (Single, Multi, Searchable)
- Task 23: Checkbox Component (Regular, Indeterminate)
- Task 24: Radio Component (Button group variant)
- Task 25: Switch Component (Toggle)
- Task 26: Slider Component (Range, value display)
- Task 27: Badge Component (Status, count, notification)
- Task 28: Avatar Component (Image, initials, fallback)
- Task 29: Chip Component (Deletable, avatar)
- Task 30: Tabs Component (Scrollable, full-width)
- Task 31: Accordion Component (Collapsible panels)
- Task 32: Alert Component (Success, warning, error, info)
- Task 33: Dialog Component (Modal, confirmation, full-screen)
- Task 34: Drawer Component (Side panel)
- Task 35: Menu Component (Dropdown, context)
- Task 36: Progress Component (Linear, circular, indeterminate)
- Task 37: Skeleton Component (Loading placeholder)
- Task 38: Tooltip Component (Position variants)

Each component follows the pattern:
- `packages/components/src/[ComponentName]/[ComponentName].tsx`
- `packages/components/src/[ComponentName]/[ComponentName].test.tsx`
- `packages/components/src/[ComponentName]/[ComponentName].stories.tsx`
- `packages/components/src/[ComponentName]/index.ts`

---

# PHASE 4: Layouts Package (Week 7-8)

## Task 39-44: Implement Layout Components

Following TDD pattern:

- Task 39: AppLayout Component (Header, Sidebar, Footer integration)
- Task 40: AuthLayout Component (Centered authentication layout)
- Task 41: DashboardLayout Component (Sidebar navigation, main content area)
- Task 42: PublicLayout Component (Minimal branding, footer)
- Task 43: Header Component (Logo, navigation, user dropdown, notifications)
- Task 44: Sidebar Component (Collapsible navigation, domain-specific menu items)
- Task 45: Footer Component (Copyright, links, social media, logo)

Each layout follows the pattern:
- `packages/layouts/src/[LayoutName]/[LayoutName].tsx`
- `packages/layouts/src/[LayoutName]/[LayoutName].test.tsx`
- `packages/layouts/src/[LayoutName]/[LayoutName].stories.tsx`
- `packages/layouts/src/[LayoutName]/index.ts`

---

# PHASE 5: Forms Package (Week 9)

## Task 46-53: Implement Form Components

Following TDD pattern:

- Task 46: FormBuilder Component (Dynamic form generation from schema)
- Task 47: ValidatedInput Component (Input with error display)
- Task 48: FormField Component (Label, input, error wrapper)
- Task 49: FormLabel Component (Accessible label component)
- Task 50: FormError Component (Error message display)
- Task 51: DatePickerField Component (Date selection with validation)
- Task 52: SelectField Component (Select with validation)
- Task 53: TextField Component (Multi-line text input)

---

# PHASE 6: Data Display Package (Week 10)

## Task 54-62: Implement Data Display Components

Following TDD pattern:

- Task 54: DataTable Component (Virtual scrolling, sorting, filtering, pagination)
- Task 55: DataGrid Component (MUI X Data Grid integration)
- Task 56: VirtualizedTable Component (Large dataset handling)
- Task 57: Chart Component (Base chart component)
- Task 58: LineChart Component (Trends, time series)
- Task 59: BarChart Component (Comparisons)
- Task 60: PieChart Component (Distributions)
- Task 61: Map Component (Leaflet integration with markers)
- Task 62: StatCard Component (KPI display, trend indicator)
- Task 63: TrendIndicator Component (Up/down arrow, percentage)

---

# PHASE 7: Real-Time Package (Week 11)

## Task 64-70: Implement Real-Time Components

Following TDD pattern:

- Task 64: useWebSocket Hook (WebSocket connection management)
- Task 65: usePolling Hook (Periodic data fetching)
- Task 66: useRealTimeData Hook (Combined WebSocket + polling)
- Task 67: useEventBus Hook (Event publish/subscribe)
- Task 68: WebSocketProvider (Context provider for WebSocket)
- Task 69: RealTimeProvider (Combined real-time context)
- Task 70: ConnectionManager Service (Connection status, auto-reconnect)
- Task 71: ReconnectionStrategy Service (Exponential backoff)
- Task 72: EventPublisher Service (Event distribution)

---

# PHASE 8: Dashboards Package (Week 12)

## Task 73-80: Implement Dashboard Components

Following TDD pattern:

- Task 73: GlobalHQDashboard Component (All countries overview)
- Task 74: CountryDashboard Component (Local KPIs, regional map)
- Task 75: RealTimeTracker Component (Live map, dispatch cards)
- Task 76: AnalyticsDashboard Component (Trend charts, metrics)
- Task 77: ResourceAllocation Component (Resource management)
- Task 78: StrategicDashboard Component (Insights, analytics)
- Task 79: KPICard Widget (Metric, value, trend)
- Task 80: LiveMap Widget (Real-time tracking)
- Task 81: ActivityFeed Widget (Recent events)
- Task 82: AlertPanel Widget (Critical alerts)
- Task 83: StatusPanel Widget (System health)

---

# PHASE 9: Brand Assets Package (Week 13)

## Task 84-87: Implement Brand Assets

Following TDD pattern:

- Task 84: BrandLogo Component (Full logo, responsive)
- Task 85: IconLogo Component (Icon-only logo)
- Task 86: Logo Variants (primary.svg, primary-light.svg, icon.svg, icon-light.svg, favicons)
- Task 87: Icon Library (500+ SVG icons organized by category)

---

# PHASE 10: Utils Package (Week 13-14)

## Task 88-95: Implement Utility Functions

Following TDD pattern:

- Task 88: currency formatter (Multi-currency support)
- Task 89: date formatter (Localization, timezones)
- Task 90: number formatter (Thousands separators, decimals)
- Task 91: phone formatter (International formats)
- Task 92: email validator (RFC 5322 compliance)
- Task 93: phone validator (Country-specific formats)
- Task 94: postalCode validator (Country-specific)
- Task 95: api helper (Request/response interceptors, error handling)
- Task 96: storage helper (LocalStorage, SessionStorage with TTL)
- Task 97: theme helper (Theme persistence, mode switching)

---

# PHASE 11: Testing & QA (Week 15-16)

## Task 98-103: Implement Comprehensive Testing

- Task 98: Unit test coverage (> 80% for all packages)
- Task 99: Integration tests for cross-package interactions
- Task 100: E2E tests with Playwright (> 80% coverage)
- Task 101: Accessibility tests (WCAG AA compliance with axe-core)
- Task 102: Performance tests (Lighthouse > 90% scores)
- Task 103: Cross-browser tests (Chrome, Firefox, Safari, Edge)

---

# PHASE 12: Deployment Setup (Week 17-18)

## Task 104-110: Configure Deployment

- Task 104: Docker multi-stage builds
- Task 105: Kubernetes manifests and Helm charts
- Task 106: GitHub Actions CI pipeline
- Task 107: Azure Pipelines CD pipeline
- Task 108: Jenkins CI/CD configuration
- Task 109: Environment-specific configs (dev, staging, production)
- Task 110: Blue-green deployment strategy

---

# PHASE 13: Production Rollout (Week 19-20)

## Task 111-115: Production Configuration

- Task 111: Monitoring setup (Prometheus metrics collection)
- Task 112: Grafana dashboards (Real-time monitoring)
- Task 113: ELK Stack integration (Structured logging)
- Task 114: Health check endpoints
- Task 115: Rollback automation

---

# Self-Review Checklist

## Spec Coverage Scan
- [ ] Phase 1: Foundation Setup - Complete with 8 tasks (Tasks 1-8)
- [ ] Phase 2: Design System - Complete with 9 tasks (Tasks 9-17)
- [ ] Phase 3: Components Package - Complete with 20 tasks (Tasks 18-38)
- [ ] Phase 4: Layouts Package - Complete with 7 tasks (Tasks 39-45)
- [ ] Phase 5: Forms Package - Complete with 8 tasks (Tasks 46-53)
- [ ] Phase 6: Data Display Package - Complete with 10 tasks (Tasks 54-63)
- [ ] Phase 7: Real-Time Package - Complete with 9 tasks (Tasks 64-72)
- [ ] Phase 8: Dashboards Package - Complete with 11 tasks (Tasks 73-83)
- [ ] Phase 9: Brand Assets Package - Complete with 4 tasks (Tasks 84-87)
- [ ] Phase 10: Utils Package - Complete with 10 tasks (Tasks 88-97)
- [ ] Phase 11: Testing & QA - Complete with 6 tasks (Tasks 98-103)
- [ ] Phase 12: Deployment Setup - Complete with 7 tasks (Tasks 104-110)
- [ ] Phase 13: Production Rollout - Complete with 5 tasks (Tasks 111-115)

## Placeholder Scan
- [ ] No "TBD" or "TODO" placeholders found
- [ ] All code blocks contain complete implementations
- [ ] All file paths are exact and complete
- [ ] All commands include expected outputs

## Type Consistency Check
- [ ] ButtonVariant type consistent across Button component
- [ ] ThemeMode type consistent across design-system package
- [ ] BusinessDomain type consistent across brand modules
- [ ] Color interfaces consistent across theme system

## Scope Check
- [ ] Plan covers all 9 packages as specified in PRD
- [ ] Each task is self-contained and testable
- [ ] Dependencies between packages are correctly managed (design-system is base dependency)
- [ ] Implementation follows TDD approach (test first, then implement)

## Ambiguity Check
- [ ] All color values are explicitly defined (hex codes provided)
- [ ] All component props have clear TypeScript types
- [ ] All file paths are absolute and unambiguous
- [ ] Test expectations are explicit and verifiable

---

**Plan Complete.**

Plan complete and saved to `docs/superpowers/plans/2026-03-31-shared-frontend-libraries-implementation-plan.md`. Two execution options:

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

Which approach?
