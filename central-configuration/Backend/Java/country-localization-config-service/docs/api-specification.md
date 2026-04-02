# Country Localization Config Service API Specification

## Base URL
```
/api/v1
```

## Authentication
All endpoints require JWT authentication with tenant context.

```
Authorization: Bearer <token>
X-Tenant-ID: <tenant-id>
```

---

## Country Localization Endpoints

### Create Country Localization

```http
POST /api/v1/country-localizations
Content-Type: application/json

{
  "countryCode": "IE",
  "countryName": "Ireland",
  "locale": {
    "languageCode": "en",
    "regionCode": "GB",
    "variant": "en-GB",
    "numberFormat": "#,##0.###",
    "dateFormat": "dd/MM/yyyy",
    "timeFormat": "HH:mm",
    "dateTimeFormat": "dd/MM/yyyy HH:mm"
  },
  "currency": {
    "currencyCode": "EUR",
    "symbol": "€",
    "symbolPosition": "BEFORE",
    "decimalPlaces": 2,
    "thousandsSeparator": ",",
    "decimalSeparator": "."
  },
  "dateTime": {
    "timezone": "Europe/Dublin",
    "dateFormat": "dd/MM/yyyy",
    "timeFormat": "HH:mm",
    "dateTimeFormat": "dd/MM/yyyy HH:mm",
    "use24HourFormat": true,
    "firstDayOfWeek": "Monday",
    "holidays": [],
    "observeDST": true
  },
  "measurementSystem": "METRIC",
  "active": true,
  "createdBy": "admin@tenant.com"
}
```

**Response:** `201 Created`

### Get Country Localization

```http
GET /api/v1/country-localizations/{countryCode}
```

**Response:** `200 OK`

```json
{
  "id": "507f1f77bcf86cd799439011",
  "countryCode": "IE",
  "countryName": "Ireland",
  "locale": {
    "languageCode": "en",
    "regionCode": "GB",
    "variant": "en-GB"
  },
  "currency": {
    "currencyCode": "EUR",
    "symbol": "€",
    "symbolPosition": "BEFORE",
    "decimalPlaces": 2
  },
  "measurementSystem": "METRIC",
  "active": true,
  "createdBy": "admin@tenant.com",
  "createdAt": "2024-01-20T10:00:00Z",
  "version": 1
}
```

### List Country Localizations

```http
GET /api/v1/country-localizations?activeOnly=true&page=0&size=50
```

**Response:** `200 OK`

```json
{
  "data": [...],
  "page": 0,
  "size": 50,
  "totalElements": 100,
  "totalPages": 2,
  "hasNext": true,
  "hasPrevious": false
}
```

### Update Country Localization

```http
PUT /api/v1/country-localizations/{countryCode}
Content-Type: application/json

{
  "countryName": "Ireland (Updated)",
  "locale": {...},
  "currency": {...},
  "updatedBy": "admin@tenant.com",
  "reason": "Update locale configuration"
}
```

**Response:** `200 OK`

### Delete Country Localization

```http
DELETE /api/v1/country-localizations/{countryCode}
```

**Response:** `204 No Content`

---

## Health Endpoints

### Health Check

```http
GET /actuator/health
```

**Response:** `200 OK`

```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "redis": {"status": "UP"},
    "kafka": {"status": "UP"}
  }
}
```

---

## Data Types

| Type | Description | Example |
|------|-------------|---------|
| METRIC | Metric measurement system | France, Germany |
| IMPERIAL | Imperial measurement system | USA, Liberia |
| MIXED | Mixed measurement systems | UK, Canada |

---

## Error Responses

All errors return a consistent format:

```json
{
  "timestamp": "2024-01-20T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid country code format",
  "path": "/api/v1/country-localizations",
  "errors": ["Country code must be ISO 3166-1 alpha-2 format"]
}
```

| Status | Error | Description |
|--------|-------|-------------|
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Missing or invalid token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Localization not found |
| 409 | Conflict | Localization already exists |
| 500 | Internal Server Error | Server error |
