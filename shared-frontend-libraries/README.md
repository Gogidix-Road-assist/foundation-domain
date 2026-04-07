# Shared Frontend Libraries

> **🟡 Status:** NOT PRODUCTION READY (40% Complete)
>
> **📋 Important:** Read [DOMAIN_STATUS.md](DOMAIN_STATUS.md) before starting work.
> **🤖 For AI Agents:** See [AGENT_GUIDE.md](AGENT_GUIDE.md) for workflow guidance.
> **✅ Quick Check:** Use [QUICK_CHECKLIST.md](QUICK_CHECKLIST.md) for tasks.

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

## Documentation

| Document | Purpose | Audience |
|----------|---------|-----------|
| [DOMAIN_STATUS.md](DOMAIN_STATUS.md) | Complete domain status, blockers, and roadmap | All developers & agents |
| [AGENT_GUIDE.md](AGENT_GUIDE.md) | Workflow guide for AI agents working on this domain | AI Agents |
| [QUICK_CHECKLIST.md](QUICK_CHECKLIST.md) | Quick reference checklist for production readiness | All developers & agents |
| [Getting Started Guide](docs/getting-started.md) | How to use the library in your project | Application developers |

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
