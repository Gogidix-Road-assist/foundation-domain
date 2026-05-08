# Mechanics GPS Tracker Service

Cross-domain integration service connecting Mechanics and Central-Monitoring domains for real-time GPS tracking and fleet management.

## Overview

This service acts as a bridge between the Mechanics domain (mobile mechanics, staff) and Central-Monitoring domain, enabling:
- **Real-time GPS Tracking**: Track mobile mechanics in real-time
- **Location Updates**: Receive and forward mechanic GPS coordinates
- **Batch Updates**: Efficiently batch-update locations to central monitoring
- **Nearby Mechanics**: Find available mechanics near customer location
- **Job Tracking**: Track mechanics while on jobs
- **Fleet Visibility**: Real-time view of entire mechanic fleet

## Port: 8098

## Architecture

```
┌─────────────────────┐
│ Mechanics Domain    │
│                     │
│ Mobile Mechanic     │
│ (8088)              │
│      ↓              │
│ Staff Mgmt (8085)   │
└──────────┬──────────┘
           │
           ↓ GPS Updates
┌──────────────────────────────────┐
│ Mechanics GPS Tracker Service     │
│ (8098)                             │
│                                    │
│ • Location aggregation             │
│ • Near mechanic search             │
│ • Job tracking                     │
│ • Batch updates to monitoring      │
│ • Distance calculations             │
└──────────┬───────────────────────┘
           │
           ↓ Forward Tracking Data
┌─────────────────────┐
│ Central Monitoring  │
│ (Agent 2)           │
│                     │
│ Tracking API        │
│ Monitoring API      │
│ Real-time Maps      │
└─────────────────────┘
```

## Key Features

### 1. Real-time Location Updates

Mobile apps send GPS updates:
```bash
POST /api/bridge/gps-tracker/locations
```

**Request:**
```json
{
  "mechanicId": "MECH-001",
  "mechanicName": "Mike Murphy",
  "latitude": 53.3498,
  "longitude": -6.2603,
  "speed": 45.5,
  "heading": 180.0,
  "altitude": 50.0,
  "timestamp": "2025-12-25T16:30:00",
  "status": "EN_ROUTE_TO_JOB",
  "currentJobId": "MM-REQ-001",
  "vehicleRegistration": "12-D-12345"
}
```

**Response:**
```json
{
  "mechanicId": "MECH-001",
  "status": "EN_ROUTE_TO_JOB",
  "timestamp": "2025-12-25T16:30:00",
  "forwardedToCentralMonitoring": true
}
```

### 2. Nearby Mechanic Search

Find available mechanics near customer:
```bash
GET /api/bridge/gps-tracker/mechanics/nearby?latitude=53.3498&longitude=-6.2603&radiusKm=10
```

**Response:**
```json
[
  {
    "mechanicId": "MECH-001",
    "mechanicName": "Mike Murphy",
    "latitude": 53.3450,
    "longitude": -6.2550,
    "speed": 0,
    "heading": 0,
    "status": "AVAILABLE",
    "timestamp": "2025-12-25T16:28:00",
    "distance": 2.3
  },
  {
    "mechanicId": "MECH-002",
    "mechanicName": "John Smith",
    "latitude": 53.3520,
    "longitude": -6.2650,
    "speed": 35.0,
    "status": "RETURNING",
    "timestamp": "2025-12-25T16:29:00",
    "distance": 3.1
  }
]
```

### 3. Job Tracking

Start tracking mechanic for job:
```bash
POST /api/bridge/gps-tracker/mechanics/MECH-001/start-tracking?jobId=MM-REQ-001
```

Stop tracking after job completion:
```bash
POST /api/bridge/gps-tracker/mechanics/MECH-001/stop-tracking
```

### 4. Tracking Summary

Get detailed tracking summary:
```bash
GET /api/bridge/gps-tracker/mechanics/MECH-001/tracking-summary
```

**Response:**
```json
{
  "mechanicId": "MECH-001",
  "mechanicName": "Mike Murphy",
  "lastKnownLocation": {
    "latitude": 53.3498,
    "longitude": -6.2603,
    "speed": 45.5,
    "status": "EN_ROUTE_TO_JOB"
  },
  "lastUpdate": "2025-12-25T16:30:00",
  "currentStatus": "EN_ROUTE_TO_JOB",
  "currentJobId": "MM-REQ-001",
  "distanceTraveledToday": 45.2,
  "jobsCompletedToday": 3
}
```

### 5. Batch Updates (Scheduled)

**Scheduled Task:** Runs every 30 seconds

- Collects all cached mechanic locations
- Creates batch with current positions
- Sends to Central Monitoring API
- Reduces API calls by batching updates

**Configuration:**
```yaml
gps-tracking:
  update-interval: 30000 # 30 seconds
  batch-size: 100
  max-history-days: 30
```

## Location Status

| Status | Description | Can Accept Jobs |
|--------|-------------|-----------------|
| AVAILABLE | Mechanic is available for work | ✅ Yes |
| EN_ROUTE_TO_JOB | Traveling to job site | ❌ No |
| AT_JOB_SITE | Working at customer location | ❌ No |
| RETURNING | Returning from job | ⚠️ Soon |
| OFF_DUTY | Not working | ❌ No |

## Distance Calculation

Uses Haversine formula to calculate great-circle distance between two coordinates:

```
Distance = R × c
Where:
  R = 6371 km (Earth's radius)
  c = 2 × atan2(√a, √(1−a))
  a = sin²(Δlat/2) + cos(lat1) × cos(lat2) × sin²(Δlon/2)
```

## API Endpoints

### Location Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/bridge/gps-tracker/locations | Update mechanic location |
| GET | /api/bridge/gps-tracker/locations | Get all active mechanic locations |
| GET | /api/bridge/gps-tracker/locations/{mechanicId} | Get specific mechanic location |

### Search & Discovery

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/bridge/gps-tracker/mechanics/nearby | Find nearby mechanics by location |

### Tracking

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/bridge/gps-tracker/mechanics/{id}/tracking-summary | Get tracking summary |
| POST | /api/bridge/gps-tracker/mechanics/{id}/start-tracking | Start job tracking |
| POST | /api/bridge/gps-tracker/mechanics/{id}/stop-tracking | Stop tracking |

## Integration Points

### Mechanics Domain

**Mobile Mechanic Service (8088):**
- Provides job assignments requiring GPS tracking
- Uses nearby mechanic search for optimal assignment

**Staff Management Service (8085):**
- Provides mechanic details
- Updates mechanic status based on location

### Central-Monitoring Domain (Agent 2)

**Tracking API:**
- Receives GPS location updates
- Provides real-time tracking dashboard
- Stores location history

**Monitoring API:**
- Fleet management dashboard
- Alerts and notifications
- Geofence monitoring

## Mobile App Integration

### Android (React Native)

```javascript
import { Location } from 'expo-location';

// Start location tracking
const subscription = await Location.watchPositionAsync(
  { accuracy: Location.Accuracy.High, distanceInterval: 100 },
  (location) => {
    // Send to GPS tracker service
    fetch('http://localhost:8098/api/bridge/gps-tracker/locations', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        mechanicId: 'MECH-001',
        mechanicName: 'Mike Murphy',
        latitude: location.coords.latitude,
        longitude: location.coords.longitude,
        speed: location.coords.speed,
        heading: location.coords.heading,
        altitude: location.coords.altitude,
        timestamp: new Date().toISOString(),
        status: 'AVAILABLE',
        vehicleRegistration: '12-D-12345'
      })
    });
  }
);
```

## WebSocket Support (Future Enhancement)

For real-time updates without polling:

```javascript
const ws = new WebSocket('ws://localhost:8098/ws/gps-tracker');

ws.onopen = () => {
  console.log('Connected to GPS tracker');
  // Subscribe to mechanic updates
  ws.send(JSON.stringify({
    action: 'subscribe',
    mechanicId: 'MECH-001'
  }));
};

ws.onmessage = (event) => {
  const location = JSON.parse(event.data);
  console.log('Location update:', location);
  // Update map
};
```

## Monitoring Metrics

Key metrics to monitor:
- `location_updates_total` - Total location updates received
- `location_updates_failed_total` - Failed updates
- `batch_updates_sent` - Batch updates to central monitoring
- `nearby_searches_total` - Nearby mechanic searches
- `mechanics_tracked` - Number of active mechanics
- `avg_location_update_latency` - Average time to process updates

## Building and Running

```bash
# Build
mvn clean package

# Run
java -jar target/mechanics-gps-tracker-service-1.0.0.jar

# Or with Spring Boot Maven plugin
mvn spring-boot:run
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SERVICES_MECHANICS_MOBILE-MECHANIC-SERVICE | Mobile mechanic service URL | http://localhost:8088 |
| SERVICES_MECHANICS_STAFF-MANAGEMENT-SERVICE | Staff management URL | http://localhost:8085 |
| SERVICES_CENTRAL-MONITORING_TRACKING-API | Central monitoring tracking URL | http://localhost:9020/api/tracking |
| GPS-TRACKING_UPDATE-INTERVAL | Location batch update interval (ms) | 30000 |
| GPS-TRACKING_BATCH-SIZE | Max locations per batch | 100 |
| GPS-TRACKING_GEOFENCE-RADIUS-METERS | Geofence radius | 500 |

## Privacy & Security

- Location data only transmitted over HTTPS
- Mechanic consent required for tracking
- Location history retained for 30 days max
- Off-duty mechanics not tracked
- Data anonymized for analytics

## Performance Optimization

### In-Memory Caching
- Location cache reduces database queries
- Fast retrieval for nearby searches
- Sub-second response times

### Batch Updates
- Reduces API calls to Central Monitoring
- More efficient network utilization
- Lower latency for tracking

### Reactive Streams
- Non-blocking I/O for high throughput
- Backpressure handling
- Efficient for many concurrent connections

## Use Cases

### 1. Optimal Mechanic Assignment
- Find nearest available mechanic
- Calculate ETA to customer
- Consider mechanic skills and availability

### 2. Real-time Fleet Visibility
- See all mechanics on map in real-time
- Monitor job progress
- Optimize resource allocation

### 3. Safety & Security
- Monitor mechanic safety
- Alert if mechanic stationary too long
- Track vehicle locations

### 4. Performance Analytics
- Track distance traveled per day
- Analyze route efficiency
- Optimize service areas

## Error Handling

| Error | Description | Action |
|-------|-------------|--------|
| MECHANIC_NOT_FOUND | Mechanic ID doesn't exist | Verify with Staff Management |
| INVALID_COORDINATES | Latitude/longitude out of range | Validate coordinates before sending |
| CENTRAL_MONITORING_UNAVAILABLE | Central Monitoring API down | Cache locations, retry later |
| LOCATION_STALE | Location data too old | Request fresh update from app |

## Future Enhancements

- [ ] WebSocket support for real-time push updates
- [ ] Geofence alerts (enter/exit zones)
- [ ] Route optimization and traffic avoidance
- [ ] ETA prediction with traffic data
- [ ] Historical location trails replay
- [ ] Speed and route analytics
- [ ] Offline location caching
- [ ] Battery level monitoring
- [ ] Auto-status update based on location (arrived at job, etc.)

## Related Services

- Mobile Mechanic Service (8088)
- Staff Management Service (8085)
- Central-Monitoring Domain (Agent 2)

---

*Last Updated: December 25, 2025*
