# MongoDB Schema Documentation - Orchestration Services

**Foundation Domain - 11 Orchestration Services**
**Database Per Service Pattern (2024-2025 Best Practices)**

---

## Table of Contents

1. [Overview](#overview)
2. [Service Architecture](#service-architecture)
3. [Database Schema Details](#database-schema-details)
   - [1. Alerting Service](#1-alerting-service)
   - [2. Dispatching Service](#2-dispatching-service)
   - [3. Fleet Assistance Service](#3-fleet-assistance-service)
   - [4. Fleet Organization Service](#4-fleet-organization-service)
   - [5. Fleet Policy Service](#5-fleet-policy-service)
   - [6. Fleet Vehicles Service](#6-fleet-vehicles-service)
   - [7. Location Service](#7-location-service)
   - [8. Matching Service](#8-matching-service)
   - [9. Monitoring Service](#9-monitoring-service)
   - [10. Reporting Service](#10-reporting-service)
   - [11. Transaction Orchestration Service](#11-transaction-orchestration-service)
4. [Common Patterns](#common-patterns)
5. [Index Strategy](#index-strategy)
6. [Data Validation Rules](#data-validation-rules)
7. [Usage Instructions](#usage-instructions)

---

## Overview

This documentation describes the MongoDB database schemas for all 11 orchestration services in the Foundation Domain. Each service follows the **Database Per Service** pattern, ensuring clear ownership boundaries and independent scalability.

### Key Features

- **Multi-tenant Architecture**: All collections include `tenantId` for tenant isolation
- **Soft Delete Support**: Most entities include `deletedAt` for logical deletion
- **Audit Trail**: All entities include `createdAt` and `updatedAt` timestamps
- **Geographic Queries**: Location-based services use 2dsphere indexes
- **Validation Rules**: JSON Schema validation ensures data integrity
- **Performance Optimization**: Strategic indexes for optimal query performance

---

## Service Architecture

```
Foundation Domain - Orchestration Services (11 Services)
├── 8083: alerting-service
├── 8084: dispatching-service
├── 8085: fleet-assistance-service
├── 8086: fleet-organization-service
├── 8087: fleet-policy-service
├── 8088: fleet-vehicles-service
├── 8089: location-service
├── 8090: matching-service
├── 8091: monitoring-service
├── 8092: reporting-service
└── 8093: transaction-orchestration-service
```

---

## Database Schema Details

### 1. Alerting Service

**Database**: `orchestration_alerting_service_db`
**Port**: 8083
**Description**: Alerts, alert rules, notifications, escalation policies

#### Collections

##### 1.1 alerts
Stores all system alerts with severity levels and acknowledgment status.

**Schema**:
```javascript
{
  alertId: string (unique, required),
  type: enum (SLA_BREACH, LONG_WAIT_TIME, PARTNER_UNAVAILABLE,
              EMERGENCY_REQUEST, SYSTEM_ISSUE, HIGH_VOLUME,
              PARTNER_TIMEOUT, ASSIGNMENT_FAILED),
  severity: enum (INFO, WARNING, CRITICAL, EMERGENCY),
  title: string (required),
  message: string (required),
  requestId: string,
  partnerId: string,
  region: string,
  acknowledged: boolean (default: false),
  acknowledgedBy: UUID,
  acknowledgedAt: date,
  expiresAt: date,
  relatedEntityId: string,
  metadata: object,
  tenantId: string (required),
  createdAt: date,
  updatedAt: date,
  deletedAt: date
}
```

**Indexes**:
- `alertId` (unique)
- `tenantId`
- `type`
- `severity`
- `acknowledged`
- `requestId`
- `partnerId`
- `createdAt` (-1)
- `expiresAt`
- `{ acknowledged, severity }` compound
- `{ tenantId, createdAt }` compound

##### 1.2 alert_rules
Defines automatic alert triggering rules.

**Schema**:
```javascript
{
  name: string (unique, required),
  description: string,
  alertType: enum (same as alerts.type),
  severity: enum (INFO, WARNING, CRITICAL, EMERGENCY),
  conditionExpression: string (required),
  metricName: string,
  thresholdValue: double (required),
  thresholdOperator: enum (GREATER_THAN, LESS_THAN, EQUALS, NOT_EQUALS),
  timeWindowMinutes: int,
  violationThreshold: int,
  escalationPolicyId: string,
  applicableRegions: array,
  applicablePartners: array,
  notificationChannels: array,
  enabled: boolean (default: true),
  validFrom: date,
  validUntil: date,
  metadata: object,
  totalTriggers: long,
  triggersLast24Hours: long,
  lastTriggeredAt: date,
  lastTriggeredAlertId: string,
  isActive: boolean,
  status: enum (ACTIVE, PAUSED, DISABLED),
  tenantId: string,
  createdAt: date,
  updatedAt: date,
  deletedAt: date
}
```

**Indexes**:
- `name` (unique)
- `tenantId`
- `alertType`
- `severity`
- `enabled`
- `isActive`
- `status`

##### 1.3 alert_subscriptions
User subscriptions for specific alert types.

**Schema**:
```javascript
{
  userId: string (required),
  alertTypes: array (required),
  severityFilter: array,
  channels: array,
  isActive: boolean,
  tenantId: string,
  createdAt: date,
  updatedAt: date
}
```

**Indexes**:
- `{ userId, tenantId }` (unique)
- `alertType`

##### 1.4 alert_history
Audit trail of all alert state changes.

**Schema**:
```javascript
{
  alertId: string (required),
  action: enum (CREATED, ACKNOWLEDGED, ESCALATED, CLOSED, EXPIRED),
  performedBy: string (required),
  previousState: object,
  newState: object,
  timestamp: date,
  metadata: object
}
```

**Indexes**:
- `alertId`
- `timestamp` (-1)
- `action`

##### 1.5 escalation_policies
Defines escalation paths for unacknowledged alerts.

**Indexes**: `tenantId`, `isActive`

##### 1.6 notification_channels
Configuration for various notification channels (email, SMS, webhook).

**Indexes**: `tenantId`, `type`, `isActive`

##### 1.7 alert_templates
Reusable templates for common alert scenarios.

**Indexes**: `tenantId`, `alertType`, `category`

---

### 2. Dispatching Service

**Database**: `orchestration_dispatching_service_db`
**Port**: 8084
**Description**: Dispatch jobs, assignments, routes, tracking

#### Collections

##### 2.1 dispatches
Main dispatch job records.

**Schema**:
```javascript
{
  dispatchId: string (unique, required),
  requestId: string (required),
  status: enum (PENDING, ASSIGNED, IN_PROGRESS, COMPLETED,
                CANCELLED, FAILED, ON_HOLD),
  priority: enum (LOW, MEDIUM, HIGH, EMERGENCY),
  serviceType: string,
  location: {
    latitude: double,
    longitude: double,
    address: string
  },
  assignedProviderId: string,
  assignedVehicleId: string,
  estimatedArrival: date,
  actualArrival: date,
  completionTime: date,
  assignmentMethod: enum (AUTOMATIC, MANUAL, ALERTING),
  metadata: object,
  tenantId: string,
  createdAt: date,
  updatedAt: date,
  deletedAt: date
}
```

**Indexes**:
- `dispatchId` (unique)
- `tenantId`
- `requestId`
- `status`
- `priority`
- `createdAt` (-1)
- `assignedProviderId`
- `location` (2dsphere)
- `{ tenantId, status, createdAt }` compound

##### 2.2 dispatch_assignments
Provider assignments for dispatches.

**Schema**:
```javascript
{
  dispatchId: string (required),
  providerId: string (required),
  vehicleId: string,
  driverId: string,
  status: enum (PENDING, ACCEPTED, REJECTED, CANCELLED,
                IN_TRANSIT, ON_SCENE, COMPLETED),
  assignedAt: date,
  acceptedAt: date,
  rejectedAt: date,
  rejectionReason: string,
  completedAt: date,
  assignmentScore: double,
  estimatedDistance: double,
  estimatedDuration: int,
  actualDistance: double,
  actualDuration: int,
  metadata: object,
  tenantId: string,
  createdAt: date,
  updatedAt: date
}
```

**Indexes**:
- `{ dispatchId, providerId }` (unique)
- `providerId`
- `status`
- `assignedAt` (-1)

##### 2.3 dispatch_tracking
Real-time tracking events for dispatches.

**Indexes**: `dispatchId`, `timestamp` (-1), `eventType`, `performedBy`

##### 2.4 dispatch_metrics
Performance metrics for providers.

**Indexes**: `{ providerId, date }`, `tenantId`, `date` (-1)

##### 2.5 dispatch_routes
Route information for dispatches.

**Indexes**: `dispatchId`, `waypoints.location` (2dsphere)

##### 2.6 dispatch_providers
Provider availability and status.

**Indexes**: `tenantId`, `isActive`, `currentStatus`, `currentLocation` (2dsphere)

---

### 3. Fleet Assistance Service

**Database**: `orchestration_fleet_assistance_service_db`
**Port**: 8085
**Description**: Assistance requests, fleet coordination

#### Collections

##### 3.1 fleet_requests
Fleet-specific assistance requests.

**Schema**:
```javascript
{
  requestId: string (unique, required),
  fleetId: string (required),
  status: enum (PENDING, ASSIGNED, IN_PROGRESS, COMPLETED,
                CANCELLED, ON_HOLD),
  serviceType: string (required),
  priority: enum (LOW, MEDIUM, HIGH, EMERGENCY),
  vehicleId: string,
  location: { latitude, longitude, address },
  assignedFleetProviderId: string,
  estimatedArrival: date,
  actualArrival: date,
  completionTime: date,
  metadata: object,
  tenantId: string,
  createdAt: date,
  updatedAt: date,
  deletedAt: date
}
```

**Indexes**:
- `requestId` (unique)
- `tenantId`
- `fleetId`
- `status`
- `createdAt` (-1)
- `location` (2dsphere)
- `{ tenantId, fleetId, status }` compound

##### 3.2 assistance_history
Historical record of all fleet assistance requests.

**Indexes**: `fleetId`, `vehicleId`, `timestamp` (-1), `serviceType`

##### 3.3 fleet_providers
Fleet provider information and coverage.

**Indexes**: `tenantId`, `isActive`, `coverageArea` (2dsphere), `serviceTypes`

##### 3.4 service_types
Catalog of available service types.

**Indexes**: `category`, `isActive`

##### 3.5 fleet_coordination
Fleet coordination and scheduling.

**Indexes**: `fleetId`, `coordinationDate` (-1)

---

### 4. Fleet Organization Service

**Database**: `orchestration_fleet_organization_service_db`
**Port**: 8086
**Description**: Fleet organization structure, hierarchy

#### Collections

##### 4.1 organizations
Organizational hierarchy structure.

**Schema**:
```javascript
{
  organizationId: string (unique, required),
  name: string (required),
  description: string,
  parentId: string,
  organizationType: enum (ROOT, DIVISION, DEPARTMENT, TEAM, UNIT),
  level: int,
  path: string, // e.g., "/org-root-001/org-div-001"
  managerId: string,
  contactEmail: string,
  contactPhone: string,
  location: { latitude, longitude, address },
  isActive: boolean,
  metadata: object,
  tenantId: string (required),
  createdAt: date,
  updatedAt: date,
  deletedAt: date
}
```

**Indexes**:
- `organizationId` (unique)
- `tenantId`
- `parentId`
- `path`
- `organizationType`
- `isActive`
- `{ tenantId, parentId }` compound

##### 4.2 fleet_units
Individual fleet units within organizations.

**Indexes**: `organizationId`, `isActive`, `unitType`

##### 4.3 organization_hierarchy
Closure table for hierarchical queries (enables efficient ancestor/descendant lookups).

**Schema**:
```javascript
{
  ancestorId: string (required),
  descendantId: string (required),
  depth: int (required) // 0 = self, 1 = direct child, 2 = grandchild, etc.
}
```

**Indexes**:
- `ancestorId`
- `descendantId`
- `{ ancestorId, descendantId, depth }` (unique)

##### 4.4 fleet_policies
Organization-level policies.

**Indexes**: `organizationId`, `policyType`, `isActive`

---

### 5. Fleet Policy Service

**Database**: `orchestration_fleet_policy_service_db`
**Port**: 8087
**Description**: Fleet policies, rules, compliance

#### Collections

##### 5.1 policies
Fleet policy definitions.

**Schema**:
```javascript
{
  policyId: string (unique, required),
  name: string (required),
  description: string,
  policyType: enum (SAFETY, MAINTENANCE, OPERATIONAL, COMPLIANCE,
                    BEHAVIOR, ENVIRONMENTAL),
  fleetId: string,
  scope: enum (GLOBAL, FLEET, VEHICLE_TYPE, INDIVIDUAL),
  scopeValue: string,
  rules: array,
  isActive: boolean,
  effectiveFrom: date,
  effectiveUntil: date,
  priority: int,
  metadata: object,
  tenantId: string (required),
  createdAt: date,
  updatedAt: date,
  deletedAt: date
}
```

**Indexes**:
- `policyId` (unique)
- `tenantId`
- `fleetId`
- `policyType`
- `isActive`
- `{ effectiveFrom, effectiveUntil }` compound
- `{ tenantId, fleetId, isActive }` compound

##### 5.2 policy_rules
Individual policy rules.

**Indexes**: `policyId`, `ruleType`, `isActive`

##### 5.3 policy_compliance
Compliance tracking records.

**Indexes**: `fleetId`, `vehicleId`, `complianceDate` (-1), `status`

##### 5.4 policy_violations
Policy violation records.

**Indexes**: `fleetId`, `vehicleId`, `severity`, `violationDate` (-1), `status`

---

### 6. Fleet Vehicles Service

**Database**: `orchestration_fleet_vehicles_service_db`
**Port**: 8088
**Description**: Vehicles, maintenance, tracking, telemetry

#### Collections

##### 6.1 vehicles
Vehicle fleet management.

**Schema**:
```javascript
{
  vehicleId: string (unique, required),
  vin: string (unique, required),
  fleetId: string (required),
  organizationId: string,
  make: string,
  model: string,
  year: int,
  licensePlate: string,
  vehicleType: enum (TOW_TRUCK, FLATBED, SERVICE_VAN, WRECKER,
                     HEAVY_DUTY, MEDIUM_DUTY, LIGHT_DUTY),
  status: enum (ACTIVE, INACTIVE, MAINTENANCE, OUT_OF_SERVICE,
                RETIRED, ASSIGNED, AVAILABLE),
  currentLocation: {
    latitude: double,
    longitude: double,
    address: string,
    timestamp: date
  },
  currentDriverId: string,
  currentAssignmentId: string,
  mileage: double,
  fuelLevel: double,
  capabilities: array, // e.g., ["TOWING", "JUMP_START"]
  specifications: object,
  lastMaintenanceDate: date,
  nextMaintenanceDate: date,
  isActive: boolean,
  metadata: object,
  tenantId: string,
  createdAt: date,
  updatedAt: date,
  deletedAt: date
}
```

**Indexes**:
- `vehicleId` (unique)
- `vin` (unique)
- `tenantId`
- `fleetId`
- `organizationId`
- `status`
- `vehicleType`
- `licensePlate`
- `currentLocation.location` (2dsphere)
- `{ tenantId, fleetId, status }` compound

##### 6.2 vehicle_maintenance
Maintenance records.

**Schema**:
```javascript
{
  vehicleId: string (required),
  maintenanceId: string,
  maintenanceType: enum (ROUTINE, REPAIR, INSPECTION, EMERGENCY,
                         PREVENTIVE, CORRECTIVE),
  description: string,
  status: enum (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED),
  scheduledDate: date,
  completedDate: date,
  cost: double,
  performedBy: string,
  partsReplaced: array,
  notes: string,
  mileage: double,
  metadata: object,
  tenantId: string,
  createdAt: date,
  updatedAt: date
}
```

**Indexes**:
- `vehicleId`
- `maintenanceDate` (-1)
- `status`
- `maintenanceType`
- `scheduledDate`

##### 6.3 vehicle_telemetry
Real-time vehicle telemetry data.

**Indexes**: `vehicleId`, `timestamp` (-1), `{ vehicleId, timestamp }`, `metricType`

##### 6.4 vehicle_locations
Historical location tracking.

**Indexes**: `vehicleId`, `timestamp` (-1), `{ vehicleId, timestamp }`, `location` (2dsphere)

##### 6.5 vehicle_insurance
Insurance policy information.

**Indexes**: `vehicleId`, `policyNumber` (unique), `expiryDate`

##### 6.6 vehicle_documents
Vehicle registration and documents.

**Indexes**: `vehicleId`, `documentType`, `expiryDate`

---

### 7. Location Service

**Database**: `orchestration_location_service_db`
**Port**: 8089
**Description**: Locations, geofences, routes, geo-queries

#### Collections

##### 7.1 locations
Real-time location tracking for all entities.

**Schema**:
```javascript
{
  entityType: enum (VEHICLE, DRIVER, REQUEST, PROVIDER, ORGANIZATION),
  entityId: string (required),
  location: {
    latitude: double (required),
    longitude: double (required),
    altitude: double,
    accuracy: double,
    address: string,
    postalCode: string,
    city: string,
    state: string,
    country: string
  },
  speed: double,
  heading: double,
  timestamp: date,
  dataSource: enum (GPS, NETWORK, MANUAL, BEACON),
  metadata: object,
  tenantId: string
}
```

**Indexes**:
- `{ entityType, entityId }` (unique)
- `location` (2dsphere)
- `timestamp` (-1)
- `tenantId`
- `entityType`

##### 7.2 location_history
Historical location trail.

**Indexes**: `entityId`, `entityType`, `timestamp` (-1), `{ entityId, timestamp }`, `location` (2dsphere)

##### 7.3 geofences
Geographic fence definitions.

**Schema**:
```javascript
{
  geofenceId: string,
  name: string (required),
  description: string,
  geometry: {
    type: enum (Polygon, Circle, Point),
    coordinates: array,
    radius: double // for Circle type
  },
  entityType: array, // e.g., ["VEHICLE", "DRIVER"]
  entityId: array, // specific entity IDs, empty = all
  fenceType: enum (INCLUSION, EXCLUSION, SPEED_LIMIT, NO_GO_ZONE),
  actions: array, // e.g., ["ENTER", "EXIT", "DWELL"]
  isActive: boolean,
  metadata: object,
  tenantId: string (required),
  createdAt: date,
  updatedAt: date,
  deletedAt: date
}
```

**Indexes**:
- `tenantId`
- `isActive`
- `geometry` (2dsphere)
- `fenceType`

##### 7.4 location_alerts
Geofence breach alerts.

**Indexes**: `geofenceId`, `entityId`, `timestamp` (-1), `action`

##### 7.5 routes
Route definitions and calculations.

**Indexes**: `routeId` (unique), `tenantId`, `status`, `waypoints.location` (2dsphere)

---

### 8. Matching Service

**Database**: `orchestration_matching_service_db`
**Port**: 8090
**Description**: Matching algorithms, assignments, optimization

#### Collections

##### 8.1 matching_requests
Provider matching requests.

**Schema**:
```javascript
{
  requestId: string (unique, required),
  dispatchRequestId: string,
  status: enum (PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED),
  serviceType: string (required),
  priority: enum (LOW, MEDIUM, HIGH, EMERGENCY),
  location: { latitude, longitude, address },
  requiredCapabilities: array,
  preferences: object,
  constraints: object,
  matchingAlgorithm: enum (NEAREST, BEST_FIT, LEAST_COST, PRIORITY_BASED),
  maxProvidersToReturn: int,
  timeoutSeconds: int,
  metadata: object,
  tenantId: string,
  createdAt: date,
  updatedAt: date,
  completedAt: date
}
```

**Indexes**:
- `requestId` (unique)
- `tenantId`
- `status`
- `createdAt` (-1)
- `serviceType`
- `location` (2dsphere)
- `{ tenantId, status, createdAt }` compound

##### 8.2 matching_results
Matching algorithm results with scored providers.

**Indexes**: `requestId`, `providerId`, `score` (-1), `rank`, `selected`

##### 8.3 matching_criteria
Matching criteria configuration.

**Indexes**: `serviceType`, `tenantId`, `isActive`

##### 8.4 provider_profiles
Provider matching profiles.

**Schema**:
```javascript
{
  providerId: string (unique, required),
  userId: string,
  vehicleId: string,
  organizationId: string,
  serviceTypes: array,
  capabilities: array,
  currentLocation: { latitude, longitude },
  status: enum (AVAILABLE, BUSY, OFFLINE, ON_BREAK, UNAVAILABLE),
  rating: double,
  totalAssignments: int,
  completedAssignments: int,
  averageResponseTime: double,
  acceptanceRate: double,
  cancellationRate: double,
  operatingHours: {
    start: string, // "06:00"
    end: string,   // "22:00"
    timezone: string
  },
  serviceArea: {
    type: enum (Polygon, Circle),
    coordinates: array,
    radius: double
  },
  preferences: object,
  constraints: object,
  isActive: boolean,
  metadata: object,
  tenantId: string,
  createdAt: date,
  updatedAt: date,
  deletedAt: date
}
```

**Indexes**:
- `providerId` (unique)
- `tenantId`
- `isActive`
- `status`
- `currentLocation` (2dsphere)
- `serviceArea.geometry` (2dsphere)
- `serviceTypes`
- `rating` (-1)

##### 8.5 matching_history
Historical matching decisions.

**Indexes**: `requestId`, `timestamp` (-1), `algorithm`

---

### 9. Monitoring Service

**Database**: `orchestration_monitoring_service_db`
**Port**: 8091
**Description**: Metrics, health checks, monitoring data

#### Collections

##### 9.1 monitors
Service and infrastructure monitoring definitions.

**Schema**:
```javascript
{
  monitorId: string (unique, required),
  name: string (required),
  description: string,
  targetType: enum (SERVICE, DATABASE, API, QUEUE, CACHE, EXTERNAL),
  targetService: string (required),
  checkType: enum (HTTP, TCP, PING, CUSTOM, HEALTH_ENDPOINT),
  endpoint: string,
  expectedResponse: int,
  timeout: int,
  checkInterval: int, // milliseconds
  retryAttempts: int,
  alertThreshold: int,
  isActive: boolean,
  lastCheck: date,
  lastStatus: enum (UP, DOWN, DEGRADED, UNKNOWN),
  uptime: double, // percentage
  metadata: object,
  tenantId: string,
  createdAt: date,
  updatedAt: date
}
```

**Indexes**:
- `monitorId` (unique)
- `tenantId`
- `targetService`
- `targetType`
- `isActive`
- `lastStatus`
- `{ tenantId, isActive }` compound

##### 9.2 monitoring_results
Historical monitoring check results.

**Indexes**: `monitorId`, `timestamp` (-1), `status`, `{ monitorId, timestamp }` compound

##### 9.3 monitoring_alerts
Alerts triggered by monitoring failures.

**Indexes**: `monitorId`, `severity`, `status`, `timestamp` (-1)

##### 9.4 monitoring_configs
Monitoring configuration templates.

**Indexes**: `metricType`, `tenantId`, `isActive`

##### 9.5 health_checks
Service health check results.

**Indexes**: `serviceId`, `timestamp` (-1), `status`

##### 9.6 metrics
Time-series metrics data.

**Indexes**: `serviceId`, `metricName`, `timestamp` (-1), `{ serviceId, metricName, timestamp }` compound

---

### 10. Reporting Service

**Database**: `orchestration_reporting_service_db`
**Port**: 8092
**Description**: Reports, templates, schedules, history

#### Collections

##### 10.1 reports
Generated reports metadata.

**Schema**:
```javascript
{
  reportId: string (unique, required),
  name: string (required),
  description: string,
  reportType: enum (DISPATCH, FLEET_UTILIZATION, PERFORMANCE, COMPLIANCE,
                    MAINTENANCE, FINANCIAL, CUSTOMER_SATISFACTION, CUSTOM),
  templateId: string,
  parameters: object,
  filters: object,
  dataSource: string,
  format: enum (PDF, EXCEL, CSV, HTML, JSON),
  status: enum (PENDING, GENERATING, COMPLETED, FAILED, CANCELLED),
  generatedBy: string,
  generatedAt: date,
  fileUrl: string,
  fileSize: long,
  recordCount: int,
  dateRangeStart: date,
  dateRangeEnd: date,
  metadata: object,
  tenantId: string (required),
  createdAt: date,
  updatedAt: date,
  deletedAt: date
}
```

**Indexes**:
- `reportId` (unique)
- `tenantId`
- `reportType`
- `generatedAt` (-1)
- `status`
- `generatedBy`
- `{ tenantId, reportType, generatedAt }` compound

##### 10.2 report_templates
Reusable report templates.

**Schema**:
```javascript
{
  templateId: string (unique, required),
  name: string (required),
  description: string,
  reportType: enum (same as reports.reportType),
  category: string,
  template: object, // layout structure
  parameters: array, // parameter definitions
  defaultFormat: enum (PDF, EXCEL, CSV, HTML, JSON),
  dataSource: string,
  query: string,
  layout: object,
  styling: object,
  isPublic: boolean,
  createdBy: string,
  version: int,
  isActive: boolean,
  metadata: object,
  tenantId: string (required),
  createdAt: date,
  updatedAt: date,
  deletedAt: date
}
```

**Indexes**:
- `templateId` (unique)
- `tenantId`
- `category`
- `reportType`
- `isPublic`
- `isActive`
- `createdBy`

##### 10.3 report_schedules
Automated report generation schedules.

**Indexes**: `templateId`, `nextRun`, `isActive`, `scheduleType`

##### 10.4 report_history
Report generation audit trail.

**Indexes**: `reportId`, `timestamp` (-1), `action`, `performedBy`

##### 10.5 report_subscriptions
User subscriptions to reports.

**Indexes**: `reportId`, `userId`, `isActive`

---

### 11. Transaction Orchestration Service

**Database**: `orchestration_transaction_orchestration_service_db`
**Port**: 8093
**Description**: Sagas, saga steps, transaction logs, compensation

#### Collections

##### 11.1 transactions
Saga transaction orchestration state machine.

**Schema**:
```javascript
{
  transactionId: string (unique, required),
  sagaType: enum (DISPATCH_SAGA, ASSISTANCE_REQUEST_SAGA,
                  FLEET_COORDINATION_SAGA, VEHICLE_ASSIGNMENT_SAGA,
                  PAYMENT_SAGA, CUSTOM_SAGA),
  status: enum (STARTED, IN_PROGRESS, COMPLETED, FAILED,
                COMPENSATING, COMPENSATED, TERMINATED),
  businessKey: string,
  requestId: string,
  currentStep: string,
  totalSteps: int,
  completedSteps: int,
  payload: object, // saga input data
  context: object, // saga state shared between steps
  error: {
    code: string,
    message: string,
    stackTrace: string,
    failedStep: string
  },
  retryCount: int,
  maxRetries: int,
  timeout: int,
  startedAt: date,
  completedAt: date,
  lastUpdatedAt: date,
  metadata: object,
  tenantId: string,
  createdAt: date,
  updatedAt: date
}
```

**Indexes**:
- `transactionId` (unique)
- `tenantId`
- `sagaType`
- `status`
- `businessKey`
- `requestId`
- `createdAt` (-1)
- `lastUpdatedAt` (-1)
- `{ tenantId, status, createdAt }` compound

##### 11.2 transaction_steps
Individual saga execution steps.

**Schema**:
```javascript
{
  transactionId: string (required),
  stepId: string,
  stepName: string (required),
  stepOrder: int (required),
  stepType: enum (SERVICE_INVOCATION, COMPENSATION,
                  VALIDATION, TRANSFORMATION),
  status: enum (PENDING, IN_PROGRESS, COMPLETED, FAILED,
                SKIPPED, COMPENSATING, COMPENSATED),
  service: {
    name: string,
    endpoint: string,
    method: enum (GET, POST, PUT, DELETE, PATCH)
  },
  input: object,
  output: object,
  compensationAction: string,
  compensationInput: object,
  compensationOutput: object,
  error: {
    code: string,
    message: string,
    details: object
  },
  retryCount: int,
  startedAt: date,
  completedAt: date,
  compensatingAt: date,
  compensatedAt: date,
  metadata: object,
  tenantId: string,
  createdAt: date,
  updatedAt: date
}
```

**Indexes**:
- `{ transactionId, stepOrder }` (unique)
- `transactionId`
- `stepId`
- `status`
- `stepOrder`
- `startedAt` (-1)
- `tenantId`

##### 11.3 transaction_state
Current state of transactions for quick lookups.

**Indexes**: `transactionId` (unique), `state`, `updatedAt` (-1)

##### 11.4 compensations
Compensation transaction logs.

**Indexes**: `transactionId`, `status`, `createdAt` (-1)

##### 11.5 saga_logs
Saga execution logs for debugging.

**Indexes**: `transactionId`, `timestamp` (-1), `level`, `eventType`

---

## Common Patterns

### Multi-Tenancy
All collections include `tenantId` field for tenant isolation:
- Indexed for efficient filtering
- Used in compound indexes with frequently queried fields

### Soft Delete
Most entities support soft delete pattern:
- `deletedAt: date` field (null = not deleted)
- Queries should exclude records where `deletedAt` is not null
- Allows data recovery and audit compliance

### Audit Trail
All entities include timestamp fields:
- `createdAt: date` - Record creation timestamp
- `updatedAt: date` - Last update timestamp
- Automatically managed by application layer

### Geographic Queries
Location-based collections use 2dsphere indexes:
- `location` field with `latitude` and `longitude`
- Supports geospatial queries: $near, $geoWithin, $geoIntersects
- Essential for dispatch, matching, and location services

### Enum Constraints
String fields with limited values use enum validation:
- Ensures data consistency
- Documented in schema validation rules
- Examples: status, severity, type fields

---

## Index Strategy

### Index Types

1. **Unique Indexes**: Ensure data uniqueness
   - All `*Id` fields (e.g., `alertId`, `dispatchId`)
   - Business identifiers (e.g., `vin` in vehicles)

2. **Single Field Indexes**: Common filter fields
   - `tenantId` - Multi-tenant queries
   - `status` - Filtering by state
   - `createdAt/updatedAt` - Time-based queries

3. **Compound Indexes**: Multi-field queries
   - `{ tenantId, status, createdAt }` - Tenant-specific status queries
   - `{ entityType, entityId }` - Specific entity lookups
   - `{ transactionId, stepOrder }` - Saga step ordering

4. **Geospatial Indexes**: Location queries
   - `2dsphere` on location fields
   - Supports proximity, containment, intersection queries

5. **Text Indexes**: Full-text search (future enhancement)
   - Can be added to description fields
   - Enables natural language search

### Index Optimization

- **Query Pattern**: Indexes designed for common query patterns
- **Read-Heavy**: Optimized for read performance (typical for dispatch systems)
- **Sort Support**: Descending indexes (-1) for recent-first queries
- **Covered Queries**: Compound indexes include all frequently accessed fields

---

## Data Validation Rules

### JSON Schema Validation

All collections use MongoDB JSON Schema validation:

```javascript
validator: {
  $jsonSchema: {
    bsonType: 'object',
    required: ['field1', 'field2'],
    properties: {
      field1: { bsonType: 'string' },
      field2: { enum: ['VALUE1', 'VALUE2'] },
      field3: { bsonType: 'date' }
    }
  }
}
```

### Validation Levels

- **Strict**: Production enforcement
- **Moderate**: Development/testing (logs violations)
- **Off**: Disabled (migration mode)

### Common Validations

1. **Required Fields**: Enforced at database level
2. **Type Checking**: BSON type validation
3. **Enum Values**: Limited set of allowed values
4. **Format Validation**: Email, phone number patterns
5. **Range Validation**: Numeric min/max values

---

## Usage Instructions

### Running the Setup Script

**In MongoDB Compass mongosh:**
```javascript
load("C:/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/orchestration-services/setup-mongodb-orchestration-complete.js")
```

**Or with mongosh directly:**
```bash
mongosh localhost:27017 Foundation-Domain/orchestration-services/setup-mongodb-orchestration-complete.js
```

### Verifying Setup

After running the setup script:

```javascript
// List all databases
show dbs

// Use a specific database
use orchestration_alerting_service_db

// List collections
show collections

// Check indexes
db.alerts.getIndexes()

// Count documents
db.alerts.count()

// View seed data
db.alerts.find().pretty()

// Check validation rules
db.getCollectionInfos({ name: 'alerts' })
```

### Application Configuration

Update `application.properties` or `application.yml` for each service:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/orchestration_alerting_service_db
      auto-index-creation: true
```

### Query Examples

**Find active alerts by tenant:**
```javascript
db.alerts.find({
  tenantId: 'tenant-001',
  acknowledged: false,
  deletedAt: null
}).sort({ createdAt: -1 })
```

**Find nearby providers:**
```javascript
db.provider_profiles.find({
  currentLocation: {
    $near: {
      $geometry: {
        type: "Point",
        coordinates: [-122.4194, 37.7749]
      },
      $maxDistance: 5000 // meters
    }
  },
  status: 'AVAILABLE',
  isActive: true
})
```

**Check geofence breach:**
```javascript
db.location_alerts.find({
  geofenceId: 'geofence-001',
  action: 'ENTER',
  timestamp: { $gte: new Date('2024-01-01') }
}).sort({ timestamp: -1 })
```

---

## Summary

**Total Databases**: 11
**Total Collections**: 55+
**Total Indexes**: 290+
**Total Seed Documents**: 50+

All orchestration services follow best practices:
- Database Per Service pattern
- Multi-tenant architecture
- Comprehensive validation
- Optimized indexing
- Production-ready schemas

---

**Document Version**: 1.0
**Last Updated**: 2026-02-04
**Author**: MongoDB Setup Script Generator
