# Foundation Domain API Documentation

Welcome to the Foundation Domain API documentation. This page provides access to API documentation for all 88 microservices in the Foundation Domain.

## Quick Links

### API Documentation by Category

#### [AI Services](#ai-services)
- [AI Anomaly Detection Service](#ai-anomaly-detection-service)
- [AI Chatbot Service](#ai-chatbot-service)
- [AI Content Generator Service](#ai-content-generator-service)
- [AI Data Prediction Service](#ai-data-prediction-service)
- [AI Document Analyzer Service](#ai-document-analyzer-service)
- [AI Image Recognition Service](#ai-image-recognition-service)
- [AI Leads Generator Service](#ai-leads-generator-service)
- [AI Recommendation Engine Service](#ai-recommendation-engine-service)
- [AI Sentiment Analysis Service](#ai-sentiment-analysis-service)
- [AI Speech Recognition Service](#ai-speech-recognition-service)
- [AI Text Summarization Service](#ai-text-summarization-service)
- [AI Training ML Service](#ai-training-ml-service)
- [AI Translation Service](#ai-translation-service)
- [AI Voice Assistant Service](#ai-voice-assistant-service)
- [Analytics Service](#analytics-service)
- [Customer Behaviour Analytics Service](#customer-behaviour-analytics-service)
- [Customer Support Chatbot Service](#customer-support-chatbot-service)
- [Data Analytics Service](#data-analytics-service)
- [Document Intelligence Service](#document-intelligence-service)
- [Dynamic Pricing Service](#dynamic-pricing-service)
- [Fraud Detection Service](#fraud-detection-service)
- [Intelligent Dispatch Service](#intelligent-dispatch-service)
- [Predictive Maintenance Service](#predictive-maintenance-service)
- [Recommendation Engine Service](#recommendation-engine-service)
- [Route Optimization Service](#route-optimization-service)
- [Sentiment Analysis Service](#sentiment-analysis-service)
- [Vendors Product Listing AI Service](#vendors-product-listing-ai-service)

#### [Central Configuration Services](#central-configuration-services)
- [Config Service](#config-service)
- [Feature Flags Service](#feature-flags-service)
- [Country Localization Config Service](#country-localization-config-service)
- [Dynamic Routing Config Service](#dynamic-routing-config-service)
- [Policy Configuration Service](#policy-configuration-service)
- [Rate Limit Policy Service](#rate-limit-policy-service)
- [Release Rollout Config Service](#release-rollout-config-service)
- [Tenancy Configuration Service](#tenancy-configuration-service)

#### [Centralized Dashboard Services](#centralized-dashboard-services)
- [Dashboard Analytics Service](#dashboard-analytics-service)
- [Dashboard Configuration Service](#dashboard-configuration-service)
- [Dashboard Reporting Service](#dashboard-reporting-service)
- [Dashboard Aggregation Service](#dashboard-aggregation-service)

#### [Shared Infrastructure Services](#shared-infrastructure-services)
- [Access Control Service](#access-control-service)
- [Alerting Service](#alerting-service)
- [Anti-Fraud Rules Service](#anti-fraud-rules-service)
- [Anti-Fraud Signals Service](#anti-fraud-signals-service)
- [API Gateway](#api-gateway)
- [API Keys Service](#api-keys-service)
- [Audit Correlation Service](#audit-correlation-service)
- [Billing Service](#billing-service)
- [Courier Adapter Service](#courier-adapter-service)
- [Currency Converter Service](#currency-converter-service)
- [Database Management Service](#database-management-service)
- [Data Privacy Consent Service](#data-privacy-consent-service)
- [Event Audit Service](#event-audit-service)
- [Geo Location Service](#geo-location-service)
- [Idempotency Service](#idempotency-service)
- [Identity Access Service](#identity-access-service)
- [Identity Service](#identity-service)
- [Insurer Adapter Service](#insurer-adapter-service)
- [Integration Adapters Service](#integration-adapters-service)
- [Logging Aggregation Service](#logging-aggregation-service)
- [Maps Geocoding Adapter Service](#maps-geocoding-adapter-service)
- [Metrics Telemetry Service](#metrics-telemetry-service)
- [MFA Service](#mfa-service)
- [Notification Service](#notification-service)
- [Onboarding Service](#onboarding-service)
- [Payments Adapter Service](#payments-adapter-service)
- [Payment Service](#payment-service)
- [Policy Engine Service](#policy-engine-service)
- [Pricing Service](#pricing-service)
- [Rate Limiting Service](#rate-limiting-service)
- [Reporting Read Model Service](#reporting-read-model-service)
- [Request Routing Service](#request-routing-service)
- [Service Health Monitor Service](#service-health-monitor-service)
- [Service Registry Discovery](#service-registry-discovery)
- [Session Token Service](#session-token-service)
- [Template Messaging Service](#template-messaging-service)
- [Tenant Org Service](#tenant-org-service)
- [User Profile Service](#user-profile-service)
- [Webhook Delivery Service](#webhook-delivery-service)

#### [Shared Libraries](#shared-libraries)
- [Common Domain Models](#common-domain-models)
- [Event Schemas](#event-schemas)
- [Shared Security Library](#shared-security-library)
- [Shared Exception Library](#shared-exception-library)
- [Shared Request Context Library](#shared-request-context-library)
- [Shared Audit Library](#shared-audit-library)
- [Shared Idempotency Library](#shared-idempotency-library)
- [Shared Observability Library](#shared-observability-library)

---

## Authentication

All API requests require authentication using a Bearer token:

```http
Authorization: Bearer <your-jwt-token>
X-Tenant-ID: <your-tenant-id>
```

### Obtaining an API Token

```http
POST /api/identity/auth/login
Content-Type: application/json

{
  "username": "your-username",
  "password": "your-password"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400,
  "user": {
    "id": "123",
    "username": "your-username"
  }
}
```

---

## Common Response Format

All API responses follow this standard format:

```json
{
  "success": true,
  "data": { ... },
  "message": "Operation successful",
  "timestamp": "2024-01-15T10:30:00Z",
  "requestId": "req-abc123"
}
```

### Error Response Format

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": [
      {
        "field": "email",
        "message": "Email is required"
      }
    ]
  },
  "timestamp": "2024-01-15T10:30:00Z",
  "requestId": "req-abc123"
}
```

---

## API Services

### AI Services

#### AI Anomaly Detection Service
**Base URL:** `http://api.gogidix.com:8100`

**Port:** 8100

**Endpoints:**
- `POST /api/anomaly/detect` - Detect anomalies in data
- `POST /api/anomaly/train` - Train anomaly detection model
- `GET /api/anomaly/models` - List available models

[View Full Documentation →](../ai-services/ai-anomaly-detection-service/README.md)

---

#### AI Chatbot Service
**Base URL:** `http://api.gogidix.com:8101`

**Port:** 8101

**Endpoints:**
- `POST /api/chatbot/message` - Send message to chatbot
- `POST /api/chatbot/session` - Create chat session
- `GET /api/chatbot/history/{sessionId}` - Get conversation history

[View Full Documentation →](../ai-services/ai-chatbot-service/README.md)

---

### Central Configuration Services

#### Config Service
**Base URL:** `http://api.gogidix.com:8000`

**Port:** 8000

**Endpoints:**
- `GET /api/config` - Get all configurations
- `GET /api/config/{key}` - Get specific configuration
- `PUT /api/config/{key}` - Update configuration
- `DELETE /api/config/{key}` - Delete configuration

[View Full Documentation →](../central-configuration/config-service/README.md)

---

#### Feature Flags Service
**Base URL:** `http://api.gogidix.com:8001`

**Port:** 8001

**Endpoints:**
- `GET /api/feature-flags` - Get all feature flags
- `GET /api/feature-flags/{flagName}` - Get specific flag
- `POST /api/feature-flags` - Create feature flag
- `PUT /api/feature-flags/{flagName}` - Update feature flag

[View Full Documentation →](../central-configuration/feature-flags-service/README.md)

---

### Shared Infrastructure Services

#### Identity Service
**Base URL:** `http://api.gogidix.com:8316`

**Port:** 8316

**Endpoints:**
- `POST /api/identity/auth/login` - User login
- `POST /api/identity/auth/logout` - User logout
- `POST /api/identity/auth/refresh` - Refresh access token
- `POST /api/identity/users` - Create user
- `GET /api/identity/users/{id}` - Get user by ID
- `PUT /api/identity/users/{id}` - Update user

[View Full Documentation →](../shared-infrastructure/identity-service/README.md)

---

#### API Gateway
**Base URL:** `http://api.gogidix.com:8304`

**Port:** 8304

**Endpoints:**
- All service routes are proxied through the gateway
- `GET /actuator/health` - Gateway health check
- `GET /routes` - List all registered routes

[View Full Documentation →](../shared-infrastructure/api-gateway/README.md)

---

#### Payment Service
**Base URL:** `http://api.gogidix.com:8326`

**Port:** 8326

**Endpoints:**
- `POST /api/payment/intents` - Create payment intent
- `GET /api/payment/intents/{id}` - Get payment intent
- `POST /api/payment/intents/{id}/capture` - Capture payment
- `POST /api/payment/refunds` - Create refund

[View Full Documentation →](../shared-infrastructure/payment-service/README.md)

---

## Rate Limiting

All API endpoints are rate limited:

| Tier | Requests | Window |
|------|----------|--------|
| Free | 1000 | per hour |
| Basic | 10000 | per hour |
| Pro | 100000 | per hour |
| Enterprise | Unlimited | - |

Rate limit headers are included in all responses:

```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1642252800
```

---

## Webhooks

Services support webhook notifications for real-time events:

### Webhook Endpoints

- `POST /api/webhooks` - Register webhook
- `GET /api/webhooks` - List webhooks
- `DELETE /api/webhooks/{id}` - Delete webhook

### Webhook Events

| Event | Description |
|-------|-------------|
| `payment.created` | New payment created |
| `payment.succeeded` | Payment successful |
| `payment.failed` | Payment failed |
| `user.created` | New user registered |
| `user.updated` | User profile updated |

---

## SDKs and Libraries

Official SDKs are available for:

- [JavaScript/TypeScript](https://github.com/gogidix/foundation-js-sdk)
- [Java](https://github.com/gogidix/foundation-java-sdk)
- [Python](https://github.com/gogidix/foundation-python-sdk)
- [Go](https://github.com/gogidix/foundation-go-sdk)

---

## Support

- **API Status:** [status.gogidix.com](https://status.gogidix.com)
- **Documentation:** [docs.gogidix.com](https://docs.gogidix.com)
- **GitHub:** [github.com/gogidix](https://github.com/gogidix)
- **Email:** api-support@gogidix.com
- **Slack:** [#foundation-api](https://gogidix.slack.com/archives/C0123456789)

---

## Changelog

### v1.0.0 (2024-01-15)
- Initial release of Foundation Domain API
- 88 services now available
- OpenAPI 3.0 specification
- JWT authentication
- Multi-tenancy support

---

*Last Updated: 2025-12-31*
