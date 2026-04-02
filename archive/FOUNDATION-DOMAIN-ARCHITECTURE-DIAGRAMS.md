# Foundation Domain - Architecture Diagrams

**Document Version**: 1.0.0
**Date**: January 11, 2026
**Author**: Agent 4 of 4 (Production Readiness Team)
**Platform**: Gogidix Rapid Assist Platform

---

## Table of Contents

1. [Platform Overview](#1-platform-overview)
2. [Service Integration Diagram](#2-service-integration-diagram)
3. [Per-Domain Architecture](#3-per-domain-architecture)
   - 3.1 AI-Services Domain
   - 3.2 Central-Configuration Domain
   - 3.3 Centralized-Dashboard Domain
   - 3.4 Shared-Infrastructure Domain
   - 3.5 Shared-Libraries Domain
4. [Multi-tenancy Architecture](#4-multi-tenancy-architecture)
5. [Data Flow Architecture](#5-data-flow-architecture)
6. [Security Architecture](#6-security-architecture)
7. [Deployment Architecture](#7-deployment-architecture)

---

## 1. Platform Overview

### 1.1 Foundation Domain Components

```mermaid
graph TB
    subgraph "Foundation Domain"
        AI[AI-Services<br/>27 Java Services]
        CC[Central-Configuration<br/>8 Java Services]
        CD[Centralized-Dashboard<br/>3 Java + 1 Node.js]
        SI[Shared-Infrastructure<br/>39 Java Services]
        SL[Shared-Libraries<br/>8 Java + 3 Frontend Libs]
    end

    subgraph "External Dependencies"
        MongoDB[(MongoDB)]
        Kafka[Kafka]
        Redis[(Redis)]
        Prometheus[Prometheus]
        Grafana[Grafana]
    end

    AI --> MongoDB
    AI --> Kafka
    CC --> MongoDB
    CD --> MongoDB
    SI --> MongoDB
    SI --> Redis
    SI --> Kafka
    SL -.-> AI
    SL -.-> CC
    SL -.-> CD
    SL -.-> SI

    SI --> Prometheus
    SI --> Grafana

    style SL fill:#f9f,stroke:#333,stroke-width:4px
    style SI fill:#bbf,stroke:#333,stroke-width:2px
```

### 1.2 Domain Responsibilities

| Domain | Responsibility | Services | Port Range |
|--------|---------------|----------|------------|
| **AI-Services** | AI/ML capabilities, analytics, recommendations | 27 | 8100-8126 |
| **Central-Configuration** | Feature flags, localization, policies | 8 | 8000-8007 |
| **Centralized-Dashboard** | Dashboards, analytics, reporting | 4 | 3000, 8200-8202 |
| **Shared-Infrastructure** | Cross-cutting infrastructure services | 39 | 8300-8338 |
| **Shared-Libraries** | Reusable components (not deployed) | 0 | N/A |

---

## 2. Service Integration Diagram

### 2.1 How Business and Management Domains Integrate

```mermaid
graph TB
    subgraph "Business Domain Services"
        BS[Booking Service]
        RS[Roadside Service]
        PS[Payment Service]
        NS[Notification Service]
    end

    subgraph "Management Domain Services"
        AS[Admin Service]
        US[User Management]
        OS[Operations Service]
    end

    subgraph "Foundation Domain - Shared-Infrastructure"
        GW[API Gateway<br/>:8304]
        SR[Service Registry<br/>:8333]
        CFG[Config Service<br/>:8000]
        AUTH[Identity Service<br/>:8316]
        AC[Access Control<br/>:8300]
        AUDIT[Audit Service<br/>:8312]
        METRICS[Metrics Service<br/>:8321]
    end

    subgraph "Foundation Domain - Shared-Libraries"
        SL1[common-domain-models]
        SL2[shared-security-library]
        SL3[shared-observability-library]
        SL4[shared-audit-library]
        SL5[shared-request-context]
    end

    BS --> GW
    RS --> GW
    PS --> GW
    NS --> GW
    AS --> GW
    US --> GW
    OS --> GW

    GW --> AUTH
    GW --> AC

    BS -.-> SL2
    RS -.-> SL2
    PS -.-> SL2
    AS -.-> SL2
    US -.-> SL2

    BS -.-> SL3
    RS -.-> SL3
    PS -.-> SL3
    AS -.-> SL3

    BS -.-> SL4
    RS -.-> SL4
    PS -.-> SL4
    AS -.-> SL4

    BS -.-> SL5
    RS -.-> SL5
    PS -.-> SL5

    BS --> SR
    RS --> SR
    PS --> SR
    AS --> SR
    US --> SR

    BS --> CFG
    RS --> CFG
    PS --> CFG
    AS --> CFG
    US --> CFG

    BS --> AUDIT
    RS --> AUDIT
    PS --> AUDIT
    AS --> AUDIT

    BS --> METRICS
    RS --> METRICS
    PS --> METRICS
    AS --> METRICS

    style SL2 fill:#f9f,stroke:#333,stroke-width:2px
    style SL3 fill:#f9f,stroke:#333,stroke-width:2px
    style SL4 fill:#f9f,stroke:#333,stroke-width:2px
    style SL5 fill:#f9f,stroke:#333,stroke-width:2px
```

### 2.2 Library Consumption Pattern

```mermaid
graph LR
    subgraph "Shared-Libraries (Artifacts)"
        LIB1[common-domain-models.jar]
        LIB2[shared-security-library.jar]
        LIB3[shared-observability-library.jar]
    end

    subgraph "Build Time (Maven)"
        MVN[pom.xml]
    end

    subgraph "Runtime (Classpath)"
        CP[Application Classpath]
    end

    LIB1 --> MVN
    LIB2 --> MVN
    LIB3 --> MVN
    MVN --> CP
```

---

## 3. Per-Domain Architecture

### 3.1 AI-Services Domain Architecture

```mermaid
graph TB
    subgraph "AI-Services Domain (27 Services)"
        subgraph "Core AI Services"
            AI1[ai-anomaly-detection<br/>:8100]
            AI2[ai-chatbot<br/>:8101]
            AI3[ai-content-generator<br/>:8102]
            AI4[ai-data-prediction<br/>:8103]
        end

        subgraph "Analytics Services"
            AN1[analytics-service<br/>:8114]
            AN2[customer-behaviour-analytics<br/>:8115]
            AN3[data-analytics-service<br/>:8117]
        end

        subgraph "Business AI Services"
            BA1[fraud-detection<br/>:8120]
            BA2[intelligent-dispatch<br/>:8121]
            BA3[predictive-maintenance<br/>:8122]
            BA4[route-optimization<br/>:8124]
            BA5[dynamic-pricing<br/>:8119]
        end

        subgraph "Support AI Services"
            SA1[customer-support-chatbot<br/>:8116]
            SA2[recommendation-engine<br/>:8123]
            SA3[sentiment-analysis<br/>:8125]
        end
    end

    subgraph "Shared Libraries (Consumed)"
        SL1[common-domain-models]
        SL2[shared-observability]
        SL3[shared-security]
    end

    AI1 -.-> SL1
    AI1 -.-> SL2
    AI1 -.-> SL3
    AI2 -.-> SL1
    AI2 -.-> SL2
    AI3 -.-> SL1
    AI3 -.-> SL2

    style SL1 fill:#f9f,stroke:#333,stroke-width:2px
    style SL2 fill:#f9f,stroke:#333,stroke-width:2px
    style SL3 fill:#f9f,stroke:#333,stroke-width:2px
```

#### AI-Services Hexagonal Architecture (Per Service)

```mermaid
graph TB
    subgraph "AI Service (e.g., ai-chatbot-service)"
        direction TB
        subgraph "Adapter In"
            REST[REST Controller]
            GRPC[gRPC Controller]
        end

        subgraph "Application"
            UC[Chatbot UseCase]
            SVCS[AI Service Integration]
        end

        subgraph "Domain"
            DOMAIN[Chatbot Domain Model]
            PORT_IN[Chatbot Port In]
        end

        subgraph "Adapter Out"
            AI_MODEL[AI Model Client]
            DB[(MongoDB)]
        end
    end

    REST --> UC
    GRPC --> UC
    UC --> PORT_IN
    PORT_IN --> DOMAIN
    UC --> SVCS
    SVCS --> AI_MODEL
    UC --> DB
```

---

### 3.2 Central-Configuration Domain Architecture

```mermaid
graph TB
    subgraph "Central-Configuration Domain (8 Services)"
        CFG1[config-service<br/>:8000]
        FF[feature-flags-service<br/>:8001]
        LOC[country-localization-config<br/>:8002]
        RT[dynamic-routing-config<br/>:8003]
        POL[policy-configuration<br/>:8004]
        RL[rate-limit-policy<br/>:8005]
        REL[release-rollout-config<br/>:8006]
        TEN[tenancy-configuration<br/>:8007]
    end

    subgraph "Shared Libraries"
        SL1[common-domain-models]
        SL2[shared-request-context]
    end

    CFG1 --> GIT[(Git Repo)]
    FF --> CFG1
    LOC --> CFG1
    RT --> CFG1
    POL --> CFG1
    RL --> CFG1
    REL --> CFG1
    TEN --> CFG1

    FF -.-> SL1
    TEN -.-> SL2

    style GIT fill:#ff9,stroke:#333,stroke-width:2px
```

#### Configuration Service Internal Architecture

```mermaid
graph LR
    subgraph "config-service"
        API[REST API]
        CONFIG[Spring Cloud Config]
        GIT[(Git Backend)]
        ENCRYPT[Encrypt/Decrypt]
    end

    subgraph "Consumers"
        S1[Service 1]
        S2[Service 2]
        S3[Service N]
    end

    API --> CONFIG
    CONFIG --> GIT
    API --> ENCRYPT
    S1 --> CONFIG
    S2 --> CONFIG
    S3 --> CONFIG
```

---

### 3.3 Centralized-Dashboard Domain Architecture

```mermaid
graph TB
    subgraph "Centralized-Dashboard Domain (4 Services)"
        DC[dashboard-configuration<br/>:8200]
        DA[dashboard-analytics<br/>:8201]
        DR[dashboard-reporting<br/>:8202]
        DAG[dashboard-aggregation<br/>:3000 - Node.js]
    end

    subgraph "Data Sources"
        M1[(Metrics DB)]
        M2[(Analytics DB)]
        M3[(Reporting DB)]
    end

    DC --> DAG
    DA --> DAG
    DR --> DAG
    DAG --> M1
    DAG --> M2
    DAG --> M3
```

#### Dashboard Aggregation Service (Node.js)

```mermaid
graph TB
    subgraph "dashboard-aggregation-service (Node.js)"
        EXPRESS[Express Server]
        GRAPHQL[GraphQL Resolver]
        AGG[Data Aggregator]
    end

    subgraph "Upstream APIs"
        DC[dashboard-configuration]
        DA[dashboard-analytics]
        DR[dashboard-reporting]
    end

    EXPRESS --> GRAPHQL
    GRAPHQL --> AGG
    AGG --> DC
    AGG --> DA
    AGG --> DR
```

---

### 3.4 Shared-Infrastructure Domain Architecture

```mermaid
graph TB
    subgraph "Shared-Infrastructure Domain (39 Services)"
        subgraph "Gateway & Discovery"
            GW[api-gateway<br/>:8304]
            SR[service-registry<br/>:8333]
            RR[request-routing<br/>:8331]
        end

        subgraph "Security & Identity"
            AUTH[identity-service<br/>:8316]
            IAC[identity-access<br/>:8315]
            AC[access-control<br/>:8300]
            MFA[mfa-service<br/>:8322]
            ST[session-token<br/>:8334]
            AK[api-keys<br/>:8305]
        end

        subgraph "Observability"
            LOG[logging-aggregation<br/>:8319]
            MET[metrics-telemetry<br/>:8321]
            SHM[service-health-monitor<br/>:8332]
        end

        subgraph "Audit & Compliance"
            AUD[event-audit<br/>:8312]
            ACOR[audit-correlation<br/>:8306]
            DP[data-privacy-consent<br/>:8311]
        end

        subgraph "Resilience"
            IDE[idempotency<br/>:8314]
            RL[rate-limiting<br/>:8329]
            AF1[anti-fraud-rules<br/>:8302]
            AF2[anti-fraud-signals<br/>:8303]
        end

        subgraph "Integration"
            IA[integration-adapters<br/>:8318]
            CA[courier-adapter<br/>:8308]
            IA1[insurer-adapter<br/>:8317]
            MA[maps-geocoding<br/>:8320]
            PA[payments-adapter<br/>:8325]
        end

        subgraph "Business Support"
            BILL[billing<br/>:8307]
            PAY[payment<br/>:8326]
            PRIC[pricing<br/>:8328]
            POL[policy-engine<br/>:8327]
            NOTIF[notification<br/>:8323]
            TMPL[template-messaging<br/>:8335]
            ONB[onboarding<br/>:8324]
        end

        subgraph "Data Management"
            DBM[database-management<br/>:8310]
            REP[reporting-read-model<br/>:8330]
            TO[tenant-org<br/>:8336]
            UP[user-profile<br/>:8337]
            GEO[geo-location<br/>:8313]
            CUR[currency-converter<br/>:8309]
            WH[webhook-delivery<br/>:8338]
            ALT[alerting<br/>:8301]
        end
    end

    GW --> SR
    GW --> AUTH
    GW --> AC

    style GW fill:#bbf,stroke:#333,stroke-width:3px
    style AUTH fill:#bbf,stroke:#333,stroke-width:3px
    style SR fill:#bbf,stroke:#333,stroke-width:3px
```

---

### 3.5 Shared-Libraries Domain Architecture

```mermaid
graph TB
    subgraph "Shared-Libraries Domain (Not Deployed)"
        direction TB

        subgraph "Backend Java Libraries (8)"
            CDM[common-domain-models]
            EVT[event-schemas]
            AUD[shared-audit-library]
            EXC[shared-exception-library]
            IDE[shared-idempotency-library]
            OBS[shared-observability-library]
            RCT[shared-request-context]
            SEC[shared-security-library]
        end

        subgraph "Frontend Libraries (3)"
            AFL[admin-framework-library<br/>React]
            UIC[ui-component-library<br/>React]
            TSS[client-sdk-typescript]
        end

        subgraph "Mobile (Empty)"
            MB[Mobile Libraries<br/>NOT IMPLEMENTED]
        end
    end

    subgraph "Consuming Services"
        SVC1[Business Services]
        SVC2[Management Services]
        SVC3[Foundation Services]
    end

    CDM -.-> SVC1
    CDM -.-> SVC2
    CDM -.-> SVC3
    SEC -.-> SVC1
    SEC -.-> SVC2
    SEC -.-> SVC3
    OBS -.-> SVC1
    OBS -.-> SVC2
    OBS -.-> SVC3
    AUD -.-> SVC1
    AUD -.-> SVC2
    AUD -.-> SVC3

    style CDM fill:#f9f,stroke:#333,stroke-width:2px
    style SEC fill:#f9f,stroke:#333,stroke-width:2px
    style OBS fill:#f9f,stroke:#333,stroke-width:2px
    style AUD fill:#f9f,stroke:#333,stroke-width:2px
    style MB fill:#faa,stroke:#333,stroke-width:2px
```

#### Shared Library Internal Architecture

```mermaid
graph TB
    subgraph "shared-security-library (Example)"
        direction TB

        subgraph "Auto-Configuration"
            AUTO[SharedSecurityAutoConfiguration]
        end

        subgraph "Domain"
            PORT_IN[Port In]
            PORT_OUT[Port Out]
        end

        subgraph "Infrastructure"
            JWT[JwtTokenUtil]
            PWD[PasswordUtil]
            MFA[MFAUtil]
            RBAC[RBACService]
        end

        subgraph "API"
            REST[Status Controller]
        end
    end

    AUTO --> PORT_IN
    PORT_IN --> JWT
    PORT_IN --> PWD
    PORT_IN --> MFA
    PORT_IN --> RBAC
    REST --> PORT_IN
```

#### Library Dependency Graph

```mermaid
graph LR
    subgraph "Library Dependencies"
        CDM[common-domain-models]
        EVT[event-schemas]
        SEC[shared-security]
        RCT[shared-request-context]
        OBS[shared-observability]
        AUD[shared-audit]
        IDE[shared-idempotency]
        EXC[shared-exception]
    end

    CDM --> EVT
    SEC --> RCT
    OBS --> RCT
    AUD --> OBS
    AUD --> RCT
    IDE --> RCT
    EXC --> OBS

    style CDM fill:#f9f,stroke:#333,stroke-width:2px
    style RCT fill:#9f9,stroke:#333,stroke-width:2px
    style OBS fill:#9f9,stroke:#333,stroke-width:2px
```

---

## 4. Multi-tenancy Architecture

### 4.1 Tenant Isolation Strategy

```mermaid
graph TB
    subgraph "Request Flow"
        CLIENT[Client Request]
        GW[API Gateway]
        RC[Request Context Filter]
    end

    subgraph "Tenant Identification"
        HDR[Headers:<br/>X-Tenant-ID<br/>X-Organization-ID]
        JWT_CLAIM[JWT Claims:<br/>tenant_id<br/>organization_id]
        SUBDOMAIN[Subdomain:<br/>tenant.api.com]
    end

    subgraph "Tenant Context"
        TC[RequestContext:<br/>tenantId<br/>organizationId<br/>userId]
    end

    subgraph "Data Isolation"
        MONGO[(MongoDB)]
        COLL[(Collections with<br/>tenant_id index)]
    end

    CLIENT --> GW
    GW --> RC
    HDR --> RC
    JWT_CLAIM --> RC
    SUBDOMAIN --> RC
    RC --> TC
    TC --> MONGO
    MONGO --> COLL

    style TC fill:#bbf,stroke:#333,stroke-width:3px
    style COLL fill:#ff9,stroke:#333,stroke-width:2px
```

### 4.2 Tenant Data Isolation

```mermaid
graph LR
    subgraph "Database: rapid_assist_db"
        subgraph "Collection: customers"
            C1[_id, tenant_id, ...]
            C2[_id, tenant_id, ...]
        end

        subgraph "Collection: service_requests"
            S1[_id, tenant_id, ...]
            S2[_id, tenant_id, ...]
        end

        subgraph "Collection: providers"
            P1[_id, tenant_id, ...]
            P2[_id, tenant_id, ...]
        end
    end

    subgraph "Queries"
        Q1[db.customers.find({tenant_id: "tenant1"})]
        Q2[db.service_requests.find({tenant_id: "tenant1"})]
    end

    Q1 --> C1
    Q2 --> S1
```

### 4.3 Tenant Context Propagation

```mermaid
sequenceDiagram
    participant Client
    participant Gateway
    participant RequestContextFilter
    participant Service
    participant Repository
    participant Database

    Client->>Gateway: HTTP Request + X-Tenant-ID
    Gateway->>RequestContextFilter: Forward
    RequestContextFilter->>RequestContextFilter: Extract tenantId
    RequestContextFilter->>RequestContext: Set tenant context
    RequestContextFilter->>Service: Chain proceeds
    Service->>RequestContext: Get tenantId
    Service->>Repository: findByTenantId(tenantId)
    Repository->>Database: Query with tenant_id filter
    Database-->>Repository: Tenant-specific data
    Repository-->>Service: Entities
    Service-->>Client: Response
```

---

## 5. Data Flow Architecture

### 5.1 Synchronous Request Flow

```mermaid
sequenceDiagram
    participant Client
    participant Gateway
    participant Service
    participant Database
    participant EventBus

    Client->>Gateway: POST /api/v1/service-requests
    Gateway->>Gateway: Validate JWT
    Gateway->>Service: Forward request
    Service->>Service: Validate input
    Service->>Database: Save entity
    Database-->>Service: Saved entity
    Service->>EventBus: Publish ServiceRequestCreated
    Service-->>Gateway: Response
    Gateway-->>Client: JSON Response
```

### 5.2 Asynchronous Event Flow

```mermaid
graph LR
    subgraph "Event Producers"
        S1[Booking Service]
        S2[Roadside Service]
        S3[Payment Service]
    end

    subgraph "Event Bus"
        KAFKA[Kafka]
    end

    subgraph "Event Consumers"
        C1[Notification Service]
        C2[Audit Service]
        C3[Analytics Service]
        C4[Dashboard Service]
    end

    S1 --> KAFKA
    S2 --> KAFKA
    S3 --> KAFNA
    KAFNA --> C1
    KAFNA --> C2
    KAFNA --> C3
    KAFNA --> C4
```

### 5.3 Event Schemas Flow

```mermaid
graph TB
    subgraph "Event Publishing"
        SVC[Service]
        EVT[Event Schema]
        PUB[Publisher]
    end

    subgraph "Event Bus"
        KAFNA[Kafka Topic]
    end

    subgraph "Event Consumption"
        SUB[Subscriber]
        PROC[Processor]
        HANDLER[Handler]
    end

    SVC --> EVT
    EVT --> PUB
    PUB --> KAFNA
    KAFNA --> SUB
    SUB --> PROC
    PROC --> HANDLER

    style EVT fill:#f9f,stroke:#333,stroke-width:2px
```

---

## 6. Security Architecture

### 6.1 Authentication & Authorization Flow

```mermaid
sequenceDiagram
    participant Client
    participant Gateway
    participant IdentityService
    participant AccessControl
    participant Service

    Client->>Gateway: Login Request
    Gateway->>IdentityService: Validate credentials
    IdentityService-->>Gateway: JWT Access Token
    Gateway->>AccessControl: Validate Token
    AccessControl-->>Gateway: User + Roles + Permissions
    Gateway->>Service: Request + X-User-Context
    Service-->>Gateway: Response
    Gateway-->>Client: Response
```

### 6.2 Security Library Integration

```mermaid
graph TB
    subgraph "shared-security-library"
        JWT[JwtTokenUtil]
        PWD[PasswordUtil]
        MFA[MFAUtil]
        RBAC[RBACService]
    end

    subgraph "Services Using Security"
        S1[identity-service]
        S2[access-control-service]
        S3[All Services]
    end

    subgraph "Security Features"
        AUTH[Authentication]
        AUTHZ[Authorization]
        MFA_F[MFA]
        PWD_M[Password Management]
    end

    S1 --> JWT
    S1 --> PWD
    S1 --> MFA
    S2 --> RBAC
    S3 --> JWT

    JWT --> AUTH
    RBAC --> AUTHZ
    MFA --> MFA_F
    PWD --> PWD_M
```

### 6.3 API Security Layers

```mermaid
graph TB
    subgraph "Layer 1: Network Security"
        CORS[CORS Configuration]
        CSRF[CSRF Protection]
        RATE[Rate Limiting]
    end

    subgraph "Layer 2: Authentication"
        JWT[JWT Validation]
        API_KEY[API Key Validation]
        MFA[MFA Check]
    end

    subgraph "Layer 3: Authorization"
        RBAC[Role-Based Access Control]
        PERM[Permission Checker]
        TENANT[Tenant Isolation]
    end

    subgraph "Layer 4: Input Validation"
        VAL[Input Validation]
        SAN[Input Sanitization]
        XSS[XSS Protection]
    end

    CLIENT[Client] --> CORS
    CORS --> CSRF
    CSRF --> RATE
    RATE --> JWT
    JWT --> API_KEY
    API_KEY --> MFA
    MFA --> RBAC
    RBAC --> PERM
    PERM --> TENANT
    TENANT --> VAL
    VAL --> SAN
    SAN --> XSS
    XSS --> SERVICE[Service]
```

---

## 7. Deployment Architecture

### 7.1 Railway Deployment

```mermaid
graph TB
    subgraph "Railway Cloud"
        subgraph "Production Environment"
            subgraph "Foundation Services"
                AI[AI Services<br/>27 services]
                CC[Config Services<br/>8 services]
                CD[Dashboard Services<br/>4 services]
                SI[Infrastructure Services<br/>39 services]
            end
        end

        subgraph "Infrastructure"
            PG[Railway PostgreSQL]
            REDIS[Railway Redis]
            MONGO[Railway MongoDB]
        end

        subgraph "Observability"
            PROM[Railway Metrics]
            LOGS[Railway Logs]
        end
    end

    AI --> MONGO
    CC --> PG
    CD --> MONGO
    SI --> MONGO
    SI --> REDIS
    SI --> PROM
    SI --> LOGS
```

### 7.2 Service Health Monitoring

```mermaid
graph LR
    subgraph "Services"
        S1[Service 1]
        S2[Service 2]
        S3[Service N]
    end

    subgraph "Health Endpoints"
        H1[/actuator/health]
        H2[/actuator/health]
        H3[/actuator/health]
    end

    subgraph "Monitoring"
        PROM[Prometheus]
        GRAF[Grafana]
        ALRT[Alertmanager]
    end

    S1 --> H1
    S2 --> H2
    S3 --> H3
    H1 --> PROM
    H2 --> PROM
    H3 --> PROM
    PROM --> GRAF
    PROM --> ALRT
```

### 7.3 CI/CD Pipeline

```mermaid
graph TB
    subgraph "GitHub"
        GH[GitHub Repository]
        PR[Pull Request]
        PUSH[Push to Main]
    end

    subgraph "GitHub Actions"
        BUILD[Build & Test]
        SECURITY[Security Scan]
        DEPLOY[Deploy to Railway]
    end

    subgraph "Artifacts"
        DOCKER[Docker Image]
        JAR[JAR Artifact]
    end

    subgraph "Railway"
        STG[Staging]
        PROD[Production]
    end

    PR --> BUILD
    PUSH --> BUILD
    BUILD --> SECURITY
    BUILD --> DOCKER
    BUILD --> JAR
    SECURITY --> DEPLOY
    DEPLOY --> STG
    DEPLOY --> PROD
```

---

## 8. Foundation Domain Service Map

### 8.1 Complete Service Inventory

```mermaid
mindmap
    root((Foundation Domain<br/>78 Services))
        AI-Services<br/>(27)
            Core AI
                Anomaly Detection
                Chatbot
                Content Generator
                Data Prediction
            Analytics
                Analytics
                Customer Behaviour
                Data Analytics
                Document Intelligence
            Business AI
                Fraud Detection
                Intelligent Dispatch
                Predictive Maintenance
                Route Optimization
                Dynamic Pricing
            Support AI
                Customer Support Chatbot
                Recommendation Engine
                Sentiment Analysis
        Central-Config<br/>(8)
            Config Service
            Feature Flags
            Localization
            Dynamic Routing
            Policy Config
            Rate Limit Policy
            Release Rollout
            Tenancy Config
        Dashboard<br/>(4)
            Dashboard Configuration
            Dashboard Analytics
            Dashboard Reporting
            Dashboard Aggregation
        Infrastructure<br/>(39)
            Gateway & Discovery
                API Gateway
                Service Registry
                Request Routing
            Security & Identity
                Identity Service
                Identity Access
                Access Control
                MFA Service
                Session Token
                API Keys
            Observability
                Logging Aggregation
                Metrics Telemetry
                Health Monitor
            Audit & Compliance
                Event Audit
                Audit Correlation
                Data Privacy Consent
            Resilience
                Idempotency
                Rate Limiting
                Anti-Fraud Rules
                Anti-Fraud Signals
            Integration
                Integration Adapters
                Courier Adapter
                Insurer Adapter
                Maps Geocoding
                Payments Adapter
            Business Support
                Billing
                Payment
                Pricing
                Policy Engine
                Notification
                Template Messaging
                Onboarding
            Data Management
                Database Management
                Reporting Read Model
                Tenant Org
                User Profile
                Geo Location
                Currency Converter
                Webhook Delivery
                Alerting
        Shared-Libraries<br/>(11)
            Java (8)
                Common Domain Models
                Event Schemas
                Shared Audit
                Shared Exception
                Shared Idempotency
                Shared Observability
                Shared Request Context
                Shared Security
            Frontend (3)
                Admin Framework
                UI Component Library
                Client SDK TypeScript
```

---

## Appendix A: Port Allocation

| Domain | Services | Port Range | Notes |
|--------|----------|------------|-------|
| **AI-Services** | 27 | 8100-8126 | AI/ML capabilities |
| **Central-Config** | 8 | 8000-8007 | Configuration management |
| **Centralized-Dashboard** | 4 | 3000, 8200-8202 | 3000 is Node.js |
| **Shared-Infrastructure** | 39 | 8300-8338 | Cross-cutting services |
| **Shared-Libraries** | 0 | N/A | Libraries, not services |
| **TOTAL** | **78** | **3000, 8000-8338** | 77 Java + 1 Node.js |

---

## Appendix B: Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Backend Language** | Java | 21 |
| **Framework** | Spring Boot | 3.3.5 |
| **Build Tool** | Maven | Latest |
| **Database** | MongoDB | Latest |
| **Message Broker** | Kafka | Latest |
| **Cache** | Redis | Latest |
| **Observability** | Micrometer + Prometheus | Latest |
| **Tracing** | Brave (Zipkin) | Latest |
| **Frontend** | React | 18.3.1 |
| **Frontend Build** | Vite | 5.4.10 |
| **TypeScript** | TypeScript | 5.x |
| **Node.js** | Node.js | 20.x |
| **Deployment** | Railway | N/A |
| **CI/CD** | GitHub Actions | N/A |

---

**Document Status**: Complete
**Last Updated**: January 11, 2026
**Next Review**: After architecture changes or new domain additions
