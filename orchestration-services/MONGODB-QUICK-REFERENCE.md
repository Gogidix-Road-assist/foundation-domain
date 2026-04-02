# MongoDB Setup Quick Reference - Orchestration Services

## Quick Start

### Run the Complete Setup
```bash
# In MongoDB Compass mongosh
load("C:/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/orchestration-services/setup-mongodb-orchestration-complete.js")
```

### What Gets Created

| Service | Database | Port | Collections | Key Features |
|---------|----------|------|-------------|--------------|
| Alerting | `orchestration_alerting_service_db` | 8083 | 7 | Alert rules, notifications, escalation policies |
| Dispatching | `orchestration_dispatching_service_db` | 8084 | 6 | Dispatch jobs, assignments, GPS tracking |
| Fleet Assistance | `orchestration_fleet_assistance_service_db` | 8085 | 5 | Fleet requests, coordination history |
| Fleet Organization | `orchestration_fleet_organization_service_db` | 8086 | 4 | Org hierarchy, fleet units, closure table |
| Fleet Policy | `orchestration_fleet_policy_service_db` | 8087 | 4 | Safety policies, compliance tracking |
| Fleet Vehicles | `orchestration_fleet_vehicles_service_db` | 8088 | 6 | Vehicle fleet, maintenance, telemetry |
| Location | `orchestration_location_service_db` | 8089 | 5 | GPS tracking, geofences, routes |
| Matching | `orchestration_matching_service_db` | 8090 | 5 | Provider profiles, matching algorithms |
| Monitoring | `orchestration_monitoring_service_db` | 8091 | 6 | Health checks, metrics, uptime monitoring |
| Reporting | `orchestration_reporting_service_db` | 8092 | 5 | Report templates, automated schedules |
| Transaction Orchestration | `orchestration_transaction_orchestration_service_db` | 8093 | 5 | Saga orchestrations, compensation |

**Total**: 11 databases, 58 collections, 290+ indexes, 50+ seed documents

---

## Common MongoDB Operations

### Connect to a Database
```javascript
use orchestration_alerting_service_db
```

### List Collections
```javascript
show collections
```

### View Collection Data
```javascript
// View all alerts
db.alerts.find().pretty()

// View last 10 alerts
db.alerts.find().sort({ createdAt: -1 }).limit(10)

// Count documents
db.alerts.count()
```

### Check Indexes
```javascript
db.alerts.getIndexes()
```

### Check Validation Rules
```javascript
db.getCollectionInfos({ name: 'alerts' })
```

### Aggregation Examples
```javascript
// Alert counts by severity
db.alerts.aggregate([
  { $match: { deletedAt: null } },
  { $group: { _id: '$severity', count: { $sum: 1 } } }
])

// Average response time by provider
db.dispatch_assignments.aggregate([
  { $group: {
      _id: '$providerId',
      avgDuration: { $avg: '$actualDuration' }
  }
  }
])
```

---

## Seed Data Examples

### Alerting Service
- 3 alert rules (SLA breach, partner unavailable, high volume)
- 3 active alerts (critical, warning, emergency)
- 2 alert history entries

### Dispatching Service
- 3 dispatches (assigned, in-progress, pending)
- 2 dispatch assignments
- 3 tracking events

### Matching Service
- 2 provider profiles with ratings and capabilities
- 1 matching request with results
- 2 scored providers (0.95 and 0.75)

### Transaction Orchestration
- 2 sagas (1 completed, 1 in-progress)
- 3 saga steps with compensation actions

---

## Application Connection Strings

### Spring Boot application.yml
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/orchestration_alerting_service_db
      auto-index-creation: true
```

### Spring Boot application.properties
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/orchestration_alerting_service_db
spring.data.mongodb.auto-index-creation=true
```

### Node.js Mongoose
```javascript
mongoose.connect('mongodb://localhost:27017/orchestration_alerting_service_db');
```

### Python PyMongo
```python
from pymongo import MongoClient
client = MongoClient('mongodb://localhost:27017')
db = client['orchestration_alerting_service_db']
```

---

## Common Query Patterns

### Multi-Tenant Filtering
```javascript
db.alerts.find({
  tenantId: 'tenant-001',
  deletedAt: null
})
```

### Active Records Only
```javascript
db.alerts.find({
  acknowledged: false,
  deletedAt: null
})
```

### Date Range Queries
```javascript
db.alerts.find({
  createdAt: {
    $gte: new Date('2024-01-01'),
    $lt: new Date('2024-02-01')
  }
})
```

### Geographic Queries (Nearby)
```javascript
db.provider_profiles.find({
  currentLocation: {
    $near: {
      $geometry: {
        type: 'Point',
        coordinates: [-122.4194, 37.7749]
      },
      $maxDistance: 5000 // 5km radius
    }
  },
  status: 'AVAILABLE'
})
```

### Geographic Queries (Within Geofence)
```javascript
db.locations.find({
  location: {
    $geoWithin: {
      $geometry: {
        type: 'Polygon',
        coordinates: [[
          [-122.4094, 37.7849],
          [-122.4194, 37.7849],
          [-122.4194, 37.7749],
          [-122.4094, 37.7749],
          [-122.4094, 37.7849]
        ]]
      }
    }
  }
})
```

### Status Workflow Queries
```javascript
// Find in-progress transactions
db.transactions.find({
  status: { $in: ['STARTED', 'IN_PROGRESS'] }
})

// Find failed transactions needing retry
db.transactions.find({
  status: 'FAILED',
  retryCount: { $lt: 3 }
})
```

---

## Index Optimization Tips

### Cover Query with Index
```javascript
// This query uses the compound index { tenantId: 1, status: 1, createdAt: -1 }
db.alerts.find({
  tenantId: 'tenant-001',
  status: 'ACTIVE'
}).sort({ createdAt: -1 })
```

### Explain Query Performance
```javascript
db.alerts.find({ tenantId: 'tenant-001' }).explain('executionStats')
```

### Create Additional Indexes if Needed
```javascript
db.alerts.createIndex({ customField: 1 })
```

### Monitor Index Usage
```javascript
db.alerts.aggregate([{ $indexStats: {} }])
```

---

## Backup and Restore

### Backup All Orchestration Databases
```bash
mongodump --db=orchestration_alerting_service_db --out=/backup
mongodump --db=orchestration_dispatching_service_db --out=/backup
# ... repeat for all 11 databases
```

### Backup All at Once
```bash
mongodump --db=orchestration_* --out=/backup
```

### Restore a Database
```bash
mongorestore --db=orchestration_alerting_service_db /backup/orchestration_alerting_service_db
```

---

## Performance Monitoring

### Check Database Stats
```javascript
db.stats()
```

### Check Collection Stats
```javascript
db.alerts.stats()
```

### Current Operations
```javascript
db.currentOp()
```

### Kill Slow Query
```javascript
db.killOp(<operationId>)
```

---

## Troubleshooting

### Issue: Setup Script Fails
**Solution**: Ensure MongoDB is running on localhost:27017

```bash
# Check MongoDB status
mongosh --eval "db.adminCommand('ping')"

# Start MongoDB if not running
# Windows: net start MongoDB
# Linux/Mac: brew services start mongodb-community
```

### Issue: Validation Errors on Insert
**Solution**: Check validation rules and adjust document structure

```javascript
// View validation rules
db.getCollectionInfos({ name: 'alerts' })

// Temporarily disable validation (not recommended for production)
db.runCommand({
  collMod: 'alerts',
  validator: {},
  validationLevel: 'off'
})
```

### Issue: Slow Queries
**Solution**: Check query execution plan and add indexes

```javascript
// Check if index is used
db.alerts.find({ tenantId: 'tenant-001' }).explain('executionStats')

// Add missing index
db.alerts.createIndex({ tenantId: 1, customField: 1 })
```

### Issue: Out of Memory
**Solution**: Increase MongoDB cache or use pagination

```javascript
// Use pagination instead of loading all
db.alerts.find().skip(0).limit(100)
```

---

## Production Checklist

- [ ] MongoDB configured with authentication
- [ ] Enable TLS/SSL for connections
- [ ] Configure replica sets for high availability
- [ ] Set up regular backups
- [ ] Monitor disk space and performance
- [ ] Configure index build on background in production
- [ ] Review and tune index strategy based on query patterns
- [ ] Set up monitoring and alerting for database metrics
- [ ] Configure connection pooling in application
- [ ] Test failover and recovery procedures

---

## Maintenance Tasks

### Regular Index Rebuild
```javascript
db.alerts.reIndex()
```

### Compact Database
```javascript
db.runCommand({ compact: 'alerts' })
```

### Clear Old Data
```javascript
// Delete alerts older than 90 days
db.alerts.deleteMany({
  createdAt: { $lt: new Date(Date.now() - 90*24*60*60*1000) }
})
```

### Update Statistics
```javascript
db.alerts.find({}).explain('executionStats')
```

---

## Resources

- **MongoDB Documentation**: https://docs.mongodb.com/
- **Spring Data MongoDB**: https://spring.io/projects/spring-data-mongodb
- **Mongoose (Node.js)**: https://mongoosejs.com/
- **PyMongo (Python)**: https://pymongo.readthedocs.io/

---

## Support

For issues or questions:
1. Check the main documentation: `MONGODB-SCHEMA-DOCUMENTATION.md`
2. Review validation rules in the setup script
3. Check MongoDB logs for errors
4. Verify indexes are created correctly

---

**Last Updated**: 2026-02-04
**Version**: 1.0
