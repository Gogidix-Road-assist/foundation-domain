# Mechanics-Vendors Bridge Service

Cross-domain integration service connecting Mechanics and Vendors-Ecommerce domains for automated parts ordering and inventory management.

## Overview

This service acts as a bridge between the Mechanics domain (workshop inventory) and Vendors-Ecommerce domain (parts suppliers), enabling:
- **Vendor Catalog Integration**: Search and compare parts across multiple vendors
- **Automated Reordering**: Auto-order low stock parts based on configurable thresholds
- **Consolidated Orders**: Combine multiple parts into single orders for efficiency
- **Order Tracking**: Monitor order status from vendors
- **Price Comparison**: Find best prices and delivery times across vendors

## Port: 8097

## Architecture

```
┌─────────────────────┐
│ Mechanics Domain    │
│                     │
│ Workshop Inventory  │
│ (8087)              │
│      ↓              │
│   Low Stock Alert   │
└──────────┬──────────┘
           │
           ↓
┌──────────────────────────────────┐
│ Mechanics-Vendors Bridge Service │
│ (8097)                            │
│                                    │
│ • Vendor catalog aggregation     │
│ • Price comparison                │
│ • Auto-reorder scheduling        │
│ • Consolidated order creation     │
│ • Order status tracking           │
└──────────┬───────────────────────┘
           │
           ↓
┌─────────────────────┐
│ Vendors Domain      │
│ (Agent 2)           │
│                     │
│ Parts Catalog API   │
│ Orders API          │
└─────────────────────┘
```

## Key Features

### 1. Vendor Catalog Aggregation

Fetches parts catalog from multiple vendors:
```bash
GET /api/bridge/mechanics-vendors/catalog
```

**Response:**
```json
{
  "vendors": [
    {
      "vendorId": "VENDOR-001",
      "vendorName": "AutoParts Ireland",
      "rating": 4.8,
      "averageDeliveryTimeDays": 2,
      "parts": [
        {
          "partNumber": "ALT-150A",
          "partName": "Alternator 150A",
          "price": 250.00,
          "availableQuantity": 50,
          "inStock": true
        }
      ]
    }
  ]
}
```

### 2. Cross-Vendor Price Comparison

Search for specific part across all vendors:
```bash
GET /api/bridge/mechanics-vendors/catalog/search?partNumber=ALT-150A
```

**Response:**
```json
[
  {
    "partNumber": "ALT-150A",
    "partName": "Alternator 150A",
    "vendorPrices": [
      {
        "vendorId": "VENDOR-001",
        "vendorName": "AutoParts Ireland",
        "price": 250.00,
        "deliveryDays": 2,
        "availableQuantity": 50
      },
      {
        "vendorId": "VENDOR-002",
        "vendorName": "EuroCar Parts",
        "price": 245.00,
        "deliveryDays": 3,
        "availableQuantity": 30
      }
    ],
    "lowestPrice": {
      "vendorId": "VENDOR-002",
      "price": 245.00
    },
    "fastestDelivery": {
      "vendorId": "VENDOR-001",
      "deliveryDays": 2
    }
  }
]
```

### 3. Automated Reordering

**Scheduled Task:** Runs every 5 minutes (configurable)

1. Checks workshop inventory for low stock parts
2. Groups alerts by workshop
3. Finds best vendor for required parts
4. Creates consolidated order
5. Submits order to vendor
6. Notifies mechanics ordering service

**Trigger manually:**
```bash
POST /api/bridge/mechanics-vendors/auto-reorder/trigger
```

**Configuration:**
```yaml
auto-reorder:
  enabled: true
  check-interval: 300000 # 5 minutes
  low-stock-threshold: 10
  default-reorder-quantity: 20
  max-order-amount: 5000.00
```

### 4. Consolidated Order Creation

Create single order for multiple parts from preferred vendor:
```bash
POST /api/bridge/mechanics-vendors/orders/consolidated
```

**Request:**
```json
{
  "workshopId": "WS-001",
  "preferredVendorId": "VENDOR-001",
  "alerts": [
    {
      "partNumber": "BF-500",
      "partName": "Brake Fluid",
      "currentStock": 2,
      "minStock": 10
    },
    {
      "partNumber": "OF-200",
      "partName": "Oil Filter",
      "currentStock": 5,
      "minStock": 8
    }
  ]
}
```

**Response:**
```json
{
  "orderId": "AUTO-ORD-1735147200000",
  "workshopId": "WS-001",
  "vendorId": "VENDOR-001",
  "requestedAt": "2025-12-25T16:00:00",
  "items": [
    {
      "partNumber": "BF-500",
      "partName": "Brake Fluid",
      "quantity": 28,
      "unitPrice": 12.50,
      "totalPrice": 350.00
    },
    {
      "partNumber": "OF-200",
      "partName": "Oil Filter",
      "quantity": 23,
      "unitPrice": 15.99,
      "totalPrice": 367.77
    }
  ],
  "totalAmount": 717.77,
  "status": "PENDING",
  "priority": "MEDIUM"
}
```

## Auto-Reorder Logic

### Trigger Conditions

- Current stock ≤ low-stock-threshold (default: 10)
- Part marked as HIGH severity in alerts

### Reorder Quantity Calculation

```
reorderQuantity = (minStock + defaultReorderQuantity) - currentStock
```

Example:
- Current stock: 2
- Min stock: 10
- Default reorder: 20
- Reorder quantity: (10 + 20) - 2 = 28

### Vendor Selection Algorithm

1. **Priority 1:** Parts available in stock
2. **Priority 2:** Lowest price
3. **Priority 3:** Fastest delivery time
4. **Priority 4:** Vendor rating

## Order Status Flow

```
PENDING → VENDOR_CONFIRMED → PROCESSING → SHIPPED → DELIVERED
                                      ↓
                                  CANCELLED/FAILED
```

## API Endpoints

### Catalog & Search

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/bridge/mechanics-vendors/catalog | Get all vendors with parts catalog |
| GET | /api/bridge/mechanics-vendors/catalog/search?partNumber={pn} | Search part across vendors |
| GET | /api/bridge/mechanics-vendors/catalog/{vendorId} | Get specific vendor details |

### Order Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/bridge/mechanics-vendors/orders | Submit order to vendor |
| POST | /api/bridge/mechanics-vendors/orders/consolidated | Create consolidated order |
| GET | /api/bridge/mechanics-vendors/orders/{id}/status?vendorId={vid} | Get order status |

### Auto-Reorder

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/bridge/mechanics-vendors/auto-reorder/trigger | Manually trigger auto-reorder |
| GET | /api/bridge/mechanics-vendors/auto-reorder/status | Get auto-reorder status |

## Circuit Breaker Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      vendorsService:
        failure-rate-threshold: 50%
        wait-duration-in-open-state: 15s
        sliding-window-size: 10
```

**Fallback Behavior:**
- Catalog search returns empty list
- Order submission returns order with FAILED status
- Status check returns FAILED status

## Error Handling

| Error | Description | Action |
|-------|-------------|--------|
| VENDOR_NOT_FOUND | Vendor ID doesn't exist | Verify vendor with Vendors domain |
| PART_NOT_AVAILABLE | Part out of stock | Try alternative vendor |
| ORDER_AMOUNT_EXCEEDED | Total order exceeds max limit | Split into multiple orders |
| VENDOR_UNAVAILABLE | Vendor API down | Circuit breaker opens, use fallback |

## Integration Points

### Mechanics Domain

**Workshop Inventory Service (8087):**
- Provides low stock alerts
- Receives stock updates after delivery

**Parts Ordering Service (8086):**
- Records purchase orders
- Tracks order history

### Vendors Domain (Agent 2)

**Catalog API:**
- Returns available parts and pricing
- Provides vendor details

**Orders API:**
- Accepts purchase orders
- Returns order status and tracking

## Monitoring Metrics

Key metrics to monitor:
- `auto_reorder_total` - Total auto-reorders triggered
- `auto_reorder_failed_total` - Failed auto-reorders
- `orders_submitted_total` - Orders submitted to vendors
- `vendor_api_latency` - Vendor API response time
- `parts_search_total` - Part searches performed
- `consolidated_orders_total` - Consolidated orders created

## Building and Running

```bash
# Build
mvn clean package

# Run
java -jar target/mechanics-vendors-bridge-service-1.0.0.jar

# Or with Spring Boot Maven plugin
mvn spring-boot:run
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SERVICES_MECHANICS_INVENTORY-SERVICE | Inventory service URL | http://localhost:8087 |
| SERVICES_MECHANICS_ORDERING-SERVICE | Ordering service URL | http://localhost:8086 |
| SERVICES_VENDORS_BASE-URL | Vendors domain URL | http://localhost:9010 |
| AUTO-REORDER_ENABLED | Enable/disable auto-reorder | true |
| AUTO-REORDER_LOW-STOCK-THRESHOLD | Stock level to trigger reorder | 10 |
| AUTO-REORDER_DEFAULT-REORDER-QUANTITY | Default quantity to order | 20 |

## Future Enhancements

- [ ] Machine learning for demand prediction
- [ ] Multi-vendor order splitting
- [ ] Bulk order discounts
- [ ] Vendor performance analytics
- [ ] Automated negotiation for best prices
- [ ] Parts compatibility validation
- [ ] Returns and refunds handling
- [ ] Backorder management

## Related Services

- Workshop Inventory Service (8087)
- Parts Ordering Service (8086)
- Vendors-Ecommerce Domain (Agent 2)

---

*Last Updated: December 25, 2025*
