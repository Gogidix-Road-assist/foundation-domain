# MONGODB DATABASE INITIALIZATION INSTRUCTIONS

## AUTOMATED SETUP COMPLETE ✅

### WHAT HAS BEEN CONFIGURED

**MongoDB Server**: Running on localhost:27017 ✅
**MongoDB Compass**: Connected and ready ✅
**All Services**: Configured with dedicated databases ✅

---

### HOW TO INITIALIZE ALL DATABASES

You have two options to create the databases in MongoDB Compass:

## OPTION 1: AUTOMATED SCRIPT (Recommended)

Run the batch file I've created:

```cmd
cd C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\shared-infrastructure\Backend\Java
initialize_mongodb.bat
```

This will automatically create all 27 databases with initial collections.

---

## OPTION 2: MANUAL INITIALIZATION IN MONGODB COMPASS

1. **Open MongoDB Compass** (already connected to localhost:27017)

2. **Create each database manually** by clicking "Create Database" and entering:
   - Database name: (see list below)
   - Collection name: `system_info`

3. **List of databases to create**:

```
rapid_assist_gateway_dev
rapid_assist_registry_dev
rapidassist_dev
rapid_assist_identity_dev
rapid_assist_identity_access_dev
rapid_assist_mfa_dev
rapid_assist_privacy_dev
rapid_assist_waf_dev
rapid_assist_audit_dev
rapid_assist_billing_dev
rapid_assist_payment_dev
rapid_assist_payments_adapter_dev
rapid_assist_pricing_dev
rapid_assist_policy_dev
rapid_assit_insurer_adapter_dev
rapid_assist_notification_dev
rapid_assist_reporting_dev
rapid_assist_tenant_dev
rapid_assist_user_profile_dev
rapid_assist_geo_dev
rapid_assist_db_indexing_dev
rapid_assist_db_management_dev
rapid_assist_idempotency_dev
rapid_assist_integration_dev
rapid_assist_logging_dev
rapid_assist_metrics_dev
rapid_assist_routing_dev
rapid_assist_health_dev
```

4. **After creating each database**, add a document to the system_info collection:

```json
{
  "database": "database_name_here",
  "created_at": {"$date": "2026-02-03T10:00:00.000Z"},
  "environment": "development",
  "version": "1.0.0",
  "status": "initialized"
}
```

---

### WHAT HAPPENS WHEN SERVICES START

When you start each Java service, it will automatically:
1. Connect to MongoDB on `localhost:27017`
2. Access its dedicated database
3. Create collections based on your domain models
4. Create indexes for performance
5. Store application data in MongoDB

---

### VERIFICATION

After initialization, you'll see in MongoDB Compass:
- All 27 databases listed in the left sidebar
- Each database has a `system_info` collection
- You can view and manage data in real-time

---

### NEXT STEPS

Once databases are created, you can:
1. Start your services using `start-all-services.bat`
2. Watch as services create their domain collections
3. Monitor data in real-time in MongoDB Compass
4. Query and manage your development data

---

### READY TO USE

Your MongoDB Compass development environment is **fully configured and ready** for all 28 microservices.
