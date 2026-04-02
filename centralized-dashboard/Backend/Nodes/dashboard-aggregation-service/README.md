# Dashboard Aggregation API Service

A comprehensive Node.js service for aggregating and visualizing metrics from all Gogidix RapidAssist platform services.

## Features

### Core Functionality
- **Real-time Metrics Aggregation**: Collects and aggregates metrics from all microservices
- **Dashboard Management**: Dynamic dashboard creation with customizable widgets
- **Service Health Monitoring**: Real-time health monitoring of all services
- **Analytics & Reporting**: Comprehensive analytics and custom report generation
- **Alerts Management**: Real-time alerts with acknowledgment and resolution workflow
- **WebSocket Support**: Real-time updates for live dashboard data

### Key Capabilities
- Multi-tenant architecture with tenant isolation
- Redis-based caching for high-performance data retrieval
- MongoDB integration for persistent data storage
- Scheduled data aggregation and cleanup jobs
- Export functionality in multiple formats (CSV, JSON, PDF, Excel)
- RESTful API with comprehensive validation

## Architecture

The service follows a clean hexagonal architecture pattern:

```
├── src/
│   ├── adapters/
│   │   └── in/http/          # HTTP Controllers and Routes
│   ├── application/         # Use Cases and Business Logic
│   ├── domain/              # Domain Models and Ports
│   ├── infrastructure/       # External Dependencies
│   └── bootstrap/            # Application Bootstrap
```

## Quick Start

### Prerequisites
- Node.js 18+
- MongoDB 6.0+
- Redis 7.0+

### Installation

```bash
# Clone the repository
git clone <repository-url>
cd dashboard-aggregation-service

# Install dependencies
npm install

# Copy environment configuration
cp .env.example .env

# Start the service
npm run dev
```

### Using Docker

```bash
# Build the Docker image
npm run docker:build

# Run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f dashboard-aggregation-service
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Service port | 3000 |
| `NODE_ENV` | Environment | development |
| `MONGODB_URI` | MongoDB connection string | mongodb://localhost:27017/dashboard_aggregation |
| `REDIS_HOST` | Redis host | localhost |
| `REDIS_PORT` | Redis port | 6379 |
| `JWT_SECRET` | JWT secret key | (required) |
| `API_GATEWAY_URL` | API Gateway URL | http://localhost:8080 |
| `DASHBOARD_CACHE_TTL` | Cache TTL in seconds | 300 |
| `METRICS_AGGREGATION_INTERVAL` | Metrics aggregation interval (ms) | 60000 |
| `WEBSOCKET_ENABLED` | Enable WebSocket support | true |

## API Documentation

### Health Check
```http
GET /api/v1/health
```

### Dashboard APIs
```http
GET /api/v1/dashboard                    # Get dashboard
GET /api/v1/dashboard/overview          # Get dashboard overview
GET /api/v1/dashboard/widgets           # Get widgets
POST /api/v1/dashboard/widgets           # Create widget
PUT /api/v1/dashboard/widgets/:id        # Update widget
DELETE /api/v1/dashboard/widgets/:id     # Delete widget
```

### Metrics APIs
```http
GET /api/v1/metrics/summary             # Get metrics summary
GET /api/v1/metrics/services/:id         # Get service metrics
GET /api/v1/metrics/performance          # Get performance metrics
GET /api/v1/metrics/trends               # Get trends data
GET /api/v1/metrics/realtime             # Get real-time metrics
```

### Analytics APIs
```http
GET /api/v1/analytics/overview            # Get analytics overview
GET /api/v1/analytics/conversion          # Get conversion analytics
GET /api/v1/analytics/usage               # Get usage analytics
GET /api/v1/analytics/performance         # Get performance analytics
GET /api/v1/analytics/custom              # Get custom analytics
POST /api/v1/analytics/custom              # Create custom analytics
```

### Service Health APIs
```http
GET /api/v1/services/health               # Get all services health
GET /api/v1/services/health/:id           # Get specific service health
POST /api/v1/services/health/check         # Trigger health check
```

### Alerts APIs
```http
GET /api/v1/alerts                        # Get alerts
GET /api/v1/alerts/:id                    # Get specific alert
POST /api/v1/alerts                        # Create alert
PUT /api/v1/alerts/:id/acknowledge         # Acknowledge alert
PUT /api/v1/alerts/:id/resolve             # Resolve alert
```

### Reports APIs
```http
GET /api/v1/reports                        # Get reports
GET /api/v1/reports/:id                    # Get specific report
POST /api/v1/reports                        # Create report
GET /api/v1/reports/:id/data               # Get report data
POST /api/v1/reports/:id/export            # Export report
```

## WebSocket Events

The service emits real-time updates through WebSocket connections:

```javascript
// Connect to WebSocket
const ws = new WebSocket('ws://localhost:3000');

// Listen for events
ws.onmessage = (event) => {
  const data = JSON.parse(event.data);
  console.log('Received:', data);
};

// Supported event types
- 'metrics-update'      // Real-time metrics updates
- 'health-alert'        // Service health alerts
- 'analytics-update'    // Analytics data updates
- 'alert-created'       // New alert created
- 'alert-acknowledged'  // Alert acknowledged
- 'alert-resolved'      // Alert resolved
```

## Scheduled Jobs

The service includes several scheduled jobs:

- **Metrics Aggregation**: Every 5 minutes
- **System Health Check**: Every 2 minutes
- **Cache Cleanup**: Daily at 2 AM UTC
- **Analytics Refresh**: Every 6 hours

## Development

### Running Tests
```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run with coverage
npm run test:coverage
```

### Linting
```bash
# Check linting
npm run lint

# Fix linting issues
npm run lint:fix
```

## Production Deployment

### Environment Setup
1. Set `NODE_ENV=production`
2. Configure MongoDB and Redis connections
3. Set up proper secrets and keys
4. Configure external service URLs
5. Enable rate limiting and security middleware

### Monitoring
- Service health: `/api/v1/health`
- Metrics endpoint: `/actuator/metrics`
- Application logs: Configured via `LOG_LEVEL`

### Scaling
- Use multiple instances behind a load balancer
- Configure Redis for session sharing
- Use MongoDB replica set for high availability

## Security

- JWT-based authentication
- Rate limiting for API endpoints
- CORS configuration
- Input validation and sanitization
- Helmet.js security headers
- Request payload size limits

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

## License

UNLICENSED - Proprietary software for Gogidix RapidAssist Platform

## Support

For support and questions, please contact the Gogidix development team.