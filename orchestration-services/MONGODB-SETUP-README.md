# MongoDB Setup for Orchestration Services

## 📋 Overview

This package contains a complete MongoDB database setup for all 11 orchestration services in the Foundation Domain, following the **Database Per Service** pattern (2024-2025 best practices).

## 📦 Package Contents

```
orchestration-services/
├── setup-mongodb-orchestration-complete.js    # Master setup script (RUN THIS)
├── MONGODB-SCHEMA-DOCUMENTATION.md            # Complete schema reference
├── MONGODB-QUICK-REFERENCE.md                 # Quick start guide
└── MONGODB-SETUP-README.md                    # This file
```

## 🚀 Quick Start

### Prerequisites
- MongoDB 4.4+ running on localhost:27017
- MongoDB Compass or mongosh shell access
- Appropriate database permissions

### Installation

**Option 1: Using MongoDB Compass**
1. Open MongoDB Compass
2. Click on "mongosh" button (or press Ctrl+`)
3. Run:
   ```javascript
   load("C:/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/orchestration-services/setup-mongodb-orchestration-complete.js")
   ```

**Option 2: Using mongosh directly**
```bash
cd C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\orchestration-services
mongosh setup-mongodb-orchestration-complete.js
```

**Option 3: Copy-paste into mongosh**
1. Open the setup script file
2. Copy all content
3. Paste into mongosh
4. Press Enter

### What Gets Created

✅ **11 Databases** - One per service
✅ **58 Collections** - All properly validated
✅ **290+ Indexes** - Optimized for performance
✅ **50+ Seed Documents** - Sample data for testing
✅ **Validation Rules** - JSON Schema validation
✅ **Geospatial Indexes** - For location-based queries

## 📊 Services Configuration

| # | Service | Database | Port | Collections | Purpose |
|---|---------|----------|------|-------------|---------|
| 1 | Alerting | `orchestration_alerting_service_db` | 8083 | 7 | Alerts, rules, notifications |
| 2 | Dispatching | `orchestration_dispatching_service_db` | 8084 | 6 | Dispatch jobs, assignments |
| 3 | Fleet Assistance | `orchestration_fleet_assistance_service_db` | 8085 | 5 | Fleet coordination |
| 4 | Fleet Organization | `orchestration_fleet_organization_service_db` | 8086 | 4 | Organizational hierarchy |
| 5 | Fleet Policy | `orchestration_fleet_policy_service_db` | 8087 | 4 | Policies and compliance |
| 6 | Fleet Vehicles | `orchestration_fleet_vehicles_service_db` | 8088 | 6 | Vehicle fleet management |
| 7 | Location | `orchestration_location_service_db` | 8089 | 5 | GPS tracking, geofences |
| 8 | Matching | `orchestration_matching_service_db` | 8090 | 5 | Provider matching |
| 9 | Monitoring | `orchestration_monitoring_service_db` | 8091 | 6 | Health checks, metrics |
| 10 | Reporting | `orchestration_reporting_service_db` | 8092 | 5 | Reports and templates |
| 11 | Transaction Orchestration | `orchestration_transaction_orchestration_service_db` | 8093 | 5 | Saga orchestrations |

## 🔍 Verification

After running the setup script, verify the installation:

```javascript
// List all databases
show dbs

// Expected output:
// orchestration_alerting_service_db               ... 8192 KB
// orchestration_dispatching_service_db            ... 8192 KB
// orchestration_fleet_assistance_service_db       ... 8192 KB
// orchestration_fleet_organization_service_db     ... 8192 KB
// orchestration_fleet_policy_service_db           ... 8192 KB
// orchestration_fleet_vehicles_service_db         ... 16384 KB
// orchestration_location_service_db               ... 8192 KB
// orchestration_matching_service_db               ... 8192 KB
// orchestration_monitoring_service_db             ... 8192 KB
// orchestration_reporting_service_db              ... 8192 KB
// orchestration_transaction_orchestration_service_db ... 8192 KB
```

```javascript
// Check collections in a database
use orchestration_alerting_service_db
show collections

// Expected output:
// alerts
// alert_rules
// alert_subscriptions
// alert_history
// escalation_policies
// notification_channels
// alert_templates
```

```javascript
// Check seed data
db.alerts.find().pretty()

// Expected output: 3 alert documents with various severities
```

```javascript
// Verify indexes
db.alerts.getIndexes()

// Expected output: 11 indexes including unique, compound, and geospatial
```

## 📚 Documentation

### Main Documentation
- **MONGODB-SCHEMA-DOCUMENTATION.md**
  - Complete schema reference for all 11 services
  - Field types and constraints
  - Index specifications
  - Data validation rules
  - Common patterns and conventions

### Quick Reference
- **MONGODB-QUICK-REFERENCE.md**
  - Quick start guide
  - Common MongoDB operations
  - Query examples
  - Troubleshooting tips
  - Performance monitoring

### This File
- **MONGODB-SETUP-README.md**
  - Installation instructions
  - Configuration guide
  - Verification steps
  - Application integration

## 🔧 Application Integration

### Spring Boot Configuration

**application.yml**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/orchestration_alerting_service_db
      auto-index-creation: true
```

**application.properties**
```properties
# Alerting Service
spring.data.mongodb.uri=mongodb://localhost:27017/orchestration_alerting_service_db

# Dispatching Service
spring.data.mongodb.uri=mongodb://localhost:27017/orchestration_dispatching_service_db

# Fleet Vehicles Service
spring.data.mongodb.uri=mongodb://localhost:27017/orchestration_fleet_vehicles_service_db

# ... configure for each service
```

### MongoDB Connection String Format
```
mongodb://[username:password@]host:port/database
```

Examples:
```
mongodb://localhost:27017/orchestration_alerting_service_db
mongodb://admin:password@localhost:27017/orchestration_alerting_service_db
mongodb://user:pass@cluster0.example.com:27017,cluster1.example.com:27017/orchestration_alerting_service_db?replicaSet=myReplicaSet
```

## 🎯 Key Features

### Multi-Tenancy
All collections support multi-tenancy with `tenantId` field:
```javascript
{
  tenantId: 'tenant-001',
  ...other fields
}
```

### Soft Delete
Most entities support soft delete pattern:
```javascript
{
  deletedAt: null,  // null = active, date = deleted
  ...other fields
}
```

### Audit Trail
All entities include audit fields:
```javascript
{
  createdAt: ISODate('2024-01-01T00:00:00Z'),
  updatedAt: ISODate('2024-02-04T00:00:00Z'),
  ...other fields
}
```

### Geospatial Queries
Location-based collections support geospatial queries:
```javascript
{
  location: {
    latitude: 37.7749,
    longitude: -122.4194
  }
}
```

## 🔐 Security Considerations

### Production Deployment
1. **Enable Authentication**
   ```javascript
   // In mongod.conf
   security:
     authorization: enabled
   ```

2. **Enable TLS/SSL**
   ```
   mongodb://user:password@host:port/database?ssl=true
   ```

3. **Use Least Privilege Principle**
   - Create database-specific users
   - Grant only necessary permissions
   - Use role-based access control

4. **Network Security**
   - Bind to specific interfaces (not 0.0.0.0)
   - Use firewall rules
   - Configure IP whitelisting

## 📈 Performance Optimization

### Index Strategy
- **Single field indexes** on frequently queried fields
- **Compound indexes** for multi-field queries
- **Geospatial indexes** for location queries
- **Descending indexes** for time-series data

### Query Optimization
```javascript
// Good - Uses index
db.alerts.find({ tenantId: 'tenant-001', status: 'ACTIVE' })

// Bad - Collection scan
db.alerts.find({ $where: 'this.tenantId === "tenant-001"' })
```

### Monitoring
```javascript
// Check query performance
db.alerts.find({ tenantId: 'tenant-001' }).explain('executionStats')

// Monitor index usage
db.alerts.aggregate([{ $indexStats: {} }])
```

## 🛠️ Troubleshooting

### Common Issues

**Issue**: Script fails to run
```bash
# Check MongoDB is running
mongosh --eval "db.adminCommand('ping')"

# Start MongoDB if needed
# Windows: net start MongoDB
# Linux/Mac: systemctl start mongod
```

**Issue**: Permission denied
```javascript
// Ensure you have proper permissions
db.adminCommand({ connectionStatus: 1 })
```

**Issue**: Out of memory
```javascript
// Increase MongoDB cache
// In mongod.conf
storage:
  wiredTiger:
    engineConfig:
      cacheSizeGB: 4
```

**Issue**: Slow queries
```javascript
// Check query plan
db.alerts.find({}).explain('executionStats')

// Add missing index
db.alerts.createIndex({ field: 1 })
```

## 🔄 Maintenance

### Regular Tasks
- Monitor disk space usage
- Check index efficiency
- Review query performance
- Update statistics
- Compact collections if needed

### Backup Strategy
```bash
# Backup all orchestration databases
mongodump --db=orchestration_alerting_service_db --out=/backup/$(date +%Y%m%d)
mongodump --db=orchestration_dispatching_service_db --out=/backup/$(date +%Y%m%d)
# ... repeat for all databases
```

### Restore Strategy
```bash
# Restore from backup
mongorestore --db=orchestration_alerting_service_db /backup/20240204/orchestration_alerting_service_db
```

## 📞 Support

### Documentation Files
1. **MONGODB-SCHEMA-DOCUMENTATION.md** - Complete schema reference
2. **MONGODB-QUICK-REFERENCE.md** - Quick start and examples
3. **MONGODB-SETUP-README.md** - This file

### Getting Help
1. Check the documentation files above
2. Review the setup script comments
3. Check MongoDB logs
4. Verify MongoDB is running
5. Test with simple queries first

### Useful Resources
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- [MongoDB Best Practices](https://www.mongodb.com/developer/best-practices/)

## ✅ Setup Checklist

- [ ] MongoDB installed and running
- [ ] Run setup script
- [ ] Verify 11 databases created
- [ ] Verify collections exist in each database
- [ ] Check indexes are created
- [ ] Verify seed data is present
- [ ] Update application configuration
- [ ] Test application connectivity
- [ ] Run integration tests
- [ ] Set up monitoring and alerts
- [ ] Configure backups
- [ ] Document any custom configurations

## 🎉 Success Indicators

If you see the following, setup was successful:

```
╔══════════════════════════════════════════════════════════════════════════════╗
║              ORCHESTRATION SERVICES SETUP COMPLETE                         ║
╚══════════════════════════════════════════════════════════════════════════════╝

SUMMARY:
  Total Databases Created:     11
  Total Collections Created:    58
  Total Indexes Created:        290+
  Total Seed Documents:         50+
  Setup Duration:               X.XX seconds
  Connection:                   mongodb://localhost:27017
```

## 📝 Notes

- **Database Per Service Pattern**: Each service has its own database for clear ownership
- **Multi-Tenant Architecture**: All collections support tenant isolation
- **Validation Rules**: JSON Schema validation ensures data integrity
- **Production Ready**: Indexes and validation rules optimized for production
- **Seed Data**: Sample data included for development and testing

## 🚀 Next Steps

1. **Update Application Configuration**: Add connection strings to your services
2. **Run Integration Tests**: Verify applications can connect and query
3. **Customize Seed Data**: Add your own test data if needed
4. **Set Up Monitoring**: Configure database performance monitoring
5. **Configure Backups**: Set up automated backup strategy
6. **Review Security**: Enable authentication and TLS for production
7. **Tune Performance**: Monitor and adjust indexes based on query patterns

---

**Version**: 1.0
**Last Updated**: 2026-02-04
**Status**: ✅ Production Ready

---

**For detailed schema information, see: [MONGODB-SCHEMA-DOCUMENTATION.md](./MONGODB-SCHEMA-DOCUMENTATION.md)**

**For quick examples and common operations, see: [MONGODB-QUICK-REFERENCE.md](./MONGODB-QUICK-REFERENCE.md)**
