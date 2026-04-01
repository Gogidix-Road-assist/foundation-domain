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
