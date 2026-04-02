# Foundation Domain - Centralized Dashboard Web

A production-ready React-based web dashboard for monitoring and managing the Foundation Domain microservices platform.

## Features

- **Real-time Monitoring**: WebSocket-based live updates for metrics, alerts, and service health
- **Service Health Monitoring**: Track status, uptime, and performance of all 83 Foundation Domain services
- **Analytics Dashboard**: Visual charts and graphs for request trends, error rates, and response times
- **Alert Management**: Real-time alert notifications with severity levels and acknowledgment
- **Reports Generation**: Generate and download performance, security, and usage reports
- **Responsive Design**: Mobile-friendly interface with sidebar navigation
- **Authentication**: JWT-based authentication with secure session management

## Tech Stack

- **React 18**: Latest React with hooks and concurrent features
- **Vite**: Fast build tool and dev server
- **Zustand**: Lightweight state management with persistence
- **React Router v6**: Client-side routing
- **Socket.io Client**: WebSocket for real-time updates
- **Axios**: HTTP client with interceptors
- **Recharts**: Data visualization charts

## Project Structure

```
src/
├── App.jsx                 # Main application with routing
├── App.css                 # Global styles and design system
├── main.jsx               # Application entry point
├── components/
│   ├── layout/
│   │   ├── MainLayout.jsx  # Sidebar navigation and header
│   │   └── MainLayout.css
│   ├── widgets/
│   │   ├── MetricWidget.jsx  # Metric display cards
│   │   └── MetricWidget.css
│   ├── charts/            # Chart components (future)
│   └── dashboard/         # Dashboard-specific components (future)
├── pages/
│   ├── LoginPage.jsx      # Authentication page
│   ├── LoginPage.css
│   ├── DashboardOverview.jsx  # Main dashboard
│   ├── DashboardOverview.css
│   ├── ServiceHealth.jsx  # Service monitoring
│   ├── ServiceHealth.css
│   ├── Analytics.jsx      # Analytics and charts
│   ├── Analytics.css
│   ├── Reports.jsx        # Reports generation
│   ├── Reports.css
│   ├── Settings.jsx       # Application settings
│   └── Settings.css
├── store/
│   ├── authStore.js       # Authentication state
│   ├── websocketStore.js  # WebSocket connection
│   └── dashboardStore.js  # Dashboard state
├── services/
│   └── api.js             # API client with interceptors
├── utils/
│   ├── helpers.js         # Utility functions
│   └── constants.js       # App constants
└── hooks/                 # Custom React hooks (future)
```

## Getting Started

### Prerequisites

- Node.js 18+
- npm or yarn

### Installation

```bash
npm install
```

### Environment Variables

Create a `.env` file in the root directory:

```env
VITE_API_URL=http://localhost:3000/api
VITE_WS_URL=ws://localhost:3000
```

### Development

```bash
npm run dev
```

The dashboard will be available at `http://localhost:5173`

### Build for Production

```bash
npm run build
```

The built files will be in the `dist/` directory.

### Preview Production Build

```bash
npm run preview
```

## Default Credentials

For demo/testing purposes:

- **Email**: admin@foundation.local
- **Password**: admin123

## API Integration

The dashboard is designed to connect to the Foundation Domain backend services:

- **API Gateway**: `http://localhost:3000/api`
- **WebSocket**: `ws://localhost:3000`

### API Endpoints

- `POST /api/auth/login` - User authentication
- `GET /api/dashboards` - Get dashboard configurations
- `GET /api/metrics` - Get system metrics
- `GET /api/services` - Get service list and health
- `GET /api/alerts` - Get alerts
- `GET /api/reports` - Get available reports

## State Management

### Auth Store

Manages user authentication state:

```javascript
import useAuthStore from './store/authStore';

const { isAuthenticated, user, login, logout } = useAuthStore();
```

### WebSocket Store

Manages real-time connections:

```javascript
import useWebSocketStore from './store/websocketStore';

const { connected, metrics, alerts, serviceHealth } = useWebSocketStore();
```

### Dashboard Store

Manages dashboard widgets and layouts:

```javascript
import useDashboardStore from './store/dashboardStore';

const { widgets, addWidget, removeWidget } = useDashboardStore();
```

## Utility Functions

### Formatting

```javascript
import { formatNumber, formatCurrency, formatDateTime, formatDuration } from './utils/helpers';

formatNumber(1234567);  // "1,234,567"
formatCurrency(123.45); // "€123.45"
formatDateTime(date);   // "25 Dec 2025, 14:30"
formatDuration(3665);   // "1h 1m"
```

### Status Helpers

```javascript
import { getStatusColor, getTimeAgo } from './utils/helpers';

getStatusColor('HEALTHY');  // 'success'
getTimeAgo(date);           // '2 hours ago'
```

## Styling

The dashboard uses CSS custom properties for theming:

```css
:root {
  --primary-color: #1890ff;
  --success-color: #52c41a;
  --warning-color: #faad14;
  --error-color: #ff4d4f;
}
```

## Features by Page

### Dashboard Overview
- Key performance metrics
- Real-time WebSocket connection status
- Live metrics feed
- Recent alerts list

### Service Health
- Service health status table
- Filter by status (Healthy, Warning, Critical)
- Uptime and request statistics
- Per-service detail views

### Analytics
- Request trend charts
- Error rate visualization
- Response time percentiles
- Top services by request volume

### Reports
- Report generation interface
- Download reports (PDF, CSV)
- Scheduled reports management
- Recent reports history

### Settings
- Dashboard configuration
- Notification preferences
- API connection settings
- Display preferences

## Production Deployment

### Docker Deployment

```dockerfile
FROM node:18-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### Environment-Specific Configuration

Update API URLs for production:

```env
VITE_API_URL=https://api.production.com/api
VITE_WS_URL=wss://api.production.com
```

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

Proprietary - Gogidix Foundation Domain Platform

## Support

For issues or questions, contact the Foundation Domain team.
