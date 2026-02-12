# AI Services Monitoring Dashboard

A comprehensive real-time monitoring dashboard for all 27 AI Services in the RapidAssist Foundation Domain.

## Features

- **Real-time Monitoring**: Live status updates for all 27 AI services via WebSocket
- **Service Health Tracking**: Monitor healthy, degraded, and down services
- **Performance Metrics**: CPU usage, memory usage, response times, error rates
- **Interactive Charts**: Visual representations of service metrics and trends
- **Alert Management**: View, acknowledge, and resolve service alerts
- **Category Filtering**: Filter services by Core AI, Business Intelligence, and Business Operations
- **Search**: Quick search across all services
- **Customizable Settings**: Configure refresh intervals, alert thresholds, and notifications

## Services Monitored

### Core AI Services (13)
- AI Leads Generator
- AI Content Generator
- AI Document Analyzer
- AI Recommendation Engine
- AI Chatbot
- AI Image Recognition
- AI Speech Recognition
- AI Data Prediction
- AI Sentiment Analysis
- AI Translation
- AI Anomaly Detection
- AI Text Summarization
- AI Voice Assistant

### Business Intelligence Services (7)
- AI Training ML
- Analytics Service
- Customer Behaviour Analytics
- Data Analytics
- Predictive Maintenance
- Sentiment Analysis (Legacy)
- Vendors Product Listing AI

### Business Operations Services (7)
- Customer Support Chatbot
- Document Intelligence
- Dynamic Pricing
- Fraud Detection
- Intelligent Dispatch
- Recommendation Engine
- Route Optimization

## Installation

```bash
cd Dashboard
npm install
```

## Development

```bash
npm run dev
```

The dashboard will be available at `http://localhost:3000`

## Build for Production

```bash
npm run build
```

The optimized build will be in the `dist` directory.

## Configuration

The dashboard can be configured via the Settings page:

- **API Base URL**: Default `http://localhost:8080`
- **WebSocket URL**: Default `ws://localhost:8080/ws`
- **Refresh Interval**: 10s, 30s, 1min, 5min
- **Alert Thresholds**: CPU, Memory, Response Time, Error Rate
- **Notifications**: Browser notifications, sound alerts

## Pages

1. **Dashboard** (`/`) - Overview of all services with key metrics
2. **Services** (`/services`) - Detailed view of all services with filtering
3. **Metrics** (`/metrics`) - Detailed analytics and charts
4. **Alerts** (`/alerts`) - Alert management and resolution
5. **Settings** (`/settings`) - Dashboard configuration

## Technology Stack

- **React 18** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool
- **Tailwind CSS** - Styling
- **Recharts** - Data visualization
- **React Router** - Navigation
- **Lucide Icons** - Icon library
- **Axios** - HTTP client
- **Socket.IO** - WebSocket communication

## API Integration

The dashboard connects to AI services via:
- HTTP REST API for service health checks (`/actuator/health`)
- WebSocket for real-time metrics updates (`/ws`)

## Project Structure

```
Dashboard/
├── public/
├── src/
│   ├── components/       # Reusable components
│   │   ├── Header.tsx
│   │   ├── Sidebar.tsx
│   │   ├── ServiceCard.tsx
│   │   ├── MetricCard.tsx
│   │   ├── Search.tsx
│   │   └── ...
│   ├── contexts/         # React contexts
│   │   ├── ServicesContext.tsx
│   │   └── WebSocketContext.tsx
│   ├── data/            # Static data
│   │   └── services.ts
│   ├── pages/           # Page components
│   │   ├── DashboardOverview.tsx
│   │   ├── ServicesPage.tsx
│   │   ├── MetricsPage.tsx
│   │   ├── AlertsPage.tsx
│   │   └── SettingsPage.tsx
│   ├── App.tsx
│   ├── main.tsx
│   └── index.css
├── index.html
├── package.json
├── tailwind.config.js
├── tsconfig.json
└── vite.config.ts
```

## License

Copyright © 2024 Gogidix. All rights reserved.
